package com.sooncode.jdbc.db;
/**
 * 数据库 配置文件辅助处理类 
 * @author he chen
 *
 */
public class DB4Parperties {

	public static final String USER="user";
	public static final String PASSWORD="password";
	public static final String UNPOOLED_DATA_SOURCE="unpooledDataSource";
	public static final String POOLED_DATA_SOURCE="pooledDataSource";
	 
	
	public static String getMysqlUrl(String ip,String port,String dataName ,String encodeing){
		String mysqlUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dataName  + "?useUnicode=true&characterEncoding=" + encodeing;
		return mysqlUrl;
	}
	
}
