package com.tianpingpai.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest.Parser;

import java.util.ArrayList;

public class ListParser<T> implements Parser<ListResult<T>, String> {
	
	private Class<T> mParseClass;
	static Gson gson = new Gson();
	static JsonParser sParse= new JsonParser();

	public ListParser(Class<T> clazz){
		this.mParseClass = clazz;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ListResult<T> parse(String is) {
		Log.e("xx","list:" + is);
		ListResult<T> mr;
		try{
			JsonObject rootObj = sParse.parse(is).getAsJsonObject();

			int statusCode = rootObj.get("statusCode").getAsInt();
			Log.e("xx", "36---------statusCode="+statusCode);
			if(statusCode == 0){
				is = is.replace("\"\"", "null");
				mr = gson.fromJson(is, ListResult.class);
				JsonElement resultObj = rootObj.get("result");
				if(resultObj != null && resultObj.isJsonObject()){
					JsonObject result = resultObj.getAsJsonObject();
					JsonArray array = null;
					ArrayList<Object> models = new ArrayList<>();
					if(result.get("pageItems") != null && result.get("pageItems").isJsonArray() ){
						array = result.get("pageItems").getAsJsonArray();
						for(int i = 0;i < array.size();i++){
							JsonElement je = array.get(i);
							Object t = gson.fromJson(je, mParseClass);
							models.add(t);
						}
					}
					mr.setModels((ArrayList<T>) models);
				}

			}else{
				mr = new ListResult<>();
				mr.setCode(statusCode);
				mr.setDesc(rootObj.get("statusDesc").getAsString());
				if(statusCode == 1){
					HttpManager.getInstance().notifyEvent(HttpEvent.accessTokenExpired, mr);
				}
			}
			return mr;
		}catch(JsonSyntaxException jse){
			jse.printStackTrace();
			mr = new ListResult<>();
			mr.setCode(-1);
			mr.setDesc("解析数据错误");
			return mr;
		}
	}
}
