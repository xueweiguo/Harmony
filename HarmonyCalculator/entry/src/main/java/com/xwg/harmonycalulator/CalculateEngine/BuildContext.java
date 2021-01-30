package com.xwg.harmonycalulator.CalculateEngine;

import java.util.LinkedList;

public class BuildContext
{
    public BuildContext(CalculatorContext context, ConstManager _manager, LinkedList<Token> list){
    	calculatorContext = context;
    	constManager = _manager;
    	tokenList = list;
    	
    }
    CalculatorContext getCalculatorContext(){
    	return calculatorContext;
    }

    private CalculatorContext calculatorContext;
    ConstManager constManager;
    LinkedList<Token> tokenList = new LinkedList<Token>();
    String errorMessage;
}
