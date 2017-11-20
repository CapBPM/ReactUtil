package com.capbpm.react.data;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.capbpm.react.util.HttpHeader;
import com.couchbase.client.deps.com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public class Server {
	public enum Type {
		bpm,
		bpmcloud
	}
	
	
	@Id
	private String id;
	private String server; 
	private Integer port;
	private String username; 
	private String password;
	
	private String alias;
	private Type   type=Type.bpm;
	
	private List<HttpHeader> headers = new LinkedList<>();
	private String urlPrefix = "";
	private Boolean trial = false;
	
	private String coachTesterAcronym = "CT";
	
	private String userId;
	
	public Server() {
	}
	
	public Server(String server, Integer port, String username, String password, Type type, String urlPrefix) {
		super();
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
		this.type = type;
		this.urlPrefix = urlPrefix;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<HttpHeader> getHeaders() {
		return headers;
	}
	public void setHeaders(List<HttpHeader> headers) {
		this.headers = headers;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getUrlPrefix() {
		return urlPrefix;
	}
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	public Boolean getTrial() {
		return trial;
	}

	public void setTrial(Boolean trial) {
		this.trial = trial;
	}

	@JsonIgnore
	public String getBaseUrlForCoach(HttpServletRequest req, String taskId, String...serverId) {
//		if( Type.bpmcloud.equals(getType()) ){
			if( "service".equals(taskId) ) {
				return req.getScheme()+"://"+req.getServerName()+getPort(req)+req.getContextPath()+"/s/"+serverId[0];
			} 
			return req.getScheme()+"://"+req.getServerName()+getPort(req)+req.getContextPath()+"/p/"+taskId;
//		}
//		return "https://"+getServer()+":"+getPort()+urlPrefix;
	}
	@JsonIgnore
	private String getPort(HttpServletRequest req) {
		if (("http".equals(req.getScheme()) && req.getServerPort() != 80)||("https".equals(req.getScheme()) && req.getServerPort() != 443)){
			return ":"+req.getServerPort();
		}
		return "";
	}

	public String getCoachTesterAcronym() {
		return coachTesterAcronym;
	}

	public void setCoachTesterAcronym(String coachTesterAcronym) {
		this.coachTesterAcronym = coachTesterAcronym;
	}
	
	
	
}
