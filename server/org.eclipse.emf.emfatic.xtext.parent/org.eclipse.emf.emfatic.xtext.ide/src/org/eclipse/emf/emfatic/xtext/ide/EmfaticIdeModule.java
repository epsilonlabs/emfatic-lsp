/*
 * generated by Xtext 2.28.0
 */
package org.eclipse.emf.emfatic.xtext.ide;

import org.eclipse.emf.emfatic.xtext.ide.contentassist.EmfaticIdeCPP;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;
import org.eclipse.xtext.service.SingletonBinding;

/**
 * Use this class to register ide components.
 */
public class EmfaticIdeModule extends AbstractEmfaticIdeModule {

	@SingletonBinding(eager = true)
	public Class<? extends IdeContentProposalProvider> bindIdeContentProposalProvider() {
		return EmfaticIdeCPP.class;
	}
}
