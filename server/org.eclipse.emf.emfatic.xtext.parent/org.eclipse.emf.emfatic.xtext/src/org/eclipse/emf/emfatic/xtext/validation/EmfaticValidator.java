/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.validation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.common.ImportUriChecker;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMember;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMemberDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassifierDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.Declaration;
import org.eclipse.emf.emfatic.xtext.emfatic.Details;
import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumLiteral;
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Import;
import org.eclipse.emf.emfatic.xtext.emfatic.Operation;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Param;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.emf.emfatic.xtext.emfatic.SubPackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.validation.Check;

import com.google.common.base.Objects;
import com.google.inject.Inject;

/**
 * The validaiton of Emfatic models is composed of Emfatic and Ecore constraints.
 * 
 *  For Emfatic we want to
 *  <ul>
 *  	<i> Ecore metamodel is not imported
 *  	<i> Validate that imports exist
 *  </ul>
 *  <p>
 *  TODO For Ecore we should use the {@link EcoreValidator} and translate those errors to Xtext ones
 *  TODO Foe Ecore we should create the emfatic-2-ecore mapper first
 *  
 *  TODO Check that MapEntryDecl dont use primitive types for key/values
 */
public class EmfaticValidator extends AbstractEmfaticValidator {
	
	/**
	 * Warn if Ecore metamodel is loaded
	 */
	@Check
	public void checkEcoreMetamodelImported(Import imprt) {
		String literalURI = imprt.getUri().getLiteral();
		if (literalURI != null && 
				Objects.equal(literalURI, EcorePackage.eINSTANCE.getNsURI())) {
			warning(
					"Ecore metemodel is imported by default.",
					EmfaticPackage.Literals.IMPORT__URI,
					IssueCodes.W_ECORE_IMPRTD,
					"");
		}
	}
	
	/**
	 * Check that the URI is valid and that it's not an empty resource
	 * @param imprt
	 */
	@Check
	public void checkImportedUriIsValid(Import imprt) {
		ImportUriChecker checker = new ImportUriChecker(imprt.eResource(), this.fileExtensionProvider);
		Optional<URI> optUri = checker.resolveURI(imprt);
		try {
			optUri = checker.resolveURI(imprt);
		} catch (Exception e) {
			error(
				"Invalid uri '" + checker.uriString(imprt) + "'",
				EmfaticPackage.Literals.IMPORT__URI,
				IssueCodes.E_INVLD_IMPRT_URI,
				""); 
		}
		if (optUri.isPresent()) {
			URI uri = optUri.get();
			if (!checker.isValidResoruce(uri)) {
				error(
					"Unsupported URI file extension. When using 'platform' or 'file' URI, only import *.ecore and " 
							+ checker.validExtensions()
							+ " models are supported.",
					EmfaticPackage.Literals.IMPORT__URI,
					IssueCodes.E_UNSPPRTD_URI_EXTNSN,
					""); 
			} else if (!checker.resourceExists(uri)) {
				error(
						"Unable to load metamodel with uri '" + uri.toString() + "'. Resource was not found.",
						EmfaticPackage.Literals.IMPORT__URI,
						IssueCodes.E_IMPRTD_MTMDL_NOT_FND,
						"");
			} else {
				Resource metamodelResource = EcoreUtil2.getResource(imprt.eResource(), uri.toString());
				if (metamodelResource != null && metamodelResource.getContents().isEmpty()) {
					warning(
							"Metamodel with uri '" + uri + "' is empty. Check the URI and/or target file.",
							EmfaticPackage.Literals.IMPORT__URI,
							IssueCodes.W_EMPTY_MTMDL,
							"");
				}
			}
		}
	}
	
	/**
	 * Check that he annotation label has been previously defined.
	 */
	@Check
	public void checkValidLabel(final Annotation annt) {
		StringOrQualifiedID source = annt.getSource();
		var label = source.getId();
		if (label != null) {
			if(!this.annotationMap.knowsLabel(label, annt.eResource())) {
				error(
						"Unknown annotation label '" + label + "'. This can mean you are "
								+ "missing an EmfaticAnnotationMap to define a custom label "
								+ "or there is a missing dependency (label provided via "
								+ "extension point). Remember that URIs must be within double quotes.",
						EmfaticPackage.Literals.ANNOTATION__SOURCE,
							IssueCodes.E_UNKWN_ANNT_LABEL,
							""); 
			}
		} else {
			String literal = source.getLiteral();
			this.annotationMap.labelForUri(literal, annt.eResource())
				.ifPresent(l ->
				warning(
						"The key uri " + literal + " can be replaced by its label: " + l,
						EmfaticPackage.Literals.ANNOTATION__SOURCE,
						IssueCodes.W_URI_INSTD_OF_LBL,
						l.toLowerCase()));			
		}
	}
	
	/**
	 * Check that annotation is not assigning duplicate keys
	 */
	@Check
	public void checkDuplicateKeys(final Details entry) {
		Annotation annt = (Annotation) entry.eContainer();
		for(int i=0;i<annt.getDetails().size();i++) {
			var d = annt.getDetails().get(i);
			if (d != entry && d.getKey().equals(entry.getKey())) {
				error(
					"Duplicate key detected: " + entry.getKey(),
					EmfaticPackage.Literals.DETAILS__KEY,
					i,
					IssueCodes.E_DUP_ANNT_KEY_FOUND,
					entry.getKey()); 
			}
		}
	}
	
	/**
	 * Check that the key is valid for the EObject to which the annotation is attached
	 */
	@Check
	public void checkValidDetails(Details entry) {
		Annotation annt = (Annotation) entry.eContainer();
		annotationTarget(annt.eContainer()).ifPresent(target ->
			resolveLabel(annt). ifPresent(label -> {
				try {
					if (!this.annotationMap.isValidKey(label, entry.getKey(), target, annt.eResource())) {
						warning(
								"The key '" + entry.getKey() + "' is not a valid key for " + target.getName() + " elements.",
								EmfaticPackage.Literals.DETAILS__KEY,
								IssueCodes.W_INVLD_KEY_USED,
								entry.getKey());
					}
				} catch (IllegalArgumentException e) {
					LOG.error("Testing for the label already done, should never get here");
					throw e;
				}
			})
		);
	}
	
	/**
	 * Check for self inheritance in ClassDecl
	 */
	@Check
	public void checkSelfInheritance(ClassDecl classDecl) {
		var extendsList = classDecl.getSuperTypes().stream()
			.map(bc -> bc.getBound()).toList();
		for(int i=0;i<extendsList.size();i++) {
			var cd = extendsList.get(i);
			if(classDecl.equals(cd)) {
				error(
					"Cycle detected: the type '" + classDecl.getName() + "' cannot extend itself",
					EmfaticPackage.Literals.CLASS_DECL__SUPER_TYPES,
					i,
					IssueCodes.E_EXTND_CYCLE_DTCTD,
					String.valueOf(i)); 
			}
		}
	}
	
	
	/**
	 * Check that ClassDecl have unique names within their PackDecl
	 */
	@Check
	public void checkClassDeclUniqueName(ClassifierDecl entry) {
		TopLevelDecl topLvlDecl = (TopLevelDecl) entry.eContainer();
		EObject context = topLvlDecl.eContainer();
		Stream<Declaration> otherClassDecl = null;
		if (Objects.equal(context.eClass(), EmfaticPackage.Literals.COMP_UNIT)) {
			CompUnit compUnit = (CompUnit) context;
			otherClassDecl = compUnit.getDeclarations().stream()
					.map(tld -> tld.getDeclaration())
					.filter(d -> d != entry);		
		} else if (Objects.equal(context.eClass(), EmfaticPackage.Literals.SUB_PACKAGE_DECL)) {
			SubPackageDecl pckDecl = (SubPackageDecl) context;
			otherClassDecl = pckDecl.getDeclarations().stream()
					.map(tld -> tld.getDeclaration())
					.filter(d -> d != entry);	
		} else {
			return;
		}
		otherClassDecl.forEach(oc -> {
			if (oc instanceof ClassifierDecl && oc.getName().equals(entry.getName())) {
				error(
						"Duplicate names detected: " + entry,
						EmfaticPackage.Literals.DECLARATION__NAME,
						IssueCodes.E_DUP_CLASS_NAME,
						oc.getName());
			}
		});
	}
	
	/**
	 * Check that Features have unique names within their class
	 */
	@Check
	public void checkFeatureUniqueName(FeatureDecl entry) {
		ClassMemberDecl decl = (ClassMemberDecl) entry.eContainer();
		ClassDecl classDecl = (ClassDecl) decl.eContainer();
		classDecl.getMembers().stream()
			.map(cm -> cm.getMember())
			.filter(cm -> (cm instanceof FeatureDecl) && cm != entry)
			.map(m  -> ((FeatureDecl)m).getFeature())
			.forEach(f -> {
				if (f.getName().equals(entry.getFeature().getName())) {
					error(
							"Duplicate names detected: " + entry.getFeature().getName(),
							EmfaticPackage.Literals.FEATURE_DECL__FEATURE,
							IssueCodes.E_DUP_FEAT_NAME,
							f.getName());
				}
			});
	}
	
	/**
	 * Check that Operations have unique names within their class
	 */
	@Check
	public void checkOperationUniqueName(Operation entry) {
		ClassMemberDecl decl = (ClassMemberDecl) entry.eContainer();
		ClassDecl classDecl = (ClassDecl) decl.eContainer();
		classDecl.getMembers().stream()
				.map(cm -> cm.getMember())
				.filter(cm -> (cm instanceof Operation) && cm != entry)
				.map(Operation.class::cast)
				.forEach(op -> {
					if (op.getName().equals(entry.getName())) {
						error(
								"Duplicate names detected: " + entry.getName(),
								EmfaticPackage.Literals.OPERATION__NAME,
								IssueCodes.E_DUP_OP_NAME,
								op.getName()); 
					}
		});
	}
	
	/**
	 * Check that an operation's parameters have unique names
	 * 
	 */
	@Check
	public void checkOperationParameterUniqueName(Operation entry) {
		Set<String> seen = new HashSet<>();
		Set<Param> duplicates = entry.getParams().stream()
			.filter(e -> !seen.add(e.getName())) // Returns true if the element was already in the set
	        .collect(Collectors.toSet());
		if (duplicates.size() > 0) {
			duplicates.forEach(p -> error(
					"Duplicate parameter name detected: " + p.getName(),
					EmfaticPackage.Literals.OPERATION__PARAMS,
					IssueCodes.E_DUP_OP_PARAM_NAME,
					p.getName())); 
		}
	}
	
	/**
	 * A {@link BoundClassifierExceptWildcard} that binds a {@link TypeParam} can't have
	 * type args
	 * @param entry
	 */
	@Check
	public void checkBoundClassifierExceptWildcardTypeParam(BoundClassifierExceptWildcard entry) {
		if (entry.getBound() instanceof TypeParam && !entry.getTypeArgs().isEmpty()) {
			error(
					"Type arguments ('<?>') are not allowed when the bound type is a TypeParameter",
					EmfaticPackage.Literals.BOUND_CLASSIFIER_EXCEPT_WILDCARD__TYPE_ARGS,
					IssueCodes.E_BOUND_TPARAM_WT_ARGS); 
		}
	}

	
	/**
	 * Check that only one Attribtue is ID
	 * @param entry
	 */
	@Check
	public void onlyOneId(Attribute entry) {
		if (entry.getId() == null || entry.getId().isNegated()) {
			return;
		}
		ClassMemberDecl decl = (ClassMemberDecl) entry.eContainer();
		ClassDecl classDecl = (ClassDecl) decl.eContainer();
		var idAttrs = classDecl.getMembers().stream()
				.map(cm -> cm.getMember())
				.filter(cm -> (cm instanceof Attribute) 
						&& cm != entry)
				.map(Attribute.class::cast)
				.filter(a -> a.getId() != null && !a.getId().isNegated())
				.toList();
		idAttrs.forEach(ida -> {
			error(
					"Duplicate ID attribute: " + ((FeatureDecl) ida).getFeature().getName(),
					EmfaticPackage.Literals.ATTRIBUTE__ID,
					IssueCodes.E_DUP_ID_ATTR,
					entry.getName());
		});
	}
	
	/**
	 * Check Feature modifier is valid for feature:
	 * 	- resolve only works on ERefernce
	 *  - id only for EAttribtues
	 *  
	 */
	@Check
	public void checkFeatureModifier() {
		
	}
	
	//TODO Check that the attribute defValue matches the Attribut's Type
	
	@Inject
	private FileExtensionProvider fileExtensionProvider;
	
	@Inject
	private AnnotationMap annotationMap;
	
	private final static Logger LOG = Logger.getLogger(EmfaticValidator.class);

	private Optional<EClass> annotationTarget(EObject anntOwner) {
		EClass target = null;
		if (anntOwner instanceof PackageDecl) {
			target = anntOwner.eClass();
		} else if (anntOwner instanceof TopLevelDecl) {
			TopLevelDecl tpLvlDclrtn = (TopLevelDecl) anntOwner;
			Declaration declaration = tpLvlDclrtn.getDeclaration();
			if (declaration != null) {
				target = declaration.eClass();
			}
		} else if (anntOwner instanceof ClassMemberDecl) {
			ClassMemberDecl declaration = (ClassMemberDecl) anntOwner;
			ClassMember member = declaration.getMember();
			if (member != null) {
				target = member.eClass();
			}
		} else if (anntOwner instanceof Param) {
			target = anntOwner.eClass();
		} else if (anntOwner instanceof EnumLiteral) {
			target = anntOwner.eClass();
		}
		return Optional.ofNullable(target);
	}
	
	private Optional<String> resolveLabel(Annotation annt) {
		StringOrQualifiedID source = annt.getSource();
		String label = null;
		label = source.getId();
		if (label != null) {
			return Optional.of(label);
		}
		return this.annotationMap.labelForUri(source.getLiteral(), annt.eResource());
	}
		
}
