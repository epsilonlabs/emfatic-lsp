package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.MapEntryDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.SubPackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.util.EmfaticSwitch;
import org.eclipse.xtext.util.OnChangeEvictingCache;

import com.google.inject.Inject;

public class Copier extends EmfaticSwitch<Object> {
	
	public Copier(OnChangeEvictingCache cache) {
		this.cache = cache;
	}

	@Override
	public Object caseCompUnit(CompUnit source) {
		if (source.getPackage() == null) {
			return null;
		}
		EPackage target = (EPackage) this.doSwitch(source.getPackage());
		for (var d : source.getDeclarations()) {
			var dTarget = this.doSwitch(d.getDeclaration());
			if (dTarget instanceof EPackage) {
				target.getESubpackages().add((EPackage) dTarget);
			} else if (dTarget instanceof EClassifier) {
				target.getEClassifiers().add((EClassifier) dTarget);
			}
			processAnnotations((EModelElement) dTarget, d);
		}
		return target;
	}

	@Override
	public Object casePackageDecl(PackageDecl source) {
		EPackage target = equivalent(source);
		target.setName(source.getName());
		if (source.getNs() != null) {
			target.setNsPrefix(source.getNs().getPrefix());
			target.setNsURI(source.getNs().getUri());
		}
		processAnnotations(target, source);
		return target;
	}
	
	@Override
	public Object caseSubPackageDecl(SubPackageDecl source) {
		EPackage target = equivalent(source);
		for (var d : source.getDeclarations()) {
			var dTarget = this.doSwitch(d);
			if (dTarget instanceof EPackage) {
				target.getESubpackages().add((EPackage) dTarget);
			} else {
				target.getEClassifiers().add((EClassifier) dTarget);
			}
			processAnnotations((EModelElement) dTarget, d);
		}
		return target;
	}
	
	@Override
	public Object caseMapEntryDecl(MapEntryDecl source) {
		EClass target = equivalent(source);
		var key = EcoreFactory.eINSTANCE.createEAttribute();
		key.setName("key");
		TypeCopier tc = equivalent(source.getKey());
		tc.load(this.cache)
			.configure(key);
		target.getEStructuralFeatures().add(key);
		var value = EcoreFactory.eINSTANCE.createEAttribute();
		value.setName("value");
		tc = equivalent(source.getValue());
		tc.load(this.cache)
				.configure(value);
		target.getEStructuralFeatures().add(value);
		target.setInstanceClassName("java.util.Map$Entry");
		target.setName(source.getName());
		mapEntries.add(target);
		return target;
	}

	@Override
	public Object caseClassDecl(ClassDecl source) {
		EClass target = equivalent(source);
		target.setAbstract(source.isAbstract());
		return target;
	}

	@Inject
	private AnnotationMap annotations;
	private final OnChangeEvictingCache cache;
	
	private Set<EClass> mapEntries = new HashSet<>();

	private void processAnnotations(EModelElement target, PackageDecl source) {
		for (Annotation annt : source.getAnnotations()) {
			target.getEAnnotations().add(processAnnotation(annt, source.eResource()));
		}
	}
	
	private void processAnnotations(EModelElement target, TopLevelDecl source) {
		for (Annotation annt : source.getAnnotations()) {
			target.getEAnnotations().add(processAnnotation(annt, source.eResource()));
		}
	}

	private EAnnotation processAnnotation(Annotation annt, Resource eResource) {
		EAnnotation emfAnnt = equivalent(annt);
		if (annt.getSource().getId() == null) {
			emfAnnt.setSource(annt.getSource().getLiteral());
		} else {
			emfAnnt.setSource(this.annotations.uriForLabel(annt.getSource().getId(), eResource));
		}
		return emfAnnt;
	}


	private <T> T equivalent(EObject source) {
		var target = this.cache.get(source, source.eResource(), () -> (T) null);
		if (target == null) {
			throw new IllegalArgumentException("Target element not found for " + source);
		}
		return (T) target;
	}
	

}