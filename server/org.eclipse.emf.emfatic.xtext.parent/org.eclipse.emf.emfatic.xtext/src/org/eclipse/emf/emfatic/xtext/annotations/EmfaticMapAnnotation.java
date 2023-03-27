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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

import com.google.common.base.Objects;

public class EmfaticMapAnnotation extends BaseAnnotation implements EmfaticAnnotation {

	@Override
	public String source() {
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
	
	@Override
	protected void doCreateKeys() {
	}
	
	private static final String EMFATIC_MAP_LABEL = "EmfaticAnnotationMap";
	private static final String EMFATIC_MAP_URI = "http://www.eclipse.org/emf/2004/EmfaticAnnotationMap";

}
