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

@SuppressWarnings("unused")
@ActionBar(title = "销售统计")
@Layout(id = R.layout.view_controller_statistics)
public class StatisticsViewController extends CrmBaseViewController{

    @OnClick(R.id.department_statistical_button_container)
    private View.OnClickListener departmentOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/app/customer/my/mycount?accessToken="+ UserManager.getInstance().getCurrentUser().getAccessToken());
            i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            getActivity().startActivity(i);
        }
    };

    @OnClick(R.id.mine_statistical_button_container)
    private View.OnClickListener mineOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/app/customer/my/mycount?accessToken="+UserManager.getInstance().getCurrentUser().getAccessToken());
            i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            getActivity().startActivity(i);
        }
    };
}
