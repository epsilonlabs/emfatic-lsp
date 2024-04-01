package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;

class BoundClassifierExceptWildcardCopier extends AbstractClassifierCopier<BoundClassifierExceptWildcard> implements TypeArgCopier {
		
	BoundClassifierExceptWildcardCopier(BoundClassifierExceptWildcard source, EmfaticImport emfaticImport) {
		this(source, null, null, Collections.emptyList(), emfaticImport);
	}

	@Override
	public BoundClassifierExceptWildcardCopier load(Content content) {
		var bound = this.source.getBound();
		if (bound == null) {
			return this;
		}
		EObject targetBound = targetBound(content, bound);
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
				targetTypeArgs(content, this.source.getTypeArgs()), 
				this.emfaticImport);
	}

	@Override
	public void configure(ETypedElement te) {
		if (this.targetEClassifier != null) {
			te.setEType(this.targetEClassifier);			
		}
		if (!this.targetTypeArgs.isEmpty()) {
			var teGt = EcoreFactory.eINSTANCE.createEGenericType();
			te.setEGenericType(teGt);
			this.targetTypeArgs.forEach(ta -> {
				var taGt = EcoreFactory.eINSTANCE.createEGenericType();
				ta.configure(taGt);
				teGt.getETypeArguments().add(taGt);
			});	
		}
	}
	
	@Override
	public void configure(EGenericType gt) {
		if (this.targetTypeParamter != null) {
			gt.setETypeParameter(this.targetTypeParamter);
		}
		if (this.targetEClassifier != null) {
			gt.setEClassifier(this.targetEClassifier);
		}
		this.targetTypeArgs.forEach(ta -> {
			var taGt = EcoreFactory.eINSTANCE.createEGenericType();
			ta.configure(taGt);
			gt.getETypeArguments().add(taGt);
		});
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