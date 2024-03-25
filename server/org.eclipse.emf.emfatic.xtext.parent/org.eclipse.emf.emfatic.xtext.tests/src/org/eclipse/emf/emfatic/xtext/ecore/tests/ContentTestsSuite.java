package org.eclipse.emf.emfatic.xtext.ecore.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ContentForClassTest.class,
	ContentForDataTypeTest.class,
	ContentForFeaturesTest.class,
	ContentForMapEntryTest.class,
	ContentForOperationsTest.class,
	//StructureForPackageTest.class,
	//StrucutreForEnumTest.class
	})
public class ContentTestsSuite {

}
