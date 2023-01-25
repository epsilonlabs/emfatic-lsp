/*******************************************************************************
 * Copyright (c) 2022 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.scoping;

import static com.google.common.collect.Iterables.concat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Import;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.ClasspathUriResolutionException;
import org.eclipse.xtext.resource.IClasspathUriResolver;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.DefaultGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.SimpleScope;
import org.eclipse.xtext.util.IAcceptor;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * This implementation adds all ECore elements and all
 * elements from imported packages by URI to the GloablScope.
 * 
 * When importing non Emfatic resources, its elements are translated into 
 *  
 * @author Horacio Hoyos Rodriguez
 * @see EmfaticScopeProvider
 *
 */
public class EmfaticGSP extends DefaultGlobalScopeProvider {
	
	public static final String ECORE_RESOURCE_URI = "lib://www.eclipse.org/emf/2002/Ecore";

	@Override
	protected IScope getScope(
		final Resource context, 
		boolean ignoreCase, 
		EClass type, 
		Predicate<IEObjectDescription> filter) {
		Iterable<IEObjectDescription> descriptions = ecoreDescriptors(context);
		// Look for all URI import statements and load the resources, if they exist
		if (context.getContents().size() > 0) {
			List<URI> collectedURIs = getImportedUris(context);
			Resource libaryResource;
			for (URI uri : collectedURIs) {
				libaryResource = null;
				LOG.info("Looking for metamodel with uri " + uri);
				Resource metamodelResource = EcoreUtil2.getResource(context, uri.toString());
				if (metamodelResource != null) {
					LOG.debug("Found Metamodel with uri: " + uri);
					if (this.serviceProvider.canHandle(uri)) {
						libaryResource = metamodelResource;
					} else {
						libaryResource = translateEcore(metamodelResource, context.getResourceSet());
					}
					if (libaryResource != null) {
						descriptions = concat(
								descriptions,
								descriptionManager.getResourceDescription(libaryResource).getExportedObjects());
					}
				}
			}
		}
		return new SimpleScope(super.getScope(context, ignoreCase, type, filter), descriptions, false);
	}
	
	private static final Logger LOG = Logger.getLogger(EmfaticGSP.class);
	private static final String ECORE_FILE_EXTENSION = "ecore";
	
	@Inject
	private IResourceDescription.Manager descriptionManager;
	@Inject
	private IResourceServiceProvider serviceProvider;
	
	
	/**
	 * The default acceptor for import URIs.
	 * 
	 * It normalizes potentially given class-path URIs.
	 * 
	 * @see ImportUriGlobalScopeProvider#createURICollector(Resource, Set)
	 */
	private class URICollector implements IAcceptor<String> {
		
		private final IClasspathUriResolver uriResolver;
		private final Object uriContext;
		private final Set<URI> result;

		public URICollector(ResourceSet resourceSet) {
			this.result = new HashSet<>();
			if (resourceSet instanceof XtextResourceSet) {
				uriResolver = ((XtextResourceSet) resourceSet).getClasspathUriResolver();
				uriContext = ((XtextResourceSet) resourceSet).getClasspathURIContext();
			} else {
				uriResolver = null;
				uriContext = null;
			}
		}
		
		@Override
		public void accept(String uriAsString) {
			if (uriAsString == null) {
				return;
			}
			try {
				URI importUri = resolve(uriAsString);
				if (importUri != null) {
					result.add(importUri);
				}
			} catch(IllegalArgumentException e) {
				// ignore, invalid uri given
			}
		}
		
		public List<URI> collectedURIs() {
			return new ArrayList<>(this.result);
		}

		private URI resolve(String uriAsString) throws IllegalArgumentException {
			URI uri = URI.createURI(uriAsString);
			if (uriResolver != null) {
				try {
					return uriResolver.resolve(uriContext, uri);
				} catch(ClasspathUriResolutionException e) {
					return uri;
				}
			}
			return uri;
		}
	}
	
	/**
	 * Translate ECore metamodel to Emfatic AST.
	 * @param resource	the metamodel
	 * @param resourceSet		the context ResourceSet
	 * @return a Resource with the equivalent Emfatic elements
	 * TODO Support generics?
	 */
	private Resource translateEcore(Resource resource, ResourceSet resourceSet) {
		LOG.debug("Translating Metamodel with uri: " + resource.getURI() + " to Emfatic");
		Resource result = null;
		if (resource.getContents().isEmpty()) {
			return result;
		}
		if (resource.getContents().size() > 1) {
			LOG.warn("Imported metamodel has multiple root packages, only the first one will be translated");
		}
		EObject root = resource.getContents().get(0);
		if (root instanceof EPackage) {
			EPackage ep = (EPackage) root;
			URI libraryResourceURI = toLibURI(resource.getURI());
			result = resourceSet.getResource(libraryResourceURI, false);
			if (result == null) {
				result = ecoreToEmf(resourceSet, libraryResourceURI, ep);
			}
		}
		return result;
		
	}
	
	/**
	 * Create <code>IEObjectDescriptions</code> for ECore package contents.
	 *  
	 * We use our ECORE_RESOURCE_URI, so when generating the emfatic ecore, we know what comes from the 
	 * Ecore metamodel.
	 */
	private Iterable<IEObjectDescription> ecoreDescriptors(final Resource resoruce) {
		URI libraryResourceURI = URI.createURI(ECORE_RESOURCE_URI);
		Resource ecoreResource = resoruce.getResourceSet().getResource(libraryResourceURI, false);
		if (ecoreResource == null) {
			ecoreResource = ecoreToEmf(
					resoruce.getResourceSet(),
					libraryResourceURI,
					EcorePackage.eINSTANCE);
		}
		return descriptionManager.getResourceDescription(ecoreResource).getExportedObjects();
	}
	
	/**
	 * We only add a simple wrappers for the EClasses and EDataTypes that have matching names.
	 *
	 * @param resourceSet the resource set
	 * @param libaryResourceURI the libary resource URI
	 * @param ep the EPackate to load
	 * @return the resource
	 */
	private Resource ecoreToEmf(ResourceSet resourceSet, URI libaryResourceURI, EPackage ep) {
		Resource libaryResource = new ResourceImpl(libaryResourceURI);
		resourceSet.getResources().add(libaryResource);
		CompUnit compUint = emfaticInstance(EmfaticPackage.Literals.COMP_UNIT);
		libaryResource.getContents().add(compUint);
		compUint.setPackage(translatePackage(ep.getName()));
		compUint.getTopLevelDecls().addAll(transalteClassifiers(ep.getEClassifiers()));
		return libaryResource;
	}
	
	/**
	 * Wrap package.
	 * @param name 
	 *
	 * @return the package decl
	 */
	private PackageDecl translatePackage(String name) {
		PackageDecl pd = emfaticInstance(EmfaticPackage.Literals.PACKAGE_DECL);
		pd.setName(name);
		return pd;
	}

	/**
	 * Wrap classifiers.
	 *
	 * @param eClassifiers the e classifiers
	 * @return the collection<? extends top level decl>
	 * TODO generics
	 */
	private Collection<? extends TopLevelDecl> transalteClassifiers(EList<EClassifier> eClassifiers) {
		List<TopLevelDecl> result = eClassifiers.stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.map(this::translateClass)
				.collect(Collectors.toList());
		result.addAll( eClassifiers.stream()
				.filter(EDataType.class::isInstance)
				.map(EDataType.class::cast)
				.flatMap(dt -> this.translateDataType(dt).stream())
				.collect(Collectors.toList()));
		result.addAll( eClassifiers.stream()
				.filter(EEnum.class::isInstance)
				.map(EEnum.class::cast)
				.map(this::translateEnum)
				.collect(Collectors.toList()));
		return result;
	}
	
	/**
	 * Wrap class.
	 *
	 * @param clazz the clazz
	 * @return the top level decl
	 * TODO generics
	 */
	private TopLevelDecl translateClass(EClass clazz) {
		ClassDecl cd = emfaticInstance(EmfaticPackage.Literals.CLASS_DECL);
		cd.setName(clazz.getName());
		TopLevelDecl tld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		tld.setDeclaration(cd);
		return tld;
	}
	
	/**
	 * Emfatic not only supports the EMF DataTypes, but also, the equivalent
	 * Java types (both primitive and wrappers).
	 * We use the EMF URI of the EDataType as the InstanceClass name, so we can
	 * use this information when creating the 
	 * 
	 * TODO The generated list of types is larger than Emfatic. Is this good/bad? 
	 * 		e.g., we get InvocationTargetException, not sure is a type we want to use,
	 * 		but... is a valid DataType non the less.
	 *
	 * @param type the type
	 * @return the list
	 * TODO generics
	 */
	private List<TopLevelDecl> translateDataType(EDataType type) {
		// All DataTypeDecl shate the same instance class name
		List<TopLevelDecl> result = new ArrayList<>();
		DataTypeDecl dtd = emfaticInstance(EmfaticPackage.Literals.DATA_TYPE_DECL);
		dtd.setName(type.getName());
		dtd.setInstanceClassName(createInstanceClassName(type));
		TopLevelDecl tld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		tld.setDeclaration(dtd);
		result.add(tld);
		Class<?> javaType = null; 
		try {
			javaType = Class.forName(type.getInstanceClassName());
		 } catch (ClassNotFoundException e) {
			 DataTypeDecl javaDtd = emfaticInstance(EmfaticPackage.Literals.DATA_TYPE_DECL);
			 javaDtd.setName(type.getInstanceClassName());
			 javaDtd.setInstanceClassName(createInstanceClassName(type));
			 TopLevelDecl javaTld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
			 javaTld.setDeclaration(javaDtd);
			 result.add(javaTld);
			 return result;	 
		}
		DataTypeDecl javaDtd = emfaticInstance(EmfaticPackage.Literals.DATA_TYPE_DECL);
		javaDtd.setName(javaType.getSimpleName());
		javaDtd.setInstanceClassName(createInstanceClassName(type));
		TopLevelDecl javaTld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		javaTld.setDeclaration(javaDtd);
		result.add(javaTld);
		return result;
	}
	
	/**
	 * Wrap enum.
	 *
	 * @param type the type
	 * @return the top level decl
	 */
	private TopLevelDecl translateEnum(EEnum type) {
		EnumDecl ed = emfaticInstance(EmfaticPackage.Literals.ENUM_DECL);
		ed.setName(type.getName());
		TopLevelDecl tld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		tld.setDeclaration(ed);
		return tld;
	}

	
	/**
	 * Emfatic instance.
	 *
	 * @param <T> the generic type
	 * @param eClass the e class
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	private <T extends EObject> T emfaticInstance(EClass eClass) {
		return (T) EmfaticPackage.eINSTANCE.getEmfaticFactory().create(eClass);
	}
	
	/**
	 * Creates the instance class name.
	 *
	 * @param element the element
	 * @return the string or qualified ID
	 */
	private StringOrQualifiedID createInstanceClassName(ENamedElement element) {
		StringOrQualifiedID instanceClassName = emfaticInstance(EmfaticPackage.Literals.STRING_OR_QUALIFIED_ID);
		instanceClassName.setLiteral("http://www.eclipse.org/emf/2002/Ecore#//" + element.getName());
		return instanceClassName;
	}
	
	// Replace the URI scheme by "lib" to differentiate them from loaded resoruces.
	private URI toLibURI(URI uri) {
		return URI.createHierarchicalURI(
				"lib",
				uri.authority(),
				uri.device(),
				uri.segments(),
				uri.query(),
				uri.fragment());
	}
	
	/**
	 * Get all the imported URIs and filter invalid and non "ecore" and "lspemf" URIs.
	 * @param context
	 * @return
	 */
	private List<URI> getImportedUris(final Resource context) {
		CompUnit compUnit = (CompUnit) context.getContents().get(0);
		URICollector collector = new URICollector(context.getResourceSet());
		for (Import is : compUnit.getImportStmts()) {
			if (is.getUri() != null) {
				String uri = is.getUri().getLiteral();
				// Don't import if null or is the ECore URI (Ecore is automatically imported).
				if (uri != null && !uri.equals(EcorePackage.eINSTANCE.getNsURI())) {
					collector.accept(uri);
				}
			}
		}
		List<URI> collectedURIs = collector.collectedURIs();
		Iterator<URI> uriIter = collectedURIs.iterator();
		// TODO Extrac this so the validation can reuse
		while(uriIter.hasNext()) {
			URI next = uriIter.next();
			if (!EcoreUtil2.isValidUri(context, next)) {
				uriIter.remove();
			}
			if (next.isPlatform() || next.isFile()) {
				String ext = next.fileExtension();
				if (ext != ECORE_FILE_EXTENSION || !serviceProvider.canHandle(next)) {
					uriIter.remove();
				}
			}
		}
		return collectedURIs;
	}
	
}
