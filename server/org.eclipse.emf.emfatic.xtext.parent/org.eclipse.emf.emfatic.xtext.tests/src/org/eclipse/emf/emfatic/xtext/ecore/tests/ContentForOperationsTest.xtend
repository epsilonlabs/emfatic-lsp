package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForOperationsTest extends ContentTest {

	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Test
	def void classWithOperation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName();
			}
		''')
		val root = process(result) as EPackage
		val eOperation = (root.EClassifiers.head as EClass).EOperations.head
		assertEquals("getFullName", eOperation.name)
		assertEquals("EString", eOperation.EType.name)
		assertEquals(0, eOperation.EParameters.size)
	}
	
	@Test
	def void classWithVoidOperation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op void printFullName();
			}
		''')
		val root = process(result) as EPackage
		val eOperation = (root.EClassifiers.head as EClass).EOperations.head
		assertEquals("printFullName", eOperation.name)
		assertNull(eOperation.EType)
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
		val eOperation = (root.EClassifiers.head as EClass).EOperations.head
		var eAnnotation = eOperation.EAnnotations.head
		assertEquals("http://class/annotation", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("k"))
		assertEquals("v", eAnnotation.details.get("k"))
	}

	@Test
	def void classWithOperationWithGenericType() {
		val result = parseHelper.parse('''
			package test;
			class A<T> {
				op <T> String getFullName();
			}
		''')
		val root = process(result) as EPackage
		val eOperation = (root.EClassifiers.head as EClass).EOperations.head
		assertEquals("T", eOperation.ETypeParameters.head.name)
	}

	@Test
	def void classWithOperationWithParameter() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op void getFullName(String ~id);
			}
		''')
		val root = process(result) as EPackage
		val eOperation = (root.EClassifiers.head as EClass).EOperations.head
		assertEquals(1, eOperation.EParameters.size)
		assertEquals("EString", eOperation.EParameters.head.EType.name)
		assertEquals("id", eOperation.EParameters.head.name)
	}

	@Test
	def void classWithOperationWithParameterWithAnnotations() {
		val result = parseHelper.parse('''
			@EmfaticAnnotationMap(after="http://aftter.com")
			package test;
			class A {
				op String getFullName(@"before"(k=v) String ~id @after(x=y));
			}
		''')
		process(result)
		val root = process(result) as EPackage
		val eOperation = (root.EClassifiers.head as EClass).EOperations.head
		val param = eOperation.EParameters.head
		var eAnnotation = param.EAnnotations.head
		assertEquals("before", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("k"))
		assertEquals("v", eAnnotation.details.get("k"))
		eAnnotation = param.EAnnotations.last
		assertEquals("http://aftter.com", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("x"))
		assertEquals("y", eAnnotation.details.get("x"))
	}
	
	@Test
	def void classWithOperationWithThrows() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName() throws B;
			}
			class B {}
		''')
		val root = process(result) as EPackage
		val eOperation = (root.EClassifiers.head as EClass).EOperations.head
		assertEquals("B", eOperation.EGenericExceptions.head.EClassifier.name)
	}
	
}
