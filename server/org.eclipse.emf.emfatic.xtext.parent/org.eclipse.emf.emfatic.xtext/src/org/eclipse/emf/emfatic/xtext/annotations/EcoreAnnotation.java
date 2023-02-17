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
		String namelc = name.toLowerCase();
		switch(namelc) {
		case "constraints":
			return true;
		case "validationDelegates":
			return Objects.equal(target, EmfaticPackage.Literals.PACKAGE_DECL);
		}
		return false;
	}
	
	protected void createKeys() {
		if (this.keyMap.isEmpty()) {
			addKey(new DetailsKey("constraints"));
			EClass[] targets = new EClass[] {EmfaticPackage.Literals.PACKAGE_DECL};
			addKey(new DetailsKey("settingDelegates",targets));
			addKey(new DetailsKey("invocationDelegates",targets));
			addKey(new DetailsKey("validationDelegates",targets));
		}
	}
	
	private static final String ECORE_LABEL = "Ecore";
	private static final String ECORE_URI = "http://www.eclipse.org/emf/2002/Ecore";
	


}
