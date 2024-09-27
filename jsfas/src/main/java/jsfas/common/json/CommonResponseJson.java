package jsfas.common.json;

import java.util.List;

/**
 * Example ResponseJson extends ResponseJson.
 * remove in new project
 * @author iswill
 * @since 15/12/2016
 */
public class CommonResponseJson extends ResponseJson {		
	private static final long serialVersionUID = -6494219054160571947L;
	
	private CommonJson commonJson;
	private List<CommonJson> commonJsonList;
	
	public CommonJson getCommonJson() {
		return commonJson;
	}
	public void setCommonJson(CommonJson commonJson) {
		this.commonJson = commonJson;
	}
	public List<CommonJson> getCommonJsonList() {
		return commonJsonList;
	}
	public void setCommonJsonList(List<CommonJson> commonJsonList) {
		this.commonJsonList = commonJsonList;
	}
}
