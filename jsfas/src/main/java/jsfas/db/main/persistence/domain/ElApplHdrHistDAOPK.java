package jsfas.db.main.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class ElApplHdrHistDAOPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4679852593953367460L;
	

	@Column(name = "ID")
	private String id;

	@Column(name = "VERSION_NO")
	private Integer versionNo;

	public ElApplHdrHistDAOPK() {
	}
	
	public ElApplHdrHistDAOPK(String id, int versionNo) {
		super();
		this.id = id;
		this.versionNo = versionNo;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	private String getCompositeKey() {
	    return this.getId() + this.getVersionNo();
	}

	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof ElApplHdrHistDAOPK) {
	    	ElApplHdrHistDAOPK elApplHdrHistDAOPK = (ElApplHdrHistDAOPK)obj;
	        return this.getCompositeKey().equals(elApplHdrHistDAOPK.getCompositeKey());
	    }
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return this.getCompositeKey().hashCode();
	}
	
}
