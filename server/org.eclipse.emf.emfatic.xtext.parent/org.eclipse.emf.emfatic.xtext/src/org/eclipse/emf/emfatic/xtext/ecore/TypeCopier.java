package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeWithMulti;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class TypeCopier {
		
	TypeCopier(TypeWithMulti twm) {
		this(twm, null, null);
	}
	
	void configure(EStructuralFeature target) {
		if (this.cCopier == null) {
			throw new IllegalStateException("Call to configure before calling load.");
		}
		this.cCopier.configure(target);
		this.mCopier.configure(target);
	}
	
	/**
	 * Returns true if the Type references an EClass and not an EDatatype/EEnum
	 * @return
	 */
	boolean toEClass() {
		if (this.cCopier == null) {
			throw new IllegalStateException("Call to toEClass before calling load.");
		}
		return this.cCopier.toEClass();
	}

	TypeCopier load(OnChangeEvictingCache cache) {
		ClassifierCopier cCopier = cache.get(this.twm.getType(), this.twm.getType().eResource(), () -> (ClassifierCopier) null);
		if (cCopier == null) {
			throw new IllegalArgumentException("Target element not found for " + this.twm.getType());
		}
		MultiplicityCopier mCopier;
		if (this.twm.getMultiplicity() != null) {
			mCopier = cache.get(this.twm.getMultiplicity(), this.twm.eResource(), () ->(MultiplicityCopier)null );
			if (mCopier == null) {
				throw new IllegalArgumentException("Target element not found for " + this.twm.getMultiplicity());
			}
		} else {
			mCopier = new MultiplicityCopier();
		}
		return new TypeCopier(this.twm, cCopier.load(cache), mCopier.load(cache));
	}
	
	private TypeCopier(TypeWithMulti twm, ClassifierCopier cCopier, MultiplicityCopier mCopier) {
		super();
		this.twm = twm;
		this.cCopier = cCopier;
		this.mCopier = mCopier;
	}
	
	private final TypeWithMulti twm;
	private final ClassifierCopier cCopier;
	private final MultiplicityCopier mCopier;
	
}