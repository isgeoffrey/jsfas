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
@Table(name = "EL_PERM_STATUS")
@DynamicUpdate
public class PermStatusDAO implements Serializable, Comparable<PermStatusDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = -4027217737627980472L;

    @Id
    @Column(name = "PERM_STATUS_TYPE")
    private String permStatusType;
    
    @Column(name = "PERM_STATUS_DESC", nullable = false)
    private String permStatusDesc;
    
    @OneToMany(mappedBy = "permStatus", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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
    
    public PermStatusDAO() {
        super();
        permissions = new LinkedHashSet<>();
    }
    
    public String getPermStatusType() {
        return permStatusType;
    }

    public String getPermStatusDesc() {
        return permStatusDesc;
    }

    public void setPermStatusType(String permStatusType) {
        this.permStatusType = permStatusType;
    }

    public void setPermStatusDesc(String permStatusDesc) {
        this.permStatusDesc = permStatusDesc;
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
        if(obj instanceof PermStatusDAO) {
            PermStatusDAO permStatusDAO = (PermStatusDAO)obj;
            return this.getPermStatusType().equals(permStatusDAO.getPermStatusType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getPermStatusType().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getPermStatusType(), this.getPermStatusDesc(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(PermStatusDAO o) {
        // TODO Auto-generated method stub
        return this.getPermStatusType().compareTo(o.getPermStatusType());
    }

}
