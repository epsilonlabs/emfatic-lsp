package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class WildcardCopier implements TypeArgCopier {

	public WildcardCopier(Wildcard source) {
		this(source, false, false, null);
	}
	
	@Override
	public void configure(ETypedElement gt) {
		throw new UnsupportedOperationException("WilcardCopier can not be used to configure ETypedElements");
	}
	
	@Override
	public void configure(EGenericType gt) {
		if (this.targetBound != null) {
			var taBound = EcoreFactory.eINSTANCE.createEGenericType();
			this.targetBound.configure(taBound);
			if (this.extnds) {
				gt.setEUpperBound(taBound);
			}
			if (this.spr) {
				gt.setEUpperBound(taBound);
			}
		}
	}
	
	@Override
	public WildcardCopier load(OnChangeEvictingCache cache) {
		var s = false;
		var e = false;
		BoundClassifierExceptWildcardCopier targetBound = null;
		if (this.source.getDir() != null) {
			s = this.source.getDir().isSuper();
			e = !s;
			targetBound = cache.get(this.source.getBound(), this.source.eResource(), () -> null);
			if (targetBound == null) {
				throw new IllegalArgumentException("Target element not found for " + this.source.getBound());
			}
		}
		if (targetBound != null) {
			targetBound = targetBound.load(cache);
		}
		return new WildcardCopier(this.source, e, s, targetBound);
	}

	private final Wildcard source;
	private final boolean extnds;
	private final boolean spr;
	private final BoundClassifierExceptWildcardCopier targetBound;

	private WildcardCopier(Wildcard source, boolean extnds, boolean spr, BoundClassifierExceptWildcardCopier targetBound) {
		super();
		this.source = source;
		this.extnds = extnds;
		this.spr = spr;
		this.targetBound = targetBound;
	}

}
