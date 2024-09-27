package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_ANAL_CHRT_V")
public class ElAnalChrtVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1311228155200866422L;
	
	@Id
	@Column(name="ANAL_CHRT")
	private String analChrt;

	public String getAnalChrt() {
		return analChrt;
	}

	public void setAnalChrt(String analChrt) {
		this.analChrt = analChrt;
	}


}
