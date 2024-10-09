package jsfas.db.main.persistence.service;

import javax.inject.Inject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.ibm.icu.text.DecimalFormat; // java.text.DecimalFormat ??

import jsfas.common.constants.StkPlanStatus;
import jsfas.common.constants.StkStatus;
import jsfas.common.excel.XCDbl;
import jsfas.common.excel.XCStr;
import jsfas.common.excel.XCol;
import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanHdrDAO;
import jsfas.db.main.persistence.repository.FasStkPlanDtlRepository;
import jsfas.db.main.persistence.repository.FasStkPlanDtlStgRepository;
import jsfas.db.main.persistence.repository.FasStkPlanHdrRepostiory;
import jsfas.db.main.persistence.service.FileService;
import jsfas.common.exception.StocktakeExcelValidationException;

import jsfas.common.utils.ExcelFileUtils;

public class StocktakeExcelEventHandler implements StocktakeExcelService {

	@Inject
	Environment env;

	@Autowired
	FasStkPlanHdrRepostiory stkPlanHdrRepository;

	@Autowired
	FasStkPlanDtlRepository stkPlanDtlRepository;

	@Autowired
	FasStkPlanDtlStgRepository stkPlanDtlStgRepository;

	@Autowired
	FileService fileService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	@Transactional(value = "transactionManagerJsfasMain", rollbackFor = Exception.class)
	public JSONObject insertExcelToStaging(MultipartFile uploadFile, String stkPlanId, String opPageName)
			throws Exception {
		JSONObject outputJson = new JSONObject();

		// System.out.println("insert excel to staging");

		stkPlanId = GeneralUtil.initBlankString(stkPlanId).trim();
		opPageName = GeneralUtil.initBlankString(opPageName).trim();
		String currentUser = "isod01";

		// prepare error & warning lists for excel file validation
		List<CommonJson> excelErrorList = new ArrayList<>();
		List<FasStkPlanDtlStgDAO> stgDAOList = new ArrayList<>();

		ArrayList<XCol> uploadFileColList = new ArrayList<>();
		ArrayList<String> colList = new ArrayList<>(Arrays.asList("exist", "notExist", "ytb","custDeptId",
				"custDeptDescr", "businessUnit", "profileId", "profileDescr", "assetId", "assetDescr", "totalCost",
				"nbv", "invoiceDt", "poId", "regionName", "notUstProperty", "donated", "location", "voucherId",
				"invoiceId"));

		for (int i = 0; i < colList.size(); i++) {
			String header = colList.get(i);
			uploadFileColList.add(
					header.equals("totalCost") || header.equals("nbv") ? new XCDbl(i, header) : new XCStr(i, header));
		}

		ArrayList<HashMap<String, Object>> uploadFileData = null;
		try {
			uploadFileData = fileService.getDataFromUploadFile(uploadFile, uploadFileColList,
					1);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		// System.out.println("uploadFileData size: " + uploadFileData.size());

		if (uploadFileData.size()<1){
			throw new Exception("Excel file data should at least have one row");
		}

		try {
			validateUploadExcelData(uploadFileData,excelErrorList,stgDAOList,stkPlanId,opPageName);
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("Failed at validation");
		}

		if (!excelErrorList.isEmpty()) {
			StocktakeExcelValidationException aeve = new StocktakeExcelValidationException();
			aeve.setErrorList(excelErrorList);
			throw aeve;
		}else{
			// try {
			// 	System.out.println(stgDAOList.size());
			// 	stkPlanDtlStgRepository.saveAll(stgDAOList);
			// 	return outputJson;
			// } catch (Exception e) {
			// 	// TODO: handle exception
			// 	throw new  Exception ("Unable to add data to staging table");
			// }
			// process data into database
			// System.out.println(stgDAOList.size());
			stkPlanDtlStgRepository.saveAll(stgDAOList);

			FasStkPlanHdrDAO stkPlan = stkPlanHdrRepository.findOne(stkPlanId);
			if (stkPlan == null){
			throw new Exception("Stocktake Plan cannot be found");
			}

			Map<String,Object> latestCount = stkPlanDtlStgRepository.findPendingStagingById(stkPlanId);

			stkPlan.setChngDat(GeneralUtil.getCurrentTimestamp());
			stkPlan.setChngUser(currentUser);
			stkPlan.setModCtrlTxt(GeneralUtil.genModCtrlTxt());
			stkPlan.setOpPageNam(opPageName);

			// System.out.println(latestCount.get("pending"));

			if (latestCount.get("pending") != null && GeneralUtil.initNullBigDecimal((BigDecimal)latestCount.get("pending")) != GeneralUtil.NULLBIGDECIMAL){
			stkPlan.setStkPlanStatus(StkPlanStatus.INPROCESS);
			}

			stkPlanHdrRepository.save(stkPlan);
			outputJson.put("pending", latestCount.get("pending"));
			return outputJson;
		}

	}

	@Override // sample used to generate excel
	public JSONObject downloadExcel(JSONObject inputJson) throws Exception {
		JSONObject outputJson = new JSONObject();

		// to do, validate if form is ready for submission by checking if all rows
		// valid, check if user can submit
		// to do 2, update stkplanhdr status = submit

		String stkPlanId = inputJson.optString("stkPlanId");

		File dir = new File(env.getProperty("downld.dir"));
		String fileExtension = ".xlsx";
		String currentTimestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

		List<FasStkPlanDtlDAO> stkItems = stkPlanDtlRepository.findAllDtlFromPlanId(stkPlanId);

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Award Report");

			// create styles
			DataFormat format = workbook.createDataFormat();
			CellStyle currencyStyle = workbook.createCellStyle();
			currencyStyle.setDataFormat(format.getFormat("_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)"));

			CellStyle percentageStyle = workbook.createCellStyle();
			percentageStyle.setDataFormat(format.getFormat("0.00%"));

			// prepare sheet header
			// List<String> headerCols = Stream.of("").collect(Collectors.toList());

			ExcelFileUtils excelFileUtils = new ExcelFileUtils();
			// excelFileUtils.createHeaderRow(sheet, headerCols);

			if (stkItems.size() < 0) {
				List<Object> noRecordList = Collections.singletonList("No Records");
				Row row = sheet.createRow(2);
				excelFileUtils.writeToCells(row, noRecordList);
			} else {
				int rowCount = 1; // assume there is header column already

				for (FasStkPlanDtlDAO item : stkItems) {
					List<Object> colValues = new ArrayList<Object>();
					colValues.add(item.getFasStkPlanDtlDAOPK().getAssetId());
					colValues.add(item.getFasStkPlanDtlDAOPK().getBusinessUnit());
					colValues.add(item.getFasStkPlanDtlDAOPK().getStkPlanId());
					Row row = sheet.createRow(rowCount++);
					excelFileUtils.writeToCells(row, colValues, percentageStyle, currencyStyle);
				}
			}

			// resize column to max content width
			// for(int i = 0;i<headerCols.size();i++) {
			// sheet.autoSizeColumn(i);
			// }

			// generate report file
			workbook.write(new FileOutputStream(
					dir.getAbsolutePath() + File.separator + stkPlanId + "_" + currentTimestamp + fileExtension));
			workbook.close();

		} catch (Exception e) {
			log.error("downloadGenericAwardReport error:" + e);
		}
		log.debug("downloadGenericAwardReport complete");

		return outputJson;
	}

	private void validateUploadExcelData(ArrayList<HashMap<String, Object>> uploadFileData,
			List<CommonJson> excelErrorList, List<FasStkPlanDtlStgDAO> stgDAOList, String stkPlanId, String opPageName)
			throws Exception {
		if (excelErrorList == null) {
			throw new Exception("Excel Error List not initialized");
		}
		if (stgDAOList == null) {
			throw new Exception("Staging DAO List no initialized");
		}

		int col, row;
		HashMap<String, Object> tmpColMap;

		for (int i = 0; i < uploadFileData.size(); i++) {
			tmpColMap = (HashMap<String, Object>) uploadFileData.get(i);

			// set starting row
			row = i + 2;

			// check col0 - exist
			col = 0;

			int numOfStatus = 0;
			String currentStkStatus = "";

			try {
				String exist = (String) tmpColMap.get("exist");
				// log.info("tmpStudentId {}", tmpStudentId);
				if (!StringUtils.isEmpty(exist.trim()) && !exist.trim().equalsIgnoreCase("Y") && !exist.trim().equalsIgnoreCase("N")) {
					excelErrorList
							.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
									.set("errorMsg", "Exist Column may only contain \"Y\",\"N\", or is Empty"));
				} else if (exist.trim().equalsIgnoreCase("Y")){
					currentStkStatus = StkStatus.EXIST;
					numOfStatus++;
				}

			} catch (Exception e) {
				excelErrorList.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
						.set("errorMsg", "exist should be a valid text."));
			}

			// check col1 - notExist
			col = 1;
			try {
				String notExist = (String) tmpColMap.get("notExist");
				// log.info("tmpStudentId {}", tmpStudentId);
				if (!StringUtils.isEmpty(notExist.trim()) && !notExist.trim().equalsIgnoreCase("Y") && !notExist.trim().equalsIgnoreCase("N")) {
					excelErrorList
							.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
									.set("errorMsg", "Not Exist Column may only contain \"Y\",\"N\", or is Empty"));
				} else if (notExist.trim().equalsIgnoreCase("Y")){
					currentStkStatus = StkStatus.NOTExist;
					numOfStatus++;
				}

			} catch (Exception e) {
				excelErrorList.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
						.set("errorMsg", "notExist should be a valid text."));
			}

			// check col2 - ytb
			col = 2;
			try {
				String ytb = (String) tmpColMap.get("ytb");
				// log.info("tmpStudentId {}", tmpStudentId);
				if (!StringUtils.isEmpty(ytb.trim()) && !ytb.trim().equalsIgnoreCase("Y") && !ytb.trim().equalsIgnoreCase("N")) {
					excelErrorList
							.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
									.set("errorMsg", "Yet-to-be Located Column may only contain \"Y\",\"N\", or is Empty"));
				} else if (ytb.trim().equalsIgnoreCase("Y")){
					currentStkStatus = StkStatus.YTBLOCATED;
					numOfStatus++;
				}

			} catch (Exception e) {
				excelErrorList.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
						.set("errorMsg", "ytb should be a valid text."));
			}

			if (numOfStatus>1){
				excelErrorList.add(new CommonJson().set("cellReference", row)
						.set("errorMsg", "Row "+row+" cannot have more than one status value"));
			}

			// check col5 - businessUnit
			col = 5;
			try {
				String businessUnit = (String) tmpColMap.get("businessUnit");
				// log.info("tmpStudentId {}", tmpStudentId);
				if (StringUtils.isEmpty(businessUnit.trim())) {
					excelErrorList
							.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
									.set("errorMsg", "Business unit should not be empty."));
				} 

			} catch (Exception e) {
				excelErrorList.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
						.set("errorMsg", "Business unit should be a valid text."));
			}

			// check col8 - assetId
			col = 8;
			try {
				String assetId = (String) tmpColMap.get("assetId");
				// log.info("tmpStudentId {}", tmpStudentId);
				if (StringUtils.isEmpty(assetId.trim())) {
					excelErrorList
							.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
									.set("errorMsg", "Asset ID should not be empty."));
				}

			} catch (Exception e) {
				excelErrorList.add(new CommonJson().set("cellReference", CellReference.convertNumToColString(col) + row)
						.set("errorMsg", "Asset ID should be a valid text."));
			}

			try {
				String assetId = (String) tmpColMap.get("assetId");
				String businessUnit = (String) tmpColMap.get("businessUnit");

				FasStkPlanDtlDAOPK stkPlanDtlDAOPK = new FasStkPlanDtlDAOPK();
				stkPlanDtlDAOPK.setAssetId(assetId);
				stkPlanDtlDAOPK.setBusinessUnit(businessUnit);
				stkPlanDtlDAOPK.setStkPlanId(stkPlanId);

				FasStkPlanDtlDAO stkPlanDtl = stkPlanDtlRepository.findOne(stkPlanDtlDAOPK);

				if (stkPlanDtl == null){
					excelErrorList.add(new CommonJson().set("cellReference", row)
						.set("errorMsg", "Row "+row+" Does not contain a valid Stock Item, please check \"Asset Id\", \"Business Unit\""));
				}else{
					if (!currentStkStatus.trim().isEmpty()){
						// FasStkPlanDtlStgDAOPK stgDAOPK = new FasStkPlanDtlStgDAOPK();
						// stgDAOPK.setAssetId(assetId);
						// stgDAOPK.setBusinessUnit(businessUnit);
						// stgDAOPK.setStkPlanId(stkPlanId);
						// stgDAOList.add(new FasStkPlanDtlStgDAO(stkPlanDtl, stgDAOPK ,currentStkStatus));
						stgDAOList.add(new FasStkPlanDtlStgDAO(stkPlanDtl, currentStkStatus));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			

		}

	}

}
