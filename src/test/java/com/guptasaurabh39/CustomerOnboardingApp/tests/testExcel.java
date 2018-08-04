package com.guptasaurabh39.CustomerOnboardingApp.tests;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.guptasaurabh39.CustomerOnboardingApp.config.mapper.Config;

import io.restassured.response.Response;

public class testExcel {
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException{
		
		Response res = given()
				.when()
				.get("http://localhost:8080/tenants/002/config");
		
//		res.prettyPrint();
		
		System.out.println(res.getBody().jsonPath().getString("entities.entity[1].name"));
		System.out.println(res.getBody().jsonPath().get("entities.entity[1].attributes[0].attribute"));
		
		ObjectMapper objectMapper = new ObjectMapper();
		Config myConfig = objectMapper.readValue(res.asString(), Config.class);
		
		System.out.println(myConfig.getEntities()[0].getEntity().getName().toString());
		System.out.println(myConfig.getEntities()[1].getEntity().getName().toString());
		
	}

}
