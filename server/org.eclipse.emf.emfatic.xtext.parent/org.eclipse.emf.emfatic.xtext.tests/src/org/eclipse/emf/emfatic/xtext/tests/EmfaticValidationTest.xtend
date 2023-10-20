package org.eclipse.emf.emfatic.xtext.tests

import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.Test
import com.google.inject.Inject
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class EmfaticValidationTest {
	
	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Inject ValidationTestHelper validationTestHelper
	
	@Test
	def void namespace_is_valid_annotation() {
		val result = parseHelper.parse('''
			@namespace(uri="http://www.eclipse.org/emf/2002/Ecore", prefix="ecore")
			package ecore;
		''')
		validationTestHelper.assertNoIssues(result)
	}
	
	@Test
	def void ecore_is_valid_annotation() {
		var result = parseHelper.parse('''
			@ecore(settingDelegates="http://www.eclipse.org/emf/2002/Ecore/OCL")
			package db;
		''')
		validationTestHelper.assertNoIssues(result)
		result = parseHelper.parse('''
			@"http://www.eclipse.org/emf/2002/Ecore"(settingDelegates="http://www.eclipse.org/emf/2002/Ecore/OCL")
			package db;
		''')
		validationTestHelper.assertWarning(result, EmfaticPackage.Literals.PACKAGE_DECL, "23")
	}

}
