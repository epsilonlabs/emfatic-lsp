package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForFeaturesTest extends ContentTest {

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
		val root = process(result) as EPackage
		val eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals("b", eAttribute.name)
		assertEquals("EString", eAttribute.EAttributeType.name)
		assertFalse(eAttribute.isMany)
		assertTrue(eAttribute.changeable)
		assertFalse(eAttribute.volatile)
		assertFalse(eAttribute.transient)
		assertFalse(eAttribute.unsettable)
		assertFalse(eAttribute.derived)
		assertFalse(eAttribute.unique)
		assertFalse(eAttribute.ordered)
		assertFalse(eAttribute.ID)
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
		val root = process(result) as EPackage
		val eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		val eAnnotation = eAttribute.EAnnotations.head 
		assertEquals("http://class/annotation", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("k"))
		assertEquals("v", eAnnotation.details.get("k"))
	}

	@Test
	def void classWithAttributeMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				attr String[*] b;
			}
		''')
		val root = process(result) as EPackage
		val eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertTrue(eAttribute.isMany)
	}
		
	@Test
	def void classWithStringAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr String b = "DefValue";
			}
		''')
		var root = process(result) as EPackage
		var eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals("DefValue", eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr String b;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertNull(eAttribute.defaultValue)
	}
	
	@Test
	def void classWithBooleanAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr boolean b = true;
			}
		''')
		var root = process(result) as EPackage
		var eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertTrue(eAttribute.defaultValue as Boolean)
		result = parseHelper.parse('''
			package test;
			class A {
				attr boolean b = false;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertFalse(eAttribute.defaultValue as Boolean)
		result = parseHelper.parse('''
			package test;
			class A {
				attr boolean b;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertFalse(eAttribute.defaultValue as Boolean)
	}
	
	@Test
	def void classWithIntegerAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr int b = 2;
			}
		''')
		var root = process(result) as EPackage
		var eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals(2, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr int b = -2;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals(-2, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr int b;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals(0, eAttribute.defaultValue)
	}
	
	@Test
	def void classWithFloatAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr float b = 2.0;
			}
		''')
		var root = process(result) as EPackage
		var eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals(2.0f, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr float b = -2.0;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals(-2.0f, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr float b;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		assertEquals(0.0f, eAttribute.defaultValue)
	}
	
	@Test
	def void classWithCharAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr char b = 'c';
			}
		''')
		var root = process(result) as EPackage
		var eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		var char expected = 'c'
		assertEquals(expected, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr char b;
			}
		''')
		root = process(result) as EPackage
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.head
		expected = '\u0000'	// null char
		assertEquals(expected, eAttribute.defaultValue)
	}
	
	@Test
	def void classWithAttributeModifiers() {
		val result = parseHelper.parse('''
			package test;
			class A {
				readonly attr String b;
				volatile attr String c;
				transient attr String d;
				unsettable attr String e;
				derived attr String f;
				unique attr String g;
				ordered attr String[*] h;
				id attr String i;
				volatile transient unsettable derived attr String j;
			}
		''')
		var root = process(result) as EPackage
		var eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(0)
		assertEquals("EString", eAttribute.EAttributeType.name)
		assertFalse(eAttribute.changeable)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(1)
		assertTrue(eAttribute.volatile)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(2)
		assertTrue(eAttribute.transient)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(3)
		assertTrue(eAttribute.unsettable)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(4)
		assertTrue(eAttribute.derived)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(5)
		assertTrue(eAttribute.unique)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(6)
		assertTrue(eAttribute.ordered)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(7)
		assertTrue(eAttribute.ID)
		eAttribute = (root.EClassifiers.head as EClass).EAttributes.get(8)
		assertTrue(eAttribute.volatile)
		assertTrue(eAttribute.transient)
		assertTrue(eAttribute.unsettable)
		assertTrue(eAttribute.derived)
	}
	
	@Test
	def void classWithRefernce() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B bs;
			}
			class B {}
		''')
		val root = process(result) as EPackage
		val eReference = (root.EClassifiers.head as EClass).EReferences.head
		assertEquals("bs", eReference.name)
		assertEquals("B", eReference.EReferenceType.name)
		assertFalse(eReference.isMany)
		assertTrue(eReference.changeable)
		assertFalse(eReference.volatile)
		assertFalse(eReference.transient)
		assertFalse(eReference.unsettable)
		assertFalse(eReference.derived)
		assertFalse(eReference.unique)
		assertFalse(eReference.ordered)
		assertFalse(eReference.resolveProxies)
	}
	
	@Test
	def void classWithRefernceWithTypeParamType() {
		val result = parseHelper.parse('''
			package test;
			class A<T> {
				val T bs;
			}
		''')
		val root = process(result) as EPackage
		val eReference = (root.EClassifiers.head as EClass).EReferences.head
		assertEquals("bs", eReference.name)
		assertEquals("T", eReference.EGenericType.ETypeParameter.name)
	}

	@Test
	def void classWithRefernceWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				@"http://class/annotation"(k="v")
				val B bs;
			}
			class B {}
		''')
		var root = process(result) as EPackage
		var eReference = (root.EClassifiers.head as EClass).EReferences.head
		val eAnnotation = eReference.EAnnotations.head 
		assertEquals("http://class/annotation", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("k"))
		assertEquals("v", eAnnotation.details.get("k"))
	}
		
	@Test
	def void classWithRefernceMulti() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B[*] bs;
			}
			class B {}
		''')
		var root = process(result) as EPackage
		var eReference = (root.EClassifiers.head as EClass).EReferences.head
		assertTrue(eReference.isMany)
	}
	
	@Test
	def void classWithRefernceMultiLimits() {
		val result = parseHelper.parse('''
			package test;
			class A {
				val B[2..4] bs;
			}
			class B {}
		''')
		var root = process(result) as EPackage
		var eReference = (root.EClassifiers.head as EClass).EReferences.head
		assertEquals(2, eReference.lowerBound)
		assertEquals(4, eReference.upperBound)
	}
	
	@Test
	def void classWithReferenceModifiers() {
		val result = parseHelper.parse('''
			package test;
			class A {
				readonly val B b;
				volatile val B c;
				transient val B d;
				unsettable val B e;
				derived val B f;
				unique val B g;
				ordered val B[*] h;
				resolve val B i;
				volatile transient unsettable derived val B j;
			}
			class B {}
		''')
		var root = process(result) as EPackage
		var eReference = (root.EClassifiers.head as EClass).EReferences.get(0)
		assertFalse(eReference.changeable)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(1).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.volatile)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(2).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.transient)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(3).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.unsettable)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(4).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.derived)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(5).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.unique)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(6).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.ordered)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(7).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.resolveProxies)
		eReference = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(8).member as FeatureDecl).feature,
			result.eResource,
			[null])
		assertTrue(eReference.volatile)
		assertTrue(eReference.transient)
		assertTrue(eReference.unsettable)
		assertTrue(eReference.derived)
	}
}
