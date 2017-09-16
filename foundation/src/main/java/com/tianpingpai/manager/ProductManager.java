package com.tianpingpai.manager;

import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.utils.SingletonFactory;

public class ProductManager extends ModelManager<ModelEvent,ProductModel>{
    public static ProductManager getInstance(){
        return SingletonFactory.getInstance(ProductManager.class);
    }
}
