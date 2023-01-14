package org.eclipse.emf.emfatic.xtext.scoping;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.AliasedEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

/**
 * The EmfaticAliasingScope has two purposes:
 * <ol>
 * 	<li> Create {@link AliasedEObjectDescription} that use the import alias
 *	<li> Change to the original name when searching for elements via alias
 * </ol>
 * @author Horacio Hoyos Rodriguez
 *
 */
public class EmfaticAliasingScope implements IScope {
	
	
	public EmfaticAliasingScope(IScope delegate, Aliases imports) {
		super();
		this.delegate = delegate;
		this.imports = imports;
	}
	
	public IEObjectDescription getSingleElement(QualifiedName name) {
		QualifiedName alias = originalName(name);
		return delegate.getSingleElement(alias);
	}
	
	public Iterable<IEObjectDescription> getElements(QualifiedName name) {
		QualifiedName alias = originalName(name);
		return delegate.getElements(alias);
	}
	
	public IEObjectDescription getSingleElement(EObject object) {
		return delegate.getSingleElement(object);
	}
	
	public Iterable<IEObjectDescription> getElements(EObject object) {
		return delegate.getElements(object);
	}
	
	public Iterable<IEObjectDescription> getAllElements() {
		List<IEObjectDescription> result = new ArrayList<>();
		delegate.getAllElements().forEach(eod -> {
			QualifiedName fqn = eod.getName();
			String namespace = fqn.getFirstSegment();
			if (this.imports.hasOriginal(namespace)) {
				QualifiedName alias = QualifiedName.create(imports.getAlias(namespace))
						.append(fqn.skipFirst(1));
				if (LOG.isDebugEnabled()) {
					LOG.debug("Adding AliasedEObjectDescription with name " + alias + " for " + eod.getName());
				}
				result.add(new AliasedEObjectDescription(alias, eod));
			} else {
				result.add(eod);
			}
		});
		return result;
	}
	
	private static final Logger LOG = Logger.getLogger(EmfaticAliasingScope.class);
	
	private final IScope delegate;
	private final Aliases imports;
	
	
	private QualifiedName originalName(QualifiedName name) {
		QualifiedName real = name;
		String namespace = name.getFirstSegment();
		if(this.imports.hasAlias(namespace)) {
			real = QualifiedName.create(imports.getOrginal(namespace))
					.append(name.skipFirst(1));
			LOG.debug("Getting real name for " + name + " result: " + real);
		}
		return real;
	}

}
