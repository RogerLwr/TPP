package com.tianpingpai.utils;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;

public class TicketLoader {

    public class Ticket{
        @SerializedName("ticket")
        private String value;

        public String getValue(){
            return value;
        }
        public void setValue(String val){
            this.value = val;
        }

        public void invalidate() {
            value = null;
        }

        public boolean isValid(){
            return value != null;
        }
    }

    public interface TicketLoaderListener{
        void onTicketLoaded(Ticket t);
        void onTicketFailed(HttpError error);
    }

    private TicketLoaderListener mListener;
    private boolean loading;
    private Ticket mTicket;

    public boolean isTicketValid(){
        return mTicket != null && mTicket.isValid();
    }

    public Ticket getTicket(){
        return mTicket;
    }

    public boolean isLoading(){
        return loading;
    }

    public void setTicketLoaderListener(TicketLoaderListener l){
        this.mListener = l;
    }

    public void load(){
        String url = ContextProvider.getBaseURL() + "/api/ticket/create";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,listener);
        req.setParser(new GenericModelParser());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                mListener.onTicketFailed(error);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    public void load(int orderTypeGroup, int groupID){
        String url = ContextProvider.getBaseURL() + "/api/ticket/create";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,listener);
        req.setParser(new GenericModelParser());
        if(orderTypeGroup != 0){
            req.addParam("order_type", ""+orderTypeGroup);
            req.addParam("group_id", "" + groupID);
        }
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                mListener.onTicketFailed(error);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                Ticket t = new Ticket();
                JsonObjectMapper.map(data.getModel(),t);
                mTicket = t;
                mListener.onTicketLoaded(t);
            }else{
                mListener.onTicketFailed(null);
            }
        }
    };
}
