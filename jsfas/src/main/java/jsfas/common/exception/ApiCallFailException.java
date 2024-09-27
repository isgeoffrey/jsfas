package jsfas.common.exception;

public class ApiCallFailException extends RuntimeException {
	private static final long serialVersionUID = -6661209202197592482L;

	public ApiCallFailException() {
		super("API call fail");
	}
	
	public ApiCallFailException(String message) {
		super(message);
	}
}
