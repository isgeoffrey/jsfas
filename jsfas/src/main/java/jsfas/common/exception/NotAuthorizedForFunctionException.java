package jsfas.common.exception;

import jsfas.common.annotation.Function;

public class NotAuthorizedForFunctionException extends Exception {
	private static final long serialVersionUID = 3816510070926356769L;
	
	private String functionCode;
	private String functionSubCode;

	public NotAuthorizedForFunctionException() {
		super("Not authorized to access the function!");
	}
	
	public NotAuthorizedForFunctionException(String functionCode, String functionSubCode) {
		super("Not authorized to access the function!");
		
		this.setFunctionCode(functionCode);
		this.setFunctionSubCode(functionSubCode);
	}
	
	public NotAuthorizedForFunctionException(Function[] functions) {
		super("Not authorized to access the function!");
		
		if (functions.length > 0) {
			this.setFunctionCode(functions[0].functionCode());
			this.setFunctionSubCode(functions[0].functionSubCode());
		}
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getFunctionSubCode() {
		return functionSubCode;
	}

	public void setFunctionSubCode(String functionSubCode) {
		this.functionSubCode = functionSubCode;
	}
}
