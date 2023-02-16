package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;


/**
 * The Details key represents the <code>key</code> of a <code>MapEntry<code> of an EAnnotation's
 * details.
 * 
 * In order to provide content assist and validation capabilities to the Emfatic editor, the 
 * specific EClass in which the key should be used can be provided.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class DetailsKey {
	
	
	public DetailsKey(String name) {
		this(name, null);
	}
	
	/**
	 * The {@link EmfaticPackage} EClasses in which the key can be used. If empty, the key can be
	 * used everywhere. Note that the specific class is used for validation/content assist, i.e.,
	 * subtypes of the EClass are not checked.
	 * 
	 * @param name		the key name
	 * @param targets	the target EClasses
	 */
	public DetailsKey(String name, EClass[] targets) {
		super(); 
		this.name = name;
		this.targets = targets == null ?  new ArrayList<>() :  Arrays.asList(targets);
	}
	
	boolean appliesTo(final EClass eClass) {
		if (this.targets.isEmpty()) {
			return true;
		}
		return this.targets.stream().anyMatch(t -> Objects.equals(t, eClass));		
	}
	
	public String name() {
		return this.name;
	}
	
	private final String name;
	private final List<EClass> targets;

}
