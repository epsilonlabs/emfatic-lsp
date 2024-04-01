package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassExceptWildcard;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;

class BoundClassExceptWildcardCopier extends AbstractClassifierCopier<BoundClassExceptWildcard> {
		
	BoundClassExceptWildcardCopier(BoundClassExceptWildcard bc, EmfaticImport emfaticImport) {
		this(bc, null, null, Collections.emptyList(), emfaticImport);
	}

	BoundClassExceptWildcardCopier load(Content content) {
		var bound = this.source.getBound();
		if (bound == null) {
			return this;
		}
		EObject targetBound = targetBound(content, bound);
		EClassifier targetEClass = null;
		ETypeParameter targetTypeParameter = null;
		if (targetBound instanceof EClassifier) {
			targetEClass = (EClassifier) targetBound;
		} else if (targetBound instanceof ETypeParameter) {
			targetTypeParameter = (ETypeParameter) targetBound;
		}
		List<TypeArgCopier> tArgs = targetTypeArgs(content, this.source.getTypeArgs());
		return new BoundClassExceptWildcardCopier(this.source, targetEClass, targetTypeParameter, tArgs, this.emfaticImport);
	}

	void configure(EClass target) {
		if (this.targetTypeArgs.isEmpty()) {
			target.getESuperTypes().add((EClass) this.targetEClassifier);
		} else {
			var gt = EcoreFactory.eINSTANCE.createEGenericType();
			gt.setEClassifier(this.targetEClassifier);
			this.targetTypeArgs.forEach(ta -> {
				var taGt = EcoreFactory.eINSTANCE.createEGenericType();
				ta.configure(taGt);
				gt.getETypeArguments().add(taGt);
			});
			target.getEGenericSuperTypes().add(gt);
		}
	}
	
	void configure(EReference target) {
		if (this.targetEClassifier != null) {
			target.setEType(targetEClassifier);
		} else if (this.targetTypeParamter != null) {
			var gt = EcoreFactory.eINSTANCE.createEGenericType();
			gt.setETypeParameter(this.targetTypeParamter);
			this.targetTypeArgs.forEach(ta -> {
				var taGt = EcoreFactory.eINSTANCE.createEGenericType();
				ta.configure(taGt);
				gt.getETypeArguments().add(taGt);
			});
			target.setEGenericType(gt);
		}
	}
		
	private BoundClassExceptWildcardCopier(
		BoundClassExceptWildcard source,
		EClassifier targetEClass,
		ETypeParameter targetTypeParamter,
		List<TypeArgCopier> typeArgs,
		EmfaticImport emfaticImport) {
		super(source, targetEClass, targetTypeParamter, typeArgs, emfaticImport);
	}
}