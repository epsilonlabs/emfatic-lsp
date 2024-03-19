package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundDataTypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.DataType;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class BoundDataTypeWithMultiCopier {
	
	public BoundDataTypeWithMultiCopier load(OnChangeEvictingCache cache) {
		var bound = this.source.getBound();
		if (bound == null) {
			return this;
		}
		EDataType targetEDataType = null;
		ETypeParameter targetTypeParameter = null;
		if (bound instanceof DataType) {
			// Its an Xtext datatype wrapping ecore
			targetEDataType = (EDataType) this.importer.export((DataType)bound);
		} else {
			EObject targetBound = cache.get(bound, bound.eResource(), () -> null);	
			if (targetBound instanceof EDataType) {
				targetEDataType = (EDataType) targetBound;
			} else if (targetBound instanceof ETypeParameter) {
				targetTypeParameter = (ETypeParameter) targetBound;
			}
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
		mCopier = mCopier.load(cache);
		return new BoundDataTypeWithMultiCopier(this.source, targetEDataType, targetTypeParameter, mCopier, this.importer);
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
	
	BoundDataTypeWithMultiCopier(BoundDataTypeWithMulti source, EmfaticImport importer) {
		this(source, null, null, null, importer);
	}
	
	private BoundDataTypeWithMultiCopier(
		BoundDataTypeWithMulti source, 
		EDataType targetEDataType,
		ETypeParameter targetTypeParameter, 
		MultiplicityCopier mCopier,
		EmfaticImport importer) {
		super();
		this.source = source;
		this.targetEDataType = targetEDataType;
		this.targetTypeParamter = targetTypeParameter;
		this.mCopier = mCopier;
		this.importer = importer;
	}

	
	private final BoundDataTypeWithMulti source;
	private final EDataType targetEDataType;
	private final ETypeParameter targetTypeParamter;
	private final MultiplicityCopier mCopier;
	private final EmfaticImport importer;

	
}
