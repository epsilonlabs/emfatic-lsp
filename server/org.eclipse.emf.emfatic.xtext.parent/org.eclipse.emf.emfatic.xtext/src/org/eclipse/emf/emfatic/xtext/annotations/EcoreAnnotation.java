package org.eclipse.emf.emfatic.xtext.annotations;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

import com.google.common.base.Objects;

public class EcoreAnnotation implements EmfaticAnnotation {

	@Override
	public String URI() {
		return ECORE_URI;
	}

	@Override
	public String label() {
		return ECORE_LABEL;
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
		String namelc = name.toLowerCase();
		switch(namelc) {
		case "constraints":
			return true;
		case "validationDelegates":
			return Objects.equal(target, EmfaticPackage.Literals.PACKAGE_DECL);
		}
		return false;
	}
	
	private static final String ECORE_LABEL = "Ecore";
	private static final String ECORE_URI = "http://www.eclipse.org/emf/2002/Ecore";


}