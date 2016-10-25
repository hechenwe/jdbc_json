package com.sooncode.jdbc.cglib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.db.DBs;
import com.sooncode.jdbc.json.SJson;
import com.sooncode.jdbc.util.T2E;
import com.sooncode.jdbc.util.create_entity.Column;
 

public class CglibCreateBean {
    private String dbKey;
	public CglibCreateBean (String dbKey){
		this.dbKey=dbKey;
	}
	
	public CglibBean getBean(String json ){
		SJson sj = new SJson(json);
		String name = sj.getTableName();
		String tableName = T2E.toTableName(name);
		Map<String, Column> columns = DBs.getColumns(dbKey,tableName);
		HashMap<String, Class<?>> propertyMap = new HashMap<>();
		for (Entry<String, Column> en : columns.entrySet()) {
			Column c = en.getValue();
			String propertyName =T2E.toField(en.getKey());
			try {
				propertyMap.put(propertyName, Class.forName("java.lang."+c.getJavaDataType()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		// 生成动态 Bean
		Cglib bean = new Cglib(propertyMap);
		
		SJson sJson = new SJson(sj.getFields(sj.getTableName()).toString());
		
		for (Entry<String, Object> en : sJson.getMap().entrySet()) {
			String key = en.getKey();
			Object val = en.getValue();
		bean.setValue(key, val);
		}
		// 获得bean的实体
		Object object = bean.getObject();
		CglibBean cb = new CglibBean();
		cb.setBeanName(name);
		cb.setBean(object);
       return cb;
	}
}
