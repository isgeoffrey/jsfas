package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import jsfas.db.main.persistence.domain.UserProfileHeaderDAO;

@Embeddable
public class UserRoleGroupDAOPK implements Serializable, Comparable<UserRoleGroupDAOPK> {

    /**
     * 
     */
    private static final long serialVersionUID = 738630581344149730L;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "USER_NAM", nullable = false)
    private UserProfileHeaderDAO userProfileHeader;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ROLE_GROUP_ID", nullable = false)
    private RoleGroupDAO roleGroup;
    
    public UserRoleGroupDAOPK() {
        super();
    }
    
    public UserRoleGroupDAOPK(UserProfileHeaderDAO userProfileHeader, RoleGroupDAO roleGroup) {
        this.userProfileHeader = userProfileHeader;
        this.roleGroup = roleGroup;
    }
    
    public UserProfileHeaderDAO getUserProfileHeader() {
        return userProfileHeader;
    }

    public void setUserProfileHeader(UserProfileHeaderDAO userProfileHeader) {
        this.userProfileHeader = userProfileHeader;
        
    }

    public RoleGroupDAO getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(RoleGroupDAO roleGroup) {
        this.roleGroup = roleGroup;
    }
    
    private String getCompositeKey() {
        return this.getUserProfileHeader().getUserName() + this.getRoleGroup().getRoleGroupId();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UserRoleGroupDAOPK) {
            UserRoleGroupDAOPK userRoleGroupDAOPK = (UserRoleGroupDAOPK)obj;
            return this.getCompositeKey().equals(userRoleGroupDAOPK.getCompositeKey());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getCompositeKey().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getUserProfileHeader().getUserName(), this.getRoleGroup().getRoleGroupId()));
    }

    @Override
    public int compareTo(UserRoleGroupDAOPK o) {
        // TODO Auto-generated method stub
        return this.getCompositeKey().compareTo(o.getCompositeKey());
    }

}
