package com.sooncode.jdbc.json;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

public class SJsons__Test {
	private static Logger logger = Logger.getLogger("SJsons__Test.class");
	
	
	
    @Test
	public void SJsons(){
    	 
    	String json = "[{\"id\":1,\"url\":\"http:123.com\"},{\"id\":2,\"url\":\"http:456.com\"}]";
    	SJsons sJsons = new SJsons(json);
    	logger.info(sJsons.size());
    	
    	
    	
		
	}
    @Test
	public void removes(){
    	String json = "[{\"id\":1,\"url\":\"http:123.com\"},{\"id\":2,\"url\":\"http:456.com\"}]";
    	SJsons sJsons = new SJsons(json);
    	sJsons.removeFields("id");
    	logger.info(sJsons.toString());
    	
    }
    @Test
    public void updateFields(){
    	SJson s = new SJson();
    	s.addFields("id", 11);
    	s.addFields("id", 23);
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
    	logger.info(s.getJsonString());
    	s.updateFields("id", 1112);
    	logger.info(s.getJsonString());
    	s.updateFields("dog.id", 222);
    	logger.info(s.getJsonString());
    	s.updateFields("dog.mao.id", 9999);
    	logger.info(s.getJsonString());
     
    	
    }
    
}
