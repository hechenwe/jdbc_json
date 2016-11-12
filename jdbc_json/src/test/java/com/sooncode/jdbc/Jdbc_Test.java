package com.sooncode.jdbc;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.sql.Parameter;

public class Jdbc_Test {
	private static Logger logger = Logger.getLogger("Jdbc_Test.class");
	private Jdbc jdbc = JdbcFactory.getJdbc();

	@Test
	public void executeProcedure() {
		String sql = "{call proc_name2(?,?)}";
		Integer in = 1;
		Object r = jdbc.procedure(sql, in);
		logger.info(r);
	}
	
	@Test
	public void get() {
		String sql = "SELECT \n" +
				"	TEACHER_ID , \n" +
				"	TEACHER_AGE , \n" +
				"	TEACHER_NAME , \n" +
				"	CLAZZ_ID , \n" +
				"	SEX\n" +
				"	FROM TEACHER\n" +
				"	WHERE 1=1\n" +
				"	AND TEACHER.TEACHER_NAME = ? ORDER BY CLAZZ_ID ASC ";
		Parameter p = new Parameter(sql);
		p.addParameter("hechen");
		long t1 = System.nanoTime();
		List<Map<String,Object>> list = jdbc.gets(p);
		long t2 = System.nanoTime();
		logger.info("耗时："+(t2-t1)+"(ns)");
		logger.info(list);
		
		long t3 = System.nanoTime();
		List<Map<String,Object>> list2 = jdbc.gets(p);
		long t4 = System.nanoTime();
		logger.info("耗时："+(t4-t3)/1000000+"(ms)");
		logger.info(list2);
	}
	 
}
