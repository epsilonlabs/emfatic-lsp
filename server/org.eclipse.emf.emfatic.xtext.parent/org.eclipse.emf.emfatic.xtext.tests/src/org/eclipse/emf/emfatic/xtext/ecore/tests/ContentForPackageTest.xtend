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

import static org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class ContentForPackageTest extends ContentTest {

	@Inject
	ParseHelper<CompUnit> parseHelper

	@Test
	def void onlyPackage() {
		val result = parseHelper.parse('''
			@namespace(uri="http://www.eclipse.org/emf/2002/Ecore", prefix="ecore")
			package test;
		''')
		val ePackage = process(result) as EPackage
		assertEquals("test", ePackage.name)
		assertEquals("http://www.eclipse.org/emf/2002/Ecore", ePackage.nsURI)
		assertEquals("ecore", ePackage.nsPrefix)
	}
	
	@Test
	def void oneSubPackage() {
		val result = parseHelper.parse('''
			package test;
			package nested {
				
			}
		''')
		val ePackage = process(result) as EPackage
		val nested = ePackage.ESubpackages.head
		assertEquals("nested", nested.name)
	}


	
}
