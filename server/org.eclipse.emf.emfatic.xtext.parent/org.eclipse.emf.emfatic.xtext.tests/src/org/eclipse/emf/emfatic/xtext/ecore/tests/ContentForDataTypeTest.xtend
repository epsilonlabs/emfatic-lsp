package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals

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
		val root = process(result) as EPackage
		val eDataType = root.EClassifiers.head
		assertEquals("EInt", eDataType.name)
		assertEquals("int", eDataType.instanceClassName)
	}
	
	@Test
	def void dataTypeWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt<T> : int;
		''')
		val root = process(result) as EPackage
		val eDataType = root.EClassifiers.head
		Assertions.assertEquals("T", eDataType.ETypeParameters.head.name);
	}

}
