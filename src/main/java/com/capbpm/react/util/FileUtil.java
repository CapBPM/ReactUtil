package com.capbpm.react.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class FileUtil {

	public static void insertContentIntoFile(String path, String content) throws Exception
	{
		File myFoo = new File(path);
		FileOutputStream fooStream = new FileOutputStream(myFoo, false); // true to append
		                                                                 // false to overwrite.
		byte[] myBytes = content.getBytes();
		fooStream.write(myBytes);
		fooStream.close();
	}
	public static void updateFile(String path, String oldStr,String newStr) throws Exception
	{
		updateFile( path,  oldStr, newStr, false);
	}
	public static void updateFile(String path, String oldStr,String newStr, boolean isAbortOnDuplicate) throws Exception
	{
		File f = new File(path);
		if (f.isDirectory())
		{
			return;
		}
		String content = new Scanner(f).useDelimiter("\\Z").next();

		if (isAbortOnDuplicate && content.indexOf(newStr)>-1)
		{
			return;
		}
		
		if (content.indexOf(oldStr)<=0)
		{
			return;
		}
		
		File myFoo = new File(path);
		
		FileOutputStream fooStream = new FileOutputStream(myFoo, false); // true to append
		
		content = content.replaceAll(oldStr, newStr);
		// false to overwrite.
		byte[] myBytes = content.getBytes();
		fooStream.write(myBytes);
		fooStream.close();
	}


	public static void insertIntoFileAtMarker(String path, String insertRightBeforeThisToken,String newStr, boolean isAbortOnDuplicate) throws Exception
	{
		File f = new File(path);
		if (f.isDirectory())
		{
			return;
		}
		String content = new Scanner(f).useDelimiter("\\Z").next();
		int index = content.indexOf(newStr.trim());
		if (isAbortOnDuplicate && index >-1)
		{
			return;
		}
		
		if (content.indexOf(insertRightBeforeThisToken)<=0)
		{
			return;
		}
		
		File myFoo = new File(path);
		
		FileOutputStream fooStream = new FileOutputStream(myFoo, false); // true to append
		
		content = content.replaceAll(insertRightBeforeThisToken, newStr +insertRightBeforeThisToken);
		// false to overwrite.
		byte[] myBytes = content.getBytes();
		fooStream.write(myBytes);
		fooStream.close();
	}
}
