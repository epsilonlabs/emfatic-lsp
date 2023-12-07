/*******************************************************************************
 * Copyright (c) 2023 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.Details;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;

import com.google.common.collect.Streams;
import com.google.inject.Singleton;

/**
 * The default implementation of {@link AnnotationMap}.
 * 
 * This class is responsible for managing annotations in each emfatic file, for providing the
 * core annotations provided by Emfatic (Ecore, GenModel, ExtendedMetaData and EmfaticAnnotationMap)
 * and for loading annotations provided via extension points.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
@Singleton
public class DefaultAnnotationMap implements AnnotationMap {
	
	public final static String EMFATIC_ANNOTATION_MAP_URI = "http://www.eclipse.org/emf/2004/EmfaticAnnotationMap";
	public final static String EMFATIC_ANNOTATION_MAP_LABEL = "EmfaticAnnotationMap";
	
	public DefaultAnnotationMap() {
		super();
		addProvidedAnnotations();
	}
	
	@Override
	public void refreshAnnotations(EList<Annotation> list, Resource resource) {
		URI uri = resource.getURI();
		this.userAnnotations.remove(uri);
		for (Annotation annt : list) {
			StringOrQualifiedID source = annt.getSource();
			if (source == null) {
				return;
			}
			String label = source.getId();
			if (label == null) {
				label = source.getLiteral();
			}
			if (Objects.equals(EMFATIC_ANNOTATION_MAP_LABEL, label)
					|| Objects.equals(EMFATIC_ANNOTATION_MAP_URI, label)) {
				for (Details kv : annt.getDetails()) {
					this.addUserAnnotation(new DefaultEmfaticAnnotation(kv.getKey(), kv.getValue()), uri);
				}
			}
		}
	}
	
	@Override
	public List<String> labels(Resource resource) {
		List<String> result = allAnnotations(resource)
				.map(a -> a.label())
				.collect(Collectors.toList());
		// Always return labels in same order
		Collections.sort(result);
		return result;
	}
	
	@Override
	public boolean isValidKey(String label, String name, EClass target, Resource resource) {
		String mapLabel = label.toLowerCase();
		EmfaticAnnotation emftcAnn = this.annotations.get(mapLabel);
		if (emftcAnn == null) {
			emftcAnn = this.userAnnotations.get(resource.getURI()).get(mapLabel);
			if (emftcAnn == null) {
				throw new IllegalArgumentException("The provided label does not match any known annotation");
			}
		}
		return emftcAnn.isValidKey(name, target);
	}
	
	@Override
	public List<String> keysFor(String label, EClass eClass) {
		String mapLabel = label.toLowerCase();
		EmfaticAnnotation emftcAnn =this.annotations.get(mapLabel);
		if (emftcAnn == null) {
			return Collections.emptyList();
		}
		return emftcAnn.keysFor(eClass);
	}
	
	@Override
	public String labelForUri(String uri, Resource resource) throws NoSuchElementException {
		return allAnnotations(resource)
				.filter(a -> Objects.equals(a.source(), uri))
				.findFirst()
				.orElseThrow()
				.label();
	}
	
	@Override
	public String uriForLabel(String id, Resource resource) {
		var result = this.annotations.get(id);
		if (result == null) {
			var user = this.userAnnotations.get(resource.getURI());
			if (user != null) {
				result = user.get(id);
				
			}
		}
		return result == null ? null : result.source();
	}

	
	@Override
	public boolean knowsLabel(String label, Resource resource) {
		return this.labels(resource).stream()
			.anyMatch(l -> l.toLowerCase().equals(label.toLowerCase()));
	}


	private final static Logger LOG = Logger.getLogger(DefaultAnnotationMap.class);
	
	Map<String, EmfaticAnnotation> annotations = new HashMap<>();
	Map<URI, Map<String, EmfaticAnnotation>> userAnnotations = new HashMap<>();
	
	/**
	 * Add annotations provided by default by Emfatic and by plugins that contribute to the 
	 * EMFATIC_ANNOTATION_EXTENSION_POINT
	 */
	private void addProvidedAnnotations() {
		LOG.debug("Adding annotations provided by Emfatic by default.");
		addAnnotation(new EcoreAnnotation());
		addAnnotation(new EmfaticMapAnnotation());
		addAnnotation(new GenModelAnnotation());
		addAnnotation(new MetaDataAnnotation());
		if (Platform.isRunning()) {
			LOG.debug("Adding annotations provided by contributing plugins.");
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = reg.getConfigurationElementsFor(AnnotationMap.EMFATIC_ANNOTATION_EXTENSION_POINT);
			for (IConfigurationElement element : elements) {
				Object annotationImpl = null;
				try {
					annotationImpl = element.createExecutableExtension("implementation");
				}
				catch (CoreException e) {
					LOG.error("Unable to instantiate Annotation provider from " + element.getAttribute("class"));
				}
				if (annotationImpl != null && annotationImpl instanceof EmfaticAnnotation) {
					addAnnotation((EmfaticAnnotation) annotationImpl);
				}
			}
		}
	}
	
	private void addAnnotation(EmfaticAnnotation annt) {
		// TODO We should support content assist for full uri labels!
		LOG.debug("Adding provided annotation with label " + annt.label());
		String label = annt.label().toLowerCase(); //labels are case insensitive
		if(this.annotations.containsKey(label)) {
			LOG.warn("Annoation label " + label + " is already in use by a provided Annotation. Annotation will be ignored");
			return;
		}
		this.annotations.put(label, annt);
	}
	
	private void addUserAnnotation(EmfaticAnnotation annt, URI uri) {
		// TODO We should support content assist for full uri labels!
		LOG.debug("Adding annotation via EmfaticAnnotationMap for resource " + uri);
		Map<String, EmfaticAnnotation> annotations = this.userAnnotations.computeIfAbsent(uri, (k) -> new HashMap<>());
		String label = annt.label().toLowerCase(); //labels are case insensitive
		if (annotations.containsKey(label)) {
			LOG.warn("User annoation label " + label + " is already in use. Annotation will be replaced");
		} else if(this.annotations.containsKey(label)) {
			LOG.warn("Annoation label " + label + " is already in use by a provided Annotation. Annotation will be ignored");
			return;
		}
		LOG.info("Adding user annotation with label " + label + " and uri " + annt.source());
		annotations.put(label, annt);
	}
	
	private Stream<EmfaticAnnotation> allAnnotations(Resource resource) {
		return Streams.concat(
					this.annotations.values().stream(),
					this.userAnnotations.getOrDefault(resource.getURI(), Collections.emptyMap())
						.values().stream()
					);
	}

}
