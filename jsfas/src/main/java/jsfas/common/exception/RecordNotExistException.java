package jsfas.common.exception;

public class RecordNotExistException extends Exception {
	private static final long serialVersionUID = -3886008311803182863L;

	public RecordNotExistException(String value) {
		super(value + " does not exist!");
	}
}
