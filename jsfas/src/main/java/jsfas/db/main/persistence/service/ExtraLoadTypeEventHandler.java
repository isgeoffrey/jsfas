package jsfas.db.main.persistence.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.RecordModifiedException;
import jsfas.common.exception.RecordNotExistException;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.ElTypeSalElemntTabDAO;
import jsfas.db.main.persistence.domain.ElTypeTabDAO;
import jsfas.db.main.persistence.repository.ElTypeSalElemntTabRepository;
import jsfas.db.main.persistence.repository.ElTypeTabRepository;
import jsfas.security.SecurityUtils;

public class ExtraLoadTypeEventHandler implements ExtraLoadTypeService {
	
	@Autowired
	ElTypeTabRepository elTypeTabRepository;
	
	@Autowired
	ElTypeSalElemntTabRepository elTypeSalElemntTabRepository;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject createExtraLoadType(JSONObject inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		
		JSONObject outputJson = new JSONObject();
		
		// Get input
		String name = GeneralUtil.initBlankString(inputJson.optString("el_type_nam").trim());
		String descr = GeneralUtil.initBlankString(inputJson.optString("el_type_descr").trim());
		String catCde = GeneralUtil.initBlankString(inputJson.optString("category_cde").trim());
		Timestamp effDt;
		
		// TBC
//		try {
//			effDt = new Timestamp(inputJson.getLong("effective_dt"));
//		} catch (Exception e) {
//			throw new InvalidDateException();
//		}
		
		// validation
		if (name.isBlank() || catCde.isBlank()) {
			throw new InvalidParameterException();
		}
		
		// start insert
		ElTypeTabDAO elTypeDAO = new ElTypeTabDAO();
		
		elTypeDAO.setElTypeNam(name);
		elTypeDAO.setCategoryCde(catCde);
		elTypeDAO.setElTypeDescr(descr);
//		elTypeDAO.setEffectiveDt(effDt);		

		elTypeDAO.setCreatUser(remoteUser);
		elTypeDAO.setChngUser(remoteUser);
		elTypeDAO.setOpPageNam(opPageName);
		
		elTypeTabRepository.save(elTypeDAO);
		
		String elTypeId = elTypeDAO.getId();
		
		// Insert salary element set up
		JSONArray salElemntJsonArr = inputJson.optJSONArray("sal_elemnt");
		
		if (salElemntJsonArr != null) {
			for (int i = 0; i < salElemntJsonArr.length(); i++) {
				JSONObject obj = salElemntJsonArr.getJSONObject(i);
				
				ElTypeSalElemntTabDAO salElemntDAO = new ElTypeSalElemntTabDAO();
				
				salElemntDAO.setElTypeId(elTypeId);
				salElemntDAO.setPymtTypeCde(obj.getString("pymt_type_cde"));
				salElemntDAO.setSalElemnt(obj.getString("sal_elemnt"));
				salElemntDAO.setSalElemntOw2(obj.getString("sal_elemnt_ow2"));
//				salElemntDAO.setEffectiveDt(effDt);
				
				salElemntDAO.setCreatUser(remoteUser);
				salElemntDAO.setChngUser(remoteUser);
				salElemntDAO.setOpPageNam(opPageName);
				
				elTypeSalElemntTabRepository.save(salElemntDAO);
			}			
		}
		
		outputJson.put("id", elTypeId);
		outputJson.put("el_type_nam", elTypeDAO.getElTypeNam());
		outputJson.put("el_type_descr", elTypeDAO.getElTypeDescr());
		outputJson.put("category_cde", elTypeDAO.getCategoryCde());
		outputJson.put("mod_ctrl_txt", elTypeDAO.getModCtrlTxt());

		return outputJson;
	}

	@Override
	public JSONArray getExtraLoadTypeList(JSONObject inputJson) throws Exception {
		JSONArray outputArr = new JSONArray();
		
		// TODO: Search params
		String obsolete = inputJson.optString("obsolete", "%");
		
		List<Map<String, Object>> resultMapList = elTypeTabRepository.searchAll(obsolete);
		
		for (Map<String, Object> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("el_type_id", resultMap.get("id"));
			jsonObj.put("el_type_nam", resultMap.get("el_type_nam"));
			jsonObj.put("el_type_descr", resultMap.get("el_type_descr"));
			jsonObj.put("category_cde", resultMap.get("category_cde"));
			jsonObj.put("obsolete", resultMap.get("obsolete"));
			jsonObj.put("mod_ctrl_txt", resultMap.get("mod_ctrl_txt"));
			jsonObj.put("has_active_sal_elemnt", resultMap.get("has_active_sal_elemnt").equals(BigDecimal.ONE));
			
			outputArr.put(jsonObj);
		}
		
		return outputArr;
	}

	@Override
	public JSONArray getExtraLoadTypeMappingList(JSONObject inputJson) throws Exception {
		JSONArray outputArr = new JSONArray();
		
		// TODO: Search params
		String elTypeId = inputJson.optString("el_type_id", "%");
		String categoryCde = inputJson.optString("category_cde", "%");
		String obsolete  = inputJson.optString("obsolete", "%");
		
		List<Map<String, String>> resultMapList = elTypeSalElemntTabRepository.searchAll(elTypeId, categoryCde, obsolete);
		
		for (Map<String, String> resultMap : resultMapList) {
			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("el_type_id", resultMap.get("el_type_id"));
			jsonObj.put("category_cde", resultMap.get("category_cde"));
			jsonObj.put("el_type_sal_elemnt_id", resultMap.get("id"));
			jsonObj.put("el_type_nam", resultMap.get("el_type_nam"));
			jsonObj.put("el_type_descr", resultMap.get("el_type_descr"));
			jsonObj.put("pymt_type_cde", resultMap.get("pymt_type_cde"));
			jsonObj.put("sal_elemnt", resultMap.get("sal_elemnt"));
			jsonObj.put("sal_elemnt_ow2", resultMap.get("sal_elemnt_ow2"));
			jsonObj.put("el_type_sal_elemnt_obsolete", resultMap.get("obsolete"));
			jsonObj.put("mod_ctrl_txt", resultMap.get("mod_ctrl_txt"));
			
			outputArr.put(jsonObj);
		}
		
		return outputArr;
	}

	@Override
	public JSONObject getExtraLoadTypeDetail(String elTypeId) throws Exception {
		JSONObject outputJson = new JSONObject();
		
		ElTypeTabDAO elTypeDAO = elTypeTabRepository.findOne(elTypeId);
		
		if (elTypeDAO == null) {
			throw new RecordNotExistException("Extra Load Type");
		}
		
		outputJson.put("el_type_id", elTypeDAO.getId());
		outputJson.put("el_type_nam", elTypeDAO.getElTypeNam());
		outputJson.put("el_type_descr", elTypeDAO.getElTypeDescr());
		outputJson.put("category_cde", elTypeDAO.getCategoryCde());
		outputJson.put("mod_ctrl_txt", elTypeDAO.getModCtrlTxt());
		
		List<ElTypeSalElemntTabDAO> salElemntDAOList = elTypeSalElemntTabRepository.findByElTypeIdAndObsolete(elTypeId, 0);
		
		JSONArray jsonArr = new JSONArray();
		
		for (ElTypeSalElemntTabDAO salElemntDAO : salElemntDAOList) {
			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("el_type_sal_elemnt_id", salElemntDAO.getId());
			jsonObj.put("pymt_type_cde", salElemntDAO.getPymtTypeCde());
			jsonObj.put("sal_elemnt", salElemntDAO.getSalElemnt());
			jsonObj.put("sal_elemnt_ow2", salElemntDAO.getSalElemntOw2());
			jsonObj.put("mod_ctrl_txt", salElemntDAO.getModCtrlTxt());
			
			jsonArr.put(jsonObj);
		}
		
		outputJson.put("sal_elemnt", jsonArr);
		
		return outputJson;
	}
	
	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject updateExtraLoadType(String elTypeId, JSONObject inputJson, String opPageName) throws Exception {
		String remoteUser = SecurityUtils.getCurrentLogin();
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		// validation
		ElTypeTabDAO elTypeDAO = elTypeTabRepository.findOneForUpdate(elTypeId);		
		if (elTypeDAO == null) {
			throw new RecordNotExistException("Extra Load Type");
		}
		
		if (!elTypeDAO.getModCtrlTxt().equals(inputJson.getString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		
		// Get input
		String name = GeneralUtil.initBlankString(inputJson.optString("el_type_nam").trim());
		String descr = GeneralUtil.initBlankString(inputJson.optString("el_type_descr").trim());
		String catCde = GeneralUtil.initBlankString(inputJson.optString("category_cde").trim());
		
		elTypeDAO.setElTypeNam(name);
		elTypeDAO.setCategoryCde(catCde);
		elTypeDAO.setElTypeDescr(descr);
		
		elTypeDAO.setChngDat(currTS);
		elTypeDAO.setChngUser(remoteUser);
		elTypeDAO.setModCtrlTxt(modCtrlTxt);
		elTypeDAO.setOpPageNam(opPageName);
		
		elTypeTabRepository.save(elTypeDAO);
		
		// Upsert salary elements mapping
		JSONArray salElemntJsonArr = inputJson.optJSONArray("sal_elemnt");
		
		List<ElTypeSalElemntTabDAO> upsertSalElemntDAOList = new ArrayList<>();
		
		if (salElemntJsonArr != null) {
			for (int i = 0; i < salElemntJsonArr.length(); i++) {
				JSONObject obj = salElemntJsonArr.getJSONObject(i);
				
				String salElemntId = obj.optString("el_type_sal_elemnt_id");
				ElTypeSalElemntTabDAO salElemntDAO = elTypeSalElemntTabRepository.findOneForUpdate(salElemntId);
				
				// insert case
				if (salElemntId.isBlank()) {
					
					salElemntDAO = new ElTypeSalElemntTabDAO();
					
					salElemntDAO.setCreatUser(remoteUser);
				}
				else if (!salElemntId.isBlank() && salElemntDAO == null) {
					throw new RecordNotExistException("Extra Load Type Mapping");
				}
				// Edit case
				else {
					// Validation
					if (!salElemntDAO.getModCtrlTxt().equals(obj.optString("mod_ctrl_txt"))) {
						throw new RecordModifiedException();
					}
				}
				
				salElemntDAO.setElTypeId(elTypeId);
				salElemntDAO.setPymtTypeCde(obj.getString("pymt_type_cde"));
				salElemntDAO.setSalElemnt(obj.getString("sal_elemnt"));
				salElemntDAO.setSalElemntOw2(obj.getString("sal_elemnt_ow2"));
//				salElemntDAO.setEffectiveDt(effDt);
				
				salElemntDAO.setChngDat(currTS);
				salElemntDAO.setChngUser(remoteUser);
				salElemntDAO.setOpPageNam(opPageName);
				salElemntDAO.setModCtrlTxt(modCtrlTxt);
				
				upsertSalElemntDAOList.add(salElemntDAO);
			}			
		}
		
		elTypeSalElemntTabRepository.saveAll(upsertSalElemntDAOList);
		
		// Obsolete active records that are removed
		List<ElTypeSalElemntTabDAO> obsoleteSalElemntDAOList = 
				elTypeSalElemntTabRepository.findForObsolete(elTypeId, upsertSalElemntDAOList.stream().map(dao -> dao.getId()).collect(Collectors.toList()));
		
		for(ElTypeSalElemntTabDAO obsoleteSalElemntDAO : obsoleteSalElemntDAOList) {
			obsoleteSalElemntDAO.setObsolete(1);
			
			obsoleteSalElemntDAO.setChngDat(currTS);
			obsoleteSalElemntDAO.setChngUser(remoteUser);
			obsoleteSalElemntDAO.setModCtrlTxt(modCtrlTxt);
			obsoleteSalElemntDAO.setOpPageNam(opPageName);
		}
		
		elTypeSalElemntTabRepository.saveAll(obsoleteSalElemntDAOList);
		
		return getExtraLoadTypeDetail(elTypeId);
	}

	@Override
	@Transactional(value = "transactionManagerJselMain", rollbackFor = Exception.class)
	public JSONObject deleteExtraLoadTypeMapping(String elTypeSalElemntId, JSONObject inputJson, String opPageName) throws Exception {
		JSONObject outputJson = new JSONObject();
		
		String remoteUser = SecurityUtils.getCurrentLogin();
		Timestamp currTS = GeneralUtil.getCurrentTimestamp();
		String modCtrlTxt = GeneralUtil.genModCtrlTxt();
		
		ElTypeSalElemntTabDAO salElemntDAO = elTypeSalElemntTabRepository.findOneForUpdate(elTypeSalElemntId);
		
		if (salElemntDAO == null) {
			throw new RecordNotExistException("Extra Load Type Mapping");
		}
		
		if (!salElemntDAO.getModCtrlTxt().equals(inputJson.optString("mod_ctrl_txt"))) {
			throw new RecordModifiedException();
		}
		
		salElemntDAO.setObsolete(1);
		
		salElemntDAO.setChngDat(currTS);
		salElemntDAO.setChngUser(remoteUser);
		salElemntDAO.setModCtrlTxt(modCtrlTxt);
		salElemntDAO.setOpPageNam(opPageName);
		
		elTypeSalElemntTabRepository.save(salElemntDAO);
		
		return outputJson.put("el_type_sal_elemnt_id", elTypeSalElemntId);
	}

}
