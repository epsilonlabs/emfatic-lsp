package org.eclipse.emfatic.ide.contentassist;

import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;


public class EmfaticContentProposalProvider extends IdeContentProposalProvider {

	@Override
	protected void createProposals(AbstractElement assignment, ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {

		acceptor.accept(getProposalCreator().createProposal("PENE", context), 100);
		super.createProposals(assignment, context, acceptor);
	}

	@Override
	protected void _createProposals(Keyword keyword, ContentAssistContext context, IIdeContentProposalAcceptor acceptor) {
		super._createProposals(keyword, context, acceptor);
	}
}
