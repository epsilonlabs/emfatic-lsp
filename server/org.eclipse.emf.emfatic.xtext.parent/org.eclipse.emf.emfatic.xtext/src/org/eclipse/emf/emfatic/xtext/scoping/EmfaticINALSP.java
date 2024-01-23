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

import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.Import;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.FilteringScope;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ImportScope;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.xtext.scoping.impl.MultimapBasedSelectable;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * The Class EmfaticINALSP is responsible for capturing alias information and for creating 
 * {@link ImportScope}s that filter out Emfatic resources in the project not explicitly
 * imported and for creating the {@link EmfaticAliasingScope} that will handle aliases.
 */
public class EmfaticINALSP extends ImportedNamespaceAwareLocalScopeProvider {
	
	/**
	 * Since our {@code importedNamespace} feature does not return a String,
	 * we need to process it accordingly. If the {@code uri} is
	 * a URI, we use the package's name; if it is a name, we use that.
	 * 
	 * Imported metamodels by URI are loaded in the {@link EmfaticGSP}. If the
	 * EPackage for the URI exists, we use the package name as the imported
	 * namespace. If not we use the {@code Import#id} as the namespace. 
	 * 
	 * Imported packages are cached so we can use the information to filter
	 * the Global Scope. Alias names are stored so they can be later used for scoping.
	 * 
	 * TODO Test nested imports: import a.b
	 */
	@Override
	protected String getImportedNamespace(EObject object) {
		// We know we only support imports within Import statements
		if (!object.eClass().equals(EmfaticPackage.Literals.IMPORT)) {
			return null;
		}
		Import imprt = (Import) object;
		StringOrQualifiedID value = imprt.getUri();
		if (value == null) {
			return null;
		}
		Optional<String> alias = Optional.ofNullable(imprt.getAlias());
		String name =  null;
		String uri = value.getLiteral();
		if (uri != null) {
			Resource metamodelResource = EcoreUtil2.getResource(object.eResource(), uri.toString());
			if (metamodelResource == null) {
				LOG.error("Metamodel with uri " + uri + " should have been loaded by the GSP.");
				return null;
			}
			if (metamodelResource.getContents().isEmpty()) {
				LOG.warn("Metamodel with uri " + uri + " is empty. Check the URI and target file.");
				return null;
			}
			EObject root = metamodelResource.getContents().get(0);
			if (root == null) {
				return null;
			}
			if (root instanceof EPackage) {
				name = ((EPackage) root).getName();	
			} else if (root instanceof CompUnit) {
				name = ((CompUnit) root).getPackage().getName();
			} else {
				LOG.error("Metamodel with uri " + uri + " does not have an EPackage or CompUnit at the root.");
				return null;
			}
		} else {
			name = value.getId();
		}
		getNamespaceAliases(object.eResource()).addAlias(name, alias.orElse(name));
		if(LOG.isDebugEnabled()) {
			LOG.debug("Added alias " +  alias.orElse(name) + " for EPackage " + name);
		}
		return alias.orElse(name);
	}
	
	/**
	 * Add ECore as an implicit import
	 * @param res 
	 */
	protected List<ImportNormalizer> getImplicitImports(boolean ignoreCase, Resource res) {
		return singletonList(new ImportNormalizer(
				QualifiedName.create("ecore"), 
				true, 
				ignoreCase));
	}
	
	/**
	 * Create an {@ ImportScope} for the global scope that uses a {@code FilteringScope}
	 * to remove from the scope all elements that do not belong to a package
	 * that is explicitly imported.
	 */
	@Override
	protected IScope getResourceScope(Resource res, EReference reference) { 
		getNamespaceAliases(res).addAlias("ecore", "ecore");
		IScope globalScope = getGlobalScope(res, reference);
		List<ImportNormalizer> normalizers = getImplicitImports(isIgnoreCase(reference), res);
		if (!normalizers.isEmpty()) {
			globalScope = createImportScope(
					new FilteringScope(
							new EmfaticAliasingScope(globalScope, this.getNamespaceAliases(res)),
							eod -> 
								getNamespaceAliases(res).hasOriginal(eod.getUserData(EmfaticRDS.USERDATA_NAMESPACE_KEY))),
					normalizers,
					new MultimapBasedSelectable(globalScope.getAllElements()),
					reference.getEReferenceType(),
					isIgnoreCase(reference));
		}
		return globalScope;
	}
	
	private static final Logger LOG = Logger.getLogger(EmfaticINALSP.class);
	
	@Inject
	private IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;
	
	/**
	 * Gets the imported packages.
	 *
	 * @param res the res
	 * @return the imported packages
	 */
	private Aliases getNamespaceAliases(Resource res) {
		return cache.get("importedPackages", res, new Provider<Aliases>() {
			@Override
			public Aliases get() {
				return internalNamespaceAliases();
			}
		});
	}
	
	/**
	 * Internal get imported packages.
	 *
	 * @return the sets the
	 */
	private Aliases internalNamespaceAliases() {
		return new Aliases();
	}
}
