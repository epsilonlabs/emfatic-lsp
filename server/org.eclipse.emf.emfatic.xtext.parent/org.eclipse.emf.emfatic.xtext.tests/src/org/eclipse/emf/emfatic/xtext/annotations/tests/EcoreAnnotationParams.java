package org.eclipse.emf.emfatic.xtext.annotations.tests;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.eclipse.emf.emfatic.xtext.annotations.EcoreAnnotation;
import org.eclipse.emf.emfatic.xtext.annotations.EmfaticMapAnnotation;
import org.eclipse.emf.emfatic.xtext.annotations.GenModelAnnotation;
import org.eclipse.emf.emfatic.xtext.annotations.MetaDataAnnotation;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.junit.jupiter.params.provider.Arguments;

public class EcoreAnnotationParams {

	public static Stream<Arguments> params() {
		return Stream.of(
				ecoreConstraints(),
				ecoreDelegates("invocationDelegates"),
				ecoreDelegates("validationDelegates"),
				ecoreDelegates("settingDelegates"),
				emfaticMap(),
				genModelAll("documentation"),
				genModelAll("copyright"),
				genModelFeatures("get"),
				genModelFeatures("suppressedGetVisibility"),
				genModelFeatures("suppressedSetVisibility"),
				genModelFeatures("suppressedIsSetVisibility"),
				genModelFeatures("suppressedUnsetVisibility"),
				genModelDataTypes("create"),
				genModelDataTypes("convert"),
				metaDataNamespace(),
				metaDataQualified(),
				metaDataName(),
				metaDataContentKind(),
				metaDataFeatures("FeatureKind"),
				metaDataFeatures("Wildcards"),
				metaDataFeatures("ProcessingKind"),
				metaDataFeatures("Affiliation"),
				metaDataFeatures("Group"),
				metaDataDataType("BaseType"),
				metaDataDataType("ItemType"),
				metaDataDataType("MemberTypes"),
				metaDataDataType("WhiteSpaceFacet"),
				metaDataDataType("EnumerationFacet"),
				metaDataDataType("PatternFacet"),
				metaDataDataType("TotalDigitsFacet"),
				metaDataDataType("FractionDigitsFacet"),
				metaDataDataType("LengthFacet"),
				metaDataDataType("MinLengthFacet"),
				metaDataDataType("MaxLengthFacet"),
				metaDataDataType("MinExclusiveFacet"),
				metaDataDataType("MaxExclusiveFacet"),
				metaDataDataType("MinInclusiveFacet"),
				metaDataDataType("MaxInclusiveFacet")
			)
	      .reduce(Stream::concat)
	      .orElseGet(Stream::empty);
	}
	
	private static Stream<Arguments> ecoreConstraints() {
		return Stream.of(
		    arguments("Ecore", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  	new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.PACKAGE_DECL, true),
			arguments("Ecore", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(), 	new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.SUB_PACKAGE_DECL, true),
			arguments("Ecore", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.CLASS_DECL, true),
			arguments("Ecore", EmfaticPackage.Literals.DATA_TYPE.getName(), 		new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.DATA_TYPE_DECL, true),
			arguments("Ecore", EmfaticPackage.Literals.DETAILS.getName(), 			new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.DETAILS, true),
			arguments("Ecore", EmfaticPackage.Literals.ATTRIBUTE.getName(), 		new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.ATTRIBUTE, true),
			arguments("Ecore", EmfaticPackage.Literals.REFERENCE.getName(),			new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.REFERENCE, true),
			arguments("Ecore", EmfaticPackage.Literals.OPERATION.getName(), 		new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.OPERATION, true),
			arguments("Ecore", EmfaticPackage.Literals.PARAM.getName(), 			new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.PARAM, true),
			arguments("Ecore", EmfaticPackage.Literals.ENUM_DECL.getName(), 		new EcoreAnnotation(), "constraints", EmfaticPackage.Literals.ENUM_DECL, true)
			);
	}
	
	private static Stream<Arguments> ecoreDelegates(String key) {
		return Stream.of(
			arguments("Ecore", EmfaticPackage.Literals.PACKAGE_DECL.getName(),		new EcoreAnnotation(), key, EmfaticPackage.Literals.PACKAGE_DECL, true),
			arguments("Ecore", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(), 	new EcoreAnnotation(), key, EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
			arguments("Ecore", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new EcoreAnnotation(), key, EmfaticPackage.Literals.CLASS_DECL, false),
			arguments("Ecore", EmfaticPackage.Literals.DATA_TYPE.getName(), 		new EcoreAnnotation(), key, EmfaticPackage.Literals.DATA_TYPE_DECL, false),
			arguments("Ecore", EmfaticPackage.Literals.DETAILS.getName(), 			new EcoreAnnotation(), key, EmfaticPackage.Literals.DETAILS, false),
			arguments("Ecore", EmfaticPackage.Literals.ATTRIBUTE.getName(), 		new EcoreAnnotation(), key, EmfaticPackage.Literals.ATTRIBUTE, false),
			arguments("Ecore", EmfaticPackage.Literals.REFERENCE.getName(),			new EcoreAnnotation(), key, EmfaticPackage.Literals.REFERENCE, false),
			arguments("Ecore", EmfaticPackage.Literals.OPERATION.getName(), 		new EcoreAnnotation(), key, EmfaticPackage.Literals.OPERATION, false),
			arguments("Ecore", EmfaticPackage.Literals.PARAM.getName(), 			new EcoreAnnotation(), key, EmfaticPackage.Literals.PARAM, false),
			arguments("Ecore", EmfaticPackage.Literals.ENUM_DECL.getName(), 		new EcoreAnnotation(), key, EmfaticPackage.Literals.ENUM_DECL, false)
			);
	}
	
	private static Stream<Arguments> emfaticMap() {
		return Stream.of(
			    arguments("Emfatic", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  	new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.PACKAGE_DECL, true),
				arguments("Emfatic", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("Emfatic", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.CLASS_DECL, false),
				arguments("Emfatic", EmfaticPackage.Literals.DATA_TYPE.getName(), 		new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.DATA_TYPE_DECL, false),
				arguments("Emfatic", EmfaticPackage.Literals.DETAILS.getName(), 		new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.DETAILS, false),
				arguments("Emfatic", EmfaticPackage.Literals.ATTRIBUTE.getName(), 		new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.ATTRIBUTE, false),
				arguments("Emfatic", EmfaticPackage.Literals.REFERENCE.getName(),		new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.REFERENCE, false),
				arguments("Emfatic", EmfaticPackage.Literals.OPERATION.getName(), 		new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.OPERATION, false),
				arguments("Emfatic", EmfaticPackage.Literals.PARAM.getName(), 			new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.PARAM, false),
				arguments("Emfatic", EmfaticPackage.Literals.ENUM_DECL.getName(), 		new EmfaticMapAnnotation(), "any", EmfaticPackage.Literals.ENUM_DECL, false)
				);
	}
	
	private static Stream<Arguments> genModelAll(String key) {
		return Stream.of(
			    arguments("GenModel", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new GenModelAnnotation(), key, EmfaticPackage.Literals.PACKAGE_DECL, true),
				arguments("GenModel", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new GenModelAnnotation(), key, EmfaticPackage.Literals.SUB_PACKAGE_DECL, true),
				arguments("GenModel", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new GenModelAnnotation(), key, EmfaticPackage.Literals.CLASS_DECL, true),
				arguments("GenModel", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.DATA_TYPE_DECL, true),
				arguments("GenModel", EmfaticPackage.Literals.DETAILS.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.DETAILS, true),
				arguments("GenModel", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.ATTRIBUTE, true),
				arguments("GenModel", EmfaticPackage.Literals.REFERENCE.getName(),			new GenModelAnnotation(), key, EmfaticPackage.Literals.REFERENCE, true),
				arguments("GenModel", EmfaticPackage.Literals.OPERATION.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.OPERATION, true),
				arguments("GenModel", EmfaticPackage.Literals.PARAM.getName(), 				new GenModelAnnotation(), key, EmfaticPackage.Literals.PARAM, true),
				arguments("GenModel", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.ENUM_DECL, true)
				);
	}
	
	private static Stream<Arguments> genModelFeatures(String key) {
		return Stream.of(
			    arguments("GenModel", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new GenModelAnnotation(), key, EmfaticPackage.Literals.PACKAGE_DECL, false),
				arguments("GenModel", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new GenModelAnnotation(), key, EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("GenModel", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new GenModelAnnotation(), key, EmfaticPackage.Literals.CLASS_DECL, false),
				arguments("GenModel", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.DATA_TYPE_DECL, false),
				arguments("GenModel", EmfaticPackage.Literals.DETAILS.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.DETAILS, false),
				arguments("GenModel", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.ATTRIBUTE, true),
				arguments("GenModel", EmfaticPackage.Literals.REFERENCE.getName(),			new GenModelAnnotation(), key, EmfaticPackage.Literals.REFERENCE, true),
				arguments("GenModel", EmfaticPackage.Literals.OPERATION.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.OPERATION, false),
				arguments("GenModel", EmfaticPackage.Literals.PARAM.getName(), 				new GenModelAnnotation(), key, EmfaticPackage.Literals.PARAM, false),
				arguments("GenModel", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.ENUM_DECL, false)
				);
	}
	
	private static Stream<Arguments> genModelDataTypes(String key) {
		return Stream.of(
			    arguments("GenModel", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new GenModelAnnotation(), key, EmfaticPackage.Literals.PACKAGE_DECL, false),
				arguments("GenModel", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new GenModelAnnotation(), key, EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("GenModel", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new GenModelAnnotation(), key, EmfaticPackage.Literals.CLASS_DECL, false),
				arguments("GenModel", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.DATA_TYPE_DECL, true),
				arguments("GenModel", EmfaticPackage.Literals.DETAILS.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.DETAILS, false),
				arguments("GenModel", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.ATTRIBUTE, false),
				arguments("GenModel", EmfaticPackage.Literals.REFERENCE.getName(),			new GenModelAnnotation(), key, EmfaticPackage.Literals.REFERENCE, false),
				arguments("GenModel", EmfaticPackage.Literals.OPERATION.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.OPERATION, false),
				arguments("GenModel", EmfaticPackage.Literals.PARAM.getName(), 				new GenModelAnnotation(), key, EmfaticPackage.Literals.PARAM, false),
				arguments("GenModel", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new GenModelAnnotation(), key, EmfaticPackage.Literals.ENUM_DECL, false)
				);
	}
	
	private static Stream<Arguments> metaDataNamespace() {
		return Stream.of(
			    arguments("ExtendedMetaData", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.PACKAGE_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.CLASS_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.DATA_TYPE_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DETAILS.getName(), 			new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.DETAILS, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.ATTRIBUTE, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.REFERENCE.getName(),			new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.REFERENCE, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.OPERATION.getName(), 			new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.OPERATION, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.PARAM.getName(), 				new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.PARAM, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new MetaDataAnnotation(), "Namespace", EmfaticPackage.Literals.ENUM_DECL, true)
				);
	}
	
	private static Stream<Arguments> metaDataQualified() {
		return Stream.of(
			    arguments("ExtendedMetaData", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.PACKAGE_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.CLASS_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.DATA_TYPE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DETAILS.getName(), 			new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.DETAILS, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.ATTRIBUTE, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.REFERENCE.getName(),			new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.REFERENCE, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.OPERATION.getName(), 			new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.OPERATION, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.PARAM.getName(), 				new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.PARAM, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new MetaDataAnnotation(), "Qualified", EmfaticPackage.Literals.ENUM_DECL, false)
				);
	}
	
	private static Stream<Arguments> metaDataName() {
		return Stream.of(
			    arguments("ExtendedMetaData", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.CLASS_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.DATA_TYPE_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DETAILS.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.DETAILS, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.ATTRIBUTE, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.REFERENCE.getName(),			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.REFERENCE, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.OPERATION.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.OPERATION, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.PARAM.getName(), 				new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.PARAM, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.ENUM_DECL, true)
				);
	}
	
	private static Stream<Arguments> metaDataContentKind() {
		return Stream.of(
			    arguments("ExtendedMetaData", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.CLASS_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.DATA_TYPE_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DETAILS.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.DETAILS, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.ATTRIBUTE, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.REFERENCE.getName(),			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.REFERENCE, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.OPERATION.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.OPERATION, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.PARAM.getName(), 				new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.PARAM, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new MetaDataAnnotation(), "Name", EmfaticPackage.Literals.ENUM_DECL, true)
				);
	}
	
	private static Stream<Arguments> metaDataFeatures(String key) {
		return Stream.of(
			    arguments("ExtendedMetaData", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new MetaDataAnnotation(), key, EmfaticPackage.Literals.PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new MetaDataAnnotation(), key, EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new MetaDataAnnotation(), key, EmfaticPackage.Literals.CLASS_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.DATA_TYPE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DETAILS.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.DETAILS, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.ATTRIBUTE, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.REFERENCE.getName(),			new MetaDataAnnotation(), key, EmfaticPackage.Literals.REFERENCE, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.OPERATION.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.OPERATION, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.PARAM.getName(), 				new MetaDataAnnotation(), key, EmfaticPackage.Literals.PARAM, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.ENUM_DECL, true)
				);
	}
	
	private static Stream<Arguments> metaDataDataType(String key) {
		return Stream.of(
			    arguments("ExtendedMetaData", EmfaticPackage.Literals.PACKAGE_DECL.getName(),  		new MetaDataAnnotation(), key, EmfaticPackage.Literals.PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.SUB_PACKAGE_DECL.getName(),	new MetaDataAnnotation(), key, EmfaticPackage.Literals.SUB_PACKAGE_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.CLASS_DECL.getName(), 		new MetaDataAnnotation(), key, EmfaticPackage.Literals.CLASS_DECL, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DATA_TYPE.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.DATA_TYPE_DECL, true),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.DETAILS.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.DETAILS, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ATTRIBUTE.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.ATTRIBUTE, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.REFERENCE.getName(),			new MetaDataAnnotation(), key, EmfaticPackage.Literals.REFERENCE, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.OPERATION.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.OPERATION, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.PARAM.getName(), 				new MetaDataAnnotation(), key, EmfaticPackage.Literals.PARAM, false),
				arguments("ExtendedMetaData", EmfaticPackage.Literals.ENUM_DECL.getName(), 			new MetaDataAnnotation(), key, EmfaticPackage.Literals.ENUM_DECL, true)
				);
	}
	
	
	
	
	
	

}
