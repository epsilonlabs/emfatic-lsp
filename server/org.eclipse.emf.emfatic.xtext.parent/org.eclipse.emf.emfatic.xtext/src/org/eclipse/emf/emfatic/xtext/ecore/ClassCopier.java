package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassExceptWildcard;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

class ClassCopier extends AbstractClassifierCopier<BoundClassExceptWildcard> {
		
	ClassCopier(BoundClassExceptWildcard bc, EmfaticImport emfaticImport) {
		this(bc, null, Collections.emptyList(), emfaticImport);
	}

	ClassCopier load(OnChangeEvictingCache cache) {
		var bound = this.source.getBound();
		if (bound == null) {
			return this;
		}
		EClassifier type = targetBound(cache, bound);
		List<TypeArgCopier> tArgs = targetTypeArgs(cache, this.source.getTypeArgs());
		return new ClassCopier(this.source, type, tArgs, this.emfaticImport);
	}

	public void configure(EClass target) {
		if (this.targetTypeArgs.isEmpty()) {
			target.getESuperTypes().add((EClass) this.targetBound);
		} else {
			var gt = EcoreFactory.eINSTANCE.createEGenericType();
			gt.setEClassifier(this.targetBound);
			this.targetTypeArgs.forEach(ta -> {
				var taGt = EcoreFactory.eINSTANCE.createEGenericType();
				ta.configure(taGt);
				gt.getETypeArguments().add(taGt);
			});
			target.getEGenericSuperTypes().add(gt);
		}
	}
		
	private ClassCopier(
		BoundClassExceptWildcard source,
		EClassifier targetBound,
		List<TypeArgCopier> typeArgs,
		EmfaticImport emfaticImport) {
		super(source, targetBound, typeArgs, emfaticImport);
	}
}