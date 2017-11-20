package com.capbpm.killme;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.capbpm.react.data.Credential;
import com.capbpm.react.data.Model;
import com.capbpm.react.data.Server;
import com.capbpm.react.util.BPMRestService;

public class IBMBPMDriver {
	public static void main(String args[]) throws Exception
	{
		Server s  = new Server();
		s.setPassword("passw0rd");
		s.setUserId("Danny");
		s.setPort(9443);
		s.setServer("169.48.72.245");
		s.setUsername("Danny");		
		Credential c = new Credential(s);
		
		String sid="2064.d38174ae-5453-4e1b-b002-a2879578ce28";
		String oid="12.e233a358-c404-47b4-9d5f-d6ac9ac7cae5";		
		
		Model v = getVars(c,oid,sid);
		System.out.println(v.toJSON());
		
	};
	
	public static Model getVars(Credential c, String oid, String sid) throws Exception{

		org.json.simple.JSONObject json = go(c,oid,sid);
		
		String name = json.get("name").toString();
		String type = json.get("type").toString();
		boolean isComplex = json.get("isComplex").toString().equalsIgnoreCase("true");

		org.json.simple.JSONArray properties = (org.json.simple.JSONArray)json.get("properties");
		
		Model v = new Model(name,name,type,isComplex);
		v.setOrder(0);
		
		for (int i=0;i<properties.size();i++)
		{
			org.json.simple.JSONObject currProp = (JSONObject) properties.get(i);
			Model currentVar = new Model();
			currentVar.setLabel((currProp.get("name").toString()));
			currentVar.setName(currProp.get("name").toString());
			currentVar.setType(currProp.get("typeClass").toString());
			boolean isA = false;
			if (currProp.get("isArray") !=null && currProp.get("isArray").toString()!=null)
			{
				isA = currProp.get("isArray").toString().equalsIgnoreCase("true");			
			}
			currentVar.setArray(isA);
			v.addChild(currentVar);
			
		}
		
		return v;
			
	}
	public static org.json.simple.JSONObject go(Credential c, String oid, String sid) throws Exception
	{
	
		String url = buildObjURL(oid,sid);	
		BPMRestService b = new BPMRestService(c);
		String str = b.GET(url);
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json =  (org.json.simple.JSONObject) parser.parse(str);
		 json = (JSONObject) json.get("data");
		return json;
	}

	private static String buildObjURL(String oid, String sid) {
		String retval = "/rest/bpm/wle/v1/businessobject/"+oid+"?snapshotId="+sid;
		
		return retval;
	}
}
