package com.xwg.harmonycalulator.CalculateEngine;
import com.xwg.harmonycalulator.ResourceTable;

import java.util.HashMap;
import java.util.Set;

class FunctionManager {
	private CalculatorContext calculatorContext;

	FunctionManager(CalculatorContext cc){
		calculatorContext = cc;
	}
	
	void registerFunction(CalculateFunction fun){
		functionMap.putIfAbsent(fun.getName(), fun);
	}
	
	int registerUserDefineFunction(String key, String funName, String funText){
		CalculateFunction fun = getFunction(funName);
		
		if(fun != null){
			if(fun.getClass().getSimpleName().compareTo("UserDefineFunction") != 0){
				//Has been registered as a system function.
				return ResourceTable.String_error_used_fun_name;
			}
			UserDefineFunction udf = (UserDefineFunction)fun;
			if(udf.getKey().compareTo(key) != 0){
				//The name has been used by other UserDefineFunctio.
				return ResourceTable.String_error_used_fun_name;
			}
			functionMap.remove(udf.getName());
			userDefineMap.remove(key);
		}
		
		//Now, we can register the UserdefineFunction safety.
		UserDefineFunction udf_new = new UserDefineFunction(key, funName, funText);
		udf_new.saveMe(calculatorContext);
		return registerUserDefineFunction(udf_new);
	}
	
	int registerUserDefineFunction(UserDefineFunction udf){
		userDefineMap.put(udf.getKey(), udf);
		functionMap.put(udf.getName(), udf);
		return 0;
	}
	
	UserDefineFunction getUserDefineFunction(String key){
		return userDefineMap.get(key);
	}
	
	Set<String> functions(){
		return functionMap.keySet();
	}
	
	CalculateFunction getFunction(String name){
		return functionMap.get(name);
	}
	
	private HashMap<String, CalculateFunction> functionMap = new HashMap<String, CalculateFunction>();
	private HashMap<String, UserDefineFunction> userDefineMap = new HashMap<String, UserDefineFunction>();
}
