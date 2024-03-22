package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
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
class StructureForFeaturesTest {

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
	def void classWithAttribute() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String b;
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(1, eClass.EAttributes.size)
	}
	
	@Test
	def void classWithAttributes() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String b;
				attr int c;
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(2, eClass.EAttributes.size)
	}
	
	@Test
	def void classWithAttributeWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				@"http://class/annotation"(k="v")
				attr String b;
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		val eAttribute = eClass.EAttributes.head
		assertEquals(1, eAttribute.EAnnotations.size)
	}
	
	@Test
	def void classWithAttributeMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String[*] b;
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(1, eClass.EAttributes.size)
	}
	
	@Test
	def void classWithAttributeDefault() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String b = "DefValue";
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(1, eClass.EAttributes.size)
	}

	@Test
	def void classWithRefernce() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B bs;
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(1, eClass.EReferences.size)
	}
	
	@Test
	def void classWithTwoRefernces() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B bs;
				val B cs;
			}
			class B {}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(2, eClass.EReferences.size)
	}
	
	@Test
	def void classWithRefernceWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				@"http://class/annotation"(k="v")
				val B bs;
			}
		''')
		process(result)
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		val eReferences = eClass.EReferences.head
		assertEquals(1, eReferences.EAnnotations.size)
	}
	
	@Test
	def void classWithRefernceMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B[1] bs;
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(1, eClass.EReferences.size)
	}
	
}
