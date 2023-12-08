package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassifierDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeArg;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class AbstractClassifierCopier<C extends EObject> {

	protected final EClassifier targetBound;
	
	protected EClassifier targetBound(OnChangeEvictingCache cache, ClassifierDecl bound) {
		EClassifier type;
		if (this.source.eResource() == bound.eResource()) {
			type = cache.get(bound, bound.eResource(), () -> null);
		} else {
			type = this.emfaticImport.export(bound);
		}
		return type;
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
	protected final List<TypeArgCopier> targetTypeArgs;
	protected final EmfaticImport emfaticImport;

	public AbstractClassifierCopier(
		C source,
		EClassifier targetBound,
		List<TypeArgCopier> typeArgs,
		EmfaticImport emfaticImport) {
		super();
		this.source = source;
		this.targetBound = targetBound;
		this.targetTypeArgs = typeArgs;
		this.emfaticImport = emfaticImport;
	}

}