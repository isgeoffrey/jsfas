package jsfas.db.main.persistence.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import jsfas.common.constants.ApplStatusConstants;
import jsfas.common.utils.GeneralUtil;


public class FoApplicationEnquiryComplete extends FoApplicationEnquiryRaw {
	
    private String deptHead;
    private String deptHeadAprvDate;
    private String bco;
    private String bcoAprvDate;
    private String adHoc1;
    private String adHoc1AprvDate;
    private String adHoc2;
    private String adHoc2AprvDate;
    private String adHoc3;
    private String adHoc3AprvDate;
    private String adHoc4;
    private String adHoc4AprvDate;
    private String adHoc5;
    private String adHoc5AprvDate;
    private String foSR;
    private String foSRAprvDate;
    private String foSFM;
    private String foSFMAprvDate;
    private String foPB;
    private String foPBAprvDate;
    
    public FoApplicationEnquiryComplete(FoApplicationEnquiryRaw foApplicationEnquiryRaw) {
        this.appId = foApplicationEnquiryRaw.getAppId();
        this.appNbr = foApplicationEnquiryRaw.getAppNbr();
        this.brNo = foApplicationEnquiryRaw.getBrNo();
        this.versionNo = foApplicationEnquiryRaw.getVersionNo();
        this.applRequesterDept = foApplicationEnquiryRaw.getApplRequesterDept();
        this.applRequesterId = foApplicationEnquiryRaw.getApplRequesterId();
        this.applRequesterName = foApplicationEnquiryRaw.getApplRequesterName();
        this.applUserId = foApplicationEnquiryRaw.getApplUserId();
        this.applUserName = foApplicationEnquiryRaw.getApplUserName();
        this.applDttm = foApplicationEnquiryRaw.getApplDttm();
        this.applStatCde = foApplicationEnquiryRaw.getApplStatCde();
        this.applStatDescr = foApplicationEnquiryRaw.getApplStatDescr();
        this.categoryDescr = foApplicationEnquiryRaw.getCategorDescr();
        
        switch(foApplicationEnquiryRaw.getApplStatCde()){
        	case ApplStatusConstants.APPROVED:
        	case ApplStatusConstants.COMPLETED:
        	case ApplStatusConstants.PYMT_SUBM:
        		this.pendingAprverId = "System";
                this.pendingAprverName = "System";
                this.pendingAprverType = "System";
                break;
        	case ApplStatusConstants.ISSUE_OFFER:
        	case ApplStatusConstants.OFFER_REJECTED:
        	case ApplStatusConstants.PYMT_REJECTED:
        	case ApplStatusConstants.READY_SUBM:
        	case ApplStatusConstants.REJECTED:
        		this.pendingAprverId = foApplicationEnquiryRaw.getApplRequesterId();
                this.pendingAprverName = foApplicationEnquiryRaw.getApplRequesterName();
                this.pendingAprverType = "Requester";
                break;
        	case ApplStatusConstants.PENDING_DECL:
        	case ApplStatusConstants.RELEASE_OFFER:
        	case ApplStatusConstants.PENDING_APPL_ACCT:
        		this.pendingAprverId = foApplicationEnquiryRaw.getApplRequesterId();
                this.pendingAprverName = foApplicationEnquiryRaw.getApplUserName();
                this.pendingAprverType = "Applicant";
                break;
            default:
            	this.pendingAprverId = foApplicationEnquiryRaw.getPendingAprverId();
                this.pendingAprverName = foApplicationEnquiryRaw.getPendingAprverName();
                this.pendingAprverType = foApplicationEnquiryRaw.getPendingAprverType();
        }
                
        this.elTypeNam = foApplicationEnquiryRaw.getElTypeNam();
        
        this.applStartTerm = foApplicationEnquiryRaw.getApplStartTerm();
        this.applEndTerm = foApplicationEnquiryRaw.getApplEndTerm();
        
        this.applStartDt = foApplicationEnquiryRaw.getApplStartDt();
        this.applEndDt = foApplicationEnquiryRaw.getApplEndDt();
        this.prgmCde = foApplicationEnquiryRaw.getPrgmCde();
        this.schCde = foApplicationEnquiryRaw.getSchCde();
        this.dept = foApplicationEnquiryRaw.getDept();
        this.appAmt = foApplicationEnquiryRaw.getAppAmt();
        this.pymtAmt = foApplicationEnquiryRaw.getPymtAmt();
        this.mpfAmt = foApplicationEnquiryRaw.getMpfAmt();
        this.pymtTypeCde = foApplicationEnquiryRaw.getPymtTypeCde();
        this.instalmNo = foApplicationEnquiryRaw.getInstalmNo();
        this.instalmSeq = foApplicationEnquiryRaw.getInstalmSeq();
        this.pymtStartDt = foApplicationEnquiryRaw.getPymtStartDt();
        this.pymtEndDt = foApplicationEnquiryRaw.getPymtEndDt();
        this.creatDat = foApplicationEnquiryRaw.getCreatDat();
        this.chngDat = foApplicationEnquiryRaw.getChngDat();
        this.applUserDept = foApplicationEnquiryRaw.getApplUserDept();
        this.applUserIdCount = foApplicationEnquiryRaw.getApplUserIdCount();
        this.applStartTermStrm = foApplicationEnquiryRaw.getApplStartTermStrm();
        this.applEndTermStrm = foApplicationEnquiryRaw.getApplEndTermStrm();
        
        this.applSchlShort = foApplicationEnquiryRaw.getApplSchlShort();
        this.applSchlLong = foApplicationEnquiryRaw.getApplSchlLong();
        this.reqSchlShort = foApplicationEnquiryRaw.getReqSchlShort();
        this.reqSchlLong = foApplicationEnquiryRaw.getReqSchlLong();
    }
    
    public FoApplicationEnquiryComplete() {
    }

	public String getDeptHead() {
		return deptHead;
	}
	public void setDeptHead(String deptHead) {
		this.deptHead = deptHead;
	}
	public String getDeptHeadAprvDate() {
		return deptHeadAprvDate;
	}
	public void setDeptHeadAprvDate(String deptHeadAprvDate) {
		this.deptHeadAprvDate = deptHeadAprvDate;
	}
	public String getBco() {
		return bco;
	}
	public void setBco(String bco) {
		this.bco = bco;
	}
	public String getBcoAprvDate() {
		return bcoAprvDate;
	}
	public void setBcoAprvDate(String bcoAprvDate) {
		this.bcoAprvDate = bcoAprvDate;
	}
	public String getAdHoc1() {
		return adHoc1;
	}
	public void setAdHoc1(String adHoc1) {
		this.adHoc1 = adHoc1;
	}
	public String getAdHoc1AprvDate() {
		return adHoc1AprvDate;
	}
	public void setAdHoc1AprvDate(String adHoc1AprvDate) {
		this.adHoc1AprvDate = adHoc1AprvDate;
	}
	public String getAdHoc2() {
		return adHoc2;
	}
	public void setAdHoc2(String adHoc2) {
		this.adHoc2 = adHoc2;
	}
	public String getAdHoc2AprvDate() {
		return adHoc2AprvDate;
	}
	public void setAdHoc2AprvDate(String adHoc2AprvDate) {
		this.adHoc2AprvDate = adHoc2AprvDate;
	}
	public String getAdHoc3() {
		return adHoc3;
	}
	public void setAdHoc3(String adHoc3) {
		this.adHoc3 = adHoc3;
	}
	public String getAdHoc3AprvDate() {
		return adHoc3AprvDate;
	}
	public void setAdHoc3AprvDate(String adHoc3AprvDate) {
		this.adHoc3AprvDate = adHoc3AprvDate;
	}
	public String getAdHoc4() {
		return adHoc4;
	}
	public void setAdHoc4(String adHoc4) {
		this.adHoc4 = adHoc4;
	}
	public String getAdHoc4AprvDate() {
		return adHoc4AprvDate;
	}
	public void setAdHoc4AprvDate(String adHoc4AprvDate) {
		this.adHoc4AprvDate = adHoc4AprvDate;
	}
	public String getAdHoc5() {
		return adHoc5;
	}
	public void setAdHoc5(String adHoc5) {
		this.adHoc5 = adHoc5;
	}
	public String getAdHoc5AprvDate() {
		return adHoc5AprvDate;
	}
	public void setAdHoc5AprvDate(String adHoc5AprvDate) {
		this.adHoc5AprvDate = adHoc5AprvDate;
	}
	public String getFoSR() {
		return foSR;
	}
	public void setFoSR(String foSR) {
		this.foSR = foSR;
	}
	public String getFoSRAprvDate() {
		return foSRAprvDate;
	}
	public void setFoSRAprvDate(String foSRAprvDate) {
		this.foSRAprvDate = foSRAprvDate;
	}
	public String getFoSFM() {
		return foSFM;
	}
	public void setFoSFM(String foSFM) {
		this.foSFM = foSFM;
	}
	public String getFoSFMAprvDate() {
		return foSFMAprvDate;
	}
	public void setFoSFMAprvDate(String foSFMAprvDate) {
		this.foSFMAprvDate = foSFMAprvDate;
	}
	public String getFoPB() {
		return foPB;
	}
	public void setFoPB(String foPB) {
		this.foPB = foPB;
	}
	public String getFoPBAprvDate() {
		return foPBAprvDate;
	}
	public void setFoPBAprvDate(String foPBAprvDate) {
		this.foPBAprvDate = foPBAprvDate;
	}
	
	public Integer getNextEmptyAdHoc() {
	
		if (!GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc1))) {
			return 1;
		}else if (!GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc2))) {
			return 2;
		}else if (!GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc3))) {
			return 3;
		}else if (!GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc4))) {
			return 4;
		}else if (!GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc5))) {
			return 5;
		}
		return 0;
	}
	
	public void setNextAdHocValues(String aprver) {
		if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc1))) {
			this.setAdHoc1(aprver);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc2))) {
			this.setAdHoc2(aprver);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc3))) {
			this.setAdHoc3(aprver);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc4))) {
			this.setAdHoc4(aprver);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc5))) {
			this.setAdHoc5(aprver);
		}
		
		// we do not provide ad hoc approver if there are more than 5
	}
	
	public void setNextAdHocValues(String aprver, String aprvdttm) {
		if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc1))) {
			this.setAdHoc1(aprver);
			this.setAdHoc1AprvDate(aprvdttm);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc2))) {
			this.setAdHoc2(aprver);
			this.setAdHoc2AprvDate(aprvdttm);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc3))) {
			this.setAdHoc3(aprver);
			this.setAdHoc3AprvDate(aprvdttm);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc4))) {
			this.setAdHoc4(aprver);
			this.setAdHoc4AprvDate(aprvdttm);
		}else if (GeneralUtil.isBlankString(GeneralUtil.initNullString(adHoc5))) {
			this.setAdHoc5(aprver);
			this.setAdHoc5AprvDate(aprvdttm);
		}
		
		// we do not provide ad hoc approver if there are more than 5
	}
	
}