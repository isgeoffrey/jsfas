package jsfas.common.exception;

public class RecordModifiedException extends Exception {
	private static final long serialVersionUID = 5535024597856122644L;

	public RecordModifiedException() {
		super("Record modified by other process!");
	}
}
