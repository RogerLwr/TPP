package com.tianpingpai.buyer.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.parser.ListResult;

import java.util.ArrayList;

/** 解析的 接口 中 result 直接是数组对象的情况
 * @author lwr
 *
 */
public class ListParserNoPageItems<T> implements Parser<ArrayList<Object>, String> {
	private Class<T> mParseClass;
	static Gson gson = new Gson();
	static JsonParser sParse= new JsonParser();

	public ListParserNoPageItems(Class<T> clazz){
		this.mParseClass = clazz;
	}
	
	@Override
	public ArrayList<Object> parse(String is) {
		Log.e("xx","34-----------list:" + is);
		ArrayList<Object> models = null;
		try{
			JsonObject rootObj = sParse.parse(is).getAsJsonObject();
			int statusCode = rootObj.get("statusCode").getAsInt();
//			JSONObject rootObj = new JSONObject(is);
//			int statusCode = rootObj.optInt("statusCode");
//			Log.e("xx", "39----------statusCode="+statusCode);
			if(statusCode == 0){
				models = new ArrayList<>();
//				JSONArray array = rootObj.optJSONArray("result");
				JsonArray array = rootObj.get("result").getAsJsonArray();
				Log.e("xx", "43----------array="+array.toString());
				if(array != null){
					for(int i = 0;i < array.size(); i++){
//					for(int i = 0;i < array.length(); i++){
//						JSONObject jobj = array.optJSONObject(i);
//						Object t = JSON.parseObject(jobj.toString(), mParseClass);
						JsonElement je = array.get(i);
						Object t = gson.fromJson(je, mParseClass);
						models.add(t);
					}
				}
				Log.e("xx", "51----------------models="+models);
			}else{
				ListResult<T> mr = new ListResult<>();
				mr.setCode(statusCode);
//				mr.setDesc(rootObj.optString("statusDesc"));
				mr.setDesc(rootObj.get("statusDesc").getAsString());
				if(statusCode == 1) {
					HttpManager.getInstance().notifyEvent(HttpEvent.accessTokenExpired, mr);
				}
			}
			return models;
		}catch(Exception jse){
			jse.printStackTrace();
			return null;
		}
	}

}
