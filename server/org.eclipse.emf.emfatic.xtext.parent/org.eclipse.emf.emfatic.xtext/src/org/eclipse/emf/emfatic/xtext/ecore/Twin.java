package org.eclipse.emf.emfatic.xtext.ecore;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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
		this.structure = new Structure(cache, emfaticImport);
		this.content = new Content(cache);
	}

	public EPackage copy(final CompUnit model) {
		EPackage root = (EPackage) this.structure.doSwitch(model);
		try {
			this.cache.execWithoutCacheClear(model.eResource(), new IUnitOfWork.Void<Resource>() {

				@Override
				public void process(Resource state) throws Exception {
					content.doSwitch(model);
				}
			});
			 
		} catch (IllegalArgumentException e) {
			LOG.error("Error copying attributes.", e);
		}
		return root;
//		var rs = model.eResource().getResourceSet();
//		URI ecoreUri = model.eResource().getURI().appendFileExtension("ecore");
//		var r = rs.getResource(ecoreUri, false);
//		if (r == null) {
//			r = rs.createResource(ecoreUri);
//		}
//		r.getContents().add((EObject) root);
//		try {
//			r.save(null);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public EObject ecoreElement(EObject emfatciElement) {
		return this.cache.get(emfatciElement, emfatciElement.eResource(), () -> null);
	}
	
	private final static Logger LOG = Logger.getLogger(Twin.class);
	
	private final OnChangeEvictingCache cache;
	private final Structure structure;
	private final Content content;
}
