package org.eclipse.emf.emfatic.xtext.naming;

import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.Declaration;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.eclipse.xtext.util.Tuples;

import com.google.inject.Inject;

/**
 * The Emfatic {@code IQualifiedNameProvider} only provides qualified names for
 * the elements that can be cross-referenced: Packages, SubPackages, Classifiers
 * and MapEntries.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class EmfaticDQNP extends IQualifiedNameProvider.AbstractImpl {

	@Override
	public QualifiedName getFullyQualifiedName(EObject obj) {
		return cache.get(Tuples.pair(obj, "fqn"), obj.eResource(), ()->computeFullyQualifiedName(obj));
	}
	
	
	/**
	 * Declarations (SubPackages, Classifiers and MapEntries) are qualified by the package
	 */
	protected QualifiedName qualifiedName(Declaration ele){
		TopLevelDecl tld = (TopLevelDecl) ele.eContainer();
		if (tld == null) {
			return null;
		}
		CompUnit unit = (CompUnit) tld.eContainer();
		if (unit == null) {
			return null;
		}
		PackageDecl p = unit.getPackage();
		if (p == null) {
			return null;
		}
		return QualifiedName.create(p.getName(), ele.getName());
	}
	
	
	/**
	 * The CompUnit is qualified by its Package
	 * @param ele the {@link CompUnit}
	 * @return the name of the CompUnit package
	 */
	protected QualifiedName qualifiedName(CompUnit ele){
		PackageDecl p = ele.getPackage();
		return QualifiedName.create(p.getName());
	}
	
	private QualifiedName computeFullyQualifiedName(EObject obj) {
		return qualifiedName.invoke(obj);
	}

	@Inject
	private IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;
	
	private PolymorphicDispatcher<QualifiedName> qualifiedName = 
			new PolymorphicDispatcher<QualifiedName>(
					"qualifiedName",
					1,
					1,
					Collections.singletonList(this), 
					PolymorphicDispatcher.NullErrorHandler.<QualifiedName>get())
		{
			@Override
			protected QualifiedName handleNoSuchMethod(Object... params) { 
				return null;
			}
		}; 
	

}
