package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals
import org.eclipse.emf.ecore.EClass

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
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals("EStringToStringMapEntry", eClass.name)
		assertEquals("java.util.Map$Entry", eClass.instanceClassName)
		assertEquals(1, eClass.EAttributes.size)
		assertEquals(1, eClass.EReferences.size)
		val keyAttr = eClass.EAttributes.head
		assertEquals("key", keyAttr.name)
		assertEquals("EString", keyAttr.EType.name)
		val valAttr = eClass.EReferences.head
		assertEquals("value", valAttr.name)
		assertEquals("B", valAttr.EType.name)
	}
}
