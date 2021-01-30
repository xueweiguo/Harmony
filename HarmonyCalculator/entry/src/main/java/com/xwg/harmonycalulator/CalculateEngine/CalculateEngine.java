package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

public class CalculateEngine
{
	private final static int CUSTOM_FUN_COUNT = 12;
	FunctionManager functionManager;
	private ConstManager constManager = new ConstManager();
	private CalculatorContext calculatorContext = null;

	public CalculateEngine(Context context){
		calculatorContext = new CalculatorContext(context);
		functionManager = new FunctionManager(calculatorContext);
		registerConst();
		registerStandardFunctions();
	}
	
	public void registerConst(){
		constManager.registerConst(calculatorContext.getText(ResourceTable.String_character_pi),
			new Complex(Math.PI));
		constManager.registerConst("e", new Complex(Math.E));
	}
	
	public void registerStandardFunctions() {
		functionManager.registerFunction(new AcosFun());
		functionManager.registerFunction(new AcoshFun());
		functionManager.registerFunction(new AsinFun());
		functionManager.registerFunction(new AsinhFun());
		functionManager.registerFunction(new AtanFun());
		functionManager.registerFunction(new AtanhFun());
		functionManager.registerFunction(new AverageFun());
		functionManager.registerFunction(new CosFun());
		functionManager.registerFunction(new CoshFun());
		functionManager.registerFunction(new Log10Fun());
		functionManager.registerFunction(new LogeFun());
		functionManager.registerFunction(new PowerFun());
		functionManager.registerFunction(new RootFun());
		functionManager.registerFunction(new SinFun());
		functionManager.registerFunction(new SinhFun());
		functionManager.registerFunction(new SumFun());
		functionManager.registerFunction(new TanFun());
		functionManager.registerFunction(new FactorialFun());

		functionManager.registerFunction(new ConvertFormatFun("to" + calculatorContext.getText(ResourceTable.String_character_angle),
				new RadiusAngleFormatter(calculatorContext, false)));
		functionManager.registerFunction(new ConvertFormatFun("to" + calculatorContext.getText(ResourceTable.String_character_degree),
				new DegreesFormatter(calculatorContext)));

		functionManager.registerFunction(new PreDefineFunction("x2", "pow(#1,2)"));
		functionManager.registerFunction(new PreDefineFunction("x3", "pow(#1,3)"));
		functionManager.registerFunction(new PreDefineFunction("²" + calculatorContext.getText(ResourceTable.String_character_sqrt), "root(#1,2)"));
		functionManager.registerFunction(new PreDefineFunction("³" + calculatorContext.getText(ResourceTable.String_character_sqrt), "root(#1,3)"));
		functionManager.registerFunction(new PreDefineFunction("exp", "pow(e,#1)"));

		for (int i = 1; i <= CUSTOM_FUN_COUNT; ++i) {
			UserDefineFunction udf = UserDefineFunction.load(calculatorContext, "F" + i);
			if (udf != null) {
				functionManager.registerUserDefineFunction(udf);
			}
		}
	}

	public String calculate(String strQuestion, boolean convertFormat) throws NotExistException, WrongTypeException, IOException {
		if(currentRecord == null){
			currentRecord = new Record();
		}
	    LinkedList<Token> tokenList = analyzeToken(strQuestion);
	    if(!convertFormat){
	    	currentRecord.question = strQuestion;
	    }
	    String result = calculate(tokenList);
		tokenList = null;
		return result;
	}

	LinkedList<Token> analyzeToken(String strQuestion){
		TokenAnalyzer analyzer = new TokenAnalyzer();
		return analyzer.analyzeToken(strQuestion, new TokenPatternFactory(){
			public int createPatterns(LinkedList<TokenPattern> list)
			{
				String funPattern = PatternBuilder.build(functionManager.functions());

				if(funPattern.length() > 0){
					list.add(new TokenPattern(Token.EType.FunctionName, funPattern));
				}
				list.add(new TokenPattern(Token.EType.Parameter, "#[1-9]"));

				String constPattern = PatternBuilder.build(constManager.consts());
				list.add(new TokenPattern(Token.EType.Number, "(" + constPattern + ")"));

				String numberPattern = "(((\\.[0-9]+)|([0-9]+(\\.[0-9]*)?))[eE][+-]?[0-9]+)";
				numberPattern += "|";
				numberPattern += "((\\.[0-9]+)|([0-9]+\\.[0-9]*))";
				numberPattern += "|";
				numberPattern += "([0-9]+)";
				String degree = calculatorContext.getText(ResourceTable.String_character_degree);
				list.add(new TokenPattern(Token.EType.Number, "((\\.[0-9]+)|([0-9]+\\.[0-9]*)|([0-9]+))%"));
				list.add(new TokenPattern(Token.EType.Number, "(" + numberPattern + ")[" + degree + "i]?"));
				list.add(new TokenPattern(Token.EType.Number, "[i]"));
				String angle = calculatorContext.getText(ResourceTable.String_character_angle);
				list.add(new TokenPattern(Token.EType.Operator, "[-+×/" + angle + "]"));
				list.add(new TokenPattern(Token.EType.Parenthese, "[()]"));
				list.add(new TokenPattern(Token.EType.Comma, ","));
				return list.size();
			}
		});
	}

	boolean isFunction(String name){
		return (functionManager.getFunction(name) != null);
	}
	
	CharSequence[] getCustomFunctionItems(){
		CharSequence[] items = new CharSequence[CUSTOM_FUN_COUNT];
		
		for(int i = 1; i <= CUSTOM_FUN_COUNT; ++i){
			String key = "F" + i;
			String item = null;
			
			UserDefineFunction udf = functionManager.getUserDefineFunction(key);
			if(udf != null){
				item = udf.getName() + ":" + udf.getExprString(); 
			}else{
				item = key + ":Empty";
			}
			
			items[i - 1] = item;
		}
		return items;
	}
	
	public int saveCustomFunction(String key, String funName, String funText){
		return functionManager.registerUserDefineFunction(key, funName, funText);
	}
	
	public void clearCustomFunctions(){
		for(int i = 1; i <= CUSTOM_FUN_COUNT; ++i){ 
			UserDefineFunction.clear(calculatorContext, "F" + i);
		}
	}
	
	ConstManager getConstManager(){
		return constManager;
	}
	
	CharSequence[] getConstItems(){
		int count = constManager.getConstCount();
		CharSequence[] items = new CharSequence[count];
		
		Set<String> set = constManager.consts();
		
		StandardFormatter formatter = new StandardFormatter(calculatorContext);
		
		int index = 0;
        for(String key : set)
        {
        	String item = key + ":" + formatter.toString(constManager.find(key));
        	items[index] = item;
        	index++;
        }
        return items;
	}

	public static class Record
	{
		boolean success;
	    String question;
	    Complex result;
	}

	private int getRecordCount(){
		return recordList.size();
	}
	
	private Record getRecord(int index){
		if(index >= 0 && index < recordList.size())
	    {
	        return recordList.get(index);
	    }
	    else
	    {
	        return null;
	    }
	}
	
	public boolean saveRecord(){
		if(currentRecord != null && currentRecord.success)
	    {
	        recordList.add(currentRecord);
	        currentRecord = null;
	        return true;
	    }else{
	    	return false;
	    }
	}
	
	CharSequence[] getRecordItems(){
		StandardFormatter formator = new StandardFormatter(calculatorContext);
		int record_count = getRecordCount();
		CharSequence[] items = new CharSequence[record_count];
		for(int i = 0; i < record_count; ++i){
			Record record = getRecord(i);
			assert record != null;
			String recordString = formator.toString(record.result) + ":" + record.question;
			items[i] = recordString;
		}
		return items;
	}
	
	public boolean clearRecord(int index){
		if(index >= 0 && index < recordList.size())
	    {
	        recordList.remove(index);
	                                                               
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}
	
	public boolean clearAllRecord(){
		recordList.clear();
	    return true;
	}
	
	public CalculatorContext getCalculatorContext(){
    	return calculatorContext;
    }
	
	private String calculate(LinkedList<Token> tokenList) throws NotExistException, WrongTypeException, IOException {
		String result;

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
	        currentRecord.success = false;
	        return calculatorContext.getText(ResourceTable.String_error_unknown_keyword) + unknownToken.toString();
	    }
	    
	    BuildContext bContext = new BuildContext(calculatorContext, constManager, tokenList);

	    Expr expr = AdditiveExpr.buildExpr(bContext);
	    if(expr != null){
	    	if(bContext.tokenList.size() > 0){
	    		StringBuilder tokenString = new StringBuilder();
	    		for(Token token : tokenList){
	    			if(tokenString.length() != 0){
	    				tokenString.append(",");
	    			}
	    			tokenString.append(token.getContent());
	    		}
	    		currentRecord.success = false;
    	        result = calculatorContext.getText(ResourceTable.String_error_unnecessary_keyword) + tokenString.toString();
	    	}else{
		        EvaluateContext eContext = new EvaluateContext(calculatorContext, this);
		        eContext.pushResult(new Complex(0));
		        if(expr.evaluate(eContext))
		        {
		        	Complex value = eContext.popResult();
		        	result = eContext.getFormatter().toString(value);
		        	if(result != null){
		        		currentRecord.success = true;
		        		currentRecord.result = value;
		        	}else{
		        		currentRecord.success = false;
		    	        result = calculatorContext.getText(ResourceTable.String_error_invalid_input);
		        	}
		        }
		        else
		        {
		            currentRecord.success = false;
		            result = eContext.getErrorMessage();
		        }
	    	}
	    }
	    else
	    {
	        currentRecord.success = false;
	        result = bContext.errorMessage;
	    }
	    return result;
		
	}
	
	boolean isSuccess(){
		return currentRecord.success;
	}
	
	private Record currentRecord;
	private LinkedList<Record> recordList = new LinkedList<Record>();
}
