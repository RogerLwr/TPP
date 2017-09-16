package com.tianpingpai.manager;

import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.utils.SingletonFactory;

public class OrderManager extends ModelManager<ModelEvent,OrderModel>{
    public static OrderManager getInstance(){
        return SingletonFactory.getInstance(OrderManager.class);
    }
}
