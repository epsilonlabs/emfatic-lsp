/*******************************************************************************
 * Copyright (c) 2022 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.scoping;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

/**
 * This {@link IDefaultResourceDescriptionStrategy} implementation adds the 
 * namespace to the descriptor's user data so we can later use this information
 * to filter them.
 * 
 * @author Horacio Hoyos Rodriguez
 * @see EmfaticINALSP
 * 
 */
public class EmfaticRDS extends DefaultResourceDescriptionStrategy {
	
	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(EmfaticRDS.class);

	@Override
	public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
		if (getQualifiedNameProvider() == null) {
			return false;
		}
		try {
			QualifiedName qualifiedName = getQualifiedNameProvider().getFullyQualifiedName(eObject);
			if (qualifiedName != null) {
				HashMap<String, String> userData = new HashMap<>();
				userData.put("namespace", qualifiedName.getFirstSegment());
				acceptor.accept(EObjectDescription.create(qualifiedName, eObject, userData));
			}
		} catch (Exception exc) {
			LOG.error(exc.getMessage(), exc);
			return false;
		}
		return true;
	}

}
