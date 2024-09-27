package jsfas.db.main.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EL_FUND_CHRT_V")
public class ElFundChrtVDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7802252635414867272L;

	@Id
	@Column(name="FUND_CDE")
	private String fundCde;
	
	@Column(name="FUND_LONG_DESC")
	private String fundLongDesc;

	public String getFundCde() {
		return fundCde;
	}

	public void setFundCde(String fundCde) {
		this.fundCde = fundCde;
	}

	public String getFundLongDesc() {
		return fundLongDesc;
	}

	public void setFundLongDesc(String fundLongDesc) {
		this.fundLongDesc = fundLongDesc;
	}
}
