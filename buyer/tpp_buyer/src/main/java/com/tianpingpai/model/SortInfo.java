package com.tianpingpai.model;

import java.io.Serializable;

public class SortInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String category_id;
	/**
	 *  排序类型 -1默认排序， 1发货速度， 2服务态度 ， 3商品质量， 4推荐排序
	 */
	private int sort = -1;
	private boolean is_selected = false;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public boolean isIs_selected() {
		return is_selected;
	}
	public void setIs_selected(boolean is_selected) {
		this.is_selected = is_selected;
	}
	@Override
	public String toString() {
		return "SortInfo [name=" + name + ", category_id=" + category_id
				+ ", sort=" + sort + ", is_selected=" + is_selected + "]";
	}

}
