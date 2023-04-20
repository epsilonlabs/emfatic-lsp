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
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.xtext.EcoreUtil2;


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
		this.targets = targets == null ?  
				new ArrayList<>() :  
				Arrays.stream(targets).map((t) -> new TargetEClass(t)).collect(Collectors.toList());
	}
	
	/**
	 * Check if this key applies to the given ECLass.
	 * @param eClass
	 * @return
	 */
	boolean appliesTo(final EClass eClass) {
		if (this.targets.isEmpty()) {
			return true;
		}
		return this.targets.stream()
				.anyMatch(t -> t.isAssignableTo(eClass));			
	}
	
	public String name() {
		return this.name;
	}
	
	private static final Logger LOG = Logger.getLogger(DetailsKey.class);
	
	private final String name;
	private final List<TargetEClass> targets;
	
	/**
	 * Instances of EClasses provided by EmfaticAnnotation implementations might not use the same
	 * java object instances as the parser. Thus, we need a custom equality checker that uses the
	 * EPackage URI and the EClas name.
	 * 
	 * @author Horacio Hoyos Rodriguez
	 *
	 */
	private class TargetEClass {

		public TargetEClass(EClass eClass) {
			super();
			this.name = eClass.getName();
			this.nsURI = eClass.getEPackage().getNsURI();
		}
		
		/**
		 * Check whether this EClass is the same as, or a super type of the subType.
		 * 
		 * @param subType the sub-type
		 * @return true, if this EClass is the same as, or a super type of, the sub class. 
		 * 	Returns false if the superType is null.
		 */
		public boolean isAssignableTo(EClass subType) {
			if (subType == null) {
				return false;
			}
			if (!this.nsURI.equals(subType.getEPackage().getNsURI())) {
				LOG.debug("SuperType is not in the same ePackage");
				return false;
			}
			EClassifier target = subType.getEPackage().getEClassifier(this.name);
			if (!(target instanceof EClass)) {
				LOG.debug("The name " + this.name + " is not for an EClass.");
				return false;
			}
			return EcoreUtil2.isAssignableFrom((EClass) target, subType);
		}
		
		private final String name;
		private final String nsURI;
		
	}
	

}
