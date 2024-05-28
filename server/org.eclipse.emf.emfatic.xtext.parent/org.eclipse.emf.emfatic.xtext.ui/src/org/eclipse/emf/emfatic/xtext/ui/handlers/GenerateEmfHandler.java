package org.eclipse.emf.emfatic.xtext.ui.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfatic.xtext.generator.EcoreGenerator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import com.google.inject.Inject;

/**
 * This Handler invokes the EcoreGenerator
 */
public class GenerateEmfHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
                .getActivePage().getSelection();
        if (selection != null & selection instanceof IStructuredSelection) {
            IStructuredSelection strucSelection = (IStructuredSelection) selection;
            @SuppressWarnings("unchecked")
			Iterator<Object> iterator = strucSelection.iterator();
            while (iterator.hasNext()) {
                IFile ecoreFile = (IFile) iterator.next();
                var resourceSet = new ResourceSetImpl();
                var resource = resourceSet.getResource(URI.createURI(ecoreFile.getFullPath().toString()), true);
                this.generator.generate(
                		resource, 
                		ecoreFile.getName().replace(".ecore", ".lspemf"), 
                		//this.fileSystemAccess(generator.outputConfiguratioName(), ecoreFile)
                		this.fsa);
            }
        }
		return null;
	}
	
	@Inject
    private EcoreGenerator generator;
	
	@Inject
	private IFileSystemAccess2 fsa;
	
//	private IFileSystemAccess2 fileSystemAccess(String outputConfigName, IFile ecoreFile) {
//		XtextActivator plugin = XtextActivator.getInstance();
//		Injector injector = plugin.getInjector(XtextActivator.ORG_ECLIPSE_EMF_EMFATIC_XTEXT_EMFATIC);
//		IFileSystemAccess2 instance = new EclipseResourceFileSystemAccess2();
//		injector.injectMembers(instance);
//		if (instance instanceof EclipseResourceFileSystemAccess2) {
//			EclipseResourceFileSystemAccess2 eclipseRFSA = (EclipseResourceFileSystemAccess2) instance;
//			eclipseRFSA.setProject(ecoreFile.getProject());
//			eclipseRFSA.setMonitor(new NullProgressMonitor());
//			IPath output = ecoreFile.getParent().getProjectRelativePath();
//			eclipseRFSA.setOutputPath(outputConfigName, output.toString());
//		}
//		return instance;
//	}

	
	
}
