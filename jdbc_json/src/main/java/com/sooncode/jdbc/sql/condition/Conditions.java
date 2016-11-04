package com.sooncode.jdbc.sql.condition;

import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.sooncode.jdbc.bean.JsonBean;
import com.sooncode.jdbc.constant.SQL_KEY;
import com.sooncode.jdbc.constant.STRING;
import com.sooncode.jdbc.sql.Parameter;
import com.sooncode.jdbc.sql.condition.sign.Sign;
import com.sooncode.jdbc.util.T2E;

/**
 * 查询条件构造器
 * 
 * @author pc
 *
 */
public class Conditions {

	private JsonBean leftBean;
	private JsonBean[] otherBeans;
	private Map<String, Condition> ces;
	
	private String keys;

	/**
	 * 排序的SQL片段
	 */
	private String oderByes = new String();

	public Conditions(JsonBean leftBean, JsonBean... otherBeans) {
		
		
		
		this.leftBean = leftBean;
		this.otherBeans = otherBeans;
		Map<String, Object> map = leftBean.getFields();
		Map<String, Object> newMap = new TreeMap<>();
		
		for(Entry<String, Object> en : map.entrySet()){
			String key = en.getKey();
			Object value = en.getValue();
			newMap.put(leftBean.getBeanName()+ STRING.POINT+key, value);
		}
		
		if( otherBeans!=null  &&  otherBeans.length >0 ){
			for (JsonBean bean : otherBeans) {
				Map<String, Object> otherMap = bean.getFields();
				 for(Entry<String,Object> en:otherMap.entrySet()){
					 String key = en.getKey();
					 Object val = en.getValue();
					 newMap.put(bean.getBeanName()+STRING.POINT+key, val);
				 }
			}
		}
		
		
		Map<String, Condition> list = new HashMap<>();
		for (Entry<String, Object> en : newMap.entrySet()) {
			String key = en.getKey();
			Object val = en.getValue();
			Condition c = new Condition(key, val, null);
			list.put(key, c);
		}

		this.ces = list;
	}

	/**
	 * 设置条件
	 * 
	 * @param key
	 *            字段
	 * @param sign
	 *            条件使用的符号
	 * @return
	 */
	public Conditions setCondition(String key, Sign sign) {

		Condition c = ces.get(key);
		if (c != null) {
			c.setConditionSign(sign + new String());
			ces.put(c.getKey(), c);
		}
		return this;
	}
	/**
	 * 设置条件
	 * 
	 * @param key
	 *            字段
	 * @param sign
	 *            条件使用的符号
	 * @return
	 */
	public Conditions setCondition(String key1, String key2) {
		if(keys == null)
		{
			keys = key1 +SQL_KEY.EQ + key2;
		}
		return this;
	}

	/**
	 * 设置条件
	 * 
	 * @param key
	 *            字段
	 * @param sign
	 *            条件使用的符号
	 * @return
	 */
	public Conditions setCondition(String key, Sign sign, Object obj) {

		Condition c = ces.get(key);

		if (c != null) {
			if (obj != null) {
				c.setVal(obj);
			}
			c.setConditionSign(sign + new String());
			ces.put(c.getKey(), c);
		}
		return this;
	}

	/**
	 * 设置Between条件
	 * 
	 * @param key
	 *            字段
	 * @param start
	 *            下线值
	 * @param end
	 *            上线值
	 * @return
	 */
	public Conditions setBetweenCondition(String key, Object start, Object end) {

		Condition c = ces.get(key);
		c.setType("0");
		if (c != null) {
			String sql = STRING.SPACING + T2E.toColumn(key) + SQL_KEY.BETWEEN + start + SQL_KEY.AND + end
					+ STRING.SPACING;
			c.setCondition(sql);
			ces.put(c.getKey(), c);
		}
		return this;
	}

	/**
	 * 设置IS NULL 条件
	 * 
	 * @param key
	 * @return
	 */
	public Conditions setIsNullCondition(String key) {

		Condition c = ces.get(key);
		c.setType("0");
		if (c != null) {
			String sql = STRING.SPACING + T2E.toColumn(key) + SQL_KEY.IS + SQL_KEY.NULL + STRING.SPACING;// "
																											// IS
																											// NULL
																											// ";
			c.setCondition(sql);
			ces.put(c.getKey(), c);
		}
		return this;
	}

	/**
	 * 设置IS NULL 条件
	 * 
	 * @param key
	 * @return
	 */
	public Conditions setIsNotNullCondition(String key) {

		Condition c = ces.get(key);
		c.setType("0");
		if (c != null) {
			String sql = STRING.SPACING + T2E.toColumn(key) + SQL_KEY.IS + SQL_KEY.NOT + SQL_KEY.NULL + STRING.SPACING;// "
																														// IS
																														// NOT
																														// NULL
																														// ";
			c.setCondition(sql);
			ces.put(c.getKey(), c);
		}
		return this;
	}

	/**
	 * 设置 IN 条件
	 * 
	 * @param key
	 * @return
	 */
	public Conditions setInCondition(String key, Object[] values) {

		Condition c = ces.get(key);
		if (c != null) {
			c.setType("1");
			c.setVales(values);
			c.setConditionSign("IN");
			ces.put(c.getKey(), c);
		}
		return this;
	}

	/**
	 * 设置排序
	 * 
	 * @param key
	 *            字段
	 * @param sort
	 *            排序规则：升序；降序。
	 * @return
	 */
	public Conditions setOderBy(String key, Sort sort) {
	 
		String[] keys = key.split(STRING.ESCAPE_POINT);
		String con = new String ();
		if(keys.length==2){
			  con = T2E.toColumn(keys[0])+STRING.POINT + T2E.toColumn(keys[1]);
		}else{
			
			  con = T2E.toColumn(keys[0]);
		}
		key = con;
		if (key == null || key.equals("")) {
			return this;
		} else {
			if (this.oderByes.equals("")) {
				this.oderByes = this.oderByes + STRING.SPACING + key.toUpperCase() + STRING.SPACING + sort.name();

			} else {
				this.oderByes = this.oderByes + SQL_KEY.COMMA + key.toUpperCase() + STRING.SPACING + sort.name();
			}
		}

		return this;

	}

	/**
	 * 获取预编译SQL模型
	 * 
	 * @return
	 */
	public Parameter getWhereParameter() {

		Parameter p = new Parameter();
		Map<Integer, Object> para = new HashMap<>();
		String sql = new String();
		int index = 1;
		for (Entry<String, Condition> en : this.ces.entrySet()) {
			Condition c = en.getValue();
			String key = c.getKey();
			String[] keys = key.split(STRING.ESCAPE_POINT);
			String con = new String ();
			if(keys.length==2){
				  con = T2E.toColumn(keys[0])+STRING.POINT + T2E.toColumn(keys[1]);
			}else{
				
				  con = T2E.toColumn(keys[0]);
			}
			if (c.getType().equals("1")) {
				if (c.getVal() != null || c.getVales() != null) {
					String sign = c.getConditionSign();
					String newSign = sign;// Sign.Signmap.get(sign);
					newSign = newSign == null ? SQL_KEY.EQ : newSign; // 如果字段不为空，但是没有条件符号，默认使用等值查询"="。
					if (newSign.equals(SQL_KEY.LIKE)) {
						sql = sql + SQL_KEY.AND + con + STRING.SPACING + SQL_KEY.LIKE + STRING.SPACING + STRING.QUESTION;// "
																										// AND
																										// LIKE
																										// ?";
						para.put(index, STRING.PERCENT + c.getVal() + STRING.PERCENT);
						index++;
					} else if (sign != null && sign.equals(SQL_KEY.IN)) {

						String vales = SQL_KEY.L_BRACKET;// "(";
						for (int i = 0; i < c.getVales().length; i++) {
							if (i != 0) {
								vales = vales + STRING.SPACING + STRING.COMMA + STRING.QUESTION + STRING.SPACING;// "
																													// ,?
																													// ";

							} else {
								vales = vales + STRING.QUESTION + STRING.SPACING;// "?
																					// ";
							}
							para.put(index, c.getVales()[i]);
							index++;
						}
						vales = vales + SQL_KEY.R_BRACKET + STRING.SPACING;// ")
																			// ";
						sql = sql + SQL_KEY.AND + con + STRING.SPACING + SQL_KEY.IN + vales;
					}

					else {
						sql = sql + SQL_KEY.AND + con + STRING.SPACING + newSign + STRING.QUESTION;// "?";
						para.put(index, c.getVal());
						index++;
					}
				}
			} else {// 自定义
				sql = sql + SQL_KEY.AND + c.getCondition();
			}

		}
		if (!this.oderByes.equals("")) {
			sql = sql + SQL_KEY.ORDER_BY + this.oderByes;
		}
		p.setReadySql(sql);
		p.setParams(para);
		return p;
	}

	public JsonBean getLeftBean() {
		return leftBean;
	}

	public JsonBean[] getOtherBeans() {
		return otherBeans;
	}

	 

}
