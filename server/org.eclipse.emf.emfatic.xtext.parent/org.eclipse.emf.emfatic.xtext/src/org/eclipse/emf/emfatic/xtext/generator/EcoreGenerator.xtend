package org.eclipse.emf.emfatic.xtext.generator

import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.emf.ecore.ETypeParameter
import org.eclipse.emf.ecore.EGenericType
import org.eclipse.emf.ecore.EAttribute
import java.util.List
import java.util.ArrayList
import org.eclipse.xtext.Grammar
import org.eclipse.xtext.GrammarUtil
import org.eclipse.xtext.generator.grammarAccess.*
import org.eclipse.xtext.xtext.generator.grammarAccess.GrammarAccessExtensions
import org.eclipse.emf.emfatic.xtext.services.EmfaticGrammarAccess
import java.util.Set
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.ETypedElement
import org.eclipse.emf.ecore.impl.ETypedElementImpl
import java.lang.annotation.ElementType
import org.eclipse.emf.ecore.EcorePackage

/**
 * Generate Emfatic files form Ecore metamodels
 */
class EcoreGenerator {
	
	def void generate(Resource resource, String name, IFileSystemAccess2 fsa) {
		val root = resource.contents.get(0) as EPackage
		fsa.generateFile(name, this.outputConfiguratioName(), '''
		«this.ePackage(root)»
		''')
		
	}
	
	def String outputConfiguratioName() {
		outputConfigName
	}
	
	def eAnnotation(EAnnotation ea) {
		return '''
		@«this.source(ea)»(
			«FOR me : ea.details SEPARATOR ','»
			«surround(me.key)»=«me.value === null ? '""' : surround(me.value)»
			«ENDFOR»
		)
		'''
	}
	
	def ePackage(EPackage p) {
		return '''
		@namespace(uri="«p.nsURI»", prefix="«p.nsPrefix»")
		«FOR ea : p.EAnnotations»«this.eAnnotation(ea)»«ENDFOR»
		package «p.name»;
		«FOR sp : p.ESubpackages»
        «this.ePackage(sp)»
		«ENDFOR»
		«FOR dt : p.EClassifiers.filter(typeof(EDataType))»
		
		«FOR ea : dt.EAnnotations»«this.eAnnotation(ea)»«ENDFOR»
		«this.eDataType(dt)»
		«ENDFOR»
		«FOR ec : p.EClassifiers.filter(typeof(EClass))»
		
		«FOR ea : ec.EAnnotations»«this.eAnnotation(ea)»«ENDFOR»
		«this.eClass(ec)»
		«ENDFOR»
		'''
	}
	
	def eDataType(EDataType dt) {
		if (dt instanceof EEnum) {
			return this.eEnum(dt)
		}
		return '''
		«IF !dt.serializable»transient «ENDIF»datatype «dt.name» : «dt.instanceClassName»;
		
		'''
	}
	
	def eEnum(EEnum en) {
		return '''
		enum «en.name» {
			«FOR el : en.ELiterals»
			«FOR ea : el.EAnnotations SEPARATOR ""»«this.eAnnotation(ea)»«ENDFOR»
			«IF el.literal === el.name»
			«el.name»;
			«ELSE»
			«el.name»=«el.literal»;
			«ENDIF»
			«ENDFOR»
		}
		
		'''
	}
	
	def eClass(EClass ec) {
		if (ec.instanceClassName === "java.util.Map$Entry"
			&& ec.EStructuralFeatures.size == 2
			&& ec.EStructuralFeatures.map[name].containsAll(#["key", "value"])
		) { // Map Entry
			var key = ec.getEStructuralFeature("key")
			var value = ec.getEStructuralFeature("value")
			return '''
			mapentry «ec.name» : «key.EType.name» -> «value.EType.name»;
			'''
		} else {
			return '''
			«
				IF ec.abstract»abstract «ENDIF»«
				IF ec.interface»interface «ELSE»class «ENDIF»«ec.name»«
				FOR tp : ec.ETypeParameters BEFORE "<" SEPARATOR ", " AFTER ">"»«typeParameter(tp)»«ENDFOR» «
				FOR sc : ec.EGenericSuperTypes BEFORE "extends " SEPARATOR ", " AFTER " " »«genericType(sc)»«ENDFOR»«
				IF ec.instanceClassName !== null»: «ec.instanceClassName» «ENDIF»{
				
				«FOR ea : ec.EAttributes»
					«attribute(ea)»
				«ENDFOR»
				«FOR ea : ec.EReferences»
					«reference(ea)»
				«ENDFOR»
				«FOR eo : ec.EOperations»
					«operation(eo)»
				«ENDFOR»
			
			}
			'''
		}
	}
	
	def attribute(EAttribute ea) {
		return '''
		«FOR an : ea.EAnnotations»«this.eAnnotation(an)»«ENDFOR»
		«modifier(ea)»attr «ea.EType.name»«multiplicity(ea)» «escape(ea.name)»«defaultValue(ea)»;'''
	}
	
	def reference(EReference er) {
		return '''
		«FOR an : er.EAnnotations»«this.eAnnotation(an)»«ENDFOR»
		«modifier(er)»«IF er.containment»val«ELSE»ref«ENDIF» «er.EType.name»«multiplicity(er)»«
			IF er.EOpposite !== null»#«er.EOpposite.name»«ENDIF» «escape(er.name)»;'''
	}
	
	def operation(EOperation eo) {
		return '''«modifier(eo)»op «IF eo.EType === null»void«ELSE»«eo.EType.name»«ENDIF» «eo.name»(«parameters(eo)»);'''
	}
	
	@Inject
	AnnotationMap annotations;
	
	
	@Inject
	EmfaticGrammarAccess grammarAccess;
	
	val outputConfigName = "/"
	
	var Set<String> keywords = null;
	
	private def surround(String value) {
		if (value.contains(" ")) {
			return String.format("\"%s\"", value)
		}
		return value;
	}

	private def source(EAnnotation ea) {
		var label = this.annotations.labelForUri(ea.getSource, ea.eResource)
		return label.orElse(ea.source)
	}	
	
	private def typeParameter(ETypeParameter etp) {
		var result = new StringBuilder
		result.append(etp.name)
		if (!etp.EBounds.empty) {
			result.append(" extends ")
			result.append(etp.EBounds.map[genericType].join(" & "));
		}
		return result.toString
	}
	
	private def genericType(EGenericType egt) {
		var result = new StringBuilder
		if (egt.EClassifier !== null || egt.ETypeParameter !== null) {
			result.append(egt.EClassifier !== null ? egt.EClassifier.name : typeParameter(egt.ETypeParameter));
			if (!egt.ETypeArguments.empty) {
				result.append("<")
				result.append(egt.ETypeArguments.map[genericType].join(", "))
				result.append(">")
			}
		} else {	// Wildcard
			result.append("?")
			if (egt.ELowerBound !== null) {
				result.append(" extends ")
				result.append(genericType(egt.ELowerBound))
			} else if (egt.EUpperBound !== null) {
				result.append(" super ")
				result.append(genericType(egt.EUpperBound))
			}
		}
		return result.toString
	}
	
	private def multiplicity(EStructuralFeature ef) {
		val List<String> multiplicity = new ArrayList;
		if (ef.lowerBound == 0) {
			if (ef.upperBound == -1) {
				multiplicity.add("*")
			} else if (ef.upperBound != 1) {
				multiplicity.add("0")
				multiplicity.add(String.valueOf(ef.upperBound))
			}
		} else if (ef.lowerBound == 1) {
			if (ef.upperBound == 1) {
				multiplicity.add("1")
			} else if (ef.upperBound == -1) {
				multiplicity.add("+")
			} else if (ef.upperBound == -2) {
				multiplicity.add("1")
				multiplicity.add("?")
			}
		} else if (ef.lowerBound == ef.upperBound) {
			multiplicity.add(String.valueOf(ef.lowerBound))
		} else { //if (ea.lowerBound != 0 &&  ea.upperBound != 1) {
			multiplicity.add(String.valueOf(ef.lowerBound))
			multiplicity.add(ef.upperBound == -1 ? "*" : String.valueOf(ef.upperBound))
		}
		return '''«FOR m : multiplicity BEFORE "[" SEPARATOR ".." AFTER "]"»«m»«ENDFOR»'''
	}
	
	private def modifier(ETypedElement te) {
		var sb = new StringBuilder
		if (!te.ordered) {
			sb.append("!ordered ")
		}
		if (!te.unique) {
			sb.append("!unique ")
		}
		return sb.toString
	}
	
	private def modifier(EStructuralFeature ef) {
		var sb = new StringBuilder(modifier(ef as ETypedElement))
		if (!ef.isChangeable) {
			sb.append("readonly ")
		}
		if (ef.derived) {
			sb.append("derived ")
		}
		if (ef.transient) {
			sb.append("transient ")
		}
		if (ef.unsettable) {
			sb.append("unsettable ")
		}
		if (ef.volatile) {
			sb.append("volatile ")
		}
		return sb.toString
	}
	
	private def modifier(EReference er) {
		var sb = new StringBuilder(modifier(er as EStructuralFeature))
		if (!er.resolveProxies) {
			sb.append("!resolve ")
		}
		return sb.toString
	}
	
	private def modifier(EAttribute ea) {
		var sb = new StringBuilder(modifier(ea as EStructuralFeature))
		if (ea.isID) {
			sb.append("id ")
		}
		return sb.toString
	}
	
	private def defaultValue(EAttribute ea) {
		if (ea.defaultValue === null) {
			return "";
		}
		return ''' = «IF ea.EType === EcorePackage.Literals.EBOOLEAN»"«ea.defaultValue»"«
		ELSEIF ea.EType === EcorePackage.Literals.ESTRING»"«ea.defaultValue»"«
		ELSEIF ea.EType === EcorePackage.Literals.ECHAR»'«ea.defaultValue»'«
		ELSE»«ea.defaultValue»«ENDIF»'''
	}
	
	private def escape(String name) {
		return keywords().contains(name) ? "~" + name : name
	}
	
	private def keywords() {
		if (keywords === null) {
			keywords = GrammarUtil.getAllKeywords(grammarAccess.grammar)
		}
		return keywords
	}
	
	private def parameters(EOperation eo) {
		return '''«FOR p : eo.EParameters SEPARATOR ', '»«p.EType.name» «p.name»«ENDFOR»'''
	}
}
