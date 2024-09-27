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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "EL_ENTITY")
@DynamicUpdate
public class EntityDAO implements Serializable, Comparable<EntityDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 8732309234319033607L;
    
    @Id
    @Column(name = "ENTITY_TYPE")
    private String entityType;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ENTITY_TYPE", referencedColumnName = "ENTITY_TYPE")
    private EntityDAO parentEntity;
    
    @OneToMany(mappedBy = "parentEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EntityDAO> childEntities;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ENTITY_CAT_TYPE", nullable = false)
    private EntityCatDAO entityCat;
    
    @Column(name = "ENTITY_DESC", nullable = false)
    private String entityDesc;
    
    @OneToMany(mappedBy = "entityRelationshipPK.entity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityRelationshipDAO> entityRelationships;
    
    @OneToMany(mappedBy = "permissionPK.entity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PermissionDAO> permissions;

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
    
    public EntityDAO() {
        super();
        childEntities = new LinkedHashSet<>();
        entityRelationships = new LinkedHashSet<>();
        permissions = new LinkedHashSet<>();
    }
    
    public String getEntityType() {
        return entityType;
    }

    public Optional<EntityDAO> getParentEntity() {
        return Optional.ofNullable(parentEntity);
    }

    public EntityCatDAO getEntityCat() {
        return entityCat;
    }

    public void setEntityCat(EntityCatDAO entityCat) {
        this.entityCat = entityCat;
    }

    public String getEntityDesc() {
        return entityDesc;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setParentEntity(EntityDAO parentEntity) {
        this.parentEntity = parentEntity;
    }

    public Set<EntityDAO> getChildEntities() {
        return childEntities;
    }

    public void setChildEntities(Set<EntityDAO> childEntities) {
        this.childEntities = childEntities;
    }

    public void setEntityDesc(String entityDesc) {
        this.entityDesc = entityDesc;
    }

    public Set<EntityRelationshipDAO> getEntityRelationships() {
        return entityRelationships;
    }

    public void setEntityRelationships(Set<EntityRelationshipDAO> entityRelationships) {
        this.entityRelationships = entityRelationships;
    }
    
    public Set<PermissionDAO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDAO> permissions) {
        this.permissions = permissions;
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
        if(obj instanceof EntityDAO) {
            EntityDAO entityDAO = (EntityDAO)obj;
            return this.getEntityType().equals(entityDAO.getEntityType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getEntityType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getEntityType(), this.getEntityCat().getEntityCatType(), this.getEntityDesc()));     
    }

    @Override
    public int compareTo(EntityDAO o) {
        // TODO Auto-generated method stub
        return this.getEntityType().compareTo(o.getEntityType());
    }

}
