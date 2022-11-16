/*
 * generated by Xtext 2.25.0
 */
package org.eclipse.emfatic.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class EmfaticParsingTest {
	@Inject
	ParseHelper<EPackage> parseHelper
	
	@Test
	def void loadModel() {
		val result = parseHelper.parse('''
			EPackage test {}
		''')
		Assertions.assertNotNull(result)
		val errors = result.eResource.errors
		Assertions.assertTrue(errors.isEmpty, '''Unexpected errors: «errors.join(", ")»''')
	}
}
