package org.eclipse.emf.emfatic.xtext.ecore.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ContentForClassTest.class,
	ContentForDataTypeTest.class,
	ContentForEnumTest.class,
	ContentForFeaturesTest.class,
	ContentForMapEntryTest.class,
	ContentForOperationsTest.class,
	ContentForPackageTest.class,
	})
public class ContentTestsSuite {

}
