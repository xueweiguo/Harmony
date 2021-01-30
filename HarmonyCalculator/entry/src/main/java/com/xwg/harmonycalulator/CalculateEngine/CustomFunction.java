package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class CustomFunction extends CalculateFunction {
	
	private String funName;
	CustomFunction(String name){
		funName = name;
	}
	String getName(){
		return funName;
	}
	
	String getPrototype(){
		return funName;
	}
	
	public String getExprString(){ return null; }
	
	boolean execute(LinkedList<Complex> paraList, EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		String functionString = getExprString();
	    
	    if(functionString == null || functionString.length() == 0){
			try {
				context.setErrorMessage(getPrototype(), ResourceTable.String_error_undefined_custom_function);
			} catch (NotExistException | WrongTypeException | IOException e) {
				e.printStackTrace();
			}
			return false;
	    }
	    
	    CalculateEngine engine = context.getCalculateEngine();
	    
	    LinkedList<Token> tokenList = engine.analyzeToken(functionString);
	    
	    StringBuilder unknownToken = new StringBuilder();
	    for(Token token : tokenList)
	    {
	        if(token.getType() == Token.EType.NoType)
	        {
	            if(unknownToken.length() > 0)
	            {
	                unknownToken.append(",");
	            }
	            unknownToken.append(token.getContent());
	        }
	    }
	    if(unknownToken.length() > 0)
	    {
	    	context.setErrorMessage(context.getCalculatorContext().getText(ResourceTable.String_error_invalid_token) + " in " + getPrototype() + ":" + unknownToken);
	        return false; 
	    }
	    	    
	    BuildContext bContext = new BuildContext(engine.getCalculatorContext(),
	    										engine.getConstManager(), 
	    										tokenList);

	    Expr expr = AdditiveExpr.buildExpr(bContext);
	    if(expr != null){
	        EvaluateContext eContext = new EvaluateContext(context);
	        eContext.pushResult(new Complex(0));

			try {
				if(!eContext.pushCustomCall(getName(), paraList)){
					context.setErrorMessage(eContext.getErrorMessage());
					return false;
				}
			} catch (NotExistException | WrongTypeException | IOException e) {
				e.printStackTrace();
				return false;
			}
			boolean success = expr.evaluate(eContext);
	        eContext.popCustomCall();
	        if(success)
	        {
	        	context.setCurrentResult(eContext.popResult());
	        	context.setFormatter(eContext.getFormatter());
	        	return true;
	        }else{
	        	context.setErrorMessage(eContext.getErrorMessage());
	        	return false;
	        }
	    }
	    else
	    {
	    	context.setErrorMessage(bContext.errorMessage);
	    	return false;
	    }
	}
}
