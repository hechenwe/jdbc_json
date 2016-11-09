package example.dao;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.bean.JsonBeans;

public class UserDao__Test {
	
	
	private static Logger logger = Logger.getLogger("UserDao__Test.class");
    private UserDao dao = new UserDao();
    
    @Test
	public void get(){
	 JsonBeans beans =	dao.getUser4age(12);
	 logger.info(beans.toString());
	 
	}
}
