package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class TanhFun extends CalculateFunction {
	String getName(){
		return "tanh";
	}
	
	String getPrototype(){
		return "tanh(X)";
	}
	
	boolean execute(LinkedList<Complex> paraList, EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		if(paraList.size() != 1)
	    {
			context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_parameter_count);
	        return false;
	    }
	    Complex para = paraList.getFirst();
	    if(para.i != 0)
	    {
	    	context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_date_type);
	        return false;
	    }
	    double result = Math.tanh(para.r);
	    context.setCurrentResult(new Complex(result));
	    return true;
	}
}