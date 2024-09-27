package jsfas.redis.domain;

import java.io.Serializable;

public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1585733807212280002L;
	private String username;

	//others
    //private String name;
    //private Gender gender;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
