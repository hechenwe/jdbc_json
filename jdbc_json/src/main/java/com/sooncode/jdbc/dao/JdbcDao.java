package com.sooncode.jdbc.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections.list.TreeList;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.cglib.DbBean;
import com.sooncode.jdbc.cglib.DbBeanCache;
import com.sooncode.jdbc.cglib.ForeignKey;
import com.sooncode.jdbc.cglib.Index;
import com.sooncode.jdbc.constant.DATA;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
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

		int n = getRelation(leftDbBean, otherDbBeans);
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
			Long size = getSize(conditions);
			Page pager = new Page(pageNum, pageSize, size, beans);
			return pager;

		} else if (n == 2) {// 1对1

			List<Map<String, Object>> list = one2Many(leftDbBean, otherDbBeans, conditions);
			List<JsonBean> jsonBeans = new LinkedList<>();
			for (Map<String, Object> map : list) {
				JsonBean jsonBean = new JsonBean();
				for (Entry<String, Object> en : map.entrySet()) {
					String key = en.getKey();
					Object val = en.getValue();
					String[] strs = key.split(STRING.ESCAPE_DOLLAR);
					if (strs.length > 0) {
						String pr = strs[1];
						jsonBean.addField(pr, val);
					}
				}
				jsonBeans.add(jsonBean);
			}

			Long size = getSize(conditions);
			Page pager = new Page(pageNum, pageSize, size, jsonBeans);
			return pager;

		} else if (n == 3) { // 一对多

			List<Map<String, Object>> list = one2Many(leftDbBean, otherDbBeans, conditions);
			List<JsonBean> leftJsonBeans = findJsonBean(list, null, null, null, leftDbBean);
			for (JsonBean leftJsonBean : leftJsonBeans) {
				String id = leftJsonBean.getId();
				Object idVal = leftJsonBean.getIdVal();
				for (DbBean dbBean : otherDbBeans) {
					List<JsonBean> otherJsonBeans = findJsonBean(list, leftDbBean.getBeanName(), id, idVal, dbBean);
					leftJsonBean.addField(dbBean.getBeanName(), otherJsonBeans);
				}
			}

			Long size = getSize(conditions);
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

			String sql = SQL_KEY.SELECT + columns + SQL_KEY.FROM  + tableNames + SQL_KEY.WHERE + SQL_KEY.ONE_EQ_ONE + condition + where;
			p.setReadySql(sql);
			List<Map<String, Object>> list = jdbc.gets(p);

			List<JsonBean> leftJsonBeans = findJsonBean(list, null, null, null, leftDbBean);
			for (JsonBean jsonBean : leftJsonBeans) {
				List<JsonBean> middleJsonBeans = findJsonBean(list, leftDbBean.getBeanName(), jsonBean.getId(),jsonBean.getIdVal(), middleDbBean);
				List<JsonBean> jsonBeans = new LinkedList<>();
				for (JsonBean middleJsonBean : middleJsonBeans) {
					List<JsonBean> rightJsonBeans = findJsonBean(list, middleDbBean.getBeanName(), middleJsonBean.getId(), middleJsonBean.getIdVal(), rightDbBean);
					jsonBeans.addAll(rightJsonBeans);
				
				}
				jsonBean.addField(rightDbBean.getBeanName(),jsonBeans);
			}
			
			Long size = getSize(conditions);
			Page pager = new Page(pageNum, pageSize, size, leftJsonBeans);
			
			
			return pager;
		} else if (n == 5) {// 未知
			return null;
		} else {
			return null;
		}
	}

	private List<Map<String, Object>> one2Many(DbBean leftDbBean, List<DbBean> otherDbBeans, Conditions conditions) {

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
		return list;
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
	private long getSize(Conditions conditions) {

		return 0;

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
	private int getRelation(DbBean leftDbBean, List<DbBean> otherBeans) {

		String leftDbBeanName = leftDbBean.getBeanName();

		if (otherBeans.size() == 0) {// 1個表
			return 1;// 单表
		} else if (otherBeans.size() == 1) { // 2個表
			DbBean rightBean = otherBeans.get(0);
			List<ForeignKey> list = rightBean.getForeignKeies();
			for (ForeignKey f : list) {
				String referDbBeanName = f.getReferDbBeanName();

				if (leftDbBeanName.toUpperCase().equals(referDbBeanName.toUpperCase())) {
					if (f.isUnique() == true) {
						return 2;// 一对一
					} else {
						return 3;// 一对多
					}
				}
			}
			return 5;// 未知
		} else if (otherBeans.size() == 2) {// 3個表
			DbBean middleBean = otherBeans.get(0);
			DbBean rightBean = otherBeans.get(1);
			List<ForeignKey> middlelist = middleBean.getForeignKeies();
			List<ForeignKey> rightList = rightBean.getForeignKeies();
			int m1to1 = 0;
			int m1to3 = 0;
			if (middlelist.size() >= 2) {
				int n = 0;
				for (ForeignKey f : middlelist) {
					if (f.getReferDbBeanName().toUpperCase().equals(rightBean.getBeanName().toUpperCase()) || f.getReferDbBeanName().toUpperCase().equals(leftDbBean.getBeanName().toUpperCase())) {
						n++;
					}
					if (f.getReferDbBeanName().toUpperCase().equals(leftDbBean.getBeanName().toUpperCase())) {
						if (f.isUnique() == true) {
							m1to1++;
						} else {
							m1to3++;
						}
					}
				}
				if (n == 2) {
					return 4;// 多对多
				} else {
					return 5;/// 未知
				}
			}

			if (middlelist.size() == 1) {
				for (ForeignKey f : middlelist) {
					if (f.getReferDbBeanName().toUpperCase().equals(leftDbBean.getBeanName().toUpperCase())) {
						if (f.isUnique() == true) {
							m1to1++;
						} else {
							m1to3++;
						}
					}
				}

				int r1to1 = 0;
				int r1to3 = 0;
				if (rightList.size() > 0) {
					for (ForeignKey f : rightList) {

						if (f.getReferDbBeanName().toUpperCase().equals(leftDbBean.getBeanName().toUpperCase())) {
							if (f.isUnique() == true) {
								r1to1++;
							} else {
								r1to3++;
							}
						}
					}
				}
				if (m1to1 == 1 && r1to1 == 1) {
					return 2;//// 一对一
				} else if (m1to3 == 1 && r1to3 == 1) {
					return 3;//// 一对多
				} else {
					return 5;
				}
			}
			return 5;
		} else {// 3個以上的表
			int n = 0;
			int m = 0;
			for (DbBean bean : otherBeans) {
				List<ForeignKey> fkes = bean.getForeignKeies();
				for (ForeignKey f : fkes) {
					if (f.getReferDbBeanName().equals(leftDbBean.getBeanName())) {
						if (f.isUnique() == true) {
							n++; // 一對一
						} else {
							m++;// 多對多
						}
					}
				}
			}
			if (n == otherBeans.size()) {
				return 2;// 一对一
			}

			if (m == otherBeans.size()) {
				return 3;// 一对多
			}

			return 5;

		}
	}

}