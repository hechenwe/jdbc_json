package com.sooncode.jdbc.bean;

 

 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.db.DBs;
 
 

public class DbBeanCache {
    
	
	private static Map<String,Map<String, Object>> fieldsCache = new HashMap<>();
	private static Map<String,String> pkCache = new HashMap<>();
	private static Map<String,List<ForeignKey>> fkCache = new HashMap<>();
	private static Map<String,List<Index>> indexCache = new HashMap<>();
		
	 
	
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
		 
		
		List<ForeignKey> foreignKeies = fkCache.get(beanName);
		
		if(foreignKeies == null){
			foreignKeies = DBs.getForeignKeies(dbKey, beanName);
			fkCache.put(beanName, foreignKeies);
		}
		List<Index> indexes = indexCache.get(beanName);
		
		if(indexes == null){
			indexes = DBs.getIndex(dbKey, beanName);
			indexCache.put(beanName, indexes);
		}
		
		if(foreignKeies!=null&&foreignKeies.size()>0){
			if(indexes != null && indexes.size()>0){
				for (ForeignKey f : foreignKeies) {
					  for (Index i : indexes) {
						if(f.getForeignProperty().equals(i.getIndexPropertyName())&& i.getUnique()==true){
						   f.setUnique(true);
						}
					}
				}
			}
		}
		
		
		DbBean dbBean = new DbBean();
		dbBean.setBeanName(beanName);
		dbBean.setPrimaryField(pkName);
		dbBean.setForeignKeies(foreignKeies);
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
