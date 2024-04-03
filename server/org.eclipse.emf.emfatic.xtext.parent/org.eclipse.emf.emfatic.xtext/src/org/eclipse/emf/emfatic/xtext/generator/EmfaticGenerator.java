package org.eclipse.emf.emfatic.xtext.generator;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.ecore.Twin;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

import com.google.inject.Inject;

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
public class EmfaticGenerator extends AbstractGenerator {

	@Override
	public void doGenerate(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
		this.twin.save();
	}
	
	@Inject
	private Twin twin;


}
