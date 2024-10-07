package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "FAS_STK_PLAN_DTL_STG")
public class FasStkPlanDtlStgDAO implements Serializable, Comparable<FasStkPlanDtlStgDAO> {

    private static final long serialVersionUID = 7014784243669996645L;

    @EmbeddedId
    private FasStkPlanDtlStgDAOPK fasStkPlanDtlStgDAOPK;

    @Column(name = "PROFILE_ID")
    private String profileId;

    @Column(name = "PROFILE_DESCR")
    private String profileDescr;

    @Column(name = "ASSET_DESCR_LONG")
    private String assetDescrLong;

    @Column(name = "TOTAL_COST")
    private BigDecimal totalCost;

    @Column(name = "NBV")
    private BigDecimal nbv;

    @Column(name = "VOUCHER_ID")
    private String voucherId;

    @Column(name = "INVOICE_ID")
    private String invoiceId;

    @Column(name = "INVOICE_DT")
    private Timestamp invoiceDt;

    @Column(name = "PO_ID")
    private String poId;

    @Column(name = "REGION_NAME")
    private String regionName;

    @Column(name = "NOT_UST_PROPRTY")
    private String notUstProprty;

    @Column(name = "DONATION_FLAG")
    private String donationFlag;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "STK_STATUS")
    private String stkStatus;

    @Column(name = "MOD_CTRL_TXT")
    private String modCtrlTxt;

    @Column(name = "CREAT_USER")
    private String createUser;

    @Column(name = "CREAT_DAT")
    private Timestamp createDate;

    @Column(name = "CHNG_USER")
    private String changeUser;

    @Column(name = "CHNG_DAT")
    private Timestamp changeDate;

    @Column(name = "OP_PAGE_NAM")
    private String opPageName;

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileDescr() {
		return profileDescr;
	}

	public void setProfileDescr(String profileDescr) {
		this.profileDescr = profileDescr;
	}

	public String getAssetDescrLong() {
		return assetDescrLong;
	}

	public void setAssetDescrLong(String assetDescrLong) {
		this.assetDescrLong = assetDescrLong;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getNbv() {
		return nbv;
	}

	public void setNbv(BigDecimal nbv) {
		this.nbv = nbv;
	}

	public String getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Timestamp getInvoiceDt() {
		return invoiceDt;
	}

	public void setInvoiceDt(Timestamp invoiceDt) {
		this.invoiceDt = invoiceDt;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getNotUstProprty() {
		return notUstProprty;
	}

	public void setNotUstProprty(String notUstProprty) {
		this.notUstProprty = notUstProprty;
	}

	public String getDonationFlag() {
		return donationFlag;
	}

	public void setDonationFlag(String donationFlag) {
		this.donationFlag = donationFlag;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStkStatus() {
		return stkStatus;
	}

	public void setStkStatus(String stkStatus) {
		this.stkStatus = stkStatus;
	}

	public String getModCtrlTxt() {
		return modCtrlTxt;
	}

	public void setModCtrlTxt(String modCtrlTxt) {
		this.modCtrlTxt = modCtrlTxt;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getChangeUser() {
		return changeUser;
	}

	public void setChangeUser(String changeUser) {
		this.changeUser = changeUser;
	}

	public Timestamp getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}

	public String getOpPageName() {
		return opPageName;
	}

	public void setOpPageName(String opPageName) {
		this.opPageName = opPageName;
	}

	public FasStkPlanDtlStgDAOPK getFasStkPlanDtlStgDAOPK() {
		return fasStkPlanDtlStgDAOPK;
	}

	public void setFasStkPlanDtlStgDAOPK(FasStkPlanDtlStgDAOPK fasStkPlanDtlStgDAOPK) {
		this.fasStkPlanDtlStgDAOPK = fasStkPlanDtlStgDAOPK;
	}
	
	@Override
	public int compareTo(FasStkPlanDtlStgDAO o) {
		return (Integer.valueOf(this.fasStkPlanDtlStgDAOPK.hashCode()).compareTo(Integer.valueOf(o.getFasStkPlanDtlStgDAOPK().hashCode())));
	}

    
}