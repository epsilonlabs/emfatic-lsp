package org.eclipse.emf.emfatic.xtext.ecore.tests;

import java.util.Map;

import org.eclipse.emf.emfatic.xtext.ecore.Elements;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;

import com.google.inject.Inject;

public abstract class StructureTest {

	@Inject
	EmfaticImport importer;
	
	Map<Object, Object> process(CompUnit result) {
		var creator = new Elements(this.importer);
		return creator.copy(result);
	}

}
