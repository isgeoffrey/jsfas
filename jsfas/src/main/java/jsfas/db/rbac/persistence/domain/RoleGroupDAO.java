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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "FAS_ROLE_GROUP")
@DynamicUpdate
public class RoleGroupDAO implements Serializable, Comparable<RoleGroupDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = 6691656171032280757L;
    
    @Id
    @GenericGenerator(name = "roleGroupSeq", strategy = "jsfas.common.StringSeqGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "FAS_ROLE_GROUP_SEQ"),
            @Parameter(name = "leftPadSize", value = "6"),
            @Parameter(name = "includeModCtrlTxtPrefix", value = "true")
    })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleGroupSeq")
    @Column(name = "ROLE_GROUP_ID")
    private String roleGroupId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ROLE_GROUP_ID", referencedColumnName = "ROLE_GROUP_ID")
    private RoleGroupDAO parentRoleGroup;
    
    @OneToMany(mappedBy = "parentRoleGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoleGroupDAO> childRoleGroups;
    
    @Column(name = "ROLE_GROUP_DESC", nullable = false)
    private String roleGroupDesc;
    
    @OneToMany(mappedBy = "userRoleGroupPK.roleGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoleGroupDAO> userRoleGroups;
    
    @OneToMany(mappedBy = "permissionPK.roleGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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
    
    public RoleGroupDAO() {
        super();
        childRoleGroups = new LinkedHashSet<>();
        userRoleGroups = new LinkedHashSet<>();
        permissions = new LinkedHashSet<>();
    }
    
    public String getRoleGroupId() {
        return roleGroupId;
    }

    public Optional<RoleGroupDAO> getParentRoleGroup() {
        return Optional.ofNullable(parentRoleGroup);
    }

    public void setParentRoleGroup(RoleGroupDAO parentRoleGroup) {
        this.parentRoleGroup = parentRoleGroup;
    }

    public Set<RoleGroupDAO> getChildRoleGroups() {
        return childRoleGroups;
    }

    public void setChildRoleGroups(Set<RoleGroupDAO> childRoleGroups) {
        this.childRoleGroups = childRoleGroups;
    }

    public String getRoleGroupDesc() {
        return roleGroupDesc;
    }

    public void setRoleGroupId(String roleGroupId) {
        this.roleGroupId = roleGroupId;
    }

    public void setRoleGroupDesc(String roleGroupDesc) {
        this.roleGroupDesc = roleGroupDesc;
    }

    public Set<UserRoleGroupDAO> getUserRoleGroups() {
        return userRoleGroups;
    }

    public void setUserRoleGroups(Set<UserRoleGroupDAO> userRoleGroups) {
        this.userRoleGroups = userRoleGroups;
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
        if(obj instanceof RoleGroupDAO) {
            RoleGroupDAO roleGroupDAO = (RoleGroupDAO)obj;
            return this.getRoleGroupId().equals(roleGroupDAO.getRoleGroupId());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getRoleGroupId().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getRoleGroupId(), this.getRoleGroupDesc(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(RoleGroupDAO o) {
        // TODO Auto-generated method stub
        return this.getRoleGroupId().compareTo(o.getRoleGroupId());
    }
    
}
