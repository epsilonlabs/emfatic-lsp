package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundDataTypeWithMulti;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class BoundDataTypeWithMultiCopier {

	public BoundDataTypeWithMultiCopier(BoundDataTypeWithMulti source) {
		this(source, null, null, null);
	}
	
	public BoundDataTypeWithMultiCopier load(OnChangeEvictingCache cache) {
		var bound = this.source.getBound();
		if (bound == null) {
			return this;
		}
		EObject targetBound = cache.get(bound, bound.eResource(), () -> null);
		EDataType targetEDataType = null;
		ETypeParameter targetTypeParameter = null;
		if (targetBound instanceof EDataType) {
			targetEDataType = (EDataType) targetBound;
		} else if (targetBound instanceof ETypeParameter) {
			targetTypeParameter = (ETypeParameter) targetBound;
		}
		MultiplicityCopier mCopier;
		if (this.source.getMultiplicity() != null) {
			mCopier = cache.get(this.source.getMultiplicity(), this.source.eResource(), () ->(MultiplicityCopier)null );
			if (mCopier == null) {
				throw new IllegalArgumentException("Target element not found for " + this.source.getMultiplicity());
			}
		} else {
			mCopier = new MultiplicityCopier();
		}
		return new BoundDataTypeWithMultiCopier(this.source, targetEDataType, targetTypeParameter, mCopier);
	}
	
	void configure(EAttribute target) {
		if (this.targetEDataType != null) {
			target.setEType(this.targetEDataType);
		}
		if (this.targetTypeParamter != null) {
			var tpGt = EcoreFactory.eINSTANCE.createEGenericType();
			tpGt.setETypeParameter(this.targetTypeParamter);
			target.setEGenericType(tpGt);
		}
		this.mCopier.configure(target);
	}
	
	private BoundDataTypeWithMultiCopier(
		BoundDataTypeWithMulti source, 
		EDataType targetEDataType,
		ETypeParameter targetTypeParamter, 
		MultiplicityCopier mCopier) {
		super();
		this.source = source;
		this.targetEDataType = targetEDataType;
		this.targetTypeParamter = targetTypeParamter;
		this.mCopier = mCopier;
	}

	private final BoundDataTypeWithMulti source;
	private final EDataType targetEDataType;
	private final ETypeParameter targetTypeParamter;
	private final MultiplicityCopier mCopier;
	
}
