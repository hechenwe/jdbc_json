package com.sooncode.jdbc.json;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SJson {

	private static final String DATE_FORMAT = "yyyy-MM-dd H:m:s";
	/**
	 * json 格式的字符串
	 */
	private String json;
	/**
	 * JSONObject 对象
	 */
	private JSONObject jObj;
	/**
	 * JSONArray 数组
	 */
	private JSONArray jArray;
	
	private boolean isJson = true;

	public SJson() {
		this.jObj = new JSONObject();
	}
    
	public SJson(Object obj){
		jObj = JSONObject.fromObject(obj);
		json = jObj.toString();
		
	}
	
	/**
	 * SJson 构造器
	 * 
	 * @param jsonString
	 *            json格式字符串
	 */

	public SJson(String jsonString) {

		if (isJsonArray(jsonString)) {// json数组
			JSONArray ja = JSONArray.fromObject(jsonString);
			JSONArray jArray = new JSONArray();
			if (ja != null && ja.size() > 0) {
				for (int i = 0; i < ja.size(); i++) {
					JSONObject j = ja.getJSONObject(i);
					@SuppressWarnings("unchecked")
					Map<String, Object> map = j;
					JSONObject jObj = getJSONObject(map);
					jArray.add(jObj);
				}
			}
			this.jArray = jArray;
			this.json = jArray.toString();
		} else if (isJson(jsonString)) {
			{
				JSONObject jb = JSONObject.fromObject(jsonString);
				@SuppressWarnings("unchecked")
				Map<String, Object> map = jb;
				JSONObject jObj = getJSONObject(map);
				this.jObj = jObj;
				this.json = jObj.toString();
			}
		}else{
			this.isJson = false;
		}

	}

	/**
	 * SJson 构造器
	 * 
	 * @param map
	 *            Map
	 */
	public SJson(Map<String, Object> map) {
		JSONObject jObj = getJSONObject(map);
		this.jObj = jObj;
		this.json = jObj.toString();
	}

	/**
	 * SJson 构造器
	 * 
	 * @param list
	 *            List
	 */
	public SJson(List<Map<String, Object>> list) {
		JSONArray jArray = new JSONArray();
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				JSONObject jObj = getJSONObject(map);
				jArray.add(jObj);
			}
		}
		this.jArray = jArray;
		this.json = jArray.toString();

	}

	
	public boolean isJson(){
		return this.isJson;
	}
	
	
	
	/**
	 * 获取Map
	 * 
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getMap() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = this.jObj;
		return map;
	}

	/**
	 * 获取List
	 * 
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getList() {
		List<Map<String, Object>> list = new LinkedList<>();
		if (jArray != null) {
			for (int i = 0; i < jArray.size(); i++) {
				JSONObject jb = jArray.getJSONObject(i);
				@SuppressWarnings("unchecked")
				Map<String, Object> map = jb;
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 添加字段
	 * 
	 * @param key
	 *            字段名称
	 * @param value
	 *            值
	 */
	public void addFields(String key, Object value) {
		if (key != null && value != null && !key.trim().equals("")) {
			this.jObj.accumulate(key, value);
			this.json = this.jObj.toString();
		}
	}

	/**
	 * 添加对象
	 * 
	 * @param key
	 *            对象名称
	 * @param map
	 *            Map 对象
	 */
	public void addObject(String key, Map<String, Object> map) {
		if (key != null && map != null && map.size() > 0) {
			JSONObject jb = getJSONObject(map);
			this.jObj.accumulate(key, jb);
			this.json = this.jObj.toString();
		}
	}

	/**
	 * 添加数组
	 * 
	 * @param key
	 *            数组名称
	 * @param list
	 *            数组
	 */
	public void addArray(String key, List<Map<String, Object>> list) {
		JSONArray ja = new JSONArray();
		if (key != null && list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				JSONObject jb = getJSONObject(map);
				ja.add(jb);
			}
			this.jObj.accumulate(key, ja);
			this.json = this.jObj.toString();
		}
	}

	/**
	 * 修改 字段
	 * 
	 * @param key
	 *            字段名称
	 * @param value
	 *            值
	 */
	public void updateFields(String key, Object value) {
		if (key != null && value != null && !key.trim().equals("")) {
			this.removeFields(key);
			this.addFields(key, value);
		}
	}

	/**
	 * 删除 字段，对象，数组
	 * 
	 * @param key
	 *            字段，对象，数组名称
	 */
	public Object removeFields(String key) {
		if (key != null && !key.trim().equals("")) {
			Object r = this.jObj.remove(key);
			this.json = this.jObj.toString();
			return r;
		}
		return null;
	}

	/**
	 * 获取字段，对象，数组 值
	 * 
	 * @param key
	 *            字段，对象，数组
	 * @return 值
	 */
	public Object getFields(String key) {
		Object obj = this.getValue(this.json, key);
		return obj;
	}

	/**
	 * 字符串是否是Json格式
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJson(String json) {
		if (json == null || json.trim().equals("")) {
			return false;
		}
		try {
			JSONObject.fromObject(json);
			return true;
		} catch (Exception e) {

			return false;
		}

	}

	/**
	 * 字符串是否是Json数组
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonArray(String json) {
		if (json == null || json.trim().equals("")) {
			return false;
		}
		try {
			JSONArray.fromObject(json);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Map 转换成 JSONObject
	 * 
	 * @param map
	 * @return
	 */
	private JSONObject getJSONObject(Map<String, Object> map) {
		JSONObject jObj = new JSONObject();
		if (map != null && map.size() > 0) {
			for (Entry<String, Object> en : map.entrySet()) {
				String key = en.getKey();
				Object val = en.getValue();
				if (isJson(val.toString())) {
					@SuppressWarnings("unchecked")
					Map<String, Object> nextMap = JSONObject.fromObject(val.toString());
					JSONObject jVal = getJSONObject(nextMap);
					jObj.accumulate(key, jVal);
				} else {
					if (val instanceof Date) {
						String str = new SimpleDateFormat(DATE_FORMAT).format(val);
						jObj.accumulate(key, str);
					} else {
						jObj.accumulate(key, val);
					}
				}
			}
		}
		return jObj;

	}

	/**
	 * 
	 * @param jsonString
	 *            避免key中有class关键字
	 * @param key
	 *            字段名称 如："nb.phots[0].url" , "name" 等
	 * @return
	 */
	private Object remove(String jsonString, String key) {

		if (jsonString == null) {
			return null;
		}

		String[] keys = key.split("\\.");
		JSONObject jsonRoot = null;
		try {
			jsonRoot = JSONObject.fromObject(jsonString);

		} catch (Exception e) {
			return null;
		}
		if (keys.length == 1) {
			if (!keys[0].contains("[") && !keys[0].contains("]")) {
				Object obj = jsonRoot.get(keys[0]);
				return obj == null ? null : obj;
			} else {
				int start = keys[0].indexOf("[");
				int end = keys[0].indexOf("]");
				String number = keys[0].substring(start + 1, end);
				int num = Integer.valueOf(number);
				String thisKey = keys[0];
				thisKey = thisKey.replace("[" + num + "]", "");
				JSONArray jsonArray = JSONArray.fromObject(jsonRoot.get(thisKey));
				if (num < jsonArray.size()) {
					JSONObject obj = (JSONObject) jsonArray.get(num);
					return obj;
				}
				return null;
			}
		} else {

			String newKeys = new String();
			for (int i = 1; i < keys.length; i++) {
				if (i == 1) {
					newKeys = keys[i];
				} else {
					newKeys = newKeys + "." + keys[i];
				}
			}
			// -----------------------------------
			String thisKey = keys[0];

			if (thisKey.contains("[") && thisKey.contains("]")) {
				int start = thisKey.indexOf("[");
				int end = thisKey.indexOf("]");
				String number = thisKey.substring(start + 1, end);
				int num = Integer.valueOf(number);
				thisKey = thisKey.replace("[" + num + "]", "");
				JSONArray jsonArray = JSONArray.fromObject(jsonRoot.get(thisKey));
				if (num < jsonArray.size()) {
					JSONObject obj = (JSONObject) jsonArray.get(num);
					return this.getValue(obj.toString(), newKeys);
				} else {
					return null;
				}
			} else {
				Object obj = jsonRoot.get(keys[0]);
				if (obj == null) {
					return null;
				} else {
					String value = obj.toString();
					return this.getValue(value, newKeys);
				}
			}
		}
	}
	/**
	 * 
	 * @param jsonString
	 *            避免key中有class关键字
	 * @param key
	 *            字段名称 如："nb.phots[0].url" , "name" 等
	 * @return
	 */
	private Object getValue(String jsonString, String key) {
		
		if (jsonString == null) {
			return null;
		}
		
		String[] keys = key.split("\\.");
		JSONObject jsonRoot = null;
		try {
			jsonRoot = JSONObject.fromObject(jsonString);
			
		} catch (Exception e) {
			return null;
		}
		if (keys.length == 1) {
			if (!keys[0].contains("[") && !keys[0].contains("]")) {
				Object obj = jsonRoot.get(keys[0]);
				return obj == null ? null : obj;
			} else {
				int start = keys[0].indexOf("[");
				int end = keys[0].indexOf("]");
				String number = keys[0].substring(start + 1, end);
				int num = Integer.valueOf(number);
				String thisKey = keys[0];
				thisKey = thisKey.replace("[" + num + "]", "");
				JSONArray jsonArray = JSONArray.fromObject(jsonRoot.get(thisKey));
				if (num < jsonArray.size()) {
					JSONObject obj = (JSONObject) jsonArray.get(num);
					return obj;
				}
				return null;
			}
		} else {
			
			String newKeys = new String();
			for (int i = 1; i < keys.length; i++) {
				if (i == 1) {
					newKeys = keys[i];
				} else {
					newKeys = newKeys + "." + keys[i];
				}
			}
			// -----------------------------------
			String thisKey = keys[0];
			
			if (thisKey.contains("[") && thisKey.contains("]")) {
				int start = thisKey.indexOf("[");
				int end = thisKey.indexOf("]");
				String number = thisKey.substring(start + 1, end);
				int num = Integer.valueOf(number);
				thisKey = thisKey.replace("[" + num + "]", "");
				JSONArray jsonArray = JSONArray.fromObject(jsonRoot.get(thisKey));
				if (num < jsonArray.size()) {
					JSONObject obj = (JSONObject) jsonArray.get(num);
					return this.getValue(obj.toString(), newKeys);
				} else {
					return null;
				}
			} else {
				Object obj = jsonRoot.get(keys[0]);
				if (obj == null) {
					return null;
				} else {
					String value = obj.toString();
					return this.getValue(value, newKeys);
				}
			}
		}
	}

	/**
	 * 获取json格式的字符串
	 * 
	 * @return
	 */
	public String getJsonString() {
		return this.json;
	}

	public String toString() {
		return this.getJsonString();
	}
}
