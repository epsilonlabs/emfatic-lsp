package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

class ClassifierCopier extends AbstractClassifierCopier<BoundClassifierExceptWildcard> implements TypeArgCopier {
		
	ClassifierCopier(BoundClassifierExceptWildcard source, EmfaticImport emfaticImport) {
		this(source, null, Collections.emptyList(), emfaticImport);
	}

	@Override
	public ClassifierCopier load(OnChangeEvictingCache cache) {
		var bound = this.source.getBound();
		if (bound == null) {
			return this;
		}
		return new ClassifierCopier(
				this.source, 
				targetBound(cache, bound), 
				targetTypeArgs(cache, this.source.getTypeArgs()), 
				this.emfaticImport);
	}

	@Override
	public void configure(EGenericType gt) {
		gt.setEClassifier(this.targetBound);
		this.targetTypeArgs.forEach(ta -> {
			var taGt = EcoreFactory.eINSTANCE.createEGenericType();
			ta.configure(taGt);
			gt.getETypeArguments().add(taGt);
		});
	}
	
	void configure(EStructuralFeature target) {
		target.setEType(this.targetBound);
		if (!this.targetTypeArgs.isEmpty()) {
			var gt = EcoreFactory.eINSTANCE.createEGenericType();
			gt.setEClassifier(this.targetBound);
			this.targetTypeArgs.forEach(ta -> {
				var taGt = EcoreFactory.eINSTANCE.createEGenericType();
				ta.configure(taGt);
				gt.getETypeArguments().add(taGt);
			});
			target.setEGenericType(gt);
		}
	}
	
	public boolean toEClass() {
		return this.targetBound instanceof EClass;
	}
		
	private ClassifierCopier(
		BoundClassifierExceptWildcard source,
		EClassifier targetBound,
		List<TypeArgCopier> targetTypeArgs,
		EmfaticImport emfaticImport) {
		super(source, targetBound, targetTypeArgs, emfaticImport);
	}

}