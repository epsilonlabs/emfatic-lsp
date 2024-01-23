package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassifierDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeArg;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class AbstractClassifierCopier<C extends EObject> {

	public AbstractClassifierCopier(
		C source,
		EClassifier targetEClassifier,
		ETypeParameter targetTypeParamter,
		List<TypeArgCopier> typeArgs,
		EmfaticImport emfaticImport) {
		super();
		this.source = source;
		this.targetEClassifier = targetEClassifier;
		this.targetTypeParamter = targetTypeParamter;
		this.targetTypeArgs = typeArgs;
		this.emfaticImport = emfaticImport;
	}
	
	protected EObject targetBound(OnChangeEvictingCache cache, EObject bound) {
		if (bound instanceof TypeParam) {
			return cache.get(bound, bound.eResource(), () -> null);
		} else {	// ClassifierDecl
			EClassifier type;
			if (this.source.eResource() == bound.eResource()) {
				type = cache.get(bound, bound.eResource(), () -> null);
			} else {
				type = this.emfaticImport.export((ClassifierDecl)bound);
			}
			return type;
		}
	}

	protected List<TypeArgCopier> targetTypeArgs(OnChangeEvictingCache cache, EList<TypeArg> typeArgs2) {
		List<TypeArgCopier> tArgs = new ArrayList<>();
		for (TypeArg ta : typeArgs2) {
			TypeArgCopier taTarget = cache.get(ta, ta.eResource(), () -> null);
			if (taTarget == null) {
				throw new IllegalArgumentException("Target element not found for " + ta);
			}
			tArgs.add(taTarget.load(cache));
		}
		return tArgs;
	}

	protected final C source;
	protected final EClassifier targetEClassifier;
	protected final ETypeParameter targetTypeParamter;
	protected final List<TypeArgCopier> targetTypeArgs;
	protected final EmfaticImport emfaticImport;

}