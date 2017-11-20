package com.capbpm.react.util;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.capbpm.react.data.Constants;


public class UnzipUtil {

	 public static void main(String[] args) {

	        
	        UnzipUtil unzipper = new UnzipUtil();
	        try {
	            unzipper.unzip(Constants.zipFilePath, Constants.destDirectory, Constants.NEW_APP_NAME);
	        } catch (Exception ex) {
	            // some errors occurred
	            ex.printStackTrace();
	        }
	    }
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws Exception 
     */
    public void unzip(String zipFilePath, String destDirectory, String newName) throws Exception {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {

        	/*String n = entry.getName().split("/")[0];
          	String oldName = DEF_APP_NAME ;
        	if (n.equals(oldName) )
        	{
        		n = NEW_APP_NAME;
        	}*/
            String filePath = destDirectory + File.separator + entry.getName();
            System.out.println(filePath);
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        
        rename(destDirectory,Constants.DEF_APP_NAME,newName);
        renameFileContent(destDirectory,Constants.DEF_APP_NAME,newName);
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        //OutputSteam os  = new  
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.flush();
        bos.close();
    }
    
    public String parseJSON(String contents) throws Exception
    {
    	JSONObject jObject = new JSONObject(contents.trim());
    	Iterator<?> keys = jObject.keys();

    	while( keys.hasNext() ) {
    	    String key = (String)keys.next();
    	    if ( jObject.get(key) instanceof JSONObject ) {

    	    }
    	}
		return contents;
    }
    
    private void rename(String path, String oldName, String newName)
    {
    	File rootDir = new File(path);
    	File[] fList = rootDir.listFiles(File::isDirectory);

    	
    	if (fList.length>0)
    	{
    	    for (File file : fList) {
    	        if (file.isDirectory()) {
    	        	rename(file.getAbsolutePath(),oldName,newName);
    	        } 
    	    }
    	}
    	if (rootDir.getName().equals(oldName))
    	{
    		rootDir.renameTo(new File(rootDir.getParentFile(),newName));
    	}
    }
   
    private void renameFileContent(String path, String oldName, String newName) throws Exception
    {
    	File rootDir = new File(path);
    	File[] fList = rootDir.listFiles();

    	
    	if (fList.length>0)
    	{
    	    for (File file : fList) {
    	        if (file.isDirectory()) {
    	        	renameFileContent(file.getAbsolutePath(),oldName,newName);
    	        } 
    	        else
    	        {
    	        	FileUtil.updateFile( path,  oldName, newName);
    	        }
    	    }
    	}
    	
    	return;
    }
}