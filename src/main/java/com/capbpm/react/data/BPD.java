package com.capbpm.react.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BPD extends BaseData{

	private String name;
	private String type;
	private String itemID;
	private String processAppID;
	private String processAppName;
	private String processAppAcronym;
	private String snapshotID;
	private String display;
	private String branchID;
	private String branchName;
	private String startURL;
	private List<Activity> activities = new ArrayList<Activity>();

	
	
	public List<Activity> getActivities() {
		return activities;
	}
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	public void addActivity(Activity a) {
		this.activities.add(a);
	}
	
	@Override
	public String toString() {
		return "BPD [name=" + name + ", type=" + type + ", itemID=" + itemID + ", processAppID=" + processAppID
				+ ", processAppName=" + processAppName + ", processAppAcronym=" + processAppAcronym + ", snapshotID="
				+ snapshotID + ", display=" + display + ", branchID=" + branchID + ", branchName=" + branchName
				+ ", startURL=" + startURL + ", activities=" + activities + super.toString()+ "]";
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public String getProcessAppID() {
		return processAppID;
	}
	public void setProcessAppID(String processAppID) {
		this.processAppID = processAppID;
	}
	public String getProcessAppName() {
		return processAppName;
	}
	public void setProcessAppName(String processAppName) {
		this.processAppName = processAppName;
	}
	public String getProcessAppAcronym() {
		return processAppAcronym;
	}
	public void setProcessAppAcronym(String processAppAcronym) {
		this.processAppAcronym = processAppAcronym;
	}
	public String getSnapshotID() {
		return snapshotID;
	}
	public void setSnapshotID(String snapshotID) {
		this.snapshotID = snapshotID;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getBranchID() {
		return branchID;
	}
	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getStartURL() {
		return startURL;
	}
	public void setStartURL(String startURL) {
		this.startURL = startURL;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
