package com.tianpingpai.manager;

import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.utils.SingletonFactory;

public class NoticeManager extends ModelManager<ModelEvent,Model> {
    public static NoticeManager getInstance(){
        return SingletonFactory.getInstance(NoticeManager.class);
    }
}
