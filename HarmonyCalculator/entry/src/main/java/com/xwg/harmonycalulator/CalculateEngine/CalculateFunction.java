package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.data.preferences.Preferences;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public abstract class CalculateFunction {
	CalculateFunction(){
		
	}
	
	boolean checkResult(EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		if(Double.isNaN(context.getCurrentResult().r)){
			context.setErrorMessage(getPrototype(), ResourceTable.String_error_mathmatic_error);
			return false;
		}
		return true;
    }
    String getName(){ return ""; }
    String getPrototype(){ return ""; }
   
    boolean execute(LinkedList<Complex> paraList, EvaluateContext context) throws NotExistException, WrongTypeException, IOException { return false; }
}
