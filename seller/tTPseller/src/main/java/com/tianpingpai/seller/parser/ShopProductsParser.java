package com.tianpingpai.seller.parser;


import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.model.LayeredProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopProductsParser implements HttpRequest.Parser<ModelResult<LayeredProduct>, String> {
	
	private String shopName;
	
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	@Override
	public ModelResult<LayeredProduct> parse(String is) {
		ModelResult<LayeredProduct> mr = new ModelResult<>();
		try {
			JSONObject response = new JSONObject(is);
			int statusCode = response.getInt("statusCode");
			String statusDesc = response.getString("statusDesc");
			mr.setCode(statusCode);
			mr.setDesc(statusDesc);
			JSONArray objArray = response.optJSONArray("result");
			if(statusCode == 0 && objArray != null){
				LayeredProduct lp = new LayeredProduct();
				
				ArrayList<LayeredProduct.FirstCategory> firstCategoris = new ArrayList<>();
				for (int i = 0; i < objArray.length(); i++) {
					LayeredProduct.FirstCategory first = new LayeredProduct.FirstCategory();
					firstCategoris.add(first);
					JSONObject obj = (JSONObject) objArray.opt(i);
					int fcategory_id;
					fcategory_id = obj.getInt("fcategory_id");
					String fcategory_name = obj.getString("fcategory_name");
					first.setId(fcategory_id);
					first.setName(fcategory_name);
					
					if(first.getId() == -1){
						JSONArray prodArray = obj.optJSONArray("prodlist");
						ArrayList<ProductModel> prodlist = new ArrayList<>();
						for (int k = 0; k < prodArray.length(); k++) {
							JSONObject prodObj = prodArray.getJSONObject(k);
							ProductModel pm = new ProductModel();
							prodlist.add(pm);
							List<Model> attrModels = new ArrayList<>();
							pm.setAttrList(attrModels);
							if(prodObj.optJSONArray("attr_list") != null){
								JSONArray attrArray = prodObj.optJSONArray("attr_list");
								for(int j=0; j<attrArray.length(); j++){
									JSONObject attrObj = attrArray.getJSONObject(j);
									Model attrModel = new Model();
									attrModels.add(attrModel);
									attrModel.set("id", attrObj.optInt("id"));
									attrModel.set("attr_type", attrObj.optString("attr_type"));
									attrModel.set("modify_value", attrObj.optString("modify_value"));
									attrModel.set("modify_level", attrObj.optString("modify_level"));
								}
							}

							//TODO
							pm.setPid(prodObj.optLong("pid"));
							pm.setSalesStatus(prodObj.optString("sales_status"));
							pm.setProdStatus(prodObj.optString("prod_status"));
							pm.setStartTime(prodObj.optString("start_time"));
							pm.setEndTime(prodObj.optString("end_time"));
							pm.setLimit(prodObj.optString("limit"));
							pm.setStock(prodObj.optInt("stock"));
							pm.setAttrStand(prodObj.optString("attr_stand"));
							pm.setAttrWeight(prodObj.optString("attr_weight"));
							pm.setColor(prodObj.optString("color"));
							pm.setfCategoryId(-1);
							pm.setId(prodObj.optLong("id"));
							pm.setUnit(prodObj.getString("unit"));
//							pm.setPrice(prodObj.optDouble("price"));
							pm.setStatus(prodObj.optInt("status"));
							pm.setDescription(prodObj.optString("description"));
							pm.setSellerId(prodObj.optInt("sale_user_id"));
							pm.setCouponPrice(prodObj.optDouble("coupon_price"));
							pm.setName(prodObj.optString("prod_name"));
							pm.setSellerName(shopName);

							pm.setImageUrl(prodObj.optString("prod_img"));

							pm.setfCategoryName(prodObj.optString("fcategory_name"));
							pm.setSecondCategoryID(prodObj.optInt("pcategory_id"));
							pm.setpCategoryName(prodObj.optString("pcategory_name"));
							pm.setCategoryId(prodObj.optInt("category_id"));
							pm.setCategoryName(prodObj.optString("category_name"));

						}
						first.setProducts(prodlist);
					}else{
						ArrayList<LayeredProduct.SecondCategory> secondCategories = new ArrayList<>();
						JSONArray secondArray = obj.optJSONArray("category_list");
						for (int j = 0; j < secondArray.length(); j++) {
							LayeredProduct.SecondCategory second = new LayeredProduct.SecondCategory();
							JSONObject secondObj = (JSONObject) secondArray.opt(j);
							int secondCategoryId = secondObj.getInt("scategory_id");
							second.setId(secondCategoryId);
							second.setName(secondObj.getString("scategory_name"));
							secondCategories.add(second);
							JSONArray prodArray = secondObj.optJSONArray("prodlist");
							ArrayList<ProductModel> prodlist = new ArrayList<>();
							for (int k = 0; k < prodArray.length(); k++) {
								JSONObject prodObj = prodArray.getJSONObject(k);
								ProductModel pm = new ProductModel();
								prodlist.add(pm);
								
								pm.setId(prodObj.optLong("id"));
								pm.setUnit(prodObj.getString("unit"));
//								pm.setPrice(prodObj.optDouble("price"));
								pm.setStatus(prodObj.optInt("status"));
								pm.setDescription(prodObj.optString("description"));
								pm.setSellerId(prodObj.optInt("sale_user_id"));
								pm.setCouponPrice(prodObj.optDouble("coupon_price"));
								pm.setName(prodObj.optString("prod_name"));
//								Log.e("xx", "name:" + pm.getName() + "-" + prodObj.optString("prod_img"));
                                pm.setImageUrl(prodObj.optString("prod_img"));
								pm.setSellerName(shopName);
//								pm.setCategoryId(second.getId());
//								pm.setCategoryId(secondCategoryId);

								pm.setfCategoryId(prodObj.optInt("fcategory_id"));
								pm.setfCategoryName(prodObj.optString("fcategory_name"));
								pm.setSecondCategoryID(prodObj.optInt("pcategory_id"));
								pm.setpCategoryName(prodObj.optString("pcategory_name"));
								pm.setCategoryId(prodObj.optInt("category_id"));
								pm.setCategoryName(prodObj.optString("category_name"));
							}
							second.setProducts(prodlist);
//							ShoppingCartManager.getInstance().syncProductNumberFromDb(prodlist,false);
						}
						first.setSecondCategories(secondCategories);
					}
				}
				lp.setCategories(firstCategoris);
				mr.setModel(lp);
			}else if(statusCode == 1){
				HttpManager.getInstance().notifyEvent(HttpEvent.accessTokenExpired, mr);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mr;
	}

}
