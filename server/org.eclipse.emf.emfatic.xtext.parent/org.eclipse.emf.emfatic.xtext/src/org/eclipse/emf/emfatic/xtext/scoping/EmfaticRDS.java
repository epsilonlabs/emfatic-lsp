package org.eclipse.emf.emfatic.xtext.scoping;

import java.util.HashMap;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.AliasedEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

/**
 * This {@link IDefaultResourceDescriptionStrategy} implementation adds additional
 * {@code IEObjectDescription} for the ECore metamodel that have a fully qualified
 * name. That way, for example, content assist and linking will work for both
 * {@code EClass} and {@code ecore.EClass}
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class EmfaticRDS extends DefaultResourceDescriptionStrategy {
	
	private final static Logger LOG = Logger.getLogger(EmfaticRDS.class);

	@Override
	public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
		if (getQualifiedNameProvider() == null) {
			return false;
		}
		if (eObject.eResource() != null
				&& Objects.equals(EmfaticGSP.ECORE_RESOURCE_URI, eObject.eResource().getURI().toString())) {
			try {
				QualifiedName qualifiedName = getQualifiedNameProvider().getFullyQualifiedName(eObject);
				if (qualifiedName != null) {
					HashMap<String, String> userData = new HashMap<>();
					userData.put("namespace", qualifiedName.getFirstSegment());
					System.out.println("Create ECore EObject Descriptions " + qualifiedName);
					IEObjectDescription descriptor = EObjectDescription.create(qualifiedName, eObject, userData);
					acceptor.accept(descriptor);
					if (qualifiedName.getSegmentCount() > 1) {
						System.out.println("Create ECore EObject Aliased Description " + qualifiedName.getLastSegment() + "->" + qualifiedName);
//						acceptor.accept(new AliasedEObjectDescription(
//								qualifiedName.skipFirst(1),		// Emfatic qualified names start at the package
//								descriptor));
					}
				}
				
			} catch (Exception exc) {
				LOG.error(exc.getMessage(), exc);
			}
		} else {
			try {
				QualifiedName qualifiedName = getQualifiedNameProvider().getFullyQualifiedName(eObject);
				if (qualifiedName != null) {
					HashMap<String, String> userData = new HashMap<>();
					userData.put("namespace", qualifiedName.getFirstSegment());
					System.out.println("Create Emfatic EObject Description " + qualifiedName);
					acceptor.accept(EObjectDescription.create(qualifiedName, eObject, userData));
				}
			} catch (Exception exc) {
				LOG.error(exc.getMessage(), exc);
			}
		}
		return true;
	}

}
