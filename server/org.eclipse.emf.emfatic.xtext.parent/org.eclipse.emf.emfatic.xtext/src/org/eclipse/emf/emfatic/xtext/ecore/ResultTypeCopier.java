package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.emfatic.xtext.emfatic.ResultType;

public class ResultTypeCopier {

	ResultTypeCopier(ResultType source) {
		this(source, null, null);
	}
	
	ResultTypeCopier load(Content content) {
		TypeWithMultiCopier cCopier = null;
		MultiplicityCopier mCopier = null;
		if (this.source.getType() != null) {
			cCopier = content.equivalent(this.source.getType());
			if (cCopier == null) {
				throw new IllegalArgumentException("Target element not found for " + this.source.getType());
			}
			cCopier = cCopier.load(content);
			if (this.source.getType().getMultiplicity() != null) {
				mCopier = content.equivalent(this.source.getType().getMultiplicity());
				if (mCopier == null) {
					throw new IllegalArgumentException("Target element not found for " + this.source.getType().getMultiplicity());
				}
			} else {
				mCopier = new MultiplicityCopier();
			}
			mCopier = mCopier.load();
		}
		return new ResultTypeCopier(this.source, cCopier, mCopier);
	}
	
	void configure(EOperation target) {
		if (this.tCopier != null) {
			this.tCopier.configure(target);
			this.mCopier.configure(target);
		}
	}
	
	private final ResultType source;
	private final TypeWithMultiCopier tCopier;
	private final MultiplicityCopier mCopier;
	
	private ResultTypeCopier(
		ResultType source,
		TypeWithMultiCopier cCopier,
		MultiplicityCopier mCopier) {
		super();
		this.source = source;
		this.tCopier = cCopier;
		this.mCopier = mCopier;
	}
	
	

}
