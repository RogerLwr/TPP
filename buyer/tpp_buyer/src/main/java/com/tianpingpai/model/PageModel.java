package com.tianpingpai.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class PageModel {
	@SerializedName("title")
	private String title;
	@SerializedName("gadgets")
	private ArrayList<GadgetModel> gadgets;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<GadgetModel> getGadgets() {
		return gadgets;
	}
	public void setGadgets(ArrayList<GadgetModel> gadgets) {
		this.gadgets = gadgets;
	}
}
