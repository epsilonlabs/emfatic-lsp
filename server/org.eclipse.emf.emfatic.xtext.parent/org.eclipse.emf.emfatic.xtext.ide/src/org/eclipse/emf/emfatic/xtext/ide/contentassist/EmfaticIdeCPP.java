package org.eclipse.emf.emfatic.xtext.ide.contentassist;

import java.util.Collection;

import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;

import com.google.inject.Inject;

public class EmfaticIdeCPP extends IdeContentProposalProvider {
	
	@Inject
	private AnnotationMap annotations;

	@Override
	public void createProposals(Collection<ContentAssistContext> contexts, IIdeContentProposalAcceptor acceptor) {
		super.createProposals(contexts, acceptor);
	}

	@Override
	protected void _createProposals(
		RuleCall ruleCall,
		ContentAssistContext context,
		IIdeContentProposalAcceptor acceptor) {
		AbstractRule calledRule = ruleCall.getRule();
		String eClassName = calledRule.getName();
		
		System.out.println("=== _createProposals ");
		System.out.println(eClassName);
		System.out.println(context.getCurrentModel());
		System.out.println("=====");
		
		EmfaticContent sw = new EmfaticContent(calledRule, getProposalCreator(), context, annotations);
		sw.doSwitch(context.getCurrentModel()).forEach(a -> 
			acceptor.accept(a, 0));
		
	}
	

}