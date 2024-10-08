package jsfas.db.main.persistence.service;

import org.springframework.transaction.annotation.Transactional;

import com.mchange.rmi.NotAuthorizedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.sql.Timestamp;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import freemarker.core.Environment;
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
import jsfas.common.exception.ErrorDataArrayException;
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
				throw new NotAuthorizedException("Plan may only unlocked by " + GeneralUtil.initBlankString(stkPlan.getLockUser()));
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
		return outputJSON;
	}
	

	@Override
	public JSONObject HandleStockPlanExcelUpload(CommonJson inputJson, String opPageName) throws Exception {
		
		if (inputJson.get("STK_PLAN_ID")==null) {
			throw new InvalidParameterException("STK PLAN ID do not exist");
		}
		if (inputJson.get("FileName")==null) {
			throw new InvalidParameterException("FileName do not exist");
		}
		
		List<List<Object>> uploadFileData= null;
		// 1. get data from execl and put it in the jsonarray
		try {
			uploadFileData = getDataFromUploadFile(inputJson.get("FileName"));
		}catch (Exception e) {
			throw new ErrorDataArrayException(e.getMessage()); 
		}

		// 2. foreach row of data , do data validation
		List<CommonJson> excelErrorList = new ArrayList<>();
		validateUploadData(uploadFileData, excelErrorList);
		if (!excelErrorList.isEmpty()) {
			ErrorDataArrayException ErrorData = new ErrorDataArrayException();
			ErrorData.setErrorList(excelErrorList);
			throw ErrorData;
		}
		
		// 3. return process result 
	

		JSONObject outputJSON = new JSONObject();
		insertUploadedData (uploadFileData, opPageName);
		outputJSON.put("data", uploadFileData);
		

		
		return outputJSON;
		
	}
	
	
	private List<List<Object>> getDataFromUploadFile(String FileName) throws JSONException{
			
		List<List<Object>> reponseList = new ArrayList<List<Object>>();
		
		List<Object> header = new ArrayList<>();
        header.add("Exist");
        header.add("Not Exist");
        header.add("Yet-to-be Located");
        header.add("Custodian Department Code");
        header.add("Custodian Department Description");
        header.add("Business Unit");
        header.add("Asset Profile ID");
        header.add("Asset Profile Description");
        header.add("Asset ID");
        header.add("Detailed Item Description");
        header.add("Total Cost");
        header.add("Net Book Value");
        header.add("Invoice Date");
        header.add("PO / BR No.");
        header.add("Region");
        header.add("Not UST Property");
        header.add("Donated Item");
        header.add("Location");
        header.add("Voucher ID");
        header.add("Invoice ID");
        
        List<Object> headerError = new ArrayList<>(); // header has wrong format
        headerError.add("Exist");
        headerError.add("Not Exist");
        headerError.add("Custodian Department Description");
        headerError.add("Yet-to-be Located");
        headerError.add("Custodian Department Code");
        headerError.add("Business Unit");
        headerError.add("Asset Profile ID");
        headerError.add("Asset Profile Description");
        headerError.add("Asset ID");
        headerError.add("Detailed Item Description");
        headerError.add("Total Cost");
        headerError.add("Net Book Value");
        headerError.add("Invoice Date");
        headerError.add("PO / BR No.");
        headerError.add("Region");
        headerError.add("Not UST Property");
        headerError.add("Donated Item");
        headerError.add("Location");
        headerError.add("Voucher ID");
        headerError.add("Invoice ID");
        
        
        List<Object> data1 = new ArrayList<>(); // correct data
        data1.add("Y");
        data1.add("");
        data1.add("Y");
        data1.add("abc");
        data1.add("abc");
        data1.add("abc");
        data1.add("asset prof 1");
        data1.add("asset descr 1");
        data1.add("asset 1");
        data1.add("lorem ispum");
        data1.add(123);
        data1.add(456);
        data1.add("2024-10-17");
        data1.add("PO 1");
        data1.add("Hong Kong");
        data1.add("N");
        data1.add("N");
        data1.add("LG7");
        data1.add("Voucher 1");
        data1.add("invoice 1");
        
        
        List<Object> data2 = new ArrayList<>(); // More than one y and asset id is null
        data2.add("Y");
        data2.add("");
        data2.add("abd");
        data2.add("abc");
        data2.add("abc");
        data2.add("abc");
        data2.add("asset prof 1");
        data2.add("asset descr 1");
        data2.add("asset 3");
        data2.add("lorem ispum");
        data2.add(123);
        data2.add(456);
        data2.add("2024-10-17");
        data2.add("PO 1");
        data2.add("Hong Kong");
        data2.add("N");
        data2.add("N");
        data2.add("LG7");
        data2.add("Voucher 1");
        data2.add("invoice 1");
        
        List<Object> data3 = new ArrayList<>(); // business unit is empty
        data3.add("");
        data3.add("");
        data3.add("Y");
        data3.add("abc");
        data3.add("abc");
        data3.add("");
        data3.add("asset prof 1");
        data3.add("asset descr 1");
        data3.add(null);
        data3.add("lorem ispum");
        data3.add(123);
        data3.add(456);
        data3.add("2024-10-17");
        data3.add("PO 1");
        data3.add("Hong Kong");
        data3.add("N");
        data3.add("N");
        data3.add("LG7");
        data3.add("Voucher 1");
        data3.add("invoice 1");
        
		
        reponseList.add(header);
        reponseList.add(data1);
        reponseList.add(data2);
        reponseList.add(data3);
		
		
		return reponseList;
		
	}
	
	private void validateUploadData(List<List<Object>> uploadFileData, List<CommonJson> excelErrorList) throws ErrorDataArrayException {
		
		if (excelErrorList == null) {
			excelErrorList = new ArrayList<>();
		}
		List<Object> headerList = uploadFileData.get(0);
		if (headerList.get(0)=="Exist" && headerList.get(1)== "Not Exist" &&
			headerList.get(2)== "Yet-to-be Located"&& headerList.get(5)== "Business Unit"&& headerList.get(8)== "Asset ID") {
			
			for (int i = 1; i < uploadFileData.size();i++) {
				try {
				List<Object> datarow = uploadFileData.get(i);
				Object Exist =  datarow.get(0);
				Object Yet_to_be_Located =  datarow.get(2);
				Object Not_Exist =  datarow.get(1);
				Object Business_Unit = datarow.get(5);
				Object Asset_ID = datarow.get(8);
				
				int Y = 0;
				if (!(Exist.equals("Y")||Exist.equals("")||Exist.equals("N")) ){
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("cell_name", "Exist");
					errormsg.set("errorMsg", "this field is not Y or N or empty");
					excelErrorList.add(errormsg);
				}else if(Exist.equals("Y")) {
					Y++;
				}
				if (!(Yet_to_be_Located.equals("Y")||Yet_to_be_Located.equals("")||Yet_to_be_Located.equals("N")) ){
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("cell_name", "Yet_to_be_Located");
					errormsg.set("errorMsg", "this field is not Y or N or empty");
					excelErrorList.add(errormsg);
				} else if(Yet_to_be_Located.equals("Y")) {
					Y++;
				}
				if (!(Not_Exist.equals("Y")||Not_Exist.equals("")||Not_Exist.equals("N")) ){
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("cell_name", "Not_Exist");
					errormsg.set("errorMsg", "this field is not Y or N or empty");
					excelErrorList.add(errormsg);
				}else if(Not_Exist.equals("Y")) {
					Y++;
				}
				if (Y>1) {
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("errorMsg", "this row have more than one 'Y' ");
					excelErrorList.add(errormsg);
				}
				if (Business_Unit == null|| Business_Unit.toString().isBlank()) {
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("cell_name", "Business Unit");
					errormsg.set("errorMsg", "Business Unit is blank or not exist ");
					excelErrorList.add(errormsg);
				}
				if (Asset_ID == null || Asset_ID.toString().isBlank()) {
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("cell_name", "Asset ID");
					errormsg.set("errorMsg", "Asset ID is blank or not exist ");
					excelErrorList.add(errormsg);
				}
				
				
				}catch(Exception e) {
					log.info(e.getMessage());
				}
			}
		}else {
			throw new ErrorDataArrayException("The header format incorrect!");
		}
	}
	
	
	private void insertUploadedData (List<List<Object>> uploadFileData, String opPageName) throws JSONException, ErrorDataArrayException {
		String currentUser = "isod01";
		List<FasStkPlanDtlStgDAO> saveDaoList = new ArrayList<>();
		try {
			for (int i = 1; i < uploadFileData.size();i++) {
				String modCtrlTxt = GeneralUtil.genModCtrlTxt();
				Timestamp currentTimestamp = GeneralUtil.getCurrentTimestamp();

				
				
				FasStkPlanDtlStgDAO FfasStkPlanDtlStgDAO = new FasStkPlanDtlStgDAO();
				FasStkPlanDtlStgDAOPK fasStkPlanDtlStgDAOPK = new FasStkPlanDtlStgDAOPK();
	
				List<Object> datarow = uploadFileData.get(i);
			//	fasStkPlanDtlStgDAOPK.setAssetId(datarow.getString("ASSET_ID"));
			//	fasStkPlanDtlStgDAOPK.setAssetId(datarow.getString("Business_Unit"));
			//	fasStkPlanDtlStgDAOPK.setAssetId(datarow.getString("STK_PLAN_ID"));
				
			//	FfasStkPlanDtlStgDAO.setFasStkPlanDtlStgDAOPK(fasStkPlanDtlStgDAOPK);
			//	FfasStkPlanDtlStgDAO.setPoId(datarow.getString("poId"));
			//	FfasStkPlanDtlStgDAO.setAssetDescrLong(null);
//				FfasStkPlanDtlStgDAO.setDonationFlag(null);
//				FfasStkPlanDtlStgDAO.setInvoiceDt(null);
//				FfasStkPlanDtlStgDAO.setInvoiceId(null);
//				FfasStkPlanDtlStgDAO.setLocation(null);
//				FfasStkPlanDtlStgDAO.setNbv(null);
//				FfasStkPlanDtlStgDAO.setNotUstProprty(null);
//				FfasStkPlanDtlStgDAO.setProfileDescr(null);
//				FfasStkPlanDtlStgDAO.setProfileId(null);
//				FfasStkPlanDtlStgDAO.setRegionName(null);
//				FfasStkPlanDtlStgDAO.setTotalCost(null);
//				FfasStkPlanDtlStgDAO.setVoucherId(null);
				
				FfasStkPlanDtlStgDAO.setModCtrlTxt(modCtrlTxt);
				FfasStkPlanDtlStgDAO.setCreateDate(currentTimestamp);
				FfasStkPlanDtlStgDAO.setCreateUser(currentUser);
				FfasStkPlanDtlStgDAO.setChangeDate(currentTimestamp);
				FfasStkPlanDtlStgDAO.setChangeUser(currentUser);
				FfasStkPlanDtlStgDAO.setOpPageName(opPageName);
				
				
				
				
				Object Exist =  datarow.get(0);
				Object Yet_to_be_Located =  datarow.get(2);
				Object Not_Exist =  datarow.get(1);
				
				if (Exist.equals("Y")) {
					FfasStkPlanDtlStgDAO.setStkStatus("E");
				}else 
				if (Yet_to_be_Located.equals("Y")) {
					FfasStkPlanDtlStgDAO.setStkStatus("Y");
				}else 
				if (Not_Exist.equals("Y")) {
					FfasStkPlanDtlStgDAO.setStkStatus("N");
				}else {
					FfasStkPlanDtlStgDAO.setStkStatus("");
				}
				
				saveDaoList.add(FfasStkPlanDtlStgDAO);
			}
//			throw new Exception();
			
//			stkPlanDtlStgRepository.saveAll(saveDaoList);
			
		}catch (Exception e) {
			throw new ErrorDataArrayException("Unable to update Staging data");
		}
	}
	
	

}
