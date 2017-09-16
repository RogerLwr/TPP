package com.tianpingpai.http.util;

import android.util.Log;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.parser.ModelResult;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorReporter {
    private static HttpRequest.ResultListener<ModelResult<Void>> listener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
//            Log.e("xx","errorReport:" + data.getDesc());
        }
    };

    public static void reportError(HttpRequest<?> req,String response,Exception e){
        String url = ContextProvider.getBaseURL() + "/api/collect/app_error";
        HttpRequest<ModelResult<Void>> request = new HttpRequest<>(url,listener);
        request.setMethod(HttpRequest.POST);
        request.addParam("use_interface", "" + req.getUrl());
        request.addParam("params","" + req.getParams().toString());
        request.addParam("result","" + response);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        request.setParser(new ErrorReportParser(Void.class));
        request.addParam("error_info", "" + sw.toString());
        VolleyDispatcher.getInstance().dispatch(request);
        Log.e("xx","reporting error:" + sw.toString());
    }
}
