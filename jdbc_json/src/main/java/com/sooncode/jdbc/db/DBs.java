package com.sooncode.jdbc.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.sooncode.jdbc.bean.ForeignKey;
import com.sooncode.jdbc.bean.Index;
import com.sooncode.jdbc.constant.DATA;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.util.T2E;

/**
 * 数据库连接管理
 * 
 * @author pc
 *
 */
public class DBs {
	/**
	 * 数据库配置文件名称后缀"_db.properties"
	 */
	private static final String DB_PROPERTIES = "_db.properties";
	/** 数据源缓存 */
	private static Map<String, DB> dBcache = new Hashtable<>();

	/** 源码所在路径 */
	private static String classesPath = null;

	/** c3p0 配置文件 */
	public static Properties c3p0properties;

	/** 数据库 c3p0 连接池 多数据源 缓存 */
	private static Map<String, DataSource> dataSources;

	public final static Logger logger = Logger.getLogger("DBs.class");

	/**
	 * 初始化
	 */
	static {
		dBcache.clear();
		if (classesPath == null) {
			classesPath = new DBs().getClassesPath();
		}
		List<String> dbConfig = getDbConfig();
		Map<String, DataSource> dss = new HashMap<>();

		Properties c3p0 = new Properties();
		InputStreamReader in;

		try {
			in = new InputStreamReader(new FileInputStream(classesPath + "c3p0.properties"), "utf-8");
			c3p0.load(in);
			c3p0properties = c3p0;
		} catch (Exception e) {
			c3p0properties = null;
			logger.debug("【Jdbc4Json】: 加载c3p0  配置文件失败! ");
		}

		for (String str : dbConfig) {

			PropertiesUtil pu = new PropertiesUtil(classesPath + str);
			DB db = new DB();
            String key = pu.getString("KEY");
			db.setKey(key == null ? DATA.DEFAULT_KEY :key);
			db.setDriver(pu.getString("DRIVER"));
			db.setIp(pu.getString("IP"));
			db.setPort(pu.getString("PORT"));
			db.setDataName(pu.getString("DATA_NAME"));
			db.setEncodeing(pu.getString("ENCODEING"));
			db.setUserName(pu.getString("USERNAME"));
			db.setPassword(pu.getString("PASSWORD"));
			db.setTransactionIsolation(pu.getString("TRANSACTION_ISOLATION"));
			dBcache.put(db.getKey(), db);

			Class<?> DataSources;
			try {
				DataSources = Class.forName("com.mchange.v2.c3p0.DataSources");
			} catch (ClassNotFoundException e) {
				DataSources = null;
				logger.info("【Jdbc4Json】: 没有添加c3p0的jar包 , DataSources 加载失败");
			}

			if (DataSources != null) {

				try {
					// 加载驱动类
					Class.forName(db.getDriver());
					String jdbcUrl = DB4Parperties.getMysqlUrl(db.getIp(), db.getPort(), db.getDataName(), db.getEncodeing());
					Properties p = new Properties();
					p.setProperty(DB4Parperties.USER, db.getUserName());
					p.setProperty(DB4Parperties.PASSWORD, db.getPassword());
					Method unpooledDataSource = DataSources.getMethod(DB4Parperties.UNPOOLED_DATA_SOURCE, String.class, Properties.class);
					DataSource ds = (DataSource) unpooledDataSource.invoke(null, jdbcUrl, p);
					Method pooledDataSource = DataSources.getMethod(DB4Parperties.POOLED_DATA_SOURCE, DataSource.class, Properties.class);
					ds = (DataSource) pooledDataSource.invoke(null, ds, p);
					dss.put(db.getKey(), ds);
					logger.info("【Jdbc4Json】: 已添加c3p0连接池 ;数据库" + db.getDataName() + (db.getKey().equals(DATA.DEFAULT_KEY) == true ? "（默认首选数据库）" : ""));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		dataSources = dss;
	}

	/**
	 * 获取数据库连接
	 * @param dbKey 代表连接数据库参数的关键字
	 * @return 数据库连接
	 */
	public static synchronized Connection getConnection(String dbKey) {
		if (dbKey == null || dbKey.trim().equals("")) {
			logger.error("【Jdbc4Json】:数据库 dbKey参数错误（dbKey为null 或者 dbKey 为空字符串）");
			return null;
		}

		Connection connection = null;
		if (dataSources != null && dataSources.size() != 0) { // 已集成c3p0连接池
			try {
				connection = dataSources.get(dbKey).getConnection();
				setTransactionIsolation(dbKey, connection);
			} catch (SQLException e) {
				logger.error("【Jdbc4Json】:从c3p0链接池中 获取数据库连接失败！ " + e.getMessage());
				return null;
			}
		} else {// 没有集成c3p0连接池
			DB db = DBs.dBcache.get(dbKey);
			if (db == null) {
				logger.error("【Jdbc4Json】:dbKey=" + dbKey + "，数据库不存在或者数据库配置文件加载失败！");
				return null;
			}
			String DRIVER = db.getDriver();
			String IP = db.getIp();
			String PORT = db.getPort();
			String DATA_NAME = db.getDataName();
			String ENCODEING = db.getEncodeing();
			String USERNAME = db.getUserName();
			String PASSWORD = db.getPassword();

			String mysqlUrl = DB4Parperties.getMysqlUrl(IP, PORT,DATA_NAME, ENCODEING);

			try {
				Class.forName(DRIVER);
			} catch (ClassNotFoundException e) {
				logger.info("【Jdbc4Json】: 加载数据库驱动失败 ");
				return null;
			}
			try {
				connection = DriverManager.getConnection(mysqlUrl, USERNAME, PASSWORD);
				setTransactionIsolation(dbKey, connection);
			} catch (SQLException e) {
				logger.info("【Jdbc4Json】: 数据库连接失败 ");
				return null;
			}

		}

		return connection;
	}

	/**
	 * 查询数据库表的所有字段 构造 “字段属性模型”
	 * 
	 * @param databaseName
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static synchronized Map<String, Object> getFields(String dbKey, String beanName) {
		String tableName = T2E.toTableName(beanName);
		DB db = DBs.dBcache.get(dbKey);
		String dataBaseName = db.getDataName();
		try {

			ResultSet columnSet = getConnection(dbKey).getMetaData().getColumns(dataBaseName, STRING.PERCENT, tableName, STRING.PERCENT);
			Map<String, Object> map = new HashMap<>();
			while (columnSet.next()) { // 遍历某个表的字段
				String columnName = columnSet.getString("COLUMN_NAME");//.toUpperCase());
				map.put(T2E.toField(columnName), null);
			}
			return map;
		} catch (Exception e) {
			return null;
		}

	}

	public static synchronized String getPrimaryField(String dbKey, String beanName) {
		String tableName = T2E.toTableName(beanName);
		DB db = DBs.dBcache.get(dbKey);
		String dataBaseName = db.getDataName();
		String primaryKeyName = null;
		try {
			ResultSet primaryKeyResultSet = getConnection(dbKey).getMetaData().getPrimaryKeys(dataBaseName, null, tableName);
			while (primaryKeyResultSet.next()) { // 遍历某个表的主键
				primaryKeyName = primaryKeyResultSet.getString("COLUMN_NAME");
			}
			return T2E.toField(primaryKeyName.toUpperCase());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static synchronized List<ForeignKey> getForeignKeies(String dbKey, String beanName) {
		String tableName = T2E.toTableName(beanName);
		DB db = DBs.dBcache.get(dbKey);
		String dataBaseName = db.getDataName();
		try {
			ResultSet foreignKeyResultSet = getConnection(dbKey).getMetaData().getImportedKeys(dataBaseName, null, tableName);
			List<ForeignKey> list = new ArrayList<ForeignKey>();
			while (foreignKeyResultSet.next()) {// 遍历某个表的外键

				String fkColumnName = foreignKeyResultSet.getString("FKCOLUMN_NAME");
				String pkTableName = foreignKeyResultSet.getString("PKTABLE_NAME").toUpperCase();
				ForeignKey f = new ForeignKey();
				f.setForeignProperty(T2E.toField(fkColumnName));
				f.setReferDbBeanName(T2E.toClassName(pkTableName));
				list.add(f);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ForeignKey>();
		}
	}

	public synchronized static List<Index> getIndex(String dbKey, String beanName) {
		String tableName = T2E.toTableName(beanName);
		DB db = DBs.dBcache.get(dbKey);
		String dataBaseName = db.getDataName();
		List<Index> indexes = new ArrayList<Index>();
		try {
			ResultSet indexResultSet = getConnection(dbKey).getMetaData().getIndexInfo(dataBaseName, null, tableName, true, true);
			while (indexResultSet.next()) {// 遍历某个表的外键
				Index index = new Index();

				String columnName = indexResultSet.getString("COLUMN_NAME").toUpperCase();// 列名
				boolean nonUnique = indexResultSet.getBoolean("NON_UNIQUE");// 非唯一索引

				index.setIndexPropertyName(T2E.toField(columnName));
				index.setUnique(!nonUnique);
				indexes.add(index);

			}
			return indexes;

		} catch (SQLException e) {

			e.printStackTrace();
			return new ArrayList<Index>();
		}

	}

	/**
	 * 关闭连接资源
	 * 
	 * @param objs
	 *            含有colse()方法的对象集合
	 * 
	 */
	public static void close(Object... objs) {
		if (objs != null && objs.length > 0) {
			for (Object obj : objs) {
				try {
					if (obj != null) {
						Method method = obj.getClass().getMethod("close");
						if (method != null) {
							method.invoke(obj);
						}
					}
				} catch (Exception e) {
					logger.info("【Jdbc4Json】: 关闭数据库资源失败 ");
				}
			}
		}
	}

	public static void rollback(Connection connection) {
		try {
			connection.rollback();
		} catch (SQLException e) {
			logger.info("【Jdbc4Json】: 回滚失败 ： ");
			e.printStackTrace();
		}
	}

	/**
	 * 扫描数据库配置文件
	 * 
	 * @return List 配置文件名称 集合.
	 */
	private static List<String> getDbConfig() {
		File file = new File(classesPath);
		String test[];
		test = file.list();
		List<String> dbCongig = new ArrayList<>();
		for (int i = 0; i < test.length; i++) {

			String fileName = test[i];
			if (fileName.contains(DB_PROPERTIES)) {
				dbCongig.add(fileName);
			}
		}

		return dbCongig;

	}

	/**
	 * 获取 源码所在路径
	 * 
	 * @return
	 */
	private String getClassesPath() {

		String path = this.getClass().getResource("/").getPath();
		File file = new File(path);
		String classesPath = file.toString() + File.separatorChar;
		classesPath = classesPath.replace("%20", STRING.SPACING);
		//logger.debug("【Jdbc4Json】: classesPath=" + classesPath);
		return classesPath;

		/*
		 * String jarFilePath =
		 * this.getClass().getProtectionDomain().getCodeSource().getLocation().
		 * getFile(); File file = new File(jarFilePath); String classesPath =
		 * file.toString() + File.separatorChar; System.out.println(
		 * "[JDBC classesPath] :"+classesPath); return classesPath;
		 */

	}

	/**
	 * 设置事务隔离级别 * 设置事务隔离级别：</br>
	 * Connection.TRANSACTION_NONE （0） :指示事务不受支持的常量。(注意，不能使用 ，因为它指定了不受支持的事务。)
	 * （不支持事务） </br>
	 * Connection.TRANSACTION_READ_UNCOMMITTED （1） :指示可以发生脏读 (dirty
	 * read)、不可重复读和虚读 (phantom read) 的常量。</br>
	 * Connection.TRANSACTION_READ_COMMITTED （2）:指示不可以发生脏读的常量；不可重复读和虚读可以发生。
	 * </br>
	 * Connection.TRANSACTION_REPEATABLE_READ （4） :指示不可以发生脏读和不可重复读的常量；虚读可以发生。
	 * (JDBC 默认值) </br>
	 * Connection.TRANSACTION_SERIALIZABLE （8）: 指示不可以发生脏读、不可重复读和虚读的常量。
	 * 
	 * @param dbKey
	 * @param connection
	 */
	private static void setTransactionIsolation(String dbKey, Connection connection) {

		try {
			String ransactionIsolation = dBcache.get(dbKey).getTransactionIsolation();
			if (ransactionIsolation != null) {
				if (ransactionIsolation.equals("TRANSACTION_NONE")) {// 0
					connection.setTransactionIsolation(Connection.TRANSACTION_NONE);
				} else if (ransactionIsolation.equals("TRANSACTION_READ_UNCOMMITTED")) {// 1
					connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				} else if (ransactionIsolation.equals("TRANSACTION_SERIALIZABLE")) {// 8
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				}
				if (ransactionIsolation.equals("TRANSACTION_READ_COMMITTED")) {// 2
					connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				}
			}
			int n = connection.getTransactionIsolation();
			logger.debug("【Jdbc4Json】数据库[" + dBcache.get(dbKey).getDataName() + "]的事务隔离级别为：" + n);
		} catch (Exception e) {
			logger.error("【Jdbc4Json】数据库[" + dBcache.get(dbKey).getDataName() + "]的事务隔离级别设置失败!");
		}
	}
}
