package com.tianpingpai.buyer.parser;

import com.tianpingpai.buyer.model.LayeredProduct;
import com.tianpingpai.buyer.model.LayeredProduct.FirstCategory;
import com.tianpingpai.buyer.model.LayeredProduct.SecondCategory;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShopProductsParser implements Parser<ModelResult<LayeredProduct>, String> {

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

				ArrayList<FirstCategory> firstCategoris = new ArrayList<>();
				for (int i = 0; i < objArray.length(); i++) {
					FirstCategory first = new FirstCategory();
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
							//TODO
							pm.setId(prodObj.optLong("id"));
							pm.setUnit(TextUtils.optString(prodObj, "unit"));
//							pm.setPrice(prodObj.optDouble("price"));
							pm.setDescription(TextUtils.optString(prodObj,"description"));
							pm.setSellerId(prodObj.optInt("sale_user_id"));
							pm.setSaleNumber(prodObj.optInt("sale_num"));
							pm.setCouponPrice(prodObj.optDouble("coupon_price"));
							pm.setName(TextUtils.optString(prodObj,"prod_name"));
							pm.setSellerName(shopName);

							pm.setImageUrl(TextUtils.optString(prodObj,"prod_img"));
//							pm.setCategoryId(second.getId());
//							pm.setSellerName(shopName);
//							pm.setCategoryId(secondCategoryId);
						}
						first.setProducts(prodlist);
					}else{
						ArrayList<SecondCategory> secondCategories = new ArrayList<>();
						JSONArray secondArray = obj.optJSONArray("category_list");
						for (int j = 0; j < secondArray.length(); j++) {
							SecondCategory second = new SecondCategory();
							JSONObject secondObj = (JSONObject) secondArray.opt(j);
							int secondCategoryId = secondObj.getInt("scategory_id");
							second.setId(secondCategoryId);
							second.setName(TextUtils.optString(secondObj,"scategory_name"));
							secondCategories.add(second);
							JSONArray prodArray = secondObj.optJSONArray("prodlist");
							ArrayList<ProductModel> prodlist = new ArrayList<>();
							for (int k = 0; k < prodArray.length(); k++) {
								JSONObject prodObj = prodArray.getJSONObject(k);
								ProductModel pm = new ProductModel();
								prodlist.add(pm);

								pm.setId(prodObj.optLong("id"));
								pm.setUnit(TextUtils.optString(prodObj,"unit"));
//								pm.setPrice(prodObj.optDouble("price"));
								pm.setDescription(TextUtils.optString(prodObj,"description"));
								pm.setSellerId(prodObj.optInt("sale_user_id"));
								pm.setCouponPrice(prodObj.optDouble("coupon_price"));
								pm.setName(TextUtils.optString(prodObj,"prod_name"));
								pm.setSaleNumber(prodObj.optInt("sale_num"));
//								Log.e("xx", "name:" + pm.getName() + "-" + prodObj.optString("prod_img"));
								pm.setImageUrl(TextUtils.optString(prodObj,"prod_img"));
								pm.setSellerName(shopName);
								pm.setCategoryId(second.getId());
								pm.setCategoryId(secondCategoryId);
							}
							second.setProducts(prodlist);
//							ShoppingCartManager.getInstance().syncProductNumberFromDb(prodlist,false);
						}
						first.setSecondCategories(secondCategories);
					}
				}
				lp.setCategories(firstCategoris);
				mr.setModel(lp);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mr;
	}


}
