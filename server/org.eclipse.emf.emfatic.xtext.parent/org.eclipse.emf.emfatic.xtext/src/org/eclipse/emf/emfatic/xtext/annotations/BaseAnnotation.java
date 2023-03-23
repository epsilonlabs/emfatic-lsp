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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;

/**
 * Base annotation that can be used to quickly provide additional annotations to Emfatic editor.
 * @author horacio
 *
 */
public abstract class BaseAnnotation implements EmfaticAnnotation {
	
	public BaseAnnotation() {
		super();
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
		createKeys();
		if (this.keyMap.containsKey(name)) {
			return this.keyMap.get(name).appliesTo(target);
		}
		return false;
	}

	@Override
	public List<String> keysFor(EClass eClass) {
		createKeys();
		return this.keyMap.entrySet().stream()
				.filter(k -> k.getValue().appliesTo(eClass))
				.map(k -> k.getKey())
				.collect(Collectors.toList());
	}

	protected final Map<String, DetailsKey> keyMap = new HashMap<>();
	
	/**
	 * Extending classes must implement this method to populate the valid keys. We recommend using
	 * the {@link #addKey(DetailsKey)} method via:
	 * <code>
	 * 	addKey(new DetailsKey("someKey", [... target EClasses ...]));
	 * </code>
	 * where the target EClasses can be omitted to apply to key to all possible EClasses.
	 */
	protected abstract void doCreateKeys();
	
	protected void addKey(DetailsKey key) {
		this.keyMap.put(key.name(), key);
	}
	
	private void createKeys() {
		if (this.keyMap.isEmpty()) {
			this.doCreateKeys();
		}
		
	}

}