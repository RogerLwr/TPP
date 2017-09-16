package com.tianpingpai.crm.ui;

import com.tianpingpai.crm.CrmApplication;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.ui.BaseViewController;

public class CrmBaseViewController extends BaseViewController {

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        CrmApplication.putActivity(getActivity());

        com.tianpingpai.http.HttpManager.getInstance().registerListener(baseListener);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        com.tianpingpai.http.HttpManager.getInstance().unregisterListener(baseListener);
        CrmApplication.removeActivity(getActivity());
    }
    com.tianpingpai.core.ModelStatusListener<com.tianpingpai.http.HttpEvent, com.tianpingpai.parser.HttpResult<?>> baseListener = new com.tianpingpai.core.ModelStatusListener<com.tianpingpai.http.HttpEvent, com.tianpingpai.parser.HttpResult<?>>() {
        @Override
        public void onModelEvent(HttpEvent event, HttpResult<?> model) {
            switch (event){
                case accessTokenExpired:
                    UserManager.getInstance().loginExpired(getActivity());
                    break;
            }
        }
    };
}
