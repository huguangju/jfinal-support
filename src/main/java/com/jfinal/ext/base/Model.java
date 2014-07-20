package com.jfinal.ext.base;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.base.model.easyui.DataGrid;
import com.jfinal.ext.base.model.easyui.Form;
import com.jfinal.ext.base.util.L;
import com.jfinal.ext.base.util.Txt;
import com.jfinal.ext.base.util.Validate;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;

public class Model<M extends com.jfinal.plugin.activerecord.Model<M>> extends
		com.jfinal.plugin.activerecord.Model<M> {

	private static final long serialVersionUID = 8924183967602127690L;

	/**
	 * 用来当 缓存名字 也用来 生成 简单sql
	 */
	public String tableName;

	/**
	 * 反射获取 注解获得 tablename
	 */
	public Model() {
		TableBind table = this.getClass().getAnnotation(TableBind.class);

		if (table != null)
			tableName = table.tableName();
	}

	public boolean update(String key, Object value, Object id) {
		return Db.update("update " + tableName + " set " + key
				+ "=? where id =?", value, id) > 0;
	}

	/**
	 * 仅适用于约定 [有名为name的字段]
	 */
	public M findByName(String name) {
		return findFirst("select * from " + tableName + " where name =? ", name);
	}

	/**
	 * 仅适用于约定 [有名为name的字段]
	 */
	public boolean checkNameExist(String name) {
		return findFirst("select * from " + tableName + " where name ='" + name + "'") != null;
	}
	
	public M findByField(String field, Object fieldValue){
		return findFirst("select * from " + tableName + " where "+ field + "=? ", fieldValue);
	}
	
	public boolean checkNameExist(String field, Object fieldValue) {
		return findFirst("select * from " + tableName + " where "+ field +"='" + fieldValue + "'") != null;
	}
	
	
	/***
	 * if empty remove the attr
	 */
	public Model<M> emptyRemove(String attr) {
		if (get(attr) == null)
			remove(attr);

		return this;
	}

	public Model<M> emptyZreo(String attr) {
		if (get(attr) == null)
			set(attr, 0);
		return this;
	}

	/***
	 * 删除自己的同时 删除 所有 子节点 属性名 必需为pid
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteByIdAndPid(Integer id) {
		boolean result = deleteById(id);

		List<Model<?>> list = (List<Model<?>>) list("where pid=?", id);

		for (Model<?> m : list) {
			deleteByIdAndPid(m.getId());

			Db.update("delete from " + tableName + " where pid=? ", id);
		}

		return result;
	}

	/***
	 * ids 必需为 连续的 1，2，3 这样子
	 */
	public boolean batchDelete(String ids) {
		if (Validate.isEmpty(ids))
			return false;
		return Db.update("delete from " + tableName + " where id in (" + ids+ ")") > 0;

	}

	/**
	 * 根据 id 判断的
	 */
	public void saveOrUpdate() {
		if (getId() != null)
			update();
		else
			save();
	}

	@SuppressWarnings("unchecked")
	public boolean pidIsChild(Integer id, Integer pid) {
		boolean result = false;
		if (pid != null) {
			List<Model<?>> list = (List<Model<?>>) list(" where  pid =?  ", id);

			if (list.size() == 0)
				result = false;

			for (Model<?> r : list) {
				if (pid.equals(r.getId())) {
					result = true;
					return result;
				} else {
					if (pidIsChild(r.getId(), pid)) {
						result = true;
						L.i("result =" + result);

						return result;
					}
				}
			}
		}

		return result;
	}

	public boolean isFind(String sql) {
		return find(sql).size() > 0;
	}

	public M findFirstByWhere(String where, Object... params) {
		return list(where, params).get(0);
	}

	public boolean isFindByWhere(String where, Object... params) {
		return list(where, params).size() > 0;
	}

	/***
	 * 返回全部的数据 比较方便 但不灵活
	 */
	public List<M> list() {
		return find(" select *from " + tableName);
	}

	public List<M> listOrderBySeq() {
		return list(" order by seq");
	}

	public List<M> listOrderBySeq(String where, Object... params) {
		return list(where + " order by seq", params);
	}

	/***
	 * 自定义sql
	 */
	public DataGrid<M> listByDataGrid(String sql, DataGrid<M> dg, Form f) {
		List<M> list = find(sql + f.getWhereAndLimit(dg));
		dg.rows = list;
		dg.total = (int) getCount(sql + f.getWhere(dg));
		
		return dg;
	}

	/***
	 * 直接插 自动删除 最简单的sql
	 */
	public DataGrid<M> listByDataGrid(DataGrid<M> dg, Form f) {
		List<M> list = list(f.getWhereAndLimit(dg));
		dg.rows = list;
		dg.total = (int) getCount(" from " + tableName + " " + f.getWhere(dg));

		return dg;
	}

	public List<M> list(String sql, Form f) {
		return find(sql + f.getWhere());
	}

	/***
	 * 返回全部的数据 比较方便 但不灵活
	 */
	public List<M> list(String where) {
		return find(" select *from " + tableName + " " + where);
	}

	/***
	 * see {@link #list(String)}
	 * @return
	 */
	public List<M> list(String where, Object... params) {
		return find(" select *from " + tableName + " " + where, params);
	}

	public List<M> list(DataGrid<?> dg, Form f) {
		return list(f.getWhereAndLimit(dg));
	}

	/***
	 * 返回全部的数据 比较方便 但不灵活
	 */
	public List<M> list(int limit) {
		return find(" select *from " + tableName + " limit " + limit);
	}

	/***
	 * 返回全部的数据 比较方便 但不灵活
	 */
	public List<M> list(int page, int size) {
		if (page < 1)
			page = 0;
		return find(" select *from " + tableName + " limit " + (page - 1)
				* size + "," + size);
	}

	// public Page<M> loadModelPage(Page<M> page)
	// {
	// List<M> modelList = page.getList();
	// for (int i = 0; i < modelList.size(); i++)
	// {
	// com.jfinal.plugin.activerecord.Model model = modelList.get(i);
	// M topic = loadModel(model.getInt("id"));
	// modelList.set(i, topic);
	// }
	// return page;
	// }

	public List<M> findByCache(String sql) {
		return super.findByCache(tableName, sql, sql);
	}

	public List<M> findByCache(String sql, Object... params) {
		return super.findByCache(tableName, sql, sql, params);
	}

	public boolean saveAndDate() {
		return this.setDate("date").save();
	}

	public boolean saveAndCreateDate() {
		this.setDate("createdate");
		return this.setDate("modifydate").save();
	}

	public boolean updateAndModifyDate() {
		return this.setDate("modifydate").update();
	}

	public Map<String, Object> getAttrs() {
		return super.getAttrs();
	}

	public M setDate(String date) {
		return this.set(date, new Timestamp(System.currentTimeMillis()));
	}

	/***
	 * 把 model 转化为 list 找到其中的单个属性
	 */
	public List<String> getAttr(String sql, String attr) {
		List<String> list = new ArrayList<String>();

		for (M t : find(sql)) {

			list.add(t.getStr(attr));
		}
		return list;
	}

	/***
	 * 把 model 转化为 list 找到其中的单个属性
	 */
	public List<String> getAttr(String sql, String attr, String... param) {
		List<String> list = new ArrayList<String>();
		for (M t : find(sql, new Object[]{param})) {
			list.add(t.getStr(attr));
		}
		return list;
	}

	public long getCount(String sql) {
		sql = Txt.split(sql.toLowerCase(), "from")[1];
		if (sql.contains("order by"))
			sql = Txt.split(sql, "order by")[0];

		return findFirst(" select count(*) as c from" + sql).getLong("c");
	}

	public Long getCount(String sql, String countName, Object... params) {
		return findFirst(sql, params).getLong(countName);
	}

	/***
	 * 取值
	 */
	public Long getCount() {
		return getLong("count");
	}

	public Integer getId() {
		return getInt("id");
	}

	public Integer getPid() {

		return getInt("pid");
	}

	public Integer getType() {
		return getInt("type");
	}

	/***
	 * return getStr("name");
	 */
	public String getName() {
		return getStr("name");
	}

	/***
	 * return getStr("pwd");
	 * 
	 * @return
	 */
	public String getPwd() {
		return getStr("pwd");
	}

	/***
	 * return getStr("des");
	 * 
	 * @return
	 */
	public String getDes() {

		return getStr("des");
	}

	/***
	 * return getStr("date");
	 * 
	 * @return
	 */
	public String getDate() {
		return getStr("date");
	}

	public String getCreateDate() {
		return getStr("createdate");
	}

	public String getModifyDate() {
		return getStr("modifydate");
	}

	public String getIcon() {
		return getStr("icon");
	}

	public String getIconCls() {
		return getStr("iconCls");
	}

	public Integer getStatus() {
		return getInt("status");
	}

	public static String sql(String key) {
		return SqlKit.sql(key);
	}

}
