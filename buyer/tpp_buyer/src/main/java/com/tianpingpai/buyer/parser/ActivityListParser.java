package com.tianpingpai.buyer.parser;

import android.util.Log;

import com.tianpingpai.buyer.model.ActivityModel;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ListResult.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityListParser implements Parser<ListResult<ActivityModel>, String> {

    @Override
    public ListResult<ActivityModel> parse(String is) throws JSONException {
        ListResult<ActivityModel> listResult = new ListResult<>();
        JSONObject rootObj;
        rootObj = new JSONObject(is);
        int statusCode = rootObj.getInt("statusCode");
        String statusDesc = rootObj.getString("statusDesc");
        listResult.setCode(statusCode);
        listResult.setDesc(statusDesc);

        JSONObject result = rootObj.getJSONObject("result");
        if (result != null) {
            JSONArray items = result.getJSONArray("pageItems");
            ArrayList<ActivityModel> activities = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject activityObj = items.getJSONObject(i);
                ActivityModel am = new ActivityModel();
                am.setId(activityObj.getLong("id"));
                am.setName(activityObj.getString("name"));
                am.setIntroduction(activityObj.getString("introduction"));
                am.setProductName(activityObj.getString("product"));
                am.setTotal(activityObj.getInt("total"));
                am.setLimitNumber(activityObj.getInt("accessNumber"));
                am.setPrice(activityObj.getDouble("price"));
                am.setUnit(activityObj.getString("unit"));

                JSONArray imageArray = activityObj.optJSONArray("images");
                if (imageArray != null) {
                    ArrayList<String> images = new ArrayList<>();
                    for (int j = 0; j < imageArray.length(); j++) {
                        String url = imageArray.getString(j);
                        images.add(url);
                    }
                    am.setImages(images);
                }

                activities.add(am);
            }
            Page<ActivityModel> page = new Page<>();
            listResult.setPage(page);
            listResult.setModels(activities);
            //TODO page
        } else {
            Log.e("xx", "result is null");
        }
        return listResult;
    }
}
