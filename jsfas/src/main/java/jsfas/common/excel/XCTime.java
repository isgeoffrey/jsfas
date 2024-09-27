package jsfas.common.excel;

import java.io.*;
import java.sql.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

public class XCTime extends XCol {
	public XCTime(int cellNum, String name) {
		super(cellNum, name);
	}
	
	public boolean isEmpty(int rowNum, HSSFSheet ws) throws IOException {
		return getData(rowNum,ws) == null;
	}
	
	public boolean isEmpty(int rowNum, XSSFSheet ws) throws IOException {
		return getData(rowNum, ws) == null;
	}
	
	public CellType getCellTypeEnum() {
		return CellType.NUMERIC;
	}
	
	public void checkCell(HSSFCell c) throws IOException {
		if(c.getCellTypeEnum() == CellType.BLANK) {
			return;
		}
		try {
			c.getDateCellValue().getTime();
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	public void checkCell(XSSFCell c) throws IOException {
		if(c.getCellTypeEnum() == CellType.BLANK) {
			return;
		}
		try {
			c.getDateCellValue().getTime();
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	public Object getData(int rowNum, HSSFSheet ws) throws IOException {
		Timestamp value = null;
		HSSFCell c = getCell(rowNum, ws);
		if(c != null) {
			try {
				value = new Timestamp(c.getDateCellValue().getTime());
			}
			catch(Exception ignore) {}
		}
		return value;
	}
	
	public Object getData(int rowNum, XSSFSheet ws) throws IOException {
		Timestamp value = null;
		XSSFCell c = getCell(rowNum, ws);
		if(c != null) {
			try {
				value = new Timestamp(c.getDateCellValue().getTime());
			}
			catch(Exception ignore) {}
		}
		return value;
	}
}