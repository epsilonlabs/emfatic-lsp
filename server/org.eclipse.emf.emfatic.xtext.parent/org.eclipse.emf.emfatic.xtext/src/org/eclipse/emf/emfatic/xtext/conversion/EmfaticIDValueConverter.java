package org.eclipse.emf.emfatic.xtext.conversion;
import java.util.Set;

import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.conversion.impl.AbstractIDValueConverter;
import org.eclipse.xtext.nodemodel.INode;

import com.google.common.collect.ImmutableSet;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class EmfaticIDValueConverter extends AbstractIDValueConverter {
	
	public EmfaticIDValueConverter() {
		super();
	}
	
	@Override
	protected String toEscapedString(String value) {
		if (mustEscape(value))
			return "~" + value;
		return value;
	}
	
	@Override
	public String toValue(String string, INode node) {
		if (string == null)
			return null;
		return string.startsWith("~") ? string.substring(1) : string;
	}
	
	@Override
	protected Set<String> computeValuesToEscape(Grammar grammar) {
		return ImmutableSet.copyOf(GrammarUtil.getAllKeywords(grammar));
	}

	@Override
	protected boolean mustEscape(String value) {
		return getValuesToEscape().contains(value);
	}
}