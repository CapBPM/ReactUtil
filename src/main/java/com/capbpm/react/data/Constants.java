package com.capbpm.react.data;

public interface Constants {

    public static String zipFilePath = "C:\\_dev\\React_Tutorial\\lab1\\bookworm-react.zip";
    public static String destDirectory = "C:\\_dev\\React_Tutorial\\lab1\\target";
	public static String DEF_APP_NAME="bookworm-react";
    public static String NEW_APP_NAME = "my-app";
    public static String targetStem = destDirectory + "\\" + NEW_APP_NAME+"\\" + "src\\"; 
    public static String FORMS_PATH =  targetStem +  "components\\forms\\";
    public static String PAGES_PATH =  targetStem +  "components\\pages\\"; 
    public static String MSG_PATH =  targetStem +  "components\\messages\\";
    public static String MY_APP = targetStem + "App.js";
    
    public static final String BASE_PATH = "C:/Users/max/SpringX1/CapReactUtil/src/main/resources/";
    public static final String REACT_FORM_FIELD_PATH = "FormField.js";
    public static final String REACT_FORM_FILE_PATH = "ReactFormTemplate.js";
    public static final String REACT_PAGE_FILE_PATH = "ReactPageTemplate.js";
    public static final String REACT_CLASS_ERROR_PATH = "Error.js";
    public static final String REACT_CLASS_V_ERROR_PATH = "ValidateError.js";
    public static final String REACT_MSG_INLINE="InlineError.js";
}
