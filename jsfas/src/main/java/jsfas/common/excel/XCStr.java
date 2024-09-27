package jsfas.common.excel;

import java.io.*;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

public class XCStr extends XCol {
	public XCStr(int cellNum,String name) {
		super(cellNum, name);
	}
	
	public boolean isEmpty(int rowNum, HSSFSheet ws) throws IOException {
		return ((String)getData(rowNum, ws)).equals("");
	}
	
	public boolean isEmpty(int rowNum,XSSFSheet ws)throws IOException {
		return ((String)getData(rowNum, ws)).equals("");
	}
	
	public CellType getCellTypeEnum(){
		return CellType.STRING;
	}
	
	protected HSSFCell getCell(int rowNum, HSSFSheet ws) throws IOException {
		HSSFCell c = getCell(rowNum,cellNum,ws);
		if(c == null) {
			throw new CellNotFoundException(rowNum, cellNum);
		}
		if((c.getCellTypeEnum() != CellType.BLANK) &&
				(c.getCellTypeEnum() != CellType.NUMERIC) &&
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
                (c.getCellTypeEnum() != CellType.NUMERIC) &&
                (c.getCellTypeEnum() != getCellTypeEnum())) {
			throw new ColTypeMismatchException(getName(), c.getRowIndex(), c.getColumnIndex());
		}
		checkCell(c);
		return c;
	}
	
	public Object getData(int rowNum, HSSFSheet ws) throws IOException {
		String value = "";
		HSSFCell c = getCell(rowNum, ws);
		if(c != null) {
			try {
				if(c.getCellTypeEnum() == CellType.NUMERIC) {
					if(c.getNumericCellValue() == (int)c.getNumericCellValue()) {
						value = String.valueOf((int)c.getNumericCellValue());
					}
					else {
						value = String.valueOf(c.getNumericCellValue());
					}
				}
				else {
					value = c.getRichStringCellValue().getString();
				}
			}
			catch(Exception ignore){}
		}
		return value;
	}
	
	public Object getData(int rowNum, XSSFSheet ws) throws IOException {
		String value = "";
		XSSFCell c = getCell(rowNum, ws);
		if(c != null) {
			try {
				if(c.getCellTypeEnum() == CellType.NUMERIC) {
					if(c.getNumericCellValue() == (int)c.getNumericCellValue()) {
						value = String.valueOf((int)c.getNumericCellValue());
					}
					else {
						value = String.valueOf(c.getNumericCellValue());
					}
				}
				else{
					value = c.getRichStringCellValue().getString();
				}
			}
			catch(Exception ignore) {}
		}
		return value;
	}
}