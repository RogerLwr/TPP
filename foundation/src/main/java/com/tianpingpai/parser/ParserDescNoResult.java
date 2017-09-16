package com.tianpingpai.parser;

import android.util.Log;

import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParserDescNoResult implements HttpRequest.Parser<ModelResult<Model>, String> {

    public boolean isResultIsObj() {
        return resultIsObj;
    }

    public void setResultIsObj(boolean resultIsObj) {
        this.resultIsObj = resultIsObj;
    }

    private boolean resultIsObj = false;


    @Override
    public ModelResult<Model> parse(String is) {
        ModelResult<Model> mr = new ModelResult<>();
        Log.e("xx", "30---------is=" + is);
        try {
            JSONObject rootObj = new JSONObject(is);

            int statusCode = rootObj.optInt("statusCode", -1);

            mr.setCode(statusCode);
            mr.setDesc(rootObj.getString("statusDesc"));

            if (statusCode == 0) {
                JSONObject result = null;
                String str = null;
                Model model = new Model();
                if(isResultIsObj()){
                    result = rootObj.optJSONObject("result");
                }else{
                    str = rootObj.optString("result");
                    model.set("result",str);
                }

                if(result != null){
                    /*ArrayList<Model> models = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject je = array.optJSONObject(i);
                        Model m = new Model();
                        JSONModelMapper.mapObject(je, m);
                        models.add(m);
                    }

                    ListResult.Page page = new ListResult.Page();
                    if(result != null){
                        page.setPageIndex(result.optInt("pageIndex"));
                        page.setTotalPageNumber(result.optInt("totalPageCount"));
                        page.setLastPage(result.optBoolean("lastPage"));
                    }
                    mr.setPage(page);
                    mr.setModels(models);*/
                }
                if(str !=null){
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
