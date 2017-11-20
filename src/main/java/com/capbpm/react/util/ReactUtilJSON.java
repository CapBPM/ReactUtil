package com.capbpm.react.util;

import java.util.List;

import com.capbpm.react.data.Constants;
import com.capbpm.react.data.Model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReactUtilJSON {

	public static void createPageAndContent(String pageLabel, String name, Model model, String[] links,
			boolean isDefaultPage) throws Exception {

		String json = model.toJSON();

		// create form
		String form = createReactForm(name, json, model);

		// create page
		String page = createReactPage(pageLabel, name, links, isDefaultPage);
		// create error messages
		createReactMsg();

		// print out the page
		System.out.println(page);

	}

	public static void createPageAndContent(String pageLabel, String name, String json, String[] links,boolean isDefaultPage) throws Exception {

		Model m = Model.initFromJSON(json);
		createPageAndContent(pageLabel, name, json, links, m, isDefaultPage);

	}

	public static void createPageAndContent(String pageLabel, String name, String json, String[] links, Model m,
			boolean isDefaultPage) throws Exception {

		String form = createReactForm(name, json, m);

		// create page
		String page = createReactPage(pageLabel, name, links, isDefaultPage);
		// create error messages
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
		String[] field3 = { "FirstName", "username", "can't be empty", "EN" };
		String[] field4 = { "LastName", "username", "can't be empty", "EN" };

		// String[] field2 = { "password", "password", "can't be empty", "EN" };
		String[][] fields = { field1, field2, field3, field4 };
		// create form
		String form = createReactForm("Login", data, fields);

		// create page
		String page = createReactPage("Login", null, null, false);

		// create error messages
		createReactMsg();

		// print out the page
		System.out.println(page);

	}

	private static String createReactPage(String label, String name, String[] linkedPages, boolean isDefaultPage)
			throws Exception {
		String retval = "";

		String content = readFile(Constants.BASE_PATH + Constants.REACT_PAGE_FILE_PATH);
		String linkChain = "";

		if (linkedPages != null) {
			for (String currLink : linkedPages) {
				linkChain += buildLink(currLink);
			}
		}

		retval = content.replaceAll("@PageLabel@", label);
		retval = retval.replaceAll("@name@", name);
		retval = retval.replaceAll("@ButtonLabel@", "Submit");
		

		FileUtil.insertContentIntoFile(Constants.PAGES_PATH + "\\" + name + "Page.js", retval);

		if (linkChain != null & !linkChain.equals("")) {
			FileUtil.insertIntoFileAtMarker(Constants.PAGES_PATH + "\\" + name + "Page.js", "</div>", linkChain, true);
		}

		createPageEntryImport(name);
		String path = name;
		if (isDefaultPage) {
			path = "";
		}
		createPageEntryRouting(path, name);

		return retval;

	}

	private static String createReactMsg() throws Exception {
		String retval = "";

		String content = readFile(Constants.BASE_PATH + Constants.REACT_MSG_INLINE);
		FileUtil.insertContentIntoFile(Constants.MSG_PATH + Constants.REACT_MSG_INLINE, content);

		return retval;

	}

	private static String createReactForm(String name, String json, Model root) throws Exception {
		String retval = "";
		if (json == null || json.equals("")) {
			json = "{}";
		}

		String content = readFile(Constants.BASE_PATH + Constants.REACT_FORM_FILE_PATH);

		retval = content.replaceAll("@name@", name);
		retval = retval.replaceAll("@data@", json);

		// create fields
		String f1 = "";
		String err = "";
		int indx = 0;
		List<Model> vars = root.getChildren();
		for (Model currentVar : vars) {
			indx++;
			f1 += createFormElement(currentVar.getName(), currentVar.getName());
			err = createErrorMessages(currentVar.getName(), currentVar.getName(), "can't be empty", "EN");
			if (indx > 0) {
				f1 += "\n\t";
			}

		}

		retval = retval.replace("@FormFields@", f1);
		retval = retval.replace("@error@", err);

		// print out the form
		System.out.println(retval);
		FileUtil.insertContentIntoFile(Constants.FORMS_PATH + "\\" + name + "Form.js", retval);

		return retval;

	}

	private static String createReactForm(String name, String json, String[][] fields) throws Exception {
		String retval = "";
		if (json == null || json.equals("")) {
			json = "{}";
		}

		String content = readFile(Constants.BASE_PATH + Constants.REACT_FORM_FILE_PATH);

		retval = content.replaceAll("@name@", name);
		retval = retval.replaceAll("@data@", json);

		// create fields
		String f1 = "";
		String err = "";
		for (int i = 0; i < fields.length; i++) {
			f1 += createFormElement(fields[i][0], fields[i][1]);
			err = createErrorMessages(fields[i][0], fields[i][1], fields[i][2], fields[i][3]);
			if (i > 0) {
				f1 += "\n\t";
			}

		}

		retval = retval.replace("@FormFields@", f1);
		retval = retval.replace("@error@", err);

		// print out the form
		System.out.println(retval);
		FileUtil.insertContentIntoFile(Constants.FORMS_PATH + "\\" + name + "Form.js", retval);

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

	private static String createPageEntryImport(String name) throws Exception {
		String retval = "";
		retval = "import " + name + " from \"./components/pages/" + name + "Page\";\n";

		FileUtil.insertIntoFileAtMarker(Constants.MY_APP, "//END PAGES IMPORTS HERE", retval, true);
		return retval;

	}

	private static String createPageEntryRouting(String name) throws Exception {
		return createPageEntryRouting(name, name + "Page");
	}

	private static String createPageEntryRouting(String path, String name) throws Exception {
		String retval = "";
		retval = "<Route path=\"/" + path + "\" exact component={" + name + "} />\n";
		FileUtil.insertIntoFileAtMarker(Constants.MY_APP, "</div>", retval, true);
		return retval;
	}

	public static String buildLink(String name) {
		return buildLink(name, name);
	}

	public static String buildLink(String name, String url) {
		String retval = "";
		if (name == null || name.equals("")) {
			return retval;
		}

		retval = "\t<Link to=\"/" + name + "\">" + name + "</Link><br/>";
		return retval;
	}

}
