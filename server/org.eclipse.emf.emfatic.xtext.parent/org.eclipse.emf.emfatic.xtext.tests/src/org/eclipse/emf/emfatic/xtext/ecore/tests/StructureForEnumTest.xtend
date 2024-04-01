package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.EnumDecl
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertNotNull

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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EEnum, cache.get(result.declarations.get(0).declaration))
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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val enumDecl = result.declarations.get(0).declaration as EnumDecl
		assertNotNull(cache.get(enumDecl.enumLiterals.get(0)))
		assertInstanceOf(EEnumLiteral, cache.get(enumDecl.enumLiterals.get(0)))
		assertNotNull(cache.get(enumDecl.enumLiterals.get(1)))
		assertInstanceOf(EEnumLiteral, cache.get(enumDecl.enumLiterals.get(1)))
		assertNotNull(cache.get(enumDecl.enumLiterals.get(2)))
		assertInstanceOf(EEnumLiteral, cache.get(enumDecl.enumLiterals.get(2)))
	}
	
	@Test
	def void enumTypeWtihAnnotations() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			enum E {}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertNotNull(cache.get(result.declarations.get(0).annotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(result.declarations.get(0).annotations.get(0)))
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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val enumDecl = result.declarations.get(0).declaration as EnumDecl
		val enumLit = enumDecl.enumLiterals.get(0)
		assertNotNull(cache.get(enumLit.leadingAnnotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(enumLit.leadingAnnotations.get(0)))
		assertNotNull(cache.get(enumLit.trailingAnnotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(enumLit.trailingAnnotations.get(0)))
	}
}
