package com.sooncode.jdbc;
 
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.dao.JdbcDao;
import com.sooncode.jdbc.dao.JdbcDaoFactory;
import com.sooncode.jdbc.json.JsonBean;
 
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.sql.condition.Sort;
import com.sooncode.jdbc.util.Page;
 
public class JdbcDao_Test {
    private static Logger logger = Logger.getLogger("JdbcDaoTest.class");
	private JdbcDao jdbcDao = JdbcDaoFactory.getJdbcDao();
	 
	
	@Test
	public void save(){
	   String json = "{\"teacher\":{\"createDate\":\"2016-10-26 14:15:22\",\"teacherName\":\"hechen\",\"clazzId\":\"001\"}}";
		JsonBean  t = new JsonBean(json);
		t.updateField("createDate", new Date());
		t.addField("teacherAge", 45);
		t.addField("address", "beijing");
		t.updateField("teacherName", "TOM");
		Long b = jdbcDao.save(t);
		logger.info(b);
		
	}
	@Test
	public void update(){
		String json = "{\"teacher\":{\"teacherId\":345,\"teacherName\":\"和陈\",\"clazzId\":\"001\"}}";
		JsonBean  t = new JsonBean(json);
		Long b = jdbcDao.update(t);
		logger.info(b);
		
	}
	
	@Test
	public void delete(){
		String json = "{\"teacher\":{\"teacherId\":345}}";
		JsonBean  t = new JsonBean(json);
		Long b = jdbcDao.delete(t);
		logger.info(b);
		
	}
	
	 
	@Test 
	public void get(){
		String json = "{\"teacher\":{\"teacherName\":\"hechen\"}}";
		JsonBean j = new JsonBean(json);
		Conditions c = new Conditions(j);
		c.setOderBy("clazzId", Sort.ASC);
		List<JsonBean> list = jdbcDao.get(c);
		logger.info(list);
	}
	
	
	@Test 
	public void getPage(){
		String json = "{\"teacher\":{\"teacherName\":\"TOM\"}}";
		JsonBean j = new JsonBean(json);
		Conditions c = new Conditions(j);
		c.setOderBy("clazzId", Sort.ASC);
		Page page = jdbcDao.getPage(1L,1L,c);
		logger.info(page.getList());
	}
}
