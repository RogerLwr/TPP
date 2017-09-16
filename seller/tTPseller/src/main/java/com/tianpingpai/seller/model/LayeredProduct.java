package com.tianpingpai.seller.model;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.model.ProductModel;

import java.util.ArrayList;

public class LayeredProduct {
	
	public abstract static class Category{
		public abstract String getName();
		public abstract int getId();
		ArrayList<ProductModel> products;
		
		public void setProducts(ArrayList<ProductModel> products) {
			this.products = products;
		}
		
		public ArrayList<ProductModel> getProducts() {
			return products;
		}
	}
	
	public static class SecondCategory extends Category{
		@SerializedName("scategory_id")
		private int id;
		@SerializedName("scategory_name")
		private String name;

		@Override
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		@Override
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class FirstCategory extends Category{
		@SerializedName("fcategory_id")
		private int id;
		@SerializedName("fcategory_name")
		private String name;
		@SerializedName("category_list")
		private ArrayList<SecondCategory> secondCategories;
		@Override
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		@Override
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public ArrayList<SecondCategory> getSecondCategories() {
			return secondCategories;
		}
		public void setSecondCategories(ArrayList<SecondCategory> secondCategories) {
			this.secondCategories = secondCategories;
		}
		
		public boolean open;
		
		public boolean isOpen(){
			return open;
		}
		
		public void setOpen(boolean open){
			this.open = open;
		}
	}
	
	private ArrayList<FirstCategory> categories;

	public ArrayList<FirstCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<FirstCategory> categories) {
		this.categories = categories;
	}

	
	private int storeId;
	
	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	
	private int storeType;
	

	ArrayList<ProductModel> products;
	public ArrayList<ProductModel> getProducts() {
		if(products == null){
			products = new ArrayList<>();
			for(FirstCategory fc:categories){
				if(fc.getSecondCategories() != null){
					for(SecondCategory sc:fc.getSecondCategories()){
						products.addAll(sc.getProducts());
					}
				}
			}
		}
		return products;
	}

	public int getStoreType() {
		return storeType;
	}
	
	public void setStoreType(int type){
		this.storeType = type;
	}
}
