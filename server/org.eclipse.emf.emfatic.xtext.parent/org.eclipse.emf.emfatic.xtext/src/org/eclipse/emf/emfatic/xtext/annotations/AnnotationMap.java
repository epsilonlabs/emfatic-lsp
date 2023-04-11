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
import java.util.NoSuchElementException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

/**
 * An umbrella for provided {@link EmfaticAnnotation}s.
 * @author Horacio Hoyos Rodriguez
 *
 */
public interface AnnotationMap {
	
	public static final String EMFATIC_ANNOTATION_EXTENSION_POINT = "org.eclipse.emf.emfatic.xtext.annotations";

	/**
	 * Add a user defined annotation (via emfatic source)
	 * @param list the annotations to add
	 * @param resource the Resource that adds the annotations
	 */
	void refreshAnnotations(EList<Annotation> list, Resource resource);

	/**
	 * The list of all available labels (user and provided annotations)
	 * 
	 * @param resource the Resoruce for which labels are retrieved
	 * @return the list of labels
	 */
	List<String> labels(Resource resource);
	
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
	 * Get the Emfatic annotation label mapped to the provided URI, in the given Resource
	 *
	 * @param uri the URI
	 * @param resource the Resoruce in which the URI is searched
	 * @return the label if registered.
	 * @throws NoSuchElementException if an annotation with the URI does not exist.
	 */
	String labelForUri(String uri, Resource resource) throws NoSuchElementException;

	/**
	 * Cehck if the provided label matches the label of any of the user or provided annotations
	 * @param label	the label to test
	 * @param resource the Resoruce in which the label is searched
	 * @return true, if the label is known
	 */
	boolean knowsLabel(String label, Resource resource);


}