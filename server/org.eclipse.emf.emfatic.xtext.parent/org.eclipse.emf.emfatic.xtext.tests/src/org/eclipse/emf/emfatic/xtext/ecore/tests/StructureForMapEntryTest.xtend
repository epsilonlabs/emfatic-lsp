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

import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertNotNull
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.emfatic.xtext.emfatic.MapEntryDecl

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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EClass, cache.get(result.declarations.get(0).declaration))
		var mapEntry = result.declarations.get(0).declaration as MapEntryDecl
		assertNotNull(cache.get(mapEntry.key))
		assertNotNull(cache.get(mapEntry.value))
	}
}
