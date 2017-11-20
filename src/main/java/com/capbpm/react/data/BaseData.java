package com.capbpm.react.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

public class BaseData implements Serializable {
	private List<Model> variables = new ArrayList<Model>();
	private Map<String, Model> varMap = new TreeMap<String, Model>();

	
	@Override
	public String toString() {
		return "BaseData [variables=" + variables + ", varMap=" + varMap + "]";
	}



	/*public List<Model> uniqueModelsAsJSONStr()
	{
		
		List<Model> modelz= uniqueModels();
		
		return null;	
	}*/
	
	public List<Model> uniqueModels()
	{
		
		Iterator<Model> it =varMap.values().iterator();
		List<Model>  retval = new ArrayList<Model>() ;
		while (it.hasNext() )
		{
			Model m = it.next();
			if (m.getDirection().equalsIgnoreCase("input") || m.getDirection().equalsIgnoreCase("output"))
			retval.add(m);
		}
		return retval;	
	}

	public Model uniqueModelAsOne()
	{
		Iterator<Model> it =varMap.values().iterator();
		Model retval = new Model("Data");
		while (it.hasNext() )
		{
			Model m = it.next();
			if (m.getDirection().equalsIgnoreCase("input") || m.getDirection().equalsIgnoreCase("output"))
			retval.addChild(m);
		}
		return retval;
	}
	
	
	public void addToVarMap(Model v )
	{
		if (v !=null && v.getType() !=null)
		{
			varMap.put(v.getType(),v);
		}
		
	}
	
	public Model getVarFromMap(String type)
	{
		Model v = varMap.get(type);
		return v;
		
	}
	//
	public List<Model> getVarType(String direction) {
		List<Model> retval = new ArrayList<Model>();
		for (Model v: variables)
		{
			if (v.getDirection().equalsIgnoreCase(direction))
			{
				retval.add(v);
			}
		}
		return retval;
	}
	
	public List<Model> getInternal() {
		return getVarType("internal");
	}
	public void setInternal(List<Model> myIn) {
		this.variables.addAll(myIn);
		updateVarMap(myIn);
	}
	public List<Model> getInputs() {
		 return getVarType("Input");
	}
	public void setInputs(List<Model> myIn) {
		this.variables.addAll(myIn);
		updateVarMap(myIn);
	}

	private void updateVarMap(List<Model> myIn) {
		for (Model k : myIn)
		{
			varMap.put(k.getType(), k);
		}
	}

	public List<Model> getOutputs() {
		return getVarType("output");
	}
	public void setOutputs(List<Model> outputs) {
		this.variables.addAll(outputs);
		updateVarMap(outputs);
	}
	public void addInput(Model in) {
		this.variables.add(in);
		addToVarMap(in);
		
	}
	public void addOutput(Model v) {
		this.variables.add(v);
		addToVarMap(v);
	}
	public void addInternal(Model v) {
		this.variables.add(v);
		addToVarMap(v);
		
	}	
}
