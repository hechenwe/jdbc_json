package com.sooncode.jdbc.cglib;

 

 
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.db.DBs;
import com.sooncode.jdbc.json.JsonBean;
 
 

public class DbBeanCache {
    
	
	private static Map<String,Map<String, Object>> fieldsCache = new HashMap<>();
	private static Map<String,String> pkCache = new HashMap<>();
	private DbBeanCache  (){
		
	}
	
	public static DbBean getDbBean(String dbKey , JsonBean jsonBean){
		 
		String  beanName =jsonBean.getBeanName();
		
		Map<String, Object> fields = fieldsCache.get(beanName);
		if(fields==null){
			fields = DBs.getFields(dbKey,beanName);
			fieldsCache.put(beanName, fields);
		}
		
		String pkName = pkCache.get(beanName);
		if(pkName == null){
			 pkName = DBs.getPrimaryField(dbKey, beanName);
			 pkCache.put(beanName,pkName);
		}
		 
		DbBean dbBean = new DbBean();
		dbBean.setBeanName(beanName);
		dbBean.setPrimaryField(pkName);
		
		Map<String,Object> map = jsonBean.getFields();
		 
		for(Entry<String, Object> en : map.entrySet()){
			String key = en.getKey();
			Object val = en.getValue();
			if(fields.containsKey(key)==true ){
				fields.remove(key);
				fields.put(key, val);
			}
		}
		dbBean.setFields(fields);
		Object primaryFieldValue = map.get(pkName);
		dbBean.setPrimaryFieldValue(primaryFieldValue);
		
       return dbBean;
	}
	
	 
	
}
