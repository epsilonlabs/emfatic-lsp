package org.eclipse.emf.emfatic.xtext.tests

import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.Test
import com.google.inject.Inject
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage
import org.eclipse.emf.emfatic.xtext.validation.IssueCodes

@ExtendWith(InjectionExtension)
@InjectWith(EmfaticInjectorProvider)
class EmfaticValidationTest {
	
	@Inject
	ParseHelper<CompUnit> parseHelper
	
	@Inject ValidationTestHelper validationTestHelper
	
	@Test
	def void ecore_is_valid_annotation() {
		var result = parseHelper.parse('''
			@ecore(settingDelegates="http://www.eclipse.org/emf/2002/Ecore/OCL")
			package db;
		''')
		validationTestHelper.assertNoIssues(result)
		result = parseHelper.parse('''
			@"http://www.eclipse.org/emf/2002/Ecore"(settingDelegates="http://www.eclipse.org/emf/2002/Ecore/OCL")
			package db;
		''')
		validationTestHelper.assertWarning(result, EmfaticPackage.Literals.ANNOTATION, IssueCodes.W_URI_INSTEAD_OF_LABEL)
	}
	
	@Test
	def void emfatic_is_valid_annotation() {
		var result = parseHelper.parse('''
			@EmfaticAnnotationMap(myLabel="http://foo/bar")
			package db;
		''')
		validationTestHelper.assertNoIssues(result)
		result = parseHelper.parse('''
			@"http://www.eclipse.org/emf/2004/EmfaticAnnotationMap"(myLabel="http://foo/bar")
			package db;
		''')
		validationTestHelper.assertWarning(result, EmfaticPackage.Literals.ANNOTATION, IssueCodes.W_URI_INSTEAD_OF_LABEL)
	}
	
	@Test
	def void genmodel_is_valid_annotation() {
		var result = parseHelper.parse('''
			@GenModel(documentation="model documentation")
			package db;
		''')
		validationTestHelper.assertNoIssues(result)
		result = parseHelper.parse('''
			@"http://www.eclipse.org/emf/2002/GenModel"(documentation="model documentation")
			package db;
		''')
		validationTestHelper.assertWarning(result, EmfaticPackage.Literals.ANNOTATION, IssueCodes.W_URI_INSTEAD_OF_LABEL)
	}
	
	@Test
	def void extendedmetadata_is_valid_annotation() {
		var result = parseHelper.parse('''
			@ExtendedMetaData(qualified="true")
			package db;
		''')
		validationTestHelper.assertNoIssues(result)
		result = parseHelper.parse('''
			@"http://org/eclipse/emf/ecore/util/ExtendedMetaData"(qualified="true")
			package db;
		''')
		validationTestHelper.assertWarning(result, EmfaticPackage.Literals.ANNOTATION, IssueCodes.W_URI_INSTEAD_OF_LABEL)
	}
	
	@Test
	def void import_unsupported_exception() {
		var result = parseHelper.parse('''
			package db;
			import "platform:/plugin/com.myplugin/icons/me.gif";
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.IMPORT, IssueCodes.E_UNSUPPORTED_URI_EXTENSION)	
	}
	
	@Test
	def void import_unable_to_load_mm() {
		var result = parseHelper.parse('''
			package db;
			import "platform:/plugin/com.myplugin/icons/me.gif";
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.IMPORT, IssueCodes.E_UNSUPPORTED_URI_EXTENSION)	
	}
	
	@Test
	def void import_valid_mm() {
		var result = parseHelper.parse('''
			package db;
			import "platform:/plugin/org.eclipse.emf.emfatic.xtext.tests/model/vogella.ecore";
		''')
		validationTestHelper.assertNoErrors(result);
	}
	
	@Test
	def void import_missing_resoruce() {
		var result = parseHelper.parse('''
			package db;
			import "platform:/plugin/org.eclipse.emf.emfatic.xtext.tests/model/vogella2.ecore";
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.IMPORT, IssueCodes.E_IMPORTED_METAMODEL_NOT_FOUND)	
	}
	
	@Test
	def void import_registred_mm() {
		var result = parseHelper.parse('''
			package db;
			import "http://www.eclipse.org/Xtext/Xbase/XAnnotations";
		''')
		validationTestHelper.assertNoErrors(result);
	}
	
	@Test
	def void import_self_inheritance() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			class KeyValueMap extends Value, KeyValueMap {
			    attr String key;
			    val Value[*] values;
			}
			
			class Value {
			    attr String tag;
			    attr String value;
			}
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.CLASS_DECL, IssueCodes.E_EXTEND_CYCLE_DETECTED)	
	}
	
	@Test
	def void invalid_annotation_label() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			@somelabel("name"="error")
			class KeyValueMap extends Value {
			    attr String key;
			    val Value[*] values;
			}
			
			class Value {
			    attr String tag;
			    attr String value;
			}
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.ANNOTATION, IssueCodes.E_UNKOWN_ANNOTATION_LABEL)	
	}
	
	@Test
	def void duplicate_annotation_key() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			@genmodel("documentation"="error", "documentation"="error2")
			class KeyValueMap extends Value {
			    attr String key;
			    val Value[*] values;
			}
			
			class Value {
			    attr String tag;
			    attr String value;
			}
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.DETAILS, IssueCodes.E_DUPLICATE_KEY_FOUND)	
	}
	
	@Test
	def void valid_annotation_details() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			@genmodel("documentation"="key-value map pairs")
			class KeyValueMap extends Value {
			    attr String key;
			    val Value[*] values;
			}
			
			class Value {
			    attr String tag;
			    attr String value;
			}
		''')
		validationTestHelper.assertNoErrors(result);
	}
	
	@Test
	def void invalid_annotation_details() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			@genmodel("get"="key-value map pairs")
			class KeyValueMap extends Value {
			    attr String key;
			    val Value[*] values;
			}
			
			class Value {
			    attr String tag;
			    attr String value;
			}
		''')
		validationTestHelper.assertWarning(result, EmfaticPackage.Literals.DETAILS, IssueCodes.W_INVALID_KEY_USED)	
	}
	
	@Test
	def void duplicate_class_name() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			class Value {
			    attr String key;
			    val Value[*] values;
			}
			
			class Value {
			    attr String key;
			    val Value[*] values;
			}
			
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.CLASS_DECL, IssueCodes.E_DUPLICATE_CLASS_NAME)	
	}
	
	@Test
	def void duplicate_feature_name() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			class Value {
			    attr String key;
			    attr String key;
			    val Value[*] values;
			}
			
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.FEATURE_DECL, IssueCodes.E_DUPLICATE_FEATURE_NAME)	
		
		result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			class Value {
			    attr String key;
			    val Value[*] values;
			    val Value[*] values;
			}
			
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.FEATURE_DECL, IssueCodes.E_DUPLICATE_FEATURE_NAME)	
	}
	
	@Test
	def void duplicate_operation_name() {
		var result = parseHelper.parse('''
			@namespace(uri="http://www.deis-project.eu/ode/base", prefix="odeBase")
			package odeBase;
			
			class Value {
			    attr String key;
			    val Value[*] values;
			    op boolean isSet();
			    op boolean isSet();
			}
			
		''')
		validationTestHelper.assertError(result, EmfaticPackage.Literals.OPERATION, IssueCodes.E_DUPLICATE_OPERATION_NAME)	
	}
	// 

}
