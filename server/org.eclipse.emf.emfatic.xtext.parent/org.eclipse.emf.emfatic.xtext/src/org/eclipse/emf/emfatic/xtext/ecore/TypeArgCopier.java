package org.eclipse.emf.emfatic.xtext.ecore;

import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.xtext.util.OnChangeEvictingCache;

public interface TypeArgCopier {

	void configure(EGenericType gt);
	
	void configure(ETypedElement gt);
	
	TypeArgCopier load(OnChangeEvictingCache cache);

}
