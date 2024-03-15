package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute;
import org.eclipse.emf.emfatic.xtext.emfatic.BoolExpr;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.CharExpr;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.IntExpr;
import org.eclipse.emf.emfatic.xtext.emfatic.MapEntryDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.StringExpr;
import org.eclipse.emf.emfatic.xtext.emfatic.SubPackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
import org.eclipse.emf.emfatic.xtext.emfatic.util.EmfaticSwitch;
import org.eclipse.xtext.util.OnChangeEvictingCache;

import com.google.inject.Inject;

public class Content extends EmfaticSwitch<Object> {
	
	public Content(OnChangeEvictingCache cache) {
		this.cache = cache;
	}

	@Override
	public Object caseCompUnit(CompUnit source) {
		if (source.getPackage() == null) {
			return null;
		}
		EPackage target = (EPackage) this.doSwitch(source.getPackage());
		for (var d : source.getDeclarations()) {
			var dTarget = this.doSwitch(d.getDeclaration());
			if (dTarget instanceof EPackage) {
				target.getESubpackages().add((EPackage) dTarget);
			} else if (dTarget instanceof EClassifier) {
				target.getEClassifiers().add((EClassifier) dTarget);
			}
			processAnnotations((EModelElement) dTarget, d);
		}
		return target;
	}

	@Override
	public Object casePackageDecl(PackageDecl source) {
		EPackage target = equivalent(source);
		target.setName(source.getName());
		if (source.getNs() != null) {
			target.setNsPrefix(source.getNs().getPrefix());
			target.setNsURI(source.getNs().getUri());
		}
		processAnnotations(target, source);
		return target;
	}
	
	@Override
	public Object caseSubPackageDecl(SubPackageDecl source) {
		EPackage target = equivalent(source);
		for (var d : source.getDeclarations()) {
			var dTarget = this.doSwitch(d);
			if (dTarget instanceof EPackage) {
				target.getESubpackages().add((EPackage) dTarget);
			} else {
				target.getEClassifiers().add((EClassifier) dTarget);
			}
			processAnnotations((EModelElement) dTarget, d);
		}
		return target;
	}
	
	@Override
	public Object caseMapEntryDecl(MapEntryDecl source) {
		EClass target = equivalent(source);
		TypeWithMultiCopier tc = ((TypeWithMultiCopier) equivalent(source.getKey())).load(this.cache);
		var key = tc.toEClass() ?  EcoreFactory.eINSTANCE.createEReference() : EcoreFactory.eINSTANCE.createEAttribute();
		key.setName("key");
		tc.configure(key);
		target.getEStructuralFeatures().add(key);
		tc = ((TypeWithMultiCopier) equivalent(source.getValue())).load(this.cache);
		var value = tc.toEClass() ?  EcoreFactory.eINSTANCE.createEReference() : EcoreFactory.eINSTANCE.createEAttribute();
		value.setName("value");
		tc.configure(value);
		target.getEStructuralFeatures().add(value);
		target.setInstanceClassName("java.util.Map$Entry");
		target.setName(source.getName());
		mapEntries.add(target);
		return target;
	}

	@Override
	public Object caseClassDecl(ClassDecl source) {
		EClass target = equivalent(source);
		target.setName(source.getName());
		target.setAbstract(source.isAbstract());
		target.setInterface(source.getKind().isInterface());
		if (source.getTypeParamsInfo() != null) {
			for (TypeParam tp : source.getTypeParamsInfo().getTp()) {
				ETypeParameter targetTp = equivalent(tp);
				if (tp.getTypeBoundsInfo() != null) {
					for (BoundClassifierExceptWildcard tb : tp.getTypeBoundsInfo().getTb()) {
						var gt = EcoreFactory.eINSTANCE.createEGenericType();
						BoundClassifierExceptWildcardCopier cp = equivalent(tb);
						cp.load(this.cache)
							.configure(gt);
						targetTp.getEBounds().add(gt);
					}
				}
				targetTp.setName(tp.getName());
				target.getETypeParameters().add(targetTp);
			}
		}
		for (BoundClassExceptWildcard st : source.getSuperTypes()) {
			BoundClassExceptWildcardCopier cp = equivalent(st);
			cp.load(this.cache).configure(target);
		}
		return target;
	}
	
	@Override
	public Object caseFeatureDecl(FeatureDecl source) {
		EStructuralFeature target = (EStructuralFeature) this.doSwitch(source.getFeature());
		target.setChangeable(source.isReadonly());
		target.setVolatile(source.isVolatile());
		target.setTransient(source.isTransient());
		target.setUnsettable(source.isUnsettable());
		target.setDerived(source.isDerived());
		target.setUnique(source.isUnique());
		target.setOrdered(source.isOrdered());
		if (target instanceof EReference) {
			((EReference) target).setResolveProxies(source.isResolve());
		}
		if (target instanceof EAttribute) {
			((EAttribute) target).setID(source.isId());
		}
		return target;
	}

	@Override
	public Object caseAttribute(Attribute source) {
		EAttribute target = equivalent(source);
		BoundDataTypeWithMultiCopier type = equivalent(source.getTypeWithMulti());
		type.configure(target);
		target.setName(source.getName()); 
		if (source.getDefValue() != null) {
			switch(source.getDefValue().eClass().getClassifierID()) {
			case EmfaticPackage.BOOL_EXPR:
				BoolExpr boolExpr = (BoolExpr)source.getDefValue();
				target.setDefaultValueLiteral(boolExpr.getValue());
				break;
			case EmfaticPackage.STRING_EXPR:
				StringExpr stringExpr = (StringExpr)source.getDefValue();
				target.setDefaultValueLiteral(stringExpr.getValue());
				break;
			case EmfaticPackage.CHAR_EXPR:
				CharExpr charExpr = (CharExpr)source.getDefValue();
				target.setDefaultValueLiteral(charExpr.getValue());
				break;
			case EmfaticPackage.INT_EXPR:
				IntExpr intExpr = (IntExpr)source.getDefValue();
				int value = intExpr.getValue();
				if (intExpr.isNegative()) {
					value *= -1;
				}
				target.setDefaultValueLiteral(Integer.toString(value));
				break;
			}
		}
		return target;
	}


	@Override
	public Object caseDataTypeDecl(DataTypeDecl source) {
		EDataType target = equivalent(source);
		target.setName(source.getName());
		if (source.getInstanceClassName().getLiteral() != null) {
			target.setInstanceClassName(source.getInstanceClassName().getLiteral());			
		} else {
			target.setInstanceClassName(source.getInstanceClassName().getId());
		}
		return target;
	}

	@Inject
	private AnnotationMap annotations;
	private final OnChangeEvictingCache cache;
	
	private Set<EClass> mapEntries = new HashSet<>();

	private void processAnnotations(EModelElement target, PackageDecl source) {
		for (Annotation annt : source.getAnnotations()) {
			target.getEAnnotations().add(processAnnotation(annt, source.eResource()));
		}
	}
	
	private void processAnnotations(EModelElement target, TopLevelDecl source) {
		for (Annotation annt : source.getAnnotations()) {
			target.getEAnnotations().add(processAnnotation(annt, source.eResource()));
		}
	}

	private EAnnotation processAnnotation(Annotation annt, Resource eResource) {
		EAnnotation emfAnnt = equivalent(annt);
		if (annt.getSource().getId() == null) {
			emfAnnt.setSource(annt.getSource().getLiteral());
		} else {
			emfAnnt.setSource(this.annotations.uriForLabel(annt.getSource().getId(), eResource));
		}
		return emfAnnt;
	}


	private <T> T equivalent(EObject source) {
		var target = this.cache.get(source, source.eResource(), () -> (T) null);
		if (target == null) {
			throw new IllegalArgumentException("Target element not found for " + source);
		}
		return (T) target;
	}
	

}
