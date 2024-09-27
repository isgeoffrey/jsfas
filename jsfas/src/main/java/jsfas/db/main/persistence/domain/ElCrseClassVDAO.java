package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="EL_CRSE_CLASS_V")
public class ElCrseClassVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -578490531662891894L;
	
	@EmbeddedId
	private ElCrseClassVDAOPK elCrseClassVDAOPK;
	
	@Column(name="SUBJECT")
	private String subject;
	
	@Column(name="CATALOG_NBR")
	private String catalogNbr;
	
	@Column(name="CRSE_TITLE")
	private String crseTitle;
	
	@Column(name="CLASS_SECTION")
	private String classSection;
	
	@Column(name="UNITS_ACAD_PROG")
	private BigDecimal unitsAcadProg;
	
	@Column(name="ACAD_CAREER")
	private String acadCareer;

	public ElCrseClassVDAOPK getElCrseClassVDAOPK() {
		return elCrseClassVDAOPK;
	}

	public void setElCrseClassVDAOPK(ElCrseClassVDAOPK elCrseClassVDAOPK) {
		this.elCrseClassVDAOPK = elCrseClassVDAOPK;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCatalogNbr() {
		return catalogNbr;
	}

	public void setCatalogNbr(String catalogNbr) {
		this.catalogNbr = catalogNbr;
	}

	public String getCrseTitle() {
		return crseTitle;
	}

	public void setCrseTitle(String crseTitle) {
		this.crseTitle = crseTitle;
	}

	public String getClassSection() {
		return classSection;
	}

	public void setClassSection(String classSection) {
		this.classSection = classSection;
	}

	public BigDecimal getUnitsAcadProg() {
		return unitsAcadProg;
	}

	public void setUnitsAcadProg(BigDecimal unitsAcadProg) {
		this.unitsAcadProg = unitsAcadProg;
	}

	public String getAcadCareer() {
		return acadCareer;
	}

	public void setAcadCareer(String acadCareer) {
		this.acadCareer = acadCareer;
	}
}
