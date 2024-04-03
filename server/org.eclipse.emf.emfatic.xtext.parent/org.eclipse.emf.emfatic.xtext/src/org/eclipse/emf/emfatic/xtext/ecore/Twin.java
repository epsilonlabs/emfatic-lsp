package org.eclipse.emf.emfatic.xtext.ecore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Twin {

	private final Elements elements;

	private final Content content;

	@Inject
	public Twin(EmfaticImport emfaticImport, AnnotationMap annotations) {
		super();
		this.elements = new Elements(emfaticImport);
		this.content = new Content(annotations);
	}

	public void copy(final CompUnit model) {
		this.elements.stop();
		this.content.stop();
		if (this.copier != null) {
			this.copier.cancel(false);
		}
		if (this.ecore != null) {
			this.ecore.getContents().clear();
		}
		this.copier = CompletableFuture.supplyAsync(() -> {
			return elements.copy(model);
		});
		this.copier.exceptionally(ex -> {
				if (ex instanceof CancellationException) {
					LOG.info("Copier was canceled");
				} else {
					LOG.error("There was an error translating emfatic to Ecore", ex);
				}
				return new HashMap<Object, Object>();
			})
			.thenApplyAsync(cache -> {
				return this.content.copy(cache, model);
			})
			.exceptionally(ex -> {
				if (ex instanceof CancellationException) {
					LOG.info("Copier was canceled");
				} else {
					LOG.error("There was an error translating emfatic to Ecore", ex);
				}
				return new HashMap<Object, Object>();
			})
			.thenAcceptAsync(cache -> {
				if (!cache.isEmpty()) {
					LOG.info("Saving the generated Ecore elements");
					var ecoreURI = model.eResource().getURI().toString().replace(".lspemf", ".ecore");
					ecore = model.eResource().getResourceSet().createResource(URI.createURI(ecoreURI));
					ecore.getContents().add((EObject) cache.get(model.getPackage()));
					cache.clear();
				}
			});
	}
	
	public void save() {
		if (this.ecore != null && !this.ecore.getContents().isEmpty()) {
			try {
				this.ecore.save(null);
			} catch (IOException e) {
				LOG.error("There was an error translating emfatic to Ecore", e);
			}
		}
	}

	private final static Logger LOG = Logger.getLogger(Twin.class);
	private Resource ecore;

	private CompletableFuture<Map<Object, Object>> copier;

}
