package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue
import org.eclipse.emf.ecore.EGenericType

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForClassTest extends ContentTest {

	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Test
	def void emptyClass() {
		val result = parseHelper.parse('''
			package test;
			class A {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eClass = root.EClassifiers.head
		assertEquals("A", (eClass as EClass).name);
	}
	
	@Test
	def void emptyClassWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			class A {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eAnnotation = root.EClassifiers.head.EAnnotations.head
		assertEquals("http://class/annotation", eAnnotation.source)
		assertEquals(1, eAnnotation.details.size)
		assertTrue(eAnnotation.details.containsKey("k"))
		assertEquals("v", eAnnotation.details.get("k"))
	}
	
	@Test
	def void abstractClass() {
		val result = parseHelper.parse('''
			package test;
			abstract class A {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertTrue(eClass.abstract);	
	}
	
	@Test
	def void interfaceClass() {
		val result = parseHelper.parse('''
			package test;
			interface A {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertTrue(eClass.interface);	
	}
	
	@Test
	def void emptyClassWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			class A<T> {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eTypeParam = root.EClassifiers.head.ETypeParameters.head
		assertEquals("T", eTypeParam.name);
	}

	@Test
	def void emptyClassWithGenericsBound() {
		val result = parseHelper.parse('''
			package test;
			class A<T extends C> {}
			class C {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eTypeParam = root.EClassifiers.head.ETypeParameters.head
		Assertions.assertEquals("T", eTypeParam.name);
		val bounds = eTypeParam.EBounds.get(0);
		assertEquals("C", bounds.EClassifier.name);
	}
	
	@Test
	def void emptyClassWithSuper() {
		val result = parseHelper.parse('''
			package test;
			class A extends B {}
			class B {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eClass = root.EClassifiers.head as EClass
		assertEquals("B", eClass.ESuperTypes.head.name);
	}

	@Test
	def void emptyClassWithSuperWildcard() {
		val result = parseHelper.parse('''
			package test;
			class A extends B<?> {}
			class B {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val genSuperType = (root.EClassifiers.head as EClass).EGenericSuperTypes.head
		assertEquals(1, genSuperType.ETypeArguments.size);
		assertNull(genSuperType.ETypeParameter);
	}
	
	@Test
	def void emptyClassWithSuperWildcardBound() {
		val result = parseHelper.parse('''
			package test;
			class A extends B<? extends C> {}
			class B {}
			class C {}
		''')
		val cache = process(result)
		val root = cache.get(result.package) as EPackage
		val eTypeArg = (root.EClassifiers.head as EClass).EGenericSuperTypes.head.ETypeArguments.head as EGenericType
		assertEquals("C", eTypeArg.EUpperBound.EClassifier.name)
	}
 	
}
