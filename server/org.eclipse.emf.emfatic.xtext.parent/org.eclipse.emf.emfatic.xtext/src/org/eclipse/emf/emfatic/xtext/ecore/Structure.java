package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundDataTypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMemberDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassRefWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumLiteral;
import org.eclipse.emf.emfatic.xtext.emfatic.Expr;
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.MapEntryDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Multiplicity;
import org.eclipse.emf.emfatic.xtext.emfatic.Operation;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Param;
import org.eclipse.emf.emfatic.xtext.emfatic.Reference;
import org.eclipse.emf.emfatic.xtext.emfatic.ResultType;
import org.eclipse.emf.emfatic.xtext.emfatic.SubPackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.util.EmfaticSwitch;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class Structure extends EmfaticSwitch<Object> {
	
	public Structure(OnChangeEvictingCache cache, EmfaticImport emfaticImport) {
		this.cache = cache;
		this.emfaticImport = emfaticImport;
	}
	
	// Support null values;
	public Object doSwitch(EObject eObject) {
		if (eObject == null) {
			return null;
		}
	    return doSwitch(eObject.eClass(), eObject);
	}

	@Override
	public Object caseCompUnit(CompUnit source) {
		if (source.getPackage() == null) {
			return null;
		}
		Object target = this.doSwitch(source.getPackage());
		for (var d : source.getDeclarations()) {
			this.doSwitch(d);
		}
		return target;
	}

	@Override
	public Object casePackageDecl(PackageDecl source) {
		var target = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEPackage);
		return target;
	}
	
	@Override
	public Object caseTopLevelDecl(TopLevelDecl source) {
		this.doSwitch(source.getDeclaration());
		source.getAnnotations().forEach(this::doSwitch);
		return super.caseTopLevelDecl(source);
	}

	@Override
	public Object caseSubPackageDecl(SubPackageDecl source) {
		var target = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEPackage);
		source.getDeclarations().forEach(this::doSwitch);
		return target;
	}
	
	@Override
	public Object caseMapEntryDecl(MapEntryDecl source) {
		EClass target = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEClass);
		this.doSwitch(source.getKey());
		this.doSwitch(source.getValue());
		return target;
	}

	@Override
	public Object caseTypeWithMulti(TypeWithMulti source) {
		TypeWithMultiCopier target = this.cache.get(
				source, 
				source.eResource(), 
				() -> new TypeWithMultiCopier(source));
		this.doSwitch(source.getType());
		this.doSwitch(source.getMultiplicity());
		return target;
	}

	@Override
	public Object caseWildcard(Wildcard source) {
		WildcardCopier target = this.cache.get(source, source.eResource(), () -> new WildcardCopier(source));
		if (source.getBound() != null) {
			this.doSwitch(source.getBound());
		}
 		return target;
	}

	@Override
	public Object caseBoundClassifierExceptWildcard(BoundClassifierExceptWildcard source) {
		var target = this.cache.get(
				source, 
				source.eResource(), 
				() -> new BoundClassifierExceptWildcardCopier(source, this.emfaticImport));
		source.getTypeArgs().forEach(this::doSwitch);
		return target;
	}

	@Override
	public Object caseBoundClassExceptWildcard(BoundClassExceptWildcard source) {
		var target = this.cache.get(
				source, 
				source.eResource(), 
				() -> new BoundClassExceptWildcardCopier(source, this.emfaticImport));
		source.getTypeArgs().forEach(this::doSwitch);
		return target;
	}

	@Override
	public Object caseClassDecl(ClassDecl source) {
		var result = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEClass);
		if (source.getTypeParamsInfo() != null) {
			source.getTypeParamsInfo().getTp().forEach(this::doSwitch);
		}
		source.getSuperTypes().forEach(this::doSwitch);
		source.getMembers().forEach(this::doSwitch);
		return result;
	}

	@Override
	public Object caseClassMemberDecl(ClassMemberDecl source) {
		source.getAnnotations().forEach(this::doSwitch);
		this.doSwitch(source.getMember());
		return super.caseClassMemberDecl(source);
	}

	@Override
	public Object caseFeatureDecl(FeatureDecl source) {
		this.doSwitch(source.getFeature());
		return super.caseFeatureDecl(source);
	}

	@Override
	public Object caseOperation(Operation source) {
		var result = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEOperation);
		if (source.getTypeParamsInfo() != null) {
			source.getTypeParamsInfo().getTp().forEach(this::doSwitch);
		}
		this.doSwitch(source.getResType());
		source.getParams().forEach(this::doSwitch);
		source.getExceptions().forEach(this::doSwitch);
		return result;
	}
	
	@Override
	public Object caseResultType(ResultType source) {
		var result = this.cache.get(
				source, 
				source.eResource(),
				() -> new ResultTypeCopier(source));
		if (source.getType() != null) {
			this.doSwitch(source.getType());
		}
		return result;
	}

	@Override
	public Object caseParam(Param source) {
		source.getLeadingAnnotations().forEach(this::doSwitch);
		this.doSwitch(source.getTypeWithMulti());
		source.getTrailingAnnotations().forEach(this::doSwitch);
		return this.cache.get(
				source,
				source.eResource(),
				EcoreFactory.eINSTANCE::createEParameter);
	}

	@Override
	public Object caseAttribute(Attribute source) {
		EAttribute result = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEAttribute);
		this.doSwitch(source.getTypeWithMulti());
		this.doSwitch(source.getDefValue());
		return result;
	}
	
	@Override
	public Object caseExpr(Expr source) {
		return this.cache.get(
				source, 
				source.eResource(), 
				() -> new ExpressionCopier(source));
	}

	@Override
	public Object caseReference(Reference source) {
		EReference result = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEReference);
		this.doSwitch(source.getTypeWithMulti());
		return result;
	}
	
	@Override
	public Object caseClassRefWithMulti(ClassRefWithMulti source) {
		var result = this.cache.get(
				source, 
				source.eResource(),
				() -> new ClassRefWithMultiCopier(source));
		this.doSwitch(source.getType());
		if (source.getMultiplicity() != null) {
			this.doSwitch(source.getMultiplicity());
		}
		return result;
	}

	@Override
	public Object caseDataTypeDecl(DataTypeDecl source) {
		if (source.getTypeParamsInfo() != null) {
			source.getTypeParamsInfo().getTp().forEach(this::doSwitch);
		}
		return this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEDataType);
	}
	
	@Override
	public Object caseEnumDecl(EnumDecl source) {
		source.getEnumLiterals().forEach(this::doSwitch);
		return this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEEnum);
	}
	
	@Override
	public Object caseEnumLiteral(EnumLiteral source) {
		source.getLeadingAnnotations().forEach(this::doSwitch);
		source.getTrailingAnnotations().forEach(this::doSwitch);
		return super.caseEnumLiteral(source);
	}

	@Override
	public Object caseAnnotation(Annotation source) {
		return this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createEAnnotation);
	}

	@Override
	public Object caseTypeParam(TypeParam source) {
		ETypeParameter target = this.cache.get(
				source, 
				source.eResource(),
				EcoreFactory.eINSTANCE::createETypeParameter);
		if (source.getTypeBoundsInfo() != null) {
			source.getTypeBoundsInfo().getTb().forEach(this::doSwitch);
		}
		return target;
	}

	@Override
	public Object caseMultiplicity(Multiplicity source) {
		return this.cache.get(
				source, 
				source.eResource(),
				() -> new MultiplicityCopier(source));
	}

	@Override
	public Object caseBoundDataTypeWithMulti(BoundDataTypeWithMulti source) {
		var result = this.cache.get(
				source, 
				source.eResource(),
				() -> new BoundDataTypeWithMultiCopier(source));
		this.doSwitch(source.getMultiplicity());
		return result;
	}

	private final OnChangeEvictingCache cache;
	private final EmfaticImport emfaticImport;
	

}