package com.capbpm.react.data;

public class ProcessData implements java.io.Serializable {

	private String name;
	private String pid;
	private String sid;
	private String submitContent;
	private String url;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}
	/**
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}
	/**
	 * @param sid the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}
	/**
	 * @return the submitContent
	 */
	public String getSubmitContent() {
		return submitContent;
	}
	/**
	 * @param submitContent the submitContent to set
	 */
	public void setSubmitContent(String submitContent) {
		this.submitContent = submitContent;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProcessData [name=" + name + ", pid=" + pid + ", sid=" + sid + ", submitContent=" + submitContent
				+ ", url=" + url + "]";
	}
	
}
