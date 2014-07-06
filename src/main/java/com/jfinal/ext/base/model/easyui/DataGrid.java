package com.jfinal.ext.base.model.easyui;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyUI DataGrid模型
 * @author 孙宇
 */
@SuppressWarnings("serial")
public class DataGrid<T> implements java.io.Serializable
{
	public int total = 0;
	public int page;
	public String sortName = "";
	public String sortOrder = "";
	public List<T> rows = new ArrayList<T>();

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

	public List<T> getRows()
	{
		return rows;
	}

	public void setRows(List<T> rows)
	{
		this.rows = rows;
	}

}
