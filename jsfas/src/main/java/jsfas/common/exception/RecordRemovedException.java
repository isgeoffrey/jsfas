package jsfas.common.exception;

public class RecordRemovedException extends Exception {
	private static final long serialVersionUID = 7788792740720823619L;

	public RecordRemovedException() {
		super("Record removed by other process!");
	}
}
