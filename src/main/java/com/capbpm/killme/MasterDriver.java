package com.capbpm.killme;

import com.capbpm.react.util.ReactUtilJSON;

public class MasterDriver {

	public static void main(String args[]) throws Exception
	{
		//UnzipUtil.main(null);
		//ReactUtil.main(null);
		executeAppSoul();
	}
	private static void executeAppSoul() throws Exception {
		
		String login="Login";
		String customer = "Customer";
		String employee = "Employee";
		String jobApplication="JobApplication";
		String linkz[] = {employee,jobApplication};
		String loginJson =  "{   \"username\":\"\",   \"password\": \"\"}";

		String json =  "{   \"name\":\"bob\",   \"age\":45,   \"amount\": 150,   \"creditScore\": 650,   \"income\": 10000,   \"isApproved\": false }";
		String jsonJob="{	\"firstName\": \"Bob\",	\"lastName\": \"Jones\",	\"job\": \"ARCHTIECT\",	\"level\": \"5\",	\"salary\": 60000,	\"approved\": false,	\"hrComments\": \"\"}";
		
		// create form
		//ReactUtilJSON.createPageAndContent(login,login,loginJson,linkz);
		//ReactUtilJSON.go(customer,json,login,jobApplication);
		//ReactUtilJSON.go(employee,json,login,jobApplication);
		//ReactUtilJSON.go(employee,json,login,customer);
		//ReactUtilJSON.go(jobApplication,jsonJob,login,customer);
		

	}
	 
	/*private static void executeAppSoulBack() throws Exception {
		// TODO Auto-generated method stub
		//String CustomerData
		String name = "Customer";
		/*String[] field1 = { "firstName", "firstName", "can't be empty", "EN" };
		String[] field2 = { "lastName", "lastName", "can't be empty", "EN" };
		String[] field3 = { "age", "age", "can't be empty", "EN" };
		String[] field4 = { "cost", "cost", "can't be empty", "EN" };		
		String[] field5 = { "status", "status", "can't be empty", "EN" };
		
		String[] fields = {field1[0],field2[0],field3[0],field4[0],field5[0]};*/

		//String[] myFields = {"firstName","lastName","age","cost","status"};
		
		//String employee = "Employee";
		//String json =  "{   \"name\":\"bob\",   \"age\":45,   \"amount\": 150,   \"creditScore\": 650,   \"income\": 10000,   \"isApproved\": false }";
		// create form
		//ReactUtilJSON.go(employee,json,"Login","Home");

		
	//}*/
}
