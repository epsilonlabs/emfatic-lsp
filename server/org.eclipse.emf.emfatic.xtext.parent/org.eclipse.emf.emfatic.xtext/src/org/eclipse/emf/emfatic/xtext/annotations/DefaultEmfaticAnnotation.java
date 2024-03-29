/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;

/**
 * The default implementation for {@link EmfaticAnnotation}. In this implementation all keys are
 * valid for all EClasses, that is, {@link #isValidKey(String, EClass)} always returns true.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class DefaultEmfaticAnnotation implements EmfaticAnnotation {

	private final String uri;
	private final String label;

	public DefaultEmfaticAnnotation(String label, String uri) {
		this.label = label;
		this.uri = uri;
	}

	@Override
	public String source() {
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

	@Override
	public List<String> keysFor(EClass eClass) {
		return Collections.emptyList();
	}

}
