package com.sooncode.jdbc;

 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.dao.JdbcDao;
import com.sooncode.jdbc.dao.JdbcDaoFactory;
import com.sooncode.jdbc.sql.condition.And;
import com.sooncode.jdbc.sql.condition.Cond;
import com.sooncode.jdbc.sql.condition.Conditions;
import com.sooncode.jdbc.sql.condition.Or;
import com.sooncode.jdbc.sql.condition.OrderBy;
import com.sooncode.jdbc.sql.condition.Sort;
import com.sooncode.jdbc.sql.condition.sign.EqualSign;
import com.sooncode.jdbc.sql.condition.sign.DateFormatSign;
import com.sooncode.jdbc.sql.condition.sign.LikeSign;
import com.sooncode.jdbc.sql.condition.sign.NullSign;
import com.sooncode.jdbc.util.Pager;

import example.entity.User;
 
 
public class JdbcDao_Test {
    private static Logger logger = Logger.getLogger("JdbcDaoTest.class");
	private JdbcDao jdbcDao = JdbcDaoFactory.getJdbcDao();
	 
	
	@Test
	public void save(){
		User u1 = new  User();
		u1.setName("AFJLSLDFJSFSF");
		Long b = jdbcDao.save(u1);
		
		logger.info(b);
		
	}
	
	@Test
	public void getPager(){
		Long pagerNumber = 1L;
		Long pagerSize = 10L;
		User u1 = new  User();
		u1.setName("AAA");
		Pager<?> p = jdbcDao.getPager(pagerNumber, pagerSize, u1);
		
		logger.info(p);
		
		
	}
	@Test
	public void getPager2(){
		Long pagerNumber = 1L;
		Long pagerSize = 10L;
		Cond name = new Cond("name", LikeSign.R_LIKE, "AA");
		Cond id = new Cond("id", new Integer[]{1079,1080,1081});
		Cond age = new Cond("age",EqualSign.GT,3);
		Cond pass = new  Cond("pass",LikeSign.LIKE,"h");
		
		And nameANDid = new And(name,id);
		Or ageORpass = new Or(age,pass);
		
		Cond o = new And(nameANDid,ageORpass);
		
		Cond o2 = new And(name,id).and(new Or(age,pass));
		
		
		Cond o3 = new And(new And(name,id),new Or(age,pass)); 
		
		Cond a = new And(name,id,age,pass);
		
		Cond o4 = new Or(name,id,age,pass).and(new Cond("note", LikeSign.LIKE, "haha"));
		
		Cond creatDate = new Cond("createDate",EqualSign.GT,DateFormatSign.YYYY_MM_DD("2012-09-04"));
		 
		Cond creatDate2 = new Cond("createDate",EqualSign.GT,new DateFormatSign("%Y-%d"),"2016-05").and(new Cond("id", NullSign.IS_NOT_NULL)).orderBy(new OrderBy("id", Sort.ASC),new OrderBy("name", Sort.DESC));
		
		Pager<?> p = jdbcDao.getPager(pagerNumber, pagerSize,User.class, creatDate2);
		
		logger.info(p.getLists());
		
		 
		
	}
	
	@Test
	public void count(){
		
		User u = new User();
		u.setAge(100);
		long n = jdbcDao.count("*", u);
		logger.info(n);
	}
	@Test 
	public void get4json (){
		String json = "{\"teacher\":{\"teacherName\":\"hechen\"}}";
		String rJson = jdbcDao.get(json);
		logger.info(rJson);
	}
}
