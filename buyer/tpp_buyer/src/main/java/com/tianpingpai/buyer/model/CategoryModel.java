package com.tianpingpai.buyer.model;

import java.util.ArrayList;

public class CategoryModel {
	
	public static final int CATEGORY_TYPE_FIRST= 1;
	public static final int CATEGORY_TYPE_SECOND = 2;
	
	private int categoryType;
	private int id;
	private String name;
	public int getId() {
		return id;
	}
	
	public CategoryModel(int id,String name){
		this.id = id;
		this.name = name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof CategoryModel)){
			return false;
		}
		CategoryModel other = (CategoryModel) o;
		return other.id == id;
	}
	
	private ArrayList<CategoryModel> subCategories;
	private boolean open;
	public ArrayList<CategoryModel> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(ArrayList<CategoryModel> subCategories) {
		this.subCategories = subCategories;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
}
