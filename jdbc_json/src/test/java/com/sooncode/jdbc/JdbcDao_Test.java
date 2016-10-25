package com.sooncode.jdbc;

 
 

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.dao.JdbcDao;
import com.sooncode.jdbc.dao.JdbcDaoFactory;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.sql.condition.Sort;
 
public class JdbcDao_Test {
    private static Logger logger = Logger.getLogger("JdbcDaoTest.class");
	private JdbcDao jdbcDao = JdbcDaoFactory.getJdbcDao();
	 
	
	@Test
	public void save(){
		String json = "{\"teacher\":{\"teacherId\":345,\"teacherName\":\"hechen\",\"clazzId\":\"001\"}}";
		Long b = jdbcDao.save(json);
		logger.info(b);
		
	}
	
	 
	@Test 
	public void get4json (){
		String json = "{\"teacher\":{\"teacherName\":\"hechen\"}}";
		Conditions c = new Conditions(json);
		c.setOderBy("clazzId", Sort.ASC);
		String rJson = jdbcDao.get(c);
		logger.info(rJson);
	}
}
