package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.emfatic.xtext.ecore.Structure
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl
import org.eclipse.emf.emfatic.xtext.emfatic.Reference
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.util.OnChangeEvictingCache
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

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
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val attribute = (classDecl).members.head.member as FeatureDecl
		var output = cache.get(
			attribute.feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output);
		output = cache.get(
			(attribute.feature as Attribute).typeWithMulti,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
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
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val annt = (classDecl).members.head.annotations.head as Annotation
		val output = cache.get(
			annt,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void classWithAttributeMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String[*] b;
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val attribute = (classDecl).members.head.member as FeatureDecl
		val output = cache.get(
			(attribute.feature as Attribute).typeWithMulti.multiplicity,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void classWithAttributeDefault() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String b = "DefValue";
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val attribute = (classDecl).members.head.member as FeatureDecl
		val output = cache.get(
			(attribute.feature as Attribute).defValue,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}

	@Test
	def void classWithRefernce() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B bs;
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val attribute = (classDecl).members.head.member as FeatureDecl
		var output = cache.get(
			attribute.feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EReference, output);
		output = cache.get(
			(attribute.feature as Reference).typeWithMulti,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			(attribute.feature as Reference).typeWithMulti.type,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
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
		val classDecl = result.declarations.head.declaration as ClassDecl
		val annt = classDecl.members.head.annotations.head as Annotation
		val output = cache.get(
			annt,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void classWithRefernceMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B[1] bs;
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val attribute = (classDecl).members.head.member as FeatureDecl
		val output = cache.get(
			(attribute.feature as Reference).typeWithMulti.multiplicity,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
}
