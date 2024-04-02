package org.eclipse.emf.emfatic.xtext.ecore;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundDataTypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassRefWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumLiteral;
import org.eclipse.emf.emfatic.xtext.emfatic.MapEntryDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Multiplicity;
import org.eclipse.emf.emfatic.xtext.emfatic.Operation;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Param;
import org.eclipse.emf.emfatic.xtext.emfatic.Reference;
import org.eclipse.emf.emfatic.xtext.emfatic.ResultType;
import org.eclipse.emf.emfatic.xtext.emfatic.SubPackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.util.EmfaticSwitch;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;

public class Elements extends EmfaticSwitch<Object> {

	public Elements(EmfaticImport emfaticImport) {
		this(new HashMap<>(), emfaticImport);
	}
	
	public Elements(Map<Object, Object> cache, EmfaticImport emfaticImport) {
		this.cache = cache;
		this.emfaticImport = emfaticImport;
	}

	public Map<Object, Object> copy(final CompUnit model) {
		TreeIterator<EObject> it = EcoreUtil.getAllContents(model, true);
		while (this.keepRunning() && it.hasNext()) {
			this.cache.computeIfAbsent(it.next(), k -> this.doSwitch((EObject) k));
		}
		return this.cache;
	}

	public synchronized void stop() {
		this.stop = true;
	}

	@Override
	public Object doSwitch(EObject eObject) {
		if (eObject == null) {
			return null;
		}
		LOG.debug("Creating element for " + eObject);
		return doSwitch(eObject.eClass(), eObject);
	}

	@Override
	public Object casePackageDecl(PackageDecl source) {
		return EcoreFactory.eINSTANCE.createEPackage();
	}

	@Override
	public Object caseSubPackageDecl(SubPackageDecl source) {
		return EcoreFactory.eINSTANCE.createEPackage();
	}

	@Override
	public Object caseMapEntryDecl(MapEntryDecl source) {
		return EcoreFactory.eINSTANCE.createEClass();
	}

	@Override
	public Object caseTypeWithMulti(TypeWithMulti source) {
		return new TypeWithMultiCopier((TypeWithMulti) source);
	}

	@Override
	public Object caseWildcard(Wildcard source) {
		return new WildcardCopier((Wildcard) source);
	}

	@Override
	public Object caseBoundClassifierExceptWildcard(BoundClassifierExceptWildcard source) {
		return new BoundClassifierExceptWildcardCopier(source, this.emfaticImport);
	}

	@Override
	public Object caseBoundClassExceptWildcard(BoundClassExceptWildcard source) {
		return new BoundClassExceptWildcardCopier(source, this.emfaticImport);
	}

	@Override
	public Object caseClassDecl(ClassDecl source) {
		return EcoreFactory.eINSTANCE.createEClass();
	}

	@Override
	public Object caseDataTypeDecl(DataTypeDecl source) {
		return EcoreFactory.eINSTANCE.createEDataType();
	}

	@Override
	public Object caseEnumDecl(EnumDecl source) {
		return EcoreFactory.eINSTANCE.createEEnum();
	}

	@Override
	public Object caseOperation(Operation source) {
		return EcoreFactory.eINSTANCE.createEOperation();
	}

	@Override
	public Object caseResultType(ResultType source) {
		return new ResultTypeCopier(source);
	}

	@Override
	public Object caseParam(Param source) {
		return EcoreFactory.eINSTANCE.createEParameter();
	}

	@Override
	public Object caseAttribute(Attribute source) {
		return EcoreFactory.eINSTANCE.createEAttribute();
	}

	@Override
	public Object caseReference(Reference source) {
		return EcoreFactory.eINSTANCE.createEReference();
	}

	@Override
	public Object caseClassRefWithMulti(ClassRefWithMulti source) {
		return new ClassRefWithMultiCopier(source);
	}

	@Override
	public Object caseEnumLiteral(EnumLiteral source) {
		return EcoreFactory.eINSTANCE.createEEnumLiteral();
	}

	@Override
	public Object caseAnnotation(Annotation source) {
		return EcoreFactory.eINSTANCE.createEAnnotation();
	}

	@Override
	public Object caseTypeParam(TypeParam source) {
		return EcoreFactory.eINSTANCE.createETypeParameter();
	}

	@Override
	public Object caseMultiplicity(Multiplicity source) {
		return new MultiplicityCopier(source);
	}

	@Override
	public Object caseBoundDataTypeWithMulti(BoundDataTypeWithMulti source) {
		return new BoundDataTypeWithMultiCopier(source, this.emfaticImport);
	}
	
	private final static Logger LOG = Logger.getLogger(Elements.class);

	private final Map<Object, Object> cache;
	private final EmfaticImport emfaticImport;

	private boolean stop = false;
	
	private synchronized boolean keepRunning() {
		return this.stop == false;
	}

}
