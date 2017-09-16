package com.tianpingpai.buyer.parser;

import android.util.Log;

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

public class MultiShopParser implements
		Parser<ModelResult<LayeredProduct>, String> {

	@Override
	public ModelResult<LayeredProduct> parse(String is) {
		ModelResult<LayeredProduct> mr = new ModelResult<>();
		try {
			Log.e("xx", "is");
			JSONObject response = new JSONObject(is);
			int statusCode = response.getInt("statusCode");
			String statusDesc = response.getString("statusDesc");
			mr.setCode(statusCode);
			mr.setDesc(statusDesc);
			JSONObject result = response.optJSONObject("result");
			if (statusCode == 0 && result != null) {
				int multiShopId = result.getInt("shop_id");
				String shopName = result.getString("shop_name");

				JSONArray objArray = result.getJSONArray("prod_list");
				LayeredProduct lp = new LayeredProduct();
				lp.setStoreId(multiShopId);
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
					ArrayList<SecondCategory> secondCategories = new ArrayList<>();
					JSONArray secondArray = obj.optJSONArray("category_list");

					if(first.getId() == -1){
						JSONArray prodArray = obj.optJSONArray("prodlist");
						ArrayList<ProductModel> prodlist = new ArrayList<>();
						for (int k = 0; k < prodArray.length(); k++) {
							JSONObject prodObj = prodArray.getJSONObject(k);
							ProductModel pm = new ProductModel();
							prodlist.add(pm);
							//TODO
							pm.setId(prodObj.optLong("id"));
							pm.setUnit(TextUtils.optString(prodObj,"unit"));
//							pm.setPrice(prodObj.optDouble("price"));
							pm.setDescription(TextUtils.optString(prodObj,"description"));
							pm.setSellerId(prodObj.optInt("sale_user_id"));
							pm.setCouponPrice(prodObj.optDouble("coupon_price"));
							pm.setName(TextUtils.optString(prodObj,"prod_name"));
							pm.setSellerName(shopName);
							pm.setImageUrl(TextUtils.optString(prodObj,"prod_img"));
						}
						first.setProducts(prodlist);
					}else{

						for (int j = 0; j < secondArray.length(); j++) {
							SecondCategory second = new SecondCategory();
							JSONObject secondObj = (JSONObject) secondArray
									.opt(j);
							second.setId(secondObj.getInt("scategory_id"));
							second.setName(TextUtils.optString(secondObj
									,"scategory_name"));
							secondCategories.add(second);
							JSONArray prodArray = secondObj
									.optJSONArray("prodlist");
							ArrayList<ProductModel> prodlist = new ArrayList<>();
							for (int k = 0; k < prodArray.length(); k++) {
								JSONObject prodObj = prodArray.getJSONObject(k);

								ProductModel pm = new ProductModel();
								prodlist.add(pm);

								pm.setId(prodObj.optLong("id"));
								pm.setUnit(TextUtils.optString(prodObj,"unit"));
//								pm.setPrice(prodObj.optDouble("price"));
								// pm.set
								pm.setDescription(TextUtils.optString(prodObj
										,"description"));
								pm.setMultiShopId(multiShopId);
								pm.setSellerId(prodObj.optInt("sale_user_id"));
								pm.setCouponPrice(prodObj
										.optDouble("coupon_price"));
								pm.setName(TextUtils.optString(prodObj,"prod_name"));
								pm.setImageUrl(TextUtils.optString(prodObj,"prod_img"));
								pm.setSellerName(shopName);
								pm.setCategoryId(second.getId());
								pm.setMultiShopId(multiShopId);
							}
							second.setProducts(prodlist);
							// ShoppingCartManager.getInstance().syncProductNumberFromDb(prodlist,true);
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
