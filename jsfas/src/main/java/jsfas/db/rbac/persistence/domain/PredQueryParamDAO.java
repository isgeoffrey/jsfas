package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "FAS_PRED_QUERY_PARAM")
@DynamicUpdate
public class PredQueryParamDAO implements Serializable, Comparable<PredQueryParamDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = -8919790221701230997L;
    
    @Id
    @Column(name = "PRED_QUERY_PARAM_TYPE")
    private String predQueryParamType;
    
    @Column(name = "PRED_QUERY_PARAM_DESC", nullable = false)
    private String predQueryParamDesc;
    
    @OneToMany(mappedBy = "predQueryParam", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PredParamDAO> predParams;

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
    
    public PredQueryParamDAO() {
        super();
        predParams = new LinkedHashSet<>();
    }
    
    public String getPredQueryParamType() {
        return predQueryParamType;
    }

    public String getPredQueryParamDesc() {
        return predQueryParamDesc;
    }

    public Set<PredParamDAO> getPredParams() {
        return predParams;
    }

    public void setPredQueryParamType(String predQueryParamType) {
        this.predQueryParamType = predQueryParamType;
    }

    public void setPredQueryParamDesc(String predQueryParamDesc) {
        this.predQueryParamDesc = predQueryParamDesc;
    }

    public void setPredParams(Set<PredParamDAO> predParams) {
        this.predParams = predParams;
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
        if(obj instanceof PredQueryParamDAO) {
            PredQueryParamDAO predQueryParamDAO = (PredQueryParamDAO)obj;
            return this.getPredQueryParamType().equals(predQueryParamDAO.getPredQueryParamType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getPredQueryParamType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getPredQueryParamType(), this.getPredQueryParamDesc(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(PredQueryParamDAO o) {
        // TODO Auto-generated method stub
        return this.getPredQueryParamType().compareTo(o.getPredQueryParamType());
    }

}
