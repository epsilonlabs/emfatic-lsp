package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForMapEntryTest extends ContentTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void mapEntry() {
		val result = parseHelper.parse('''
			package test;
			mapentry EStringToStringMapEntry : String -> B;
			class B {}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output)
		var eClass = output as EClass
		Assertions.assertEquals("EStringToStringMapEntry", eClass.name)
		Assertions.assertEquals("java.util.Map$Entry", eClass.instanceClassName)
		Assertions.assertEquals(1, eClass.EAttributes.size)
		Assertions.assertEquals(1, eClass.EReferences.size)
		val keyAttr = eClass.EAttributes.head
		Assertions.assertEquals("key", keyAttr.name)
		Assertions.assertEquals("EString", keyAttr.EType.name)
		val valAttr = eClass.EReferences.head
		Assertions.assertEquals("value", valAttr.name)
		Assertions.assertEquals("B", valAttr.EType.name)
	}
}
