package com.guptasaurabh39.CustomerOnboardingApp.excelOperation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ApachePOIExcelWrite {

	private static final String FILE_NAME = "D:/guptasaurabh39_Git/Customer_0002.xlsx";

	public static void main(String[] args) {
		ReadExcel rEx = new ReadExcel(FILE_NAME);
		List<String> lstData = rEx.getDataListByColumnName("address", "customerId");
		for(String s:lstData){
			System.out.println(s.toString());
		}
	}
	
	/*public static void main(String[] args) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
		Object[][] datatypes = { { "Datatype", "Type", "Size(in bytes)" },
				{ "int", "Primitive", 2 }, { "float", "Primitive", 4 },
				{ "double", "Primitive", 8 }, { "char", "Primitive", 1 },
				{ "String", "Non-Primitive", "No fixed size" } };

		int rowNum = 0;
		System.out.println("Creating excel");

		for (Object[] datatype : datatypes) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : datatype) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				}
			}
		}

		Row newRow = sheet.createRow(10);
		newRow.createCell(0).setCellValue("String 1");
		newRow.createCell(1).setCellValue(111);

		try {
			FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}*/
}