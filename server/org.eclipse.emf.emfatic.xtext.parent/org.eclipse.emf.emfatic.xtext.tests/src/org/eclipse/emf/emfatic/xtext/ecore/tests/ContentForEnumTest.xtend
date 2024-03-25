package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForEnumTest extends ContentTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void enumTypeEmpty() {
		val result = parseHelper.parse('''
			package test;
			enum E {
			}
		''')
		val root = process(result) as EPackage
		val eEnum = root.EClassifiers.head as EEnum
		assertEquals("E", eEnum.name)
	}
	
	@Test
	def void enumType() {
		val result = parseHelper.parse('''
			package test;
			enum E {
			  A=1;
			  B=2;
			  C=3;
			}
		''')
		val root = process(result) as EPackage
		val eEnum = root.EClassifiers.head as EEnum
		var eLit = eEnum.ELiterals.get(0)
		assertEquals("A", eLit.name)
		assertEquals(1, eLit.value)
		eLit = eEnum.ELiterals.get(1)
		assertEquals("B", eLit.name)
		assertEquals(2, eLit.value)
		eLit = eEnum.ELiterals.get(2)
		assertEquals("C", eLit.name)
		assertEquals(3, eLit.value)
	}
	
	@Test
	def void enumTypeWtihAnnotations() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			enum E {}
		''')
		val root = process(result) as EPackage
		val eEnum = root.EClassifiers.head
		val eAnnotation = eEnum.EAnnotations.head 
		assertEquals("http://class/annotation", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("k"))
		assertEquals("v", eAnnotation.details.get("k"))
	}
	
	@Test
	def void enumTypeWithLitearlAnnotations() {
		val result = parseHelper.parse('''
			package test;
			enum E { 
			  @"http://before"(k=v) 
			  A=1 @"http://after"(x=y); 
			}
		''')
		val root = process(result) as EPackage
		val eEnum = root.EClassifiers.head as EEnum
		var aLit = eEnum.ELiterals.head
		var eAnnotation = aLit.EAnnotations.head 
		assertEquals("http://before", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("k"))
		assertEquals("v", eAnnotation.details.get("k"))
		eAnnotation = aLit.EAnnotations.last 
		assertEquals("http://after", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("x"))
		assertEquals("y", eAnnotation.details.get("x"))
	}
	
	@Test
	def void eenumUnspecified() {
		val result = parseHelper.parse('''
			package test;
			enum E {
			  A;  // = 0 (if not specified, first literal has value 0)
			  B = 3;
			  C; // = 4 (in general, unspecified values are 1 greater than previous value)
			  D; // = 5
			}
		''')
		val root = process(result) as EPackage
		val eEnum = root.EClassifiers.head as EEnum
		var eLit = eEnum.ELiterals.get(0)
		assertEquals("A", eLit.name)
		assertEquals(0, eLit.value)
		eLit = eEnum.ELiterals.get(1)
		assertEquals("B", eLit.name)
		assertEquals(3, eLit.value)
		eLit = eEnum.ELiterals.get(2)
		assertEquals("C", eLit.name)
		assertEquals(4, eLit.value)
		eLit = eEnum.ELiterals.get(3)
		assertEquals("D", eLit.name)
		assertEquals(5, eLit.value)
	}

}
