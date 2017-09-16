package com.tianpingpai.crm.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.model.StoreCategoryModel;
import com.tianpingpai.parser.ListResult;

import java.util.ArrayList;

/**
 * Created by Chon on 15/6/11.
 */
public class CategoriesParser implements HttpRequest.Parser<ListResult<StoreCategoryModel>,String>{
    static JsonParser sParse= new JsonParser();
    static Gson gson = new Gson();

    //TODO
    @Override
    public ListResult<StoreCategoryModel> parse(String is) {
        ListResult<StoreCategoryModel> mr;
        try{
            is = is.replace("\"\"", "null");
            JsonObject rootObj = sParse.parse(is).getAsJsonObject();

            int statusCode = rootObj.get("statusCode").getAsInt();
            if(statusCode == 0){
                mr = new ListResult<>();
                ListResult.Page<StoreCategoryModel> p  = new ListResult.Page<>();
                p.setLastPage(true);
                mr.setPage(p);

                JsonElement result = rootObj.get("result");
                if(result != null){
                    JsonArray array = result.getAsJsonArray();
                    ArrayList<StoreCategoryModel> models = new ArrayList<>();
                    for(int i = 0;i < array.size();i++){
                        JsonElement je = array.get(i);
                        StoreCategoryModel t = gson.fromJson(je, StoreCategoryModel.class);
                        models.add(t);
                    }
                    mr.setModels( models);
                }
            } else {
                mr = new ListResult<>();
                mr.setCode(statusCode);


                if(statusCode == 1) {
                    HttpManager.getInstance().notifyEvent(HttpEvent.accessTokenExpired, mr);
                }
            }
            mr.setDesc(rootObj.get("statusDesc").getAsString());
            return mr;
        }catch(JsonSyntaxException jse){
            jse.printStackTrace();
            return null;
        }
    }
}
