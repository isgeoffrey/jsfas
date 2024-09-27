package jsfas.common.json;

import java.io.Serializable;

/**
 * @author iswill
 * @since 11/1/2021
 */
public class Meta implements Serializable {		
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5772444872312168029L;
	
	private int code;
	private String message;
	
	public Meta(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
				
}
