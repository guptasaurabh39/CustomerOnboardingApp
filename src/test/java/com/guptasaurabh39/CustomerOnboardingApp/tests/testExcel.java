package com.guptasaurabh39.CustomerOnboardingApp.tests;

import static io.restassured.RestAssured.given;

import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guptasaurabh39.CustomerOnboardingApp.config.mapper.Attribute;
import com.guptasaurabh39.CustomerOnboardingApp.config.mapper.Config;
import com.guptasaurabh39.CustomerOnboardingApp.config.mapper.Entity;
import com.guptasaurabh39.CustomerOnboardingApp.excelOperation.ReadExcel;
import com.guptasaurabh39.CustomerOnboardingApp.excelOperation.WriteExcel;

import io.restassured.response.Response;

public class testExcel {

	String inputFilePath;
	String outputFilePath;
	String tenantId;
	Config myConfig;

	final static Logger logger = Logger.getLogger(testExcel.class);

	ReadExcel rEx;
	WriteExcel wEx;

	@AfterClass
	public void atLast() throws IOException {
		wEx.saveExcel();
	}

	@BeforeClass(description = "Setup steps to run test on Excel file based on config.")
	public void getConfig() throws JsonParseException, JsonMappingException,
			IOException {

		DOMConfigurator.configure("log4j.xml");

		inputFilePath = System.getProperty("inputFilePath");
		logger.info("SETUP : Input File path = '" + inputFilePath + "'");
		rEx = new ReadExcel(inputFilePath);

		outputFilePath = System.getProperty("outputFile");
		if (outputFilePath == null) {
			outputFilePath = inputFilePath.replace(".xlsx", "_Err.xlsx");
		}
		logger.info("SETUP : Output File path = '" + outputFilePath + "'");
		wEx = new WriteExcel(outputFilePath);

		tenantId = System.getProperty("tenantId");
		logger.info("SETUP : Tenant ID = " + tenantId);

		Response res = given().when().get(
				"http://localhost:8080/tenants/" + tenantId + "/config");
		ObjectMapper objectMapper = new ObjectMapper();
		myConfig = objectMapper.readValue(res.asString(), Config.class);

	}

	public boolean equalLists(List<String> a, List<String> b) {
		if (a == null && b == null)
			return true;

		if ((a == null && b != null) || (a != null && b == null)
				|| (a.size() != b.size())) {
			return false;
		}

		// Sort and compare the two lists
		Collections.sort(a);
		Collections.sort(b);
		return a.equals(b);
	}

	@Test(enabled = true, priority = 1, description = "Number and name of tabs in excel file should be same as in config received.")
	public void test_TabNames() {
		logger.info("TEST-SCENARIO : VALLIDATING TAB NAME WITH ENTTIES IN CONFIG JSON FOR TENANT = "
				+ tenantId);
		List<String> configEntities = new ArrayList<String>();
		int entitiesCount = myConfig.getEntities().length;
		for (int i = 0; i < entitiesCount; i++) {
			configEntities.add(myConfig.getEntities()[i].getEntity().getName());
		}

		String missingTabs = "";

		boolean flgHaveAllEnities = true;
		Iterator<String> iterator = configEntities.iterator();
		String currEntityName;

		while (iterator.hasNext()) {
			currEntityName = iterator.next();
			if (!rEx.isSheetNameAvailable(currEntityName)) {
				flgHaveAllEnities = false;
				missingTabs = missingTabs + ", " + currEntityName;
				wEx.writeError("NA", 0, 0, "Missing tab for entity, "
						+ currEntityName);
			}
		}

		if (!flgHaveAllEnities) {
			logger.info("INFO : Missing tab/tabs for entties in config is/are"
					+ missingTabs);
		}
	}

	@Test(enabled = true, priority = 2, dependsOnMethods = { "test_TabNames" }, description = "Excel column names validation.")
	public void test_ExcelColumnHeader() {
		logger.info("TEST-SCENARIO : VALLIDATING EXCEL COLUMN HEADER FOR TENANT = "
				+ tenantId);

		int currEntityAttrCnt;
		String currAttrName;
		Entity currentEntity;
		// Get the entities.
		int entitiesCount = myConfig.getEntities().length;

		for (int i = 0; i < entitiesCount; i++) {
			currentEntity = myConfig.getEntities()[i].getEntity();
			logger.debug("DEBUG : Entity Name = " + currentEntity.getName());
			currEntityAttrCnt = currentEntity.getAttributes().length;
			for (int j = 0; j < currEntityAttrCnt; j++) {
				currAttrName = currentEntity.getAttributes()[j].getAttribute()
						.getName();
				logger.debug("DEBUG : Entity Attribute Name = " + currAttrName);
				if (rEx.isColumnHeaderAvailable(currentEntity.getName(),
						currAttrName)) {
					logger.debug("INFO : Excel Tab Name = "
							+ currentEntity.getName() + ", Attribute Name = "
							+ currAttrName + " exists.");
				} else {
					wEx.writeError(currentEntity.getName(), 0, 0,
							"Expected Column Name = " + currAttrName
									+ " not available in excel file.");
					logger.error("ERROR : Excel Tab Name = "
							+ currentEntity.getName() + ", Attribute Name = "
							+ currAttrName + " does not exist.");
				}
			}

		}

	}

	@SuppressWarnings("deprecation")
	@Test(enabled = true, priority = 3, dependsOnMethods = { "test_ExcelColumnHeader" }, description = "Excel column DataType test.")
	public void test_ExcelColumnDataType() {
		logger.info("TEST-SCENARIO : VALIDATE EXCEL COLUMN DATA-TYPE FOR TENANT = "
				+ tenantId);
		Entity currEntity;
		String currEntityName;
		Attribute currAttribute;
		String currAttrDataType;
		String currAttrName;
		int colNumForAttr;
		int dataRowCnt;

		// For each Entity
		for (int i = 0; i < myConfig.getEntities().length; i++) {
			currEntity = myConfig.getEntities()[i].getEntity();
			currEntityName = myConfig.getEntities()[i].getEntity().getName();
			// For Each Attribute in Current Entity.
			for (int j = 0; j < currEntity.getAttributes().length; j++) {
				currAttribute = currEntity.getAttributes()[j].getAttribute();
				currAttrDataType = currAttribute.getDataType();
				currAttrName = currAttribute.getName();
				dataRowCnt = rEx.getRowCount(rEx.getSheet(currEntityName));
				colNumForAttr = rEx.getColNumForHeader(currEntityName,
						currAttrName);
				// For Each Row for Current Attribute.
				for (int rowNum = 1; rowNum <= dataRowCnt; rowNum++) {
					// IF Cell is Not Empty.
					if (!(rEx.getSheet(currEntityName).getRow(rowNum).getCell(colNumForAttr, Row.RETURN_BLANK_AS_NULL) == null)) {
						// IF Expected DataType is Integer
						if (currAttrDataType.equalsIgnoreCase("Integer")) {
							if (rEx.isCellValueNumeric(
									rEx.getSheet(currEntityName), rowNum,
									colNumForAttr)) {
								logger.debug("INFO : " + currEntityName + ">>"
										+ currAttrName + ">>" + rowNum
										+ " is an Integer value.");
							} else {
								logger.error("ERROR : " + currEntityName + ">>"
										+ currAttrName + ">>" + rowNum
										+ " is not a Integer value.");
								wEx.writeError(currEntityName, rowNum,
										colNumForAttr,
										"Expected DataType is Integer.");
							}
						}
						// IF Expected DataType is String
						else if (currAttrDataType.equalsIgnoreCase("String")) {
							if (rEx.isCellValueString(
									rEx.getSheet(currEntityName), rowNum,
									colNumForAttr)) {
								logger.debug("INFO : " + currEntityName + ">>"
										+ currAttrName + ">>" + rowNum
										+ " is a String value.");
							} else {
								logger.error("ERROR : " + currEntityName + ">>"
										+ currAttrName + ">>" + rowNum
										+ " is not a String value.");
								wEx.writeError(currEntityName, rowNum,
										colNumForAttr,
										"Expected DataType is String.");
							}
						}
					}
				}

			}
		}
	}
}
