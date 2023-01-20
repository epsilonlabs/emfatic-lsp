package org.eclipse.emf.emfatic.xtext.scoping;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.xtext.naming.QualifiedName;

public class Aliases {
	
	
	public void addAlias(String original, String alias) {
		this.alias2orig.put(alias, original);
		this.orig2alias.put(original, alias);
	}
	
	public boolean hasOriginal(String original) {
		return this.orig2alias.containsKey(original);
	}
	
	public boolean hasAlias(String alias) {
		return this.alias2orig.containsKey(alias);
	}
	
	public String getAlias(String original) {
		return this.orig2alias.get(original);
	}
	
	public String getOrginal(String alias) {
		return this.alias2orig.get(alias);
	}
	
	public boolean hasImportedNamespace(QualifiedName name) {
		return alias2orig.keySet().stream()
				.anyMatch(ns -> name.startsWith(QualifiedName.create(ns)));
	}
	
	
	private final Map<String, String> orig2alias = new HashMap<>();
	private final Map<String, String> alias2orig = new HashMap<>();
	

}
