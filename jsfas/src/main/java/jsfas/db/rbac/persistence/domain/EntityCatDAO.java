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
@Table(name = "EL_ENTITY_CAT")
@DynamicUpdate
public class EntityCatDAO implements Serializable, Comparable<EntityCatDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 5408291931165236868L;
    
    @Id
    @Column(name = "ENTITY_CAT_TYPE")
    private String entityCatType;
    
    @Column(name = "ENTITY_CAT_DESC", nullable = false)
    private String entityCatDesc;
    
    @OneToMany(mappedBy = "entityCat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EntityDAO> entities;

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
    
    public EntityCatDAO() {
        super();
        entities = new LinkedHashSet<>();
    }
    
    public String getEntityCatType() {
        return entityCatType;
    }

    public String getEntityCatDesc() {
        return entityCatDesc;
    }

    public void setEntityCatType(String entityCatType) {
        this.entityCatType = entityCatType;
    }

    public void setEntityCatDesc(String entityCatDesc) {
        this.entityCatDesc = entityCatDesc;
    }

    public Set<EntityDAO> getEntities() {
        return entities;
    }

    public void setEntities(Set<EntityDAO> entities) {
        this.entities = entities;
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
        if(obj instanceof EntityCatDAO) {
            EntityCatDAO entityCatDAO = (EntityCatDAO)obj;
            return this.getEntityCatType().equals(entityCatDAO.getEntityCatType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getEntityCatType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getEntityCatType(), this.getEntityCatDesc(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(EntityCatDAO o) {
        // TODO Auto-generated method stub
        return this.getEntityCatType().compareTo(o.getEntityCatType());
    }

}
