package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_CLASS_CHRT_V")
public class ElClassChrtVDAO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5647069568212524126L;

	@Id
	@Column(name="CLASS_CHRT")
	private String classChrt;

	public String getClassChrt() {
		return classChrt;
	}

	public void setClassChrt(String classChrt) {
		this.classChrt = classChrt;
	}
}
