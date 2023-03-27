/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.linking;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.PackageDecl;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.linking.lazy.LazyLinker;

import com.google.inject.Inject;

public class EmfaticLinker extends LazyLinker {
	
	
	public AnnotationMap getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationMap annotations) {
		this.annotations = annotations;
	}

	@Override
	protected void doLinkModel(EObject model, IDiagnosticConsumer consumer) {
		super.doLinkModel(model, consumer);
		if (model instanceof CompUnit) {
			CompUnit unit = (CompUnit) model;
			PackageDecl pckg = unit.getPackage();
			if (pckg != null) {
				this.annotations.setResource(model.eResource());
				for (Annotation annt : pckg.getAnnotations()) {
					annotations.addAnnotation(annt);
				}
			}
		}
	}
	
	private static final Logger LOG = Logger.getLogger(EmfaticLinker.class);
	
	@Inject
	private AnnotationMap annotations;
}
