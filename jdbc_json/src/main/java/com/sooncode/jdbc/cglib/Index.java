package com.sooncode.jdbc.cglib;
 

/**
 * 索引
 * @author  sooncode
 */
public class Index {

	/** 索引的字段名称 */
	private String indexPropertyName;
	
	/** 是否是唯一索引 */
	private Boolean isUnique;

	public String getIndexPropertyName() {
		return indexPropertyName;
	}

	public void setIndexPropertyName(String indexPropertyName) {
		this.indexPropertyName = indexPropertyName;
	}

	public Boolean getUnique() {
		return isUnique;
	}

	public void setUnique(Boolean isUnique) {
		this.isUnique = isUnique;
	}
	
	 
}
