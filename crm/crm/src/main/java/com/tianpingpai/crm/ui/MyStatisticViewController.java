package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.view.View;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@ActionBar(title = "我的统计")
@Layout(id = R.layout.view_controller_merchants_statistical)
public class MyStatisticViewController extends CrmBaseViewController {

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        UserModel userModel = UserManager.getInstance().getCurrentUser();
        if (userModel != null) {
//            loadData(userModel);
        } else {
            UserManager.getInstance().loginExpired(getActivity());
        }
    }

    //进入部门统计
    @OnClick(R.id.department_statistical_button_container)
    private View.OnClickListener departmentStatisticsButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            UserModel um = UserManager.getInstance().getCurrentUser();
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL()+"/crm/app/customer/statistic_mydepartment?accessToken=" +
                    um.getAccessToken()+"&user_level="+um.getLevel()+"&marketer_id="+um.getMarketerId()+"&dateType=1");
            i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            getActivity().startActivity(i);
        }
    };

    //进入个人统计
    @OnClick(R.id.mine_statistical_button_container)
    private View.OnClickListener mineStatisticsButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            UserModel um = UserManager.getInstance().getCurrentUser();
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL()+"/crm/app/customer/statistic?accessToken=" +
                    um.getAccessToken()+"&marketer_id="+um.getMarketerId()+"&dateType=1");
            i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            getActivity().startActivity(i);
        }
    };
}
