package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EParameter
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.Operation
import org.eclipse.emf.emfatic.xtext.emfatic.Param
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
class StructureForOperationsTest {

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
	def void classWithOperation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName();
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val operation = (classDecl).members.head.member as Operation
		var output = cache.get(
			operation,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EOperation, output);
		output = cache.get(
			operation.resultType,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			operation.resultType.type,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void classWithOperationWithAnnotation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				@"http://class/annotation"(k="v")
				op String getFullName();
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
	def void classWithOperationWithGenericType() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op <T> String getFullName();
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val operation = (classDecl).members.head.member as Operation
		val output = cache.get(
			operation.typeParamsInfo.tp.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void classWithOperationWithParameter() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(String ~id);
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val operation = (classDecl).members.head.member as Operation
		val param = operation.params.head as Param
		var output = cache.get(
			param,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EParameter, output);
		output = cache.get(
			param.typeWithMulti,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void classWithOperationWithParameterWithAnnotations() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(@before(k=v) String ~id @after(k=v));
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val operation = (classDecl).members.head.member as Operation
		val param = operation.params.head
		var output = cache.get(
			param.leadingAnnotations.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			param.trailingAnnotations.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
	
	@Test
	def void classWithOperationWithThrows() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName();
			}
		''')
		process(result)
		val classDecl = result.declarations.head.declaration as ClassDecl
		val operation = (classDecl).members.head.member as Operation
		var output = cache.get(
			operation,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EOperation, output);
		output = cache.get(
			operation.resultType,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			operation.resultType.type,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
}
