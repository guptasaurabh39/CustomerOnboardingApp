package com.guptasaurabh39.CustomerOnboardingApp.excelOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel {

	private String inputFile;
	FileInputStream excelFile;
	Workbook workbook;

	public ReadExcel(String inputFile) {
		this.inputFile = inputFile;

		try {
			excelFile = new FileInputStream(new File(inputFile));
			workbook = new XSSFWorkbook(excelFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isSheetNameAvailable(String sheetName) {
		boolean flgSheetFound = false;
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		Sheet currSheet;
		while (sheetIterator.hasNext()) {
			currSheet = sheetIterator.next();
			if (currSheet.getSheetName().equals(sheetName)) {
				flgSheetFound = true;
				break;
			}
		}
		return flgSheetFound;
	}

	public boolean isColumnHeaderAvailable(String sheetName, String headerName) {
		boolean flgSheetFound = false;
		boolean flgHeaderFound = false;
		int columnCount;
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		Sheet currSheet;
		while (sheetIterator.hasNext()) {
			currSheet = sheetIterator.next();
			if (currSheet.getSheetName().equals(sheetName)) {
				flgSheetFound = true;
				columnCount = currSheet.getRow(0).getPhysicalNumberOfCells();
				for (int i = 0; i < columnCount; i++) {
					if (currSheet.getRow(0).getCell(i).getStringCellValue()
							.equalsIgnoreCase(headerName)) {
						flgHeaderFound = true;
						break;
					}
				}
				break;
			}
		}
		return (flgSheetFound && flgHeaderFound);
	}

	public Sheet getSheet(String sheetName) {
		return workbook.getSheet(sheetName);
	}

	public String getStringValue(Sheet workSheet, int row, int column) {
		return workSheet.getRow(row).getCell(column).getStringCellValue();
	}

	public double getNumericalValue(Sheet workSheet, int row, int column) {
		return workSheet.getRow(row).getCell(column).getNumericCellValue();
	}

	public int getIntegerValue(Sheet workSheet, int row, int column) {
		return (int) workSheet.getRow(row).getCell(column)
				.getNumericCellValue();
	}

	public int getRowCount(Sheet workSheet) {
		return workSheet.getLastRowNum();
	}
	
	public List<String> getDataListByColumnName(String sheetName, String headerName){
		FormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
		DataFormatter objDefaultFormat = new DataFormatter();
		
		List<String> lstData = new ArrayList<String>();
		boolean flgSheetFound = false;
		boolean flgHeaderFound = false;
		int columnID = -1;
		int columnCount;
		int rowNum = 0;
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		Sheet currSheet;
		while (sheetIterator.hasNext()) {
			currSheet = sheetIterator.next();
			rowNum = currSheet.getLastRowNum();
			if (currSheet.getSheetName().equals(sheetName)) {
				flgSheetFound = true;
				columnCount = currSheet.getRow(0).getPhysicalNumberOfCells();
				for (int i = 0; i < columnCount; i++) {
					if (currSheet.getRow(0).getCell(i).getStringCellValue()
							.equalsIgnoreCase(headerName)) {
						flgHeaderFound = true;
						columnID = i;
						break;
					}
				}
				break;
			}
		}
		if(columnID > -1){
			for(int i = 1; i <= rowNum; i++){
				Cell cellValue = workbook.getSheet(sheetName).getRow(i).getCell(columnID);
			    objFormulaEvaluator.evaluate(cellValue);
			    String cellValueStr = objDefaultFormat.formatCellValue(cellValue,objFormulaEvaluator);

				lstData.add(cellValueStr);
			}
		}
		return lstData;
	}

	@SuppressWarnings("deprecation")
	public boolean isCellValueNumeric(Sheet workSheet, int row, int column) {
		CellType cellType = workSheet.getRow(row).getCell(column)
				.getCellTypeEnum();
		if (cellType.equals(CellType.NUMERIC)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public boolean isCellValueString(Sheet workSheet, int row, int column) {
		CellType cellType = workSheet.getRow(row).getCell(column)
				.getCellTypeEnum();
		if (cellType.equals(CellType.STRING)) {
			return true;
		} else {
			return false;
		}
	}

	public int getColNumForHeader(String sheetName, String attributeName) {
		Sheet sheet = workbook.getSheet(sheetName);
		int colNum = -1;
		for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			if (sheet.getRow(0).getCell(i).getStringCellValue()
					.equalsIgnoreCase(attributeName)) {
				colNum = i;
				break;
			}
		}
		return colNum;
	}

}
