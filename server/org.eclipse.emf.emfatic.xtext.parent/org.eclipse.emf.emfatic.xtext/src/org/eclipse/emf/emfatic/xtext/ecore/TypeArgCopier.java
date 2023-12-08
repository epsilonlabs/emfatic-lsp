package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public interface TypeArgCopier {

	void configure(EGenericType gt);
	
	TypeArgCopier load(OnChangeEvictingCache cache);

}
