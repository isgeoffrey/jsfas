package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "EL_PREDICATE")
@DynamicUpdate
public class PredicateDAO implements Serializable, Comparable<PredicateDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 5251231606011854841L;
    
    @Id
    @Column(name = "PRED_TYPE")
    private String predType;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PRED_QUERY_TYPE", nullable = false)
    private PredQueryDAO predQuery;
    
    @Column(name = "PRED_QUERY_STR", nullable = false)
    private String predQueryStr;
    
    @Column(name = "PRED_DESC", nullable = false)
    private String predDesc;
    
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PRED_PARAM_TYPE_DEF", nullable = false)
    private PredParamDAO predParamDef;
    
    @OneToMany(mappedBy = "predicate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PredParamDAO> predParams;
    
    @OneToMany(mappedBy = "predicate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityRelationshipDAO> entityRelationships;

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
    
    public PredicateDAO() {
        super();
        predParams = new LinkedHashSet<>();
        entityRelationships = new LinkedHashSet<>();
    }
    
    public String getPredType() {
        return predType;
    }

    public PredQueryDAO getPredQuery() {
        return predQuery;
    }

    public String getPredQueryStr() {
        return predQueryStr;
    }

    public String getPredDesc() {
        return predDesc;
    }

    public PredParamDAO getPredParamDef() {
        return predParamDef;
    }

    public void setPredType(String predType) {
        this.predType = predType;
    }

    public void setPredQuery(PredQueryDAO predQuery) {
        this.predQuery = predQuery;
    }

    public void setPredQueryStr(String predQueryStr) {
        this.predQueryStr = predQueryStr;
    }

    public void setPredDesc(String predDesc) {
        this.predDesc = predDesc;
    }

    public void setPredParamDef(PredParamDAO predParamDef) {
        this.predParamDef = predParamDef;
    }

    public Set<PredParamDAO> getPredParams() {
        return predParams;
    }

    public void setPredParams(Set<PredParamDAO> predParams) {
        this.predParams = predParams;
    }

    public Set<EntityRelationshipDAO> getEntityRelationships() {
        return entityRelationships;
    }

    public void setEntityRelationships(Set<EntityRelationshipDAO> entityRelationships) {
        this.entityRelationships = entityRelationships;
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
        if(obj instanceof PredicateDAO) {
            PredicateDAO predicateDAO = (PredicateDAO)obj;
            return this.getPredType().equals(predicateDAO.getPredType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getPredType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", this.getPredType(), this.getPredQuery().getPredQueryType(), this.getPredQueryStr(), this.getPredDesc(), this.getPredParamDef().getPredParamType(), this.getModCtrlTxt());
    }

    @Override
    public int compareTo(PredicateDAO o) {
        // TODO Auto-generated method stub
        return this.getPredType().compareTo(o.getPredType());
    }

}
