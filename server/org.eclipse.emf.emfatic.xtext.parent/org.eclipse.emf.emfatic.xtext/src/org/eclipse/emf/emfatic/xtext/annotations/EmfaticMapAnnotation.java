package org.eclipse.emf.emfatic.xtext.annotations;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

import com.google.common.base.Objects;

public class EmfaticMapAnnotation implements EmfaticAnnotation {

	@Override
	public String URI() {
		return EMFATIC_MAP_URI;
	}

	@Override
	public String label() {
		return EMFATIC_MAP_LABEL;
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
		return Objects.equal(EmfaticPackage.Literals.PACKAGE_DECL, target);
	}
	
	private static final String EMFATIC_MAP_LABEL = "EmfaticAnnotationMap";
	private static final String EMFATIC_MAP_URI = "http://www.eclipse.org/emf/2004/EmfaticAnnotationMap";

}
