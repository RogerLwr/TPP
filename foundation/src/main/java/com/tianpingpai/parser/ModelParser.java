package com.tianpingpai.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest.Parser;

import java.util.HashMap;

public class ModelParser<T> implements Parser<ModelResult<T>, String> {
	private Class<T> mParseClass;
	static Gson gson = new Gson();
	static JsonParser sParse = new JsonParser();

	public ModelParser(Class<T> parseClass) {
		mParseClass = parseClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ModelResult<T> parse(String is) {
		ModelResult<T> mr;

		JsonObject rootObj = sParse.parse(is).getAsJsonObject();

		int statusCode = rootObj.get("statusCode").getAsInt();

		if (statusCode == 0) {
			// is = is.replace("\"\"", "null");
			mr = gson.fromJson(is, ModelResult.class);
			JsonElement result = rootObj.get("result");
			mr.setModel(gson.fromJson(result, mParseClass));
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
