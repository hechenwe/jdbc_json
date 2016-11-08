package com.sooncode.jdbc.json;

import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SJsons {
	private List<SJson> sJsons = new LinkedList<>();

	public SJsons(String jsonString) {
		if (SJson.isJsonArray(jsonString)) {// json数组
			JSONArray jArray = JSONArray.fromObject(jsonString);
			if (jArray != null && jArray.size() > 0) {
				for (int i = 0; i < jArray.size(); i++) {
					JSONObject jObj = jArray.getJSONObject(i);
					SJson sJson = new SJson(jObj.toString());
					this.sJsons.add(sJson);
				}
			}
		}
	}
	
	public SJson getSJson(int index){
		return this.sJsons.get(index);
	}
	
	public void removeSJson (int index){
		this.sJsons.remove(index);
	}
	
	public void removeFields(String key){
		for (SJson sJson : sJsons) {
			sJson.removeFields(key);
		}
	}
	
	public int size(){
		return this.sJsons.size();
	}
	
	public String toString(){
		JSONArray array = new JSONArray();
		for (SJson sJson : sJsons) {
			array.add(sJson.toString());
		}
		return array.toString();
	}
}
