package org.eclipse.emf.emfatic.xtext.conversion;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractIDValueConverter;

import com.google.inject.Inject;

public class EmfaticValueConverterService extends DefaultTerminalConverters implements IValueConverterService {
	
	
	@ValueConverter(rule = "ID")
	public IValueConverter<String> ID() {
		return idValueConverter;
	}
	
	@Inject
	private AbstractIDValueConverter idValueConverter;
	

}
