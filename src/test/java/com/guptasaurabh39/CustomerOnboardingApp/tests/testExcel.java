package com.guptasaurabh39.CustomerOnboardingApp.tests;

import static io.restassured.RestAssured.given;

import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.beust.jcommander.Parameter;
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

	private boolean isAvailableInList(List<String> lstMtchPrntIDs,
			String strValue) {
		Iterator<String> iterator = lstMtchPrntIDs.iterator();
		boolean flgFound = false;
		while (iterator.hasNext()) {
			if (iterator.next().equalsIgnoreCase(strValue)) {
				flgFound = true;
				break;
			}
		}
		return flgFound;
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

	@AfterClass
	public void atLast() throws IOException {
		logger.info("INFO : Error Excel file saved at " + outputFilePath);
		wEx.saveExcel();
	}

	@BeforeClass(description = "Setup steps to run test on Excel file based on config.")
	public void getConfig() throws JsonParseException, JsonMappingException,
			IOException {

		inputFilePath = "D:/guptasaurabh39_Git/Customer_0002.xlsx";
		outputFilePath = "D:/guptasaurabh39_Git/Customer_0002_Err.xlsx";
		tenantId = "002";

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
		logger.debug("DEBUG : Config JSON for Tenant ID = " + tenantId
				+ ">>>>>>>>>>>>>>>>>>>>>>>>>>.");
		logger.debug(res.asString());
		ObjectMapper objectMapper = new ObjectMapper();
		myConfig = objectMapper.readValue(res.asString(), Config.class);

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
					logger.debug("DEBUG : Excel Tab Name = "
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
	@Test(enabled = true, priority = 3, dependsOnMethods = { "test_ExcelColumnHeader" }, description = "Excel column DataType Validation.")
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
					if (!(rEx.getSheet(currEntityName).getRow(rowNum)
							.getCell(colNumForAttr, Row.RETURN_BLANK_AS_NULL) == null)) {
						// IF Expected DataType is Integer
						if (currAttrDataType.equalsIgnoreCase("Integer")) {
							if (rEx.isCellValueNumeric(
									rEx.getSheet(currEntityName), rowNum,
									colNumForAttr)) {
								logger.debug("DEBUG : " + currEntityName + ">>"
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
								logger.debug("DEBUG : " + currEntityName + ">>"
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

	@Test(enabled = true, priority = 4, dependsOnMethods = { "test_ExcelColumnDataType" }, description = "Excel Parent Attribute Validation.")
	public void test_ExcelParentAttribute() {
		logger.info("TEST-SCENARIO : VALIDATE EXCEL PARENT ATTRIBUTE FOR TENANT = "
				+ tenantId);
		Entity srcEntity;
		Entity mtchEntity;

		String srcEntityName;
		String mtchEntityName;

		Attribute srcAttribute;
		Attribute mtchAttribute;

		String srcParentAttributeId;
		String srcPrntId;
		String srcAttrName;
		int srcColNum;

		String mtchAttrId;
		String mtchAttrName;

		int colNumForAttr;
		int dataRowCnt;

		// Scan all Entity Attributes for Parent Attribute
		// For each Entity
		for (int i = 0; i < myConfig.getEntities().length; i++) {
			srcEntity = myConfig.getEntities()[i].getEntity();
			srcEntityName = myConfig.getEntities()[i].getEntity().getName();
			// For Each Attribute in Current Entity.
			for (int j = 0; j < srcEntity.getAttributes().length; j++) {
				srcAttribute = srcEntity.getAttributes()[j].getAttribute();
				srcAttrName = srcAttribute.getName();
				srcColNum = rEx.getColNumForHeader(srcEntityName, srcAttrName);
				srcParentAttributeId = srcAttribute.getParentAttributeId();
				if (!(srcParentAttributeId.equals("") || srcParentAttributeId == null)) {
					// Find the Parent Entity and Attribute
					for (int prntEntyCnt = 0; prntEntyCnt < myConfig
							.getEntities().length; prntEntyCnt++) {
						mtchEntity = myConfig.getEntities()[prntEntyCnt]
								.getEntity();
						for (int prntAtrCnt = 0; prntAtrCnt < mtchEntity
								.getAttributes().length; prntAtrCnt++) {
							mtchAttribute = mtchEntity.getAttributes()[prntAtrCnt]
									.getAttribute();
							mtchAttrId = mtchAttribute.getId();
							if (mtchAttrId.equals(srcParentAttributeId)) {
								mtchAttrName = mtchAttribute.getName();
								mtchEntityName = mtchEntity.getName();
								// GET SOURCE ID LIST
								List<String> lstSrcPrntIDs = rEx
										.getDataListByColumnName(srcEntityName,
												srcAttrName);
								logger.debug("DEBUG : SOURCE : Data List : "
										+ lstSrcPrntIDs.toString());
								// GET MATCHING ID LIST
								List<String> lstMtchPrntIDs = rEx
										.getDataListByColumnName(
												mtchEntity.getName(),
												mtchAttrName);
								logger.debug("DEBUG : PARENT : Data List : "
										+ lstMtchPrntIDs.toString());
								// REPORT ERROR IF EXISTS
								for (int idCnt = 0; idCnt < lstSrcPrntIDs
										.size(); idCnt++) {
									if (!(isAvailableInList(lstMtchPrntIDs,
											lstSrcPrntIDs.get(idCnt)))) {
										logger.error("ERROR : Entity[" + srcEntityName
												+ "]>>Attribute[" + srcAttrName + "]>>RowNumber["
												+ (idCnt + 2)
												+ "] is not a valid value.");
										wEx.writeError(srcEntityName,
												idCnt + 2, srcColNum + 1,
												"This is not a valid value, Please correct. This is foreign key of "
														+ mtchEntityName + ">>"
														+ mtchAttrName);
									}
								}

							}
						}
					}
				}
			}
		}

	}
	
	
	@Test(enabled = true, priority = 5, dependsOnMethods = { "test_ExcelParentAttribute" }, description = "Excel Attribute Nullable Validation.")
	public void test_ExcelNullableAttribute() {
		logger.info("TEST-SCENARIO : VALIDATE EXCEL NULLABLE ATTRIBUTE FOR TENANT = " + tenantId);
		logger.info("INFO : STILL TO BE IMPLEMENTED.................");
	}
	

}