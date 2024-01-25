/*
 * generated by Xtext 2.28.0
 */
package org.eclipse.emf.emfatic.xtext;

import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap;
import org.eclipse.emf.emfatic.xtext.annotations.DefaultAnnotationMap;
import org.eclipse.emf.emfatic.xtext.conversion.EmfaticIDValueConverter;
import org.eclipse.emf.emfatic.xtext.conversion.EmfaticValueConverterService;
import org.eclipse.emf.emfatic.xtext.ecore.Twin;
import org.eclipse.emf.emfatic.xtext.linking.EmfaticLinker;
import org.eclipse.emf.emfatic.xtext.naming.EmfaticQNP;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticGSP;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticINALSP;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticImport;
import org.eclipse.emf.emfatic.xtext.scoping.EmfaticRDS;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.impl.AbstractIDValueConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used at runtime / without the 
 * Equinox extension registry.
 */
public class EmfaticRuntimeModule extends AbstractEmfaticRuntimeModule {

	
	public Class<? extends IGlobalScopeProvider> bindIGlobalScopeProvider() {
		return EmfaticGSP.class;
	}
	
	/**
	 * Bind I default resource description strategy.
	 *
	 * @return the class<? extends I default resource description strategy>
	 */
	public Class<? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy() {
		return EmfaticRDS.class;
	}
	
	public void configureIScopeProviderDelegate(Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE)).to(EmfaticINALSP.class);
	}
	
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return EmfaticQNP.class;
	}
	
	
	public Class<? extends org.eclipse.xtext.linking.ILinker> bindILinker() {
		return EmfaticLinker.class;
	}
	
	public void configureAnnotationsProvider(Binder binder) {
		binder.bind(AnnotationMap.class).to(DefaultAnnotationMap.class);
	}
	
	public void configureTwinProvider(Binder binder) {
		binder.bind(Twin.class);
	}
	
	public void configureEmfaticImport(Binder binder) {
		binder.bind(EmfaticImport.class);
	}
	
	public Class<? extends IValueConverterService> bindIValueConverterService() {
	    return EmfaticValueConverterService.class;
	}
	
	public Class<? extends AbstractIDValueConverter> bindAbstractIDValueConverter() {
		return EmfaticIDValueConverter.class;
	}
}
