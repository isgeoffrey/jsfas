package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_ACCT_CHRT_V")
public class ElAcctChrtVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5108123597916335420L;

	@Id
	@Column(name="ACCT_CHRT")
	private String acctChrt;

	public String getAcctChrt() {
		return acctChrt;
	}

	public void setAcctChrt(String acctChrt) {
		this.acctChrt = acctChrt;
	}
}
