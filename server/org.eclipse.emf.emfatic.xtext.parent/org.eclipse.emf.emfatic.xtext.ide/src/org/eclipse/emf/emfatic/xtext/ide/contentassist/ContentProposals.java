/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.ide.contentassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMember;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMemberDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Declaration;
import org.eclipse.emf.emfatic.xtext.emfatic.EnumLiteral;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Param;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.util.EmfaticSwitch;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistEntry;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalCreator;
import org.eclipse.xtext.util.TextRegion;

import com.google.common.base.Strings;

public class ContentProposals extends EmfaticSwitch<Collection<ContentAssistEntry>> {
	
	public ContentProposals(
		AbstractRule rule,
		IdeContentProposalCreator proposalCreator,
		ContentAssistContext context,
		AnnotationMap annotations) {
		super();
		this.rule = rule;
		this.proposalCreator = proposalCreator;
		this.context = context;
		this.annotationMap = annotations;
	}
	
	

//	@Override
//	public Collection<ContentAssistEntry> caseTopLevelDecl(TopLevelDecl object) {
//		Collection<ContentAssistEntry> result = new ArrayList<>();
//		switch (rule.getName()) {
//		case "Annotation":
//			LOG.debug("Creating labels for Annotation@TopLevelDecl");
//			for (String label : this.annotationMap.labels(object.eResource())) {
//				result.add(createProposal("@"+label));
//			}
//			break;
//		default:
//			break;
//		}
//		return result;
//	}




	@Override
	public Collection<ContentAssistEntry> caseAnnotation(Annotation object) {
		Collection<ContentAssistEntry> result = new ArrayList<>();
		if ("QualifiedID".equals(rule.getName())) {
			// Content for QualifiedID depends on the las completed node
			// '@': We are looking for annotation labels
			// '(', ',': We are looking for valid keys
			String completedNode = context.getLastCompleteNode().getText();
			switch (completedNode) {
			case "@":
				LOG.debug("Creating labels for label@Annotation");
				for (String label : this.annotationMap.labels(object.eResource())) {
					result.add(createProposal(label));
				}
				break;
			case "(":
			case ",":
				LOG.debug("Creating labels for key@Annotation");
				// This is for the complete key=value part...
				EObject anntOwner = object.eContainer();
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
				// TODO We should support content assist for full uri labels! 
				StringOrQualifiedID source = object.getSource();
				if (source.getId() != null && target != null) {
					for (String key : this.annotationMap.keysFor(source.getId(), target)) {
						result.add(createProposal(key));
					}
				}
				break;
			default:
				break;
			}
		}
		return result;
	}

	private ContentAssistEntry createProposal(String label) {
		LOG.debug("Creating proposal with label <" + label + ">,  given the prefix " + this.context.getPrefix());
		ContentAssistEntry proposal = proposalCreator.createProposal(label, context,
				(ContentAssistEntry it) -> {
					if ("STRING".equals(rule.getName())) {
						it.getEditPositions()
								.add(new TextRegion(context.getOffset() + 1, label.length() - 2));
						it.setKind(ContentAssistEntry.KIND_TEXT);
					} else {
						it.getEditPositions().add(new TextRegion(context.getOffset(), label.length()));
						it.setKind(ContentAssistEntry.KIND_VALUE);
					}
					//it.setDescription(rule.getName());
				});
		if (proposal == null) {
			LOG.warn("ProposaL with label " + label + " was rejected by the proposalCreator.");
			proposalCreator.isValidProposal(label, context.getPrefix(), context);
		}
		return proposal;
	}

	@Override
	public Collection<ContentAssistEntry> defaultCase(EObject object) {
		return Collections.emptyList();
	}

	private static final Logger LOG = Logger.getLogger(ContentProposals.class);

	// TODO Decide if this should be changeable so we can reuse the switch
	private final AbstractRule rule;
	private final IdeContentProposalCreator proposalCreator;
	private final ContentAssistContext context;
	private final AnnotationMap annotationMap;

}
