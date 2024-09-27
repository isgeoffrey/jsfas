package jsfas.db.rbac.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "FAS_USER_ROLE_GROUP")
@DynamicUpdate
public class UserRoleGroupDAO implements Serializable, Comparable<UserRoleGroupDAO> {

    /**
     * 
     */
    private static final long serialVersionUID = -2787335027796301204L;
    
    @EmbeddedId
    private UserRoleGroupDAOPK userRoleGroupPK;

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
    
    public UserRoleGroupDAO() {
        super();
    }
    
    public UserRoleGroupDAO(UserRoleGroupDAOPK userRoleGroupPK) {
        this.userRoleGroupPK = userRoleGroupPK;
    }
    
    public UserRoleGroupDAOPK getUserRoleGroupPK() {
        return userRoleGroupPK;
    }

    public void setUserRoleGroupPK(UserRoleGroupDAOPK userRoleGroupPK) {
        this.userRoleGroupPK = userRoleGroupPK;
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
        if(obj instanceof UserRoleGroupDAO) {
            UserRoleGroupDAO userRoleGroupDAO = (UserRoleGroupDAO)obj;
            return this.getUserRoleGroupPK().equals(userRoleGroupDAO.getUserRoleGroupPK());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getUserRoleGroupPK().hashCode();
    }
    
    @Override
    public String toString() {
        return String.join(",", Arrays.asList(this.getUserRoleGroupPK().toString(), this.getModCtrlTxt()));
    }

    @Override
    public int compareTo(UserRoleGroupDAO o) {
        // TODO Auto-generated method stub
        return this.getUserRoleGroupPK().compareTo(o.getUserRoleGroupPK());
    }

}
