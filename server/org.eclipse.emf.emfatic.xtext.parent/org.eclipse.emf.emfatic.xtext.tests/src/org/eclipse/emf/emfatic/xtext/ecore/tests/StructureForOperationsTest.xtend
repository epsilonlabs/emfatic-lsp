package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EParameter
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.Operation
import org.eclipse.emf.emfatic.xtext.emfatic.Param
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.util.OnChangeEvictingCache
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForOperationsTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Inject
	OnChangeEvictingCache cache


	@Test
	def void classWithOperation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName();
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(1, eClass.EOperations.size)
		val eOperation = eClass.EOperations.head
		assertEquals(0, eOperation.EParameters.size)
		assertEquals(0, eOperation.ETypeParameters.size)
	}
	
	@Test
	def void classWithOperationWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				@"http://class/annotation"(k="v")
				op String getFullName();
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		val eOperation = eClass.EOperations.head
		assertEquals(1, eOperation.EAnnotations.size)
	}
	
	@Test
	def void classWithOperationWithGenericType() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op <T> String getFullName();
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		val eOperation = eClass.EOperations.head
		assertEquals(0, eOperation.EParameters.size)
		assertEquals(1, eOperation.ETypeParameters.size)
	}
	
	@Test
	def void classWithOperationWithParameter() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(String ~id);
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		val eOperation = eClass.EOperations.head
		assertEquals(1, eOperation.EParameters.size)
		assertEquals(0, eOperation.ETypeParameters.size)
	}
	
	@Test
	def void classWithOperationWithParameters() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(String ~id, B school);
			}
			class B {}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		val eOperation = eClass.EOperations.head
		assertEquals(2, eOperation.EParameters.size)
		assertEquals(0, eOperation.ETypeParameters.size)
	}
	
	@Test
	def void classWithOperationWithParameterWithAnnotations() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(@before(k=v) String ~id @after(k=v));
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		val eOperation = eClass.EOperations.head
		val eParam = eOperation.EParameters.head
		assertEquals(2, eParam.EAnnotations.size)
	}
	
	@Test
	def void classWithOperationWithThrows() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName() throws NullPointerException;
			}
		''')
		val root = process(result) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals(1, eClass.EOperations.size)
	}
}
