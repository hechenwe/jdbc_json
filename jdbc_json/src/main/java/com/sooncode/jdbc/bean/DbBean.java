package com.sooncode.jdbc.bean;

import java.util.List;
import java.util.Map;


public class DbBean {
	private String beanName;
	private String primaryField;
	private Object primaryFieldValue;
	private List<ForeignKey> foreignKeies;
	private Map<String,Object> fields;
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getPrimaryField() {
		return primaryField;
	}

	public void setPrimaryField(String primaryField) {
		this.primaryField = primaryField;
	}
 

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	public Object getPrimaryFieldValue() {
		return primaryFieldValue;
	}

	public void setPrimaryFieldValue(Object primaryFieldValue) {
		this.primaryFieldValue = primaryFieldValue;
	}

	public List<ForeignKey> getForeignKeies() {
		return foreignKeies;
	}

	public void setForeignKeies(List<ForeignKey> foreignKeies) {
		this.foreignKeies = foreignKeies;
	}

}
