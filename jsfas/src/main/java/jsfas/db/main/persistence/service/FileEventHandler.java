package jsfas.db.main.persistence.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import jsfas.common.constants.AppConstants;
import jsfas.common.excel.XCol;
import jsfas.common.excel.XLSReader;
import jsfas.common.excel.XLSReader.FileEmptyException;
import jsfas.common.excel.XLSReader.FileFormatInvalidException;

import jsfas.common.exception.InvalidParameterException;
import jsfas.common.json.CommonJson;
import jsfas.common.utils.GeneralUtil;

import jsfas.security.SecurityUtils;

public class FileEventHandler implements FileService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	Environment env;
	
	@Override
	public ArrayList<HashMap<String, Object>> getDataFromUploadFile(MultipartFile uploadFile,
			ArrayList<XCol> cols, int dataStartRow) throws Exception{

			String originalFileName = uploadFile.getOriginalFilename();
	
			String uploadFileName = FilenameUtils.getBaseName(originalFileName);
			String uploadFileExt = FilenameUtils.getExtension(originalFileName);
		
		if(uploadFile.isEmpty()) {
			throw new Exception("File not found or empty");
		}
		
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		
		if(uploadFileExt.contentEquals("xlsx")) {			
			try (XSSFWorkbook wb = XLSReader.getXSSFWorkbook(uploadFile.getBytes())) {
				XSSFSheet ws = wb.getSheetAt(0);
				data = XLSReader.readXSSF(cols, dataStartRow, ws);
			}
		}
		else if(uploadFileExt.contentEquals("xls")) {
			try (HSSFWorkbook wb = XLSReader.getWorkbook(uploadFile.getBytes())) {				
				HSSFSheet ws = wb.getSheetAt(0);
				data = XLSReader.read(cols, dataStartRow, ws);
			}
		}
		
		return data;
	}

}
