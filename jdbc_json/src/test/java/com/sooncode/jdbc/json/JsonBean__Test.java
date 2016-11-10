package com.sooncode.jdbc.json;

 
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sooncode.jdbc.bean.JsonBean;

public class JsonBean__Test {
	private static Logger logger = Logger.getLogger("JsonBean__Test.class");
    @Test
	public void test(){
    	JsonBean jb = new JsonBean("student");
    	jb.addField("id", 1);
    	jb.addField("name", "hechen");
    	jb.addField("address", "china");
    	jb.addField("age", 100);
    	jb.addField("id",345);
    	
    	JsonBean id1 = new JsonBean("identity");
    	id1.addField("identityId", 123);
    	JsonBean id2 = new JsonBean("identity");
    	id2.addField("identityName", "beijian");
    	List<JsonBean> list = new ArrayList<>();
    	list.add(id1);
    	list.add(id2);
    	//jb.addField("identities",list);
		logger.info(jb.getJsonString());
		
	}
    
    @Test
  	public void JsonBean(){
    	
    	JsonBean jb = new JsonBean();
    	jb.addField("id",1);
    	jb.addField("name","hechen");
    	
    	logger.info(jb.getJsonString());
    	
    }
}
