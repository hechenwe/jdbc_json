package com.sooncode.jdbc;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.dao.JdbcDao;
import com.sooncode.jdbc.dao.JdbcDaoFactory;
import com.sooncode.jdbc.json.JsonBean;
 
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.sql.condition.Sort;
import com.sooncode.jdbc.sql.condition.sign.EqualSign;
import com.sooncode.jdbc.util.Page;
 
public class JdbcDao_Test {
    private static Logger logger = Logger.getLogger("JdbcDaoTest.class");
	private JdbcDao jdbcDao = JdbcDaoFactory.getJdbcDao();
	 
	
	@Test
	public void save(){
	   String json = "{\"teacher\":{\"createDate\":\"2016-10-26 14:15:22\",\"teacherName\":\"hechen\",\"clazzId\":\"001\",\"sex\":\"1\"}}";
		JsonBean  t = new JsonBean(json);
		t.updateField("createDate", new Date());
		t.addField("teacherAge", 45);
		t.addField("address", "beijing");
		t.updateField("teacherName", "TOM");
		Long b = jdbcDao.save(t);
		logger.info(b);
		
	}
	@Test
	public void save2(){
		String json = "{\"identity\":{\"id\":\"001\",}}";
		JsonBean  t = new JsonBean(json);
		Long b = jdbcDao.save(t);
		logger.info(b);
		
	}
	@Test
	public void saves(){
		String json = "{\"teacher\":{\"createDate\":\"2016-10-26 14:15:22\",\"teacherName\":\"hechen6578\",\"clazzId\":\"001\"}}";
		JsonBean  t = new JsonBean(json);
		 
		String json2 = "{\"teacher\":{\"createDate\":\"2016-10-26 14:15:23\",\"teacherName\":\"hechen2345\",\"clazzId\":\"001\"}}";
		JsonBean  t2 = new JsonBean(json2);
		
		List<JsonBean> beans = new ArrayList<>();
		beans.add(t);
		beans.add(t2);
		
		boolean b = jdbcDao.saves(beans);
		logger.info(b);
		
	}
	@Test
	public void saveOrUpdate(){
		String json = "{\"teacher\":{\"teacherName\":\"ni mei de \"}}";
		JsonBean  t = new JsonBean(json);
		t.addField("teacherId", 11);
		Long b = jdbcDao.saveOrUpdate(t);
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
	public void updates(){
		String json1 = "{\"teacher\":{\"teacherId\":3,\"teacherName\":\"AA\"}}";
		JsonBean  t1 = new JsonBean(json1);
		String json2 = "{\"teacher\":{\"teacherId\":4,\"teacherName\":\"BB\"}}";
		JsonBean  t2 = new JsonBean(json2);
		List<JsonBean> list = new ArrayList<>();
		list.add(t1);
		list.add(t2);
		boolean b = jdbcDao.updates(list);
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
		String json = "{\"teacher\":{\"teacherName\":\"TOM\"}}";
		JsonBean j = new JsonBean(json);
		Conditions c = new Conditions(j);
		//c.setCondition("teacherName", EqualSign.NOT_EQ);
		c.setOderBy("clazzId", Sort.ASC);
		List<JsonBean> list = jdbcDao.gets(c);
		logger.info(list);
	}
	
	
	@Test 
	public void getPage1(){  //单表 
		String json = "{\"teacher\":{\"teacherName\":\"TOM\"}}";
		JsonBean j = new JsonBean(json);
		Conditions c = new Conditions(j);
		c.setOderBy("teacher.clazzId", Sort.ASC);
		Page page = jdbcDao.getPage(1L,1L,c);
		logger.info(page.getList());
	}
	
	
	@Test 
	public void getPage2(){  //1对1
		 
		JsonBean student = new JsonBean("student");
		JsonBean identity = new JsonBean("identity");
		Conditions c = new Conditions(student,identity);
	//	c.setOderBy("teacher.clazzId", Sort.ASC);
		Page page = jdbcDao.getPage(1L,1L,c);
		logger.info(page.getList());
	}
	@Test 
	public void getPage3(){  //1对多
		
		JsonBean clazz = new JsonBean("clazz");
		JsonBean student = new JsonBean("student");
		Conditions c = new Conditions(clazz,student);
		//	c.setOderBy("teacher.clazzId", Sort.ASC);
		Page page = jdbcDao.getPage(1L,1L,c);
		logger.info(page.getList());
	}
	
	
}
