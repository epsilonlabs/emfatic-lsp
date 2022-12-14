package org.eclipse.emf.emfatic.xtext.naming;

import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.Declaration;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

public class EmfaticDQNP extends DefaultDeclarativeQualifiedNameProvider {
	
	
	/**
	 * Declarations are qualified by the package
	 */
	protected QualifiedName qualifiedName(Declaration ele){
		TopLevelDecl tld = (TopLevelDecl) ele.eContainer();
		CompUnit unit = (CompUnit) tld.eContainer();
		PackageDecl p = unit.getPackage();
		return QualifiedName.create(p.getName(), ele.getName());
	}
	

}
