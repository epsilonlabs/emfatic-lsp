/*
 * generated by Xtext 2.28.0
 */
package org.eclipse.emf.emfatic.xtext.scoping;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.Reference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.FilteringScope;

import com.google.common.base.Objects;

/**
 * This class contains custom scoping description in order to filter the EMF
 * elements provided by the global scope according to the grammar reference
 * being resolved.
 * 
 * @see EmfaticGSP
 */
public class EmfaticScopeProvider extends AbstractEmfaticScopeProvider {

	@Override
	public IScope getScope(
		EObject context,
		EReference reference) {
		if (reference == EmfaticPackage.Literals.DATA_TYPE_WITH_MULTI__TYPE) {
			// DataTypes
			return new FilteringScope(
					super.getScope(context, reference),
					(e) ->   isSubType(e.getEClass(), EmfaticPackage.Literals.DATA_TYPE_DECL));
		}
		if (reference == EmfaticPackage.Literals.BOUND_CLASS_EXCEPT_WILDCARD__BOUND) {
			// Classes
			if (context instanceof ClassDecl) {
				return new FilteringScope(
						super.getScope(context, reference),
						e -> isSubType(e.getEClass(), EmfaticPackage.Literals.CLASS_DECL)
								&& !Objects.equal(e.getEObjectOrProxy(), context));
			} else {
				return new FilteringScope(
						super.getScope(context, reference),
						e -> isSubType(e.getEClass(), EmfaticPackage.Literals.CLASS_DECL));
			}
			
			
		}
		if (reference == EmfaticPackage.Literals.BOUND_CLASSIFIER_EXCEPT_WILDCARD__BOUND) {
			// Classifiers
			return new FilteringScope(
					super.getScope(context, reference),
					(e) -> isSubType(e.getEClass(), EmfaticPackage.Literals.CLASSIFIER_DECL));
			
		}
		if (context	instanceof Reference && reference == EmfaticPackage.Literals.REFERENCE__OPPOSITE) {
			var emfRef = (Reference) context;
			var target = emfRef.getTypeWithMulti().getType().getBound();
			var candidates = target.getMembers().stream()
				.filter(m -> m.getMember() instanceof Reference)
				.map(m -> (Reference) m.getMember())
				.filter(f -> f.getTypeWithMulti().getType().getBound().equals(emfRef.eContainer().eContainer()))
				.toList();
			return Scopes.scopeFor(candidates);
		}
		return super.getScope(context, reference);
	}
	
	/**
	 * Checks if is sub type.
	 *
	 * @param type the type
	 * @param superType the super type
	 * @return true, if is sub type
	 */
	private boolean isSubType(
		EClass type,
		EClass superType) {
		return EcoreUtil2.isAssignableFrom(superType, type);
	}

	
}
