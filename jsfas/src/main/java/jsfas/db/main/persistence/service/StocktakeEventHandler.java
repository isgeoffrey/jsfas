package jsfas.db.main.persistence.service;

import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.Map;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAO;
import jsfas.db.main.persistence.domain.FasStkPlanHdrDAO;
import jsfas.db.main.persistence.repository.FasStkPlanDtlRepository;
import jsfas.db.main.persistence.repository.FasStkPlanDtlStgRepository;
import jsfas.db.main.persistence.repository.FasStkPlanHdrRepostiory;
import jsfas.common.constants.StkPlanStatus;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.RecordModifiedException;
import jsfas.common.json.CommonJson;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class StocktakeEventHandler implements StocktakeService{

	@Autowired
	FasStkPlanHdrRepostiory stkPlanHdrRepository;

	@Autowired
	FasStkPlanDtlRepository stkPlanDtlRepository;

	@Autowired
	FasStkPlanDtlStgRepository stkPlanDtlStgRepository;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public JSONArray getAllStocktakePlans(JSONObject inputJson) throws Exception {
		JSONArray outJsonArray = new JSONArray(); 
		// TO DO change hardcore user to SecurityUtil id
		String currentUser = "isod01";

		String deptID = GeneralUtil.refineParam(inputJson.optString("deptId"));
		String stkPlanID = GeneralUtil.refineParam(inputJson.optString("stkPlanId"));
		String status = GeneralUtil.refineParam(inputJson.optString("stkPlanStatus"));
		
		Timestamp creatTo;
		Timestamp creatFrom; 

		if (inputJson.optLong("creatTo")>0){
			creatTo = new Timestamp(inputJson.optLong("creatTo"));
		}else{
			creatTo = GeneralUtil.NULLTIMESTAMP;
		}

		if (inputJson.optLong("creatFrom")>0){
			creatFrom = new Timestamp(inputJson.optLong("creatFrom"));
		}else{
			creatFrom = GeneralUtil.INFINTYTIMESTAMP;
		}
		
		log.info("getAllStocktakePlans currentUser: {}, stkPlanID: {}, deptID: {},  status: {}, creatTo: {}, creatFrom: {}",currentUser,stkPlanID,deptID,status,creatTo,creatFrom);

		List<FasStkPlanHdrDAO> hdrList = stkPlanHdrRepository.findStocktakeHdrByUserId(currentUser,stkPlanID,deptID,status,creatTo,creatFrom);
		for (FasStkPlanHdrDAO hdr: hdrList){
			outJsonArray.put(
				new JSONObject()
				.put("stkPlanId",hdr.getStkPlanId())
				.put("stkPlanSnapDat",hdr.getStkPlanSnapDat())
				.put("stkPlanStatus",hdr.getStkPlanStatus())
				.put("deptId",hdr.getCustDeptid())
				.put("deptDescrShort",hdr.getCustDeptDescrShort())
				.put("lockStatus",hdr.getLockStatus())
				.put("lockUser",GeneralUtil.initNullString(hdr.getLockUser()))
				.put("lockDat",hdr.getLockDat())
				.put("creatDat",hdr.getCreatDat())
				.put("modCtrlTxt", hdr.getModCtrlTxt())
			);
		}

		return outJsonArray;
	}

	@Override
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject deleteStocktakePlan(JSONObject inputJson) throws Exception {

		JSONObject outputJSON = new JSONObject();
		String currentUser = "isod01";
		//to do remove hard code for currentUser, add validation to check if user can delete

		String planID = inputJson.optString("id");
		String modCtrlTxt = inputJson.optString("modCtrlTxt");

		FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOneForUpdate(planID);

		if (stkPlan == null){
			throw new InvalidParameterException("Stocktake plan not found");
		}

		if(!modCtrlTxt.equalsIgnoreCase(stkPlan.getModCtrlTxt())){
			throw new RecordModifiedException("Stocktake Plan modified by another user, please reload page");
		}

		List<FasStkPlanDtlStgDAO> stkItems = stkPlanDtlStgRepository.findAllDtlFromPlanId(planID);
		stkPlanDtlStgRepository.deleteInBatch(stkItems);

		stkPlan.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
		stkPlan.setChngDat(GeneralUtil.getCurrentTimestamp());
		stkPlan.setChngUser(currentUser);
		stkPlan.setStkPlanStatus(StkPlanStatus.DELETED);

		outputJSON
			.put("stkPlanId",stkPlan.getStkPlanId())
			.put("stkPlanSnapDat",stkPlan.getStkPlanSnapDat())
			.put("stkPlanStatus",stkPlan.getStkPlanStatus())
			.put("deptId",stkPlan.getCustDeptid())
			.put("deptDescrShort",stkPlan.getCustDeptDescrShort())
			.put("lockStatus",stkPlan.getLockStatus())
			.put("lockUser",GeneralUtil.initNullString(stkPlan.getLockUser()))
			.put("lockDat",stkPlan.getLockDat())
			.put("creatDat",stkPlan.getCreatDat())
			.put("modCtrlTxt",stkPlan.getModCtrlTxt());

		log.info("deleteStocktakePlan Success: "+stkPlan.getStkPlanId());
		return outputJSON;
	}

	@Override
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject lockStocktakePlan(JSONObject inputJson) throws Exception {
		JSONObject outputJSON = new JSONObject();
		String currentUser = "isod01";
		//to do remove hard code for currentUser, add validation to check if user can lock

		String planID = inputJson.optString("id");
		String modCtrlTxt = inputJson.optString("modCtrlTxt");

		FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOneForUpdate(planID);

		if (stkPlan == null){
			throw new InvalidParameterException("Stocktake plan not found");
		}

		if(!modCtrlTxt.equalsIgnoreCase(stkPlan.getModCtrlTxt())){
			throw new RecordModifiedException("Stocktake Plan modified by another user, please reload page");
		}

		if (stkPlan.getLockStatus().equalsIgnoreCase("N")){
			stkPlan.setLockStatus("Y");
			stkPlan.setLockUser(currentUser);
			stkPlan.setLockDat(GeneralUtil.getCurrentTimestamp());
			
			stkPlan.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			stkPlan.setChngDat(GeneralUtil.getCurrentTimestamp());
			stkPlan.setChngUser(currentUser);
		}else {

			if (!stkPlan.getLockUser().equalsIgnoreCase(currentUser)){
				throw new Exception("Plan may only unlocked by " + GeneralUtil.initBlankString(stkPlan.getLockUser()));
			}

			stkPlan.setLockStatus("N");
			stkPlan.setLockUser(" ");
			stkPlan.setLockDat(GeneralUtil.NULLTIMESTAMP);

			stkPlan.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			stkPlan.setChngDat(GeneralUtil.getCurrentTimestamp());
			stkPlan.setChngUser(currentUser);
		}

		stkPlanHdrRepository.save(stkPlan);

		// instead of replacing entire table, only push updated object to selectively rerender
		outputJSON
			.put("stkPlanId",stkPlan.getStkPlanId())
			.put("stkPlanSnapDat",stkPlan.getStkPlanSnapDat())
			.put("stkPlanStatus",stkPlan.getStkPlanStatus())
			.put("deptId",stkPlan.getCustDeptid())
			.put("deptDescrShort",stkPlan.getCustDeptDescrShort())
			.put("lockStatus",stkPlan.getLockStatus())
			.put("lockUser",GeneralUtil.initNullString(stkPlan.getLockUser()))
			.put("lockDat",stkPlan.getLockDat())
			.put("creatDat",stkPlan.getCreatDat())
			.put("modCtrlTxt",stkPlan.getModCtrlTxt());

		log.info("lockStocktakePlan Success: "+stkPlan.getStkPlanId());
		return outputJSON;
	}

	@Override
	public JSONObject getStocktakeById(JSONObject inputJson) throws Exception {
		JSONObject outputJson = new JSONObject();
		// JSONArray outJsonArray = new JSONArray(); 
		String planID = inputJson.optString("stkPlanId");

		List<FasStkPlanDtlDAO> stkList = stkPlanDtlRepository.findAllDtlFromPlanId(planID);
		JSONArray stkListClean = new JSONArray();

		for (FasStkPlanDtlDAO stk:stkList){
			stkListClean.put(
				new JSONObject()
				.put("assetDescrLong", stk.getAssetDescrLong())
				.put("donationFlag", stk.getDonationFlag())
				.put("invoiceDt", stk.getInvoiceDt())
				.put("invoiceId", stk.getInvoiceId())
				.put("location", stk.getLocation())
				.put("modCtrlTxt", stk.getModCtrlTxt())
				.put("nbv", stk.getNbv())
				.put("notUstProprty", stk.getNotUstProprty())
				.put("poId", stk.getPoId())
				.put("profileDescr", stk.getProfileDescr())
				.put("profileId", stk.getProfileId())
				.put("regionName", stk.getRegionName())
				.put("stkStatus", stk.getStkStatus())
				.put("totalCost", stk.getTotalCost())
				.put("voucherId", stk.getVoucherId())
				.put("stkPlanId",stk.getFasStkPlanDtlDAOPK().getStkPlanId())
				.put("businessUnit",stk.getFasStkPlanDtlDAOPK().getBusinessUnit())
				.put("assetId",stk.getFasStkPlanDtlDAOPK().getAssetId())
			);
		}

		FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOne(planID);

		outputJson
		.put("stkItems", stkListClean)
		.put("stkPlan", stkPlan);

		return outputJson;
	}

	@Override
	public JSONObject getStocktakeHdrById(JSONObject inputJson)throws Exception {
		JSONObject outputJson = new JSONObject(); 
		// TO DO change hardcore user to SecurityUtil id
		String currentUser = "isod01";	
		String planId = inputJson.optString("stkPlanId");
		
		FasStkPlanHdrDAO stkHdr = stkPlanHdrRepository.findOne(planId);
		if (stkHdr == null){
			throw new InvalidParameterException("Stocktake Plan not found");
		}

		outputJson.put("stkPlanId",stkHdr.getStkPlanId())
			.put("stkPlanSnapDat",stkHdr.getStkPlanSnapDat())
			.put("stkPlanStatus",stkHdr.getStkPlanStatus())
			.put("deptId",stkHdr.getCustDeptid())
			.put("deptDescrShort",stkHdr.getCustDeptDescrShort())
			.put("lockStatus",stkHdr.getLockStatus())
			.put("lockUser",GeneralUtil.initNullString(stkHdr.getLockUser()))
			.put("lockDat",stkHdr.getLockDat())
			.put("creatDat",stkHdr.getCreatDat())
			.put("modCtrlTxt", stkHdr.getModCtrlTxt());

		return outputJson;
	}

	@Override
	public JSONObject getSummaryOfStocktakeById(JSONObject inputJson) throws Exception {
		JSONObject outputJSON = new JSONObject();
		String planID = inputJson.optString("stkPlanId");

		// List<Map<String,Object>> summary = stkPlanDtlRepository.findSumamryByPlanId(planID);
		// for (int i=0;i<1;i++){
		// 	outputJSON
		// 		.put("total", summary.get(i).get("total"))
		// 		.put("pending", summary.get(i).get("pending"))
		// 		.put("updated", summary.get(i).get("updated"));
		// }
		Map<String,Object> summary = stkPlanDtlRepository.findSumamryByPlanId(planID);
		outputJSON
		.put("total", summary.get("total"))
		.put("pending", summary.get("pending"))
		.put("updated", summary.get("updated"));

		return outputJSON;
	}

	@Override
	public JSONObject updateStocktakeByRow(JSONObject inputJson) throws Exception {
		JSONObject outputJSON = new JSONObject();
		String currentUser = "isod01";
		//to do remove hard code for currentUser, add validation to check if user can lock

		String planID = inputJson.optString("stkPlanId");
		String buUnit = inputJson.optString("businessUnit");
		String assetId = inputJson.optString("assetId");
		String modCtrlTxt = inputJson.optString("modCtrlTxt");
		String stkStatus = inputJson.optString("stkStatus");
		String opPageName = inputJson.optString("opPageName");

		FasStkPlanDtlDAOPK stkItemDAOPK = new FasStkPlanDtlDAOPK();
		stkItemDAOPK.setAssetId(assetId);
		stkItemDAOPK.setBusinessUnit(buUnit);
		stkItemDAOPK.setStkPlanId(planID);

		// FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOneForUpdate(planID);
		FasStkPlanDtlDAO stkItem = stkPlanDtlRepository.findOneForUpdate(stkItemDAOPK);

		if (stkItem == null){
			throw new InvalidParameterException("Asset Item not found");
		}

		if(!modCtrlTxt.equalsIgnoreCase(stkItem.getModCtrlTxt())){
			throw new RecordModifiedException("Stocktake Item modified by another user, please reload page");
		}

		stkItem.setStkStatus(stkStatus);
		stkItem.setChangeDate(GeneralUtil.getCurrentTimestamp());
		stkItem.setChangeUser(currentUser);
		stkItem.setModCtrlTxt(GeneralUtil.genModCtrlTxt());

		stkPlanDtlRepository.save(stkItem);

		// instead of replacing entire table, only push updated object to selectively rerender
		outputJSON
		.put("assetDescrLong", stkItem.getAssetDescrLong())
		.put("donationFlag", stkItem.getDonationFlag())
		.put("invoiceDt", stkItem.getInvoiceDt())
		.put("invoiceId", stkItem.getInvoiceId())
		.put("location", stkItem.getLocation())
		.put("modCtrlTxt", stkItem.getModCtrlTxt())
		.put("nbv", stkItem.getNbv())
		.put("notUstProprty", stkItem.getNotUstProprty())
		.put("poId", stkItem.getPoId())
		.put("profileDescr", stkItem.getProfileDescr())
		.put("profileId", stkItem.getProfileId())
		.put("regionName", stkItem.getRegionName())
		.put("stkStatus", stkItem.getStkStatus())
		.put("totalCost", stkItem.getTotalCost())
		.put("voucherId", stkItem.getVoucherId())
		.put("stkPlanId",stkItem.getFasStkPlanDtlDAOPK().getStkPlanId())
		.put("businessUnit",stkItem.getFasStkPlanDtlDAOPK().getBusinessUnit())
		.put("assetId",stkItem.getFasStkPlanDtlDAOPK().getAssetId());

		log.info("updateStocktakeByRow Success {} {} {}: ",stkItem.getFasStkPlanDtlDAOPK().getStkPlanId(),stkItem.getFasStkPlanDtlDAOPK().getBusinessUnit(),stkItem.getFasStkPlanDtlDAOPK().getAssetId());
		
		// Change stkPlanHdr Status on change
		Map<String,Object> latestCount = stkPlanDtlRepository.findSumamryByPlanId(planID);
		
		FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOne(planID);
		if (stkPlan == null){
			throw new Exception("Stocktake Plan cannot be found");
		}

		stkPlan.setChngDat(GeneralUtil.getCurrentTimestamp());
		stkPlan.setChngUser(currentUser);
		stkPlan.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
		stkPlan.setOpPageNam(opPageName);

		if (latestCount.get("pending") != null && GeneralUtil.initNullBigDecimal((BigDecimal)latestCount.get("pending")) == GeneralUtil.NULLBIGDECIMAL){
			stkPlan.setStkPlanStatus(StkPlanStatus.READYFORSUBM);
		}else if (latestCount.get("pending") != null && GeneralUtil.initNullBigDecimal((BigDecimal)latestCount.get("pending")) != GeneralUtil.NULLBIGDECIMAL){
			stkPlan.setStkPlanStatus(StkPlanStatus.OPEN);
		}

		return outputJSON;
	}

	@Override
	public JSONObject clearStocktakeByRow(JSONObject inputJson) throws Exception {
		JSONObject outputJSON = new JSONObject();
		String currentUser = "isod01";
		//to do remove hard code for currentUser, add validation to check if user can lock

		String planID = inputJson.optString("stkPlanId");
		String buUnit = inputJson.optString("businessUnit");
		String assetId = inputJson.optString("assetId");
		String modCtrlTxt = inputJson.optString("modCtrlTxt");
		String opPageName = inputJson.optString("opPageName");

		FasStkPlanDtlDAOPK stkItemDAOPK = new FasStkPlanDtlDAOPK();
		stkItemDAOPK.setAssetId(assetId);
		stkItemDAOPK.setBusinessUnit(buUnit);
		stkItemDAOPK.setStkPlanId(planID);

		// FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOneForUpdate(planID);
		FasStkPlanDtlDAO stkItem = stkPlanDtlRepository.findOneForUpdate(stkItemDAOPK);

		if (stkItem == null){
			throw new InvalidParameterException("Asset Item not found");
		}

		if(!modCtrlTxt.equalsIgnoreCase(stkItem.getModCtrlTxt())){
			throw new RecordModifiedException("Stocktake Item modified by another user, please reload page");
		}

		stkItem.setStkStatus(" ");
		stkItem.setChangeDate(GeneralUtil.getCurrentTimestamp());
		stkItem.setChangeUser(currentUser);
		stkItem.setModCtrlTxt(GeneralUtil.genModCtrlTxt());

		stkPlanDtlRepository.save(stkItem);

		// instead of replacing entire table, only push updated object to selectively rerender
		outputJSON
		.put("assetDescrLong", stkItem.getAssetDescrLong())
		.put("donationFlag", stkItem.getDonationFlag())
		.put("invoiceDt", stkItem.getInvoiceDt())
		.put("invoiceId", stkItem.getInvoiceId())
		.put("location", stkItem.getLocation())
		.put("modCtrlTxt", stkItem.getModCtrlTxt())
		.put("nbv", stkItem.getNbv())
		.put("notUstProprty", stkItem.getNotUstProprty())
		.put("poId", stkItem.getPoId())
		.put("profileDescr", stkItem.getProfileDescr())
		.put("profileId", stkItem.getProfileId())
		.put("regionName", stkItem.getRegionName())
		.put("stkStatus", stkItem.getStkStatus())
		.put("totalCost", stkItem.getTotalCost())
		.put("voucherId", stkItem.getVoucherId())
		.put("stkPlanId",stkItem.getFasStkPlanDtlDAOPK().getStkPlanId())
		.put("businessUnit",stkItem.getFasStkPlanDtlDAOPK().getBusinessUnit())
		.put("assetId",stkItem.getFasStkPlanDtlDAOPK().getAssetId());

		log.info("clearStocktakeByRow Success {} {} {}: ",stkItem.getFasStkPlanDtlDAOPK().getStkPlanId(),stkItem.getFasStkPlanDtlDAOPK().getBusinessUnit(),stkItem.getFasStkPlanDtlDAOPK().getAssetId());
		
		// Change stkPlanHdr Status on change
		Map<String,Object> latestCount = stkPlanDtlRepository.findSumamryByPlanId(planID);
		
		FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOne(planID);
		if (stkPlan == null){
			throw new Exception("Stocktake Plan cannot be found");
		}

		stkPlan.setChngDat(GeneralUtil.getCurrentTimestamp());
		stkPlan.setChngUser(currentUser);
		stkPlan.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
		stkPlan.setOpPageNam(opPageName);

		if (latestCount.get("pending") != null && GeneralUtil.initNullBigDecimal((BigDecimal)latestCount.get("pending")) == GeneralUtil.NULLBIGDECIMAL){
			stkPlan.setStkPlanStatus(StkPlanStatus.READYFORSUBM);
		}else if (latestCount.get("pending") != null && GeneralUtil.initNullBigDecimal((BigDecimal)latestCount.get("pending")) != GeneralUtil.NULLBIGDECIMAL){
			stkPlan.setStkPlanStatus(StkPlanStatus.OPEN);
		}
		
		return outputJSON;
	}

}
