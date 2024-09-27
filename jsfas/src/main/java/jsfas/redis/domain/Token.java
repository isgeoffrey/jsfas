package jsfas.redis.domain;

import java.io.Serializable;

import jsfas.common.json.CommonJson;

public class Token implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1231114824801157879L;
	
	private String id;
	private String username;
	
	// Others Information
	
	public Token(String id, String username) {
		this.id = id;
		this.username = username;
	}
	
	public Token() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
