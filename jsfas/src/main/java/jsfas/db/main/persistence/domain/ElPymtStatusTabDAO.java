package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_PYMT_STATUS_TAB")
public class ElPymtStatusTabDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6585396414093775733L;
	
	@Id
	@Column(name="PYMT_STATUS_CDE")
	private String pymtStatusCde;
	@Column(name="PYMT_STATUS_DESCR")
	private String pymtStatusDescr;

	@Column(name="MOD_CTRL_TXT")
	private String modCtrlTxt;
	@Column(name="CREAT_USER")
	private String creatUser;
	@Column(name="CREAT_DAT")
	private Timestamp creatDat;
	@Column(name="CHNG_USER")
	private String chngUser;
	@Column(name="CHNG_DAT")
	private Timestamp chngDat;
	@Column(name="OP_PAGE_NAM")
	private String opPageNam;
	
	public String getPymtStatusCde() {
		return pymtStatusCde;
	}
	public void setPymtStatusCde(String pymtStatusCde) {
		this.pymtStatusCde = pymtStatusCde;
	}
	public String getPymtStatusDescr() {
		return pymtStatusDescr;
	}
	public void setPymtStatusDescr(String pymtStatusDescr) {
		this.pymtStatusDescr = pymtStatusDescr;
	}
	public String getModCtrlTxt() {
		return modCtrlTxt;
	}
	public void setModCtrlTxt(String modCtrlTxt) {
		this.modCtrlTxt = modCtrlTxt;
	}
	public String getCreatUser() {
		return creatUser;
	}
	public void setCreatUser(String creatUser) {
		this.creatUser = creatUser;
	}
	public Timestamp getCreatDat() {
		return creatDat;
	}
	public void setCreatDat(Timestamp creatDat) {
		this.creatDat = creatDat;
	}
	public String getChngUser() {
		return chngUser;
	}
	public void setChngUser(String chngUser) {
		this.chngUser = chngUser;
	}
	public Timestamp getChngDat() {
		return chngDat;
	}
	public void setChngDat(Timestamp chngDat) {
		this.chngDat = chngDat;
	}
	public String getOpPageNam() {
		return opPageNam;
	}
	public void setOpPageNam(String opPageNam) {
		this.opPageNam = opPageNam;
	}
	
}
