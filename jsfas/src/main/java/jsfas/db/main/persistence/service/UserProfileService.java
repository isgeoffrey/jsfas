package jsfas.db.main.persistence.service;

import java.util.List;

import jsfas.common.json.CommonJson;
import jsfas.common.json.ResponseJson;

/**
 * @author iswill
 * @since 12/6/2017
 */
public interface UserProfileService {
	public ResponseJson insertUserProfile(CommonJson inputJson, String opPageName) throws Exception;
	public ResponseJson updateUserProfile(CommonJson inputJson, String opPageName) throws Exception;
	public ResponseJson deleteUserProfile(CommonJson inputJson) throws Exception;
	
	public List<CommonJson> getUserProfileList(String userName);
	public List<CommonJson> getUserProfileView(String userName);
	public List<CommonJson> getUserProfileAddView();
	public CommonJson getUserProfile(String userName);
	public CommonJson getUserProfileWithDetails(String userName);
}
