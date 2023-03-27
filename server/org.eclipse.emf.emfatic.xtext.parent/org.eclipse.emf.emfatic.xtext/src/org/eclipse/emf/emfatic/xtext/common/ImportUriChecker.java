/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.common;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.emfatic.xtext.emfatic.Import;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.ClasspathUriResolutionException;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IClasspathUriResolver;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;

/**
 * The default acceptor for import URIs.
 * 
 * It normalizes potentially given class-path URIs.
 * 
 * @see ImportUriGlobalScopeProvider#createURICollector(Resource, Set)
 */
public class ImportUriChecker {
	
	public static final String ECORE_FILE_EXTENSION = "ecore";
	
	public ImportUriChecker(Resource resource, FileExtensionProvider fileExtensionProvider) {
		ResourceSet resourceSet = resource.getResourceSet();
		this.resource = resource;
		this.fileExtensionProvider = fileExtensionProvider;
		if (resourceSet instanceof XtextResourceSet) {
			uriResolver = ((XtextResourceSet) resourceSet).getClasspathUriResolver();
			uriContext = ((XtextResourceSet) resourceSet).getClasspathURIContext();
		} else {
			uriResolver = null;
			uriContext = null;
		}
	}
	
	/**
	 * @throws IllegalArgumentException	if any component parsed from uri is not valid according to
	 * 		{@link URI#createURI(String)}
	 * @param imprtStmt 
	 * @return
	 */
	public Optional<URI> resolveURI(Import imprtStmt) {
		StringOrQualifiedID uri = imprtStmt.getUri();
		if (uri == null) {
			LOG.debug("Cant check uri as no URI part available.");
			return Optional.empty();
		}
		String uriLiteral = uri.getLiteral();
		if (uriLiteral == null) {
			LOG.debug("Cant check uri as no URI is not string literal.");
			return Optional.empty();
		}
		if (uriLiteral.equals(EcorePackage.eINSTANCE.getNsURI())) {
			LOG.debug("URI is Ecore URI, ignoring");
			return Optional.empty();
		}
		return Optional.of(resolve(uriLiteral));
	}
	
	public boolean isValidResoruce(URI uri) {
		if (uri.isPlatform() || uri.isFile()) {
			String ext = uri.fileExtension();
			if (!(ECORE_FILE_EXTENSION.equals(ext) || canHandle(uri))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean resourceExists(URI uri) {
		return EcoreUtil2.isValidUri(resource, uri);
	}
	
	public boolean canHandle(URI uri) {
		return fileExtensionProvider.isValid(uri.fileExtension());
	}
	
	public String validExtensions() {
		return "*." + fileExtensionProvider.getFileExtensions().stream().collect(Collectors.joining(", *."));
	}
	
	public String uriString(Import imprtStmt) {
		StringOrQualifiedID uri = imprtStmt.getUri();
		if (uri == null) {
			return null;
		}
		return uri.getLiteral();
	}
	
	private static final Logger LOG = Logger.getLogger(ImportUriChecker.class);
	
	private final IClasspathUriResolver uriResolver;
	private final Object uriContext;
	private final Resource resource;
	private final FileExtensionProvider fileExtensionProvider;
	
	

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