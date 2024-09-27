package jsfas.common.json;

import java.io.Serializable;

/**
 * @author iswill
 * @since 15/1/2021
 * @see Class for Common Response output
 */
public class Response implements Serializable {		
	private static final long serialVersionUID = -1305882566527043219L;
	
	private Meta meta;
	private CommonJson data;
	
	public Response(Meta meta, CommonJson data) {
		this.meta = meta;
		this.data = data;
	}
	
	public Response() {
		this.meta = new Meta(0, "");
		this.data = new CommonJson();
	}
	
	public Meta getMeta() {
		return meta;
	}
	
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	
	public CommonJson getData() {
		return data;
	}
	
	public void setData(CommonJson data) {
		this.data = data;
	}
	
	//additional set method
	public void setMeta(int code, String message) {
		this.meta.setCode(code);
		this.meta.setMessage(message);
	}
	
	public void setCode(int code) {
		this.meta.setCode(code);
	}
	
	public void setMessage(String message) {
		this.meta.setMessage(message);
	}
				
}
