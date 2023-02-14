package org.eclipse.emf.emfatic.xtext.annotations;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

import com.google.common.base.Objects;

public class GenModelAnnotation implements EmfaticAnnotation {

	@Override
	public String URI() {
		return GEN_MODEL_URI;
	}

	@Override
	public String label() {
		return GEN_MODEL_LABEL;
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
		String namelc = name.toLowerCase();
		switch(namelc) {
		case "documentation":
			return true;
		case "copyright":
			return true;
		case "get":
		case "suppressedGetVisibility":
		case "suppressedSetVisibility":
		case "suppressedIsSetVisibility":
		case "suppressedUnsetVisibility":
			return isFeature(target);
		case "body":
			return Objects.equal(target, EmfaticPackage.Literals.OPERATION);
		case "create":
		case "convert":
			return Objects.equal(target, EmfaticPackage.Literals.DATA_TYPE);
			
		}
		return false;
	}
	
	private static final String GEN_MODEL_LABEL = "GenModel";
	private static final String GEN_MODEL_URI = "http://www.eclipse.org/emf/2002/GenModel";
	
	private boolean isFeature(EClass target) {
		return Objects.equal(target, EmfaticPackage.Literals.ATTRIBUTE)
				|| Objects.equal(target, EmfaticPackage.Literals.REFERENCE);
	}

}
