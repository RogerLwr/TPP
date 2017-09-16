package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.parser.ParserIntResult;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.widget.BadgeView;

@SuppressWarnings("unused")
@Layout(id = R.layout.fragment_main_work)
public class WorkViewController extends BaseViewController {

    @Binding(id = R.id.todo_list_container)
    private View todoListContainer;

    @Binding(id = R.id.blog_badge_view)
    private BadgeView badgeView;
    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        View actionBar = setActionBarLayout(R.layout.ab_title_green);
        Toolbar toolbar = (Toolbar)actionBar.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        setTitle("工作");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("onResume","yunxingle");
        loadBlogNumber();
    }



    @OnClick(R.id.sign_in_container)
    private View.OnClickListener signInOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT
                    , SignInListViewController.class);
            getActivity().startActivity(i);

        }
    };
    @OnClick(R.id.approve_buyer_container)
    private View.OnClickListener buyerApproveOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/customer/approvePage?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken());
            getActivity().startActivity(i);

        }
    };
    @OnClick(R.id.register_seller_container)
    private View.OnClickListener registerSellerOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
//            i.putExtra(ContainerActivity.KEY_CONTENT, RegisterViewController.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,RegisterSellerViewController.class);
            getActivity().startActivity(i);

        }
    };
    @OnClick(R.id.my_statistics_container)
    private View.OnClickListener myStatisticsOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            UserModel um = UserManager.getInstance().getCurrentUser();
            if(null==um){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
//            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/app/customer/my/mycount?accessToken="+UserManager.getInstance().getCurrentUser().getAccessToken());
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/app/customer/statistic?accessToken="+um.getAccessToken()+"&marketer_id="+um.getMarketerId());
            i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            getActivity().startActivity(i);

            /*

            UserModel um = UserManager.getInstance().getCurrentUser();
            if(null==um){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            Log.e("level", "-------" + um.getLevel());
            if(0==um.getLevel()){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            Intent i = new Intent(getActivity(),ContainerActivity.class);
            if(11==um.getLevel()) {
                i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);

                i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL()+"/crm/app/customer/statistic?accessToken=" +
                        um.getAccessToken()+"&marketer_id="+um.getMarketerId()+"&dateType=1");
                i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            }else{
                i.putExtra(ContainerActivity.KEY_CONTENT, MyStatisticViewController.class);
            }
            getActivity().startActivity(i);

            */

        }
    };
    @OnClick(R.id.todo_list_container)
    private View.OnClickListener backLogOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(WebViewController.KEY_URL,ContextProvider.getBaseURL()+"/crm/app/schedule/distribution?accessToken="+UserManager.getInstance().getCurrentUser().getAccessToken());
            i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            getActivity().startActivity(i);

        }
    };
    @OnClick(R.id.customer_claim_container)
    private View.OnClickListener customerClaimOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,CustomerClaimViewController.class);
            getActivity().startActivity(i);

        }
    };
    @OnClick(R.id.add_buyer_container)
    private View.OnClickListener addBuyerOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,AddCustomerBuyerViewController.class);
            getActivity().startActivity(i);

        }
    };
    @OnClick(R.id.add_seller_container)
    private View.OnClickListener addSellerOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,AddCustomerSellerViewController.class);
            getActivity().startActivity(i);

        }
    };

    //上传合同
    @OnClick(R.id.update_contract_container)
    private View.OnClickListener upDateContractOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,ContractManagerViewController.class);
            getActivity().startActivity(i);

        }
    };

    //招商统计
    @OnClick(R.id.merchants_statistical_container)
    View.OnClickListener merchants_statistical_container = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            UserModel um = UserManager.getInstance().getCurrentUser();
            if(null==um){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            Log.e("level", "-------" + um.getLevel());
            if(0==um.getLevel()){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            Intent i = new Intent(getActivity(),ContainerActivity.class);
            if(11==um.getLevel()) {
                i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);

                i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL()+"/crm/app/customer/purchase?accessToken=" +
                        um.getAccessToken()+"&marketer_id="+um.getMarketerId()+"&dateType=1");
                i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTIONBAR_STYLE_HIDDEN);
            }else{
                i.putExtra(ContainerActivity.KEY_CONTENT, MerchantsStatisticalViewController.class);
            }
            getActivity().startActivity(i);
        }
    };

    private void loadBlogNumber(){
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String url = URLApi.getBaseUrl()+"/crm/marketer/schedule_num";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,blogListener);
        req.setParser(new ParserIntResult());
        req.addParam("accessToken",user.getAccessToken());
        VolleyDispatcher.getInstance().dispatch(req);
        Log.e("url",req.getUrl());
    }

    private HttpRequest.ResultListener<ModelResult<Model>> blogListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                int number = data.getModel().getInt("result");
                Log.e("result===",number+"");
                if(number>0){
                    badgeView.setBadge(number);
                }
            }
        }
    };

}
