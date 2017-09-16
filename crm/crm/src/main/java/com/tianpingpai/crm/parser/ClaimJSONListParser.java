package com.tianpingpai.crm.parser;

import android.util.Log;

import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONModelMapper;
import com.tianpingpai.parser.ListResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClaimJSONListParser implements HttpRequest.Parser<ClaimListResult<Model>, String>{

    public boolean isPaged() {
        return paged;
    }

    public void setPaged(boolean paged) {
        this.paged = paged;
    }

    private boolean paged = true;
    /**
     * 当result的数组参数 不是pageItems 而是list
     */
    private boolean isList = false;

    public void setIsList(boolean isList) {
        this.isList = isList;
    }


    @Override
    public ClaimListResult<Model> parse(String is) throws JSONException {
        ClaimListResult<Model> mr = new ClaimListResult<>();
        Log.e("xx", "32---------is=" + is);
        JSONObject rootObj = new JSONObject(is);

        int statusCode = rootObj.optInt("statusCode", -1);
        if (statusCode == 0) {
            JSONObject result = null;
            JSONArray array = null;
            JSONObject results = null;
            int num ;
            if (isPaged()) {
                result = rootObj.optJSONObject("result");
                if(result != null){
                    num = result.getInt("claim_num");
                    mr.setNum(num);
                    results = result.getJSONObject("results");
                    if (results != null) {
                        if (isList) {
                            array = results.optJSONArray("list");
                        } else {
                            array = results.optJSONArray("pageItems");
                        }
                    }
                }
            } else {
                array = rootObj.optJSONArray("result");
            }

            if (array != null) {
                ArrayList<Model> models = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject je = array.optJSONObject(i);
                    Model m = new Model();
                    JSONModelMapper.mapObject(je, m);
                    models.add(m);
                }

                ClaimListResult.Page page = new ClaimListResult.Page();
                if (results != null) {
                    page.setPageIndex(results.optInt("pageIndex"));
                    page.setTotalPageNumber(results.optInt("totalPageCount"));
                    page.setLastPage(results.optBoolean("lastPage"));
                }
                if (!paged) {
                    page.setLastPage(true);
                }
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
        return mr;
    }
}
