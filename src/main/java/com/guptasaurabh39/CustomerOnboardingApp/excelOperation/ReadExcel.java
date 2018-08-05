package com.guptasaurabh39.CustomerOnboardingApp.excelOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

}
