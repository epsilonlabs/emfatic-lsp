package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeWithMulti;

public class TypeWithMultiCopier {
		
	public void configure(ETypedElement target) {
		if (this.cCopier == null) {
			throw new IllegalStateException("Call to configure before calling load.");
		}
		
		this.cCopier.configure(target);
		this.mCopier.configure(target);
	}
	
	TypeWithMultiCopier(TypeWithMulti twm) {
		this(twm, null, null);
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

	TypeWithMultiCopier load(Content content) {
		BoundClassifierExceptWildcardCopier cCopier = content.equivalent(this.twm.getType());
		if (cCopier == null) {
			throw new IllegalArgumentException("Target element not found for " + this.twm.getType());
		}
		MultiplicityCopier mCopier;
		if (this.twm.getMultiplicity() != null) {
			mCopier = content.equivalent(this.twm.getMultiplicity());
			if (mCopier == null) {
				throw new IllegalArgumentException("Target element not found for " + this.twm.getMultiplicity());
			}
		} else {
			mCopier = new MultiplicityCopier();
		}
		return new TypeWithMultiCopier(this.twm, cCopier.load(content), mCopier.load());
	}

	
	private final TypeWithMulti twm;
	private final BoundClassifierExceptWildcardCopier cCopier;
	private final MultiplicityCopier mCopier;
	
	private TypeWithMultiCopier(TypeWithMulti twm, BoundClassifierExceptWildcardCopier cCopier, MultiplicityCopier mCopier) {
		super();
		this.twm = twm;
		this.cCopier = cCopier;
		this.mCopier = mCopier;
	}
	
}