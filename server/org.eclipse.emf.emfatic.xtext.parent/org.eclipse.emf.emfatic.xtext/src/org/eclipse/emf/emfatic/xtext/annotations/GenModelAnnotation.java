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
 * Provides the valid GenModel annotations.
 * 
 * Taken from <a href="https://dentrassi.de/my-projects/emf-genmodel-annotations/#sample_gen_pattern_1">EMF GenModel Annotations<a>
 * @author Horacio Hoyos Rodriguez
 *
 */
public class GenModelAnnotation extends BaseAnnotation implements EmfaticAnnotation {

	@Override
	public String source() {
		return GEN_MODEL_URI;
	}

	@Override
	public String label() {
		return GEN_MODEL_LABEL;
	}

	private static final String GEN_MODEL_LABEL = "GenModel";
	private static final String GEN_MODEL_URI = "http://www.eclipse.org/emf/2002/GenModel";
	
	protected void createKeys() {
		if (this.keyMap.isEmpty()) {
			addKey(new DetailsKey("documentation"));
			addKey(new DetailsKey("copyright"));
			EClass[] targets = new EClass[] {
					EmfaticPackage.Literals.ATTRIBUTE, 
					EmfaticPackage.Literals.REFERENCE};
			addKey(new DetailsKey("get", targets));
			addKey(new DetailsKey("suppressedGetVisibility", targets));
			addKey(new DetailsKey("suppressedSetVisibility", targets));
			addKey(new DetailsKey("suppressedIsSetVisibility", targets));
			addKey(new DetailsKey("suppressedUnsetVisibility", targets));
			targets = new EClass[] {EmfaticPackage.Literals.DATA_TYPE};
			addKey(new DetailsKey("create", targets));
			addKey(new DetailsKey("convert", targets));
		}
	}

}
