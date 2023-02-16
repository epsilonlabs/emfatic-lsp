package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;

/**
 * EmfaticAnnotationMap annotations are only valid in the package declaration
 * @author horacio
 *
 */
public interface AnnotationMap {
	
	public static final String EMFATIC_ANNOTATION_EXTENSION_POINT = "org.eclipse.emf.emfatic.xtext.annotations";

	
	void addAnnotation(Annotation annt);
	
	void addAnnotation(EmfaticAnnotation annt);

	EmfaticAnnotation removeAnnotation(String label);
	
	List<String> labels();

	void setResource(Resource resource);
	
	List<String> keysFor(String label, EClass eClass);

	

}