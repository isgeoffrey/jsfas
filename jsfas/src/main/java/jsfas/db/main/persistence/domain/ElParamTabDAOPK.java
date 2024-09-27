package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ElParamTabDAOPK implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4009453315737695024L;
	
	@Column(name="TYPE")
	private String type;
	@Column(name="PROGRAM")
	private String program;
	@Column(name="NAME")
	private String name;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}
