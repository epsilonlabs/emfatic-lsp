package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.Annotation;
import org.eclipse.emf.emfatic.xtext.emfatic.KeyEqualsValue;
import org.eclipse.emf.emfatic.xtext.emfatic.StringOrQualifiedID;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class DefaultAnnotationMap implements AnnotationMap {
	
	public final static String EMFATIC_ANNOTATION_MAP_URI = "http://www.eclipse.org/emf/2004/EmfaticAnnotationMap";
	public final static String EMFATIC_ANNOTATION_MAP_LABEL = "EmfaticAnnotationMap";
	
	public DefaultAnnotationMap() {
		super();
	}
	
	@Override
	public void addAnnotation(Annotation annt) {
		StringOrQualifiedID source = annt.getSource();
		if (source == null) {
			return;
		}
		// EmfaticAnnotationMap should be an identifier
		String label = source.getId();
		if (label == null) {
			return;
		}
		if (Objects.equal(EMFATIC_ANNOTATION_MAP_LABEL, label)) {
			for (KeyEqualsValue kv : annt.getKeyValues()) {
				this.addAnnotation(new DefaultEmfaticAnnotation(kv.getKey(), kv.getValue()));
			}
		}
	}
	
	@Override
	public void addAnnotation(EmfaticAnnotation annt) {
		// TODO We should support content assist for full uri labels! 
		String label = annt.label().toLowerCase(); //labels are case insensitive
		Map<String, EmfaticAnnotation> annotations = getMap();
		if (annotations.containsKey(label)) {
			LOG.error("Annoation label " + label + "is already in use. Annotation will be replaced");
		} else if(this.defaultAnnotations.containsKey(label)) {
			LOG.warn("Annoation label " + label + "is already in use by a provided Annotation. Annotation will be ignored");
			return;
		}
		else {
			LOG.info("Adding annotation with label " + label + " and uri " + annt.source());
		}
		annotations.put(label, annt);
	}
	
	
	@Override
	public EmfaticAnnotation removeAnnotation(String label) {
		return getMap().remove(label);
	}
	
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
		addProvidedAnnotations();
	}
	
	@Override
	public List<String> labels() {
		
		List<String> result = Stream.concat(
					getMap().values().stream(),
					this.defaultAnnotations.values().stream())
				.map(v -> v.label())
				.collect(Collectors.toList());
		// Always return labels in same order
		Collections.sort(result);
		return result;
	}
	
	@Override
	public List<String> keysFor(String label, EClass eClass) {
		String mapLabel = label.toLowerCase();
		EmfaticAnnotation emftcAnn = getMap().getOrDefault(
				mapLabel,
				this.defaultAnnotations.get(mapLabel));
		if (emftcAnn == null) {
			return Collections.emptyList();
		}
		return emftcAnn.keysFor(eClass);
	}


	private final static Logger LOG = Logger.getLogger(DefaultAnnotationMap.class);
	
	@Inject
	private IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;
	
	private Resource resource;
	
	Map<String, EmfaticAnnotation> defaultAnnotations = new HashMap<>();
	
	private Map<String, EmfaticAnnotation> getMap() {
		if (this.resource == null) {
			throw new IllegalStateException("The DefaultAnnotationMap resource must not be null");
		}
		return cache.get("AnnotationMap", this.resource, new Provider<Map<String, EmfaticAnnotation>>() {
			@Override
			public Map<String, EmfaticAnnotation> get() {
				return new HashMap<>();
			}
		});
	}
	
	private void addProvidedAnnotations() {
		// This should be loaded once, so we probably need a separate map, we only want to cache
		// the EmfaticAnnotationMap ones since this are the ones that can change on edit.
		addProvidedAnnotation(new EcoreAnnotation());
		addProvidedAnnotation(new EmfaticMapAnnotation());
		addProvidedAnnotation(new GenModelAnnotation());
		addProvidedAnnotation(new MetaDataAnnotation());
		IExtensionRegistry reg = Platform.getExtensionRegistry();
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(AnnotationMap.EMFATIC_ANNOTATION_EXTENSION_POINT);
	    for(IConfigurationElement element: elements) {
	    	System.out.println(element.getAttribute("implementation"));
	    	Object executable = null;
			try {
				executable = element.createExecutableExtension("implementation");
			} catch (CoreException e) {
				LOG.error("Unable to instantiate Annotation provider from " + element.getAttribute("class"));
			}
			if (executable != null && executable instanceof EmfaticAnnotation) {
				addProvidedAnnotation((EmfaticAnnotation) executable);
			}
	    }
	}

	private void addProvidedAnnotation(EmfaticAnnotation annt) {
		String label = annt.label().toLowerCase(); //labels are case insensitive
		if (defaultAnnotations.containsKey(label)) {
			LOG.error("Annoation label " + label + "is already in use. Annotation will be replaced");
		} else {
			LOG.info("Adding annotation with label " + label + " and uri " + annt.source() + " via label");
		}
		defaultAnnotations.put(label, annt);
//		label = annt.source();
//		if (defaultAnnotations.containsKey(label)) {
//			LOG.error("Annoation source " + label + "is already in use. Annotation will be replaced");
//		} else {
//			LOG.info("Adding annotation with label " + label + " and uri " + annt.source()  + " via uri");
//		}
		defaultAnnotations.put(label, annt);
	}

}
