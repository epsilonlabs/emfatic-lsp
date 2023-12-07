package org.eclipse.emf.emfatic.xtext.ecore;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.xtext.util.OnChangeEvictingCache;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Twin {

	@Inject
	public Twin(OnChangeEvictingCache cache, EmfaticImport emfaticImport) {
		super();
		this.cache = cache;
		this.creator = new Creator(cache, emfaticImport);
		this.copier = new Copier(cache);
	}

	public void copy(final CompUnit model) {
		this.creator.doSwitch(model);
		try {
			this.cache.execWithoutCacheClear(model.eResource(), new IUnitOfWork.Void<Resource>() {

				@Override
				public void process(Resource state) throws Exception {
					copier.doSwitch(model);
				}
			});
			 
		} catch (IllegalArgumentException e) {
			LOG.error("Error copying attributes.", e);
		}
		
	}
	
	private final static Logger LOG = Logger.getLogger(Twin.class);
	
	private final OnChangeEvictingCache cache;
	private final Creator creator;
	private final Copier copier;
}
