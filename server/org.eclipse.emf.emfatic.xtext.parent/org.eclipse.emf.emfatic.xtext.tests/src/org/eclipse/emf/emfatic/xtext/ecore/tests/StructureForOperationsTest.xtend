package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
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
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.Operation
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMemberDecl
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EParameter

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForOperationsTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void classWithOperation() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName();
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertInstanceOf(EPackage, cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		assertInstanceOf(EClass, cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val operation = classDecl.members.get(0).member as Operation
		assertNotNull(cache.get(operation))
		assertInstanceOf(EOperation, cache.get(operation))
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
	def void classWithOperationWithGenericType() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op <T> String getFullName();
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val operation = classDecl.members.get(0).member as Operation
		assertNotNull(cache.get(operation))
		assertInstanceOf(EOperation, cache.get(operation))
	}
	
	@Test
	def void classWithOperationWithParameter() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(String ~id);
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val operation = classDecl.members.get(0).member as Operation
		assertNotNull(cache.get(operation))
		assertInstanceOf(EOperation, cache.get(operation))
		assertNotNull(cache.get(operation.params.get(0)))
		assertInstanceOf(EParameter, cache.get(operation.params.get(0)))
	}
	
	@Test
	def void classWithOperationWithParameters() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(String ~id, B school);
			}
			class B {}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val operation = classDecl.members.get(0).member as Operation
		assertNotNull(cache.get(operation))
		assertInstanceOf(EOperation, cache.get(operation))
		assertNotNull(cache.get(operation.params.get(0)))
		assertInstanceOf(EParameter, cache.get(operation.params.get(0)))
		assertNotNull(cache.get(operation.params.get(1)))
		assertInstanceOf(EParameter, cache.get(operation.params.get(1)))
	}
	
	@Test
	def void classWithOperationWithParameterWithAnnotations() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName(@before(k=v) String ~id @after(k=v));
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val operation = classDecl.members.get(0).member as Operation
		assertNotNull(cache.get(operation))
		assertInstanceOf(EOperation, cache.get(operation))
		assertNotNull(cache.get(operation.params.get(0)))
		val param = operation.params.get(0)
		assertNotNull(cache.get(param.leadingAnnotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(param.leadingAnnotations.get(0)))
		assertNotNull(cache.get(param.trailingAnnotations.get(0)))
		assertInstanceOf(EAnnotation, cache.get(param.trailingAnnotations.get(0)))
		
	}
	
	@Test
	def void classWithOperationWithThrows() {
		val result = parseHelper.parse('''
			package test;
			class A {
				op String getFullName() throws NullPointerException;
			}
		''')
		val cache = process(result)
		assertNotNull(cache.get(result.package))
		assertNotNull(cache.get(result.declarations.get(0).declaration))
		val classDecl = result.declarations.get(0).declaration as ClassDecl
		val operation = classDecl.members.get(0).member as Operation
		assertNotNull(cache.get(operation))
		assertInstanceOf(EOperation, cache.get(operation))
		assertNotNull(cache.get(operation.exceptions.get(0)))
	}
}
