package org.eclipse.emf.emfatic.xtext.validation;

/**
 * Issue codes must be defined as strings as the fix provider matches fixes by issue code value.
 */
public interface IssueCodes {
	
	public static final String E_INVLD_IMPRT_URI = 			"E1000";
	public static final String E_UNSPPRTD_URI_EXTNSN = 		"E1001";
	public static final String E_IMPRTD_MTMDL_NOT_FND = 	"E1002";
	public static final String E_EXTND_CYCLE_DTCTD = 		"E1003";
	public static final String E_UNKWN_ANNT_LABEL = 		"E1004";
	public static final String E_DUP_ANNT_KEY_FOUND = 		"E1005";
	public static final String E_DUP_CLASS_NAME = 			"E1006";
	public static final String E_DUP_FEAT_NAME = 			"E1007";
	public static final String E_DUP_OP_NAME = 				"E1008";
	public static final String E_BOUND_TPARAM_WT_ARGS =		"E1009";
	public static final String E_REF_AS_ID = 				"E1010";
	public static final String E_ATTR_IS_RESLVBL =			"E1011";
	public static final String E_DUP_ID_ATTR = 				"E1012";
	public static final String E_DUP_OP_PARAM_NAME = 		"E1008";
	
	public static final String W_ECORE_IMPRTD = 			"W2000";
	public static final String W_EMPTY_MTMDL = 				"W2001";
	public static final String W_URI_INSTD_OF_LBL = 		"W2003";
	public static final String W_INVLD_KEY_USED = 			"W2004";
	
}
