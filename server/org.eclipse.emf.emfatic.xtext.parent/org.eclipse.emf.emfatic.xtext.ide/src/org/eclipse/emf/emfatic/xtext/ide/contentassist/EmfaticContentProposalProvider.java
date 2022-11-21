package org.eclipse.emf.emfatic.xtext.ide.contentassist;

import java.util.Collection;

import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;

public class EmfaticContentProposalProvider extends IdeContentProposalProvider {

	@Override
	public void createProposals(Collection<ContentAssistContext> contexts, IIdeContentProposalAcceptor acceptor) {
		super.createProposals(contexts, acceptor);
	}
}