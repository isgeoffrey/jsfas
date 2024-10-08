package jsfas.common.exception;

import jsfas.common.json.CommonJson;
import java.util.List;

import jsfas.common.json.CommonJson;

public class StocktakeExcelValidationException extends Exception {
private static final long serialVersionUID = -3903447769450035969L;
	
	//CommonJson
	// - cellReference, e.g. B1
	// - errorMsg, e.g. The value must be a valid number
	private List<CommonJson> errorList;

	public StocktakeExcelValidationException() {
		super("Excel Validation Exception!");
	}
	
	public StocktakeExcelValidationException(String message) {
		super(message);
	}

	public List<CommonJson> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<CommonJson> errorList) {
		this.errorList = errorList;
	}
}
