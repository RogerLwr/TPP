package com.tianpingpai.http;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest<T> {
    public long getMinLoadingTime() {
        return minLoadingTime;
    }

    public interface ErrorListener {
        void onError(HttpRequest<?> request, HttpError eror);
    }

    public interface ResultListener<T> {
        void onResult(HttpRequest<T> request, T data);
    }

    public interface Parser<T, I> {
        T parse(I i) throws JSONException;
    }

    public static final int GET = 0;
    public static final int POST = 1;

    private String mUrl;
    private int mMethod = GET;

    public void setMinLoadingTime(long minLoadingTime) {
        this.minLoadingTime = minLoadingTime;
    }

    private long minLoadingTime;
    private Parser<?, ?> mParser;
    private ResultListener<T> mListener;
    private ErrorListener mErrorListener;
    public ErrorListener getErrorListener() {
        return mErrorListener;
    }

    public void setErrorListener(ErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

    private HashMap<String, String> mParams = new HashMap<>();
    private HashMap<String, File> mFilesMap = new HashMap<>();
    private HashMap<Class<?>, Object> attachments = new HashMap<>();
    private HashMap<String, String> headers;
    private HashMap<String, String> responseHeaders;

    public void setResponseHeaders(HashMap<String, String> headers){
        this.responseHeaders = headers;
    }

    public HashMap<String, String> getResponseHeaders(){
        return responseHeaders;
    }

    public HttpRequest(String url, ResultListener<T> listener) {
        this.mUrl = url;
        this.mListener = listener;
    }

    public void setMethod(int method) {
        this.mMethod = method;
    }

    public int getMethod() {
        return mMethod;
    }

    public String getUrl() {
        StringBuilder sb = new StringBuilder(mUrl);
        if (!mUrl.contains("?")) {
            sb.append("?");
        }
        if (getMethod() == HttpRequest.GET) {
            if (getParams() != null) {
                for (String key : getParams().keySet()) {
                    sb.append("&").append(key).append("=")
                            .append(mParams.get(key));
                }
            }
            return sb.toString();
        }
        return mUrl;
    }

    @SuppressWarnings("rawtypes")
    public void setParser(Parser p) {
        this.mParser = p;
    }

    public Parser<?, ?> getParser() {
        return mParser;
    }

    public ResultListener<T> getListener() {
        return mListener;
    }

    public void addParam(String key, String value) {
        if (value != null) {
            mParams.put(key, value);
        }
    }

    public void addParam(String key, String value, boolean encode) {
        try {
            mParams.put(key, URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void addHeader(String key,String value){
        if(headers == null){
            headers = new HashMap<>();
        }
        headers.put(key,value);
    }


    public void addFile(String key, File file) {
        mFilesMap.put(key, file);
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public Map<String, File> getFiles() {
        return mFilesMap;
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

    public <E> void setAttachment(E a) {
        attachments.put(a.getClass(), a);
    }

    @SuppressWarnings("unchecked")
    public <E> E getAttachment(Class<E> c) {
        return (E) attachments.get(c);
    }

    public boolean isCanceled() {
        return false;
    }
}
