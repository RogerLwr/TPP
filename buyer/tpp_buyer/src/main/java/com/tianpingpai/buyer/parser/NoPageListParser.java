package com.tianpingpai.buyer.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.utils.TextUtils;

public class NoPageListParser<T> implements Parser<ModelResult<ArrayList<T>>, String> {

	private Class<?> mParseClass;
	private static Gson g = new Gson();

	public NoPageListParser(Class<T> cc){
		this.mParseClass = cc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ModelResult<ArrayList<T>> parse(String is) {
		JSONObject rootObj;
		ArrayList<T> models;
		ModelResult<ArrayList<T>> lr = new ModelResult<ArrayList<T>>();
		try {
			rootObj = new JSONObject(is);
			int statusCode = rootObj.optInt("statusCode");

			if(statusCode == 0){
				is = is.replace("\"\"", "null");
				models = new ArrayList<T>();
				JSONArray array = rootObj.optJSONArray("result");
				if(array != null){
					for(int i = 0;i < array.length(); i++){
						JSONObject jobj = array.optJSONObject(i);
//						Object t = JSON.parseObject(jobj.toString(), mParseClass);
						Object t = g.fromJson(jobj.toString(), mParseClass);
						models.add((T) t);
					}
				}
				lr.setModel(models);

			}else{
				ModelResult<ArrayList<T>> mr = new ModelResult<ArrayList<T>>();
				mr.setCode(statusCode);
				mr.setDesc(TextUtils.optString(rootObj,"statusDesc"));
				if(statusCode == 1){
					HttpManager.getInstance().notifyEvent(HttpEvent.accessTokenExpired, mr);
				}
				lr = mr;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return lr;
	}
}
