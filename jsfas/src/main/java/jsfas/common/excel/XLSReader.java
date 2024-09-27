package jsfas.common.excel;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import jsfas.common.excel.XCol.CellNotFoundException;
import jsfas.common.excel.XCol.ColTypeMismatchException;

public class XLSReader {
	
	public static ArrayList<HashMap<String, Object>> getDataFromFileByXCol(MultipartFile file,
			ArrayList<XCol> cols, int dataStartRow) throws IOException {
		
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		if (!file.isEmpty()) {
			if(file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
				// Read .xlsx to XSSFWorkbook
				XSSFWorkbook wb = XLSReader.getXSSFWorkbook(file.getBytes());
				XSSFSheet ws = wb.getSheetAt(0);
				data = XLSReader.readXSSF(cols, dataStartRow, ws);
			}
			else if(file.getOriginalFilename().toLowerCase().endsWith(".xls")) {
				// Read .xls to HSSFWorkbook
				HSSFWorkbook wb = XLSReader.getWorkbook(file.getBytes());				
				HSSFSheet ws = wb.getSheetAt(0);
				
				data = XLSReader.read(cols, dataStartRow, ws);
			}
			else {
				throw new FileFormatInvalidException(file.getOriginalFilename());
			}
		}
		else {
			throw new FileEmptyException(file.getOriginalFilename());
		}
		
		return data;
	}
	
	public static HSSFWorkbook getWorkbook(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			return new HSSFWorkbook(bais);
		}
		catch(Exception ignore) {
			return null;
		}
		finally {
				try {
					bais.close();
				}
				catch(Exception ignore) {}
		}
	}
	
	public static XSSFWorkbook getXSSFWorkbook(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			return new XSSFWorkbook(bais);
		}
		catch(Exception ignore) {
			return null;
		}
		finally {
				try {
					bais.close();
				}
				catch(Exception ignore) {}
		}
	}
	
	public static ArrayList<HashMap<String, Object>> read(ArrayList<XCol> cols,
			int dataStartRow, HSSFSheet ws) throws IOException {
		XCol[] colsArray = new XCol[cols.size()];
		for(int i=0; i<cols.size(); i++) {
			colsArray[i] = cols.get(i);
		}
		return read(colsArray, dataStartRow, ws);
	}
	public static ArrayList<HashMap<String, Object>> readXSSF(ArrayList<XCol> cols,
			int dataStartRow, XSSFSheet ws) throws IOException {
		XCol[] colsArray = new XCol[cols.size()];
		for(int i=0; i<cols.size(); i++){
			colsArray[i] = cols.get(i);
		}
		return readXSSF(colsArray, dataStartRow, ws);
	}
	
	public static ArrayList<HashMap<String, Object>> read(XCol[] cols,
			int dataStartRow, HSSFSheet ws) throws IOException {
		checkCols(cols);
		ArrayList<HashMap<String, Object>> r = new ArrayList<HashMap<String, Object>>();
		for(int rowNum=dataStartRow; rowNum<=ws.getLastRowNum(); rowNum++) {
			HashMap<String, Object> h = new HashMap<String, Object>();
			boolean allEmpty = true;
			
			for(XCol c : cols) {
				try {
					if(!c.isEmpty(rowNum, ws)) {
						allEmpty = false;
					}
					h.put(c.getName(), c.getData(rowNum, ws));
				}
				catch(ColTypeMismatchException e) {
					h.put(c.getName(), null);
				}
				catch(CellNotFoundException e) {
					break;
				}
			}
			
			if(!allEmpty) {
				r.add(h);
			}
		}
		return r;
	}
	
	public static ArrayList<HashMap<String, Object>> readXSSF(XCol[] cols,
			int dataStartRow, XSSFSheet ws) throws IOException {
		checkCols(cols);
		ArrayList<HashMap<String, Object>> r = new ArrayList<HashMap<String, Object>>();
		for(int rowNum=dataStartRow; rowNum<=ws.getLastRowNum(); rowNum++){
			HashMap<String, Object> h = new HashMap<String, Object>();
			boolean allEmpty = true;
			
			for(XCol c : cols) {
				try {
					if(!c.isEmpty(rowNum, ws)) {
						allEmpty = false;
					}
					h.put(c.getName(), c.getData(rowNum, ws));
				}
				catch(ColTypeMismatchException e) {
					h.put(c.getName(), null);
				}
				catch(CellNotFoundException e) {
					break;
				}
			}
			
			if(!allEmpty) {
				r.add(h);
			}
		}
		return r;
	}
	
	public static int getCellNumByValue(String key, int rowNum, HSSFSheet ws) throws IOException {
		HSSFRow r = ws.getRow(rowNum);
		if(r == null) {
			return -1;
		}
		for(int cellNum=0; cellNum<=r.getLastCellNum(); cellNum++) {
			HSSFCell c = r.getCell(cellNum);
			if((c != null) && (c.getStringCellValue().equalsIgnoreCase(key))) {
				return cellNum;
			}
		}
		return -1;
	}
	
	public static int getCellNumByValue(String key, int rowNum, XSSFSheet ws) throws IOException {
		XSSFRow r = ws.getRow(rowNum);
		if(r == null) {
			return -1;
		}
		for(int cellNum=0; cellNum<=r.getLastCellNum(); cellNum++) {
			XSSFCell c = r.getCell(cellNum);
			if((c != null) && (c.getStringCellValue().equalsIgnoreCase(key))) {
				return cellNum;
			}
		}
		return -1;
	}
	
	public static int getCellNum(String cellRef) {
		return CellReference.convertColStringToIndex(cellRef);
	}
	
	public static String convertExcelDateToString(String excelDateString) throws DateFormatInvalidException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = DateUtil.getJavaDate(new Double(excelDateString));
		return formatter.format(new Timestamp(date.getTime()));
	}
	
	public static Timestamp convertExcelDateToTimestamp(String excelDateString) throws DateFormatInvalidException {
		Date date = DateUtil.getJavaDate(new Double(excelDateString));
		return new Timestamp(date.getTime());
	}
	
	private static void checkCols(XCol[] cols) throws IOException {
		ArrayList<String> colNames = new ArrayList<String>();
		for(XCol c : cols) {
			if(!colNames.contains(c.name)) {
				colNames.add(c.name);
			}
		}
		if(cols.length != colNames.size()) {
			throw new IOException("Column duplicated");
		}
	}
	
	public static class DateFormatInvalidException extends Exception {
		/**
         * 
         */
        private static final long serialVersionUID = 4393552982164053771L;

        public String getMessage() {
			return "Invalid date format";
		}
	}
	
	public static class FileFormatInvalidException extends IOException {
		/**
         * 
         */
        private static final long serialVersionUID = 2005124351478262005L;
        
        String filename;
		
		public FileFormatInvalidException(String filename) {
			this.filename = filename;
		}
		
		public String getMessage() {
			return "File format of (" + filename + ") is not valid.";
		}
	}
	
	public static class FileEmptyException extends IOException {
		/**
         * 
         */
        private static final long serialVersionUID = 4723396849003922376L;
        
        String filename;
		
		public FileEmptyException(String filename) {
			this.filename = filename;
		}
		
		public String getMessage() {
			return "File (" + filename + ") is empty.";
		}
	}
}