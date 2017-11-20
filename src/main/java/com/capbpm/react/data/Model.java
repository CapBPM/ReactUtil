package com.capbpm.react.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Model implements Serializable, Comparable<Model>, Comparator<Model> {
	@JsonIgnore
	public static final String BOOLEAN = "Boolean";
	@JsonIgnore
	public static final String DATE = "Date";
	@JsonIgnore
	public static final String INTEGER = "Integer";
	@JsonIgnore
	public static final String DECIMAL = "Decimal";
	@JsonIgnore
	public static final String STRING = "String";
	private String label;
	private String name;
	private String type = "String";
	private int order = -1;
	@JsonIgnore
	private int x = -1;
	@JsonIgnore
	private int y = -1;

	private boolean isArray;
	@JsonIgnore
	private String oid;
	@JsonIgnore
	private String sid;
	@JsonIgnore
	private String direction;
	private String value;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	private List<Model> children = new ArrayList<Model>();

	public List<Model> getChildren() {
		return children;
	}

	public void setChildren(List<Model> children) {
		this.children = children;
	}

	public void addChild(Model child) {
		children.remove(child);
		this.children.add(child);

	}

	public void removeChild(Model child) {
		children.remove(child);
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

	public Model() {

	}
	
	public static Model initFromJSON(String json) throws Exception
	{
		
		Model retval = new Model();
		JSONObject o = new JSONObject(json);
		Iterator<JSONObject> it = o.keys();
		while (it.hasNext())
		{
			Object oo = it.next();
			String key = oo.toString();
			String v  = o.getString(key);
			System.out.println(key+"="+v);
			
			Model m = new Model(key,key);
			m.setValue(v);
		}
		
		return retval;
	}

	public Model(String name, boolean isArray) {
		this(name, name, "String", isArray);
	}

	public Model(String name) {
		this(name, name, "String", false);
	}

	public Model(String name, String type) {
		this(name, name, type, false);
	}

	@Override
	public String toString() {
		return "Var [label=" + label + ", name=" + name + ", type=" + type + ", order=" + order + ", x=" + x + ", y="
				+ y + ", isArray=" + isArray + ", oid=" + oid + ", sid=" + sid + ", direction=" + direction
				+ ", children=" + children + "]";
	}

	public Model(String name, String type, boolean isArray) {
		this(name, name, type, isArray);
	}

	public Model(String label, String name, String type, boolean isArray) {
		super();
		this.label = label;
		this.name = name;
		this.type = type;
		this.isArray = isArray;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	@Override
	public int compareTo(Model o) {
		return compare(this, o);

	}

	@Override
	public int compare(Model o1, Model o2) {
		return (o1.order - o2.order);
	}

	public JSONObject toJSONObject() throws Exception
	{
		JSONObject j = new JSONObject();
		j.put(getLabel(),getDefaultTypeData(getType()));
		
		for (Model kid : children)
		{
			j.put(kid.getLabel(),getDefaultTypeData(getType()));
		}
		return j;
	}
	public String toJSON() throws Exception {

		return toJSONObject().toString();// toJSON(false);
	}
	public  String getDefaultTypeData() {
		
		return getDefaultTypeData(getType());
	}

	private static String getDefaultTypeData(String type) {
		String retval = "";

		if (type.equals(BOOLEAN)) {
			retval = "false";
		} else if (type.equals(DATE)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
			TimeZone tz = TimeZone.getTimeZone("UTC");
			df.setTimeZone(tz);
			retval = "\"" + df.format(new Date()) + "\"";

		} else if (type.equals(INTEGER)) {
			retval = "0";
		} else if (type.equals(DECIMAL)) {
			retval = "0.0";
		}
		return retval;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
