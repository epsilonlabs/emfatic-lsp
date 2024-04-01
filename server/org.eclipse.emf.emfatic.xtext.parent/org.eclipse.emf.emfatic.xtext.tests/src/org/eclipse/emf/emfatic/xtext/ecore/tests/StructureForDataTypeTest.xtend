package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertNotNull
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl
import org.eclipse.emf.ecore.ETypeParameter

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForDataTypeTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Test
	def void dataType() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt : int;
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EDataType, cache.get(result.declarations.get(0).declaration))
	}
	
	@Test
	def void dataTypeWithAnnotations() {
		val result = parseHelper.parse('''
			package test;
			@"http://class/annotation"(k="v")
			datatype EInt : int;
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertNotNull(cache.get(result.declarations.get(0).annotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(result.declarations.get(0).annotations.get(0)))
	}
	
	@Test
	def void dataTypeWithGenerics() {
		val result = parseHelper.parse('''
			package test;
			datatype EInt<A> : int;
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as DataTypeDecl
		assertNotNull(cache.get(classDecl.typeParamsInfo.tp.get(0)))
		assertInstanceOf(ETypeParameter, cache.get(classDecl.typeParamsInfo.tp.get(0)))
	}

}
