package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_ACAD_TERM_V")
public class ElAcadTermVDAO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8924929897111353370L;
	
	@Id
	@Column(name="STRM")
	private String strm;
	
	@Column(name="ACAD_YR")
	private String acadYr;
	
	@Column(name="ACAD_YR_DESC")
	private String acadYrDesc;
	
	@Column(name="YR_TERM_DESC")
	private String yrTermDesc;
	
	@Column(name="TERM_BEGIN_DAT")
	private Timestamp termBeginDat;
	
	@Column(name="TERM_END_DAT")
	private Timestamp termEndDat;

	public String getStrm() {
		return strm;
	}

	public void setStrm(String strm) {
		this.strm = strm;
	}

	public String getAcadYr() {
		return acadYr;
	}

	public void setAcadYr(String acadYr) {
		this.acadYr = acadYr;
	}

	public String getAcadYrDesc() {
		return acadYrDesc;
	}

	public void setAcadYrDesc(String acadYrDesc) {
		this.acadYrDesc = acadYrDesc;
	}

	public String getYrTermDesc() {
		return yrTermDesc;
	}

	public void setYrTermDesc(String yrTermDesc) {
		this.yrTermDesc = yrTermDesc;
	}

	public Timestamp getTermBeginDat() {
		return termBeginDat;
	}

	public void setTermBeginDat(Timestamp termBeginDat) {
		this.termBeginDat = termBeginDat;
	}

	public Timestamp getTermEndDat() {
		return termEndDat;
	}

	public void setTermEndDat(Timestamp termEndDat) {
		this.termEndDat = termEndDat;
	}
}
