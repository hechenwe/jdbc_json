package com.sooncode.jdbc.cglib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.db.DBs;
import com.sooncode.jdbc.json.SoonJson;
import com.sooncode.jdbc.util.T2E;
import com.sooncode.jdbc.util.create_entity.Column;
 

public class CglibCreateBean {
    private String dbKey;
	public CglibCreateBean (String dbKey){
		this.dbKey=dbKey;
	}
	
	public CglibBean getBean(String json){
		Map<String, Object> map = SoonJson.getMap(json);
		String key = new String();
		Object val = new String();
		for (Entry<String, Object> en : map.entrySet()) {
			key = en.getKey();
			val = en.getValue();
			break;
		}

		Map<String, Object> proMap = SoonJson.getMap(val.toString());
		
		String tableName = T2E.toTableName(key);

		 
		Map<String, Column> columns = DBs.getColumns(dbKey,tableName);
		HashMap<String, Class<?>> propertyMap = new HashMap<>();
		for (Entry<String, Column> en : columns.entrySet()) {
			Column c = en.getValue();
			String propertyName =T2E.toField(  en.getKey());
			try {
				propertyMap.put(propertyName, Class.forName("java.lang."+c.getJavaDataType()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		// 生成动态 Bean
		Cglib bean = new Cglib(propertyMap);
		for (Entry<String, Object> en : proMap.entrySet()) {
		bean.setValue(en.getKey(), en.getValue());
		}
		// 获得bean的实体
		Object object = bean.getObject();
		CglibBean cb = new CglibBean();
		cb.setBeanName(key);
		cb.setBean(object);
       return cb;
	}
}
