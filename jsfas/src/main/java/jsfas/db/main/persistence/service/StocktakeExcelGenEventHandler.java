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

import com.ibm.icu.text.DecimalFormat;	// java.text.DecimalFormat ??

import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAO;
import jsfas.db.main.persistence.domain.FasStkPlanDtlDAOPK;
import jsfas.db.main.persistence.domain.FasStkPlanDtlStgDAO;
import jsfas.db.main.persistence.domain.FasStkPlanHdrDAO;
import jsfas.db.main.persistence.repository.FasStkPlanDtlRepository;
import jsfas.db.main.persistence.repository.FasStkPlanDtlStgRepository;
import jsfas.db.main.persistence.repository.FasStkPlanHdrRepostiory;

import jsfas.common.utils.ExcelFileUtils;

public class StocktakeExcelGenEventHandler implements StocktakeExcelGenService{
	
	
	@Inject Environment env;

	@Autowired
	FasStkPlanHdrRepostiory stkPlanHdrRepository;

	@Autowired
	FasStkPlanDtlRepository stkPlanDtlRepository;

	@Autowired
	FasStkPlanDtlStgRepository stkPlanDtlStgRepository;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public JSONObject commitAssetGeneration(JSONObject inputJson) throws Exception {
		JSONObject outputJson = new JSONObject();

		//to do, validate if form is ready for submission by checking if all rows valid, check if user can submit
		//to do 2, update stkplanhdr status = submit 

		String stkPlanId = inputJson.optString("stkPlanId");

		File dir = new File(env.getProperty("downld.dir"));
		String fileExtension = ".xlsx";
		String currentTimestamp =  new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

		List<FasStkPlanDtlDAO> stkItems = stkPlanDtlRepository.findAllDtlFromPlanId(stkPlanId);

		try {
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("Award Report");
				
				//create styles
				DataFormat format = workbook.createDataFormat();
				CellStyle currencyStyle = workbook.createCellStyle();
				currencyStyle.setDataFormat(format.getFormat("_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)"));
				
				CellStyle percentageStyle = workbook.createCellStyle();
				percentageStyle.setDataFormat(format.getFormat("0.00%"));
						
				// prepare sheet header
				// List<String> headerCols = Stream.of("").collect(Collectors.toList());
				
				ExcelFileUtils excelFileUtils = new ExcelFileUtils();
				// excelFileUtils.createHeaderRow(sheet, headerCols);		
				
				if(stkItems.size()<0) {
					List<Object> noRecordList = Collections.singletonList("No Records");
					Row row = sheet.createRow(2);
					excelFileUtils.writeToCells(row, noRecordList);
				}else {
					int rowCount = 1; // assume there is header column already
					
					for (FasStkPlanDtlDAO item : stkItems) {
						List<Object> colValues = new ArrayList<Object>();
						colValues.add(item.getFasStkPlanDtlDAOPK().getAssetId());
						colValues.add(item.getFasStkPlanDtlDAOPK().getBusinessUnit());
						colValues.add(item.getFasStkPlanDtlDAOPK().getStkPlanId());
						Row row = sheet.createRow(rowCount++);
						excelFileUtils.writeToCells(row, colValues,percentageStyle,currencyStyle);
					}
				}
								
				// resize column to max content width
				// for(int i = 0;i<headerCols.size();i++) {
				// 	sheet.autoSizeColumn(i);
				// }
				
				
				// generate report file			
				workbook.write(new FileOutputStream(dir.getAbsolutePath() + File.separator + stkPlanId+"_"+currentTimestamp +fileExtension));
				workbook.close();
								
			} catch (Exception e){
				log.error("downloadGenericAwardReport error:"+e);
			}
			log.debug("downloadGenericAwardReport complete");

		return outputJson;
	}

	@Override
	public JSONObject rfidGeneration(JSONObject inputJson) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'rfidGeneration'");
	}

}
