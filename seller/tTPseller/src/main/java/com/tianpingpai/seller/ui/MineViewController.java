package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.adapter.ActionAdapter;
import com.tianpingpai.seller.adapter.MineAction;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.ui.BaseFragment;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.umeng.fb.FeedbackAgent;

@SuppressWarnings("unused")
@ActionBar(hidden = true)
@Layout(id = R.layout.ui_mine)
@Statistics(page = "我的")
public class MineViewController extends BaseViewController {

    @Binding(id = R.id.username_text_view, format = "{{display_name}}")
    private TextView usernameTextView;
    @Binding(id = R.id.store_name_text_view, format = "{{sale_name}}")
    private TextView storeNameTextView;
    @Binding(id = R.id.shop_status_text_view)
    private TextView shopStatusTextView;
    @Binding(id = R.id.buyer_orders_container)
    private View buyerOrdersContainer;
    @Binding(id = R.id.group_buy_container)
    private View groupBuyContainer;
    @Binding(id = R.id.group_buy_line)
    private View groupBuyLine;
    @Binding(id = R.id.coupons_container)
    private View couponsContainer;
    @Binding(id = R.id.address_container)
    private View addressContainer;

    private ActionAdapter actionAdapter = new ActionAdapter();

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView gridView = (ListView) rootView.findViewById(R.id.action_grid_view);
        gridView.setAdapter(actionAdapter);
        gridView.setOnItemClickListener(onItemClickListener);

        actionAdapter.setContext(getActivity());

        UserManager.getInstance().registerListener(userStateListener);
        fillInfo();
        loadUserInfo();
        updateBuyerOrders();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userStateListener);
    }

    private void updateBuyerOrders() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            groupBuyLine.setVisibility(View.VISIBLE);
            if (user.getGrade() == UserModel.GRADE_3) {
                buyerOrdersContainer.setVisibility(View.VISIBLE);
                addressContainer.setVisibility(View.VISIBLE);
                groupBuyContainer.setVisibility(View.VISIBLE);
                couponsContainer.setVisibility(View.VISIBLE);
            } else {
                buyerOrdersContainer.setVisibility(View.GONE);
                addressContainer.setVisibility(View.GONE);
//                groupBuyContainer.setVisibility(View.GONE);
                groupBuyLine.setVisibility(View.GONE);
                couponsContainer.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.settings_button)
    private View.OnClickListener settingsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SettingsViewController.class);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.messages_button)
    private View.OnClickListener msgButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, MessageListViewController.class);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.buyer_orders_container)
    private View.OnClickListener buyerOrdersListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!UserManager.getInstance().isLoggedIn()) {
                Toast.makeText(ContextProvider.getContext(), "请登录后重试！", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            String url = URLApi.getWebBaseUrl() + "/saler/upstream/order/list?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken();
            intent.putExtra(WebViewController.KEY_URL, url);
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.group_buy_container)
    private View.OnClickListener groupBuyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!UserManager.getInstance().isLoggedIn()) {
                Toast.makeText(ContextProvider.getContext(), "请登录后重试！", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            String url = URLApi.getWebBaseUrl() + "/groupbuy/upstream/orderlist?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken();
            intent.putExtra(WebViewController.KEY_URL, url);
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.coupons_button)
    private View.OnClickListener couponsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!UserManager.getInstance().isLoggedIn()) {
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, CouponListViewController.class);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.address_container)
    private View.OnClickListener addressListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!UserManager.getInstance().isLoggedIn()) {
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, AddressListViewController.class);
            getActivity().startActivity(intent);
        }
    };


    @OnClick(R.id.service_line_button)
    private View.OnClickListener hotLineButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String serviceLine = getActivity().getResources().getString(R.string.ownmain_tel);
            Intent mIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + serviceLine));
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(mIntent);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MineAction action = actionAdapter.getItem(position);
            Class c = action.target;
            Intent intent;

            if (c == null) {
                FeedbackAgent agent = new FeedbackAgent(getActivity());
                agent.startFeedbackActivity();
                return;
            }

            if (BaseFragment.class.isAssignableFrom(c) || BaseViewController.class.isAssignableFrom(c)) {
                intent = actionAdapter.getIntent(position);
                if (UserManager.getInstance().isLoggedIn()) {
                    if (c == WebViewController.class) {
                        if (actionAdapter.getItem(position).icon == R.drawable.ic_151103_agreement) {//TODO
                            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_NORMAL);
                        } else {
                            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
                        }
                    }
                }
            } else {
                if (UserManager.getInstance().isLoggedIn()) {
                    intent = new Intent(getActivity(), c);
                } else {
                    intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                }
            }
            getActivity().startActivity(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setStatusBarColor(getActivity().getResources().getColor(R.color.green));
        loadCount();
        fillInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatusBarColor(Color.WHITE);
    }

    private void fillInfo() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            usernameTextView.setText("点击登录");
            storeNameTextView.setText("");
        }
    }

    private ModelStatusListener<UserEvent, UserModel> userStateListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            fillInfo();
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user != null) {
                loadUserInfo();
            }
            updateBuyerOrders();
        }
    };

    @OnClick(R.id.user_info_container)
    private View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!UserManager.getInstance().isLoggedIn()) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                getActivity().startActivity(intent);
            }
        }
    };

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
                actionAdapter.setUnreadEnvelopeCount(data.getModel());
            } else {
                ResultHandler.handleError(data, MineViewController.this);
            }
        }
    };

    private void loadUserInfo() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            String url = ContextProvider.getBaseURL() + "/api/user/getSaleUserDetailInfo.json";
            HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, userInfoListener);
            req.addParam("phone", UserManager.getInstance().getCurrentUser().getPhone());
            req.setParser(new GenericModelParser());
            VolleyDispatcher.getInstance().dispatch(req);
        }
    }

    private HttpRequest.ResultListener<ModelResult<Model>> userInfoListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            Model m = data.getModel();
            if (data.isSuccess()) {
                getBinder().bindData(m);

                switch (m.getInt("rest")){
                    case CommonUtil.ShopStatus.shopBusiness:
                        shopStatusTextView.setText("营业中");
                        break;
                    case CommonUtil.ShopStatus.shopRest:
                        shopStatusTextView.setText("暂停营业");
                        break;
                }

                int grade = m.getInt("grade");
                if (grade == UserModel.GRADE_3) {
                    actionAdapter.addMyScore();
                }
            }
            hideLoading();
        }
    };
}
