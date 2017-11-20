package com.capbpm.react;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.capbpm.*;
import com.capbpm.react.data.Activity;
import com.capbpm.react.data.BPD;
import com.capbpm.react.data.Credential;
import com.capbpm.react.data.Model;
import com.capbpm.react.data.Server;
import com.capbpm.react.util.BPMRestService;
import com.capbpm.react.util.JSONUtil;

public class IBMBPMController {

	private static boolean isTest = false;

	private static String getFromFile(String filePath) throws FileNotFoundException, IOException {
		File f = new File(filePath);
		FileInputStream fis = new FileInputStream(f);
		byte[] tmp = new byte[(int) f.length()];
		fis.read(tmp);
		fis.close();
		return tmp.toString();
	}

	private static void writeToFile(String filePath, String str) throws FileNotFoundException, IOException {
		File f = new File(filePath);
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(str.getBytes());
		fos.flush();
		fos.close();
	}

	public static void main(String args[]) throws Exception {
		Credential c = getCreds();

		String pid = "25.22fd5444-f579-4a2e-9e4f-51e8537a8245";
		String sid = "2064.d38174ae-5453-4e1b-b002-a2879578ce28";
		String serviceId = "1.50c47237-1732-4329-92f8-4a8e08ff3db1";

		BPD bpd = getBPD(c, pid, sid);
		System.out.println("Process name:"+bpd.getName());
		System.out.println("Process inputs:"+bpd.getInputs());
		System.out.println("Process output:"+bpd.getOutputs());
		System.out.println("Process private:"+bpd.getInternal());
		System.out.println("\n\n");
		
		for (Activity a : bpd.getActivities())
		{
			System.out.println("\tactivity:"+a.getName());
			System.out.println("\tAct: input"+a.getInputs());
			System.out.println("\tAct: output"+a.getOutputs());
		}

		// getActivityDataFromService(c,serviceId,sid);

	}

	public static void testGetAllProcesses() throws Exception {
		Credential c = getCreds();
		BPMRestService b = new BPMRestService(c);
		String url = getURLForForAllProcesses();

		String str = b.GET(url);
		System.out.println(str);
	}
	public static BPD getBPD(String pid, String sid) throws Exception{
		return  getBPD(getCreds(),pid,sid);
}
	public static BPD getBPD(Credential c, String pid, String sid) throws Exception {
		// create empty holder variables
		BPD bpd = new BPD();

		//create a list of activities
		List<Activity> activities = new ArrayList<Activity>();

		// call the visual model for the process
		BPMRestService b = new BPMRestService(c);
		String url = getURLForBPDVisualData(pid, sid);
		String str = b.GET(url);

		// parse the response
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = JSONUtil.findChild(json, "data");

		// get BPD level data
		String name = JSONUtil.getValFromJSON(data, "name");
		bpd.setName(name);
		bpd.setProcessAppID(pid);
		bpd.setItemID(pid);
		bpd.setSnapshotID(sid);
		//Get the variables(input, output, and private) for this BPD
		org.json.simple.JSONObject properties = (org.json.simple.JSONObject) data.get("properties");

		//Map each of the BPD variables to Input or Output(we don't care about private yet)
		getVariablesForBPDLevel(bpd, properties,c);
		
		// get activity level data
		org.json.simple.JSONArray items = (org.json.simple.JSONArray) data.get("items");
		for (int i = 0; i < items.size(); i++) {
			org.json.simple.JSONObject currentItem = (org.json.simple.JSONObject) items.get(i);
			Activity a = new Activity();
			a.setSid(sid);
			a.setPoType(JSONUtil.getValFromJSON(currentItem, "poType"));
			a.setPoId(JSONUtil.getValFromJSON(currentItem, "poId"));

			if (a.getPoId() != null && a.getPoType().equals("Service")) {
				a.setPoType(JSONUtil.getValFromJSON(currentItem, "poType"));
				a.setLane(JSONUtil.getValFromJSON(currentItem, "lane"));
				a.setId(JSONUtil.getValFromJSON(currentItem, "id"));
				a.setPoType(JSONUtil.getValFromJSON(currentItem, "poType"));
				a.setName(JSONUtil.getValFromJSON(currentItem, "label"));
				a.setX(Integer.parseInt(JSONUtil.getValFromJSON(currentItem, "x")));
				a.setY(Integer.parseInt(JSONUtil.getValFromJSON(currentItem, "y")));
				a.calcOrder();
				activities.add(a);
			}

		}
		for (Activity a : activities) {
			getActivityDataFromService(c, a);
		}
		bpd.setActivities(activities);

		return bpd;
	}

	public static void getActivityDataFromService(Credential c, Activity activity) throws Exception {

		if (activity==null) return;
			
		if (!activity.getPoType().equals("Service") || activity.getPoId() == null) {
			return;
		}
		String serviceId = activity.getPoId();
		String sid = activity.getSid();
		BPMRestService b = new BPMRestService(c);
		String url = getURLForServiceModel(serviceId, sid);
		String str = b.GET(url);
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = JSONUtil.findChild(json, "data");// (org.json.simple.JSONObject)
																			// json.get("data");

		org.json.simple.JSONObject properties = JSONUtil.findChild(data, "properties");
		org.json.simple.JSONArray variables = JSONUtil.findChildren(properties, "variables");

		List<Model> vars = getServiceLevelVariables(variables);

		for (int i = 0; i < vars.size(); i++) {
			// get the deep, official definition of the variables
			Model currentVal = vars.get(i);
			getDeepDataForModel(c, currentVal);
			// Credential c, String oid, String sid
			if (currentVal.getDirection().equalsIgnoreCase("input")) {
				activity.addInput(currentVal);
			} else if(currentVal.getDirection().equalsIgnoreCase("output")) {
				activity.addOutput(currentVal);
			}
			else
			{
				activity.addInternal(currentVal);
			}

		}
	}

	private static void getDeepDataForModel(Credential c, Model currentVal) throws Exception {
		if (currentVal == null)
			return;
		if (!currentVal.getType().equalsIgnoreCase(Model.STRING) && !currentVal.getType().equalsIgnoreCase(Model.BOOLEAN)
				&& !currentVal.getType().equalsIgnoreCase(Model.INTEGER)
				&& !currentVal.getType().equalsIgnoreCase(Model.DECIMAL)
				&& !currentVal.getType().equalsIgnoreCase(Model.DATE)

		) {
			 getDeepVariableDetails(c, currentVal.getOid(), currentVal.getSid(), currentVal);
		}
	}

	private static Credential getCreds() {
		Server s = new Server();
		s.setPassword("passw0rd");
		s.setUserId("Danny");
		s.setPort(9443);
		s.setServer("169.48.72.245");
		s.setUsername("Danny");

		Credential c = new Credential(s);
		String pid = "25.22fd5444-f579-4a2e-9e4f-51e8537a8245";
		String sid = "2064.d38174ae-5453-4e1b-b002-a2879578ce28";
		String serviceId = "1.50c47237-1732-4329-92f8-4a8e08ff3db1";

		return c;
	}

	/////////////////////////////////////////////////
	private static String getURLForForAllProcesses() {
		return "/rest/bpm/wle/v1/exposed/process";

	}

	private static String getURLForBPDVisualData(String pid, String sid) {
		return "/rest/bpm/wle/v1/visual/processModel/" + pid + "?snapshotId=" + sid + "&parts=all";
	}

	private static String getURLForServiceModel(String serviceId, String sid) {
		return "/rest/bpm/wle/v1/visual/serviceModel/" + serviceId + "?snapshotId=" + sid;

	}

	public static Model getDeepVariableDetails(Credential c, String oid, String sid, Model v) throws Exception {

		String url = buildObjURL(oid, sid);
		BPMRestService b = new BPMRestService(c);
		String str = b.GET(url);
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = JSONUtil.findChild(json, "data");

		String name = JSONUtil.getValFromJSON(data, "name");
		String type = JSONUtil.getValFromJSON(data, "type");
		boolean isComplex = JSONUtil.getValFromJSON(data, "isComplex").equalsIgnoreCase("true");

		org.json.simple.JSONArray properties = JSONUtil.findChildren(data, "properties");

		v.setOrder(0);

		for (int i = 0; i < properties.size(); i++) {
			org.json.simple.JSONObject currProp = (JSONObject) properties.get(i);
			Model currentVar = new Model();
			currentVar.setOrder(i);
			currentVar.setLabel(JSONUtil.getValFromJSON(currProp, "name"));
			currentVar.setName(JSONUtil.getValFromJSON(currProp, "name"));
			currentVar.setType(JSONUtil.getValFromJSON(currProp, "typeClass"));
			boolean isArray = JSONUtil.getValFromJSON(currProp, "isArray").equalsIgnoreCase("true");
			currentVar.setArray(isArray);
			currentVar.setOid(oid);
			currentVar.setSid(sid);
			v.addChild(currentVar);

		}

		return v;

	}

	private static String buildObjURL(String oid, String sid) {
		String retval = "/rest/bpm/wle/v1/businessobject/" + oid + "?snapshotId=" + sid;

		return retval;
	}

	private static List<Model> getServiceLevelVariables(org.json.simple.JSONArray jsonVarArray) {
		List<Model> retval = new ArrayList<Model>();
		if (jsonVarArray == null) {
			return retval;
		}

		for (int i = 0; i < jsonVarArray.size(); i++) {
			try {
				org.json.simple.JSONObject currObj = (JSONObject) jsonVarArray.get(i);
				Model var = new Model();

				var.setDirection(JSONUtil.getValFromJSON(currObj, "type"));
				var.setType(JSONUtil.getValFromJSON(currObj, "poName"));
				var.setOid(JSONUtil.getValFromJSON(currObj, "poId"));
				var.setName(JSONUtil.getValFromJSON(currObj, "name"));
				var.setLabel(JSONUtil.getValFromJSON(currObj, "name"));
				var.setSid(JSONUtil.getValFromJSON(currObj, "snapshotId"));
				var.setArray((JSONUtil.getValFromJSON(currObj, "isList").equals("true")));

				retval.add(var);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return retval;
	}

	private static void getVariablesForBPDLevel(BPD bpd, org.json.simple.JSONObject varData, Credential c) throws Exception {
		if (varData == null) {
			return;
		}
		org.json.simple.JSONArray variables = JSONUtil.findChildren(varData, "variables");
		if (variables != null) {
			if (variables.size() > 0) {

				for (int i = 0; i < variables.size(); i++) {
					org.json.simple.JSONObject cvar = (JSONObject) variables.get(i);
					Model v = new Model();
					v.setLabel(JSONUtil.getValFromJSON(cvar, "name"));
					v.setName(JSONUtil.getValFromJSON(cvar, "name"));
					v.setOid(JSONUtil.getValFromJSON(cvar, "poId"));
					v.setType(JSONUtil.getValFromJSON(cvar, "poName"));
					v.setSid(JSONUtil.getValFromJSON(cvar, "snapshotId"));
					v.setDirection(JSONUtil.getValFromJSON(cvar, "type"));
					
					getDeepDataForModel(c, v);

					if (v.getDirection().equals("input")) {
						bpd.addInput(v);
					}

					if (v.getDirection().equals("output")) {
						bpd.addOutput(v);
					} else if (!v.getDirection().equals("input")) {
						bpd.addInternal(v);
					}

				}
			}

		}

	}

}
