package com.tianpingpai.parser;

import com.tianpingpai.model.Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONModelMapper {
    private static void mapValue(String key, Object obj, Model m) {
        if (obj instanceof JSONObject) {
            JSONObject childObj = (JSONObject) obj;
            Model childModel = new Model();
            m.set(key, childModel);
            mapObject(childObj, childModel);
        } else if (obj instanceof JSONArray) {
            JSONArray childArray = (JSONArray) obj;
            ArrayList<Object> models = new ArrayList<>();
            mapArray(childArray, models);
            m.setList(key, models);
        } else if (obj instanceof String) {
            m.set(key, obj);
        } else {
            m.set(key, obj);
        }
    }

    public  static void mapObject(JSONObject jobj, Model m) {
        Iterator<String> keyIterator = jobj.keys();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            if (!jobj.isNull(key)) {
                Object obj = jobj.opt(key);
                mapValue(key, obj, m);
            }
        }
    }

    public static void mapArray(JSONArray jsonArray, List<Object> models) {
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            Object obj = jsonArray.opt(i);
            if (obj instanceof JSONObject) {
                JSONObject childObj = (JSONObject) obj;
                Model m = new Model();
                mapObject(childObj, m);
                models.add(m);
            } else {
                models.add(obj);
            }
        }
    }
}
