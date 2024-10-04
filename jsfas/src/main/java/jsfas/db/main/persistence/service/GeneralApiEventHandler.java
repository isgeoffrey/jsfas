package jsfas.db.main.persistence.service;


import java.util.List;
import javax.inject.Inject;
import org.json.JSONArray;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import jsfas.db.main.persistence.domain.FasDeptVDAO;
import jsfas.db.main.persistence.domain.FasStkPlanStatusDAO;
import jsfas.db.main.persistence.repository.FasDeptVRepository;
import jsfas.db.main.persistence.repository.FasStkPlanStatusRepository;
// import jsfas.security.SecurityUtils;

public class GeneralApiEventHandler implements GeneralApiService {

	@Inject
	Environment env;

	@Autowired
	FasStkPlanStatusRepository stkPlanStatusRepository;

	@Autowired
	FasDeptVRepository deptRepository;


	// Get Department List
	public JSONArray getDeptList() throws Exception{
		JSONArray outputJsonArray = new JSONArray();

		List<FasDeptVDAO> deptList = deptRepository.findAll();
		for (FasDeptVDAO dept : deptList){
			outputJsonArray.put(
				new JSONObject()
				.put("deptId", dept.getDeptID())
				.put("deptDescrShort",dept.getDeptDescrShort())
				.put("deptDescrLong", dept.getDeptDescrLong())
			);
		}

		return outputJsonArray;
	}
	
	//Get status for stocktake header
	public JSONArray getStocktakePlanStatus() throws Exception{
		JSONArray outputJsonArray = new JSONArray();

		List<FasStkPlanStatusDAO> stkPlanStatus = stkPlanStatusRepository.findAll();

		for (FasStkPlanStatusDAO status: stkPlanStatus){
			outputJsonArray.put(
				new JSONObject()
				.put("stkPlanStatus", status.getStkPlanstatus())
				.put("descr", status.getDescr())
			);
		}

		return outputJsonArray;
	}

}
