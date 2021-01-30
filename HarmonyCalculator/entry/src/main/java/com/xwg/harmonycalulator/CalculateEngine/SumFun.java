package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class SumFun extends CalculateFunction {
	String getName(){
		return "sum";
	}
	
	String getPrototype(){
		return "sum(X1,X2,X3,...)";
	}
	
	boolean execute(LinkedList<Complex> paraList, EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		if(paraList.size() == 0)
	    {
	        context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_parameter_count);
	        return false;
	    }
	    Complex result = new Complex();
	    for(Complex c : paraList){
	    	result = Complex.add(result, c);
	    }
	    context.setCurrentResult(result);
	    return checkResult(context);
	}
}


