package jsfas.common.exception;

public class InvalidParameterException extends Exception {
	private static final long serialVersionUID = -7679983604183995025L;

	public InvalidParameterException() {
		super("Invalid parameter!");
	}
	
	public InvalidParameterException(String message) {
		super(message);
	}
}
