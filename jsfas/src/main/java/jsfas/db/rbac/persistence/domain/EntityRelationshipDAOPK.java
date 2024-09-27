package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class EntityRelationshipDAOPK implements Serializable, Comparable<EntityRelationshipDAOPK>{

    /**
     * 
     */
    private static final long serialVersionUID = 2497495949725688015L;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ENTITY_TYPE", nullable = false)
    private EntityDAO entity;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ACTION_TYPE", nullable = false)
    private ActionDAO action;

    public EntityRelationshipDAOPK() {
        super();
    }
    
    public EntityRelationshipDAOPK(EntityDAO entity, ActionDAO action) {
        this();
        this.entity = entity;
        this.action = action;
    }
    
    public EntityDAO getEntity() {
        return entity;
    }

    public ActionDAO getAction() {
        return action;
    }

    public void setEntity(EntityDAO entity) {
        this.entity = entity;
    }

    public void setAction(ActionDAO action) {
        this.action = action;
    }
    
    private String getCompositeKey() {
        return this.getEntity().getEntityType() + this.getAction().getActionType();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof EntityRelationshipDAOPK) {
            EntityRelationshipDAOPK entityRelationshipDAOPK = (EntityRelationshipDAOPK)obj;
            return this.getCompositeKey().equals(entityRelationshipDAOPK.getCompositeKey());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getCompositeKey().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getEntity().getEntityType(), this.getAction().getActionType()));
    }

    @Override
    public int compareTo(EntityRelationshipDAOPK o) {
        // TODO Auto-generated method stub
        return this.getCompositeKey().compareTo(o.getCompositeKey());
    }
}
