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
/** 解析的 接口 中 result 直接是数组对象的情况
 * @author lwr
 *
 */
public class ListParserNoPageItems<T> implements Parser<ModelResult<T>, String> {

	private Class<T> mParseClass;
	static Gson gson = new Gson();
	static JsonParser sParse= new JsonParser();

	public ListParserNoPageItems(Class<T> clazz){
		this.mParseClass = clazz;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ModelResult<T> parse(String is) {
		Log.e("xx", "i" + is);
		ModelResult<T> mr;

		JsonObject rootObj = sParse.parse(is).getAsJsonObject();

		int statusCode = rootObj.get("statusCode").getAsInt();

		if (statusCode == 0) {
			// is = is.replace("\"\"", "null");
			mr = gson.fromJson(is, ModelResult.class);
//			JsonElement result = rootObj.get("result");
			JsonArray array = rootObj.get("result").getAsJsonArray();
			Log.e("xx", "45----------array="+array.toString());
			ArrayList<Object> models = new ArrayList<Object>();
			for(int i = 0;i < array.size();i++){
				JsonElement je = array.get(i);
				Object t = gson.fromJson(je, mParseClass);
				models.add(t);
			}
			Log.e("xx", "52----------------models="+models);
			mr.setModel((T) models);
//			mr.setModel(gson.fromJson(result, mParseClass));
		} else {
			mr = new ModelResult<>();
			mr.setCode(statusCode);
			mr.setDesc(rootObj.get("statusDesc").getAsString());
			if (statusCode == 1) {
				HttpManager.getInstance().notifyEvent(
						HttpEvent.accessTokenExpired, mr);
			}
		}
		return mr;
	}
}
