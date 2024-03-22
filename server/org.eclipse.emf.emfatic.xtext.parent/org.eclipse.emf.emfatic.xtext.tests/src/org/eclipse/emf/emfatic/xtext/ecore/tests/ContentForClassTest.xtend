package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.emf.ecore.EAnnotation

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
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		Assertions.assertEquals("A", (output as EClass).name);
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
		Assertions.assertEquals("http://class/annotation", (output as EAnnotation).source)
		Assertions.assertEquals(1, (output as EAnnotation).details.size)
		Assertions.assertTrue((output as EAnnotation).details.containsKey("k"))
		Assertions.assertEquals("v", (output as EAnnotation).details.get("k"))
	}
	
	@Test
	def void abstractClass() {
		val result = parseHelper.parse('''
			package test;
			abstract class A {}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		Assertions.assertTrue((output as EClass).abstract);	
	}
	
	@Test
	def void interfaceClass() {
		val result = parseHelper.parse('''
			package test;
			interface A {}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		Assertions.assertTrue((output as EClass).interface);	
	}
	
	@Test
	def void emptyClassWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			class A<T> {}
		''')
		process(result)
		val output = cache.get(
			(result.declarations.head.declaration as ClassDecl).typeParamsInfo.tp.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(ETypeParameter, output);
		Assertions.assertEquals("T", (output as ETypeParameter).name);
	}


	@Test
	def void emptyClassWithGenericsBound() {
		val result = parseHelper.parse('''
			package test;
			class A<T extends C> {}
			class C {}
		''')
		process(result)
		val output = cache.get(
			(result.declarations.head.declaration as ClassDecl).typeParamsInfo.tp.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(ETypeParameter, output);
		Assertions.assertEquals("T", (output as ETypeParameter).name);
		val bounds = (output as ETypeParameter).EBounds.get(0);
		Assertions.assertEquals("C", bounds.EClassifier.name);
	}
	
	@Test
	def void emptyClassWithSuper() {
		val result = parseHelper.parse('''
			package test;
			class A extends B {}
			class B {}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		Assertions.assertEquals("B", (output as EClass).ESuperTypes.head.name);
	}

	@Test
	def void emptyClassWithSuperWildcard() {
		val result = parseHelper.parse('''
			package test;
			class A extends B<?> {}
			class B {}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		val genSuperType = (output as EClass).EGenericSuperTypes.head
		Assertions.assertEquals(1, genSuperType.ETypeArguments.size);
		Assertions.assertNull(genSuperType.ETypeParameter);
	}
	
	@Test
	def void emptyClassWithSuperWildcardBound() {
		val result = parseHelper.parse('''
			package test;
			class A extends B<? extends C> {}
			class B {}
			class C {}
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
