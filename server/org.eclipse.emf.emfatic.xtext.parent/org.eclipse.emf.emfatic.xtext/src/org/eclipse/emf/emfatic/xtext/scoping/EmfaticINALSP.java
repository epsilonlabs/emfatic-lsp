package org.eclipse.emf.emfatic.xtext.scoping;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.ISelectable;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.xtext.scoping.impl.MultimapBasedSelectable;

public class EmfaticINALSP extends ImportedNamespaceAwareLocalScopeProvider {
	
	// Save a list of imported packages
	List<String> importedPackages = new ArrayList<>();

	@Override
	protected String getImportedNamespace(EObject object) {
		EStructuralFeature feature = object.eClass().getEStructuralFeature("importedNamespace");
		if (feature == null) {
			return null;
		}
		StringOrQualifiedID value = (StringOrQualifiedID) object.eGet(feature);
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
		importedPackages.add(name);
		return name;
	}
	
	
	protected IScope getLocalElementsScope(
		IScope parent,
		final EObject context,
		final EReference reference) {
		System.out.println("getLocalElementsScope " + context);
		IScope result = parent;
		ISelectable allDescriptions = getAllDescriptions(context.eResource());
		QualifiedName name = getQualifiedNameOfLocalElement(context);
		boolean ignoreCase = isIgnoreCase(reference);
		final List<ImportNormalizer> namespaceResolvers = getImportedNamespaceResolvers(context, ignoreCase);
		if (!namespaceResolvers.isEmpty()) {
			if (isRelativeImport() && name!=null && !name.isEmpty()) {
				ImportNormalizer localNormalizer = doCreateImportNormalizer(name, true, ignoreCase); 
				result = createImportScope(result, singletonList(localNormalizer), allDescriptions, reference.getEReferenceType(), isIgnoreCase(reference));
			}
			result = createImportScope(result, namespaceResolvers, null, reference.getEReferenceType(), isIgnoreCase(reference));
		}
		if (name!=null) {
			ImportNormalizer localNormalizer = doCreateImportNormalizer(name, true, ignoreCase); 
			result = createImportScope(result, singletonList(localNormalizer), allDescriptions, reference.getEReferenceType(), isIgnoreCase(reference));
		}
		return result;
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
	
	@Override
	protected IScope getResourceScope(Resource res, EReference reference) {
		EObject context = res.getContents().get(0);
		IScope globalScope = getGlobalScope(res, reference);
		List<ImportNormalizer> normalizers = getImplicitImports(isIgnoreCase(reference));
		if (!normalizers.isEmpty()) {
			globalScope = createImportScope(
					globalScope,
					normalizers,
					new MultimapBasedSelectable(globalScope.getAllElements()),
					reference.getEReferenceType(),
					isIgnoreCase(reference));
		}
		//return getResourceScope(globalScope, context, reference);
		return globalScope;
	}
	
//	protected IScope getResourceScope(Resource res, EReference reference) {
//		System.out.println("getResourceScope");
//		System.out.println(res);
//		System.out.println(reference);
//		EObject context = res.getContents().get(0);
//		IScope globalScope = getGlobalScope(res, reference);
//		
//		Predicate<IEObjectDescription> resFilter = new Predicate<IEObjectDescription>() {
//		      @Override
//		      public boolean apply(IEObjectDescription input) {
//		      	// Filter out if not in the same resource
//		      	URI inputBase = input.getEObjectURI().trimFragment();
//		      	boolean result = inputBase.hashCode() == res.getURI().hashCode();
//		      	if (result) {
//		      		System.out.println("Filtering other resoruce " + context);
//		      	}
//				//return result;
//		      	//return inputBase.hashCode() == context.getURI().hashCode();
//		      	return true;
//		      }
//		  };
//		
//		IScope filteredScope = SelectableBasedScope.createScope(
//				IScope.NULLSCOPE,
//				new ScopeBasedSelectable(globalScope),
//				resFilter,
//				reference.getEReferenceType(),
//				isIgnoreCase(reference));
//		
//		
//		List<ImportNormalizer> normalizers = getImplicitImports(isIgnoreCase(reference));
//		if (!normalizers.isEmpty()) {
//			globalScope = createImportScope(globalScope, normalizers, null, reference.getEReferenceType(), isIgnoreCase(reference));
//		}
//		return getResourceScope(filteredScope, context, reference);
//	}
	
}
