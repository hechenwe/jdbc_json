package com.sooncode.jdbc.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.chainsaw.Main;

import com.sooncode.jdbc.constant.STRING;

 

/**
 * 数据库表 和 对应的实体类
 * 
 * @author pc
 *
 */
public class T2E {

	private static Map<String, String> columnCache = new HashMap<String, String>();
	private static Map<String, String> fieldCache = new HashMap<String, String>();

	/** 属性名称转换成字段名称 */ 
	/**
	 * @param field
	 * @return
	 */
	public static String toColumn(String field) {
		String column = new String();
		column = columnCache.get(field);
		if (column != null) {
			return column;
		}

		String string = new String();
		char[] c = field.toCharArray();
		int i = 0;
		StringBuilder sb = new StringBuilder();
		while (i < field.length()) {

			while (i < field.length() && (isLower(c[i]) || isNumber(c[i]) || is$(c[i]))) {
				sb.append(c[i]);
				i++;
			}
			if (i != 0) {
				string = string + sb.toString().toUpperCase() + STRING.UNDERLINE;
			}
			if (i < field.length()) {
				sb = new StringBuilder();
				sb.append(c[i++]);
			}

		}
		column = string.substring(0, string.length() - 1);
		columnCache.put(field, column);
		return column;
	}

	/**
	 * 字段名称转换成属性名称
	 * 
	 * @param columnName
	 * @return
	 */
	public static String toField(String columnName) {
		String field = new String();
		field = fieldCache.get(columnName);
		if (field != null) {
			return field;
		}

		String[] arrays = columnName.toLowerCase().split(STRING.UNDERLINE);
		field = new  String ();
		if (arrays.length > 0) {
			field = arrays[0];
		}
		for (int i = 1; i < arrays.length; i++) {
			field += (arrays[i].substring(0, 1).toUpperCase() + arrays[i].substring(1, arrays[i].length()));
		}
		fieldCache.put(columnName, field);
		return field;
	}
	
	public static String toTableName(String className){
		return toColumn(className);
		
	}
	
    public static String toClassName(String tableName){
		String str = toField(tableName);
		str = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
		return str;
	}
	

	/**
	 * 字符是否是‘$’
	 * 
	 * @param c
	 * @return
	 */
	private static boolean is$(char c) {
		if (c == '$') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 字符是否是小写字母
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isLower(char c) {
		if (c >= 'a' && c <= 'z') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 字符是否是数字
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isNumber(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		} else {
			return false;
		}
	}

	 public static void main(String[] args) {
		System.out.println( toField("CLASS_ID") );
	}

}
