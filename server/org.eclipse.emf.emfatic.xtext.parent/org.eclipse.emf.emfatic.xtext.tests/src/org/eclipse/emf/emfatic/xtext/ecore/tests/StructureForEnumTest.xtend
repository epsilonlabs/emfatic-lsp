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

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForEnumTest extends StructureTest {

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
		assertEquals(1, root.EClassifiers.size)
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
		val eenum = root.EClassifiers.head as EEnum
		assertEquals(3, eenum.ELiterals.size)
	}
	
	@Test
	def void enumTypeWtihAnnotations() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			enum E {}
		''')
		val root = process(result) as EPackage
		val eenum = root.EClassifiers.head
		assertEquals(1, eenum.EAnnotations.size)
	}
	
	@Test
	def void enumTypeWithLitearlAnnotations() {
		val result = parseHelper.parse('''
			package test;
			enum E { 
			  @"http://before"(k=v) 
			  A=1 @"http://after"(k=v); 
			}
		''')
		val root = process(result) as EPackage
		val eenum = root.EClassifiers.head as EEnum
		val aLit = eenum.ELiterals.head
		assertEquals(2, aLit.EAnnotations.size)
	}
	
//	@Test
//	def void eenumUnspecified() {
//		val result = parseHelper.parse('''
//			package test;
//			enum E {
//			  A;  // = 0 (if not specified, first literal has value 0)
//			  B = 3;
//			  C; // = 4 (in general, unspecified values are 1 greater than previous value)
//			  D; // = 5
//			}
//		''')
//		val root = process(result) as EPackage
//		val eenum = root.EClassifiers.head
//		Assertions.assertNotNull(output)
//		Assertions.assertInstanceOf(EEnum, output)
//		Assertions.assertEquals(4, (output as EEnum).ELiterals.size)
//	}

}
