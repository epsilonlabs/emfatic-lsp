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
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.emfatic.xtext.common.ImportUriChecker;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.Import;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.DefaultGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.SimpleScope;

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
			ImportUriChecker checker = new ImportUriChecker(context, this.fileExtensionProvider);
			List<URI> importedURIs = getImportedUris(context, checker);
			Resource libaryResource;
			for (URI uri : importedURIs) {
				libaryResource = null;
				LOG.info("Looking for metamodel with uri " + uri);
				Resource metamodelResource = EcoreUtil2.getResource(context, uri.toString());
				if (metamodelResource != null) {
					LOG.debug("Found Metamodel with uri: " + uri);
					if (checker.canHandle(uri)) {
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
	
	@Inject
	private IResourceDescription.Manager descriptionManager;
	
	@Inject
	private FileExtensionProvider fileExtensionProvider;
	
	@Inject
	private EmfaticImport emfaticImport;
	
	/**
	 * Translate ECore metamodel to Emfatic AST.
	 * @param resource	the metamodel
	 * @param resourceSet		the context ResourceSet
	 * @return a Resource with the equivalent Emfatic elements
	 * TODO Support generics?
	 */
	private Resource translateEcore(
		Resource resource,
		ResourceSet resourceSet) {
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
	private Iterable<IEObjectDescription> ecoreDescriptors(
		final Resource resoruce) {
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
	private Resource ecoreToEmf(
		ResourceSet resourceSet,
		URI libaryResourceURI,
		EPackage ep) {
		Resource libaryResource = new ResourceImpl(libaryResourceURI);
		resourceSet.getResources().add(libaryResource);
		CompUnit compUint = EmfaticFactory.eINSTANCE.createCompUnit();
		libaryResource.getContents().add(compUint);
		compUint.setPackage(this.emfaticImport.translate(ep));
		compUint.getDeclarations().addAll(this.emfaticImport.translate(ep.getEClassifiers()));
		return libaryResource;
	}
	
	

	// Replace the URI scheme by "lib" to differentiate them from loaded resources.
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
	 * @param checker TODO
	 * @return
	 */
	private List<URI> getImportedUris(
		final Resource context,
		ImportUriChecker checker) {
		CompUnit compUnit = (CompUnit) context.getContents().get(0);
		List<URI> result = new ArrayList<>();
		for (Import stmt : compUnit.getImports()) {
			Optional<URI> optUri;
			try {
				optUri = checker.resolveURI(stmt);
			} catch (IllegalArgumentException e) {
				LOG.error("Invalid imported uri " + stmt.getUri().getLiteral());
				continue;
			}
			optUri.ifPresent(uri -> {
				if(checker.isValidResoruce(uri) || checker.resourceExists(uri)) {
					result.add(uri);
				}
			});
			
		}
		return result;
	}
	
}
