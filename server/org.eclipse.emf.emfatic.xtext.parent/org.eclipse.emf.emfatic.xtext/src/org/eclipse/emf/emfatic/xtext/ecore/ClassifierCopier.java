package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeArg;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

class ClassifierCopier extends TypeArgCopier {
		
	ClassifierCopier(BoundClassifierExceptWildcard bc, EmfaticImport emfaticImport) {
		this(bc, null, Collections.emptyList(), emfaticImport);
	}

	ClassifierCopier load(OnChangeEvictingCache cache) {
		var bound = this.bc.getBound();
		EClassifier type;
		if (this.bc.eResource() == bound.eResource()) {
			type = cache.get(bound, bound.eResource(), () -> null);
		} else {
			type = this.emfaticImport.export(bound);
		}
		List<TypeArgCopier> tArgs = new ArrayList<>();
		for (TypeArg ta : this.bc.getTypeArgs()) {
			var taTarget = cache.get(ta, ta.eResource(), () -> null);
			if (taTarget == null) {
				throw new IllegalArgumentException("Target element not found for " + ta);
			}
		}
		return new ClassifierCopier(this.bc, type, tArgs, this.emfaticImport);
	}
	
	void configure(EAttribute target) {
		if (this.typeArgs.isEmpty()) {
			target.setEType(this.type);
		} else {
			var gt = EcoreFactory.eINSTANCE.createEGenericType();
			gt.setEClassifier(this.type);
			this.typeArgs.forEach(ta -> ta.configure(gt));
		}
	}

	@Override
	void configure(EGenericType gt) {
		gt.setEClassifier(this.type);
		this.typeArgs.forEach(ta -> ta.configure(gt));
		
	}
		
	private final BoundClassifierExceptWildcard bc;
	private final EClassifier type;
	private final List<TypeArgCopier> typeArgs;
	private final EmfaticImport emfaticImport;
	
	private ClassifierCopier(
		BoundClassifierExceptWildcard bc,
		EClassifier boundTarget,
		List<TypeArgCopier> typeArgs,
		EmfaticImport emfaticImport) {
		this.bc = bc;
		this.type = boundTarget;
		this.typeArgs = typeArgs;
		this.emfaticImport = emfaticImport;
	}
	
}