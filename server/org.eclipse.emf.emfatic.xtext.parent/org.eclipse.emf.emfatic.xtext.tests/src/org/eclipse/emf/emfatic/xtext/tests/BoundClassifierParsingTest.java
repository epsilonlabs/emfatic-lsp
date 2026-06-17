/*
 * Test for BoundClassifier (generics) in Emfatic
 */
package org.eclipse.emf.emfatic.xtext.tests;

import java.util.Objects;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfatic.xtext.emfatic.Attribute;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundClassifierExceptWildcard;
import org.eclipse.emf.emfatic.xtext.emfatic.BoundDataTypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassMemberDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.ClassRefWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.CompUnit;
import org.eclipse.emf.emfatic.xtext.emfatic.ExtendsOrSuper;
import org.eclipse.emf.emfatic.xtext.emfatic.FeatureDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.Operation;
import org.eclipse.emf.emfatic.xtext.emfatic.Reference;
import org.eclipse.emf.emfatic.xtext.emfatic.ResultType;
import org.eclipse.emf.emfatic.xtext.emfatic.TopLevelDecl;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeArg;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeBoundsInfo;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParam;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeParamsInfo;
import org.eclipse.emf.emfatic.xtext.emfatic.TypeWithMulti;
import org.eclipse.emf.emfatic.xtext.emfatic.Wildcard;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

/**
 * Test suite for BoundClassifier expressions, which represent generic types
 * (type parameters with bounds, type arguments, and wildcards).
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(EmfaticInjectorProvider.class)
class BoundClassifierParsingTest {

	@Inject
	private ParseHelper<CompUnit> parseHelper;

	/**
	 * Test simple type parameter on a class: class Container<T> { ... }
	 */
	@Test
	void simpleTypeParameter() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Container<T> {
				attr T value;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		Assertions.assertNotNull(unit.getPackage());
		
		// Get Container class
		ClassDecl container = findClassByName(unit, "Container");
		Assertions.assertNotNull(container, "Container class should be found");
		
		// Check type parameters
		TypeParamsInfo typeParams = container.getTypeParamsInfo();
		Assertions.assertNotNull(typeParams, "Container should have type parameters");
		EList<TypeParam> params = typeParams.getTp();
		Assertions.assertEquals(1, params.size(), "Container should have one type parameter");
		
		TypeParam typeParamT = params.get(0);
		Assertions.assertEquals("T", typeParamT.getName());
		Assertions.assertNull(typeParamT.getTypeBoundsInfo(), "T should have no bounds");
	}

	/**
	 * Test type parameter with bounds: class Container<T extends Bound> { ... }
	 */
	@Test
	void typeParameterWithBounds() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Bound { }
			
			class Container<T extends Bound> {
				attr T value;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl container = findClassByName(unit, "Container");
		Assertions.assertNotNull(container);
		
		TypeParamsInfo typeParams = container.getTypeParamsInfo();
		Assertions.assertNotNull(typeParams);
		
		TypeParam typeParamT = typeParams.getTp().get(0);
		Assertions.assertEquals("T", typeParamT.getName());
		
		TypeBoundsInfo bounds = typeParamT.getTypeBoundsInfo();
		Assertions.assertNotNull(bounds, "T should have bounds");
		EList<BoundClassifierExceptWildcard> boundList = bounds.getTb();
		Assertions.assertEquals(1, boundList.size());
		
		BoundClassifierExceptWildcard bound = boundList.get(0);
		ClassDecl boundClass = (ClassDecl) bound.getBound();
		Assertions.assertEquals("Bound", boundClass.getName());
		Assertions.assertEquals(0, bound.getTypeArgs().size(), "Bound should have no type args");
	}

	/**
	 * Test multiple type parameters: class Container<T, U> { ... }
	 */
	@Test
	void multipleTypeParameters() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Container<T, U> {
				attr T first;
				attr U second;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl container = findClassByName(unit, "Container");
		Assertions.assertNotNull(container);
		
		TypeParamsInfo typeParams = container.getTypeParamsInfo();
		Assertions.assertNotNull(typeParams);
		
		EList<TypeParam> params = typeParams.getTp();
		Assertions.assertEquals(2, params.size(), "Container should have two type parameters");
		Assertions.assertEquals("T", params.get(0).getName());
		Assertions.assertEquals("U", params.get(1).getName());
	}

	/**
	 * Test type parameter with multiple bounds: 
	 * class Container<T extends Bound1 & Bound2> { ... }
	 */
	@Test
	void typeParameterWithMultipleBounds() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Bound1 { }
			class Bound2 { }
			
			class Container<T extends Bound1 & Bound2> {
				attr T value;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl container = findClassByName(unit, "Container");
		Assertions.assertNotNull(container);
		
		TypeParamsInfo typeParams = container.getTypeParamsInfo();
		TypeParam typeParamT = typeParams.getTp().get(0);
		
		TypeBoundsInfo bounds = typeParamT.getTypeBoundsInfo();
		Assertions.assertNotNull(bounds);
		
		EList<BoundClassifierExceptWildcard> boundList = bounds.getTb();
		Assertions.assertEquals(2, boundList.size(), 
			"T should have two bounds");
		
		ClassDecl bound1 = (ClassDecl) boundList.get(0).getBound();
		ClassDecl bound2 = (ClassDecl) boundList.get(1).getBound();
		Assertions.assertEquals("Bound1", bound1.getName());
		Assertions.assertEquals("Bound2", bound2.getName());
	}

	/**
	 * Test type argument when using a generic type:
	 * attr Container<String> field;
	 */
	@Test
	void typeArgumentOnAttribute() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			datatype String : java.lang.String;
			
			class Container<T> { }
			
			class MyClass {
				attr Container<String> field;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl myClass = findClassByName(unit, "MyClass");
		Assertions.assertNotNull(myClass);
		
		// Find the field attribute
		Attribute field = findAttributeByName(myClass, "field");
		Assertions.assertNotNull(field);
		
		// Check type with type arguments
		BoundDataTypeWithMulti typeWithMulti = field.getTypeWithMulti();
		Object boundObj = typeWithMulti.getBound();
		Assertions.assertNotNull(boundObj);
		
		// Cast to BoundClassifierExceptWildcard since Container<String> is a reference
		BoundClassifierExceptWildcard bound = (BoundClassifierExceptWildcard) boundObj;
		ClassDecl containerType = (ClassDecl) bound.getBound();
		Assertions.assertEquals("Container", containerType.getName());
		
		// Check type arguments
		EList<TypeArg> typeArgs = bound.getTypeArgs();
		Assertions.assertEquals(1, typeArgs.size(), 
			"Container<String> should have one type argument");
	}

	/**
	 * Test wildcard type argument: attr Container<?> field;
	 */
	@Test
	void wildcardTypeArgument() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Container<T> { }
			
			class MyClass {
				ref Container<?> field;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl myClass = findClassByName(unit, "MyClass");
		Assertions.assertNotNull(myClass);
		
		// Find the field reference
		Reference field = findReferenceByName(myClass, "field");
		Assertions.assertNotNull(field);
		
		ClassRefWithMulti typeWithMulti = field.getTypeWithMulti();
		BoundClassExceptWildcard bound = typeWithMulti.getType();
		Assertions.assertNotNull(bound);
		
		ClassDecl containerType = (ClassDecl) bound.getBound();
		Assertions.assertEquals("Container", containerType.getName());
		
		bound = field.getTypeWithMulti().getType();
		EList<TypeArg> typeArgs = bound.getTypeArgs();
		Assertions.assertEquals(1, typeArgs.size());
		
		TypeArg arg = typeArgs.get(0);
		Assertions.assertInstanceOf(Wildcard.class, arg, 
			"Type argument should be a wildcard");
		
		Wildcard wildcard = (Wildcard) arg;
		Assertions.assertNull(wildcard.getBound(), 
			"Bare wildcard should have no bound");
	}

	/**
	 * Test wildcard with upper bound: attr Container<? extends Bound> field;
	 */
	@Test
	void wildcardWithExtendsBound() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Bound { }
			class Container<T> { }
			
			class MyClass {
				ref Container<? extends Bound> field;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl myClass = findClassByName(unit, "MyClass");
		Reference field = findReferenceByName(myClass, "field");
		Assertions.assertNotNull(field);
		
		BoundClassExceptWildcard bound = field.getTypeWithMulti().getType();
		EList<TypeArg> typeArgs = bound.getTypeArgs();
		Assertions.assertEquals(1, typeArgs.size());
		
		Wildcard wildcard = (Wildcard) typeArgs.get(0);
		Assertions.assertNotNull(wildcard.getBound(), 
			"Wildcard should have a bound");
		
		ClassDecl boundClass = (ClassDecl) wildcard.getBound().getBound();
		Assertions.assertEquals("Bound", boundClass.getName());
		
		ExtendsOrSuper dir = wildcard.getDir();
		Assertions.assertNotNull(dir);
		Assertions.assertFalse(dir.isSuper(), 
			"Direction should be 'extends'");
	}

	/**
	 * Test wildcard with lower bound: attr Container<? super Bound> field;
	 */
	@Test
	void wildcardWithSuperBound() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Bound { }
			class Container<T> { }
			
			class MyClass {
				ref Container<? super Bound> field;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl myClass = findClassByName(unit, "MyClass");
		Reference field = findReferenceByName(myClass, "field");
		
		BoundClassExceptWildcard bound = field.getTypeWithMulti().getType();
		EList<TypeArg> typeArgs = bound.getTypeArgs();
		
		Wildcard wildcard = (Wildcard) typeArgs.get(0);
		ExtendsOrSuper dir = wildcard.getDir();
		Assertions.assertNotNull(dir);
		Assertions.assertTrue(dir.isSuper(), 
			"Direction should be 'super'");
	}

	/**
	 * Test nested type arguments: attr Container<MyClass<String>> field;
	 */
	@Test
	void nestedTypeArguments() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			datatype String : java.lang.String;
			
			class InnerContainer<T> { }
			class OuterContainer<U> { }
			
			class MyClass {
				ref OuterContainer<InnerContainer<String>> field;
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl myClass = findClassByName(unit, "MyClass");
		Reference field = findReferenceByName(myClass, "field");
		
		BoundClassExceptWildcard outerBound = field.getTypeWithMulti().getType();
		ClassDecl outerContainer = (ClassDecl) outerBound.getBound();
		Assertions.assertEquals("OuterContainer", outerContainer.getName());
		
		EList<TypeArg> outerArgs = outerBound.getTypeArgs();
		Assertions.assertEquals(1, outerArgs.size());
		
		// Inner type argument should be BoundClassifierExceptWildcard
		BoundClassifierExceptWildcard innerBound = 
			(BoundClassifierExceptWildcard) outerArgs.get(0);
		ClassDecl innerContainer = (ClassDecl) innerBound.getBound();
		Assertions.assertEquals("InnerContainer", innerContainer.getName());
		
		// InnerContainer itself has type argument
		EList<TypeArg> innerArgs = innerBound.getTypeArgs();
		Assertions.assertEquals(1, innerArgs.size());
	}

	/**
	 * Test type parameter reference in operation return type:
	 * op List<T> getItems();
	 */
	@Test
	void typeParameterInOperationReturnType() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class List<T> { }
			
			class Container<T> {
				op List<T> getItems();
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl container = findClassByName(unit, "Container");
		Assertions.assertNotNull(container);
		
		// Find getItems operation
		Operation op = findOperationByName(container, "getItems");
		Assertions.assertNotNull(op);
		
		ResultType resultType = op.getResultType();
		Assertions.assertNotNull(resultType);
		Assertions.assertFalse(resultType.isVoid());
		
		TypeWithMulti typeWithMulti = resultType.getType();
		Assertions.assertNotNull(typeWithMulti);
		
		BoundClassifierExceptWildcard bound = typeWithMulti.getType();
		ClassDecl listType = (ClassDecl) bound.getBound();
		Assertions.assertEquals("List", listType.getName());
		
		EList<TypeArg> typeArgs = bound.getTypeArgs();
		Assertions.assertEquals(1, typeArgs.size(), 
			"List<T> should have one type argument");
	}

	/**
	 * Test complex scenario: multiple generic classes with bounds
	 */
	@Test
	void complexGenericHierarchy() throws Exception {
		String model = """
			@namespace(uri="http://test", prefix="test")
			package test;
			
			class Comparable { }
			class Number { }
			class Repository<T extends Comparable> { }
			
			class NumberRepository<N extends Number & Comparable> extends Repository<N> {
				ref Repository<N> parentRepo;
				op N getById();
			}
			""";
		CompUnit unit = this.parseHelper.parse(model);
		
		Assertions.assertNotNull(unit);
		
		ClassDecl numberRepo = findClassByName(unit, "NumberRepository");
		Assertions.assertNotNull(numberRepo);
		
		// Check type parameter N with bounds
		TypeParamsInfo typeParams = numberRepo.getTypeParamsInfo();
		Assertions.assertNotNull(typeParams);
		
		TypeParam typeParamN = typeParams.getTp().get(0);
		Assertions.assertEquals("N", typeParamN.getName());
		
		TypeBoundsInfo bounds = typeParamN.getTypeBoundsInfo();
		Assertions.assertNotNull(bounds);
		Assertions.assertEquals(2, bounds.getTb().size(), 
			"N should have two bounds: Number & Comparable");
		
		// Check extends Repository<N>
		EList<BoundClassExceptWildcard> superTypes = numberRepo.getSuperTypes();
		Assertions.assertEquals(1, superTypes.size());
		
		BoundClassExceptWildcard superType = superTypes.get(0);
		ClassDecl superClass = (ClassDecl) superType.getBound();
		Assertions.assertEquals("Repository", superClass.getName());
		Assertions.assertEquals(1, superType.getTypeArgs().size());
	}

	// ============ Helper Methods ============

	private ClassDecl findClassByName(CompUnit unit, String name) {
		return unit.getDeclarations().stream()
			.map(TopLevelDecl::getDeclaration)
			.filter(d -> d instanceof ClassDecl)
			.map(d -> (ClassDecl) d)
			.filter(c -> Objects.equals(name, c.getName()))
			.findFirst()
			.orElse(null);
	}

	private Attribute findAttributeByName(ClassDecl classDecl, String name) {
		return classDecl.getMembers().stream()
			.map(ClassMemberDecl::getMember)
			.filter(m -> m instanceof FeatureDecl)
			.map(m -> (FeatureDecl) m)
			.filter(fd -> fd.getFeature() instanceof Attribute)
			.map(fd -> (Attribute) fd.getFeature())
			.filter(a -> Objects.equals(name, a.getName()))
			.findFirst()
			.orElse(null);
	}

	private Reference findReferenceByName(ClassDecl classDecl, String name) {
		return classDecl.getMembers().stream()
			.map(ClassMemberDecl::getMember)
			.filter(m -> m instanceof FeatureDecl)
			.map(m -> (FeatureDecl) m)
			.filter(fd -> fd.getFeature()  instanceof Reference)
			.map(fd -> (Reference) fd.getFeature())
			.filter(r -> Objects.equals(name, r.getName()))
			.findFirst()
			.orElse(null);
	}

	private Operation findOperationByName(ClassDecl classDecl, String name) {
		return classDecl.getMembers().stream()
			.map(ClassMemberDecl::getMember)
			.filter(m -> m instanceof Operation)
			.map(m -> (Operation) m)
			.filter(o -> Objects.equals(name, o.getName()))
			.findFirst()
			.orElse(null);
	}
}
