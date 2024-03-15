package org.eclipse.emf.emfatic.xtext.ecore.tests;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.ecore.Content;
import org.eclipse.emf.emfatic.xtext.ecore.Structure;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

import com.google.inject.Inject;

public abstract class ContentTest {

	@Inject
	OnChangeEvictingCache cache;

	@Inject
	EmfaticImport importer;
	
	protected Object process(EObject result) {
		var structure = new Structure(cache, importer);
		structure.doSwitch(result);
		var content = new Content(this.cache);
		return content.doSwitch(result);
	}
}
