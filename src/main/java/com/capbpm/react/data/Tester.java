package com.capbpm.react.data;

import org.json.JSONObject;

import com.capbpm.killme.JSONData;
import com.capbpm.spring.PageController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Tester {

	
///////////////////////////////////////////
	public static void testModel(String[] args)/*main(String[] args)*/ throws Exception
	{	
		Model m = new Model("email", "email", Model.STRING, false);
		//m.setValue("bob@capbpm.com");
		m.addChild(new Model("username"));
		m.addChild(new Model("password"));
		
		/*for (int i=0;i<10;i++)
		{
			Model kid = new Model("label"+i,"name"+i);
			m.addChild(kid);
			
		}*/
		
		JSONObject tmp = m.toJSONObject();
		String json=tmp.toString();	
		Model x = Model.initFromJSON(json);
		System.out.println(json);
		
		
	}
///////////////////////////////////////////
	public static void main(String args[])/*testPage()/* */throws Exception
	{
		String pid = "25.22fd5444-f579-4a2e-9e4f-51e8537a8245";
		String sid = "2064.d38174ae-5453-4e1b-b002-a2879578ce28";
		String serviceId = "1.50c47237-1732-4329-92f8-4a8e08ff3db1";
		
		new PageController().buildPagesForBPD(pid,sid);
		
	}
	
///////////////////////////////////////////	
	public static void JSONData(String[] args) throws Exception
	{	
		JSONData jd = new JSONData();
		jd.setName("root1");
		jd.setLabel("label_root");;
		jd.setErrorMsg("root err");;
		jd.setLang("EN");;

		for (int i=0;i<10;i++)
		{
			JSONData current = new JSONData();
			current.setName("name" +i);
			current.setLabel("label" +i);;
			current.setErrorMsg(" err" +i);;
			current.setLang("EN");
			
			jd.getChildren().add(current);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		String jsonString = mapper.writeValueAsString(jd);
		System.out.println(jsonString);
	}
}
