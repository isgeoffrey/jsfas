package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class PermissionDAOPK implements Serializable, Comparable<PermissionDAOPK> {

    /**
     * 
     */
    private static final long serialVersionUID = -2454687043590965014L;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ROLE_GROUP_ID", nullable = false)
    private RoleGroupDAO roleGroup;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ENTITY_TYPE", nullable = false)
    private EntityDAO entity;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ACTION_TYPE", nullable = false)
    private ActionDAO action;
    
    public PermissionDAOPK() {
        super();
    }
    
    public PermissionDAOPK(RoleGroupDAO roleGroup, EntityDAO entity, ActionDAO action) {
        this();
        this.roleGroup = roleGroup;
        this.entity = entity;
        this.action = action;
    }
    
    public RoleGroupDAO getRoleGroup() {
        return roleGroup;
    }

    public EntityDAO getEntity() {
        return entity;
    }

    public ActionDAO getAction() {
        return action;
    }

    public void setRoleGroup(RoleGroupDAO roleGroup) {
        this.roleGroup = roleGroup;
    }

    public void setEntity(EntityDAO entity) {
        this.entity = entity;
    }

    public void setAction(ActionDAO action) {
        this.action = action;
    }
    
    private String getCompositeKey() {
        return this.getRoleGroup().getRoleGroupId() + this.getEntity().getEntityType() + this.getAction().getActionType();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PermissionDAOPK) {
            PermissionDAOPK permissionDAOPK = (PermissionDAOPK)obj;
            return this.getCompositeKey().equals(permissionDAOPK.getCompositeKey());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getCompositeKey().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getRoleGroup().getRoleGroupId(), this.getEntity().getEntityType(), this.getAction().getActionType()));
    }

    @Override
    public int compareTo(PermissionDAOPK o) {
        // TODO Auto-generated method stub
        return this.getCompositeKey().compareTo(o.getCompositeKey());
    }

}
