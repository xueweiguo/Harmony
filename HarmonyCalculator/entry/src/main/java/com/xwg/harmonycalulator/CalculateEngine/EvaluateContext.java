package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class EvaluateContext
{
	private CalculateEngine calculateEngine;
	private String errorMessage;
    private CalculatorContext calculatorContext;
    private LinkedList<Complex> resultStack = new LinkedList<Complex>();
    private LinkedList<String> customFunctionStack;
    private LinkedList<LinkedList<Complex>> paraListStack;
    private ComplexFormatter formatter;
    
	EvaluateContext(CalculatorContext context, CalculateEngine engine){
		calculatorContext = context;
		calculateEngine = engine;
	}
	
	EvaluateContext(EvaluateContext context){
		calculatorContext = context.getCalculatorContext();
		calculateEngine = context.getCalculateEngine();
		customFunctionStack = context.getCustomNameStack();
		paraListStack = context.getParaListStack();
		formatter = context.formatter;
	}
	
	CalculateEngine getCalculateEngine(){
		return calculateEngine;
	}

	CalculatorContext getCalculatorContext(){
		return calculatorContext;
	}
	
	FunctionManager getFunctionManager(){
		return calculateEngine.functionManager;
	}
	
	void setCurrentResult(Complex current){
		resultStack.removeLast();
		resultStack.add(current);
	}
	
	Complex getCurrentResult(){
		return resultStack.getLast();
	}
	
	void pushResult(Complex complex){
		resultStack.add(complex);
	}
	
	Complex popResult(){
		return resultStack.removeLast();
	}
	
	void clearResult(){
		resultStack.clear();
	}
	
	LinkedList<String> getCustomNameStack(){
		if(customFunctionStack == null){
			customFunctionStack = new LinkedList<String>();
		}
		return customFunctionStack;
	}
	
	LinkedList<LinkedList<Complex>> getParaListStack(){
		if(paraListStack == null){
			paraListStack = new LinkedList<LinkedList<Complex>>();
		}
		return paraListStack;
	}
	
	boolean pushCustomCall(String funName, LinkedList<Complex> paraList) throws NotExistException, WrongTypeException, IOException {
		LinkedList<String> callStack = getCustomNameStack();
		if(callStack.indexOf(funName) == -1){
			callStack.add(funName);
			paraListStack.add(paraList);
			return true;
		}else{
			setErrorMessage(funName, ResourceTable.String_error_self_call);
			return false;
		}
	}
	
	boolean popCustomCall(){
		LinkedList<String> callStack = getCustomNameStack();
		callStack.removeLast();
		paraListStack.removeLast();
		return true;
	}
	
	ComplexFormatter getFormatter(){
		if(formatter == null){
			formatter = new StandardFormatter(calculatorContext);
		}
		return formatter;
	}
	
	void setFormatter(ComplexFormatter _formatter){
		formatter = _formatter;
	}
	
	void setErrorMessage(String errorSource, int errorId) throws NotExistException, WrongTypeException, IOException {
		ohos.global.resource.ResourceManager resManager = calculatorContext.getResourceManager();
		errorMessage = errorSource + ":" + resManager.getElement(errorId).getString();
	}
	
	void setErrorMessage(String message){
		errorMessage = message;
	}
	
	String getErrorMessage(){
		return errorMessage;
	}
    
};