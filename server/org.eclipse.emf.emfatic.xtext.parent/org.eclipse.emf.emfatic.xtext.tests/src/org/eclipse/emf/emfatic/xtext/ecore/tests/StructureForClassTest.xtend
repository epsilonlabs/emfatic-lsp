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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForClassTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Inject
	OnChangeEvictingCache cache

	@Inject
	EmfaticImport importer
	
	def Object process(EObject result) {
		val structure = new Structure(cache, importer)
		return structure.doSwitch(result)
	}
	
	@Test
	def void emptyClass() {
		val result = parseHelper.parse('''
			package test;
			class A {}
		''')
		val root = process(result) as EPackage
		assertEquals(1, root.EClassifiers.size)
	}
	
	@Test
	def void emptyClassWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			class A {}
		''')
		val root = process(result) as EPackage
		Assertions.assertEquals(1, root.EClassifiers.size)
		val eClass = root.EClassifiers.head
		assertEquals(1, eClass.EAnnotations.size)
	}
	
	@Test
	def void emptyClassWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			class A<B> {}
		''')
		val root = process(result) as EPackage
		Assertions.assertEquals(1, root.EClassifiers.size)
		val eClass = root.EClassifiers.head
		assertEquals(1, eClass.ETypeParameters.size)
	}
	
//	@Test
//	def void emptyClassWithSuper() {
//		val result = parseHelper.parse('''
//			package test;
//			class A extends B {}
//			class B {}
//		''')
//		val root = process(result) as EPackage
//		Assertions.assertEquals(1, root.EClassifiers.size)
//		val eClass = root.EClassifiers.head as EClass
//		assertEquals(1, eClass.ESuperTypes.size)
//	}
//	
//	@Test
//	def void emptyClassWithSuperWildcard() {
//		val result = parseHelper.parse('''
//			package test;
//			class A extends B<?> {}
//			class B {}
//		''')
//		process(result)
//		val output = cache.get(
//			(result.declarations.head.declaration as ClassDecl).superTypes.head.typeArgs.head,
//			result.eResource,
//			[null])
//		Assertions.assertNotNull(output)
//	}
//	
//	@Test
//	def void emptyClassWithSuperWildcardBound() {
//		val result = parseHelper.parse('''
//			package test;
//			class A extends B<? extends C> {}
//			class B{}
//			class C{}
//		''')
//		process(result)
//		val classDecl = result.declarations.head.declaration as ClassDecl
//		val wildcard = (classDecl).superTypes.head.typeArgs.head as Wildcard
//		val output = cache.get(
//			wildcard.bound,
//			result.eResource,
//			[null])
//		Assertions.assertNotNull(output)
//	}

}
