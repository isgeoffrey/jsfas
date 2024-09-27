package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "EL_ENTITY_RELATIONSHIP")
@DynamicUpdate
public class EntityRelationshipDAO implements Serializable, Comparable<EntityRelationshipDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 9199596888295436929L;
    
    @EmbeddedId
    private EntityRelationshipDAOPK entityRelationshipPK;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRED_TYPE")
    private PredicateDAO predicate;

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
    
    public EntityRelationshipDAOPK getEntityRelationshipPK() {
        return entityRelationshipPK;
    }

    public void setEntityRelationshipPK(EntityRelationshipDAOPK entityRelationshipPK) {
        this.entityRelationshipPK = entityRelationshipPK;
    }

    public Optional<PredicateDAO> getPredicate() {
        return Optional.ofNullable(predicate);
    }

    public void setPredicate(PredicateDAO predicate) {
        this.predicate = predicate;
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
        if(obj instanceof EntityRelationshipDAO) {
            EntityRelationshipDAO entityRelationshipDAO = (EntityRelationshipDAO)obj;
            return this.getEntityRelationshipPK().equals(entityRelationshipDAO.getEntityRelationshipPK());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getEntityRelationshipPK().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getEntityRelationshipPK().toString(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(EntityRelationshipDAO o) {
        // TODO Auto-generated method stub
        return this.getEntityRelationshipPK().compareTo(o.getEntityRelationshipPK());
    }

}
