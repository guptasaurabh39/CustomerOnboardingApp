# QA Manager Assignment

Problem- Customer Onboarding Application.
Stack – React, Java, Spring, Hibernate, Oracle, Mongo
Deployment – SAAS.
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

Hosted an local API for tenant configuration with "http://localhost:8080/tenants/002/config" as below

```
{
<br>entities: [
<br>{
entity: {
name: "customer",
attributes: [
{
attribute: {
id: "1",
name: "customerId",
dataType: "Integer",
parentAttributeId: "",
nullable: "false"
}
},
{
attribute: {
id: "2",
name: "customerName",
dataType: "String",
parentAttributeId: "",
nullable: "false"
}
}
]
}
},
{
entity: {
name: "address",
attributes: [
{
attribute: {
id: "3",
name: "customerId",
dataType: "Integer",
parentAttributeId: "1",
nullable: "false"
}
},
{
attribute: {
id: "4",
name: "addressLine",
dataType: "String",
parentAttributeId: "",
nullable: "false"
}
},
{
attribute: {
id: "5",
name: "addressCity",
dataType: "String",
parentAttributeId: "",
nullable: "true"
}
},
{
attribute: {
id: "6",
name: "addressState",
dataType: "String",
parentAttributeId: "",
nullable: "true"
}
},
{
attribute: {
id: "7",
name: "addressZip",
dataType: "Integer",
parentAttributeId: "",
nullable: "true"
}
},
{
attribute: {
id: "8",
name: "addressCountry",
dataType: "String",
parentAttributeId: "",
nullable: "true"
}
}
]
}
}
]
}
```

I am using WireMock-1.57-standalone.jar for hosting this API.