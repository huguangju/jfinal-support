package com.jfinal.ext.base.model.easyui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.base.Model;

@SuppressWarnings({ "rawtypes", "serial" })
public class TreeGrid<T extends Model> implements Serializable
{
	public int total;
	
	public List<Map> rows = new ArrayList<Map>();
	
	public  void addRows(List<T> list){

		for(Model m:list){
			rows.add(m.getAttrs());
		}
	}
	
}
