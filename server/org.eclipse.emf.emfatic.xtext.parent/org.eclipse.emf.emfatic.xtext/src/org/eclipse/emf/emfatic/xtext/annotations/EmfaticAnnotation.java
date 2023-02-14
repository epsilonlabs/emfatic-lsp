package org.eclipse.emf.emfatic.xtext.annotations;

import org.eclipse.emf.ecore.EClass;

public interface EmfaticAnnotation {
	
	public interface Key {
	
		/**
		 * The EClass that this annoation can be applied to.
		 * @return
		 */
		EClass appliesTo();
		
		String name();
	}
	
	String URI();
	
	String label();
	
	boolean isValidKey(String name, EClass target);

	
	

}
