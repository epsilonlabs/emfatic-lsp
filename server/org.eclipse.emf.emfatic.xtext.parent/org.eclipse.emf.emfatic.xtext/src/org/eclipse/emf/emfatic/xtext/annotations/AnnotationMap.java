package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;

/**
 * EmfaticAnnotationMap annotations are only valid in the package declaration
 * @author horacio
 *
 */
public interface AnnotationMap {

	
	void addAnnotation(Annotation annt);
	
	void addAnnotation(EmfaticAnnotation annt);

	EmfaticAnnotation removeAnnotation(String label);
	
	List<String> labels();

	void setResource(Resource resource);

	

}