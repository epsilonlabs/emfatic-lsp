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

public abstract class BaseAnnotation implements EmfaticAnnotation {
	
	public BaseAnnotation() {
		super();
	}

	@Override
	public boolean isValidKey(String name, EClass target) {
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

//	@Override
//	public List<String> keys() {
//		createKeys();
//		return this.keyMap.keySet().stream()
//				.sorted()
//				.collect(Collectors.toList());
//	}

	
	
	protected final Map<String, DetailsKey> keyMap = new HashMap<>();
	
	protected abstract void createKeys();
	
	protected void addKey(DetailsKey key) {
		this.keyMap.put(key.name(), key);
	}

}