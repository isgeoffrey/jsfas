package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import jsfas.common.utils.GeneralUtil;

@Entity
@Table(name="EL_APPL_EL_TYPE_HIST")
public class ElApplElTypeHistDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3400684803686907138L;
	
	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="EL_TYPE_ID")
	private String elTypeId;
	@Column(name="EL_TYPE_DESCR")
	private String elTypeDescr;
	@Column(name="STDT_ENROLLED")
	private Integer stdtEnrolled;
	@Column(name="PYMT_AMT")
	private BigDecimal pymtAmt;
	@Column(name="PMT_CURRENCY")
	private String pmtCurrency;
	@Column(name="VERSION_NO")
	private Integer versionNo;
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
	
	public ElApplElTypeHistDAO(ElApplElTypeDAO originDAO, ElApplHdrDAO hdrDAO) {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = originDAO.getApplHdrId();
		this.elTypeId = originDAO.getElTypeId();
		this.elTypeDescr = originDAO.getElTypeDescr();
		this.stdtEnrolled = originDAO.getStdtEnrolled();
		this.pymtAmt = originDAO.getPymtAmt();
		this.pmtCurrency = originDAO.getPmtCurrency();

		this.versionNo = hdrDAO.getVersionNo();
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}
	
	public ElApplElTypeHistDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.elTypeId = " ";
		this.elTypeDescr = " ";
		this.stdtEnrolled = 0;
		this.pymtAmt = BigDecimal.ZERO;
		this.pmtCurrency = "HKD";
		this.versionNo = 0;
		
		this.modCtrlTxt = GeneralUtil.genModCtrlTxt();
		this.creatDat = GeneralUtil.getCurrentTimestamp();
		this.creatUser = " ";
		this.chngDat = GeneralUtil.getCurrentTimestamp();
		this.chngUser = " ";
		this.opPageNam = " ";
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApplHdrId() {
		return applHdrId;
	}
	public void setApplHdrId(String applHdrId) {
		this.applHdrId = applHdrId;
	}
	public String getElTypeId() {
		return elTypeId;
	}
	public void setElTypeId(String elTypeId) {
		this.elTypeId = elTypeId;
	}
	public String getElTypeDescr() {
		return elTypeDescr;
	}
	public void setElTypeDescr(String elTypeDescr) {
		this.elTypeDescr = elTypeDescr;
	}
	public Integer getStdtEnrolled() {
		return stdtEnrolled;
	}
	public void setStdtEnrolled(Integer stdtEnrolled) {
		this.stdtEnrolled = stdtEnrolled;
	}
	public BigDecimal getPymtAmt() {
		return pymtAmt;
	}
	public void setPymtAmt(BigDecimal pymtAmt) {
		this.pymtAmt = pymtAmt;
	}
	public String getPmtCurrency() {
		return pmtCurrency;
	}
	public void setPmtCurrency(String pmtCurrency) {
		this.pmtCurrency = pmtCurrency;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
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
