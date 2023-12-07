package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public class WildcardCopier extends TypeArgCopier {

	public WildcardCopier(Wildcard source) {
		this(source, false, false, null);
	}
	
	WildcardCopier load(OnChangeEvictingCache cache) {
		var e = false;
		var s = false;
		ClassifierCopier cc = null;
		if (this.source.getDir() != null) {
			e = this.source.getDir().isExtnds();
			s = this.source.getDir().isSper();
			cc = cache.get(this.source.getBound(), this.source.eResource(), () -> null);
		}
		if (e && s) {
			throw new IllegalStateException("A Wildcard (TypeArg) can't both be 'extends' and 'super'.");
		}
		return new WildcardCopier(this.source, e, s, cc.load(cache));
	}
	
	@Override
	void configure(EGenericType gt) {
		var ta = EcoreFactory.eINSTANCE.createEGenericType();
		if (this.extnds || this.spr) {
			var taBound = EcoreFactory.eINSTANCE.createEGenericType();
			this.bound.configure(taBound);
			if (this.extnds) {
				ta.setEUpperBound(taBound);
			}
			if (this.spr) {
				ta.setEUpperBound(taBound);
			}
		}
		gt.getETypeArguments().add(ta);
		
	}
	
	private WildcardCopier(Wildcard source, boolean extnds, boolean spr, ClassifierCopier bound) {
		super();
		this.source = source;
		this.extnds = extnds;
		this.spr = spr;
		this.bound = bound;
	}

	private final Wildcard source;
	private final boolean extnds;
	private final boolean spr;
	private final ClassifierCopier bound;
	
}
