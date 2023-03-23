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

/**
 * This implementation provides Annotation support for Ecore annotations.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class EcoreAnnotation extends BaseAnnotation implements EmfaticAnnotation {
	
	@Override
	public String source() {
		return ECORE_URI;
	}

	@Override
	public String label() {
		return ECORE_LABEL;
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
		if (CONSTRAINTS_KEY.equals(name)) {
			return true;
		}
		return super.isValidKey(name, target);
	}
	
	protected void doCreateKeys() {
		addKey(new DetailsKey(CONSTRAINTS_KEY));
		EClass[] targets = new EClass[] {EmfaticPackage.Literals.PACKAGE_DECL};
		addKey(new DetailsKey(SETTING_DELEGATES_KEY,targets));
		addKey(new DetailsKey(INVOCATION_DELEGATES_KEY,targets));
		addKey(new DetailsKey(VALIDATION_DELEGATES_KEY,targets));
	}
	
	private static final String ECORE_LABEL = "Ecore";
	private static final String ECORE_URI = "http://www.eclipse.org/emf/2002/Ecore";

	private static final String CONSTRAINTS_KEY = "constraints";
	private static final String INVOCATION_DELEGATES_KEY = "invocationDelegates";
	private static final String VALIDATION_DELEGATES_KEY = "validationDelegates";
	private static final String SETTING_DELEGATES_KEY = "settingDelegates";

}
