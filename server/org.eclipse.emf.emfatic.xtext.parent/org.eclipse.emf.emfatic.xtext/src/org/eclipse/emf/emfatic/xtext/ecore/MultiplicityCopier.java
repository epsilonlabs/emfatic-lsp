package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.emfatic.xtext.emfatic.Multiplicity;

public class MultiplicityCopier {
	
	MultiplicityCopier() {
		this(null);
	}
	
	MultiplicityCopier(Multiplicity source) {
		this(source, 0, 1);
	}
	
	MultiplicityCopier load() {
		int lb = 0;
		int ub = 1;
		if (this.source != null) {
			if (this.source.getLowerBound() == null) {			// No bounds
				lb = 0;
				ub = -1;
			} else if (this.source.getUpperBound() == null) {	// Only lower bound
				switch (this.source.getLowerBound()) {
				case "?":
					lb = 0;
					ub = 1;
					break;
				case "*":
					lb = 0;
					ub = -1;
					break;
				case "+":
					lb = 1;
					ub = -1;
					break;
				default:
					lb = Integer.valueOf(this.source.getLowerBound());
				}
			} else {
				switch (this.source.getUpperBound()) {
				case "?":
					lb = Integer.valueOf(this.source.getLowerBound());
					ub =  -1;
					break;
				case "*":
					lb = Integer.valueOf(this.source.getLowerBound());
					ub =  -1;
					break;
				default:
					lb = Integer.valueOf(this.source.getLowerBound());
					ub = Integer.valueOf(this.source.getUpperBound());
				}
			}
		}
		return new MultiplicityCopier(this.source, lb, ub);
	}
	
	public void configure(ETypedElement target) {
		target.setLowerBound(this.lowerBound);
		target.setUpperBound(this.upperBound);
	}


	private final Multiplicity source;
	private final int lowerBound;
	private final int upperBound;
	
	private MultiplicityCopier(Multiplicity source, int lowerBound, int upperBound) {
		super();
		this.source = source;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	

}
