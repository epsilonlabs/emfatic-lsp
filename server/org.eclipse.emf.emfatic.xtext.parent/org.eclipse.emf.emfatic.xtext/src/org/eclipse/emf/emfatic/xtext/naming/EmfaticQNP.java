/*******************************************************************************
 * Copyright (c) 2022 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
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
public class EmfaticQNP extends IQualifiedNameProvider.AbstractImpl {

	/**
	 * Delegates to {@link #computeFullyQualifiedName(EObject)} and caches the
	 * result.
	 */
	@Override
	public QualifiedName getFullyQualifiedName(EObject obj) {
		return cache.get(Tuples.pair(obj, "fqn"), obj.eResource(), ()->computeFullyQualifiedName(obj));
	}
	
	
	/**
	 * Declarations (SubPackages, Classifiers and MapEntries) are qualified by the package.
	 *
	 * @param ele the ele
	 * @return the qualified name
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
	 * The CompUnit is qualified by its Package.
	 * 
	 * @param ele the {@link CompUnit}
	 * @return the name of the CompUnit package
	 */
	protected QualifiedName qualifiedName(CompUnit ele){
		PackageDecl p = ele.getPackage();
		return QualifiedName.create(p.getName());
	}
	
	/**
	 * Compute fully qualified name by invoking the a {@code PolymorphicDispatcher}
	 * on this element
	 *
	 * @param obj the obj
	 * @return the qualified name
	 */
	private QualifiedName computeFullyQualifiedName(EObject obj) {
		return qualifiedName.invoke(obj);
	}

	/** The cache. */
	@Inject
	private IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;
	
	/** The qualified name polymorphic provider. */
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
