package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassRefWithMulti;

public class ClassRefWithMultiCopier {
	
	public void configure(EReference target) {
		if (this.cCopier == null) {
			throw new IllegalStateException("Call to configure before calling load.");
		}
		
		this.cCopier.configure(target);
		this.mCopier.configure(target);
	}

	ClassRefWithMultiCopier(ClassRefWithMulti source) {
		this(source, null, null);
	}
		
	ClassRefWithMultiCopier load(Content content) {
		BoundClassExceptWildcardCopier cCopier = content.equivalent(this.source.getType());
		if (cCopier == null) {
			throw new IllegalArgumentException("Target element not found for " + this.source.getType());
		}
		MultiplicityCopier mCopier;
		if (this.source.getMultiplicity() != null) {
			mCopier = content.equivalent(this.source.getMultiplicity());
			if (mCopier == null) {
				throw new IllegalArgumentException("Target element not found for " + this.source.getMultiplicity());
			}
		} else {
			mCopier = new MultiplicityCopier();
		}
		return new ClassRefWithMultiCopier(this.source, cCopier.load(content), mCopier.load());
	}
	
	private final ClassRefWithMulti source;
	private final BoundClassExceptWildcardCopier cCopier;
	private final MultiplicityCopier mCopier;
	
	private ClassRefWithMultiCopier(
		ClassRefWithMulti source,
		BoundClassExceptWildcardCopier cCopier,
		MultiplicityCopier mCopier) {
		super();
		this.source = source;
		this.cCopier = cCopier;
		this.mCopier = mCopier;
	}
	
	
}
