package com.capbpm.react.data;

import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

import com.capbpm.react.util.HttpHeader;
import com.couchbase.client.core.utils.Base64;

public class Credential {
	private final String username; 
	private final String password; 
	private final String server; 
	private final Integer port;
	private final List<HttpHeader> headers;
	private final String urlPrefix;

	@SuppressWarnings("unused")
	private Credential() {
		this.username = this.password = this.server = null;
		this.port = 0;
		this.headers = Collections.EMPTY_LIST;
		this.urlPrefix = "";
	}
	
	public Credential(Server serverInfo) {
		Assert.notNull(serverInfo, "serverInfo must be not null!");
		this.username = serverInfo.getUsername();
		this.password = serverInfo.getPassword();
		this.server = serverInfo.getServer();
		this.port = serverInfo.getPort();
		this.urlPrefix = serverInfo.getUrlPrefix();
		this.headers = serverInfo.getHeaders();
	}

	public Credential(String username, String password ) {
		this(username, password, null, null, Collections.EMPTY_LIST, "");
	}
	
	public Credential(String username, String password, String server, Integer port, List<HttpHeader> headers, String urlPrefix) {
		this.username = username;
		this.password = password;
		this.server = server;
		this.port = port;
		this.headers = headers;
		this.urlPrefix = urlPrefix;
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getServer() {
		return server;
	}
	public Integer getPort() {
		return port;
	}
	public List<HttpHeader> getHeaders() {
		return headers;
	}
	public String getUrlPrefix() {
		return urlPrefix;
	}

	public String getBaseUrl() {
		String port=""; 
		if( this.port != 443 ){
			 port = ":"+this.port;
		}
		return "https://"+server+port+urlPrefix;
	}
	
	public String getEncodedCredential(){
		return new String(Base64.encode((this.getUsername()+":"+this.getPassword()).getBytes()));
	}
}
