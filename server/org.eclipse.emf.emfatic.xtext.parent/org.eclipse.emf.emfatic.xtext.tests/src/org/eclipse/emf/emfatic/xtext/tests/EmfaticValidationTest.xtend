package org.eclipse.emf.emfatic.xtext.tests

import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.Test
import com.google.inject.Inject
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class EmfaticValidationTest {
	
	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Test
	def void mainPackageWithAnnotation() {
		val result = parseHelper.parse('''
			@namespace(uri="http://www.eclipse.org/emf/2002/Ecore", prefix="ecore")
			package ecore;
		''')
		Assertions.assertNotNull(result)
		val errors = result.eResource.errors
		Assertions.assertTrue(errors.isEmpty, '''Unexpected errors: «errors.join(", ")»''')
	}

}
