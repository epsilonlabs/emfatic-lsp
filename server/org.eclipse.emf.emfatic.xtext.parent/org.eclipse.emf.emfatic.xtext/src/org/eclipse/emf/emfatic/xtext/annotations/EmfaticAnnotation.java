package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;

/**
 * Interface for providing Annotation information to the Emfatic editor.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public interface EmfaticAnnotation {
	
	/**
	 * The annotation source, typically used to store a URI representing the type of the annotation.
	 * @return
	 */
	String source();
	
	/**
	 * Short labels to be used to map to the source attribute 
	 * @return
	 */
	String label();
	
	/**
	 * True if the annotation's key is valid for the provided ECLass
	 * @param name	the key name
	 * @param target	the EClass (from {@link EmfaticPackage} to which the annotation has been added
	 * @return true if the key can be used for that EClass
	 */
	boolean isValidKey(String name, EClass target);
	
	/**
	 * All keys provided by this annotation for the given EClass
	 * @param eClass the EClass (from {@link EmfaticPackage})
	 * @return the list of keys that can be used for the given EClass
	 */
	List<String> keysFor(EClass eClass);
	

}
