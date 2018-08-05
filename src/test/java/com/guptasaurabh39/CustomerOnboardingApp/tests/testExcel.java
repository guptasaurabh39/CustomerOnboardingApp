package com.guptasaurabh39.CustomerOnboardingApp.tests;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.ReporterConfig;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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
			Assert.fail("ASSERT : Missing tab/tabs for entties in config is/are"
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
					logger.info("ASSERT : Excel Tab Name = "
							+ currentEntity.getName() + ", Attribute Name = "
							+ currAttrName + " exists.");
				} else {
					wEx.writeError(currentEntity.getName(), 0, 0, "Expected Column Name = " + currAttrName + " not available in excel file.");
					logger.error("ERROR : Excel Tab Name = "
							+ currentEntity.getName() + ", Attribute Name = "
							+ currAttrName + " does not exist.");
				}
			}

		}

	}

}
