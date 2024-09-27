package jsfas.db.jssv.persistence.service;

import jsfas.common.json.CommonJson;

public interface OAuth2RestService {
	public CommonJson getStaffInfo(String itscAc) throws Exception;
}
