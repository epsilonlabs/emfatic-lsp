package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.util.OnChangeEvictingCache
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl
import org.eclipse.emf.emfatic.xtext.ecore.Structure
import org.eclipse.emf.emfatic.xtext.ecore.Content

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForDataTypeTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Inject
	OnChangeEvictingCache cache

	@Inject
	EmfaticImport importer
	
	def Object process(EObject result) {
		val structure = new Structure(cache, importer)
		structure.doSwitch(result)
		val content = new Content(this.cache)
		return content.doSwitch(result)
	}
	
	@Test
	def void dataType() {
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
		Assertions.assertInstanceOf(EDataType, output)
		Assertions.assertEquals("EInt", (output as EDataType).name)
		Assertions.assertEquals("int", (output as EDataType).instanceClassName)
	}
	/* 
	@Test
	def void dataTypeWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt<A> : int;
		''')
		process(result)
		val datatype = result.declarations.head.declaration as DataTypeDecl
		var output = cache.get(
			datatype.typeParamsInfo.tp.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
*/
}
