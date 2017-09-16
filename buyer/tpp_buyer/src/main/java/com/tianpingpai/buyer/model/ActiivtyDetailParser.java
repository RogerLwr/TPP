package com.tianpingpai.buyer.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.utils.SingletonFactory;

public class ActiivtyDetailParser implements Parser<ModelResult<ActivityModel>, String> {

	@Override
	public ModelResult<ActivityModel> parse(String i) {
		try {
			JSONObject rootObj = new JSONObject(i);
			ModelResult<ActivityModel> mr = new ModelResult<ActivityModel>();
			int statusCode = rootObj.getInt("statusCode");
			String statusDesc = rootObj.getString("statusDesc");
			mr.setCode(statusCode);
			mr.setDesc(statusDesc);
			

			JSONObject actiivyObj = rootObj.optJSONObject("result");
			if(actiivyObj != null){
				ActivityModel am = new ActivityModel();
				am.setId(actiivyObj.getLong("id"));
				am.setName(actiivyObj.getString("name"));
				am.setIntroduction(actiivyObj.getString("introduction"));
				//html
				am.setProductName(actiivyObj.getString("product"));
				am.setTotal(actiivyObj.getInt("total"));
				am.setLimitNumber(actiivyObj.getInt("accessNumber"));
				am.setPrice(actiivyObj.optDouble("price"));
				am.setUnit(actiivyObj.getString("unit"));
				am.setRules(actiivyObj.getString("rules"));
				am.setEndTime(actiivyObj.getString("endTime"));
				am.setCurrentTime(actiivyObj.getString("currentTime"));
				
				JSONObject productObj = actiivyObj.optJSONObject("productExt");
				if(productObj != null){
					//TODO
					ProductModel p = SingletonFactory.getInstance(Gson.class).fromJson(productObj.toString(), ProductModel.class);
					p.setCategoryId(productObj.optInt("category_id"));
					am.setProduct(p);
					
				}
				
				JSONObject sellerObj = actiivyObj.optJSONObject("saler");
				if(sellerObj != null){
					SellerModel seller = SingletonFactory.getInstance(Gson.class).fromJson(sellerObj.toString(), SellerModel.class);
					am.setSeller(seller);
				}
				
				JSONArray imageArray = actiivyObj.optJSONArray("images");
				if(imageArray != null){
					ArrayList<String> images = new ArrayList<String>();
					for(int j = 0;j < imageArray.length();j++){
						String url = imageArray.getString(j);
						images.add(url);
					}
					am.setImages(images);
				}
				
				mr.setModel(am);
			}
			
			return mr;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
