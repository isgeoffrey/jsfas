package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ElCrseClassVDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5631410295014275507L;

	@Column(name="STRM")
	private String strm;
	@Column(name="CRSE_ID")
	private String crseId;
	
	public String getStrm() {
		return strm;
	}
	public void setStrm(String strm) {
		this.strm = strm;
	}
	public String getCrseId() {
		return crseId;
	}
	public void setCrseId(String crseId) {
		this.crseId = crseId;
	}
}
