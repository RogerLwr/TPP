package com.tianpingpai.seller.manager;

import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.utils.SingletonFactory;

public class BalanceManager extends ModelManager<ModelEvent,BalanceModel> {
    public static BalanceManager getInstance(){
        return SingletonFactory.getInstance(BalanceManager.class);
    }
}
