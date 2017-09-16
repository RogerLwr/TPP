package com.tianpingpai.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.tianpingpai.parser.JSONModelMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model implements Parcelable {
    private HashMap<String, Object> itemMap = new HashMap<>();
    private HashMap<String, List<?>> arrayMap = new HashMap<>();


    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            Model m = new Model();
            try {
                JSONObject json = new JSONObject(in.readString());
                JSONModelMapper.mapObject( json,m);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return m;
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    public String getString(String key) {
        return get(key, String.class);
    }

    public Number getNumber(String key) {
        Object value = get(key);
        if (value != null && value instanceof Number) {
            return (Number) value;
        }
        return null;
    }

    public int getInt(String key) {
        Integer value = get(key, Integer.class);
        return value == null ? 0 : value;
    }

    public double getDouble(String key) {
        Double value = get(key, Double.class);
        if (value == null) {
            Integer intValue = get(key, Integer.class);
            if (intValue != null) {
                return intValue;
            }
            String stringValue = get(key, String.class);
            if (stringValue != null && !TextUtils.isEmpty(stringValue)) {
                try {
                    return Double.parseDouble(stringValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return value == null ? 0 : value;
    }

    public Object get(String key) {
        return itemMap.get(key);
    }

    public boolean getBoolean(String key) {
        Boolean value = get(key, Boolean.class);
        Log.e("xx", value + "");
        if (value != null) {
            return value;
        }
        return false;
    }

    public Model getModel(String key) {
        return get(key, Model.class);
    }

    public <T> T getKeyPath(String key, Class<T> clz) {
        String[] keys = key.split(".");
        T result;
        Model m = this;
        for (int i = 0; i < keys.length - 1; i++) {
            m = m.getModel(keys[i]);
            if (m == null) {
                return null;
            }
        }
        result = m.get(keys[keys.length - 1], clz);
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clz) {
        Object value = itemMap.get(key);
        if(value != null && value.getClass() == clz){
            return (T) value;
        }
        return null;
    }

    public <T> void set(String key, T value) {
        if (value == null) {
            return;//TODO
        }
        itemMap.put(key, value);
    }

    public void setList(String key, List<?> models) {
        arrayMap.put(key, models);
    }

    public <T> List<T> getList(String key, Class<T> clz) {
        return (List<T>) arrayMap.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String k : itemMap.keySet()) {
            sb.append(k);
            Object value = this.itemMap.get(k);
            sb.append(value);
        }
        for (String key : arrayMap.keySet()) {
            List<?> array = arrayMap.get(key);
            if (array != null) {
                sb.append(key);
                sb.append(":");
                sb.append(array);
            }
        }
        return sb.toString();
    }

    public long getLong(String key) {
        long result = 0;
        Long l = get(key, Long.class);
        if (l == null) {
            Integer intValue = get(key, Integer.class);
            if (intValue == null) {
                String stringValue = getString(key);
                if (stringValue != null) {
                    try {
                        result = Integer.parseInt(stringValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                result = intValue.longValue();
            }
        } else {
            result = l;
        }
        return result;
    }

    public String toJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
//        for (String k : itemMap.keySet()) {
//            HashMap<String, Object> itemMap = this.itemMap.get(k);
            for (String key : itemMap.keySet()) {
                sb.append("\"");
                sb.append(key);
                sb.append("\":");
                sb.append(valueString(itemMap.get(key)));
                sb.append(",");
            }
            //TODO remove last?
//        }

        for (String k : arrayMap.keySet()) {
            List<?> array = arrayMap.get(k);
            sb.append("\"");
            sb.append(k);
            sb.append("\":");
            sb.append(valueString(array));
            sb.append(",");
        }

        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String valueString(List<?> array) {
        if(array == null || array.isEmpty()){
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object o : array) {
            sb.append(valueString(o));
            sb.append(",");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    private static String valueString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Model) {
            Model m = (Model) obj;
            return m.toJsonString();
        }

        if (obj instanceof String) {
            String s = (String) obj;
            return JSONObject.quote(s);
        }
        return obj.toString();
    }

    public Map<String, Object> getAll() {
        return itemMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toJsonString());
    }

    public static void copy(Model src,Model dest,String... names){
        for(String name:names){
            dest.set(name,src.get(name));
        }
    }
}
