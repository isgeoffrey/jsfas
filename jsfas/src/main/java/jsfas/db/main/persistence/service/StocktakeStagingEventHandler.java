package jsfas.db.main.persistence.service;

import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import java.sql.Timestamp;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;

import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanHdrDAO;
import jsfas.db.main.persistence.repository.FasStkPlanDtlRepository;
import jsfas.db.main.persistence.repository.FasStkPlanDtlStgRepository;
import jsfas.db.main.persistence.repository.FasStkPlanHdrRepostiory;
import jsfas.common.constants.StkPlanStatus;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.exception.RecordModifiedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StocktakeStagingEventHandler implements StocktakeStagingService{
	@Autowired
	FasStkPlanHdrRepostiory stkPlanHdrRepository;

	@Autowired
	FasStkPlanDtlRepository stkPlanDtlRepository;

	@Autowired
	FasStkPlanDtlStgRepository stkPlanDtlStgRepository;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public JSONObject getStagingItemsById(JSONObject inputJson) throws Exception {
		JSONObject outputJson = new JSONObject();
		// JSONArray outJsonArray = new JSONArray(); 
		String planID = inputJson.optString("stkPlanId");

		List<Map<String,Object>> stkList = stkPlanDtlRepository.findDtlAndStagingOverlapFromPlanId(planID);
		JSONArray stkListClean = new JSONArray();

		for (Map<String,Object> stk:stkList){
			stkListClean.put(
				new JSONObject()
				.put("assetDescrLong", stk.get("asset_descr_long"))
				.put("donationFlag", stk.get("donation_flag"))
				.put("invoiceDt", stk.get("invoice_dt"))
				.put("invoiceId", stk.get("invoice_id"))
				.put("location", stk.get("location"))
				.put("modCtrlTxt", stk.get("mod_ctrl_txt"))
				.put("nbv", stk.get("nbv"))
				.put("notUstProprty", stk.get("not_ust_proprty"))
				.put("poId", stk.get("po_id"))
				.put("profileDescr", stk.get("profile_descr"))
				.put("profileId", stk.get("profile_id"))
				.put("regionName", stk.get("region_name"))
				.put("stkStatus", stk.get("stk_status"))
				.put("totalCost", stk.get("total_cost"))
				.put("voucherId", stk.get("voucher_id"))
				.put("stkPlanId", stk.get("stk_plan_id"))
				.put("businessUnit", stk.get("business_unit"))
				.put("assetId", stk.get("asset_id"))
				.put("stgStkStatus", stk.get("stg_stk_status"))
			);
		}

		FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOne(planID);

		outputJson
		.put("stkItems", stkListClean)
		.put("stkPlan", stkPlan);

		return outputJson;
	}

	@Override
	public JSONObject updateStagingByRow(JSONObject inputJson) throws Exception {
		JSONObject outputJSON = new JSONObject();
		String currentUser = "isod01";
		//to do remove hard code for currentUser, add validation to check if user can lock

		String planID = inputJson.optString("stkPlanId");
		String buUnit = inputJson.optString("businessUnit");
		String assetId = inputJson.optString("assetId");
		String modCtrlTxt = inputJson.optString("modCtrlTxt");
		String stgStkStatus = inputJson.optString("stgStkStatus");

		FasStkPlanDtlStgDAOPK stgStkItemDAOPK = new FasStkPlanDtlStgDAOPK();
		stgStkItemDAOPK.setAssetId(assetId);
		stgStkItemDAOPK.setBusinessUnit(buUnit);
		stgStkItemDAOPK.setStkPlanId(planID);

		// FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOneForUpdate(planID);
		FasStkPlanDtlStgDAO stgStkItem = stkPlanDtlStgRepository.findOneForUpdate(stgStkItemDAOPK);

		FasStkPlanDtlDAOPK stkItemDAOPK = new FasStkPlanDtlDAOPK();
		stkItemDAOPK.setAssetId(assetId);
		stkItemDAOPK.setBusinessUnit(buUnit);
		stkItemDAOPK.setStkPlanId(planID);
		FasStkPlanDtlDAO stkItem = stkPlanDtlRepository.findOne(stkItemDAOPK);

		if (stgStkItem == null){
			throw new InvalidParameterException("Staged Item not found");
		}

		if (stkItem == null){
			throw new InvalidParameterException("Asset Item not found");
		}

		if(!modCtrlTxt.equalsIgnoreCase(stgStkItem.getModCtrlTxt())){
			throw new RecordModifiedException("Staged Item modified by another user, please reload page");
		}

		stgStkItem.setStkStatus(stgStkStatus);
		stgStkItem.setChangeDate(GeneralUtil.getCurrentTimestamp());
		stgStkItem.setChangeUser(currentUser);
		stgStkItem.setModCtrlTxt(GeneralUtil.genModCtrlTxt());

		stkPlanDtlStgRepository.save(stgStkItem);

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
		.put("assetId",stkItem.getFasStkPlanDtlDAOPK().getAssetId())
		.put("stgStkStatus",stgStkItem.getStkStatus());

		log.info("updateStocktakeByRow Success {} {} {}: ",stkItem.getFasStkPlanDtlDAOPK().getStkPlanId(),stkItem.getFasStkPlanDtlDAOPK().getBusinessUnit(),stkItem.getFasStkPlanDtlDAOPK().getAssetId());
		
		return outputJSON;
	}

	@Override
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject clearStagingByRow(JSONObject inputJson) throws Exception {
		JSONObject outputJSON = new JSONObject();
		String currentUser = "isod01";
		//to do remove hard code for currentUser, add validation to check if user can lock

		String planID = inputJson.optString("stkPlanId");
		String buUnit = inputJson.optString("businessUnit");
		String assetId = inputJson.optString("assetId");
		String modCtrlTxt = inputJson.optString("modCtrlTxt");

		FasStkPlanDtlStgDAOPK stgStkItemDAOPK = new FasStkPlanDtlStgDAOPK();
		stgStkItemDAOPK.setAssetId(assetId);
		stgStkItemDAOPK.setBusinessUnit(buUnit);
		stgStkItemDAOPK.setStkPlanId(planID);

		// FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOneForUpdate(planID);
		FasStkPlanDtlStgDAO stgStkItem = stkPlanDtlStgRepository.findOneForUpdate(stgStkItemDAOPK);

		FasStkPlanDtlDAOPK stkItemDAOPK = new FasStkPlanDtlDAOPK();
		stkItemDAOPK.setAssetId(assetId);
		stkItemDAOPK.setBusinessUnit(buUnit);
		stkItemDAOPK.setStkPlanId(planID);

		// FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOneForUpdate(planID);
		FasStkPlanDtlDAO stkItem = stkPlanDtlRepository.findOneForUpdate(stkItemDAOPK);

		if (stgStkItem == null){
			throw new InvalidParameterException("Staged Item not found");
		}

		if (stkItem == null){
			throw new InvalidParameterException("Asset Item not found");
		}

		if(!modCtrlTxt.equalsIgnoreCase(stkItem.getModCtrlTxt())){
			throw new RecordModifiedException("Stocktake Item modified by another user, please reload page");
		}

		stgStkItem.setStkStatus(" ");
		stgStkItem.setChangeDate(GeneralUtil.getCurrentTimestamp());
		stgStkItem.setChangeUser(currentUser);
		stgStkItem.setModCtrlTxt(GeneralUtil.genModCtrlTxt());

		stkPlanDtlStgRepository.save(stgStkItem);

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
		.put("assetId",stkItem.getFasStkPlanDtlDAOPK().getAssetId())
		.put("stgStkStatus",stgStkItem.getStkStatus());

		log.info("clearStocktakeByRow Success {} {} {}: ",stkItem.getFasStkPlanDtlDAOPK().getStkPlanId(),stkItem.getFasStkPlanDtlDAOPK().getBusinessUnit(),stkItem.getFasStkPlanDtlDAOPK().getAssetId());
		
		return outputJSON;
	}

	@Override
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject deleteAllStagingById(JSONObject inputJson) throws Exception{
		JSONObject outputJSON = new JSONObject();
		
		String currentUser = "isod01";
		//to do remove hard code for currentUser, add validation to check if user can delete

		String planID = inputJson.optString("stkPlanId");
		String opPageName = inputJson.optString("opPageName");

		List<FasStkPlanDtlStgDAO> delItems = stkPlanDtlStgRepository.findAllDtlFromPlanId(planID);

		stkPlanDtlStgRepository.deleteAll(delItems);
		

		log.info("deleteAllStagingById Success: "+planID);

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

		stkPlanHdrRepository.save(stkPlan);
		return outputJSON;
	}

	@Override
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public String updateDtlWithStgById (JSONObject inputJson) throws Exception{

		String currentUser = "isod01";
		String planID = inputJson.optString("stkPlanId");
		String opPageName = inputJson.optString("opPageName");

		List<FasStkPlanDtlStgDAO> delItems = stkPlanDtlStgRepository.findAllDtlFromPlanId(planID);

		if (delItems.size()==0){
			throw new InvalidParameterException("No Stocktake items not found in staging");
		}

		List<FasStkPlanDtlDAO> coercedDtl = stkPlanDtlRepository.coerceStgToDtlData(planID);

		for (FasStkPlanDtlDAO dtl:coercedDtl){
			dtl.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			dtl.setChangeUser(currentUser);
			dtl.setChangeDate(GeneralUtil.getCurrentTimestamp());
			dtl.setOpPageName(opPageName);
		}
		

		stkPlanDtlRepository.saveAll(coercedDtl);
		stkPlanDtlStgRepository.deleteAll(delItems);

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

		stkPlanHdrRepository.save(stkPlan);
		return planID;
	}

}
