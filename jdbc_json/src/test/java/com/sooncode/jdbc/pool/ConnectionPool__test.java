package com.sooncode.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool__test {
	
	
	
	
	public static void main(String[] args) {
	
		ConnectionPool connPool = new ConnectionPool("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@*.*.*.*", "name", "password");
	 
		try {
			Connection conn = connPool.getConnection();
		} catch (SQLException ex1) {
			ex1.printStackTrace();
		}
	
	}
	
}

