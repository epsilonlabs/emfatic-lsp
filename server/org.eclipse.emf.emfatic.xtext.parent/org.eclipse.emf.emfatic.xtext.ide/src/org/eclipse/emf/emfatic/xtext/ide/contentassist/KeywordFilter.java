package org.eclipse.emf.emfatic.xtext.ide.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.util.EmfaticSwitch;

public class KeywordFilter extends EmfaticSwitch<Boolean> {

	public KeywordFilter(String value) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean caseTopLevelDecl(TopLevelDecl object) {
		return !object.getAnnotations().isEmpty();
	}

	@Override
	public Boolean defaultCase(EObject object) {
		return true;
	}
	
	

}
