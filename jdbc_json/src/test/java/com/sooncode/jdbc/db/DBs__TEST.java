package com.sooncode.jdbc.db;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import soontest.ManyTheadTest;
import soontest.OpenInterfaceTest;
import soontest.SoonTest;

public class DBs__TEST {
	private static Logger logger = Logger.getLogger("DBs_TEST.class");

	@Test
	public void getConnection() {

		Connection con1 = DBs.getConnection(null);
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功：" + (con1 != null));

		Connection con2 = DBs.getConnection("default");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功：" + (con2 != null));

		Connection con3 = DBs.getConnection("jdbc");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功：" + (con3 != null));

		Connection con4 = DBs.getConnection("JDBC");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功：" + (con4 != null));

		Connection con5 = DBs.getConnection("DEFAULT");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功：" + (con5 != null));

		Connection con6 = DBs.getConnection("");
		logger.info("【Jdbc4Json  test】获取数据库连接是否成功：" + (con6 != null));
	}

	@Test
	public void getConnection4pressureTest() {

		ManyTheadTest mtt = new ManyTheadTest(1, 1);
		SoonTest st = (SoonTest) OpenInterfaceTest.newInstance(mtt);
		List<Object> list = st.testMethod(DBs.class, "getConnection", new String[] { "default" });
		for (Object obj : list) {
			if (obj instanceof Connection) {
				Connection con = (Connection) obj;
				logger.info("【Jdbc4Json  test】 获取数据库连接是否成功：" + (con != null));
			}
		}
 
	}
	
	@Test 
	public void getFields (){
		Map<String,Object> map = DBs.getFields("default", "teacher");
		logger.info(map);
		
	}

}
