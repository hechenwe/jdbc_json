package com.sooncode.jdbc.dao;
import java.util.List;
import com.sooncode.jdbc.bean.JsonBean;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.util.Page;

/**
 * Jdbc Dao 服务接口
 * 
 * @author pc
 * 
 */
public interface JdbcDaoInterface {
 

	public Page getPage(long pageNum, long pageSize, Conditions conditions);

	public long save(JsonBean jsonBean)  ;

	public boolean saves(List<JsonBean> jsonBeans) ;

	public boolean updates(List<JsonBean> jsonBeans)  ;

	public long saveOrUpdate(JsonBean jsonBean)  ;

	public long update(JsonBean jsonBean)  ;

	public long delete(JsonBean jsonBean)  ;

	public List<JsonBean> gets(Conditions conditions)  ;
 
	 
}