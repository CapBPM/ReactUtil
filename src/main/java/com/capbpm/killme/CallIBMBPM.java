package com.capbpm.killme;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

public class CallIBMBPM {

	public static void main(String args[]) throws Exception
	{
		String tmp = getVar();
		call(tmp,"Danny","passw0rd");
		
	}
	public static String getVar()
	{
		return getVar("25.22fd5444-f579-4a2e-9e4f-51e8537a8245","2064.d38174ae-5453-4e1b-b002-a2879578ce28");
	}
	public static String getVar(String oid, String sid)
	{
		//String url = "https://"+server+":"+port+"/rest/bpm/wle/v1/processModel/"+oid+"?snapshotId="+sid+"&parts=dataMode";
		https://169.48.72.245:9443/rest/bpm/wle/v1/processModel/25.22fd5444-f579-4a2e-9e4f-51e8537a8245?snapshotId=2064.d38174ae-5453-4e1b-b002-a2879578ce28&parts=dataMode;
			return getVar("169.48.72.245","9443","Danny","passw0rd",oid,sid);
	}
	public static String getVar(String server, String port, String u, String p, String oid, String sid)
	{
		String url = "https://"+server+":"+port+"/rest/bpm/wle/v1/processModel/"+oid+"?snapshotId="+sid+"&parts=dataMode";
		//https://169.48.72.245:9443/rest/bpm/wle/v1/processModel/25.22fd5444-f579-4a2e-9e4f-51e8537a8245?snapshotId=2064.d38174ae-5453-4e1b-b002-a2879578ce28&parts=dataMode;
		
		return url;
	}
	
	public static void call(String url, String u, String p) throws Exception
	{

	}
	
	
	public static void go(String url, String u, String p)
	{
		
	}
}
