package com.sooncode.jdbc.bean;

 

 

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
	
	/**外键是否是唯一索引 */
	private boolean isUnique;
	
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

	 

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public String getReferDbBeanName() {
		return referDbBeanName;
	}

	public void setReferDbBeanName(String referDbBeanName) {
		this.referDbBeanName = referDbBeanName;
	}

	 


	 
	 
	 

	 
	
}
