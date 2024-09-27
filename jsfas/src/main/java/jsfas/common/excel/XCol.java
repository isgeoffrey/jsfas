package jsfas.common.excel;

import java.io.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public abstract class XCol {
	int cellNum = -1;
	String name = "";
	
	public XCol(int cellNum, String name) {
		this.cellNum = cellNum;
		this.name = name; // original: name.toUpperCase();
	}
	
	public abstract boolean isEmpty(int rownum, HSSFSheet ws) throws IOException;
	public abstract boolean isEmpty(int rownum, XSSFSheet ws) throws IOException;
	
	public String getName(){
		return name;
	}
	
	public abstract CellType getCellTypeEnum();
	
	public void checkCell(HSSFCell c) throws IOException{};
	public void checkCell(XSSFCell c) throws IOException{};
	
	public abstract Object getData(int rowNum, HSSFSheet ws) throws IOException;
	public abstract Object getData(int rowNum, XSSFSheet ws) throws IOException;
	
	protected HSSFCell getCell(int rowNum, HSSFSheet ws) throws IOException {
		HSSFCell c = getCell(rowNum, cellNum, ws);
		if(c == null) {
			throw new CellNotFoundException(rowNum, cellNum);
		}
		if((c.getCellTypeEnum() != CellType.BLANK) &&
				(c.getCellTypeEnum() != getCellTypeEnum())) {
			throw new ColTypeMismatchException(getName(), c.getRowIndex(), c.getColumnIndex());
		}
		checkCell(c);
		return c;
	}
	
	protected XSSFCell getCell(int rowNum, XSSFSheet ws) throws IOException {
		XSSFCell c = getCell(rowNum, cellNum, ws);
		if(c == null) {
			throw new CellNotFoundException(rowNum, cellNum);
		}
		if((c.getCellTypeEnum() != CellType.BLANK) &&
				(c.getCellTypeEnum() != getCellTypeEnum())) {
			throw new ColTypeMismatchException(getName(), c.getRowIndex(), c.getColumnIndex());
		}
		checkCell(c);
		return c;
	}
	
	protected static HSSFCell getCell(int rowNum, int cellNum, HSSFSheet ws) throws IOException {
		HSSFCell c = null;
		HSSFRow r = ws.getRow(rowNum);
		if(r != null) {
			c = r.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		}
		return c;
	}
	
	protected static XSSFCell getCell(int rowNum, int cellNum, XSSFSheet ws) throws IOException {
		XSSFCell c = null;
		XSSFRow r = ws.getRow(rowNum);
		if(r != null) {
			c = r.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		}
		return c;
	}
	
	public static class CellNotFoundException extends IOException {
		/**
         * 
         */
        private static final long serialVersionUID = -1019108188807298191L;
        
        int eRowNum = -1;
		int eCellNum = -1;
		
		public CellNotFoundException(int rowNum, int cellNum) {
			eRowNum = rowNum;
			eCellNum = cellNum;
		}
		
		public String getMessage() {
			return "Cell not found (rownum=" + eRowNum + ",cellnum=" + eCellNum + ")";
		}
	}
	
	public static class ColTypeMismatchException extends IOException {
		/**
         * 
         */
        private static final long serialVersionUID = -2563618269220176212L;
        
        String eName = "";
		int eRowNum = -1;
		int eCellNum = -1;
		
		public ColTypeMismatchException(String name, int rowNum, int cellNum) {
			eName = name;
			eRowNum = rowNum;
			eCellNum = cellNum;
		}
		
		public String getMessage() {
			return "Cell type mismatch for column '" + eName + 
					"'(rownum=" + eRowNum + ",cellnum=" + eCellNum + ")";
		}
	};
}
