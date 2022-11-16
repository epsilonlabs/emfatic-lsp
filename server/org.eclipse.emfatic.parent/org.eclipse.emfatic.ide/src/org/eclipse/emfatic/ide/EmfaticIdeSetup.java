/*
 * generated by Xtext 2.25.0
 */
package org.eclipse.emfatic.ide;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.emfatic.EmfaticRuntimeModule;
import org.eclipse.emfatic.EmfaticStandaloneSetup;
import org.eclipse.xtext.util.Modules2;

/**
 * Initialization support for running Xtext languages as language servers.
 */
public class EmfaticIdeSetup extends EmfaticStandaloneSetup {

	@Override
	public Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new EmfaticRuntimeModule(), new EmfaticIdeModule()));
	}
	
}
