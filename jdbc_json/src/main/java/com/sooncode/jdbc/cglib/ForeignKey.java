package com.sooncode.jdbc.cglib;

 

 

/**
 * 外键
 * @author sooncode
 *
 */
public class ForeignKey {
	/**
	 * 外键名
	 */
	private String foreignProperty;
	
	/**外键索引类型 */
	private String indexingType; 
	
	/**
	 * 参照表名称
	 */
    private String referDbBeanName;

	public String getForeignProperty() {
		return foreignProperty;
	}

	public void setForeignProperty(String foreignProperty) {
		this.foreignProperty = foreignProperty;
	}

	public String getIndexingType() {
		return indexingType;
	}

	public void setIndexingType(String indexingType) {
		this.indexingType = indexingType;
	}

	public String getReferDbBeanName() {
		return referDbBeanName;
	}

	public void setReferDbBeanName(String referDbBeanName) {
		this.referDbBeanName = referDbBeanName;
	}

	 


	 
	 
	 

	 
	
}
