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

public class MetaDataAnnotation extends BaseAnnotation implements EmfaticAnnotation {

	@Override
	public String source() {
		return EXTENDED_METADATA_URI;
	}

	@Override
	public String label() {
		return EXTENDED_METADATA_LABEL;
	}
	
	protected void createKeys() {
		if (this.keyMap.isEmpty()) {
			addKey(new DetailsKey("Namespace", new EClass[] {
					EmfaticPackage.Literals.PACKAGE_DECL,
					EmfaticPackage.Literals.CLASSIFIER_DECL,
					EmfaticPackage.Literals.ATTRIBUTE,
					EmfaticPackage.Literals.REFERENCE}));
			addKey(new DetailsKey("Qualified", new EClass[] {
					EmfaticPackage.Literals.PACKAGE_DECL}));
			addKey(new DetailsKey("Name", new EClass[] {
					EmfaticPackage.Literals.CLASSIFIER_DECL,
					EmfaticPackage.Literals.ATTRIBUTE,
					EmfaticPackage.Literals.REFERENCE
					}));
			addKey(new DetailsKey("ContentKind", new EClass[] {
					EmfaticPackage.Literals.CLASSIFIER_DECL}));
			EClass[] targets =  new EClass[] {
					EmfaticPackage.Literals.ATTRIBUTE,
					EmfaticPackage.Literals.REFERENCE};
			addKey(new DetailsKey("FeatureKind", targets));
			addKey(new DetailsKey("Wildcards", targets));
			addKey(new DetailsKey("ProcessingKind", targets));
			addKey(new DetailsKey("Affiliation", targets));
			addKey(new DetailsKey("Group", targets));
			targets =  new EClass[] {
					EmfaticPackage.Literals.DATA_TYPE};
			addKey(new DetailsKey("BaseType", targets));
			addKey(new DetailsKey("ItemType", targets));
			addKey(new DetailsKey("MemberTypes", targets));
			addKey(new DetailsKey("WhiteSpaceFacet", targets));
			addKey(new DetailsKey("EnumerationFacet", targets));
			addKey(new DetailsKey("PatternFacet", targets));
			addKey(new DetailsKey("TotalDigitsFacet", targets));
			addKey(new DetailsKey("FractionDigitsFacet", targets));
			addKey(new DetailsKey("LengthFacet", targets));
			addKey(new DetailsKey("MinLengthFacet", targets));
			addKey(new DetailsKey("MaxLengthFacet", targets));
			addKey(new DetailsKey("MinExclusiveFacet", targets));
			addKey(new DetailsKey("MaxExclusiveFacet", targets));
			addKey(new DetailsKey("MinInclusiveFacet", targets));
			addKey(new DetailsKey("MaxInclusiveFacet", targets));
		}
	}
	
	private static final String EXTENDED_METADATA_LABEL = "ExtendedMetaData";
	private static final String EXTENDED_METADATA_URI = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";

}
