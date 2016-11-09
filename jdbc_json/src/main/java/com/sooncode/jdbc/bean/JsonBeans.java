package com.sooncode.jdbc.bean;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sooncode.jdbc.json.SJson;

import net.sf.json.JSONArray;

public class JsonBeans {

	private List<JsonBean> jsonBeans;
    
	public JsonBeans (String jsonString){
		List<JsonBean> list = new LinkedList<>();
		boolean b = SJson.isJsonArray(jsonString);
		if(b==true){
			JSONArray array = JSONArray.fromObject(jsonString);
			for (Object obj : array) {
				JsonBean bean = new JsonBean(obj.toString());	
				list.add(bean);
			}
		}
		jsonBeans= list;
	}
	
	
	public JsonBeans(String beanName, List<Map<String, Object>> list) {
		List<JsonBean> beans = new LinkedList<JsonBean>();
		for (Map<String, Object> map : list) {
			JsonBean bean = new JsonBean(beanName, map);
			beans.add(bean);
		}
		jsonBeans = beans;
	}

	public List<JsonBean> getJsonBeans() {
		return jsonBeans;
	}

	public void setJsonBeans(List<JsonBean> jsonBeans) {
		this.jsonBeans = jsonBeans;
	}

	public int size(){
		return this.jsonBeans.size();
	}
	
	public String toString(){
		JSONArray array = new JSONArray();
		for (JsonBean bean : jsonBeans) {
			array.add(bean.toString());
		}
		return array.toString();
	}
}
