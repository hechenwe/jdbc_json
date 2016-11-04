package com.sooncode.jdbc.sql;

import java.util.List;
import java.util.Map;

/**
 * 分页数据
 * @author pc
 *
 */
public class PageData {

	private long size;
	private List<Map<String,Object>> list;
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	
	
	
}
