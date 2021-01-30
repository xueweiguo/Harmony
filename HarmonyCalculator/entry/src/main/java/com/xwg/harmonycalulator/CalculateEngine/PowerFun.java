package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class PowerFun extends CalculateFunction {
	String getName(){
		return "pow";
	}
	
	String getPrototype(){
		return "pow(X1,X2)";
	}
	
	boolean execute(LinkedList<Complex> paraList, EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		if(paraList.size() != 2)
	    {
	        context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_parameter_count);
	        return false;
	    }
	    Complex x = paraList.removeFirst();
	    if(x.i != 0)
	    {
	    	context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_date_type);
	        return false;
	    }
	    Complex y = paraList.removeFirst();
	    if(y.i != 0)
	    {
	    	context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_date_type);
	        return false;
	    }
	    context.setCurrentResult(new Complex(Math.pow(x.r, y.r)));
	    return checkResult(context);
	}
}
