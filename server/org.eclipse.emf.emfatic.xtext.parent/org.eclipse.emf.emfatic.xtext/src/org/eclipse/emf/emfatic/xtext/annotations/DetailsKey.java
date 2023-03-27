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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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
		return this.targets.stream()
				.anyMatch(this.strictMatch(eClass)
						.or(this.subClass(eClass)));		
	}
	
	public String name() {
		return this.name;
	}
	
	private final String name;
	private final List<EClass> targets;
	
	private Predicate<EClass> strictMatch(EClass eClass) {
		return t -> Objects.equals(t, eClass);
	}
	
	private Predicate<EClass> subClass(EClass eClass) {
		return t -> eClass.getEAllSuperTypes().stream()
				.anyMatch(this.strictMatch(t));
	}

}
