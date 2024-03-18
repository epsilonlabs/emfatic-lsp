package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.MapEntryDecl
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForMapEntryTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void mapEntry() {
		val result = parseHelper.parse('''
			package test;
			mapentry EStringToStringMapEntry : String -> String;
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		Assertions.assertNotNull(cache.get(
			(result.declarations.head.declaration as MapEntryDecl).key,
			result.eResource,
			[null]))
		Assertions.assertNotNull(cache.get(
			(result.declarations.head.declaration as MapEntryDecl).value,
			result.eResource,
			[null]))
		Assertions.assertEquals(
			cache.get(
				result.package,
				result.eResource,
				[null]),
			(output as EClass).eContainer)
	}
}
