package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMemberDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl
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
class StructureForFeaturesTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Test
	def void classWithAttribute() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String b;
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EClass, cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val feature = (classDecl.members.get(0).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EAttribute, cache.get(feature))
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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		var feature = (classDecl.members.get(0).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EAttribute, cache.get(feature))
		feature = (classDecl.members.get(1).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EAttribute, cache.get(feature))
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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EClass, cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		var member = classDecl.members.get(0) as ClassMemberDecl
		assertInstanceOf(EAnnotation, cache.get(member.annotations.get(0)))
	}
	
	@Test
	def void classWithAttributeMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String[*] b;
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val feature = (classDecl.members.get(0).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EAttribute, cache.get(feature))
	}
	
	@Test
	def void classWithAttributeDefault() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String b = "DefValue";
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val feature = (classDecl.members.get(0).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EAttribute, cache.get(feature))
	}

	@Test
	def void classWithRefernce() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B bs;
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val feature = (classDecl.members.get(0).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EReference, cache.get(feature))
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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		var feature = (classDecl.members.get(0).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EReference, cache.get(feature))
		feature = (classDecl.members.get(1).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EReference, cache.get(feature))
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
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EClass, cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		var member = classDecl.members.get(0) as ClassMemberDecl
		assertInstanceOf(EAnnotation, cache.get(member.annotations.get(0)))
	}
	
	@Test
	def void classWithRefernceMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B[1] bs;
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val feature = (classDecl.members.get(0).member as FeatureDecl).feature
		assertNotNull(cache.get(feature))
		assertInstanceOf(EReference, cache.get(feature))
	}
	
}
