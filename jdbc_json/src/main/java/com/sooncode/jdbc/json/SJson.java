package com.sooncode.jdbc.json;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sooncode.jdbc.constant.DATE_FORMAT;
import com.sooncode.jdbc.constant.STRING;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SJson {

	//private static final String DATE_FORMAT = "yyyy-MM-dd H:m:s";

	/**
	 * JSONObject 对象
	 */
	private JSONObject jSONObject;

	private Map<String, SJson> map = new LinkedHashMap<>();
	private Map<String, List<SJson>> array = new LinkedHashMap<>();

	private boolean isJson = true;

	public SJson() {
		this.jSONObject = new JSONObject();
	}

	public SJson(Object obj) {
		jSONObject = JSONObject.fromObject(obj);

	}

	/**
	 * SJson 构造器
	 * 
	 * @param jsonString
	 *            json格式字符串
	 */

	public SJson(String jsonString) {

		if (isJson(jsonString)) {
			{
				JSONObject jb = JSONObject.fromObject(jsonString);
				@SuppressWarnings("unchecked")
				Map<String, Object> map = jb;
				JSONObject jObj = getJSONObject(map);
				this.jSONObject = jObj;

			}
		} else {
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
		this.jSONObject = jObj;

	}

	public boolean isJson() {
		return this.isJson;
	}

	/**
	 * 获取Map
	 * 
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getMap() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = this.jSONObject;
		return map;
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
			Object obj = this.jSONObject.get(key);
			SJson sMap = this.map.get(key);
			List<SJson> sList = this.array.get(key);
			if (obj == null && sMap == null && sList == null)
				this.jSONObject.accumulate(key, value);
		}
	}

	public void addFields(String key, SJson sJson) {
		if (key != null && sJson != null && !key.trim().equals("")) {
			Object obj = this.jSONObject.get(key);
			SJson sMap = this.map.get(key);
			List<SJson> sList = this.array.get(key);
			if (obj == null && sMap == null && sList == null) {
				this.map.put(key, sJson);
			}
		}
	}

	/**
	 * 添加数组
	 * 
	 * @param key
	 * @param sJsons
	 */
	public void addFields(String key, List<SJson> sJsons) {
		if (key != null && sJsons != null && sJsons.size() > 0 && !key.trim().equals("")) {
			Object obj = this.jSONObject.get(key);
			SJson sMap = this.map.get(key);
			List<SJson> sList = this.array.get(key);
			if (obj == null && sMap == null && sList == null) {
				this.array.put(key, sJsons);
			}

		}
	}

	/**
	 * 获取字段，对象，数组 值
	 * 
	 * @param key
	 *            字段，对象，数组
	 * @return 值
	 */
	public Object getFields(String key) {
		Object obj = this.getValue(this.getJsonString(), key);
		return obj;
	}

	public void updateFields(String key, Object value) {
		update(this,key,value);
	}

	private SJson update(SJson sJson, String key, Object value) {
		if (key != null && !key.equals(STRING.NULL_STR)) {
			if (!key.contains(STRING.POINT)) {
				Object obj = sJson.getFields(key);
				if (obj != null) {
					sJson.removeFields(key);
					sJson.addFields(key, value);
					return null;
				}
			} else {
				String[] keys = key.split(STRING.ESCAPE_POINT);
				String lastKey = keys[0];
				String thisKey = key.replace(lastKey + STRING.POINT, STRING.NULL_STR);
				SJson s = sJson.map.get(lastKey);
				if (s != null) {
					return update(s, thisKey, value);
				}  
			}
		}  
		return null;

	}

	/**
	 * 删除
	 * 
	 * @param key
	 *            关键字
	 */
	public void removeFields(String key) {
		remove(null, this, null, key);
	}

	public JSONObject getJSONObject() {
		return jSONObject;
	}

	/**
	 * 获取json格式的字符串
	 * 
	 * @return
	 */
	public String getJsonString() {

		return getJson(this).toString();
	}

	public String toString() {
		return this.getJsonString();
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

	private JSONObject getJson(SJson sJson) {
		if (sJson.map.size() == 0) {
			return sJson.jSONObject;
		} else {
			JSONObject jObj = JSONObject.fromObject(sJson.jSONObject.toString());
			for (Entry<String, SJson> en : sJson.map.entrySet()) {
				String key = en.getKey();
				SJson s = en.getValue();
				jObj.accumulate(key, getJson(s));
			}
			for (Entry<String, List<SJson>> en : sJson.array.entrySet()) {
				String key = en.getKey();
				List<SJson> list = en.getValue();
				List<JSONObject> jObjs = new LinkedList<>();
				for (SJson s : list) {
					jObjs.add(s.jSONObject);
				}
				jObj.accumulate(key, jObjs);
			}
			return jObj;
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
				if ( val != null && isJson(val.toString())) {
					@SuppressWarnings("unchecked")
					Map<String, Object> nextMap = JSONObject.fromObject(val.toString());
					JSONObject jVal = getJSONObject(nextMap);
					jObj.accumulate(key, jVal);
				} else {
					if (val instanceof Date) {
						String str = new SimpleDateFormat(DATE_FORMAT.ALL_DATE).format(val);
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

	private SJson remove(SJson lastSJson, SJson sJson, String lastKey, String key) {
		String[] keys = key.split(STRING.ESCAPE_POINT);
		if (keys.length == 1) {
			String thisKey = keys[0];
			JSONObject j = sJson.jSONObject;
			j.remove(thisKey);
			sJson.map.remove(thisKey);
			if (key.contains("[]")) {
				String newKey = new String(thisKey.replace("[]", ""));
				sJson.array.remove(newKey);
			} else if (thisKey.contains("[") && thisKey.contains("]")) {
				int start = thisKey.indexOf("[");
				int end = thisKey.indexOf("]");
				String number = thisKey.substring(start + 1, end);
				int num = Integer.valueOf(number);
				String newKey = new String(key.replace("[" + num + "]", ""));
				List<SJson> list = sJson.array.get(newKey);
				if (num < list.size() && num >= 0) {
					list.remove(num);
				}
			}
			if (lastSJson != null && lastKey != null) {
				lastSJson.map.remove(lastKey);
				lastSJson.map.put(lastKey, sJson);
			}
			return lastSJson;
		} else {
			String lKey = keys[0];
			String thisKey = key.replace(lKey + ".", "");
			SJson s = sJson.map.get(lKey);
			SJson updateS = remove(sJson, s, lKey, thisKey);
			return updateS;
		}
	}
}
