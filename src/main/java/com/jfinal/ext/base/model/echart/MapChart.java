package com.jfinal.ext.base.model.echart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapChart
{
	public List<String> legendDate = new ArrayList<String>();
	public List<Map<String,Object>> mapDate = new ArrayList<Map<String,Object>>();
	public List<Map<String,Object>> pieDate = new ArrayList<Map<String,Object>>();
	
	public int max;

	public void setMapDate(String name, int value, boolean select){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);
		map.put("value", value);
		map.put("selected", select);
		
		if(value>max) max=value;

		mapDate.add(map);
	}

	public void setPieDate(String name, int value, boolean select){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);
		map.put("value", value);
		map.put("selected", select);
		pieDate.add(map);
	}

}
