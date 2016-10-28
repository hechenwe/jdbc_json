package com.sooncode.jdbc.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

public class SJson__Test {
	private static Logger logger = Logger.getLogger("SJson__Test.class");
    @Test
	public void SJson(){
    	List<Map<String,Object>> list = new ArrayList<>();
    	
    	Map<String,Object> map = new HashMap<>();
    	map.put("class1", 1);
    	map.put("name", "hechen");
    	map.put("age", 18);
    	map.put("clas", "{\"id\":1,\"className\":\"gao yi ban\",\"room\":\"{\\\"id\\\":234}\"}");
    	map.put("phots", "[{\"id\":1,\"url\":\"http:123.com\"},{\"id\":2,\"url\":\"http:456.com\"}]");
    	Map<String,Object> map1 = new HashMap<>();
    	map1.put("id", 2);
    	map1.put("name", "hechen2");
    	map1.put("age", 19);
    	map1.put("clas", "{\"id\":1,\"className\":\"gao yi ban\",\"room\":\"{\\\"id\\\":234}\"}");
    	map1.put("phots", "[{\"id\":1,\"url\":\"http:123.com\"},{\"id\":2,\"url\":\"http:456.com\"}]");
    	
    	list.add(map);
    	list.add(map1);
		SJson sj = new SJson(map);
		//logger.info(sj.getJsonString());
		
		String json = "[{\"id\":1,\"className\":\"gao yi ban\",\"room\":\"{\\\"id\\\":234}\"},{\"id\":2,\"className\":\"gao yi ban\",\"room\":\"{\\\"id\\\":234}\"}]";
		
		//SJson sj2 = new SJson(sj);
		//sj2.addFields("name", "hechen");
		//sj2.addObject("nb", map);
		//sj2.removeFields("name");
		//sj2.updateFields("name", "Tom");
		logger.info(sj.getFields("class1"));
		//logger.info(sj2.getFields("nb"));
		//logger.info(sj2.getFields("nb.phots[1].url"));
		
	}
}
