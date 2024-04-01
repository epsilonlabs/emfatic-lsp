package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
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
import static org.junit.jupiter.api.Assertions.assertNull

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForPackageTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void empty() {
		val result = parseHelper.parse(''' ''')
		val cache = process(result)
		assertNull(cache.get(result))
	}

	@Test
	def void onlyPackage() {
		val result = parseHelper.parse('''
			package test;
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
	}
	
	@Test
	def void oneSubPackage() {
		val result = parseHelper.parse('''
			package test;
			package nested {
				
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EPackage, cache.get(result.declarations.get(0).declaration))
	}
	
		@Test
	def void subPackageWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			package nested {
				
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EPackage, cache.get(result.declarations.get(0).declaration))
		assertNotNull(cache.get(result.declarations.get(0).annotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(result.declarations.get(0).annotations.get(0)))
	}
	
}
