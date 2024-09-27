package jsfas.common.exception;

public class RecordExistException extends Exception {
	private static final long serialVersionUID = -6661209202197592482L;

	public RecordExistException() {
		super("Record exists!");
	}
}
