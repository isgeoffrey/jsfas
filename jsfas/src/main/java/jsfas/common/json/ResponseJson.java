package jsfas.common.json;

import java.io.Serializable;

/**
 * @author iseric
 * @since 12/5/2016
 * @see Class for Common Response output
 */
public class ResponseJson implements Serializable{		
	private static final long serialVersionUID = -1305882566527043219L;
	
	private String status;
	private String message;
	
	public String getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

				
}
