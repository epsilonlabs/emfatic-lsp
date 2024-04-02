package org.eclipse.emf.emfatic.xtext.ecore.tests;

import java.util.Map;

import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.ecore.Content;
import org.eclipse.emf.emfatic.xtext.ecore.Elements;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;

import com.google.inject.Inject;

public abstract class ContentTest {
	
	@Inject
	EmfaticImport importer;
	
	@Inject
	AnnotationMap annotations;
	
	Map<Object, Object>  process(CompUnit result) {
		var structure = new Elements(importer);
		var cache = structure.copy(result);
		var content = new Content(this.annotations);
		return content.copy(cache, result);
	}
}
