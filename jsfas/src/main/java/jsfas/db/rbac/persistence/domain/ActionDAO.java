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
@Table(name = "FAS_ACTION")
@DynamicUpdate
public class ActionDAO implements Serializable, Comparable<ActionDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 1687400193279995216L;

    @Id
    @Column(name = "ACTION_TYPE")
    private String actionType;
    
    @Column(name = "ACTION_DESC", nullable = false)
    private String actionDesc;
    
    @OneToMany(mappedBy = "entityRelationshipPK.action", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityRelationshipDAO> entityRelationships;
    
    @OneToMany(mappedBy = "permissionPK.action", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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
    
    public ActionDAO() {
        super();
        entityRelationships = new LinkedHashSet<>();
        permissions = new LinkedHashSet<>();
    }
    
    public String getActionType() {
        return actionType;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public Set<EntityRelationshipDAO> getEntityRelationships() {
        return entityRelationships;
    }

    public void setEntityRelationships(Set<EntityRelationshipDAO> entityActions) {
        this.entityRelationships = entityActions;
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
        if(obj instanceof ActionDAO) {
            ActionDAO actionDAO = (ActionDAO)obj;
            return this.getActionType().equals(actionDAO.getActionType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getActionType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getActionType(), this.getActionDesc(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(ActionDAO o) {
        // TODO Auto-generated method stub
        return this.getActionType().compareTo(o.getActionType());
    }

}
