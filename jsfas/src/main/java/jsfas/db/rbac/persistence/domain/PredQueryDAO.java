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
@Table(name = "EL_PRED_QUERY")
@DynamicUpdate
public class PredQueryDAO implements Serializable, Comparable<PredQueryDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 7318115264558561942L;
    
    @Id
    @Column(name = "PRED_QUERY_TYPE")
    private String predQueryType;
    
    @Column(name = "PRED_QUERY_DESC", nullable = false)
    private String predQueryDesc;
    
    @OneToMany(mappedBy = "predQuery", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PredicateDAO> predicates;

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
    
    public PredQueryDAO() {
        super();
        predicates = new LinkedHashSet<>();
    }
    
    public String getPredQueryType() {
        return predQueryType;
    }

    public String getPredQueryDesc() {
        return predQueryDesc;
    }

    public void setPredQueryType(String predQueryType) {
        this.predQueryType = predQueryType;
    }

    public void setPredQueryDesc(String predQueryDesc) {
        this.predQueryDesc = predQueryDesc;
    }

    public Set<PredicateDAO> getPredicates() {
        return predicates;
    }

    public void setPredicates(Set<PredicateDAO> predicates) {
        this.predicates = predicates;
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
        if(obj instanceof PredQueryDAO) {
            PredQueryDAO predQueryDAO = (PredQueryDAO)obj;
            return this.getPredQueryType().equals(predQueryDAO.getPredQueryType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getPredQueryType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getPredQueryType(), this.getPredQueryDesc(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(PredQueryDAO o) {
        // TODO Auto-generated method stub
        return this.getPredQueryType().compareTo(o.getPredQueryType());
    }

}
