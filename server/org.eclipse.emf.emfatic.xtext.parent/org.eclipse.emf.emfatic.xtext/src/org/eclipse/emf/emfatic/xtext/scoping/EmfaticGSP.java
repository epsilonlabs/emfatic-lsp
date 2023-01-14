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
import java.util.List;
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
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.DefaultGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * This implementation adds all ECore elements to the GloablScope and all
 * elements from imported packages by URI.
 * 
 * The Element's name and type is used to create an equivalent Emfatic EClasse
 * that can be used for linking and content assist. This is necessary, as Emfatic's
 * AST does not reference ECore types directly.
 *  
 * @author Horacio Hoyos Rodriguez
 * @see EmfaticScopeProvider
 *
 */
public class EmfaticGSP extends DefaultGlobalScopeProvider {
	
	
	/** The Constant ECORE_RESOURCE_URI. */
	public static final String ECORE_RESOURCE_URI = "lib://www.eclipse.org/emf/2002/Ecore";

	@Override
	protected IScope getScope(
		final Resource context, 
		boolean ignoreCase, 
		EClass type, 
		Predicate<IEObjectDescription> filter) {
		// We use the ECore URI, so when generating the emfatic ecore, we know what
		// comes from the Ecore metamodel
		URI libraryResourceURI = URI.createURI(ECORE_RESOURCE_URI);
		Resource libaryResource = context.getResourceSet().getResource(libraryResourceURI, false);
		if (libaryResource == null) {
			libaryResource = ecoreToEmf(context.getResourceSet(), libraryResourceURI, EcorePackage.eINSTANCE);
		}
		IResourceDescription resourceDescription = descriptionManager.getResourceDescription(libaryResource);
		Iterable<IEObjectDescription> library = resourceDescription.getExportedObjects();
		// Look for all URI import statements and load the resources, if they exist
		if (context.getContents().size() > 0) {
			CompUnit compUnit = (CompUnit) context.getContents().get(0);
			for (Import is : compUnit.getImportStmts()) {
				if (is.getImportedNamespace() != null) {
					String uri = is.getImportedNamespace().getLiteral();
					// Don't import if null or is the ECore URI (Ecore is automatically imported).
					if (uri != null && !uri.equals(EcorePackage.eINSTANCE.getNsURI())) {
						LOG.warn("Loading metamodel with uri " + uri);
						libraryResourceURI = toLibURI(URI.createURI(uri));
						libaryResource = context.getResourceSet().getResource(libraryResourceURI, false);
						if (libaryResource == null) {
							EPackage ep = EPackage.Registry.INSTANCE.getEPackage(uri);
							if (ep == null) {
								LOG.warn("The package for URI " + uri + " was not found. Check that it has been registered.");
								continue;
							}
							libaryResource = ecoreToEmf(context.getResourceSet(), libraryResourceURI, ep);
						}
						resourceDescription = descriptionManager.getResourceDescription(libaryResource);
						library = concat(library, resourceDescription.getExportedObjects());
					}
				}
			}
		}
		return new SimpleScope(super.getScope(context, ignoreCase, type, filter), library, false);
	}
	
	private static final Logger LOG = Logger.getLogger(EmfaticGSP.class);
	
	/** The description manager. */
	@Inject
	private IResourceDescription.Manager descriptionManager;
	
	/**
	 * We only add a simple wrappers for the EClasses and EDataTypes that have 
	 * matching names.
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
		compUint.setPackage(wrapPackage(ep.getName()));
		compUint.getTopLevelDecls().addAll(wrapClassifiers(ep.getEClassifiers()));
		return libaryResource;
	}
	
	/**
	 * Wrap package.
	 * @param name 
	 *
	 * @return the package decl
	 */
	private PackageDecl wrapPackage(String name) {
		PackageDecl pd = emfaticInstance(EmfaticPackage.Literals.PACKAGE_DECL);
		pd.setName(name);
		return pd;
	}

	/**
	 * Wrap classifiers.
	 *
	 * @param eClassifiers the e classifiers
	 * @return the collection<? extends top level decl>
	 */
	private Collection<? extends TopLevelDecl> wrapClassifiers(EList<EClassifier> eClassifiers) {
		List<TopLevelDecl> result = eClassifiers.stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.map(this::wrapClass)
				.collect(Collectors.toList());
		result.addAll( eClassifiers.stream()
				.filter(EDataType.class::isInstance)
				.map(EDataType.class::cast)
				.flatMap(dt -> this.wrapDataType(dt).stream())
				.collect(Collectors.toList()));
		result.addAll( eClassifiers.stream()
				.filter(EEnum.class::isInstance)
				.map(EEnum.class::cast)
				.map(this::wrapEnum)
				.collect(Collectors.toList()));
		return result;
	}
	
	/**
	 * Wrap class.
	 *
	 * @param clazz the clazz
	 * @return the top level decl
	 */
	private TopLevelDecl wrapClass(EClass clazz) {
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
	 */
	private List<TopLevelDecl> wrapDataType(EDataType type) {
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
	private TopLevelDecl wrapEnum(EEnum type) {
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

}
