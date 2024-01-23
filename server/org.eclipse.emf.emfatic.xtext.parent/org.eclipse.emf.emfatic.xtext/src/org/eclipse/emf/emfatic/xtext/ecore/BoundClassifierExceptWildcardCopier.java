package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

class BoundClassifierExceptWildcardCopier extends AbstractClassifierCopier<BoundClassifierExceptWildcard> implements TypeArgCopier {
		
	BoundClassifierExceptWildcardCopier(BoundClassifierExceptWildcard source, EmfaticImport emfaticImport) {
		this(source, null, null, Collections.emptyList(), emfaticImport);
	}

	@Override
	public BoundClassifierExceptWildcardCopier load(OnChangeEvictingCache cache) {
		var bound = this.source.getBound();
		if (bound == null) {
			return this;
		}
		EObject targetBound = targetBound(cache, bound);
		EClassifier targetEClassifier = null;
		ETypeParameter targetTypeParameter = null;
		if (targetBound instanceof EClassifier) {
			targetEClassifier = (EClassifier) targetBound;
		} else if (targetBound instanceof ETypeParameter) {
			targetTypeParameter = (ETypeParameter) targetBound;
		}
		return new BoundClassifierExceptWildcardCopier(
				this.source, 
				targetEClassifier,
				targetTypeParameter,
				targetTypeArgs(cache, this.source.getTypeArgs()), 
				this.emfaticImport);
	}

	@Override
	public void configure(EGenericType gt) {
		gt.setEClassifier(this.targetEClassifier);
		this.targetTypeArgs.forEach(ta -> {
			var taGt = EcoreFactory.eINSTANCE.createEGenericType();
			ta.configure(taGt);
			gt.getETypeArguments().add(taGt);
		});
	}
	
	void configure(EAttribute target) {
		if (this.targetEClassifier != null) {
			target.setEType(this.targetEClassifier);
		}
		if (this.targetTypeParamter != null) {
			var tpGt = EcoreFactory.eINSTANCE.createEGenericType();
			tpGt.setETypeParameter(this.targetTypeParamter);
			target.setEGenericType(tpGt);
		}
		if (!this.targetTypeArgs.isEmpty()) {
			var gt = EcoreFactory.eINSTANCE.createEGenericType();
			gt.setEClassifier(this.targetEClassifier);
			this.targetTypeArgs.forEach(ta -> {
				var taGt = EcoreFactory.eINSTANCE.createEGenericType();
				ta.configure(taGt);
				gt.getETypeArguments().add(taGt);
			});
			target.setEGenericType(gt);
		}
	}
	
	public boolean toEClass() {
		return this.targetEClassifier instanceof EClass;
	}
		
	private BoundClassifierExceptWildcardCopier(
		BoundClassifierExceptWildcard source,
		EClassifier targetEClassifier,
		ETypeParameter targetTypeParamter,
		List<TypeArgCopier> targetTypeArgs,
		EmfaticImport emfaticImport) {
		super(source, targetEClassifier, targetTypeParamter, targetTypeArgs, emfaticImport);
	}

}