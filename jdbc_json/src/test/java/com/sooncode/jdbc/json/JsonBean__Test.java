package com.sooncode.jdbc.json;

 
import org.apache.log4j.Logger;
import org.junit.Test;

public class JsonBean__Test {
	private static Logger logger = Logger.getLogger("JsonBean__Test.class");
    @Test
	public void test(){
    	JsonBean jb = new JsonBean("student");
    	jb.addField("id", 1);
    	jb.addField("name", "hechen");
    	jb.addField("address", "china");
    	jb.addField("age", 100);
		logger.info(jb.getJson());
		
	}
}
