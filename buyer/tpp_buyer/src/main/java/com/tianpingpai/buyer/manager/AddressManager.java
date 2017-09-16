package com.tianpingpai.buyer.manager;

import com.tianpingpai.buyer.model.AddressModel;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.utils.SingletonFactory;

public class AddressManager extends ModelManager<ModelEvent,AddressModel> {

    public static AddressManager getInstance(){
        return SingletonFactory.getInstance(AddressManager.class);
    }
}
