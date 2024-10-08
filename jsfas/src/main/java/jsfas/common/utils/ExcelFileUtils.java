package jsfas.common.utils;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author isgeoffrey
 * @since 06/11/2023
 * @based on jsak ExcelFileUtils by isjoey
 */


public class ExcelFileUtils {

	public void createHeaderRow(Sheet sheet, List<String> headerTxtList) {
		Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 11);

		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setFont(font);
//		cellStyle.setBorderBottom((short) 2);
		cellStyle.getShrinkToFit();
		cellStyle.setWrapText(false);

		Row row = sheet.createRow(0);

		int colIdx = 0;
		for (String headerTxt : headerTxtList) {
			Cell cellTitle = row.createCell(colIdx);
			cellTitle.setCellStyle(cellStyle);
			cellTitle.setCellValue(headerTxt);

			colIdx++;
		}
	}

	public void writeToCells(Row row, List<Object> cellValList) {
		int colIdx = 0;
		for (Object colVal : cellValList) {
			Cell cellTitle = row.createCell(colIdx);
			
			if (colVal instanceof String) {
				cellTitle.setCellValue((String) colVal);
			} else if (colVal instanceof Integer) {
				cellTitle.setCellValue((Integer) colVal);
			}
			
			colIdx++;
		}
	}
	
	public void writeToCells(Row row, List<Object> cellValList, CellStyle style) {
		int colIdx = 0;
		for (Object colVal : cellValList) {
			Cell cellTitle = row.createCell(colIdx);
			
			if (colVal instanceof String) {
				cellTitle.setCellValue((String) colVal);
				cellTitle.setCellStyle(style);
			} else if (colVal instanceof Integer) {
				cellTitle.setCellValue((Integer) colVal);
				cellTitle.setCellStyle(style);
			}
			
			colIdx++;
		}
	}
	
	// reference: https://stackoverflow.com/questions/319438/basic-excel-currency-format-with-apache-poi
	// reference: https://stackoverflow.com/questions/1607680/convert-excel-cell-text-into-number-poi
	public void writeToCells(Row row, List<Object> cellValList,CellStyle percentageStyle, CellStyle currencyStyle) {
		int colIdx = 0;
		for (Object colVal : cellValList) {
			Cell cellTitle = row.createCell(colIdx);
			
			if (colVal instanceof String) {
				if (((String) colVal).contains("$")) {
					cellTitle.setCellStyle(currencyStyle);
					cellTitle.setCellValue(Double.parseDouble(((String) colVal).replace("$", "").replace(",", "").replace("%", "").trim()));
				}else if(((String) colVal).contains("%")) {
					cellTitle.setCellStyle(percentageStyle);
					// after converting to double, need to divide by 100 to generate valid percentage
					cellTitle.setCellValue(Double.parseDouble(((String) colVal).replace(",", "").replace("%", "").trim())*0.01);
				}
				else{
					cellTitle.setCellValue((String) colVal);
				}
			} else if (colVal instanceof Integer) {
				cellTitle.setCellValue((Integer) colVal);
			}
			
			colIdx++;
		}
	}

}