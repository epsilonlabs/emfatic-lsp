package org.eclipse.emf.emfatic.xtext.scoping;

import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.naming.EmfaticDQNP;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

public class EmfaticRDS extends DefaultResourceDescriptionStrategy {
	
	private final static Logger LOG = Logger.getLogger(EmfaticRDS.class);

	@Override
	public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
		if (getQualifiedNameProvider() == null) {
			return false;
		}
		if (eObject.eResource() != null
				&& Objects.equals(EmfaticGSP.ECORE_RESOURCE_URI, eObject.eResource().getURI().toString())) {
			IQualifiedNameProvider qnm = new EmfaticDQNP();
			
			try {
				// Full qualified
				QualifiedName qualifiedName = qnm.getFullyQualifiedName(eObject);
				if (qualifiedName != null) {
					acceptor.accept(EObjectDescription.create(qualifiedName, eObject));
				}
				
			} catch (Exception exc) {
				LOG.error(exc.getMessage(), exc);
			}
		}
		// This will also create the unqualified
		return super.createEObjectDescriptions(eObject, acceptor);
	}

	
	

}
