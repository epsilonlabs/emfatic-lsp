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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
		System.out.println(output);
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
		String output = this.fsa.getTextFiles().get("/test.emf").toString();
		System.out.println(output);
		var result = output.split("\\r?\\n");
		var it = Arrays.asList(result).subList(11, result.length).iterator();
		var line = it.next();
		assertEquals("class SCLType extends TBaseElement, TLN0 {", line);
		line = it.next();
		line = it.next();
		assertEquals("}", line);
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
	
	@Inject
	private EcoreGenerator generator;
	
	private final InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
	
}