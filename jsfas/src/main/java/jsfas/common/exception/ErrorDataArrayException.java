package jsfas.common.exception;

import java.util.List;

import org.json.JSONArray;

import jsfas.common.json.CommonJson;

public class ErrorDataArrayException extends Exception {
	
	
	public ErrorDataArrayException() {
		super("Error Data Exception!");
	};
	
	public ErrorDataArrayException(String message) {
		super(message);
	};
		private List<CommonJson> errorList;
		public List<CommonJson> getErrorList() {
			return errorList;
		}

		public void setErrorList(List<CommonJson> errorList) {
			this.errorList = errorList;
		}
		
		
	
}
