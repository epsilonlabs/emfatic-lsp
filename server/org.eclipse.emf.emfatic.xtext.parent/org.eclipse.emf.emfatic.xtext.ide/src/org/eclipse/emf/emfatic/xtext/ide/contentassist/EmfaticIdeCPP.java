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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistEntry;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalCreator;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalPriorities;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;
import org.eclipse.xtext.ide.editor.contentassist.IdeCrossrefProposalProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.util.TextRegion;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

public class EmfaticIdeCPP extends IdeContentProposalProvider {

	/**
	 * Create content assist proposals and pass them to the given acceptor.
	 */
	public void createProposals(
		Collection<ContentAssistContext> contexts,
		IIdeContentProposalAcceptor acceptor) {
		for (ContentAssistContext context : getFilteredContexts(contexts)) {
			for (AbstractElement element : context.getFirstSetGrammarElements()) {
				if (!acceptor.canAcceptMoreProposals()) {
					return;
				}
				createProposals(element, context, acceptor);
			}
		}
	}

	@Override
	protected Iterable<ContentAssistContext> getFilteredContexts(Collection<ContentAssistContext> contexts) {
		return contexts;
	}
	
	/**
	 * Check if the Keyword should be added to the content proposals in the given context.
	 *
	 * @param keyword the keyword
	 * @param context the context
	 * @return true, if the keyword should be added
	 */
	@Override
	protected boolean filterKeyword(Keyword keyword, ContentAssistContext context) {
		KeywordFilter filter = new KeywordFilter(keyword.getValue());
		return filter.doSwitch(context.getCurrentModel());
	}
	
	@Override
	protected void _createProposals(Assignment assignment, ContentAssistContext context,
			IIdeContentProposalAcceptor acceptor) {
		LOG.debug("Creating Assignment proposals in the context of " + context.getCurrentModel().eClass().getName());
		AbstractElement terminal = assignment.getTerminal();
		if (terminal instanceof CrossReference) {
			createProposals(terminal, context, acceptor);
		} else {
			if (terminal instanceof RuleCall) {
				AbstractRule rule = ((RuleCall) terminal).getRule();
				if (rule instanceof TerminalRule && context.getPrefix().isEmpty()) {
					LOG.debug("Creating proposals for TerminalRule " + rule.getName() + " given that no prefix is supplied.");
					final String proposal;
					if ("STRING".equals(rule.getName())) {
						proposal = "\"" + assignment.getFeature() + "\"";
					} else {
						proposal = assignment.getFeature();
					}
					ContentAssistEntry entry = proposalCreator.createProposal(proposal, context,
							(ContentAssistEntry it) -> {
								if ("STRING".equals(rule.getName())) {
									it.getEditPositions()
											.add(new TextRegion(context.getOffset() + 1, proposal.length() - 2));
									it.setKind(ContentAssistEntry.KIND_TEXT);
								} else {
									it.getEditPositions().add(new TextRegion(context.getOffset(), proposal.length()));
									it.setKind(ContentAssistEntry.KIND_VALUE);
								}
								it.setDescription(rule.getName());
							});
						if (entry != null) {
							LOG.warn("Adding Proposal " + entry.getProposal() + ".");
						}
					acceptor.accept(entry, proposalPriorities.getDefaultPriority(entry));
				}
			}
		}
	}

	@Override
	protected void _createProposals(
		Keyword keyword,
		ContentAssistContext context,
		IIdeContentProposalAcceptor acceptor) {
		LOG.debug("Creating Keyword proposals for " + keyword.getValue() + " in the context of " + context.getCurrentModel().eClass().getName());
		if (filterKeyword(keyword, context)) {
			ContentAssistEntry entry = proposalCreator.createProposal(keyword.getValue(), context,
					ContentAssistEntry.KIND_KEYWORD, null);
			if (entry != null) {
				LOG.warn("Adding Proposal " + entry.getProposal() + ".");
				acceptor.accept(entry, proposalPriorities.getKeywordPriority(keyword.getValue(), entry));
			}
		}
	}
	
	@Override
	protected void _createProposals(
		RuleCall ruleCall,
		ContentAssistContext context,
		IIdeContentProposalAcceptor acceptor) {
		AbstractRule calledRule = ruleCall.getRule();
		String calledRuleName = calledRule.getName();
		LOG.debug("Creating RuleCall proposals for " + calledRuleName + " in the context of " + context.getCurrentModel().eClass().getName());
		
		ContentProposals sw = new ContentProposals(calledRule, getProposalCreator(), context, annotationMap);
		sw.doSwitch(context.getCurrentModel()).
			forEach(a ->  {
			acceptor.accept(a, proposalPriorities.getDefaultPriority(a));
			});
		
	}

	
	protected void _createProposals(AbstractElement element, ContentAssistContext context,
			IIdeContentProposalAcceptor acceptor) {
	}

	protected Predicate<IEObjectDescription> getCrossrefFilter(CrossReference reference, ContentAssistContext context) {
		return Predicates.alwaysTrue();
	}

	

	protected IScopeProvider getScopeProvider() {
		return scopeProvider;
	}

	protected IQualifiedNameConverter getQualifiedNameConverter() {
		return qualifiedNameConverter;
	}

	protected IdeCrossrefProposalProvider getCrossrefProposalProvider() {
		return crossrefProposalProvider;
	}

	protected IdeContentProposalCreator getProposalCreator() {
		return proposalCreator;
	}

	protected IdeContentProposalPriorities getProposalPriorities() {
		return proposalPriorities;
	}
	
	private static final Logger LOG = Logger.getLogger(EmfaticIdeCPP.class);
	
	
	@Inject
	private AnnotationMap annotationMap;
	
	@Inject
	private IScopeProvider scopeProvider;

	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;

	@Inject
	private IdeCrossrefProposalProvider crossrefProposalProvider;

	@Inject
	private IdeContentProposalCreator proposalCreator;

	@Inject
	private IdeContentProposalPriorities proposalPriorities;
	

}