package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;
import java.util.LinkedList;

public class ParameterExpr extends TerminalExpr
{
	static ParameterExpr buildExpr(BuildContext context){
		Token token = context.tokenList.removeFirst();
		return new ParameterExpr(Integer.valueOf(token.getContent().substring(1)));
	}
	public ParameterExpr(int number){
		paraNumber = number;
	}
	
	public boolean evaluate(EvaluateContext context) throws NotExistException, WrongTypeException, IOException {
		LinkedList<String> customNameList = context.getCustomNameStack();
		if(customNameList.size() > 0){
			LinkedList<Complex> paraList = context.getParaListStack().getLast();
			String funName = customNameList.getLast();
			if(paraList.size() >= paraNumber)
			{
				Complex para = paraList.get(paraNumber - 1);
				context.setCurrentResult(para);
				return true;
			}else{
				context.setErrorMessage(funName, ResourceTable.String_error_invalid_parameter_count);
				return false;
			}
		}else{
			String para = String.format("#%d", paraNumber);
			context.setErrorMessage(para, ResourceTable.String_error_invalid_input);
			return false;
		}
	}
	
	private int paraNumber;
}