package org.eclipse.emf.emfatic.xtext.ui.builder.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Horacio Hoyos Rodriguez - Initial contribution and API
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfatic.xtext.ui.builder.preferences.messages"; //$NON-NLS-1$
	public static String BuilderPreferencePage_GenerateAuto;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
