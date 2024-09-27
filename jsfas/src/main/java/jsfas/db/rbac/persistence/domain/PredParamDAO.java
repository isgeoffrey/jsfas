package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "EL_PRED_PARAM")
@DynamicUpdate
public class PredParamDAO implements Serializable, Comparable<PredParamDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = -2602647142815978050L;
    
    @Id
    @GenericGenerator(name = "predParamSeq", strategy = "jsfas.common.StringSeqGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "EL_PRED_PARAM_SEQ"),
            @Parameter(name = "leftPadSize", value = "6"),
            @Parameter(name = "includeModCtrlTxtPrefix", value = "true")
    })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "predParamSeq")
    @Column(name = "PRED_PARAM_TYPE")
    private String predParamType;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PRED_QUERY_PARAM_TYPE", nullable = false)
    private PredQueryParamDAO predQueryParam;
    
    @Column(name = "PRED_QUERY_PARAM_STR", nullable = false)
    private String predQueryParamStr;
    
    @Column(name = "PRED_PARAM_DESC", nullable = false)
    private String predParamDesc;
    
    @OneToMany(mappedBy = "predParam", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PermissionDAO> permissions;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRED_TYPE", nullable = false)
    private PredicateDAO predicate;
    
    @OneToOne(mappedBy = "predParamDef", fetch = FetchType.LAZY, orphanRemoval = true)
    private PredicateDAO predicateDef;

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
    
    public PredParamDAO() {
        super();
        permissions = new LinkedHashSet<>();
    }
    
    public String getPredParamType() {
        return predParamType;
    }

    public PredQueryParamDAO getPredQueryParam() {
        return predQueryParam;
    }

    public String getPredQueryParamStr() {
        return predQueryParamStr;
    }

    public String getPredParamDesc() {
        return predParamDesc;
    }

    public void setPredParamType(String predParamType) {
        this.predParamType = predParamType;
    }

    public void setPredQueryParam(PredQueryParamDAO predQueryParam) {
        this.predQueryParam = predQueryParam;
    }

    public void setPredQueryParamStr(String predQueryParamStr) {
        this.predQueryParamStr = predQueryParamStr;
    }

    public void setPredParamDesc(String predParamDesc) {
        this.predParamDesc = predParamDesc;
    }
    
    public Set<PermissionDAO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDAO> permissions) {
        this.permissions = permissions;
    }

    public PredicateDAO getPredicate() {
        return predicate;
    }

    public void setPredicate(PredicateDAO predicate) {
        this.predicate = predicate;
    }

    public Optional<PredicateDAO> getPredicateDef() {
        return Optional.ofNullable(predicateDef);
    }

    public void setPredicateDef(PredicateDAO predicateDef) {
        this.predicateDef = predicateDef;
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
        if(obj instanceof PredParamDAO) {
            PredParamDAO predParamDAO = (PredParamDAO)obj;
            return this.getPredParamType().equals(predParamDAO.getPredParamType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getPredParamType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getPredParamType(), this.getPredQueryParam().getPredQueryParamType(), this.getPredQueryParamStr(), this.getPredParamDesc(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(PredParamDAO o) {
        // TODO Auto-generated method stub
        return this.getPredParamType().compareTo(o.getPredParamType());
    }
}
