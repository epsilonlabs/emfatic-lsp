package org.eclipse.emf.emfatic.xtext.annotations.tests;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.annotations.EcoreAnnotation;
import org.eclipse.emf.emfatic.xtext.annotations.EmfaticAnnotation;
import org.eclipse.emf.emfatic.xtext.annotations.EmfaticMapAnnotation;
import org.eclipse.emf.emfatic.xtext.annotations.GenModelAnnotation;
import org.eclipse.emf.emfatic.xtext.annotations.MetaDataAnnotation;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class EmfaticMapAnnotationTest {
	
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("sources")
	public void hasCorrectSource(String annotation, EmfaticAnnotation underTest, String source) {
		Assertions.assertEquals(source, underTest.source());
	}
	
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("labels")
	public void hasCorrectLabel(String annotation, EmfaticAnnotation underTest, String label) {
		Assertions.assertEquals(label, underTest.label());
	}
	
	@ParameterizedTest(name = "[{index}] {0} - {3} => {1}")
	@MethodSource("org.eclipse.emf.emfatic.xtext.annotations.tests.EcoreAnnotationParams#params")
	public void isValidKey(String annotation, String targetName, EmfaticAnnotation underTest, String key, EClass target, boolean valid) {
		Assertions.assertEquals(valid, underTest.isValidKey(key, target));
	}

	private static Stream<Arguments> sources() {
	    return Stream.of(
	    		arguments("Ecore", new EcoreAnnotation(), "http://www.eclipse.org/emf/2002/Ecore"),
	    		arguments("Emfatic", new EmfaticMapAnnotation(), "http://www.eclipse.org/emf/2004/EmfaticAnnotationMap"),
	    		arguments("GenModel", new GenModelAnnotation(), "http://www.eclipse.org/emf/2002/GenModel"),
	    		arguments("MetaData", new MetaDataAnnotation(), "http:///org/eclipse/emf/ecore/util/ExtendedMetaData")
	    		
	    );
	}
	
	private static Stream<Arguments> labels() {
	    return Stream.of(
	    		arguments("Ecore", new EcoreAnnotation(), "Ecore"),
	    		arguments("Emfatic", new EmfaticMapAnnotation(), "EmfaticAnnotationMap"),
	    		arguments("GenModel", new GenModelAnnotation(), "GenModel"),
	    		arguments("MetaData", new MetaDataAnnotation(), "ExtendedMetaData")
	    		
	    );
	}
	
	
	
	private static EClass[] annotatableElements = new EClass[] {
			EmfaticPackage.Literals.PACKAGE_DECL,
			EmfaticPackage.Literals.SUB_PACKAGE_DECL,
			EmfaticPackage.Literals.CLASS_DECL,
			EmfaticPackage.Literals.DATA_TYPE,
			EmfaticPackage.Literals.DETAILS,
			EmfaticPackage.Literals.ATTRIBUTE,
			EmfaticPackage.Literals.REFERENCE,
			EmfaticPackage.Literals.OPERATION,
			EmfaticPackage.Literals.PARAM,
			EmfaticPackage.Literals.ENUM_LITERAL
	};
}
