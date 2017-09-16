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

public class JSONListParser implements HttpRequest.Parser<ListResult<Model>, String> {

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
    public ListResult<Model> parse(String is) throws JSONException {
        ListResult<Model> mr = new ListResult<>();
        Log.e("xx", "32---------is=" + is);
        JSONObject rootObj = new JSONObject(is);

        int statusCode = rootObj.optInt("statusCode", -1);
        if (statusCode == 0) {
            JSONObject result = null;
            JSONArray array = null;
            if (isPaged()) {
                result = rootObj.optJSONObject("result");
                if (result != null) {
                    if (isList) {
                        array = result.optJSONArray("list");
                    } else {
                        array = result.optJSONArray("pageItems");
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

                ListResult.Page page = new ListResult.Page();
                if (result != null) {
                    page.setPageIndex(result.optInt("pageIndex"));
                    page.setTotalPageNumber(result.optInt("totalPageCount"));
                    page.setLastPage(result.optBoolean("lastPage"));
                    page.setTotalItemCount(result.optInt("totalItemCount"));
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
