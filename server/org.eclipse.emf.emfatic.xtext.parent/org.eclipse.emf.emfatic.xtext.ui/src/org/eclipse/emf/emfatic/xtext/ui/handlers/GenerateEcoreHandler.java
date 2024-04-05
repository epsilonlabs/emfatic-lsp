package org.eclipse.emf.emfatic.xtext.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.emf.emfatic.xtext.ecore.Twin;

import com.google.inject.Inject;

public class GenerateEcoreHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.twin.save();
		return null;
	}

	@Inject
	private Twin twin;

}
