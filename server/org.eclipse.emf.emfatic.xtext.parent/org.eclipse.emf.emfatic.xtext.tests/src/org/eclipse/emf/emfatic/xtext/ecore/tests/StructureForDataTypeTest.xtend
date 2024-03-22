package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.ecore.Structure
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.util.OnChangeEvictingCache
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForDataTypeTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Inject
	OnChangeEvictingCache cache

	@Inject
	EmfaticImport importer
	
	def Object process(EObject result) {
		val creator = new Structure(cache, importer)
		return creator.doSwitch(result)
	}
	
	@Test
	def void dataType() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt : int;
		''')
		val root = process(result) as EPackage
		assertEquals(1, root.EClassifiers.size)
	}
	
	@Test
	def void dataTypeWithAnnotations() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			datatype EInt : int;
		''')
		val root = process(result) as EPackage
		val eDataType = root.EClassifiers.head
		assertEquals(1, eDataType.EAnnotations.size)
	}
	
	@Test
	def void dataTypeWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt<A> : int;
		''')
		val root = process(result) as EPackage
		val eDataType = root.EClassifiers.head
		assertEquals(1, eDataType.ETypeParameters.size)
	}

}
