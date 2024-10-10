package jsfas.db.main.persistence.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mchange.rmi.NotAuthorizedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;

import java.util.Map;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanHdrDAO;
import jsfas.db.main.persistence.repository.FasStkPlanDtlRepository;
import jsfas.db.main.persistence.repository.FasStkPlanDtlStgRepository;
import jsfas.db.main.persistence.repository.FasStkPlanHdrRepostiory;
import jsfas.common.excel.XLSReader.FileFormatInvalidException;
import jsfas.common.excel.XCol;
import jsfas.common.excel.XCDbl;
import jsfas.common.excel.XCStr;
import jsfas.common.excel.XLSReader;
import jsfas.common.constants.AppConstants;
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
	public JSONObject HandleStockPlanExcelUpload(MultipartFile uploadFile, String stkPlanId, String opPageName) throws Exception {
		
		if (GeneralUtil.initBlankString(stkPlanId).trim()==null) {
			throw new InvalidParameterException("STK PLAN ID do not exist");
		}
		if (uploadFile.isEmpty()) {
			throw new InvalidParameterException("uploadFile is not exist");
		}
		
		if (uploadFile.getSize()==0) {
			throw new InvalidParameterException("uploadFile is empty");
		}
		
		List<FasStkPlanDtlStgDAO> fasStkPlanDtlStgDAOList = new ArrayList<>();
		ArrayList<HashMap<String, Object>> uploadFileData= null;
		// 1. get data from execl and put it in the jsonarray
		try {
			uploadFileData = getDataFromUploadFile(uploadFile);
		}catch (Exception e) {
			throw new ErrorDataArrayException(e.getMessage()); 
		}

		// 2. foreach row of data , do data validation
		List<CommonJson> excelErrorList = new ArrayList<>();
		validateUploadData(uploadFileData, excelErrorList, fasStkPlanDtlStgDAOList, stkPlanId, opPageName);
		if (!excelErrorList.isEmpty()) {
			ErrorDataArrayException ErrorData = new ErrorDataArrayException();
			ErrorData.setErrorList(excelErrorList);
			throw ErrorData;
		}
			
		
		// 3. return process result 
	
		insertUploadedData (fasStkPlanDtlStgDAOList);
		JSONObject outputJSON = new JSONObject();
		outputJSON.put("data", fasStkPlanDtlStgDAOList);
		
		return outputJSON;
		
	}
	
	private ArrayList<HashMap<String, Object>> getDataFromUploadFile(MultipartFile uploadFile) throws IOException, InvalidParameterException{
			
		List<Map<String,Object>> reponseList = new ArrayList<Map<String,Object>>();
		String originalFileName = uploadFile.getOriginalFilename();
		
		// remove file extension
		// String uploadFileName = FilenameUtils.getBaseName(originalFileName);
		String time_str = GeneralUtil.genModCtrlTxt();
		
		String currentUser = "isod01";
	
		// copy upload file to temp dir
		File outputFileDir = new File ("C:\\tmp\\fas\\upload_excel\\temp" + File.separator + currentUser);
		if (!outputFileDir.exists()) {
			outputFileDir.mkdirs();
		}
		File outputFile = new File(outputFileDir.getAbsolutePath() + File.separator	+ time_str + "_" + originalFileName );
			
		FileUtils.writeByteArrayToFile(outputFile, uploadFile.getBytes());
		String outputFileName = outputFile.getName();

		if(!outputFileName.toLowerCase().endsWith(".xlsx") && !outputFileName.toLowerCase().endsWith(".xls")) {
			throw new FileFormatInvalidException(outputFileName);
		}
		
		
		if (!outputFile.exists()) {
			throw new FileNotFoundException(outputFileName);
		}

		ArrayList<XCol> uploadFileColList = new ArrayList<>();
		uploadFileColList.add(new XCStr(0,"Exist"));
		uploadFileColList.add(new XCStr(1,"Not Exist"));
		uploadFileColList.add(new XCStr(2,"Yet-to-be Located"));
		uploadFileColList.add(new XCStr(3,"Custodian Department Code"));
		uploadFileColList.add(new XCStr(4,"Custodian Department Description"));
		uploadFileColList.add(new XCStr(5,"Business Unit"));
		uploadFileColList.add(new XCStr(6,"Asset Profile ID"));
		uploadFileColList.add(new XCStr(7,"Asset Profile Description"));
		uploadFileColList.add(new XCStr(8,"Asset ID"));
		uploadFileColList.add(new XCStr(9,"Detailed Item Description"));
		uploadFileColList.add(new XCDbl(10,"Total Cost"));
		uploadFileColList.add(new XCDbl(11,"Net Book Value"));
		uploadFileColList.add(new XCStr(12,"Invoice Date"));
		uploadFileColList.add(new XCStr(13,"PO / BR No."));
		uploadFileColList.add(new XCStr(14,"Region"));
		uploadFileColList.add(new XCStr(15,"Not UST Property"));
		uploadFileColList.add(new XCStr(16,"Donated Item"));
		uploadFileColList.add(new XCStr(17,"Location"));
		uploadFileColList.add(new XCStr(18,"Voucher ID"));
		uploadFileColList.add(new XCStr(19,"Invoice ID"));
		
		// read data from excel
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		
		if(outputFileName.toLowerCase().endsWith(".xls")) {
			try (HSSFWorkbook wb = XLSReader.getWorkbook(FileUtils.readFileToByteArray(outputFile))) {				
				HSSFSheet ws = wb.getSheetAt(0);
				data = XLSReader.read(uploadFileColList, 0, ws);
			}
		}else if (outputFileName.toLowerCase().endsWith(".xlsx")) {
			try (XSSFWorkbook wb = XLSReader.getXSSFWorkbook(FileUtils.readFileToByteArray(outputFile))) {
				XSSFSheet ws = wb.getSheetAt(0);
				data = XLSReader.readXSSF(uploadFileColList, 0, ws);
			}
		}
		
		
//		Map<String, Object> header = new HashMap<>();
//        header.put("Exist", "Exist");
//        header.put("Not Exist", "Not Exist");
//        header.put("Yet-to-be Located", "Yet-to-be Located");
//        header.put("Custodian Department Code", "Custodian Department Code");
//        header.put("Custodian Department Description", "Custodian Department Description");
//        header.put("Business Unit", "Business Unit");
//        header.put("Asset Profile ID", "Asset Profile ID");
//        header.put("Asset Profile Description", "Asset Profile Description");
//        header.put("Asset ID", "Asset ID");
//        header.put("Detailed Item Description", "Detailed Item Description");
//        header.put("Total Cost", "Total Cost");
//        header.put("Net Book Value", "Net Book Value");
//        header.put("Invoice Date", "Invoice Date");
//        header.put("PO / BR No.", "PO / BR No.");
//        header.put("Region", "Region");
//        header.put("Not UST Property", "Not UST Property");
//        header.put("Donated Item", "Donated Item");
//        header.put("Location", "Location");
//        header.put("Voucher ID", "Voucher ID");
//        header.put("Invoice ID", "Invoice ID");
//        
//        List<Object> headerError = new ArrayList<>(); // header has wrong format
//        headerError.add("Exist");
//        headerError.add("Not Exist");
//        headerError.add("Custodian Department Description");
//        headerError.add("Yet-to-be Located");
//        headerError.add("Custodian Department Code");
//        headerError.add("Business Unit");
//        headerError.add("Asset Profile ID");
//        headerError.add("Asset Profile Description");
//        headerError.add("Asset ID");
//        headerError.add("Detailed Item Description");
//        headerError.add("Total Cost");
//        headerError.add("Net Book Value");
//        headerError.add("Invoice Date");
//        headerError.add("PO / BR No.");
//        headerError.add("Region");
//        headerError.add("Not UST Property");
//        headerError.add("Donated Item");
//        headerError.add("Location");
//        headerError.add("Voucher ID");
//        headerError.add("Invoice ID");
//        
//        
//        Map<String, Object> data1 = new HashMap<>(); // correct data
//        data1.put("Exist", "Y");
//        data1.put("Not Exist", "");
//        data1.put("Yet-to-be Located", "");
//        data1.put("Custodian Department Code", "abc");
//        data1.put("Custodian Department Description", "abc");
//        data1.put("Business Unit", "BU1");
//        data1.put("Asset Profile ID", "asset prof 1");
//        data1.put("Asset Profile Description", "asset descr 1");
//        data1.put("Asset ID", "ASSET_001");
//        data1.put("Detailed Item Description", "lorem ispum");
//        data1.put("Total Cost", 123.1);
//        data1.put("Net Book Value", 456.8);
//        data1.put("Invoice Date", "2024-10-17");
//        data1.put("PO / BR No.", "PO 1");
//        data1.put("Region", "Hong Kong");
//        data1.put("Not UST Property", "N");
//        data1.put("Donated Item", "N");
//        data1.put("Location", "LG7");
//        data1.put("Voucher ID", "Voucher 1");
//        data1.put("Invoice ID", "invoice 1");
//
//        
//        
//        Map<String, Object> data2 = new HashMap<>(); // More than one y and asset id is null
//        data2.put("Exist", "");
//        data2.put("Not Exist", "Y");
//        data2.put("Yet-to-be Located", "");
//        data2.put("Custodian Department Code", "abc");
//        data2.put("Custodian Department Description", "abc");
//        data2.put("Business Unit", "BU1");
//        data2.put("Asset Profile ID", "asset prof 1");
//        data2.put("Asset Profile Description", "asset descr 1");
//        data2.put("Asset ID", "ASSET_003");
//        data2.put("Detailed Item Description", "lorem ispum");
//        data2.put("Total Cost", 123.1);
//        data2.put("Net Book Value", 456.8);
//        data2.put("Invoice Date", "2024-10-17");
//        data2.put("PO / BR No.", "PO 1");
//        data2.put("Region", "Hong Kong");
//        data2.put("Not UST Property", "N");
//        data2.put("Donated Item", "N");
//        data2.put("Location", "LG7");
//        data2.put("Voucher ID", "Voucher 1");
//        data2.put("Invoice ID", "invoice 1");
//
//        Map<String, Object> data3 = new HashMap<>(); // business unit is empty
//        data3.put("Exist", "");
//        data3.put("Not Exist", "");
//        data3.put("Yet-to-be Located", "");
//        data3.put("Custodian Department Code", "abc");
//        data3.put("Custodian Department Description", "abc");
//        data3.put("Business Unit", "BU1");
//        data3.put("Asset Profile ID", "asset prof 1");
//        data3.put("Asset Profile Description", "asset descr 1");
//        data3.put("Asset ID", "ASSET_005");
//        data3.put("Detailed Item Description", "lorem ispum");
//        data3.put("Total Cost", 123.1);
//        data3.put("Net Book Value", 456.8);
//        data3.put("Invoice Date", "2024-10-17");
//        data3.put("PO / BR No.", "PO 1");
//        data3.put("Region", "Hong Kong");
//        data3.put("Not UST Property", "N");
//        data3.put("Donated Item", "N");
//        data3.put("Location", "LG7");
//        data3.put("Voucher ID", "Voucher 1");
//        data3.put("Invoice ID", "invoice 1");
//  
//		
//        reponseList.add(header);
//        reponseList.add(data1);
//        reponseList.add(data2);
//        reponseList.add(data3);
		
		return data;
		
	}
	//list<fasstkdtlstgdao>
	private void validateUploadData(ArrayList<HashMap<String, Object>> uploadFileData, List<CommonJson> excelErrorList, List<FasStkPlanDtlStgDAO> fasStkPlanDtlStgDAOList, String stkPlanId, String opPageName) throws ErrorDataArrayException {
		
		// check header format
		HashMap<String, Object> headerList = uploadFileData.get(0);
		if (headerList.get("Exist").equals(AppConstants.XLS_HDR_EXIST) && 
			headerList.get("Not Exist").equals(AppConstants.XLS_HDR_NOT_EXIST) &&
			headerList.get("Yet-to-be Located").equals(AppConstants.XLS_HDR_YET_TO_BE_LOCATED) &&
			headerList.get("Business Unit").equals(AppConstants.XLS_HDR_BUSINESS_UNIT) && 
			headerList.get("Asset ID").equals( AppConstants.XLS_HDR_ASSET_ID)) {
			for (int i = 1; i < uploadFileData.size() ;i++) {
				try {
					HashMap<String,Object> datarow = uploadFileData.get(i);
				if (uploadFileData.get(i).equals(headerList)) {
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("errorMsg", "this row is headerlist");
					excelErrorList.add(errormsg);
				}
				
				String Exist =  GeneralUtil.initBlankString((String) datarow.get("Exist")).trim().toUpperCase();
				String Yet_to_be_Located =  GeneralUtil.initBlankString((String)datarow.get("Yet-to-be Located")).trim().toUpperCase();
				String Not_Exist =  GeneralUtil.initBlankString((String)datarow.get("Not Exist")).trim().toUpperCase();
				String Business_Unit = GeneralUtil.initBlankString((String)(datarow.get("Business Unit"))).trim();
				String Asset_ID = GeneralUtil.initBlankString((String)(datarow.get("Asset ID"))).trim();
				
				// check exist, not exist yet-to-be located field
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
				//check business unit
				if (Business_Unit == null|| Business_Unit.toString().isBlank()) {
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("cell_name", "Business Unit");
					errormsg.set("errorMsg", "Business Unit is blank or not exist ");
					excelErrorList.add(errormsg);
				}
				//check asset id
				if (Asset_ID == null || Asset_ID.toString().isBlank()) {
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("cell_name", "Asset ID");
					errormsg.set("errorMsg", "Asset ID is blank or not exist ");
					excelErrorList.add(errormsg);
				}
				FasStkPlanDtlDAOPK fasStkPlanDtlDAOPK = new FasStkPlanDtlDAOPK();
				fasStkPlanDtlDAOPK.setAssetId(Asset_ID);
				fasStkPlanDtlDAOPK.setBusinessUnit(Business_Unit);
				fasStkPlanDtlDAOPK.setStkPlanId(stkPlanId.trim());
				
				FasStkPlanDtlDAO stkPlanDtlDAO = stkPlanDtlRepository.findOne(fasStkPlanDtlDAOPK);
				
				//create staging dao only if that row of excel data exist in stk_plan_dtl
				if (stkPlanDtlDAO == null) {
					CommonJson errormsg = new CommonJson();
					errormsg.set("rowNumber", i);
					errormsg.set("errorMsg", "Row " + i + " Does not contain a valid Stock item, please check Asset Id, Business Unit.");
					excelErrorList.add(errormsg);
				}else if (Y == 1){
					String StkStatus ="";
					if (Exist.equals("Y")) {
						StkStatus = "E";
					}else 
					if (Yet_to_be_Located.equals("Y")) {
						StkStatus = "Y";
					}else 
					if (Not_Exist.equals("Y")) {
						StkStatus = "N";
					}else {
						StkStatus = "";
					}
					FasStkPlanDtlStgDAO FfasStkPlanDtlStgDAO = new FasStkPlanDtlStgDAO(stkPlanDtlDAO, StkStatus);
					fasStkPlanDtlStgDAOList.add(FfasStkPlanDtlStgDAO);
				}
	
//				FasStkPlanDtlStgDAOPK fasStkPlanDtlStgDAOPK = new FasStkPlanDtlStgDAOPK();
//				FasStkPlanDtlStgDAO FfasStkPlanDtlStgDAO = new FasStkPlanDtlStgDAO();
//				
//				fasStkPlanDtlStgDAOPK.setAssetId((String) datarow.get(8));
//				fasStkPlanDtlStgDAOPK.setBusinessUnit((String) datarow.get(5));
//				fasStkPlanDtlStgDAOPK.setStkPlanId(stkPlanId);
//				
//				FfasStkPlanDtlStgDAO.setFasStkPlanDtlStgDAOPK(fasStkPlanDtlStgDAOPK);
//				FfasStkPlanDtlStgDAO.setPoId(GeneralUtil.initNullString((String) datarow.get(13)));
//				FfasStkPlanDtlStgDAO.setAssetDescrLong(GeneralUtil.initNullString((String) datarow.get(9)));
//				FfasStkPlanDtlStgDAO.setDonationFlag(GeneralUtil.initNullString((String) datarow.get(16)));
//				FfasStkPlanDtlStgDAO.setInvoiceDt( GeneralUtil.initNullTimestamp((Timestamp) GeneralUtil.convertStringToTimestamp((String) datarow.get(12))));
//				FfasStkPlanDtlStgDAO.setInvoiceId(GeneralUtil.initNullString((String) datarow.get(19)));
//				FfasStkPlanDtlStgDAO.setLocation(GeneralUtil.initNullString((String) datarow.get(17)));
//				FfasStkPlanDtlStgDAO.setTotalCost(GeneralUtil.initNullDouble((Double) datarow.get(10)));
//				FfasStkPlanDtlStgDAO.setNbv(GeneralUtil.initNullDouble((Double) datarow.get(11)));
//				FfasStkPlanDtlStgDAO.setNotUstProprty(GeneralUtil.initNullString((String) datarow.get(15)));
//				FfasStkPlanDtlStgDAO.setProfileDescr(GeneralUtil.initNullString((String) datarow.get(7)));
//				FfasStkPlanDtlStgDAO.setProfileId(GeneralUtil.initNullString((String) datarow.get(6)));
//				FfasStkPlanDtlStgDAO.setRegionName(GeneralUtil.initNullString((String) datarow.get(14)));
//				FfasStkPlanDtlStgDAO.setVoucherId(GeneralUtil.initNullString((String) datarow.get(18)));
	
//				FfasStkPlanDtlStgDAO.setModCtrlTxt(modCtrlTxt);
//				FfasStkPlanDtlStgDAO.setCreateDate(currentTimestamp);
//				FfasStkPlanDtlStgDAO.setCreateUser(currentUser);
//				FfasStkPlanDtlStgDAO.setChangeDate(currentTimestamp);
//				FfasStkPlanDtlStgDAO.setChangeUser(currentUser);
//				FfasStkPlanDtlStgDAO.setOpPageName(opPageName);
				
//				fasStkPlanDtlStgDAOList.add(FfasStkPlanDtlStgDAO);
				
			
				}catch(Exception e) {
					log.info(e.getMessage());
				}
			}
		}else {
			throw new ErrorDataArrayException("The header format incorrect!");
		}
	}
	
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	private void insertUploadedData (List<FasStkPlanDtlStgDAO> fasStkPlanDtlStgDAOList) throws ErrorDataArrayException {
		for(FasStkPlanDtlStgDAO fasStkPlanDtlStgDAO:fasStkPlanDtlStgDAOList) {
			log.info((fasStkPlanDtlStgDAO.getFasStkPlanDtlStgDAOPK().getAssetId()));
		}
		try {
			stkPlanDtlStgRepository.saveAll(fasStkPlanDtlStgDAOList);
			
		}catch (Exception e) {
			throw new ErrorDataArrayException("Unable to update Staging data");
		}
	}
	
	

}
