package org.eclipse.emf.emfatic.xtext.ide.contentassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.util.EmfaticSwitch;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistEntry;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalCreator;
import org.eclipse.xtext.util.TextRegion;

public class EmfaticContent extends EmfaticSwitch<Collection<ContentAssistEntry>> {
	
	public EmfaticContent(AbstractRule rule, IdeContentProposalCreator proposalCreator, ContentAssistContext context) {
		super();
		this.rule = rule;
		this.proposalCreator = proposalCreator;
		this.context = context;
	}

	@Override
	public Collection<ContentAssistEntry> caseAnnotation(Annotation object) {
		Collection<ContentAssistEntry> result = new ArrayList<>();
		if ("StringOrQualifiedID".equals(rule.getName())) {
			for (String label : defaultAnnotationLabels) {
				result.add(proposalCreator.createProposal(label, context,
						(ContentAssistEntry it) -> {
							if ("STRING".equals(rule.getName())) {
								it.getEditPositions()
										.add(new TextRegion(context.getOffset() + 1, label.length() - 2));
								it.setKind(ContentAssistEntry.KIND_TEXT);
							} else {
								it.getEditPositions().add(new TextRegion(context.getOffset(), label.length()));
								it.setKind(ContentAssistEntry.KIND_VALUE);
							}
							it.setDescription(rule.getName());
						})
					);
			}
		}
		return result;
	}

	@Override
	public Collection<ContentAssistEntry> defaultCase(EObject object) {
		return Collections.emptyList();
	}





	// TODO Decide if this should be changeable so we can reuse the switch
	private final AbstractRule rule;
	private final IdeContentProposalCreator proposalCreator;
	private final ContentAssistContext context;
	
	private final String defaultAnnotationLabels[] = new String[] {
			"Ecore", "GenModel", "ExtendedMetaData", "EmfaticAnnotationMap"
	};

}
