package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
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
class StructureForClassTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Test
	def void emptyClass() {
		val result = parseHelper.parse('''
			package test;
			class A {}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EClass, cache.get(result.declarations.get(0).declaration))
	}
	
	@Test
	def void emptyClassWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			class A {}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertNotNull(cache.get(result.declarations.get(0).annotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(result.declarations.get(0).annotations.get(0)))
	}
	
	@Test
	def void emptyClassWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			class A<B> {}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		assertNotNull(cache.get(classDecl.typeParamsInfo.tp.get(0)))
		assertInstanceOf(ETypeParameter, cache.get(classDecl.typeParamsInfo.tp.get(0)))
	}
	

}
