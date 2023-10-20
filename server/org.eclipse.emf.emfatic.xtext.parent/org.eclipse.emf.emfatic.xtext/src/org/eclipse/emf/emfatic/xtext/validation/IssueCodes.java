package org.eclipse.emf.emfatic.xtext.validation;

public interface IssueCodes {
	
	public static final String E_INVALID_METAMODEL_IMPORTED = "E1000";
	public static final String E_EXTEND_CYCLE_DETECTED = "E1001";
	public static final String E_UNKOWN_ANNOTATION_LABEL = "E1002";
	public static final String E_DUPLICATE_KEY_FOUND = "E1004";
	public static final String E_DUPLICATE_CLASS_NAME = "E1005";
	public static final String E_DUPLICATE_FEATURE_NAME = "E1006";
	public static final String E_DUPLICATE_OPERATION_NAME = "E1007";
	
	public static final String W_ECORE_IMPORTED = "W2000";
	public static final String W_EMPTY_METAMODEL = "W2001";
	public static final String W_URI_INSTEAD_OF_LABEL = "W1003";
	public static final String W_INVALID_KEY_USED = "W1004";
	
	
}
