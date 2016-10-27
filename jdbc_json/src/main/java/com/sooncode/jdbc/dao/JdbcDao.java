package com.sooncode.jdbc.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.cglib.DbBean;
import com.sooncode.jdbc.cglib.DbBeanCache;
import com.sooncode.jdbc.constant.DATA;
import com.sooncode.jdbc.constant.SQL_KEY;
 
import com.sooncode.jdbc.json.JsonBean;
import com.sooncode.jdbc.sql.ComSQL;
import com.sooncode.jdbc.sql.Parameter;
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

		/*
		 * else if (others.length == 1) {// 3.一对多
		 * 
		 * RObject rightRO = new RObject(others[0]); String leftPk =
		 * leftRO.getPk(); String rightPk = rightRO.getPk();
		 * 
		 * if (rightRO.hasField(leftPk) && !leftRO.hasField(rightPk)) {
		 * Parameter p = ComSQL.getO2M(left, others[0], pageNum, pageSize);
		 * List<Map<String, Object>> list = jdbc.executeQueryL(p); Object l =
		 * ToEntity.findEntityObject(list, left.getClass()); List<?> res =
		 * ToEntity.findEntityObject(list, others[0].getClass());
		 * leftRO.invokeSetMethod(leftRO.getListFieldName(others[0].getClass()),
		 * res); long size = getSize(left, others); Pager<?> pager = new
		 * Pager<>(pageNum, pageSize, size, l); return pager; } else { return
		 * o2o(pageNum, pageSize, left, others); } } else if (others.length ==
		 * 2) {// 4.多对多 String leftPk = new RObject(left).getPk(); String
		 * rightPk = new RObject(others[1]).getPk(); RObject middle = new
		 * RObject(others[0]); if (middle.hasField(leftPk) &&
		 * middle.hasField(rightPk)) { Parameter p = ComSQL.getM2M(left,
		 * others[0], others[1], pageNum, pageSize); List<Map<String, Object>>
		 * list = jdbc.executeQueryL(p); Object l =
		 * ToEntity.findEntityObject(list, left.getClass()).get(0); leftRO = new
		 * RObject(l);
		 * 
		 * Object res = ToEntity.findEntityObject(list, others[1].getClass());
		 * String listFieldName = leftRO.getListFieldName(others[1].getClass());
		 * leftRO.invokeSetMethod(listFieldName, res); long size = getSize(left,
		 * others); Pager<?> pager = new Pager<Object>(pageNum, pageSize, size,
		 * leftRO.getObject()); return pager; } else { // 一对一 return
		 * o2o(pageNum, pageSize, left, others); }
		 * 
		 * } else {// 一对一 return o2o(pageNum, pageSize, left, others); }
		 */
	}

	/*
	 * public Pager<?> getPager(long pageNum, long pageSize, Conditions
	 * conditions) { String columns = ComSQL.columns(conditions.getObj());
	 * Parameter where = conditions.getWhereSql(); String tableName =
	 * T2E.toColumn(conditions.getObj().getClass().getSimpleName()); Long index
	 * = (pageNum - 1) * pageSize; String sql = SQL_KEY.SELECT + columns +
	 * SQL_KEY.FROM + tableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE +
	 * where.getReadySql() + SQL_KEY.LIMIT + index + STRING.COMMA + pageSize;
	 * String sql4size = SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS +
	 * SQL_KEY.SIZE + SQL_KEY.FROM + tableName + SQL_KEY.WHERE +
	 * SQL_KEY.ONE_EQ_ONE + where.getReadySql();
	 * 
	 * Parameter sqlP = new Parameter(); sqlP.setReadySql(sql);
	 * sqlP.setParams(where.getParams());
	 * 
	 * Parameter sizeP = new Parameter(); sizeP.setReadySql(sql4size);
	 * sizeP.setParams(where.getParams());
	 * 
	 * List<Map<String, Object>> list = jdbc.executeQueryL(sqlP); Long size =
	 * (Long) jdbc.executeQueryM(sizeP).get(SQL_KEY.SIZE); List<?> lists =
	 * ToEntity.findEntityObject(list, conditions.getObj().getClass()); Pager<?>
	 * pager = new Pager<>(pageNum, pageSize, size, lists); return pager; }
	 */

	/*
	 * public Pager<?> getPager(long pageNum, long pageSize, Class<?>
	 * entityClass, Cond cond) {
	 * 
	 * if (entityClass == null || cond == null || cond.isHaveCond() == false) {
	 * return null; }
	 * 
	 * RObject rObj = new RObject(entityClass); String columns =
	 * ComSQL.columns(rObj.getObject()); Parameter where = cond.getParameter();
	 * String tableName = T2E.toColumn(entityClass.getSimpleName()); Long index
	 * = (pageNum - 1) * pageSize; String sql = SQL_KEY.SELECT + columns +
	 * SQL_KEY.FROM + tableName + SQL_KEY.WHERE + where.getReadySql() +
	 * SQL_KEY.LIMIT + index + STRING.COMMA + pageSize; String sql4size =
	 * SQL_KEY.SELECT + SQL_KEY.COUNT + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM
	 * + tableName + SQL_KEY.WHERE + where.getReadySql();
	 * 
	 * Parameter sqlP = new Parameter(); sqlP.setReadySql(sql);
	 * sqlP.setParams(where.getParams());
	 * 
	 * Parameter sizeP = new Parameter(); sizeP.setReadySql(sql4size);
	 * sizeP.setParams(where.getParams());
	 * 
	 * List<Map<String, Object>> list = jdbc.executeQueryL(sqlP); Long size =
	 * (Long) jdbc.executeQueryM(sizeP).get(SQL_KEY.SIZE); List<?> lists =
	 * ToEntity.findEntityObject(list, entityClass); Pager<?> pager = new
	 * Pager<>(pageNum, pageSize, size, lists); return pager; }
	 */
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
		List<Parameter > parameters = new ArrayList<>();
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
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param left
	 * @param others
	 * @return
	 */
	/*
	 * private Pager<?> o2o(Long pageNum, Long pageSize, Object left, Object...
	 * others) { Parameter p = ComSQL.getO2O(left, others); //
	 * logger.debug(sql); List<Map<String, Object>> list =
	 * jdbc.executeQueryL(p);
	 * 
	 * String TClassName = left.getClass().getName();//
	 * Genericity.getGenericity(this.getClass(),
	 * 
	 * List<Object> resultList = new LinkedList<>();
	 * 
	 * for (Map<String, Object> m : list) { // 遍历数据库返回的数据集合
	 * 
	 * RObject rObject = new RObject(TClassName); List<Field> fields =
	 * rObject.getFields();// T 对应的属性
	 * 
	 * for (Field f : fields) { Object obj =
	 * m.get(T2E.toField(Genericity.getSimpleName(TClassName).toUpperCase() +
	 * STRING.UNDERLINE + T2E.toColumn(f.getName()))); if (obj != null) {
	 * rObject.invokeSetMethod(f.getName(), obj); }
	 * 
	 * String typeSimpleName = f.getType().getSimpleName(); String typeName =
	 * f.getType().getName(); String baseType =
	 * JavaBaseType.map.get(typeSimpleName); if (baseType == null) {// 不包含
	 * Integer Long Short Byte Float // Double Character Boolean Date String //
	 * List
	 * 
	 * RObject rO = new RObject(typeName);
	 * 
	 * List<Field> fs = rO.getFields();
	 * 
	 * for (Field ff : fs) {
	 * 
	 * Object o = m.get(T2E .toField(typeSimpleName.toUpperCase() +
	 * STRING.UNDERLINE + T2E.toColumn(ff.getName()))); if (o != null) {
	 * rO.invokeSetMethod(ff.getName(), o); }
	 * 
	 * }
	 * 
	 * rObject.invokeSetMethod(f.getName(), rO.getObject()); } }
	 * 
	 * resultList.add(rObject.getObject());
	 * 
	 * }
	 * 
	 * long size = getSize(left, others); Pager<?> pager = new Pager<>(pageNum,
	 * pageSize, size, resultList); return pager; }
	 */

	/*
	 * public long count(String key, Object obj) { String sql = "SELECT COUNT("
	 * + key + ") AS SIZE" + " FROM " +
	 * T2E.toTableName(obj.getClass().getSimpleName()) + " WHERE 1=1 " +
	 * ComSQL.where(obj).getReadySql(); Parameter p = new Parameter();
	 * p.setReadySql(sql); p.setParams(ComSQL.where(obj).getParams());
	 * Map<String, Object> map = jdbc.executeQueryM(p); Long n = (Long)
	 * map.get("SIZE"); if (n != null && n > 0) { return n; } else { return 0L;
	 * } }
	 */

	/**
	 * 获取查询长度
	 * 
	 * @param left
	 * @param others
	 * @return
	 */
	private long getSize(Conditions left, Conditions... others) {

		DbBean leftBean = DbBeanCache.getDbBean(dbKey, left.getJsonBean());

		Parameter p = new Parameter();

		if (others.length == 0) { // 单表

			p = ComSQL.selectSize(leftBean);
		}

		/*
		 * else
		 * 
		 * if (others.length == 1) { // 一对多 RObject leftRO = new RObject(left);
		 * RObject rightRO = new RObject(others[0]); String leftPk =
		 * leftRO.getPk(); String rightPk = rightRO.getPk();
		 * 
		 * if (rightRO.hasField(leftPk) && !leftRO.hasField(rightPk)) { p =
		 * ComSQL.getO2Msize(left, others[0]); } else { p = ComSQL.O2OSize(left,
		 * others); }
		 * 
		 * else if (others.length == 2) {// 多对多 String leftPk = new
		 * RObject(left).getPk(); String rightPk = new
		 * RObject(others[1]).getPk(); RObject middle = new RObject(others[0]);
		 * if (middle.hasField(leftPk) && middle.hasField(rightPk)) { p =
		 * ComSQL.selectSize(others[1]); } else { p = ComSQL.O2OSize(left,
		 * others); } } else {// 一对一 p = ComSQL.O2OSize(left, others); }
		 */
		Map<String, Object> map = jdbc.get(p);
		Object obj = map.get(SQL_KEY.SIZE);
		if (obj == null) {
			return 0L;
		} else {
			return (long) obj;
		}
	}

	/*
	 * @Override public long update(Object oldEntityObject, Object
	 * newEnityObject) { Object old = this.get(oldEntityObject); if (old !=
	 * null) { Object key = new RObject(old).getPkValue(); RObject rObj = new
	 * RObject(newEnityObject); rObj.setPk(key); long n =
	 * this.update(rObj.getObject()); if (n == 1) { return 1L; } else { return
	 * 0L; } } else { return 0L; }
	 * 
	 * }
	 */
	public List<JsonBean> gets(Conditions conditions) {

		DbBean cb = DbBeanCache.getDbBean(dbKey, conditions.getJsonBean());
		String columns = ComSQL.columns(cb);
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