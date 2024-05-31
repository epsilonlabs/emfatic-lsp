package org.eclipse.emf.emfatic.xtext.generator.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		var line = it.next();
		assertEquals("class SCLType extends TBaseElement {", line);
		it.next();
		it.next();
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
		var it = Arrays.asList(result).subList(12, result.length).iterator();
		it.next();
		var line = it.next();
		assertEquals("class SCLType extends TBaseElement, TLN0 {", line);
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		var line = it.next();
		assertEquals("class AuthenticationType extends TBaseElement : org.eclipse.emf.common.util.Enumerator {", line);
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
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
		it.next();
		it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassMapEntryAttributes() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("SizeToWeight");
		ec.setInstanceClassName("java.util.Map$Entry");
		ePackage.getEClassifiers().add(ec);
		var key = EcoreFactory.eINSTANCE.createEAttribute();
		key.setName("key");
		key.setEType(EcorePackage.Literals.ESTRING);
		ec.getEStructuralFeatures().add(key);
		var value = EcoreFactory.eINSTANCE.createEAttribute();
		value.setName("value");
		value.setEType(EcorePackage.Literals.ESHORT);
		ec.getEStructuralFeatures().add(value);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("mapentry SizeToWeight : EString -> EShort;", line);
	}
	
	@Test
	public void eClassMapEntryAttributeAndReference() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("SizeToWeight");
		ec.setInstanceClassName("java.util.Map$Entry");
		ePackage.getEClassifiers().add(ec);
		var key = EcoreFactory.eINSTANCE.createEAttribute();
		key.setName("key");
		key.setEType(EcorePackage.Literals.ESTRING);
		ec.getEStructuralFeatures().add(key);
		var value = EcoreFactory.eINSTANCE.createEReference();
		value.setName("value");
		var valueType = EcoreFactory.eINSTANCE.createEClass();
		valueType.setName("A");
		ePackage.getEClassifiers().add(valueType);
		value.setEType(valueType);
		ec.getEStructuralFeatures().add(value);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("mapentry SizeToWeight : EString -> A;", line);
	}
	
	@Test
	public void eClassWithAttribtues() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		// [0..1]
		var ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("certificate");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ec.getEStructuralFeatures().add(ea);
		// [0..-1]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("none");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(-1);
		ec.getEStructuralFeatures().add(ea);
		// [0..n]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("password");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(4);
		ec.getEStructuralFeatures().add(ea);
		// [1..-1]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("strong");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(1);
		ea.setUpperBound(-1);
		ec.getEStructuralFeatures().add(ea);
		// [1..1]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("weak");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(1);
		ea.setUpperBound(1);
		ec.getEStructuralFeatures().add(ea);
		// [n]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("mixed");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(2);
		ea.setUpperBound(2);
		ec.getEStructuralFeatures().add(ea);
		// [m..n]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("configRef");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(3);
		ea.setUpperBound(7);
		ec.getEStructuralFeatures().add(ea);
		// [5..*]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("dataRef");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(5);
		ea.setUpperBound(-1);
		ec.getEStructuralFeatures().add(ea);
		// [1..?]
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("dataSet");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(1);
		ea.setUpperBound(-2);
		ec.getEStructuralFeatures().add(ea);
		
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType {", line);
		line = it.next();
		line = it.next();
		assertEquals("\tattr EString certificate;", line);
		line = it.next();
		assertEquals("\tattr EString[*] none;", line);
		line = it.next();
		assertEquals("\tattr EString[0..4] password;", line);
		line = it.next();
		assertEquals("\tattr EString[+] strong;", line);
		line = it.next();
		assertEquals("\tattr EString[1] weak;", line);
		line = it.next();
		assertEquals("\tattr EString[2] mixed;", line);
		line = it.next();
		assertEquals("\tattr EString[3..7] configRef;", line);
		line = it.next();
		assertEquals("\tattr EString[5..*] dataRef;", line);
		line = it.next();
		assertEquals("\tattr EString[1..?] dataSet;", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassWithAttribtuesEscapeKeywords() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		var ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("abstract");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ec.getEStructuralFeatures().add(ea);
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("interface");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(-1);
		ec.getEStructuralFeatures().add(ea);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType {", line);
		it.next();
		line = it.next();
		assertEquals("\tattr EString ~abstract;", line);
		line = it.next();
		assertEquals("\tattr EString[*] ~interface;", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassWithReferences() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec1 = EcoreFactory.eINSTANCE.createEClass();
		ec1.setName("School");
		ePackage.getEClassifiers().add(ec1);
		var ec2 = EcoreFactory.eINSTANCE.createEClass();
		ec2.setName("Course");
		ePackage.getEClassifiers().add(ec2);
		var ec3 = EcoreFactory.eINSTANCE.createEClass();
		ec3.setName("Teacher");
		ePackage.getEClassifiers().add(ec3);
		var er = EcoreFactory.eINSTANCE.createEReference();
		er.setName("courses");
		er.setEType(ec2);
		er.setContainment(true);
		er.setLowerBound(0);
		er.setUpperBound(-1);
		ec1.getEStructuralFeatures().add(er);
		er = EcoreFactory.eINSTANCE.createEReference();
		er.setName("teachers");
		er.setEType(ec3);
		er.setContainment(true);
		er.setLowerBound(0);
		er.setUpperBound(-1);
		ec1.getEStructuralFeatures().add(er);
		var toughtBy = EcoreFactory.eINSTANCE.createEReference();
		toughtBy.setName("toughtBy");
		toughtBy.setEType(ec3);
		toughtBy.setContainment(false);
		toughtBy.setLowerBound(1);
		toughtBy.setUpperBound(1);
		ec2.getEStructuralFeatures().add(toughtBy);
		var teaches = EcoreFactory.eINSTANCE.createEReference();
		teaches.setName("teaches");
		teaches.setEType(ec2);
		teaches.setContainment(false);
		teaches.setLowerBound(1);
		teaches.setUpperBound(-1);
		teaches.setEOpposite(toughtBy);
		toughtBy.setEOpposite(teaches);
		ec3.getEStructuralFeatures().add(teaches);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class School {", line);
		it.next();
		line = it.next();
		assertEquals("\tval Course[*] courses;", line);
		line = it.next();
		assertEquals("\tval Teacher[*] teachers;", line);
		it.next();
		line = it.next();
		assertEquals("}", line);
		it.next();
		line = it.next();
		assertEquals("class Course {", line);
		it.next();
		line = it.next();
		assertEquals("\tref Teacher[1]#teaches toughtBy;", line);
		it.next();
		line = it.next();
		assertEquals("}", line);
		it.next();
		line = it.next();
		assertEquals("class Teacher {", line);
		it.next();
		line = it.next();
		assertEquals("\tref Course[+]#toughtBy teaches;", line);
		it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassFeatureModifiers() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("School");
		ePackage.getEClassifiers().add(ec);
		var ec2 = EcoreFactory.eINSTANCE.createEClass();
		ec2.setName("Course");
		ePackage.getEClassifiers().add(ec2);
		// Resolve
		var er = EcoreFactory.eINSTANCE.createEReference();
		er.setName("courses");
		er.setEType(ec2);
		er.setContainment(true);
		er.setLowerBound(0);
		er.setUpperBound(-1);
		er.setResolveProxies(false);
		ec.getEStructuralFeatures().add(er);
		// Readonly
		var ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("certificate");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ea.setChangeable(false);
		ec.getEStructuralFeatures().add(ea);
		// Volatile
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("none");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(-1);
		ea.setVolatile(true);
		ec.getEStructuralFeatures().add(ea);
		// Transient
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("password");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(4);
		ea.setTransient(true);
		ec.getEStructuralFeatures().add(ea);
		// Unsettable
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("strong");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(1);
		ea.setUpperBound(-1);
		ea.setUnsettable(true);
		ec.getEStructuralFeatures().add(ea);
		// ID
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("weak");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(1);
		ea.setUpperBound(1);
		ea.setID(true);
		ec.getEStructuralFeatures().add(ea);
		// Unique
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("mixed");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(2);
		ea.setUpperBound(2);
		ea.setUnique(false);
		ec.getEStructuralFeatures().add(ea);
		// Ordered
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("configRef");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(3);
		ea.setUpperBound(7);
		ea.setOrdered(false);
		ec.getEStructuralFeatures().add(ea);
		// Derived
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("dataRef");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(5);
		ea.setUpperBound(-1);
		ea.setDerived(true);
		ec.getEStructuralFeatures().add(ea);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class School {", line);
		it.next();
		line = it.next();
		assertEquals("\treadonly attr EString certificate;", line);
		line = it.next();
		assertEquals("\tvolatile attr EString[*] none;", line);
		line = it.next();
		assertEquals("\ttransient attr EString[0..4] password;", line);
		line = it.next();
		assertEquals("\tunsettable attr EString[+] strong;", line);
		line = it.next();
		assertEquals("\tid attr EString[1] weak;", line);
		line = it.next();
		assertEquals("\t!unique attr EString[2] mixed;", line);
		line = it.next();
		assertEquals("\t!ordered attr EString[3..7] configRef;", line);
		line = it.next();
		assertEquals("\tderived attr EString[5..*] dataRef;", line);
		line = it.next();
		assertEquals("\t!resolve val Course[*] courses;", line);
		it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassWithAnnotatedFeatures() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec1 = EcoreFactory.eINSTANCE.createEClass();
		ec1.setName("School");
		ePackage.getEClassifiers().add(ec1);
		var ec2 = EcoreFactory.eINSTANCE.createEClass();
		ec2.setName("Course");
		ePackage.getEClassifiers().add(ec2);
		var ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("certificate");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		var annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://example.org");
		annot.getDetails().put("keyA", "Value A");
		ea.getEAnnotations().add(annot);
		ec2.getEStructuralFeatures().add(ea);
		var er = EcoreFactory.eINSTANCE.createEReference();
		er.setName("courses");
		er.setEType(ec2);
		er.setContainment(true);
		er.setLowerBound(0);
		er.setUpperBound(-1);
		annot = EcoreFactory.eINSTANCE.createEAnnotation();
		annot.setSource("http://example.org");
		annot.getDetails().put("key B", "ValueB");
		er.getEAnnotations().add(annot);
		ec1.getEStructuralFeatures().add(er); 
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class School {", line);
		it.next();
		line = it.next();
		assertEquals("\t@http://example.org(", line);
		line = it.next();
		assertEquals("\t\t\"key B\"=ValueB", line);
		line = it.next();
		assertEquals("\t)", line);
		line = it.next();
		assertEquals("\tval Course[*] courses;", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
		line = it.next();
		line = it.next();
		assertEquals("class Course {", line);
		it.next();
		line = it.next();
		assertEquals("\t@http://example.org(", line);
		line = it.next();
		assertEquals("\t\tkeyA=\"Value A\"", line);
		line = it.next();
		assertEquals("\t)", line);
		line = it.next();
		assertEquals("\tattr EString certificate;", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	
	@Test
	public void eClassWithAttribtuesWithDefaultValues() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		// Boolean
		var ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("certificate");
		ea.setEType(EcorePackage.Literals.EBOOLEAN);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ea.setDefaultValue(true);
		ec.getEStructuralFeatures().add(ea);
		// Integer
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("none");
		ea.setEType(EcorePackage.Literals.EINT);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ea.setDefaultValue(123);
		ec.getEStructuralFeatures().add(ea);
		// Float
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("password");
		ea.setEType(EcorePackage.Literals.EFLOAT);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ea.setDefaultValue(4.5);
		ec.getEStructuralFeatures().add(ea);
		// String
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("strong");
		ea.setEType(EcorePackage.Literals.ESTRING);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ea.setDefaultValue("unfold");
		ec.getEStructuralFeatures().add(ea);
		// Char
		ea = EcoreFactory.eINSTANCE.createEAttribute();
		ea.setName("weak");
		ea.setEType(EcorePackage.Literals.ECHAR);
		ea.setLowerBound(0);
		ea.setUpperBound(1);
		ea.setDefaultValue('g');
		ec.getEStructuralFeatures().add(ea);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(false);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType {", line);
		it.next();
		line = it.next();
		assertEquals("\tattr EBoolean certificate = \"true\";", line);
		line = it.next();
		assertEquals("\tattr EInt none = 123;", line);
		line = it.next();
		assertEquals("\tattr EFloat password = 4.5;", line);
		line = it.next();
		assertEquals("\tattr EString strong = \"unfold\";", line);
		line = it.next();
		assertEquals("\tattr EChar weak = 'g';", line);
		it.next();
		line = it.next();
		assertEquals("}", line);
	}
	
	@Test
	public void eClassWithOperations() {
		var ePackage = rootPackage();
		var r = addToResource(ePackage);
		var ec = EcoreFactory.eINSTANCE.createEClass();
		ec.setName("AuthenticationType");
		ePackage.getEClassifiers().add(ec);
		// void - no parameters
		var op = EcoreFactory.eINSTANCE.createEOperation();
		op.setName("validate");
		ec.getEOperations().add(op);
		// Return type - no parameters
		op = EcoreFactory.eINSTANCE.createEOperation();
		op.setName("type");
		op.setEType(EcorePackage.Literals.ESTRING);
		ec.getEOperations().add(op);
		// Return type, parameters
		op = EcoreFactory.eINSTANCE.createEOperation();
		op.setName("approve");
		op.setEType(EcorePackage.Literals.ESTRING);
		var param = EcoreFactory.eINSTANCE.createEParameter();
		param.setName("name");
		param.setEType(EcorePackage.Literals.ESTRING);
		op.getEParameters().add(param);
		param = EcoreFactory.eINSTANCE.createEParameter();
		param.setName("admin");
		param.setEType(EcorePackage.Literals.EBOOLEAN);
		op.getEParameters().add(param);
		ec.getEOperations().add(op);
		this.generator.generate(r, "test.emf", this.fsa);
		String output = output(true);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(3, result.length).iterator();
		var line = it.next();
		assertEquals("class AuthenticationType {", line);
		it.next();
		line = it.next();
		assertEquals("\top void validate();", line);
		line = it.next();
		assertEquals("\top EString type();", line);
		line = it.next();
		assertEquals("\top EString approve(EString name, EBoolean admin);", line);
		it.next();
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