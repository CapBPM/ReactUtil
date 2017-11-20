package com.capbpm.killme;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.capbpm.react.data.Constants;
import com.capbpm.react.util.FileUtil;

public class ReactUtil {


	public static void go( String name, String[] fields, String linkBack, String linkForward) throws Exception {
		// the data for the form. This is just standin.
		String data = "";
		String fArrray[][]=new String[fields.length][4];
		
		for (int i=0;i<fields.length;i++)
		{			
			String[] tmpArray = {fields[i], fields[i], "can't be empty", "EN" };
			
			if ((i + 1 )< (fields.length))
				{
				data +=fields[i]+":"+"'',";
				}
			else
			{
				data +=fields[i]+":"+"''";
			}
			
			
			
			fArrray[i]=tmpArray;
			
		}
		data = "{" + data + "}";

		// create form
		String form = createReactForm(name, data,fArrray);

		// create page
		String page = createReactPage(name, linkBack,  linkForward);
		
		//create error messages
		createReactMsg();

		// print out the page
		System.out.println(page);
		
	}

	

	public static void main(String args[]) throws Exception {
		// the data for the form. This is just standin.
		String data = "{email:'', password:''}";

		// the fields of the data. This is just standin.
		String[] field1 = { "email", "email", "can't be empty", "EN" };
		String[] field2 = { "password", "password", "can't be empty", "EN" };
		String[][] fields = {field1,field2};
		// create form
		String form = createReactForm("Login", data,fields);

		// create page
		String page = createReactPage("Login",null,null);
		
		//create error messages
		createReactMsg();

		// print out the page
		System.out.println(page);
		
	}

	public static String createReactPage(String name, String linkBack, String linkForward) throws Exception {
		String retval = "";

		String content = readFile(Constants.BASE_PATH + Constants.REACT_PAGE_FILE_PATH);
		String backLink = buildLink(linkBack);
		String forwardLink = buildLink(linkForward);
		retval = content.replaceAll("@name@", name);
		FileUtil.insertContentIntoFile(Constants.PAGES_PATH +"\\"+name+"Page.js", retval);
		
		if (backLink !=null & !backLink.equals(""))
		{
			FileUtil.insertIntoFileAtMarker(Constants.PAGES_PATH +"\\"+name+"Page.js","</div>",backLink,true);
		}
		if (forwardLink !=null & !forwardLink.equals(""))
		{
			FileUtil.insertIntoFileAtMarker(Constants.PAGES_PATH +"\\"+name+"Page.js","</div>",forwardLink,true);
		}
		
		
		createPageEntryImport(name);
		createPageEntryRouting(name);
		

		return retval;

	}

	
	public static String createReactMsg() throws Exception {
		String retval = "";

		String content = readFile(Constants.BASE_PATH + Constants.REACT_MSG_INLINE);
		FileUtil.insertContentIntoFile( Constants.MSG_PATH + Constants.REACT_MSG_INLINE, content);

		return retval;

	}

	public static String createReactForm(String name, String json, String[][] fields) throws Exception {
		String retval = "";
		if (json == null || json.equals("")) {
			json = "{}";
		}

		String content = readFile(Constants.BASE_PATH + Constants.REACT_FORM_FILE_PATH);

		retval = content.replaceAll("@name@", name);
		retval = retval.replaceAll("@data@", json);

		// create fields
		String f1 ="";
		String err ="";
		for (int i=0; i< fields.length; i++)
		{
			f1 += createFormElement(fields[i][0], fields[i][1]);
			err = createErrorMessages(fields[i][0], fields[i][1], fields[i][2], fields[i][3]);
			if (i>0)
			{
				f1+="\n\t";
			}
			
		}
		
		retval = retval.replace("@FormFields@", f1);
		retval = retval.replace("@error@", err);
		
		// print out the form
		System.out.println(retval);
		FileUtil.insertContentIntoFile(Constants.FORMS_PATH +"\\"+name+"Form.js", retval);

		return retval;

	}

	static String readFile(String path) throws IOException {
		Charset encoding = Charset.defaultCharset();
		return readFile(path, encoding);
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	static String createFormElement(String name, String type) throws Exception {
		String retval = "";

		String content = readFile(Constants.BASE_PATH + Constants.REACT_FORM_FIELD_PATH);

		retval = content.replaceAll("@name@", name);
		retval = retval.replaceAll("@type@", type);

		{

		}

		return retval;

	}

	static String createErrorMessages(String name, String type, String message, String lang) throws Exception {
		String retval = "";

		String content = readFile(Constants.BASE_PATH + Constants.REACT_CLASS_ERROR_PATH);
		if (type == "email") {
			content = readFile(Constants.BASE_PATH + Constants.REACT_CLASS_V_ERROR_PATH);
		}

		retval = content.replaceAll("@name@", name);
		retval = retval.replaceAll("@msg@", message);

		return retval;

	}

	
	public static String createPageEntryImport(String name) throws Exception
	{
		String retval = "";	
		retval = "import "+name+"Page from \"./components/pages/"+name+"Page\";\n";
		
		FileUtil.insertIntoFileAtMarker(Constants.MY_APP,"//END PAGES IMPORTS HERE",retval,true);
		return retval;
	
	}
	public static String createPageEntryRouting(String name) throws Exception
	{
		return createPageEntryRouting(name,name+"Page");
	}
	public static String createPageEntryRouting(String path, String name) throws Exception
	{
		String retval = "";	
		retval = "<Route path=\"/"+path+"\" exact component={"+name+"} />\n";	
		FileUtil.insertIntoFileAtMarker(Constants.MY_APP,"</div>",retval,true);
		return retval;
	}

	public static String buildLink(String name)
	{
		return buildLink(name,name);
	}
	public static String buildLink(String name, String url)
	{
		String retval = "";
		if (name==null || name.equals(""))
		{
			return retval;
		}
			
		retval="\t<Link to=\"/"+name+"\">"+name+"</Link>;\n";
		return retval;
	}
	/*public static String buildLink(String name, String url)
	{
		String retval = "";	
		retval="<Link to=\"/"+name+"\">"+name+"</Link>;\n";
		return retval;
	}*/	
}
