/*
 * generated by Xtext 2.28.0
 */
package org.eclipse.emf.emfatic.xtext.validation;

import org.eclipse.emf.emfatic.xtext.emfatic.EmfaticPackage;
import org.eclipse.emf.emfatic.xtext.emfatic.Import;
import org.eclipse.xtext.validation.Check;

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class EmfaticValidator extends AbstractEmfaticValidator {
	
//	public static final String INVALID_NAME = "invalidName";
//
//	@Check
//	public void checkGreetingStartsWithCapital(Greeting greeting) {
//		if (!Character.isUpperCase(greeting.getName().charAt(0))) {
//			warning("Name should start with a capital",
//					EmfaticPackage.Literals.GREETING__NAME,
//					INVALID_NAME);
//		}
//	}
	
	
	@Check
	public void checkEcoreMetamodelImported(Import imprt) {
		String literalURI = imprt.getUri().getLiteral();
		if (literalURI != null && literalURI.equals("http://www.eclipse.org/emf/2002/Ecore")) {
			warning(
					"Ecore metemodel is imported by default.",
					EmfaticPackage.Literals.IMPORT__URI,
					IssueCodes.ECORE_METAMODEL_IMPORTED,
					"");
		}
	}
}
