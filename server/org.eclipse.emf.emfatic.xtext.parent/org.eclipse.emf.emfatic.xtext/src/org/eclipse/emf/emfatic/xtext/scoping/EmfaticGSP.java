package org.eclipse.emf.emfatic.xtext.scoping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.DefaultGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

public class EmfaticGSP extends DefaultGlobalScopeProvider {
	
	
	@Override
	protected IScope getScope(
		final Resource context, 
		boolean ignoreCase, 
		EClass type, 
		Predicate<IEObjectDescription> filter) {
		// We use the ECore URI, so when generating the emfatic ecore, we know what
		// comes from the Ecore metamodel
		URI libaryResourceURI = URI.createURI("lib://www.eclipse.org/emf/2002/Ecore");
		Resource libaryResource = context.getResourceSet().getResource(libaryResourceURI, false);
		if (libaryResource == null) {
			System.out.println("Loading Ecore");
			libaryResource = ecoreToEmf(context.getResourceSet(), libaryResourceURI);
			
		}
		IResourceDescription resourceDescription = descriptionManager.getResourceDescription(libaryResource);
		Iterable<IEObjectDescription> libary = resourceDescription.getExportedObjects();
		return new SimpleScope(super.getScope(context, ignoreCase, type, filter), libary, false);
	}
	
	@Inject
	private IResourceDescription.Manager descriptionManager;

	/**
	 * We only add a simple wrappers for the EClasses and EDataTypes that have 
	 * matching names
	 * @param resourceSet
	 * @param libaryResourceURI
	 * @return
	 */
	private Resource ecoreToEmf(ResourceSet resourceSet, URI libaryResourceURI) {
		Resource libaryResource = resourceSet.createResource(libaryResourceURI);
		CompUnit compUint = emfaticInstance(EmfaticPackage.Literals.COMP_UNIT);
		libaryResource.getContents().add(compUint);
		compUint.setPackage(wrapPackage(EcorePackage.eINSTANCE));
		compUint.getTopLevelDecls().addAll(wrapClassifiers(EcorePackage.eINSTANCE.getEClassifiers()));
		try {
			libaryResource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return libaryResource;
	}

	private Collection<? extends TopLevelDecl> wrapClassifiers(EList<EClassifier> eClassifiers) {
		List<TopLevelDecl> result = eClassifiers.stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.map(this::wrapClass)
				.collect(Collectors.toList());
		result.addAll( eClassifiers.stream()
				.filter(EDataType.class::isInstance)
				.map(EDataType.class::cast)
				.flatMap(dt -> this.wrapDataType(dt).stream())
				.collect(Collectors.toList()));
		result.addAll( eClassifiers.stream()
				.filter(EEnum.class::isInstance)
				.map(EEnum.class::cast)
				.map(this::wrapEnum)
				.collect(Collectors.toList()));
		return result;
	}

	
	
	private PackageDecl wrapPackage(EcorePackage ePackage) {
		PackageDecl pd = emfaticInstance(EmfaticPackage.Literals.PACKAGE_DECL);
		pd.setName(ePackage.getName());
		return pd;
	}
	
	
	
	private TopLevelDecl wrapClass(EClass clazz) {
		ClassDecl cd = emfaticInstance(EmfaticPackage.Literals.CLASS_DECL);
		cd.setName(clazz.getName());
		TopLevelDecl tld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		tld.setDeclaration(cd);
		return tld;
	}
	
	/**
	 * Emfatic not only supports the EMF DataTypes, but also, the equivalent
	 * Java types (both primitive and wrappers).
	 * When the Ecore DataType is xxxxObject, we want to generate the equivalent
	 * wrapped Java, else we generate the primitive.
	 * 
	 * TODO The generated list of types is larger than Emfatic. Is this good/bad? 
	 * 		e.g., we get InvocationTargetException, not sure is a type we want to use,
	 * 		but... is a valid DataType non the less.
	 * @param type
	 * @return
	 */
	private List<TopLevelDecl> wrapDataType(EDataType type) {
		List<TopLevelDecl> result = new ArrayList<>();
		DataTypeDecl dtd = emfaticInstance(EmfaticPackage.Literals.DATA_TYPE_DECL);
		dtd.setName(type.getName());
		TopLevelDecl tld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		tld.setDeclaration(dtd);
		result.add(tld);
//		System.out.println(
//				String.format("add(\"%s\", ecore.get%s());", dtd.getName(), dtd.getName()));
		Class<?> javaType = null; 
		try {
			javaType = Class.forName(type.getInstanceClassName());
		 } catch (ClassNotFoundException e) {
			 DataTypeDecl javaDtd = emfaticInstance(EmfaticPackage.Literals.DATA_TYPE_DECL);
			 javaDtd.setName(type.getInstanceClassName());
			 TopLevelDecl javaTld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
			 javaTld.setDeclaration(javaDtd);
			 result.add(javaTld);
//			 System.out.println(
//					 String.format("add(\"%s\", ecore.get%s());", javaDtd.getName(), dtd.getName()));
			 return result;	 
		}
			
		DataTypeDecl javaDtd = emfaticInstance(EmfaticPackage.Literals.DATA_TYPE_DECL);
		javaDtd.setName(javaType.getSimpleName());
		TopLevelDecl javaTld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		javaTld.setDeclaration(javaDtd);
		result.add(javaTld);
//		System.out.println(
//				String.format("add(\"%s\", ecore.get%s());", javaDtd.getName(), dtd.getName()));
		return result;
	}
	
	private TopLevelDecl wrapEnum(EEnum type) {
		EnumDecl ed = emfaticInstance(EmfaticPackage.Literals.ENUM_DECL);
		ed.setName(type.getName());
		TopLevelDecl tld = emfaticInstance(EmfaticPackage.Literals.TOP_LEVEL_DECL);
		tld.setDeclaration(ed);
		return tld;
	}

	
	
	@SuppressWarnings("unchecked")
	private <T extends EObject> T emfaticInstance(EClass eClass) {
		return (T) EmfaticPackage.eINSTANCE.getEmfaticFactory().create(eClass);
	}

}
