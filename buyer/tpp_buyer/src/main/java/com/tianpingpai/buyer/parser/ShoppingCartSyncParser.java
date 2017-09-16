package com.tianpingpai.buyer.parser;

import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ListResult.Page;
import com.tianpingpai.utils.SingletonFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShoppingCartSyncParser implements
		Parser<ListResult<Pair<SellerModel, ArrayList<ProductModel>>>, String> {

	@Override
	public ListResult<Pair<SellerModel, ArrayList<ProductModel>>> parse(
			String is) {
		Log.e("xx","is:" + is);
		ListResult<Pair<SellerModel, ArrayList<ProductModel>>> result = new ListResult<>();
		try {
			JSONObject rootObj = new JSONObject(is);
			int code = rootObj.getInt("statusCode");
			String statusDesc = rootObj.getString("statusDesc");
			result.setCode(code);
			result.setDesc(statusDesc);
			JSONArray resultArray = rootObj.optJSONArray("result");
			if (code == 0 && resultArray != null) {// TODO
				ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> data = new ArrayList<>();
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject resultObj = resultArray.getJSONObject(i);
					SellerModel seller = new SellerModel();
					long sellerId = resultObj.optLong("sale_user_id");
					seller.setId(sellerId);
					String sellerName = resultObj.getString("user_name");
					seller.setSaleName(sellerName);
					seller.setDisplayName(sellerName);//TODO
					seller.setAddress(resultObj.getString("address"));
					seller.setFreight(resultObj.optDouble("freight"));
					seller.setMinAmount(resultObj.optDouble("minAmount"));
					seller.setStartingPrice(resultObj.optDouble("startingPrice"));
					if(!resultObj.isNull("remarks")) {
						seller.setRemark(resultObj.getString("remarks"));
					}
					JSONArray productArray = resultObj
							.optJSONArray("prod_list");
					ArrayList<ProductModel> productList = new ArrayList<>();
					if (productArray != null) {
						for(int j = 0;j < productArray.length();j++){
							
							JSONObject productObj = productArray.getJSONObject(j);
							
							int categoryId = productObj.optInt("pcategory_id");
							ProductModel p = SingletonFactory.getInstance(Gson.class).fromJson(productObj.toString(), ProductModel.class);
							p.setCategoryId(categoryId);
                            p.setId(productObj.optInt("id"));
							productList.add(p);
							p.setSellerName(sellerName);
							p.setSellerId((int) sellerId);
//							p.setPrice(productObj.optDouble("price"));
							p.setCouponPrice(productObj.optDouble("coupon_price"));

							p.setCartStatus(ProductModel.STATUS_IN_CART);
							Log.e("xx","id" + p.getId() + "catId:" + p.getCategoryId() + "sellerID:" + p.getSellerId());
						}
					}
//					ShoppingCartManager.getInstance().save(productList);
					data.add(new Pair<>(seller,productList));
				}
				ListResult.Page<Pair<SellerModel, ArrayList<ProductModel>>> page = new Page<>();
				result.setPage(page);
				result.setModels(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
