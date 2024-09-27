package jsfas.common.exception;

public class JsonSchemaLoadingFailedException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1455837734521147226L;

	public JsonSchemaLoadingFailedException(String message) {
        super(message);
    }

    public JsonSchemaLoadingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
