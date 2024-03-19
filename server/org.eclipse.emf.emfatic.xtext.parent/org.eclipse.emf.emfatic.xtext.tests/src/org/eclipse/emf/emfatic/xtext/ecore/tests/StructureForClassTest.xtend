package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.util.OnChangeEvictingCache
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.emf.emfatic.xtext.ecore.Structure

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
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		
	}
	
	@Test
	def void emptyClassWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			class A {}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.annotations.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAnnotation, output);
	}
	
	@Test
	def void emptyClassWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			class A<B> {}
		''')
		process(result)
		val output = cache.get(
			(result.declarations.head.declaration as ClassDecl).typeParamsInfo.tp.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(ETypeParameter, output);
	}
	
	@Test
	def void emptyClassWithGenericsBound() {
		val result = parseHelper.parse('''
			package test;
			class A<B extends C> {}
		''')
		process(result)
		val output = cache.get(
			(result.declarations.head.declaration as ClassDecl).typeParamsInfo.tp.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(ETypeParameter, output);
	}
	
	@Test
	def void emptyClassWithSuper() {
		val result = parseHelper.parse('''
			package test;
			class A extends B {}
		''')
		process(result)
		val output = cache.get(
			(result.declarations.head.declaration as ClassDecl).superTypes.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void emptyClassWithSuperWildcard() {
		val result = parseHelper.parse('''
			package test;
			class A extends B<?> {}
		''')
		process(result)
		val output = cache.get(
			(result.declarations.head.declaration as ClassDecl).superTypes.head.typeArgs.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void emptyClassWithSuperWildcardBound() {
		val result = parseHelper.parse('''
			package test;
			class A extends B<? extends C> {}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val wildcard = (classDecl).superTypes.head.typeArgs.head as Wildcard
		val output = cache.get(
			wildcard.bound,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}

}
