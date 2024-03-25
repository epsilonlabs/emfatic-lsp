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
		val root = process(result) as EPackage
		assertEquals(1, root.EClassifiers.size)
	}
}
