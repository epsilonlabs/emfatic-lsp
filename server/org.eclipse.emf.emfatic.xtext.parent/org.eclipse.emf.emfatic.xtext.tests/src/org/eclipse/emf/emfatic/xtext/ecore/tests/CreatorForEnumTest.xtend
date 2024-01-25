package org.eclipse.emf.emfatic.xtext.ecore.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.util.OnChangeEvictingCache
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.emfatic.xtext.emfatic.EnumDecl
import org.eclipse.emf.emfatic.xtext.ecore.Structure

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class CreatorForEnumTest {

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
	def void enumType() {
		val result = parseHelper.parse('''
			package test;
			enum E {
			  A=1;
			  B=2;
			  C=3;
			}
		''')
		process(result)
		var output = cache.get(
			result.declarations.head.declaration,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		Assertions.assertInstanceOf(EEnum, output);
		
	}
	
	@Test
	def void enumTypeWithAnnotations() {
		val result = parseHelper.parse('''
			package test;
			enum E { 
			  @"http://before"(k=v) 
			  A=1 @"http://after"(k=v); 
			}
		''')
		process(result)
		val datatype = result.declarations.head.declaration as EnumDecl
		var output = cache.get(
			datatype.enumLiterals.head.leadingAnnotations.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
		output = cache.get(
			datatype.enumLiterals.head.trailingAnnotations.head,
			result.eResource,
			[null])
		Assertions.assertNotNull(output)
	}

}
