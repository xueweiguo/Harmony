package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class ConvertFormatFun extends CalculateFunction {
	public ConvertFormatFun(String name, ComplexFormatter formatter){
		funName = name;
		complexFormatter = formatter;
	}
	
	public String getName(){
		return funName;
	}
	
	public boolean execute(LinkedList<Complex> paraList, EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		if(paraList.size() != 1)
	    {
	        context.setErrorMessage(getName(), ResourceTable.String_error_invalid_parameter_count);
	        return false;
	    }
	    Complex para = paraList.getFirst();
	    context.setCurrentResult(para);
	    context.setFormatter(complexFormatter);
	    return checkResult(context);
	}
	
	private String funName;
	private ComplexFormatter complexFormatter;
}
