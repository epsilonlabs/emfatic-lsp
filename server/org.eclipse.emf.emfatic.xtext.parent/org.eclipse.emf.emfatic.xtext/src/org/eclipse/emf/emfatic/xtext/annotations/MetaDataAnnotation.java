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
 * Extended metadata EAnnotations are used in models that were created from XML Schema to capture 
 * details about the schema that have no other direct representation in Ecore.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class MetaDataAnnotation extends BaseAnnotation implements EmfaticAnnotation {

	@Override
	public String source() {
		return EXTENDED_METADATA_URI;
	}

	@Override
	public String label() {
		return EXTENDED_METADATA_LABEL;
	}
	
	protected void doCreateKeys() {
		addKey(new DetailsKey("namespace", new EClass[] {
				EmfaticPackage.Literals.PACKAGE_DECL,
				EmfaticPackage.Literals.CLASSIFIER_DECL,
				EmfaticPackage.Literals.FEATURE_DECL}));
		addKey(new DetailsKey("qualified", new EClass[] {
				EmfaticPackage.Literals.PACKAGE_DECL}));
		addKey(new DetailsKey("name", new EClass[] {
				EmfaticPackage.Literals.CLASSIFIER_DECL,
				EmfaticPackage.Literals.FEATURE_DECL
				}));
		addKey(new DetailsKey("contentKind", new EClass[] {
				EmfaticPackage.Literals.CLASSIFIER_DECL}));
		EClass[] targets =  new EClass[] {EmfaticPackage.Literals.FEATURE_DECL};
		addKey(new DetailsKey("featureKind", targets));
		addKey(new DetailsKey("wildcards", targets));
		addKey(new DetailsKey("processingKind", targets));
		addKey(new DetailsKey("affiliation", targets));
		addKey(new DetailsKey("group", targets));
		targets =  new EClass[] {
				EmfaticPackage.Literals.DATA_TYPE};
		addKey(new DetailsKey("baseType", targets));
		addKey(new DetailsKey("itemType", targets));
		addKey(new DetailsKey("memberTypes", targets));
		addKey(new DetailsKey("whiteSpaceFacet", targets));
		addKey(new DetailsKey("enumerationFacet", targets));
		addKey(new DetailsKey("patternFacet", targets));
		addKey(new DetailsKey("totalDigitsFacet", targets));
		addKey(new DetailsKey("fractionDigitsFacet", targets));
		addKey(new DetailsKey("lengthFacet", targets));
		addKey(new DetailsKey("minLengthFacet", targets));
		addKey(new DetailsKey("maxLengthFacet", targets));
		addKey(new DetailsKey("minExclusiveFacet", targets));
		addKey(new DetailsKey("maxExclusiveFacet", targets));
		addKey(new DetailsKey("minInclusiveFacet", targets));
		addKey(new DetailsKey("maxInclusiveFacet", targets));
	}
	
	private static final String EXTENDED_METADATA_LABEL = "ExtendedMetaData";
	private static final String EXTENDED_METADATA_URI = "http://org/eclipse/emf/ecore/util/ExtendedMetaData";

}
