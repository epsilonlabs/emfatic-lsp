/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
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