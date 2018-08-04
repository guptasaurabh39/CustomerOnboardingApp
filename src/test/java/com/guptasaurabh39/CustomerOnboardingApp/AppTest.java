package com.guptasaurabh39.CustomerOnboardingApp;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import org.testng.Reporter;
import org.testng.annotations.Test;

import io.restassured.response.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guptasaurabh39.CustomerOnboardingApp.config.mapper.Config;
import com.guptasaurabh39.CustomerOnboardingApp.excelOperation.ReadExcel;
import com.guptasaurabh39.CustomerOnboardingApp.excelOperation.WriteExcel;

/**
 * Unit test for simple App.
 */
public class AppTest {

	@Test
	public void test_read_excel() throws JsonParseException, JsonMappingException, IOException {
//		String filePath = "D:/guptasaurabh39_Git/Customer_0002.xlsx";
		String filePath = System.getProperty("inputFilePath");
		System.out.println(filePath);
		Reporter.log(filePath);
		ReadExcel rEx = new ReadExcel(filePath);

		Response res = given().when().get(
				"http://localhost:8080/tenants/002/config");
		ObjectMapper objectMapper = new ObjectMapper();
		Config myConfig = objectMapper.readValue(res.asString(), Config.class);

		int rowCount = rEx.getRowCount(rEx.getSheet(myConfig.getEntities()[0]
				.getEntity().getName().toString()));
		System.out.println(rowCount);

		WriteExcel wEx = new WriteExcel(filePath.replace(".xlsx", "") + "_error.xlsx");
		
		for (int i = 1; i <= rowCount; i++) {
			System.out.println(rEx.getIntegerValue(
					rEx.getSheet(myConfig.getEntities()[0].getEntity().getName()
							.toString()), i, 0));
			System.out.println(rEx.getStringValue(
					rEx.getSheet(myConfig.getEntities()[0].getEntity().getName()
							.toString()), i, 1));
			
			wEx.writeError("customer", i, 0, "Error Message.");
		}
		
		wEx.saveExcel();
		
	}

}
