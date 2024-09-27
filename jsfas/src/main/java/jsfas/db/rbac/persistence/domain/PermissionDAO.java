package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import jsfas.common.utils.GeneralUtil;

@Entity
@Table(name = "FAS_PERMISSION")
@DynamicUpdate
public class PermissionDAO implements Serializable, Comparable<PermissionDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 2460972621032261271L;
    
    @EmbeddedId
    private PermissionDAOPK permissionPK;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PERM_STATUS_TYPE", nullable = false)
    private PermStatusDAO permStatus;
    
    @Column(name = "EFFECTIVE_DATE", nullable = false)
    private Timestamp effectiveDate;    
    
    @Column(name = "EXPIRY_DATE", nullable = false)
    private Timestamp expiryDate;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "PRED_PARAM_TYPE")
    private PredParamDAO predParam;

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
    
    public PermissionDAO() {
        super();
    }
    
    public PermissionDAO(PermissionDAOPK permissionPK) {
        this();
        this.permissionPK = permissionPK;
    }
    
    public PermissionDAOPK getPermissionPK() {
        return permissionPK;
    }

    public void setPermissionPK(PermissionDAOPK permissionPK) {
        this.permissionPK = permissionPK;
    }

    public PermStatusDAO getPermStatus() {
        return permStatus;
    }

    public void setPermStatus(PermStatusDAO permStatus) {
        this.permStatus = permStatus;
    }

    public Timestamp getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Optional<PredParamDAO> getPredParam() {
        return Optional.ofNullable(predParam);
    }

    public void setPredParam(PredParamDAO predParam) {
        this.predParam = predParam;
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public String getChangeUser() {
        return changeUser;
    }

    public Timestamp getChangeDate() {
        return changeDate;
    }
    
    public String getOpPageName() {
        return opPageName;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setChangeUser(String changeUser) {
        this.changeUser = changeUser;
    }

    public void setChangeDate(Timestamp changeDate) {
        this.changeDate = changeDate;
    }

    public void setOpPageName(String opPageName) {
        this.opPageName = opPageName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PermissionDAO) {
            PermissionDAO permissionDAO = (PermissionDAO)obj;
            return this.getPermissionPK().equals(permissionDAO.getPermissionPK());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getPermissionPK().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getPermissionPK().toString()
                , this.getPermStatus().getPermStatusType()
                , GeneralUtil.getStringByDate(this.getEffectiveDate())
                , GeneralUtil.getStringByDate(this.getExpiryDate())
                , this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(PermissionDAO o) {
        // TODO Auto-generated method stub
        return this.getPermissionPK().compareTo(o.getPermissionPK());
    }

}
