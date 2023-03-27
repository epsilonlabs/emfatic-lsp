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

import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;

import com.google.inject.Inject;

public class EmfaticIdeCPP extends IdeContentProposalProvider {
	
	@Inject
	private AnnotationMap annotationMap;

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
		
		EmfaticContent sw = new EmfaticContent(calledRule, getProposalCreator(), context, annotationMap);
		sw.doSwitch(context.getCurrentModel()).forEach(a -> 
			acceptor.accept(a, 0));
		
	}
	

}