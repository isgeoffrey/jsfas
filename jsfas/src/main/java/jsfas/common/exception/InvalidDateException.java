package jsfas.common.exception;

public class InvalidDateException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6127117217998486649L;

	public InvalidDateException() {
		super("You have selected an invalid date!");
	}
}