package com.guptasaurabh39.CustomerOnboardingApp.excelOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
		return (int) workSheet.getRow(row).getCell(column).getNumericCellValue();
	}
	
	public int getRowCount(Sheet workSheet){
		return workSheet.getLastRowNum();
	}

}
