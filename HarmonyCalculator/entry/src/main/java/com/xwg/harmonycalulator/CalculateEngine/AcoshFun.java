package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class AcoshFun extends CalculateFunction {
	String getName(){
		return "acosh";
	}
	
	String getPrototype(){
		return "acosh(X)";
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
	    if(para.r < 1){
	    	context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_input);
	        return false;
	    }
	    double value = StrictMath.sqrt((para.r + 1) / 2) + StrictMath.sqrt((para.r  - 1)/2);
	    if(value == 0){
	    	context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_input);
	        return false;
	    }
	    context.setCurrentResult(new Complex(StrictMath.log(value) * 2));
	    return checkResult(context);
	}
}