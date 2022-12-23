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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.FilteringScope;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.xtext.scoping.impl.MultimapBasedSelectable;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * The Class EmfaticINALSP.
 */
public class EmfaticINALSP extends ImportedNamespaceAwareLocalScopeProvider {
	
	/**
	 * Since our {@code importedNamespace} feature does not return a String,
	 * we need to process it accordingly. If the {@code importedNamespace} is
	 * a URI, we use the package's name; if it is a name, we use that.
	 * 
	 * Imported packages are cached so we can use the information to filter
	 * the Global Scope.
	 * 
	 * TODO Test nested imports: import a.b
	 */
	@Override
	protected String getImportedNamespace(EObject object) {
		EStructuralFeature feature = object.eClass().getEStructuralFeature("importedNamespace");
		if (feature == null) {
			return null;
		}
		StringOrQualifiedID value = (StringOrQualifiedID) object.eGet(feature);
		if (value == null) {
			return null;
		}
		String name =  null;
		String uri = value.getLiteral();
		if (uri != null) {
			// Imported metamodels by URI are loaded in the EmfaticGSP
			// If the EPackage for the URI exists, we add use the package
			// name as the imported namespace
			EPackage ep = EPackage.Registry.INSTANCE.getEPackage(uri);
			if (ep == null) {
				return null;
			}
			name = ep.getName();
		} else {
			name = value.getId();
		}
		System.out.println("Adding imported namespace " + name);
		getImportedPackages(object.eResource()).add(name);
		return name;
	}
	
	/**
	 * Add ECore as an implicit import
	 */
	@Override
	protected List<ImportNormalizer> getImplicitImports(boolean ignoreCase) {
		return singletonList(new ImportNormalizer(
				QualifiedName.create("ecore"), 
				true, 
				ignoreCase));
	}
	
	/**
	 * Create an {@ ImportScope} for the global scope that uses a {@code FilteringScope}
	 * to remove from the scope all elements that do not belong to a package
	 * that is explicitly imported.
	 * TODO Probable need a qualified name resolver for the filter
	 */
	@Override
	protected IScope getResourceScope(Resource res, EReference reference) {
		getImportedPackages(res).add("ecore");
		IScope globalScope = getGlobalScope(res, reference);
		List<ImportNormalizer> normalizers = getImplicitImports(isIgnoreCase(reference));
		if (!normalizers.isEmpty()) {
			globalScope = createImportScope(
					new FilteringScope(globalScope, eod -> getImportedPackages(res).contains(eod.getUserData("namespace"))),
					normalizers,
					new MultimapBasedSelectable(globalScope.getAllElements()),
					reference.getEReferenceType(),
					isIgnoreCase(reference));
		}
		return globalScope;
	}
	
	/** The cache. */
	@Inject
	private IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;
	
	/**
	 * Gets the imported packages.
	 *
	 * @param res the res
	 * @return the imported packages
	 */
	private Set<String> getImportedPackages(Resource res) {
		return cache.get("importedPackages", res, new Provider<Set<String>>() {
			@Override
			public Set<String> get() {
				return internalgetImportedPackages();
			}
		});
	}
	
	/**
	 * Internal get imported packages.
	 *
	 * @return the sets the
	 */
	private Set<String> internalgetImportedPackages() {
		return new HashSet<>();
	}
}
