/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.scoping;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.xtext.naming.QualifiedName;

import com.google.common.base.Objects;

/**
 * The Class Aliases helps manage alias information.
 */
public class Aliases {
	
	
	/**
	 * Adds the alias.
	 *
	 * @param original the original
	 * @param alias the alias
	 */
	public void addAlias(String original, String alias) {
		this.alias2orig.put(alias, original);
		this.orig2alias.put(original, alias);
	}
	
	/**
	 * Checks if the alias namesapce has an original.
	 *
	 * @param original the original
	 * @return true, if successful
	 */
	public boolean hasOriginal(String original) {
		return this.orig2alias.containsKey(original);
	}
	
	/**
	 * Checks if the original namesapce has an alias.
	 *
	 * @param alias the alias
	 * @return true, if successful
	 */
	public boolean hasAlias(String alias) {
		return this.alias2orig.containsKey(alias);
	}
	
	/**
	 * Gets the alias for the namespace.
	 *
	 * @param original the original
	 * @return the alias
	 */
	public String getAlias(String original) {
		return this.orig2alias.get(original);
	}
	
	/**
	 * Gets the orginal for the namespace.
	 *
	 * @param alias the alias
	 * @return the orginal
	 */
	public String getOrginal(String alias) {
		return this.alias2orig.get(alias);
	}
	
	/**
	 * Returns true if the original namespace has an alias, and the alias is different that itself.
	 *
	 * @param original the original
	 * @return true, if is aliased
	 */
	public boolean isAliased(String original) {
		return this.hasOriginal(original) && !Objects.equal(original, this.getAlias(original));
	}
	
	/**
	 * Checks if the qualified name's namespace is imported
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean hasImportedNamespace(QualifiedName name) {
		return alias2orig.keySet().stream()
				.anyMatch(ns -> name.startsWith(QualifiedName.create(ns)));
	}
	
	
	private final Map<String, String> orig2alias = new HashMap<>();
	private final Map<String, String> alias2orig = new HashMap<>();
	

}
