package org.eclipse.emf.emfatic.xtext.ecore.tests;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.ecore.Structure;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

import com.google.inject.Inject;

public abstract class StructureTest {

	@Inject
	OnChangeEvictingCache cache;

	@Inject
	EmfaticImport importer;
	
	Object process(EObject result) {
		var creator = new Structure(cache, importer);
		return creator.doSwitch(result);
	}

}
