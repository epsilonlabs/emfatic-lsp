package org.eclipse.emf.emfatic.xtext.ecore.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
//@SelectPackages("org.eclipse.emf.emfatic.xtext.ecore.tests")
@SelectClasses({
		StructureForClassTest.class,
		StructureForDataTypeTest.class,
		StructureForFeaturesTest.class,
		ContentForMapEntryTest.class,
		StructureForOperationsTest.class,
		StructureForPackageTest.class,
		StructureForEnumTest.class })
public class StructureTestsSuite {

}
