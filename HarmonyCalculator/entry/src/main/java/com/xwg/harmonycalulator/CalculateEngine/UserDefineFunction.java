package com.xwg.harmonycalulator.CalculateEngine;

import ohos.data.preferences.Preferences;

public class UserDefineFunction extends CustomFunction{
	public static final String PREFS_NAME = "CustomFunction";
	
	public UserDefineFunction(String key, String name, String expr){
		super(name);
		functionKey = key;
		functionExpr = expr;
	}
	
	public String getKey(){
		return functionKey;
	}
	
	public String getExprString(){
		return functionExpr;
	}
	
	static UserDefineFunction load(CalculatorContext cc, String key){
		Preferences settings = cc.getPreferences(PREFS_NAME);
		String funInfo = settings.getString(key, "");
	    
	    if(funInfo.length() == 0) return null;
	    
	    int pre_expr = funInfo.indexOf(":");
	    if(pre_expr >= 1 && pre_expr < (funInfo.length() - 1)){ 
	    	// Must has name and expression
	    	return new UserDefineFunction(key, 
	    								funInfo.substring(0, pre_expr),
	    								funInfo.substring(pre_expr + 1));
	    }else{
	    	return null;
	    }
	}
	
	static void clear(CalculatorContext context, String key){
		Preferences settings = context.getPreferences(PREFS_NAME);
		settings.putString(key, "");
	}
	
	void saveMe(CalculatorContext context){
		Preferences settings = context.getPreferences(PREFS_NAME);
		settings.putString(functionKey, getName() + ":" + functionExpr);
	}
	
	private String functionKey;
	private String functionExpr;
}
