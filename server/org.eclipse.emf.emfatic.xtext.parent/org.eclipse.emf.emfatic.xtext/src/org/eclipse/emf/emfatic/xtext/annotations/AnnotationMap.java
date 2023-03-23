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
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

/**
 * An umbrella for provided {@link EmfaticAnnotation}s.
 * @author horacio
 *
 */
public interface AnnotationMap {
	
	public static final String EMFATIC_ANNOTATION_EXTENSION_POINT = "org.eclipse.emf.emfatic.xtext.annotations";

	/**
	 * Add a user defined annotation (via emfatic source)
	 * @param annt the annotation to add
	 */
	void addAnnotation(Annotation annt);
	
	/**
	 * Add a framework annotation. It can be the emfatic predefined ones, or user provided via
	 * extension point.
	 * @param annt the annotation to add.
	 */
	void addAnnotation(EmfaticAnnotation annt);

	/**
	 * The list of all available labels (user and provided annotations)
	 * @return
	 */
	List<String> labels();
	
	/**
	 * True if the annotation's key is valid for the provided ECLass. The annotation is found using
	 * the provided label.
	 * 
	 * @param label	the {@link EmfaticAnnotation} label
	 * @param name	the key name
	 * @param target	the EClass (from {@link EmfaticPackage} to which the annotation has been added
	 * @return true if the key for the annotation can be used for that EClass
	 */
	boolean isValidKey(String label, String name, EClass target);

	/**
	 * All keys provided by the annotation identified by the label for the given EClass
	 * 
	 * @param label	the {@link EmfaticAnnotation} label
	 * @param eClass the EClass (from {@link EmfaticPackage})
	 * @return the list of keys that can be used for the given EClass
	 */
	List<String> keysFor(String label, EClass eClass);
	
	
	/**
	 * The resource this AnnotationMap is used for.
	 * @param resource
	 */
	void setResource(Resource resource);

}