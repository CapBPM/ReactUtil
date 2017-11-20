package com.capbpm.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capbpm.react.IBMBPMController;
import com.capbpm.react.data.Activity;
import com.capbpm.react.data.BPD;
import com.capbpm.react.data.Model;
import com.capbpm.react.data.ProcessData;
import com.capbpm.react.util.ReactUtilJSON;

@RestController
public class PageController {

	@RequestMapping(method = RequestMethod.POST, value = "/create/{name}")
	public String createPage(@PathVariable String name, @RequestBody String json) {

		return "";// buildPage(name, json,null);
	}

	@RequestMapping(method = RequestMethod.GET,  value = "/apps")
	public List<ProcessData> getApps() throws Exception {
		
		List<ProcessData> retval = new ArrayList<ProcessData>();
		String pid = "25.22fd5444-f579-4a2e-9e4f-51e8537a8245";
		String sid = "2064.d38174ae-5453-4e1b-b002-a2879578ce28";
		String serviceId = "1.50c47237-1732-4329-92f8-4a8e08ff3db1";

		
		for (int i=0;i<10;i++)
		{
			ProcessData pd = new ProcessData();
			pd.setName("name"+i);
			pd.setPid(pid);
			pd.setSid(sid);
			retval.add(pd);
		}
		
		return retval;
		
		
	
	}
	@RequestMapping(method = RequestMethod.GET, value = "/create/{pid}/{sid}")
	public void buildPagesForBPD(@PathVariable String pid, @PathVariable String sid) throws Exception {
		BPD bpd = IBMBPMController.getBPD(pid, sid);
		String[] actNames = new String[bpd.getActivities().size()];
		for (int i = 0; i < bpd.getActivities().size(); i++) {
			actNames[i] = bpd.getActivities().get(i).getTrimName();
		};

		for (Activity a : bpd.getActivities()) {
			buildPage(a.getName(), a.getTrimName(), a.uniqueModels(), actNames);
		}
		String login = "Login";
		//
		Model root = new Model("Data");
		Model u = new Model("username");
		Model p = new Model("password");
		List<Model> list = new ArrayList<Model>();
		list.add(root);
		
		//buildPage(login, login, list, null);
		
		createLoginPage(null);
	}

	private static void createLoginPage(String[] linkz) throws Exception {
		String login = "Login";
		//
		Model root = new Model("Data");
		Model u = new Model("username");
		Model p = new Model("password");
		root.addChild(u);
		root.addChild(p);
		
		//
		String loginJson =  "{   \"username\":\"\",   \"password\": \"\"}";// root.toJSONObject().toString();

		// create form
		ReactUtilJSON.createPageAndContent(login, login, loginJson, linkz, root,false);

	}

	private String buildPage(String pageLabel, String name, java.util.List<Model> varList, String[] linkedPages) {
		String retval = "http://localhost:3000/" + name;
		try {
			ReactUtilJSON.createPageAndContent(pageLabel, name, varList.get(0), linkedPages, false);
		} catch (Exception e) {
			System.out.println(e);
			retval = e.toString();
		}

		return retval;
	}
}