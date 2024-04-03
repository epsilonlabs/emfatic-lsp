package org.eclipse.emf.emfatic.xtext.ui.builder.preferences;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.builder.preferences.BuilderConfigurationBlock;
import org.eclipse.xtext.builder.preferences.BuilderPreferenceAccess;
import org.eclipse.xtext.generator.OutputConfiguration;

/**
 * @author Michael Clay - Initial contribution and API
 * @author Stephane Galland - Add getOutputConfigurations()
 * @since 2.1
 */
@SuppressWarnings("restriction")
public class EmfaticBuilderConfigurationBlock extends BuilderConfigurationBlock {
	
	@Override
	protected void createGeneralSectionItems(Composite composite) {
		addCheckBox(composite, Messages.BuilderPreferencePage_GenerateAuto,
				BuilderPreferenceAccess.PREF_AUTO_BUILDING, BOOLEAN_VALUES, 0);
	}
	
	protected Set<OutputConfiguration> getOutputConfigurations(IProject project) {
		return Collections.emptySet();
	}

}
