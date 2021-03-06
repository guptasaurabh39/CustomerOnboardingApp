### Customer Onboarding Application.
#### Stack – React, Java, Spring, Hibernate, Oracle, Mongo
#### Deployment – SAAS.
The customer Onboarding Application enable customization and a tenant who buys this application can customize the fields to be captured, the validations to be performed, number of views, sub Views, and the workflow for approval. The entire configuration is stored as json in Mongo. When a user logs in based on this configuration dynamic views are presented so that the Customers can be onboarded.
Majority of the Validations are applied in the UI from the configuration. Some Tenants already have customers onboarded when they join our platform and would want them migrated to our environment. For this we have provided an Excel template which can be uploaded. We need to support 1000 tenants uploading 50000 customers simultaneously for this upload. 
Consider there is an API called /customers/upload (POST/MULTIPART)  and this receives the excel file
Another API /customers (POST/JSON) creates one customer
Another API /tenants/{id}/config (GET/JSON) gets the config file which contains the entities, fields, datatypes, relations.
For Example: Customer is an Entity, Address could be another Entity. These entities have a relationship – Customer can have multiple addresses. Etc
Now the excel template will have one sheet per Entity.
In case there is an issue in creation an error excel is created by /upload api that has error message against the row where error occurred. 

As the QA Manager, you need to provide a design, build a framework, and layout a plan for ensuring this product is tested. We would also like to be able to sell Testing suits to customers so they can test whether the template provided matches their configuration and the different error conditions that may arise. We also want to test that the API is scalable enough to support the expected NFRs.
Delivery: 
1)	Test Architecture
2)	Frameworks to be used
3)	Test Plan 
a.	Types of testing to be covered
b.	Identify challenges and how they will be mitigated
c.	No of people needed and their skills
d.	How the dynamic screens would be handled
i.	When testing generically Developers code
ii.	When testing a client configuration
e.	How to prove the testing plan is adequate
4)	 Working code for testing the template upload
a.	Should test for scalability
b.	You need to ensure that data has gone through same set of validations as UI
c.	Providing a pipeline would be a plus (consider this will be executed in a grid as part of pipeline) 
5)	You would need to Mock the APIs.
6)	Please ensure that the coding conventions, directory structure and build approach of your project follow the conventions set by popular open source projects.
7)	Please use Git for version control. We expect you to send us a standard zip or tarball of your source code when you're done that includes Git metadata (the .git folder) in the tarball so we can look at your commit logs and understand how your solution evolved. Frequent commits are a huge plus.
8)	Please do not check in class files, jars or other libraries or output from the build process. Use maven for build automation and dependency. 
9)	Please write comprehensive unit tests/specs.





## Assumpitons and Prerequisites
1. I am using <b>WireMock-1.57-standalone.jar</b> for hosting this API http://www.mocky.io/v2/5b675f4e320000bb04ee1277
	- Hosted an local API for tenant configuration with "http://localhost:8080/tenants/002/config". 
2. Sample JOSN response I have hosted on my localhost as below:
	- This is Sample JSON Configuration for [Tenant 001], having 3 entities as customer, address and office.(SampleConfig_001.json)
	- This is Sample JSON Configuration for [Tenant 002], having 2 entities as customer and address.(SampleConfig_002.json)
3. Sample input excel files.
	- Input excel file for [Tenant 001]. (Customer_0001.xlsx)
	- Input excel file for [Tenant 002]. (Customer_0002.xlsx)

## Automation Design and Architecture
![Alt text](Detailed_Arch.png?raw=true "Detailed Architecture")

## How to Use
Application accept 3 parameters:
 - TenantID
 - Input Excel file complete path.
 - Output Excel file complete path. [Optional]

```
mvn clean compile test -DtenantID="001" -DinputFile="D:/guptasaurabh39_Git/Customer_0001.xlsx" -DinputFile="D:/guptasaurabh39_Git/Customer_0001.xlsx"
mvn clean compile test -DtenantID="001" -DinputFile="D:/guptasaurabh39_Git/Customer_0001.xlsx"
```

## Sample Console Logs
```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running TestSuite
log4j: reset attribute= "false".
log4j: Threshold ="null".
log4j: Level value for root is  [INFO].
log4j: root level set to INFO
log4j: Class name: [org.apache.log4j.ConsoleAppender]
log4j: Parsing layout of class: "org.apache.log4j.PatternLayout"
log4j: Setting property [conversionPattern] to [%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n].
log4j: Adding appender named [console] to category [root].
2018-08-07 02:35:12 INFO  testExcel:89 - SETUP : Input File path = 'D:/guptasaurabh39_Git/Customer_0002.xlsx'
2018-08-07 02:35:13 INFO  testExcel:96 - SETUP : Output File path = 'D:/guptasaurabh39_Git/Customer_0002_Err.xlsx'
2018-08-07 02:35:13 INFO  testExcel:100 - SETUP : Tenant ID = 002
2018-08-07 02:35:15 INFO  testExcel:114 - TEST-SCENARIO : VALLIDATING TAB NAME WITH ENTTIES IN CONFIG JSON FOR TENANT = 002
2018-08-07 02:35:15 INFO  testExcel:146 - TEST-SCENARIO : VALLIDATING EXCEL COLUMN HEADER FOR TENANT = 002
2018-08-07 02:35:15 INFO  testExcel:185 - TEST-SCENARIO : VALIDATE EXCEL COLUMN DATA-TYPE FOR TENANT = 002
2018-08-07 02:35:15 INFO  testExcel:255 - TEST-SCENARIO : VALIDATE EXCEL PARENT ATTRIBUTE FOR TENANT = 002
2018-08-07 02:35:15 ERROR testExcel:320 - ERROR : Entity[address]>>Attribute[customerId]>>RowNumber[3] is not a valid value.
2018-08-07 02:35:15 ERROR testExcel:320 - ERROR : Entity[address]>>Attribute[customerId]>>RowNumber[6] is not a valid value.
2018-08-07 02:35:15 ERROR testExcel:320 - ERROR : Entity[address]>>Attribute[customerId]>>RowNumber[8] is not a valid value.
2018-08-07 02:35:15 ERROR testExcel:320 - ERROR : Entity[address]>>Attribute[customerId]>>RowNumber[9] is not a valid value.
2018-08-07 02:35:15 INFO  testExcel:344 - TEST-SCENARIO : VALIDATE EXCEL NULLABLE ATTRIBUTE FOR TENANT = 002
2018-08-07 02:35:15 INFO  testExcel:346 - INFO : STILL TO BE IMPLEMENTED.................
2018-08-07 02:35:15 ERROR testExcel:378 - ERROR : customer>>customerId>>3 should not be BLANK/EMPTY.
2018-08-07 02:35:15 ERROR testExcel:378 - ERROR : customer>>customerName>>2 should not be BLANK/EMPTY.
2018-08-07 02:35:15 ERROR testExcel:378 - ERROR : address>>customerId>>4 should not be BLANK/EMPTY.
2018-08-07 02:35:15 ERROR testExcel:378 - ERROR : address>>addressLine>>3 should not be BLANK/EMPTY.
2018-08-07 02:35:15 INFO  testExcel:74 - INFO : Error Excel file saved at D:/guptasaurabh39_Git/Customer_0002_Err.xlsx
```