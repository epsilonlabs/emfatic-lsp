package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EParameter
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.emfatic.xtext.ecore.Structure
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl
import org.eclipse.emf.emfatic.xtext.emfatic.MapEntryDecl
import org.eclipse.emf.emfatic.xtext.emfatic.Operation
import org.eclipse.emf.emfatic.xtext.emfatic.Param
import org.eclipse.emf.emfatic.xtext.emfatic.Reference
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith


@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class StructureForPackageTest extends StructureTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void empty() {
		val result = parseHelper.parse(''' ''')
		val creator = new Structure(cache, importer)
		val created = creator.doSwitch(result)
		Assertions.assertNull(created)
	}

	@Test
	def void onlyPackage() {
		val result = parseHelper.parse('''
			package test;
		''')
		val output = process(result)
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EPackage, output);
	}
	
	@Test
	def void subPackage() {
		val result = parseHelper.parse('''
			package test;
			package nested {
				
			}
		''')
		process(result)
		val output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EPackage, output);
		Assertions.assertEquals(
			cache.get(
				result.package,
				result.eResource,
				[null]),
			(output as EPackage).eContainer)
	}

	
	@Test
	def void mapEntry() {
		val result = parseHelper.parse('''
			package test;
			mapentry EStringToStringMapEntry : String -> String;
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EClass, output);
		output = cache.get(
			(result.declarations.head.declaration as MapEntryDecl).key,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			(result.declarations.head.declaration as MapEntryDecl).value,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
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
	//val EPackage[*]#eSuperPackage eSubpackages;
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
			operation.resType,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			operation.resType.type,
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
			operation.resType,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			operation.resType.type,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}
}
