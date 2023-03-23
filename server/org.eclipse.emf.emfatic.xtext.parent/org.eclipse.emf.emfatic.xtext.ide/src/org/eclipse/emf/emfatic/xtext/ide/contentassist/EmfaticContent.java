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

public class EmfaticContent extends EmfaticSwitch<Collection<ContentAssistEntry>> {
	
	public EmfaticContent(
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

	@Override
	public Collection<ContentAssistEntry> caseAnnotation(Annotation object) {
		Collection<ContentAssistEntry> result = new ArrayList<>();
		if ("StringOrQualifiedID".equals(rule.getName())) {
			for (String label : this.annotationMap.labels()) {
				result.add(createProposal(label));
			}
		} else if ("KeyEqualsValue".equals(rule.getName())) {
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
		}
		return result;
	}

	private ContentAssistEntry createProposal(String label) {
		return proposalCreator.createProposal(label, context,
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
	}

	@Override
	public Collection<ContentAssistEntry> defaultCase(EObject object) {
		return Collections.emptyList();
	}


	// TODO Decide if this should be changeable so we can reuse the switch
	private final AbstractRule rule;
	private final IdeContentProposalCreator proposalCreator;
	private final ContentAssistContext context;
	private final AnnotationMap annotationMap;

}
