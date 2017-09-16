package com.tianpingpai.utils;

import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.Model;

public class JsonModelManager extends ModelManager<ModelEvent,Model> {
    public static JsonModelManager getInstance(){
        return SingletonFactory.getInstance(JsonModelManager.class);
    }
}
