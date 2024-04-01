package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassifierDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeArg;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;

public class AbstractClassifierCopier<C extends EObject> {

	public AbstractClassifierCopier(
		C source,
		EClassifier targetEClassifier,
		ETypeParameter targetTypeParamter,
		List<TypeArgCopier> typeArgs,
		EmfaticImport emfaticImport) {
		super();
		this.source = source;
		this.targetEClassifier = targetEClassifier;
		this.targetTypeParamter = targetTypeParamter;
		this.targetTypeArgs = typeArgs;
		this.emfaticImport = emfaticImport;
	}
	
	protected EObject targetBound(Content content, EObject bound) {
		if (bound instanceof TypeParam) {
			return content.equivalent(bound);
		} else {	// ClassifierDecl
			EClassifier type;
			if (this.source.eResource() == bound.eResource()) {
				type =  content.equivalent(bound);
			} else {
				type = this.emfaticImport.export((ClassifierDecl)bound);
			}
			return type;
		}
	}

	protected List<TypeArgCopier> targetTypeArgs(Content content, EList<TypeArg> typeArgs) {
		List<TypeArgCopier> tArgs = new ArrayList<>();
		for (TypeArg ta : typeArgs) {
			TypeArgCopier taTarget = content.equivalent(ta);
			if (taTarget == null) {
				throw new IllegalArgumentException("Target element not found for " + ta);
			}
			tArgs.add(taTarget.load(content));
		}
		return tArgs;
	}

	protected final C source;
	protected final EClassifier targetEClassifier;
	protected final ETypeParameter targetTypeParamter;
	protected final List<TypeArgCopier> targetTypeArgs;
	protected final EmfaticImport emfaticImport;

}