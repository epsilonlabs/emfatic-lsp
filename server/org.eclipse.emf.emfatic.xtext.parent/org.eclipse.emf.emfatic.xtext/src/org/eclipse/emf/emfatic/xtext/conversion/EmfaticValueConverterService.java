package org.eclipse.emf.emfatic.xtext.conversion;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractIDValueConverter;

import com.google.inject.Inject;

public class EmfaticValueConverterService extends DefaultTerminalConverters implements IValueConverterService {
	
	@Inject
	public EmfaticValueConverterService(AbstractIDValueConverter idValueConverter,
			CHARValueConverter charValueConverter) {
		super();
		this.idValueConverter = idValueConverter;
		this.charValueConverter = charValueConverter;
	}

	@ValueConverter(rule = "ID")
	public IValueConverter<String> ID() {
		return idValueConverter;
	}
	
	@ValueConverter(rule = "CHAR")
	public IValueConverter<Character> CHAR() {
		return charValueConverter;
	}
	
	private final AbstractIDValueConverter idValueConverter;
	
	private final CHARValueConverter charValueConverter;
	

}
