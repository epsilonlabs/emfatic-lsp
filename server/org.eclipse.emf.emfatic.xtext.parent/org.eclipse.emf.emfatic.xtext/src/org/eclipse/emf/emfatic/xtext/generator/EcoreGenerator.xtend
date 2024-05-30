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
		return '''
		«
			IF ec.abstract»abstract «ENDIF»«
			IF ec.interface»interface «ELSE»class «ENDIF»«ec.name»«
			FOR tp : ec.ETypeParameters BEFORE "<" SEPARATOR ", " AFTER ">"»«typeParameter(tp)»«ENDFOR» «
			FOR sc : ec.EGenericSuperTypes BEFORE "extends " SEPARATOR ", " AFTER " " »«genericType(sc)»«ENDFOR»«
			IF ec.instanceClassName !== null»: «ec.instanceClassName» «ENDIF»{
		
		}
		'''
	}
	
	@Inject
	AnnotationMap annotations;
	
	val outputConfigName = "/"
	
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
}
