package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "FAS_USER_FUNC_V")
public class UserFuncVDAO implements Comparable<UserFuncVDAO>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4411100687351273272L;
	
	@EmbeddedId
    private UserFuncVDAOPK userFuncVDAOPK;
	
	public UserFuncVDAOPK getUserFuncVDAOPK() {
		return userFuncVDAOPK;
	}
	
	@Override
	public int compareTo(UserFuncVDAO o) {
		return (Integer.valueOf(this.userFuncVDAOPK.hashCode()).compareTo(Integer.valueOf(o.getUserFuncVDAOPK().hashCode())));
	}

}
