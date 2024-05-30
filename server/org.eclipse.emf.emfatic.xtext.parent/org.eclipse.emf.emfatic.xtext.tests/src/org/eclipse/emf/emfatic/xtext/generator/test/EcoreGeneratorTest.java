package org.eclipse.emf.emfatic.xtext.generator.test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfatic.xtext.generator.EcoreGenerator;
import org.eclipse.emf.emfatic.xtext.tests.EmfaticInjectorProvider;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(EmfaticInjectorProvider.class)
class EcoreGeneratorTest {
	
	@Test
	public void ePackage() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).iterator();
		var line = it.next();
		assertEquals("@namespace(uri=\"http://www.iec.ch/61850/2006/SCL\", prefix=\"scl\")", line);
		line = it.next();
		assertEquals("package scl;", line);	
	}
	
	@Test
	public void ePackageWithAnnotations() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://example.org");
		annot.getDetails().put("keyA", "Value A");
		annot.getDetails().put("key B", "ValueB");
		ePackage.getEAnnotations().add(annot);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).iterator();
		var line = it.next();
		line = it.next();
		assertEquals("@http://example.org(", line);
		line = it.next();
		assertEquals("\tkeyA=\"Value A\",", line);
		line = it.next();
		assertEquals("\t\"key B\"=ValueB", line);
		line = it.next();
		assertEquals(")", line);
	}
	
	@Test
	public void eDataType() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var dt = EcoreFactory.eINSTANCE.createEDataType();
		ePackage.getEClassifiers().add(dt);
		dt.setName("TAuthenticationEnumObject");
		dt.setInstanceClassName("org.eclipse.emf.common.util.Enumerator");
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, 4).iterator();
		var line = it.next();
		assertEquals("datatype TAuthenticationEnumObject : org.eclipse.emf.common.util.Enumerator;", line);
	}
	
	@Test
	public void eDataTypeWithAnnotations() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var dt = EcoreFactory.eINSTANCE.createEDataType();
		ePackage.getEClassifiers().add(dt);
		dt.setName("TAuthenticationEnumObject");
		dt.setInstanceClassName("org.eclipse.emf.common.util.Enumerator");
		var annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://example.org");
		annot.getDetails().put("keyA", "Value A");
		dt.getEAnnotations().add(annot);
		annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://test.org");
		annot.getDetails().put("key B", "ValueB");
		dt.getEAnnotations().add(annot);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("@http://example.org(", line);
		line = it.next();
		assertEquals("\tkeyA=\"Value A\"", line);
		line = it.next();
		assertEquals(")", line);
		line = it.next();
		assertEquals("@http://test.org(", line);
		line = it.next();
		assertEquals("\t\"key B\"=ValueB", line);
		line = it.next();
		assertEquals(")", line);
	}
	
	@Test
	public void eDataTypeTransient() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var dt = EcoreFactory.eINSTANCE.createEDataType();
		ePackage.getEClassifiers().add(dt);
		dt.setName("TAuthenticationEnumObject");
		dt.setInstanceClassName("org.eclipse.emf.common.util.Enumerator");
		dt.setSerializable(false);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("transient datatype TAuthenticationEnumObject : org.eclipse.emf.common.util.Enumerator;", line);
	}
	
	@Test
	public void eEnumEmpty() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var en = EcoreFactory.eINSTANCE.createEEnum();
		ePackage.getEClassifiers().add(en);
		en.setName("TDomainLNGroupAEnum");
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("enum TDomainLNGroupAEnum {", line);
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eEnumEmptyWithAnnotations() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var en = EcoreFactory.eINSTANCE.createEEnum();
		ePackage.getEClassifiers().add(en);
		en.setName("TDomainLNGroupAEnum");
		var annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://example.org");
		annot.getDetails().put("keyA", "Value A");
		en.getEAnnotations().add(annot);
		annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://test.org");
		annot.getDetails().put("key B", "ValueB");
		en.getEAnnotations().add(annot);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("@http://example.org(", line);
		line = it.next();
		assertEquals("\tkeyA=\"Value A\"", line);
		line = it.next();
		assertEquals(")", line);
		line = it.next();
		assertEquals("@http://test.org(", line);
		line = it.next();
		assertEquals("\t\"key B\"=ValueB", line);
		line = it.next();
		assertEquals(")", line);
		line = it.next();
		assertEquals("enum TDomainLNGroupAEnum {", line);
	}
	
	@Test
	public void eEnumLiterals() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var en = EcoreFactory.eINSTANCE.createEEnum();
		ePackage.getEClassifiers().add(en);
		en.setName("TDomainLNGroupAEnum");
		var enl = EcoreFactory.eINSTANCE.createEEnumLiteral();
		enl.setName("ANCR");
		en.getELiterals().add(enl);
		enl = EcoreFactory.eINSTANCE.createEEnumLiteral();
		enl.setName("ARCO");
		enl.setLiteral("1");
		en.getELiterals().add(enl);
		enl = EcoreFactory.eINSTANCE.createEEnumLiteral();
		enl.setName("ATCC");
		en.getELiterals().add(enl);
		enl = EcoreFactory.eINSTANCE.createEEnumLiteral();
		enl.setName("AVCO");
		enl.setLiteral("3");
		en.getELiterals().add(enl);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("enum TDomainLNGroupAEnum {", line);
		line = it.next();
		assertEquals("\tANCR;", line);
		line = it.next();
		assertEquals("\tARCO=1;", line);
		line = it.next();
		assertEquals("\tATCC;", line);
		line = it.next();
		assertEquals("\tAVCO=3;", line);
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eEnumLiteralWithAnnotations() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var en = EcoreFactory.eINSTANCE.createEEnum();
		ePackage.getEClassifiers().add(en);
		en.setName("TDomainLNGroupAEnum");
		var enl = EcoreFactory.eINSTANCE.createEEnumLiteral();
		enl.setName("ANCR");
		en.getELiterals().add(enl);
		var annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://example.org");
		annot.getDetails().put("keyA", "Value A");
		enl.getEAnnotations().add(annot);
		annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://test.org");
		annot.getDetails().put("key B", "ValueB");
		enl.getEAnnotations().add(annot);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("enum TDomainLNGroupAEnum {", line);
		line = it.next();
		assertEquals("\t@http://example.org(", line);
		line = it.next();
		assertEquals("\t\tkeyA=\"Value A\"", line);
		line = it.next();
		assertEquals("\t)", line);
		line = it.next();
		assertEquals("\t@http://test.org(", line);
		line = it.next();
		assertEquals("\t\t\"key B\"=ValueB", line);
		line = it.next();
		assertEquals("\t)", line);
		line = it.next();
		assertEquals("\tANCR;", line);
	}
	
	@Test
	public void eClassEmpty() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithAnnotations() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		var annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://example.org");
		annot.getDetails().put("keyA", "Value A");
		ec.getEAnnotations().add(annot);
		annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://test.org");
		annot.getDetails().put("key B", "ValueB");
		ec.getEAnnotations().add(annot);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("@http://example.org(", line);
		line = it.next();
		assertEquals("\tkeyA=\"Value A\"", line);
		line = it.next();
		assertEquals(")", line);
		line = it.next();
		assertEquals("@http://test.org(", line);
		line = it.next();
		assertEquals("\t\"key B\"=ValueB", line);
		line = it.next();
		assertEquals(")", line);
		line = it.next();
		assertEquals("class AuthenticationType {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithSuperType() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ecSuper = EcoreFactory.eINSTANCE.createEClass();
		ecSuper.setName("TBaseElement");
		ePackage.getEClassifiers().add(ecSuper);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("SCLType");
		ec.getESuperTypes().add(ecSuper);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(7, result.length).iterator();
		var line = it.next();
		assertEquals("class SCLType extends TBaseElement {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithSuperTypes() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ecSuper1 = EcoreFactory.eINSTANCE.createEClass();
		ecSuper1.setName("TBaseElement");
		ePackage.getEClassifiers().add(ecSuper1);
		var ecSuper2 = EcoreFactory.eINSTANCE.createEClass();
		ecSuper2.setName("TLN0");
		ePackage.getEClassifiers().add(ecSuper2);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("SCLType");
		ec.getESuperTypes().add(ecSuper1);
		ec.getESuperTypes().add(ecSuper2);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(11, result.length).iterator();
		var line = it.next();
		assertEquals("class SCLType extends TBaseElement, TLN0 {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}

	@Test
	public void eClassEmptyWtihInstanceClassName() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ec.setInstanceClassName("org.eclipse.emf.common.util.Enumerator");
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType : org.eclipse.emf.common.util.Enumerator {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithSuperAndInstanceClassName() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ecSuper = EcoreFactory.eINSTANCE.createEClass();
		ecSuper.setName("TBaseElement");
		ePackage.getEClassifiers().add(ecSuper);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ec.setInstanceClassName("org.eclipse.emf.common.util.Enumerator");
		ec.getESuperTypes().add(ecSuper);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(7, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType extends TBaseElement : org.eclipse.emf.common.util.Enumerator {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyAbstract() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ec.setAbstract(true);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("abstract class AuthenticationType {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyInterface() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ec.setInterface(true);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("interface AuthenticationType {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyAbstractInterface() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ec.setAbstract(true);
		ec.setInterface(true);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("abstract interface AuthenticationType {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithTypeParam() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		var tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("C");
		ec.getETypeParameters().add(tp);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType<C> {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithTypeParams() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		var tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("C");
		ec.getETypeParameters().add(tp);
		tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("T");
		ec.getETypeParameters().add(tp);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType<C, T> {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithTypeParamExtends() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ecGen = EcoreFactory.eINSTANCE.createEClass();
		ecGen.setName("A");
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		var tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("C");
		var bound = EcoreFactory.eINSTANCE.createEGenericType();
		bound.setEClassifier(ecGen);
		tp.getEBounds().add(bound);
		ec.getETypeParameters().add(tp);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType<C extends A> {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithTypeParamExtendsMulti() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		var tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("C");
		var bound = EcoreFactory.eINSTANCE.createEGenericType();
		var ecGen = EcoreFactory.eINSTANCE.createEClass();
		ecGen.setName("A");
		bound.setEClassifier(ecGen);
		tp.getEBounds().add(bound);
		bound = EcoreFactory.eINSTANCE.createEGenericType();
		ecGen = EcoreFactory.eINSTANCE.createEClass();
		ecGen.setName("B");
		bound.setEClassifier(ecGen);
		tp.getEBounds().add(bound);
		ec.getETypeParameters().add(tp);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType<C extends A & B> {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithTypeParamExtendsWildcard() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		// TypeParam
		var tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("C");
		ec.getETypeParameters().add(tp);
		// TypeBoundsInfo
		var bound = EcoreFactory.eINSTANCE.createEGenericType();
		tp.getEBounds().add(bound);
		// BoundClassifierExceptWildcard
		var ecGen = EcoreFactory.eINSTANCE.createEClass();
		ecGen.setName("A");
		bound.setEClassifier(ecGen);
		// TypeArg
		var ta = EcoreFactory.eINSTANCE.createEGenericType();
		bound.getETypeArguments().add(ta);
		ePackage.getEClassifiers().add(ec);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType<C extends A<?>> {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithTypeParamExtendsWildcardLower() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		// TypeParam
		var tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("C");
		ec.getETypeParameters().add(tp);
		// TypeBoundsInfo
		var bound = EcoreFactory.eINSTANCE.createEGenericType();
		tp.getEBounds().add(bound);
		// BoundClassifierExceptWildcard
		var ecGen = EcoreFactory.eINSTANCE.createEClass();
		ecGen.setName("A");
		bound.setEClassifier(ecGen);
		// TypeArg
		var ta = EcoreFactory.eINSTANCE.createEGenericType();
		bound.getETypeArguments().add(ta);
		// LowerBound
		var lower = EcoreFactory.eINSTANCE.createEGenericType();
		var ecLower = EcoreFactory.eINSTANCE.createEClass();
		ecLower.setName("X");
		lower.setEClassifier(ecLower);
		ta.setELowerBound(lower);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType<C extends A<? extends X>> {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassEmptyWithTypeParamExtendsWildcardUpper() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		// TypeParam
		var tp = EcoreFactory.eINSTANCE.createETypeParameter();
		tp.setName("C");
		ec.getETypeParameters().add(tp);
		// TypeBoundsInfo
		var bound = EcoreFactory.eINSTANCE.createEGenericType();
		tp.getEBounds().add(bound);
		// BoundClassifierExceptWildcard
		var ecGen = EcoreFactory.eINSTANCE.createEClass();
		ecGen.setName("A");
		bound.setEClassifier(ecGen);
		// TypeArg
		var ta = EcoreFactory.eINSTANCE.createEGenericType();
		bound.getETypeArguments().add(ta);
		// LowerBound
		var upper = EcoreFactory.eINSTANCE.createEGenericType();
		var ecUpper = EcoreFactory.eINSTANCE.createEClass();
		ecUpper.setName("X");
		upper.setEClassifier(ecUpper);
		ta.setEUpperBound(upper);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType<C extends A<? super X>> {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	
	
	@Inject
	private EcoreGenerator generator;
	
	private final InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
	
	private String output(boolean print) {
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
		if (print) {
			System.out.println(output);
		}
		return output;
	}
	
	private EPackage rootPackage() {
		var ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("scl");
		ePackage.setNsURI("http://www.iec.ch/61850/2006/SCL");
		ePackage.setNsPrefix("scl");
		return ePackage;
	}
	
	private Resource addToResource(EPackage ePackage) {
		var r = createResource();
		r.getContents().add(ePackage);
		return r;
	}

	private Resource createResource() {
		var rs = new ResourceSetImpl();
		return rs.createResource(URI.createURI("some/uri"));
	}
	
}