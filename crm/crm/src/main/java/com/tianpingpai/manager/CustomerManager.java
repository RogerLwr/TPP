package com.tianpingpai.manager;

import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.utils.SingletonFactory;

public class CustomerManager extends ModelManager<CustomerEvent, CustomerModel> {
	public static CustomerManager getInstance(){
		return SingletonFactory.getInstance(CustomerManager.class);
	}
}
