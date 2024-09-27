package jsfas.common.excel;

import java.io.*;
import java.math.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

public class XCDbl extends XCol {
	public XCDbl(int cellNum, String name) {
		super(cellNum, name);
	}
	
	public boolean isEmpty(int rowNum, HSSFSheet ws) throws IOException {
		return new BigDecimal(Double.MIN_VALUE).equals(getData(rowNum, ws));
	}
	
	public boolean isEmpty(int rowNum, XSSFSheet ws) throws IOException {
		return new BigDecimal(Double.MIN_VALUE).equals(getData(rowNum, ws));
	}
	
	public CellType getCellTypeEnum() {
		return CellType.NUMERIC;
	}
	
	public Object getData(int rowNum, HSSFSheet ws) throws IOException {
		double value = Double.MIN_VALUE;
		HSSFCell c = getCell(rowNum, ws);
		if(c != null) {
			value = c.getNumericCellValue();
		}
		return new BigDecimal(value);
	}
	
	public Object getData(int rowNum, XSSFSheet ws) throws IOException {
		double value = Double.MIN_VALUE;
		XSSFCell c = getCell(rowNum, ws);
		if(c != null) {
			value = c.getNumericCellValue();
		}
		return new BigDecimal(value);
	}
}