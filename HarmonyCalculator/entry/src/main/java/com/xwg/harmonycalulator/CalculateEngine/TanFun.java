package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class TanFun extends CalculateFunction {
	static final double MIN_NUMBER = 1e-15;
	
	String getName(){
		return "tan";
	}
	
	String getPrototype(){
		return "tan(X)";
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
	    
	    double cycles = StrictMath.floor(para.r / 2 / StrictMath.PI);
		if(cycles < 0){
			cycles += 1;
		}
		double angle = para.r - cycles * 2 * StrictMath.PI;
		if((StrictMath.abs(angle - StrictMath.PI / 2) < MIN_NUMBER)
				||(StrictMath.abs(angle - StrictMath.PI * 3 / 2) < MIN_NUMBER)){
			context.setErrorMessage(getPrototype(), ResourceTable.String_error_invalid_input);
	        return false;
		}
	    
	    double result = Math.tan(angle);
	    context.setCurrentResult(new Complex(result));
	    return true;
	}
}
