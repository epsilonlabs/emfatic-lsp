package org.eclipse.emf.emfatic.xtext.scoping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassifierDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.DataTypeDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;

import com.google.inject.Singleton;

@Singleton
public class EmfaticImport {
	
	/**
	 * Wrap package.
	 * @param name 
	 *
	 * @return the package decl
	 */
	public PackageDecl translate(
		EPackage ep) {
		PackageDecl pd = EmfaticFactory.eINSTANCE.createPackageDecl();
		pd.setName(ep.getName());
		return pd;
	}
	
	/**
	 * Wrap classifiers.
	 *
	 * @param eClassifiers the e classifiers
	 * @return the collection<? extends top level decl>
	 * TODO generics
	 */
	public Collection<? extends TopLevelDecl> translate(
		EList<EClassifier> eClassifiers) {
		List<TopLevelDecl> result = eClassifiers.stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.map(this::translateClass)
				.collect(Collectors.toList());
		result.addAll( eClassifiers.stream()
				.filter(EDataType.class::isInstance)
				.map(EDataType.class::cast)
				.flatMap(dt -> this.translateDataType(dt).stream())
				.collect(Collectors.toList()));
		result.addAll( eClassifiers.stream()
				.filter(EEnum.class::isInstance)
				.map(EEnum.class::cast)
				.map(this::translateEnum)
				.collect(Collectors.toList()));
		return result;
	}
	
	private final Map<ClassifierDecl, EClassifier> imports = new HashMap<>();
	private final Map<String, EClassifier> nativeImports = new HashMap<>();

	/**
	 * Wrap class.
	 *
	 * @param clazz the clazz
	 * @return the top level decl
	 * TODO generics
	 */
	private TopLevelDecl translateClass(
		EClass clazz) {
		ClassDecl cd = EmfaticFactory.eINSTANCE.createClassDecl();
		cd.setName(clazz.getName());
		TopLevelDecl tld = EmfaticFactory.eINSTANCE.createTopLevelDecl();
		tld.setDeclaration(cd);
		this.imports.put(cd, clazz);
		return tld;
	}
	
	/**
	 * Emfatic not only supports the EMF DataTypes, but also, the equivalent
	 * Java types (both primitive and wrappers).
	 * We use the EMF URI of the EDataType as the InstanceClass name, so we can
	 * use this information when exporting to ECore
	 * 
	 * TODO The generated list of types is larger than Emfatic. Is this good/bad? 
	 * 		e.g., we get InvocationTargetException, not sure is a type we want to use,
	 * 		but... is a valid DataType non the less.
	 *
	 * @param type the type
	 * @return the list
	 * TODO generics
	 */
	private List<TopLevelDecl> translateDataType(
		EDataType type) {
		// All DataTypeDecl share the same instance class name
		List<TopLevelDecl> result = new ArrayList<>();
		DataTypeDecl dtd =  EmfaticFactory.eINSTANCE.createDataTypeDecl();
		dtd.setName(type.getName());
		TopLevelDecl tld = EmfaticFactory.eINSTANCE.createTopLevelDecl();
		tld.setDeclaration(dtd);
		result.add(tld);
		this.imports.put(dtd, type);
		Class<?> javaType = null; 
		try {
			javaType = Class.forName(type.getInstanceClassName());
		 } catch (ClassNotFoundException e) {
			 DataTypeDecl javaDtd =  EmfaticFactory.eINSTANCE.createDataTypeDecl();
			 javaDtd.setName(type.getInstanceClassName());
			 javaDtd.setInstanceClassName(createInstanceClassName(type));
			 TopLevelDecl javaTld = EmfaticFactory.eINSTANCE.createTopLevelDecl();
			 javaTld.setDeclaration(javaDtd);
			 result.add(javaTld);
			 this.nativeImports.put(javaDtd.getInstanceClassName().getLiteral(), type);
			 return result;	 
		}
		DataTypeDecl javaDtd = EmfaticFactory.eINSTANCE.createDataTypeDecl();
		javaDtd.setName(javaType.getSimpleName());
		javaDtd.setInstanceClassName(createInstanceClassName(type));
		TopLevelDecl javaTld = EmfaticFactory.eINSTANCE.createTopLevelDecl();
		javaTld.setDeclaration(javaDtd);
		result.add(javaTld);
		this.nativeImports.put(javaDtd.getInstanceClassName().getLiteral(), type);
		return result;
	}
	
	/**
	 * Wrap enum.
	 *
	 * @param type the type
	 * @return the top level decl
	 */
	private TopLevelDecl translateEnum(
		EEnum type) {
		EnumDecl ed = EmfaticFactory.eINSTANCE.createEnumDecl();
		ed.setName(type.getName());
		TopLevelDecl tld = EmfaticFactory.eINSTANCE.createTopLevelDecl();
		tld.setDeclaration(ed);
		this.imports.put(ed, type);
		return tld;
	}
	
	/**
	 * Creates the instance class name.
	 *
	 * @param element the element
	 * @return the string or qualified ID
	 */
	private StringOrQualifiedID createInstanceClassName(
		ENamedElement element) {
		StringOrQualifiedID instanceClassName = EmfaticFactory.eINSTANCE.createStringOrQualifiedID();
		instanceClassName.setLiteral("http://www.eclipse.org/emf/2002/Ecore#//" + element.getName());
		return instanceClassName;
	}

	public EClassifier export(DataTypeDecl bound) {
		if (bound.getInstanceClassName() == null) {
			return this.imports.get(bound);
		} else {
			return this.nativeImports.get(bound.getInstanceClassName().getLiteral());
		}
	}
	
	public EClassifier export(EnumDecl bound) {
		return this.imports.get(bound);
	}
	
	public EClassifier export(ClassDecl bound) {
		return this.imports.get(bound);
	}

	public EClassifier export(ClassifierDecl bound) {
		return switch(bound.eClass().getClassifierID()) {
			case EmfaticPackage.CLASS_DECL 		->	{ yield this.export((ClassDecl)bound); }
			case EmfaticPackage.DATA_TYPE_DECL	-> 	{ yield this.export((DataTypeDecl)bound); }
			case EmfaticPackage.ENUM_DECL 		->	{ yield this.export((EnumDecl)bound); }
			default -> throw new IllegalArgumentException("Unexpected value: " + bound.eClass().getClassifierID());
			};
		
	}
	
	
	
}
