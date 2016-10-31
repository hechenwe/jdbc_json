package com.sooncode.jdbc.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.cglib.DbBean;
import com.sooncode.jdbc.cglib.DbBeanCache;
import com.sooncode.jdbc.constant.DATA;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.json.JsonBean;
import com.sooncode.jdbc.sql.ComSQL;
import com.sooncode.jdbc.sql.Parameter;
import com.sooncode.jdbc.sql.SelectEngine;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.util.Page;
import com.sooncode.jdbc.util.T2E;

/**
 * Jdbc Dao 服务
 * 
 * @author pc
 * 
 */
public class JdbcDao {

	public final static Logger logger = Logger.getLogger("JdbcDao.class");

	/**
	 * 数据处理对象JDBC
	 */
	private Jdbc jdbc;
	private String dbKey = DATA.DEFAULT_KEY;

	JdbcDao() {
		jdbc = JdbcFactory.getJdbc();
	}

	JdbcDao(String dbKey) {
		jdbc = JdbcFactory.getJdbc(dbKey);
		this.dbKey = dbKey;
	}

	public Page getPage(long pageNum, long pageSize, Conditions leftConditions, Conditions... others) {

		DbBean leftBean = DbBeanCache.getDbBean(dbKey, leftConditions.getJsonBean());

		// 1.单表
		if (others.length == 0) {
			Parameter p = ComSQL.select(leftBean, pageNum, pageSize);
			List<Map<String, Object>> list = jdbc.gets(p);
			List<JsonBean> beans = new ArrayList<>();
			for (Map<String, Object> map : list) {
				JsonBean jb = new JsonBean(leftConditions.getJsonBean().getBeanName());
				jb.addFields(map);
				beans.add(jb);
			}
			Long size = getSize(leftConditions, others);
			Page pager = new Page(pageNum, pageSize, size, beans);
			return pager;

		} else {
			return null;
		}

		 
	}

	 
	public long save(JsonBean jsonBean) {
		DbBean cb = DbBeanCache.getDbBean(dbKey, jsonBean);
		Parameter p = ComSQL.insert(cb);
		long n = jdbc.update(p);
		return n;

	}

	public boolean saves(List<JsonBean> jsonBeans) {

		if (jsonBeans == null) {
			return false;
		}

		List<Parameter> pes = new LinkedList<>();
		for (JsonBean jsonBean : jsonBeans) {
			DbBean cb = DbBeanCache.getDbBean(dbKey, jsonBean);
			Parameter p = ComSQL.insert(cb);
			pes.add(p);
		}
		return jdbc.updates(pes);

	}

	public boolean updates(List<JsonBean> jsonBeans) {
		List<Parameter> parameters = new ArrayList<>();
		for (JsonBean jsonBean : jsonBeans) {
			DbBean cb = DbBeanCache.getDbBean(dbKey, jsonBean);
			Object pkVal = cb.getPrimaryFieldValue();
			if (pkVal != null) {
				Parameter p = ComSQL.update(cb);
				parameters.add(p);
			}
		}
		return jdbc.updates(parameters);
	}

	public long saveOrUpdate(JsonBean jsonBean) {
		DbBean dbBean = DbBeanCache.getDbBean(dbKey, jsonBean);

		Object id = dbBean.getPrimaryFieldValue();
		if (id != null) {// obj 有id update();
			JsonBean newJsonBean = new JsonBean(jsonBean.getBeanName());
			newJsonBean.addField(dbBean.getPrimaryField(), id);

			Conditions c = new Conditions(newJsonBean);
			List<JsonBean> newObj = this.gets(c);
			if (newObj == null || newObj.size() == 0) {
				return save(jsonBean);
			} else {
				return update(jsonBean);
			}

		} else {// obj 没有id
			Conditions c = new Conditions(jsonBean);
			List<JsonBean> oldObj = this.gets(c);
			if (oldObj == null || oldObj.size() == 0) {
				return save(jsonBean);// 用obj 去数据库查询 如果不存在 则保存
			} else {
				return -1L;// 用obj 去数据库查询 如何存在 不更新 不保存
			}
		}

	}

	public long update(JsonBean jsonBean) {
		DbBean dbBean = DbBeanCache.getDbBean(dbKey, jsonBean);
		Object pkValue = dbBean.getPrimaryFieldValue();
		if (pkValue == null) {
			return 0L;
		}
		Parameter p = ComSQL.update(dbBean);
		long n = jdbc.update(p);
		return n;

	}

	public long delete(JsonBean jsonBean) {
		DbBean dbBean = DbBeanCache.getDbBean(dbKey, jsonBean);
		Parameter p = ComSQL.delete(dbBean);
		long n = jdbc.update(p);
		return n;

	}

	 

	/**
	 * 获取查询长度
	 * 
	 * @param left
	 * @param others
	 * @return
	 */
	private long getSize(Conditions leftCond, Conditions... otherConds) {

		SelectEngine se = new SelectEngine(dbKey);

		if (otherConds.length == 0) { // 单表
			return se.getMainSize(leftCond);
			 
		}
      return 0;
	  
	}

	 
	public List<JsonBean> gets(Conditions conditions) {

		DbBean cb = DbBeanCache.getDbBean(dbKey, conditions.getJsonBean());
		String columns = ComSQL.columns4One(cb);
		Parameter where = conditions.getWhereSql();
		Parameter p = new Parameter();
		String tableName = T2E.toTableName(cb.getBeanName());
		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE+ where.getReadySql();
		p.setReadySql(sql);
		p.setParams(where.getParams());
		List<Map<String, Object>> list = jdbc.gets(p);
		List<JsonBean> beans = new ArrayList<>();
		for (Map<String, Object> map : list) {
			JsonBean bean = new JsonBean(cb.getBeanName());
			bean.addFields(map);
			beans.add(bean);
		}
		return beans;
	}
}