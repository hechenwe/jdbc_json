package com.sooncode.jdbc.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.bean.DbBean;
import com.sooncode.jdbc.bean.DbBeanCache;
import com.sooncode.jdbc.bean.ForeignKey;
import com.sooncode.jdbc.bean.JsonBean;
import com.sooncode.jdbc.constant.DATA;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.sql.ComSQL;
import com.sooncode.jdbc.sql.PageData;
import com.sooncode.jdbc.sql.Parameter;
import com.sooncode.jdbc.sql.TableRelationAnalyze;
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

	public Page getPage(long pageNum, long pageSize, Conditions conditions) {

		DbBean leftDbBean = DbBeanCache.getDbBean(dbKey, conditions.getLeftBean());
		JsonBean[] otherBeans = conditions.getOtherBeans();
		List<DbBean> otherDbBeans = new ArrayList<>();

		if (otherBeans.length > 0) {
			for (JsonBean jBean : otherBeans) {
				DbBean dbBean = DbBeanCache.getDbBean(dbKey, jBean);
				otherDbBeans.add(dbBean);
			}
		}

		int n = getRelation(leftDbBean, otherDbBeans.toArray(new DbBean[otherBeans.length]));
		String leftTableName = T2E.toTableName(leftDbBean.getBeanName());
		String leftTablePk = T2E.toColumn(leftDbBean.getPrimaryField());

		// 1.单表
		if (n == 1) {
			String columns = ComSQL.columns4One(leftDbBean);
			String where = conditions.getWhereParameter().getReadySql();
			String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + leftTableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + where;
			Parameter p = conditions.getWhereParameter();
			p.setReadySql(sql);
			List<Map<String, Object>> list = jdbc.gets(p);
			List<JsonBean> beans = new ArrayList<>();
			for (Map<String, Object> map : list) {
				JsonBean jb = new JsonBean(leftDbBean.getBeanName());
				jb.addFields(map);
				beans.add(jb);
			}
			String sizeSql = SQL_KEY.SELECT + SQL_KEY.COUNT_START + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + leftTableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + where;
			Parameter sizeP = conditions.getWhereParameter();
			sizeP.setReadySql(sizeSql);
			Map<String, Object> map = jdbc.get(sizeP);
			Long size = (Long) map.get(T2E.toField(SQL_KEY.SIZE));
			if (size == null) {
				size = 0L;
			}
			Page pager = new Page(pageNum, pageSize, size, beans);
			return pager;

		} else if (n == 2) {// 1对1

			PageData pd = one2one(leftDbBean, otherDbBeans, conditions);
			List<JsonBean> jsonBeans = findJsonBean(pd.getList(), null, null, null, leftDbBean);
			for (JsonBean jsonBean : jsonBeans) {
				for (DbBean dbBean : otherDbBeans) {
					List<JsonBean> beans = findJsonBean(pd.getList(), leftDbBean.getBeanName(), jsonBean.getId(), jsonBean.getIdVal(), dbBean);
					if (beans != null && beans.size() == 1) {
						jsonBean.addField(dbBean.getBeanName(), beans.get(0));
					}
				}
			}

			long size = pd.getSize();
			Page pager = new Page(pageNum, pageSize, size, jsonBeans);
			return pager;

		} else if (n == 3) { // 一对多

			PageData pd = one2Many(leftDbBean, otherDbBeans, conditions);
			List<JsonBean> leftJsonBeans = findJsonBean(pd.getList(), null, null, null, leftDbBean);
			for (JsonBean leftJsonBean : leftJsonBeans) {
				String id = leftJsonBean.getId();
				Object idVal = leftJsonBean.getIdVal();
				for (DbBean dbBean : otherDbBeans) {
					List<JsonBean> otherJsonBeans = findJsonBean(pd.getList(), leftDbBean.getBeanName(), id, idVal, dbBean);
					leftJsonBean.addField(dbBean.getBeanName(), otherJsonBeans);
				}
			}

			long size = pd.getSize();
			Page pager = new Page(pageNum, pageSize, size, leftJsonBeans);
			return pager;
		} else if (n == 4) {// 多对多

			List<DbBean> newDbBeans = new ArrayList<>();
			newDbBeans.add(leftDbBean);
			newDbBeans.addAll(otherDbBeans);

			String columns = ComSQL.columns(newDbBeans);
			Parameter p = conditions.getWhereParameter();
			String where = p.getReadySql();
			String tableNames = new String();
			String condition = new String();

			DbBean middleDbBean = otherDbBeans.get(0);
			DbBean rightDbBean = otherDbBeans.get(1);
			String middleTableName = T2E.toTableName(middleDbBean.getBeanName());
			String rightTableName = T2E.toTableName(rightDbBean.getBeanName());
			String rightTablePk = T2E.toColumn(rightDbBean.getPrimaryField());
			tableNames = leftTableName + STRING.SPACING + STRING.COMMA + STRING.SPACING + middleTableName + STRING.SPACING + STRING.COMMA + STRING.SPACING + rightTableName + STRING.SPACING;
			List<ForeignKey> fkes = middleDbBean.getForeignKeies();
			for (ForeignKey f : fkes) {
				if (f.getReferDbBeanName().toUpperCase().equals(leftTableName.toUpperCase())) {
					String fk = T2E.toColumn(f.getForeignProperty());
					condition = condition + SQL_KEY.AND + middleTableName + STRING.POINT + fk + STRING.SPACING + STRING.EQ + STRING.SPACING + leftTableName + STRING.POINT + leftTablePk + STRING.SPACING;

				}
				if (f.getReferDbBeanName().toUpperCase().equals(rightTableName.toUpperCase())) {
					String fk = T2E.toColumn(f.getForeignProperty());
					condition = condition + SQL_KEY.AND + middleTableName + STRING.POINT + fk + STRING.SPACING + STRING.EQ + STRING.SPACING + rightTableName + STRING.POINT + rightTablePk + STRING.SPACING;

				}
			}

			String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + tableNames + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + condition + where;
			p.setReadySql(sql);
			List<Map<String, Object>> list = jdbc.gets(p);

			List<JsonBean> leftJsonBeans = findJsonBean(list, null, null, null, leftDbBean);
			for (JsonBean jsonBean : leftJsonBeans) {
				List<JsonBean> middleJsonBeans = findJsonBean(list, leftDbBean.getBeanName(), jsonBean.getId(), jsonBean.getIdVal(), middleDbBean);
				List<JsonBean> jsonBeans = new LinkedList<>();
				for (JsonBean middleJsonBean : middleJsonBeans) {
					List<JsonBean> rightJsonBeans = findJsonBean(list, middleDbBean.getBeanName(), middleJsonBean.getId(), middleJsonBean.getIdVal(), rightDbBean);
					jsonBeans.addAll(rightJsonBeans);

				}
				jsonBean.addField(rightDbBean.getBeanName(), jsonBeans);
			}
			String sizeSql = SQL_KEY.SELECT + SQL_KEY.COUNT_START + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + tableNames + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + condition + where;
			Parameter sizeP = conditions.getWhereParameter();
			sizeP.setReadySql(sizeSql);
			Map<String, Object> map = jdbc.get(sizeP);
			Long size = (Long) map.get(T2E.toField(SQL_KEY.SIZE));
			if (size == null) {
				size = 0L;
			}
			Page pager = new Page(pageNum, pageSize, size, leftJsonBeans);

			return pager;
		} else if (n == 5) {// 未知
			return null;
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

	public List<JsonBean> gets(Conditions conditions) {

		DbBean cb = DbBeanCache.getDbBean(dbKey, conditions.getLeftBean());
		String columns = ComSQL.columns4One(cb);
		Parameter where = conditions.getWhereParameter();
		Parameter p = new Parameter();
		String tableName = T2E.toTableName(cb.getBeanName());
		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + tableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + where.getReadySql();
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

	/**
	 * 获取表关系模式
	 * 
	 * @param leftDbBean
	 * @param otherBeans
	 * @return 单表 ：1 ; 一对一：2 一对多：3 ; 多对多：4 ;未知 ：5
	 */
	private int getRelation(DbBean leftDbBean, DbBean... otherBeans) {
		if (TableRelationAnalyze.isOne(leftDbBean, otherBeans)) {
			return 1;
		} else if (TableRelationAnalyze.isOne2One(leftDbBean, otherBeans)) {
			return 2;
		} else if (TableRelationAnalyze.isOne2Many(leftDbBean, otherBeans)) {
			return 3;
		} else if (TableRelationAnalyze.isMany2Many(leftDbBean, otherBeans)) {
			return 4;
		} else {
			return 5;
		}

	}

	private List<JsonBean> findJsonBean(List<Map<String, Object>> list, String mainBeanName, String id, Object idVal, DbBean dbBean) {
		if (id != null && idVal != null) {
			List<Map<String, Object>> newlist = new LinkedList<>();
			for (Map<String, Object> map : list) {
				Object thisVal = map.get(mainBeanName + STRING.DOLLAR + id);
				if (thisVal.toString().equals(idVal.toString())) {
					newlist.add(map);
				}
			}
			list = newlist;
		}

		String dbBeanName = dbBean.getBeanName();
		String pkName = dbBean.getPrimaryField();
		List<JsonBean> jsonBeans = new LinkedList<>();
		String str = new String();
		for (Map<String, Object> map : list) {
			JsonBean jsonBean = new JsonBean();
			for (Entry<String, Object> en : map.entrySet()) {
				String key = en.getKey();
				Object val = en.getValue();
				String[] strs = key.split(STRING.ESCAPE_DOLLAR);
				if (strs.length > 0) {
					String beanName = strs[0];
					String pr = strs[1];

					if (dbBeanName.toUpperCase().equals(beanName.toUpperCase())) {
						jsonBean.addField(pr, val);
						if (pkName.toUpperCase().equals(pr.toUpperCase())) {
							jsonBean.setId(pkName);
							jsonBean.setIdVal(val);
						}
					}
				}
			}
			if (jsonBeans.size() == 0) {
				jsonBeans.add(jsonBean);
				str = str + jsonBean.getIdVal().toString() + STRING.AT;
			} else {
				if (!str.contains(jsonBean.getIdVal().toString())) {
					jsonBeans.add(jsonBean);
					str = str + jsonBean.getIdVal().toString() + STRING.AT;
				}
			}

		}

		return jsonBeans;

	}
	
	/**
	 * 一对多
	 * 
	 * @param leftDbBean
	 * @param otherDbBeans
	 * @param conditions
	 * @return
	 */
	private PageData one2Many(DbBean leftDbBean, List<DbBean> otherDbBeans, Conditions conditions) {
		PageData pd = new PageData();
		String leftTableName = T2E.toTableName(leftDbBean.getBeanName());
		String leftTablePk = T2E.toColumn(leftDbBean.getPrimaryField());
		List<DbBean> newDbBeans = new ArrayList<>();
		newDbBeans.add(leftDbBean);
		newDbBeans.addAll(otherDbBeans);

		String columns = ComSQL.columns(newDbBeans);
		Parameter p = conditions.getWhereParameter();
		String where = p.getReadySql();
		String otherTableName = new String();
		String condition = new String();
		for (DbBean dbBean : otherDbBeans) {
			String tableName = T2E.toTableName(dbBean.getBeanName());

			otherTableName = otherTableName + STRING.SPACING + STRING.COMMA + STRING.SPACING + tableName + STRING.SPACING;
			List<ForeignKey> fkes = dbBean.getForeignKeies();
			for (ForeignKey f : fkes) {
				if (f.getReferDbBeanName().toUpperCase().equals(leftTableName.toUpperCase())) {
					String fk = T2E.toColumn(f.getForeignProperty());
					condition = condition + SQL_KEY.AND + tableName + STRING.POINT + fk + STRING.SPACING + STRING.EQ + STRING.SPACING + leftTableName + STRING.POINT + leftTablePk + STRING.SPACING;

				}
			}
		}
		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + leftTableName + otherTableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + condition + where;
		p.setReadySql(sql);
		List<Map<String, Object>> list = jdbc.gets(p);
		pd.setList(list);

		String sizeSql = SQL_KEY.SELECT + SQL_KEY.COUNT_START + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + leftTableName + otherTableName + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + condition + where;
		Parameter sizeP = conditions.getWhereParameter();
		sizeP.setReadySql(sizeSql);
		Map<String, Object> map = jdbc.get(sizeP);
		Long size = (Long) map.get(T2E.toField(SQL_KEY.SIZE));
		if (size != null) {
			pd.setSize(size);
		} else {
			pd.setSize(0);
		}
		return pd;
	}

	/**
	 * 一对一
	 * 
	 * @param leftDbBean
	 * @param otherDbBeans
	 * @param conditions
	 * @return
	 */
	private PageData one2one(DbBean leftDbBean, List<DbBean> otherDbBeans, Conditions conditions) {
		PageData pd = new PageData();
		String leftTableName = T2E.toTableName(leftDbBean.getBeanName());

		List<DbBean> newDbBeans = new ArrayList<>();
		newDbBeans.add(leftDbBean);
		newDbBeans.addAll(otherDbBeans);

		String columns = ComSQL.columns(newDbBeans);
		Parameter p = conditions.getWhereParameter();
		String where = p.getReadySql();
		String otherTableNames = new String();
		String condition = new String();

		List<ForeignKey> leftPkes = leftDbBean.getForeignKeies();
		int n = 0;
		for (ForeignKey fk : leftPkes) {
			String fkName = T2E.toColumn(fk.getForeignProperty());
			String referTableName = T2E.toTableName(fk.getReferDbBeanName());
			for (DbBean dbBean : otherDbBeans) {
				String otherTableName = T2E.toTableName(dbBean.getBeanName());
				if (n == 0) {
					otherTableNames = otherTableNames + STRING.SPACING + STRING.COMMA + STRING.SPACING + otherTableName + STRING.SPACING;
				}
				String otherTablePkName = T2E.toColumn(dbBean.getPrimaryField());

				if (referTableName.equals(otherTableName)) {
					condition = condition + SQL_KEY.AND + leftTableName + STRING.POINT + fkName + STRING.SPACING + STRING.EQ + STRING.SPACING + otherTableName + STRING.POINT + otherTablePkName + STRING.SPACING;
				}

			}
			n++;
		}

		String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM + leftTableName + otherTableNames + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + condition + where;
		p.setReadySql(sql);
		List<Map<String, Object>> list = jdbc.gets(p);
		pd.setList(list);

		String sizeSql = SQL_KEY.SELECT + SQL_KEY.COUNT_START + SQL_KEY.AS + SQL_KEY.SIZE + SQL_KEY.FROM + leftTableName + otherTableNames + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + condition + where;
		Parameter sizeP = conditions.getWhereParameter();
		sizeP.setReadySql(sizeSql);
		Map<String, Object> map = jdbc.get(sizeP);
		Long size = (Long) map.get(T2E.toField(SQL_KEY.SIZE));
		if (size != null) {
			pd.setSize(size);
		} else {
			pd.setSize(0);
		}
		return pd;
	}

}