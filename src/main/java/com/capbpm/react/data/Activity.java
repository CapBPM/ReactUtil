package com.capbpm.react.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Activity extends BaseData implements Serializable, Comparable<Activity>, Comparator<Activity>  {
	private int order = -1;
	private int x = -1;
	private int y = -1;

	private String trimName;
	private String name;
	private String lane;
	private String id;
	private String poId;
	private String PoType;
	private String sid;
	
	public String getTrimName() {
		return trimName;
	}
	public void setTrimName(String trimName) {
		this.trimName = trimName;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getPoId() {
		return poId;
	}
	public void setPoId(String poId) {
		this.poId = poId;
	}
	public String getPoType() {
		return PoType;
	}
	public void setPoType(String poType) {
		PoType = poType;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if (name !=null)
		{
		name = name.replace("\n", "").replace("\r", "");
		this.name = name;
		this.setTrimName(buildTrimName(name));
		}
	}


	public Activity() {
		super();

	}
	public Activity(String name) {
		super();
		setName(name);

	}

	@Override
	public String toString() {
		return "Activity [order=" + order + ", x=" + x + ", y=" + y + ", trimName=" + trimName + ", name=" + name
				+ ", lane=" + lane + ", id=" + id + ", poId=" + poId + ", PoType=" + PoType + ", sid=" + sid + "]";
	}
	
	
	public String getLane() {
		return lane;
	}
	public void setLane(String lane) {
		this.lane = lane;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(Activity o) {
		return compare(this, o);

	}

	@Override
	public int compare(Activity o1, Activity o2) {
		return (o1.order - o2.order);
	}
	public void calcOrder() {
		order = (this.x * this.x + this.y*this.y)/2;
		order = (int)Math.sqrt(order);
	}
	
	private String buildTrimName(String inVal) {
		return inVal.replace("\n", "").replace("\r", "").replaceAll("\\s+", "_").replaceAll("[^a-zA-Z ]", "");
	}
}
