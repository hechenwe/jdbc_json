package com.sooncode.jdbc.json;

import java.util.HashMap;
 
import java.util.Map;
import java.util.Map.Entry;

public class JsonBean {
	private String beanName;

	private Map<String, Object> map = new HashMap<>();

	public JsonBean(String string) {

		boolean b = SJson.isJson(string);
		if (b == true) {
			SJson sj = new SJson(string);
			Map<String, Object> map = sj.getMap();
			String beanName = null;
			if (map != null && map.size() == 1) {
				for (Entry<String, Object> en : map.entrySet()) {
					beanName = en.getKey();
					this.beanName = beanName;
					SJson newSj = new SJson(sj.getFields(beanName).toString());
					for (Entry<String, Object> e : newSj.getMap().entrySet()) {
						this.addField(e.getKey(), e.getValue());
					}
				}
			}
		}else{
			this.beanName = string;
		}
	}

	public void addField(String key, Object value) {
		if (key != null && !key.trim().equals("") && value != null) {
			map.put(key, value);
		}
	}
	public void addFields( Map<String, Object>  fields) {
		
		if(fields != null && fields.size() >0){
			for ( Entry<String, Object> en : fields.entrySet()) {
				String key = en.getKey();
				Object value = en.getValue();
				if (!key.trim().equals("") && value != null) {
					map.put(key, value);
				}
				
			}
		}
		
	}

	public Object getField(String key) {
		if (key != null && !key.trim().equals("")) {
			return this.map.get(key);
		}else{
			return null;
		}
		 
	}
	public Map<String,Object> getFields() {
		return this.map;
		
	}

	public void removeField(String key) {
		if (key != null && !key.trim().equals("")) {
			this.map.remove(key);
		}
	}

	public void updateField(String key, Object value) {
		if (key != null && !key.trim().equals("") && value != null) {
			this.map.remove(key);
			this.map.put(key, value);
		}
	}

	public String getJson() {
		SJson sBean = new SJson(map);
		Map<String, Object> newMap = new HashMap<>();
		newMap.put(beanName, sBean.getJsonString());
		String str = new SJson(newMap).getJsonString();
		return str;
	}
    
	public String getBeanName() {
		return beanName;
	}

	public String toString() {
		return this.getJson();
	}
}
