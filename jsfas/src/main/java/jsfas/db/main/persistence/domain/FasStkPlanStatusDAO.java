package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FAS_STK_PLAN_STATUS")
public class FasStkPlanStatusDAO implements Serializable {

    private static final long serialVersionUID = 7011234243644996645L;
    
    @Id
    @Column(name = "STK_PLAN_STATUS")
    private String stkPlanstatus;
    
    @Column(name = "DESCR")
    private String descr;
    
    @Column(name = "MOD_CTRL_TXT")
    private String modCtrlTxt;
    
    @Column(name = "CREAT_USER")
    private String creatUser;
    
    @Column(name = "CREAT_DAT")
    private Timestamp creatDat;
    
    @Column(name = "CHNG_USER")
    private String chngUser;
    
    @Column(name = "CHNG_DAT")
    private Timestamp chngDat;
    
    @Column(name = "OP_PAGE_NAM")
    private String opPageNam;

	public String getStkPlanstatus() {
		return stkPlanstatus;
	}

	public void setStkPlanstatus(String stkPlanstatus) {
		this.stkPlanstatus = stkPlanstatus;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
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
