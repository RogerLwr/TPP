package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.buyer.model.Promotion;

import java.util.ArrayList;
import java.util.List;

public class GadgetModel {
	
	public static final int TYPE_BANNER = 1;
	public static final int TYPE_FUNCTION = 2;
	public static final int TYPE_CATEGORY = 3;
	public static final int TYPE_TITLE = 4;
	public static final int TYPE_ACTIVITY = 5;
	public static final int TYPE_STORES = 6;
    public static final int TYPE_NOTIFICATION = 7;
	public static final int TYPE_ACTIVITY_NEW = 8;

	public static class Item{
		@SerializedName("title")
		private String title;
		@SerializedName("image")
		private String image;
		@SerializedName("href")
		private String url;
		@SerializedName("disable")
		private boolean disable;

		public boolean isDisabled() {
			return disable;
		}

		public void setDisabled(boolean disable) {
			this.disable = disable;
		}

		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        @SerializedName("data")
        private Data data;

	}
	
	@SerializedName("type")
	private int type;
	@SerializedName("title")
	private String title;
	@SerializedName("content")
	private ArrayList<Item> content;

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<Item> getContent() {
		return content;
	}
	public void setContent(ArrayList<Item> content) {
		this.content = content;
	}

	public static class Data{
		public int getShopId() {
			return shopId;
		}

		public void setShopId(int shopId) {
			this.shopId = shopId;
		}
        @SerializedName("shopId")
		private int shopId;

		public int getOrderNum() {
			return orderNum;
		}

		public void setOrderNum(int orderNum) {
			this.orderNum = orderNum;
		}

		@SerializedName("orderNum")
		private int orderNum;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		private String description;
		private List<Promotion> promotions;

		public List<Promotion> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<Promotion> promotions) {
			this.promotions = promotions;
		}

		public double getRatings() {
            return ratings;
        }

        public void setRatings(double ratings) {
            this.ratings = ratings;
        }

        @SerializedName("score")
        private double ratings;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @SerializedName("address")
        private String address;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @SerializedName("content")
        private String content;

	}
}
