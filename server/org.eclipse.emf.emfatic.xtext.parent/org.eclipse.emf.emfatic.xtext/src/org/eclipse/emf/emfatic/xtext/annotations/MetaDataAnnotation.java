package org.eclipse.emf.emfatic.xtext.annotations;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

import com.google.common.base.Objects;

public class MetaDataAnnotation implements EmfaticAnnotation {

	@Override
	public String URI() {
		return EXTENDED_METADATA_URI;
	}

	@Override
	public String label() {
		return EXTENDED_METADATA_LABEL;
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
		String namelc = name.toLowerCase();
		switch(namelc) {
		case "Namespace":
			return Objects.equal(target, EmfaticPackage.Literals.PACKAGE_DECL)
					|| Objects.equal(target, EmfaticPackage.Literals.CLASSIFIER_DECL)
					|| Objects.equal(target, EmfaticPackage.Literals.ATTRIBUTE)
				    || Objects.equal(target, EmfaticPackage.Literals.REFERENCE);
		case "Qualified":
			return Objects.equal(target, EmfaticPackage.Literals.PACKAGE_DECL);
		case "Name":
			return Objects.equal(target, EmfaticPackage.Literals.CLASSIFIER_DECL)
				|| Objects.equal(target, EmfaticPackage.Literals.ATTRIBUTE)
			    || Objects.equal(target, EmfaticPackage.Literals.REFERENCE);
		case "ContentKind":
			return Objects.equal(target, EmfaticPackage.Literals.CLASSIFIER_DECL);
		case "FeatureKind":
		case "Wildcards":
		case "ProcessingKind":
		case "Affiliation":
		case "Group":
			return Objects.equal(target, EmfaticPackage.Literals.ATTRIBUTE)
					|| Objects.equal(target, EmfaticPackage.Literals.REFERENCE);
		case "BaseType":
		case "ItemType":
		case "MemberTypes":
		case "WhiteSpaceFacet":
		case "EnumerationFacet":
		case "PatternFacet":
		case "TotalDigitsFacet":
		case "FractionDigitsFacet":
		case "LengthFacet":
		case "MinLengthFacet":
		case "MaxLengthFacet":
		case "MinExclusiveFacet":
		case "MaxExclusiveFacet":
		case "MinInclusiveFacet":
		case "MaxInclusiveFacet":
			return Objects.equal(target, EmfaticPackage.Literals.DATA_TYPE);
		}
		return false;
	}
	
	private static final String EXTENDED_METADATA_LABEL = "ExtendedMetaData";
	private static final String EXTENDED_METADATA_URI = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";

}
