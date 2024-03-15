package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForDataTypeTest extends ContentTest {

	@Inject
	ParseHelper<CompUnit> parseHelper
	
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
	
	@Test
	def void dataTypeWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt<T> : int;
		''')
		process(result)
		val output = cache.get(
			(result.declarations.head.declaration as DataTypeDecl).typeParamsInfo.tp.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(ETypeParameter, output);
		Assertions.assertEquals("T", (output as ETypeParameter).name);
	}

}
