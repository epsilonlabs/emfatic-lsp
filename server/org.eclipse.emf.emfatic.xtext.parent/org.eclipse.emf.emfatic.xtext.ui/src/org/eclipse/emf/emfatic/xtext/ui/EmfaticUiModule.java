/*
 * generated by Xtext 2.28.0
 */
package org.eclipse.emf.emfatic.xtext.ui;

import org.eclipse.emf.emfatic.xtext.ide.contentassist.EmfaticIdeCPP;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ide.editor.contentassist.FQNPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AntlrProposalConflictHelper;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.editor.contentassist.IContentProposalProvider;
import org.eclipse.xtext.ui.editor.contentassist.UiToIdeContentProposalProvider;

/**
 * Use this class to register components to be used within the Eclipse IDE.
 */
public class EmfaticUiModule extends AbstractEmfaticUiModule {

	public EmfaticUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	public Class<? extends IContentProposalProvider> bindIContentProposalProvider() {
		return UiToIdeContentProposalProvider.class;
	}
	
	@SingletonBinding(eager = true)
	public Class<? extends IdeContentProposalProvider> bindIdeContentProposalProvider() {
		return EmfaticIdeCPP.class;
	}
	
	/*
	 * Since we are reusing the IdeContentProposalProvider, we need to make sure that interfaces that
	 * have @ImplementedBy annotations in the "ide" project are bound as done by the IdeModule (either
	 * the base ones or our own)
	 */
	public Class<? extends IProposalConflictHelper> bindIdeIProposalConflictHelper() {
		return AntlrProposalConflictHelper.class;
	}
	
	public Class<? extends IPrefixMatcher> bindIdeIPrefixMatcher() {
		return FQNPrefixMatcher.class;
	}
}
