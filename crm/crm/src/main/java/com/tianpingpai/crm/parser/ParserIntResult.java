package com.tianpingpai.crm.parser;

import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.ModelResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ParserIntResult implements HttpRequest.Parser<ModelResult<Model>, String> {

    @Override
    public ModelResult<Model> parse(String is) {

        ModelResult<Model> mr = new ModelResult<>();
        try {
            JSONObject rootObj = new JSONObject(is);

            int statusCode = rootObj.optInt("statusCode", -1);

            mr.setCode(statusCode);
            mr.setDesc(rootObj.getString("statusDesc"));

            if (statusCode == 0) {
                Model model = new Model();
                int i = -1;
                i = rootObj.optInt("result");
                model.set("result",i);
                if(-1!=i){
                    mr.setModel(model);
                }
            } else {
                mr.setCode(statusCode);
                mr.setDesc(rootObj.getString("statusDesc"));
                if (statusCode == 1) {
                    HttpManager.getInstance().notifyEvent(HttpEvent.accessTokenExpired, mr);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mr;
    }
}
