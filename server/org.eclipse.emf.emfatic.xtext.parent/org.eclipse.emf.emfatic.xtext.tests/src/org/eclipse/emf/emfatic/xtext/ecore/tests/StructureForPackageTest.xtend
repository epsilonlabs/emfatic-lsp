package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.ecore.Structure
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
class StructureForPackageTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void empty() {
		val result = parseHelper.parse(''' ''')
		val creator = new Structure(cache, importer)
		val created = creator.doSwitch(result)
		Assertions.assertNull(created)
	}

	@Test
	def void onlyPackage() {
		val result = parseHelper.parse('''
			package test;
		''')
		val output = process(result)
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EPackage, output);
	}
	
	@Test
	def void oneSubPackage() {
		val result = parseHelper.parse('''
			package test;
			package nested {
				
			}
		''')
		process(result)
		val output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EPackage, output);
		Assertions.assertEquals(1, (cache.get(result.package, result.eResource, [null]) as EPackage).ESubpackages.size)
		Assertions.assertEquals(output, (cache.get(result.package, result.eResource, [null]) as EPackage).ESubpackages.head)
	}

	
	@Test
	def void oneMapEntry() {
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
		var key = cache.get(
			(result.declarations.head.declaration as MapEntryDecl).key,
			result.eResource,
			[null])
		Assertions.assertNotNull(key)
		var value = cache.get(
			(result.declarations.head.declaration as MapEntryDecl).value,
			result.eResource,
			[null])
		Assertions.assertNotNull(value)
		Assertions.assertEquals(1, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.size)
		Assertions.assertEquals(output, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.head)
	}
	
	@Test
	def void oneClass() {
		val result = parseHelper.parse('''
			package test;
			class A {}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		Assertions.assertEquals(1, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.size)
		Assertions.assertEquals(output, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.head)
		
	}
	
	@Test
	def void oneDataType() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt : int;
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EDataType, output);
		Assertions.assertEquals(1, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.size)
		Assertions.assertEquals(output, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.head)
	}
	
	@Test
	def void oneEnumType() {
		val result = parseHelper.parse('''
			package test;
			enum E {
			  A=1;
			  B=2;
			  C=3;
			}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EEnum, output);
		Assertions.assertEquals(1, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.size)
		Assertions.assertEquals(output, (cache.get(result.package, result.eResource, [null]) as EPackage).EClassifiers.head)
	}
	
}
