/*
 * generated by Xtext 2.28.0
 */
package org.eclipse.emf.emfatic.xtext.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

/**
 * See https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#content-assist
 * on how to customize the content assistant.
 */
public class EmfaticProposalProvider extends AbstractEmfaticProposalProvider {
	
	
	public void completeAnnotation_Source(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		super.completeAnnotation_Source(model, assignment, context, acceptor);
		
		acceptor.accept(createCompletionProposal("Ecore", context));
		acceptor.accept(createCompletionProposal("GenModel", context));
		acceptor.accept(createCompletionProposal("ExtendedMetaData", context));
		acceptor.accept(createCompletionProposal("EmfaticAnnotationMap", context));
	}
	
	public void complete_StringOrQualifiedID(Annotation model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		System.out.println("complete_StringOrQualifiedID " + model.toString());
		System.out.println(ruleCall.getRule().getName());
	}
}
