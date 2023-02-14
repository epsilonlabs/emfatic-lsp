package org.eclipse.emf.emfatic.xtext.annotations;

import org.eclipse.emf.ecore.EClass;

public class DefaultEmfaticAnnotation implements EmfaticAnnotation {

	private final String uri;
	private final String label;

	public DefaultEmfaticAnnotation(String label, String uri) {
		this.label = label;
		this.uri = uri;
	}

	@Override
	public String URI() {
		return this.uri;
	}

	@Override
	public String label() {
		return this.label;
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
		return true;
	}

}
