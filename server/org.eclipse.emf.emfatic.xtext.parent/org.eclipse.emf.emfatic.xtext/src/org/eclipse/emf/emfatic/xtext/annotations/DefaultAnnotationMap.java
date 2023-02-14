package org.eclipse.emf.emfatic.xtext.annotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
		String label = annt.label();
		Map<String, EmfaticAnnotation> annotations2 = getMap();
		if (annotations2.containsKey(label)) {
			LOG.error("Annoation label " + label + "is already in use. Annotation will be replaced");
		} else {
			LOG.info("Adding annotation with label " + label + " and uri " + annt.URI());
		}
		annotations2.put(label, annt);
	}
	
	
	@Override
	public EmfaticAnnotation removeAnnotation(String label) {
		return getMap().remove(label);
	}
	
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
		addAnnotation(new EcoreAnnotation());
		addAnnotation(new EmfaticMapAnnotation());
		addAnnotation(new GenModelAnnotation());
		addAnnotation(new MetaDataAnnotation());
	}
	
	@Override
	public List<String> labels() {
		return new ArrayList<>(getMap().keySet());
	}


	private final static Logger LOG = Logger.getLogger(DefaultAnnotationMap.class);
	
	@Inject
	private IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;
	
	private Resource resource;
	
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



	

}
