package com.sooncode.jdbc.sql;

import java.util.Map;

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
import com.sooncode.jdbc.cglib.DbBean;
import com.sooncode.jdbc.cglib.DbBeanCache;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.sql.condition.Conditions;

/**
 * 查询引擎
 * 
 * @author pc
 *
 */
public class SelectEngine {
	private String dbKey;
	private Jdbc jdbc ;

	public SelectEngine(String dbKey) {
		this.dbKey = dbKey;
		this.jdbc  = JdbcFactory.getJdbc(this.dbKey);
	}

	public long getMainSize(Conditions leftCond) {

		DbBean leftBean = DbBeanCache.getDbBean(dbKey, leftCond.getJsonBean());
		Parameter p = new Parameter();
		p = ComSQL.selectSize(leftBean);
		Map<String, Object> map = jdbc.get(p);
		Object obj = map.get(SQL_KEY.SIZE);
		if (obj == null) {
			return 0L;
		} else {
			return (long) obj;
		}
	}

	public long getOne2oneSize(Conditions leftCond, Conditions rightCond) {
		DbBean leftBean = DbBeanCache.getDbBean(dbKey, leftCond.getJsonBean());
		DbBean rightBean = DbBeanCache.getDbBean(dbKey, rightCond.getJsonBean());
		
		String columns = ComSQL.columns(leftBean,rightBean);
		

		return 0;
	}

	public long getOne2manySize(Conditions leftCond, Conditions rightCond) {

		return 0;
	}

	public long getOne2manySize(Conditions leftCond, Conditions middleCond, Conditions rightCond) {

		return 0;
	}
}
