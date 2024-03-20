/*
 * generated by Xtext 2.28.0
 */
package org.eclipse.emf.emfatic.xtext.scoping;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundDataTypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Reference;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
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
		if (reference == EmfaticPackage.Literals.BOUND_DATA_TYPE_WITH_MULTI__BOUND) {
			ClassDecl cls = null;
			if (context instanceof BoundDataTypeWithMulti) {
				cls = (ClassDecl)context.eContainer().eContainer().eContainer().eContainer();
			} else if (context instanceof Attribute) {
				cls = (ClassDecl)context.eContainer().eContainer().eContainer();
			}
			// DataTypes and TypeParameters
			List<TypeParam> candidates = cls.getTypeParamsInfo() == null ? 
					new ArrayList<>() : cls.getTypeParamsInfo().getTp();
			return Scopes.scopeFor(candidates, new FilteringScope(
					super.getScope(context, reference),
					(e) -> isSubType(e.getEClass(), EmfaticPackage.Literals.DATA_TYPE_DECL)));
		}
		if (reference == EmfaticPackage.Literals.BOUND_CLASS_EXCEPT_WILDCARD__BOUND) {
			if (context instanceof ClassDecl) {
				// Classes
				return new FilteringScope(
						super.getScope(context, reference),
						e -> isSubType(e.getEClass(), EmfaticPackage.Literals.CLASS_DECL)
								&& !Objects.equal(e.getEObjectOrProxy(), context));
			} else if (context instanceof BoundClassExceptWildcard || context instanceof Reference) {
				// Classes and TypeParameters
				var owner = context.eContainer();
				while (!(owner instanceof ClassDecl)) {
					owner = owner.eContainer();
				}
				var cls = (ClassDecl)owner;
				List<TypeParam> candidates = cls.getTypeParamsInfo() == null ? 
						new ArrayList<>() : cls.getTypeParamsInfo().getTp();
				return Scopes.scopeFor(candidates, new FilteringScope(
						super.getScope(context, reference),
						e -> isSubType(e.getEClass(), EmfaticPackage.Literals.CLASS_DECL)));
			}
		}
		if (reference == EmfaticPackage.Literals.BOUND_CLASSIFIER_EXCEPT_WILDCARD__BOUND) {
			// Classifiers
			return new FilteringScope(
					super.getScope(context, reference),
					(e) -> isSubType(e.getEClass(), EmfaticPackage.Literals.CLASSIFIER_DECL)
						|| isSubType(e.getEClass(), EmfaticPackage.Literals.TYPE_PARAM));
			
		}
		if (context	instanceof Reference && reference == EmfaticPackage.Literals.REFERENCE__OPPOSITE) {
			var emfRef = (Reference) context;
			var target = emfRef.getTypeWithMulti().getType().getBound();
			if (target instanceof ClassDecl) {
				ClassDecl contextEClass = (ClassDecl) emfRef.eContainer().eContainer().eContainer();
				var candidates = ((ClassDecl) target).getMembers().stream()
					.filter(m -> m.getMember() instanceof FeatureDecl 
							&& ((FeatureDecl)m.getMember()).getFeature() instanceof Reference)
					.map(m -> (Reference)((FeatureDecl) m.getMember()).getFeature())
					.filter(f -> f.getTypeWithMulti().getType().getBound().equals(contextEClass))
					.toList();
				return Scopes.scopeFor(candidates);
			}
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
