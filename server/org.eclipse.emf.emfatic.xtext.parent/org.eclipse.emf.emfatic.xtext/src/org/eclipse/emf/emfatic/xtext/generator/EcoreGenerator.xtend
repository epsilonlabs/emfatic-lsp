package org.eclipse.emf.emfatic.xtext.generator

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.emf.emfatic.xtext.annotations.AnnotationMap
import com.google.inject.Inject
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EClass

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
		class «ec.name» «FOR sc : ec.ESuperTypes BEFORE "extends " SEPARATOR ", " AFTER " " »«sc.name»«ENDFOR»{
		
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

	private def String source(EAnnotation ea) {
		var label = this.annotations.labelForUri(ea.getSource, ea.eResource)
		return label.orElse(ea.source)
	}	
}
