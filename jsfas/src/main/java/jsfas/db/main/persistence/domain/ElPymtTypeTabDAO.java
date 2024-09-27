package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_PYMT_TYPE_TAB")
public class ElPymtTypeTabDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2394570736383032749L;
	
	@Id
	@Column(name="PYMT_TYPE_CDE")
	private String pymtTypeCde;
	@Column(name="PYMT_TYPE_DESCR")
	private String pymtTypeDescr;
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
	
	public String getPymtTypeCde() {
		return pymtTypeCde;
	}
	public void setPymtTypeCde(String pymtTypeCde) {
		this.pymtTypeCde = pymtTypeCde;
	}
	public String getPymtTypeDescr() {
		return pymtTypeDescr;
	}
	public void setPymtTypeDescr(String pymtTypeDescr) {
		this.pymtTypeDescr = pymtTypeDescr;
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
