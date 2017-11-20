package com.capbpm.react.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.capbpm.react.data.Model;

public class JSONUtil {

	//private static String jsonStr = "{   \"name\":\"bob\",   \"age\":45,   \"amount\": 150,   \"creditScore\": 650,   \"income\": 10000,   \"isApproved\": false }";
private static String jsonStr="{	\"jobApplication\": {		\"firstName\": \"Bob\",		\"lastName\": \"Jones\",		\"job\": \"ARCHTIECT\",		\"level\": \"5\",		\"salary\": 60000,		\"approved\": false,		\"hrComments\": \"\"	}}";
public static void main(String args[]) throws Exception {
		JSONParser parser = new JSONParser();

		JSONObject json = (JSONObject) parser.parse(jsonStr);
		
		List<Model> vars = new ArrayList<Model>();

		
		findVars(json, vars);
		vars.get(0).setType("Decimal");
		vars.get(1).setType("Decimal");
		vars.get(2).setType("Integer");
		vars.get(3).setType("String");
		vars.get(4).setType("Boolean");
		vars.get(5).setType("Date");		
		Model data = new Model("Data");
		data.setChildren(vars);
		

		System.out.println(data.toJSON());

	}

	public static void findKeys(String jsonStr, List<String> keys) throws Exception {

		JSONObject json = (JSONObject) new JSONParser().parse(jsonStr);
		findKeys(json, keys);

	}

	public static void findKeys(JSONObject obj, List<String> keys) {

		List<String> listOfNames = new ArrayList(obj.keySet());

		keys.addAll(listOfNames);
		for (String key : listOfNames) {
			if (obj.get(key).getClass() == JSONObject.class) {
				findKeys((JSONObject) obj.get(key), keys);
			}
		}
	}
	public static void findVars(String jsonStr, List<Model> vList) throws Exception {

		JSONObject json = (JSONObject) new JSONParser().parse(jsonStr);
		findVars(json, vList);

	}
	public static void findVars(JSONObject obj, List<Model> vList) {
		if (vList == null) {
			vList = new ArrayList<Model>();
		}
		List<String> listOfNames = new ArrayList(obj.keySet());
		int startIndex = vList.size();
		for (int i = 0; i < listOfNames.size(); i++) {
			Model currentVar = new Model(listOfNames.get(i));
			currentVar.setOrder(startIndex + i);
			vList.add(currentVar);
		}
		for (String key : listOfNames) {
			if (obj.get(key).getClass() == JSONObject.class) {
				findVars((JSONObject) obj.get(key), vList);
			}
		}

	}
	
	public static String getValFromJSON(org.json.simple.JSONObject currentItem, String key)
	{
		String retval="";
		try
		{
			retval = currentItem.get(key).toString();
		}
		catch(Exception e)
		{
			
		}
		
		return retval;
		
	}
	
	public static org.json.simple.JSONObject findChild(org.json.simple.JSONObject parent, String key)
	{
		org.json.simple.JSONObject retval=null;
		if (parent !=null &&  parent.get(key) !=null && parent.get(key) instanceof org.json.simple.JSONObject)
		{
			retval = (org.json.simple.JSONObject) parent.get(key);
		}
		
		return retval;
		
	}
	public static org.json.simple.JSONArray findChildren(org.json.simple.JSONObject parent, String key)
	{
		org.json.simple.JSONArray retval=null;
		if (parent !=null &&  parent.get(key) !=null && parent.get(key) instanceof org.json.simple.JSONArray)
		{
			retval = (org.json.simple.JSONArray) parent.get(key);
		}
		else if (parent !=null &&  parent.get(key) !=null && parent.get(key) instanceof org.json.simple.JSONObject)
		{
			retval=new org.json.simple.JSONArray();
			retval.add(parent.get(key));
		}
		
		return retval;
		
	}

}
