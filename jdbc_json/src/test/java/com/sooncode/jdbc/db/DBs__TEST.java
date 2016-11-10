package com.sooncode.jdbc.db;

 

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.Test;
 
 

 
public class DBs__TEST {
	private static Logger logger = Logger.getLogger("DBs_TEST.class");
	@Test
	public void getConnection() {
	    
		Connection con1 = DBs.getConnection(null);
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功："+(con1!=null));
		
		Connection con2 = DBs.getConnection("default");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功："+(con2!=null));
		
		Connection con3 = DBs.getConnection("jdbc");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功："+(con3!=null));
		
		Connection con4 = DBs.getConnection("JDBC");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功："+(con4!=null));
		
		Connection con5 = DBs.getConnection("DEFAULT");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功："+(con5!=null));
		
		Connection con6 = DBs.getConnection("");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功："+(con6!=null));
	}
	
	
	
	@Test
	public void getConnection4pressureTest() {
		
		 
		for(int i=0;i<10000;i++){
			Connection con = DBs.getConnection("default");
			try {
				con.close(); //关闭连接资源
			} catch (SQLException e) {
				e.printStackTrace();
			}
			logger.info("【Jdbc4Json  test】 ("+i+")获取数据库连接是否成功："+(con!=null));
		}
		
		 
	}
	
	
	
	 
}
