package com.sooncode.jdbc.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.constant.DATE_FORMAT;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.json.SJson;

import java.util.TreeMap;

public class JsonBean extends Bean {

	private String beanName;

	/***
	 * 唯一标识字段
	 */
	private String id;

	/** 标识字段对应的值 */
	private Object idVal;

	private Map<String, Object> map = new LinkedHashMap<>();

	public JsonBean() {

	}

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
		} else {
			this.beanName = string;
		}
	}

	public JsonBean (String beanName,Map<String,Object> map){
		
		for(Entry<String,Object> en : map.entrySet()){
			String key = en.getKey();
			Object val = en.getValue();
			if(beanName==null && !key.contains(STRING.DOLLAR)){
				this.map.put(key, val);
			}else if(beanName!=null && !"".equals(beanName) && key.contains(STRING.DOLLAR)){
				String[] keys = key.split(STRING.ESCAPE_DOLLAR);
				if(keys.length==2  &&  beanName.equals(keys[0])){
					this.map.put(keys[1], val);	
				}
			}
		}
	}
	
	
	
	public void addField(JsonBean jsonBean) {
		if (jsonBean != null) {
			this.addField(jsonBean.getBeanName(), jsonBean.getFields());
		}
	}

	public void addField(String key, Object value) {
		if (key != null && !key.trim().equals("") && value != null) {
			if (value instanceof Date) {
				String str = new SimpleDateFormat(DATE_FORMAT.ALL_DATE).format(value);
				map.put(key, str);
			}else{
			map.put(key, value);
			}
		}
	}

	public void addField(String key, List<JsonBean> jsonBeans) {
		if (jsonBeans != null && jsonBeans.size() > 0) {
			List<Map<String, Object>> list = new LinkedList<>();
			for (JsonBean j : jsonBeans) {
				list.add(j.getFields());
			}
			this.addField(key, list);
		}
	}

	public void addFields(Map<String, Object> fields) {

		if (fields != null && fields.size() > 0) {
			for (Entry<String, Object> en : fields.entrySet()) {
				String key = en.getKey();
				Object value = en.getValue();
				if (!key.trim().equals("") && value != null) {
					if (value instanceof Date) {
						String str = new SimpleDateFormat(DATE_FORMAT.ALL_DATE).format(value);
						map.put(key, str);
					}else{
						map.put(key, value);
					}
				}

			}
		}

	}

	public Object getField(String key) {
		if (key != null && !key.trim().equals("")) {
			return this.map.get(key);
		} else {
			return null;
		}

	}

	public Map<String, Object> getFields() {
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

	public String getJsonString() {
		SJson sBean = new SJson(map);
		String jsonString = new String();
		if (this.beanName != null) {
			Map<String, Object> newMap = new TreeMap<>();
			newMap.put(beanName, sBean.getJsonString());
			jsonString = new SJson(newMap).getJsonString();

		} else {
			jsonString = sBean.getJsonString();
		}
		return jsonString;
	}

	public String getBeanName() {
		return beanName;
	}

	public String toString() {
		return this.getJsonString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getIdVal() {
		return idVal;
	}

	public void setIdVal(Object idVal) {
		this.idVal = idVal;
	}

}
