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
@Table(name="EL_APPL_PYMT_METHOD")
public class ElApplPymtMethodDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4407711346297684942L;
	
	@Id
	@Column(name="ID")
	private String id;
	@Column(name="APPL_HDR_ID")
	private String applHdrId;
	@Column(name="APPL_EL_TYPE_ID")
	private String applElTypeId;
	@Column(name="PYMT_CATEGORY")
	private String pymtCategory;
	@Column(name="PYMT_LINE_NO")
	private Integer pymtLineNo;
	@Column(name="PYMT_TYPE_CDE")
	private String pymtTypeCde;
	@Column(name="PYMT_FREQ")
	private String pymtFreq;
	@Column(name="PYMT_AMT")
	private BigDecimal pymtAmt;
	@Column(name="PYMT_START_DT")
	private Timestamp pymtStartDt;
	@Column(name="PYMT_END_DT")
	private Timestamp pymtEndDt;
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
	
	public ElApplPymtMethodDAO() {
		super();
		this.id = UUID.randomUUID().toString();
		this.applHdrId = " ";
		this.applElTypeId = " ";
		this.pymtCategory = " ";
		this.pymtLineNo = 0;
		this.pymtTypeCde = " ";
		this.pymtFreq = " ";
		this.pymtAmt = BigDecimal.ZERO;
		this.pymtStartDt = GeneralUtil.NULLTIMESTAMP;
		this.pymtEndDt = GeneralUtil.NULLTIMESTAMP;
		
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
	public String getApplElTypeId() {
		return applElTypeId;
	}
	public void setApplElTypeId(String applElTypeId) {
		this.applElTypeId = applElTypeId;
	}
	public String getPymtCategory() {
		return pymtCategory;
	}
	public void setPymtCategory(String pymtCategory) {
		this.pymtCategory = pymtCategory;
	}
	public Integer getPymtLineNo() {
		return pymtLineNo;
	}
	public void setPymtLineNo(Integer pymtLineNo) {
		this.pymtLineNo = pymtLineNo;
	}
	public String getPymtTypeCde() {
		return pymtTypeCde;
	}
	public void setPymtTypeCde(String pymtTypeCde) {
		this.pymtTypeCde = pymtTypeCde;
	}
	public String getPymtFreq() {
		return pymtFreq;
	}
	public void setPymtFreq(String pymtFreq) {
		this.pymtFreq = pymtFreq;
	}
	public BigDecimal getPymtAmt() {
		return pymtAmt;
	}
	public void setPymtAmt(BigDecimal pymtAmt) {
		this.pymtAmt = pymtAmt;
	}
	public Timestamp getPymtStartDt() {
		return pymtStartDt;
	}
	public void setPymtStartDt(Timestamp pymtStartDt) {
		this.pymtStartDt = pymtStartDt;
	}
	public Timestamp getPymtEndDt() {
		return pymtEndDt;
	}
	public void setPymtEndDt(Timestamp pymtEndDt) {
		this.pymtEndDt = pymtEndDt;
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
