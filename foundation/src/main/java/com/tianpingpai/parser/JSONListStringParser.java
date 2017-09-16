package com.tianpingpai.parser;

import android.util.Log;

import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yi on 2015/12/2.
 */
public class JSONListStringParser implements HttpRequest.Parser<ListResult<Model>, String> {

    public boolean isPaged() {
        return paged;
    }

    public void setPaged(boolean paged) {
        this.paged = paged;
    }

    private boolean paged = true;
    /**
     当result的数组参数 不是pageItems 而是list
     */
    private boolean isList = false;
    public void setIsList(boolean isList){
        this.isList = isList;
    }


    @Override
    public ListResult<Model> parse(String is) {
        ListResult<Model> mr = new ListResult<>();
        Log.e("xx", "32---------is=" + is);
        try {
            JSONObject rootObj = new JSONObject(is);

            int statusCode = rootObj.optInt("statusCode", -1);

            if (statusCode == 0) {

                JSONArray array = null;

                array = rootObj.optJSONArray("result");

                Log.e("array",array.toString()+""+array.length());

                if(array != null){
                    ArrayList<Model> models = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String name = array.optString(i);
                        Model m = new Model();
                        m.set("name", name);
                        models.add(m);
                    }
                    Log.e("models",models.toString()+models.size());
                    Log.e("mr", mr.toString() + "" + mr);
                    ListResult.Page page = new ListResult.Page();
                    page.setLastPage(true);
                    mr.setPage(page);
                    mr.setModels(models);
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
