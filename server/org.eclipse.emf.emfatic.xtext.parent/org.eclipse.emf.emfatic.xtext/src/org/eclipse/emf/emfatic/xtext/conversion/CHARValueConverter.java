package org.eclipse.emf.emfatic.xtext.conversion;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

public class CHARValueConverter extends AbstractLexerBasedConverter<Character> {

	@Override
	public Character toValue(String string, INode node) throws ValueConverterException {
		if (string.length() == 2) {
			return null;
		}
		return string.charAt(1);
	}

}
