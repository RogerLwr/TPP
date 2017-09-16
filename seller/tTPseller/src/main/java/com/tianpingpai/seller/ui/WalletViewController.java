package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.manager.BalanceManager;
import com.tianpingpai.seller.manager.BalanceModel;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

@SuppressWarnings("unused")
@Statistics(page = "钱包")
@Layout(id = R.layout.ui_wallet)
public class WalletViewController extends BaseViewController{

    @Binding(id = R.id.balance_text_view , format = "{{balance|money}}")
    private TextView balanceTextView;
    @Binding(id = R.id.badge_view)
    private View badgeView;

    private Model model;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        View view = setActionBarLayout(R.layout.ab_title_green);
        Toolbar toolbar = (Toolbar) view.findViewById(com.tianpingpai.foundation.R.id.toolbar);
        if(toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back_white);
        }
        loadAccountData();
        BalanceManager.getInstance().registerListener(balanceListener);
        showContent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BalanceManager.getInstance().unregisterListener(balanceListener);
    }

    private ModelStatusListener<ModelEvent, BalanceModel> balanceListener = new ModelStatusListener<ModelEvent, BalanceModel>() {
        @Override
        public void onModelEvent(ModelEvent event, BalanceModel model) {
            switch (event) {
                case OnModelUpdate:
                    loadAccountData();
                    break;
            }
        }
    };


    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        loadCount();
    }

    private void loadCount() {
        String url = ContextProvider.getBaseURL() + "/api/saler/bonus/count";
        HttpRequest<ModelResult<Integer>> req = new HttpRequest<>(url, countListener);
        req.setParser(new ModelParser<>(Integer.class));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Integer>> countListener = new HttpRequest.ResultListener<ModelResult<Integer>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Integer>> request, ModelResult<Integer> data) {
            if (data.isSuccess()) {
                updateCount(data.getModel());
            } else {
                ResultHandler.handleError(data, WalletViewController.this);
            }
        }
    };

    private void updateCount(int count) {
        if (count > 0) {
            badgeView.setVisibility(View.VISIBLE);
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }

    private void loadAccountData() {
        String url = ContextProvider.getBaseURL() + "/api/user/account";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideLoading();
            if (data.isSuccess()) {
                showContent();
                model = data.getModel();
                getBinder().bindData(model);
            } else {
                ResultHandler.handleError(data, WalletViewController.this);
            }
        }
    };

    @OnClick(R.id.draw_cash_button)
    private View.OnClickListener drawCashButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (model != null) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, DrawCashViewController.class);
                intent.putExtra(DrawCashViewController.KEY_MONEY, model.getDouble("balance"));
                getActivity().startActivity(intent);
            }
        }
    };

    @OnClick(R.id.check_balance_button)
    View.OnClickListener checkBalanceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(getActivity(), ContainerActivity.class);
//            intent.putExtra(ContainerActivity.KEY_CONTENT, MyJournalViewController.class);
//            getActivity().startActivity(intent);
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            if (UserManager.getInstance().isLoggedIn()) {
                intent.putExtra(ContainerActivity.KEY_CONTENT, BillsViewController.class);
            } else {
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
            }
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.check_envelope_button)
    private View.OnClickListener checkEnvelopesButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            if (UserManager.getInstance().isLoggedIn()) {
                intent.putExtra(ContainerActivity.KEY_CONTENT, EnvelopeListViewController.class);
            } else {
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
            }
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.bill_button)
    private View.OnClickListener billButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            if (UserManager.getInstance().isLoggedIn()) {
//                intent.putExtra(ContainerActivity.KEY_CONTENT, BillsViewController.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                String url = URLApi.getWebBaseUrl() + "/apply/saler/bill?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken();
                intent.putExtra(WebViewController.KEY_URL,url);
            } else {
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
            }
            getActivity().startActivity(intent);
        }
    };
}
