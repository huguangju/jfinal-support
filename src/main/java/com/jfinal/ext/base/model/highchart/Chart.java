package com.jfinal.ext.base.model.highchart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chart
{
	public List<String> categories = new ArrayList<String>();
	
	public List<Object> series = new ArrayList<Object>();
	
	public void setSeriesDate(String name ,List<Long>  _series){
		Map<String ,Object> datas = new HashMap<String ,Object>();
		
		datas.put("name", name);
		datas.put("data", _series);
		series.add( datas);
	}
	
	

}
