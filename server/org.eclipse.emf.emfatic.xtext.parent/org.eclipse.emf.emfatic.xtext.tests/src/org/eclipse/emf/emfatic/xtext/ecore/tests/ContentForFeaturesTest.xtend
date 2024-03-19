package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EReference

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
		process(result)
		var output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output)
		var eAttribute = output as EAttribute
		Assertions.assertEquals("b", eAttribute.name)
		Assertions.assertEquals("EString", eAttribute.EAttributeType.name)
		Assertions.assertFalse(eAttribute.isMany)
		Assertions.assertTrue(eAttribute.changeable)
		Assertions.assertFalse(eAttribute.volatile)
		Assertions.assertFalse(eAttribute.transient)
		Assertions.assertFalse(eAttribute.unsettable)
		Assertions.assertFalse(eAttribute.derived)
		Assertions.assertFalse(eAttribute.unique)
		Assertions.assertFalse(eAttribute.ordered)
		Assertions.assertFalse(eAttribute.ID)
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
		Assertions.assertInstanceOf(EAnnotation, output);
		Assertions.assertEquals("http://class/annotation", (output as EAnnotation).source)
		Assertions.assertEquals(1, (output as EAnnotation).details.size)
		Assertions.assertTrue((output as EAnnotation).details.containsKey("k"))
		Assertions.assertEquals("v", (output as EAnnotation).details.get("k"))
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
		var output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output)
		var eAttribute = output as EAttribute
		Assertions.assertTrue(eAttribute.isMany)
	}
		
	@Test
	def void classWithStringAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr String b = "DefValue";
			}
		''')
		process(result)
		var output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output);
		var eAttribute = output as EAttribute;
		Assertions.assertEquals("DefValue", eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr String b;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute
		Assertions.assertNull(eAttribute.defaultValue)
	}
	
	@Test
	def void classWithBooleanAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr boolean b = true;
			}
		''')
		process(result)
		var output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output);
		var eAttribute = output as EAttribute;
		Assertions.assertTrue(eAttribute.defaultValue as Boolean)
		result = parseHelper.parse('''
			package test;
			class A {
				attr boolean b = false;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute;
		Assertions.assertFalse(eAttribute.defaultValue as Boolean)
		result = parseHelper.parse('''
			package test;
			class A {
				attr boolean b;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute
		Assertions.assertFalse(eAttribute.defaultValue as Boolean)
	}
	
	@Test
	def void classWithIntegerAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr int b = 2;
			}
		''')
		process(result)
		var output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output);
		var eAttribute = output as EAttribute;
		Assertions.assertEquals(2, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr int b = -2;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute;
		Assertions.assertEquals(-2, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr int b;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute
		Assertions.assertEquals(0, eAttribute.defaultValue)
	}
	
	@Test
	def void classWithFloatAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr float b = 2.0;
			}
		''')
		process(result)
		var output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output);
		var eAttribute = output as EAttribute;
		Assertions.assertEquals(2.0f, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr float b = -2.0;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute;
		Assertions.assertEquals(-2.0f, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr float b;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute
		Assertions.assertEquals(0.0f, eAttribute.defaultValue)
	}
	
	@Test
	def void classWithCharAttributeDefault() {
		var result = parseHelper.parse('''
			package test;
			class A {
				attr char b = 'c';
			}
		''')
		process(result)
		var output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EAttribute, output);
		var eAttribute = output as EAttribute;
		var char expected = 'c'
		Assertions.assertEquals(expected, eAttribute.defaultValue)
		result = parseHelper.parse('''
			package test;
			class A {
				attr char b;
			}
		''')
		process(result)
		output = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.head.member as FeatureDecl).feature,
			result.eResource,
			[null])
		eAttribute = output as EAttribute
		expected = '\u0000'
		Assertions.assertEquals(expected, eAttribute.defaultValue)
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
		process(result)
		var eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(0).member as FeatureDecl).feature,
			result.eResource,
			[null]) as EAttribute
		Assertions.assertEquals("EString", eAttribute.EAttributeType.name)
		Assertions.assertFalse(eAttribute.changeable)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(1).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.volatile)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(2).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.transient)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(3).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.unsettable)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(4).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.derived)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(5).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.unique)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(6).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.ordered)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(7).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.ID)
		eAttribute = cache.get(
			((result.declarations.head.declaration as ClassDecl).members.get(8).member as FeatureDecl).feature,
			result.eResource,
			[null])
		Assertions.assertTrue(eAttribute.volatile)
		Assertions.assertTrue(eAttribute.transient)
		Assertions.assertTrue(eAttribute.unsettable)
		Assertions.assertTrue(eAttribute.derived)
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
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val attribute = (classDecl).members.head.member as FeatureDecl
		var output = cache.get(
			attribute.feature,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EReference, output);
		val eReference = output as EReference
		Assertions.assertEquals("b", eReference.name)
		Assertions.assertEquals("B", eReference.EReferenceType.name)
		Assertions.assertFalse(eReference.isMany)
		Assertions.assertTrue(eReference.changeable)
		Assertions.assertFalse(eReference.volatile)
		Assertions.assertFalse(eReference.transient)
		Assertions.assertFalse(eReference.unsettable)
		Assertions.assertFalse(eReference.derived)
		Assertions.assertFalse(eReference.unique)
		Assertions.assertFalse(eReference.ordered)
		Assertions.assertFalse(eReference.resolveProxies)
	}
	/*	
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
	 */
}
