package com.guptasaurabh39.CustomerOnboardingApp.excelOperation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteExcel {
	private String filePath;
	XSSFWorkbook workbook;
	XSSFSheet sheet;
	int currRowNum;
	int errorID;

	public WriteExcel(String filePath) {
		this.filePath = filePath;
		this.currRowNum = 0;
		this.errorID = 1;

		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("errors");

		// Write Header
		Row row = sheet.createRow(this.currRowNum);
		row.createCell(0).setCellValue("ErrorID");
		row.createCell(1).setCellValue("SheetName");
		row.createCell(2).setCellValue("RowNumber");
		row.createCell(3).setCellValue("ColumnNumber");
		row.createCell(4).setCellValue("ErrorDescription");

		this.currRowNum++;
	}

	public void autoSizeColumns() {
		if (sheet.getPhysicalNumberOfRows() > 0) {
			Row row = sheet.getRow(0);
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int columnIndex = cell.getColumnIndex();
				sheet.autoSizeColumn(columnIndex);
			}
		}
	}

	public void saveExcel() throws IOException {
		autoSizeColumns();
		FileOutputStream outputStream = new FileOutputStream(filePath);
		workbook.write(outputStream);
		workbook.close();
	}

	public void writeError(String sheetName, int rowNumber, int columnNumber,
			String errorMsg) {

		Row row = sheet.createRow(this.currRowNum);
		row.createCell(0).setCellValue(this.errorID);
		row.createCell(1).setCellValue(sheetName);
		row.createCell(2).setCellValue(rowNumber);
		row.createCell(3).setCellValue(columnNumber);
		row.createCell(4).setCellValue(errorMsg);

		this.currRowNum++;
		this.errorID++;
	}

}
