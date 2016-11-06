package com.sooncode.jdbc.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
    	map.put("class", 1);
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
		logger.info(sj.toString());
		logger.info(sj.getFields("phots[0].id"));
		//logger.info(sj2.getFields("nb"));
		//logger.info(sj2.getFields("nb.phots[1].url"));
		
	}
    @Test
	public void remove(){
    	SJson s = new SJson();
    	s.addFields("id", 11);
    	s.addFields("name", "hechen");
    	
    	SJson newJ = new SJson();
    	newJ.addFields("id", 22);
    	newJ.addFields("name", "xiaogo");
    	
    	SJson newJ2 = new SJson();
    	newJ2.addFields("id", "90");
    	newJ2.addFields("name", "hechen");
    	newJ.addFields("mao", newJ2);
    	
    	List<SJson> list = new LinkedList<>();
    	
    	SJson s1 = new SJson();
    	s1.addFields("age", 12);
    	SJson s2 = new SJson();
    	s2.addFields("age", 22);
    	list.add(s1);
    	list.add(s2);
    	
    	s.addFields("dog", newJ);
    	s.addFields("mous", list);
    	logger.info(s.getJsonString());
    	s.removeFields("id");
    	logger.info(s.getJsonString());
    	s.removeFields("dog.id");
    	logger.info(s.getJsonString());
    	s.removeFields("dog.mao.id");
    	logger.info(s.getJsonString());
    	
    	s.removeFields("dog.mao.name");
    	logger.info(s.getJsonString());
    	
    	s.removeFields("dog.mao");
    	logger.info(s.getJsonString());
    	
    	s.removeFields("dog");
    	logger.info(s.getJsonString());
    	
    }
    
}
