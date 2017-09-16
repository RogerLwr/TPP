package com.tianpingpai.parser;

import android.util.Log;

import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class GenericModelParser implements HttpRequest.Parser<ModelResult<Model>, String> {
    @Override
    public ModelResult<Model> parse(String s) throws JSONException {
        ModelResult<Model> result = new ModelResult<>();
        result.setCode(-1);
        Log.e("xx GenericModelParser", s);
        JSONObject rootObj = new JSONObject(s);
        int statusCode = rootObj.optInt("statusCode");
        result.setCode(statusCode);
        result.setDesc(rootObj.getString("statusDesc"));
        JSONObject resultObj = rootObj.optJSONObject("result");
        if (statusCode == 0 && resultObj != null) {
            Model model = new Model();
            JSONModelMapper.mapObject(resultObj, model);
            result.setModel(model);
        }
        if (statusCode == 1) {
            HttpManager.getInstance().notifyEvent(HttpEvent.accessTokenExpired, result);
        }
        return result;
    }
}
