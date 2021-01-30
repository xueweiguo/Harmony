package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class Log10Fun extends CalculateFunction {
	String getName(){
		return "log10";
	}
	
	String getPrototype(){
		return "log10(X)";
	}
	
	
	boolean execute(LinkedList<Complex> paraList, EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		if(paraList.size() != 1)
	    {
	        context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_parameter_count);
	        return false;
	    }
	    Complex para = paraList.getFirst();
	    if(para.r < 0)
	    {
	    	context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_input);
	        return false;
	    }
	    if(para.i != 0)
	    {
	        context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_date_type);
	        return false;
	    }
	    context.setCurrentResult(new Complex(Math.log10(para.r)));
	    return checkResult(context);
	}
}
