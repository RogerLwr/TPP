package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.brother.tpp.activity.StatisticAct;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.OrderStatusCount;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.buyer.parser.ListParserNoPageItems;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.widget.BadgeView;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("unused")
@Statistics(page = "我的")
@ActionBar(hidden = true)
@Layout(id = R.layout.ui_mine)
public class MineViewController extends BaseViewController {
    @Binding(id = R.id.name_text_view)
    private TextView nameTextView;
    @Binding(id = R.id.not_payed_badge_view)
    private BadgeView notPayedBadgeView;
    @Binding(id = R.id.pending_orders_badge_view)
    private BadgeView notSentBadgeView;
    @Binding(id = R.id.send_order_badge_view)
    private BadgeView sentBadgeView;
    @Binding(id = R.id.not_commented_badge_view)
    private BadgeView notCommentedBadgeView;
    @Binding(id = R.id.dot_view)
    private View dotView;
    @Binding(id = R.id.points_text_view)
    private TextView pointsTextView;

    private ModelStatusListener<UserEvent, UserModel> userStatusListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            updateUserInfo();
        }
    };

    private ModelStatusListener<ModelEvent, OrderModel> ordersListener = new ModelStatusListener<ModelEvent, OrderModel>() {
        @Override
        public void onModelEvent(ModelEvent event, OrderModel model) {
            loadOrdersCount();
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        OrderManager.getInstance().registerListener(ordersListener);
        UserManager.getInstance().registerListener(userStatusListener);
        updateUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userStatusListener);
        OrderManager.getInstance().unregisterListener(ordersListener);
    }

    private ResultListener<ArrayList<OrderStatusCount>> listener = new ResultListener<ArrayList<OrderStatusCount>>() {
        @Override
        public void onResult(HttpRequest<ArrayList<OrderStatusCount>> request,
                             ArrayList<OrderStatusCount> data) {
            if (data == null) {
                return;
            }
            int notPayed = 0;
            int notSent = 0;
            int notDeliver = 0;
            int waitAccept = 0;
            int completeWaitComment = 0;
            int couponNumber = 0;
            for (int j = 0; j < data.size(); j++) {
                OrderStatusCount item = data.get(j);

                switch (item.getStatus()) {
                    case CommonUtil.NOT_PAY_MONEY:
                        notPayed = item.getCount();
                        notPayed = 112;//TODO
                        break;
                    case CommonUtil.NOT_ACCEPT_SELLER:
                        notSent = item.getCount();
                        break;
                    case CommonUtil.NOT_DELEVIER_GOODS:
                        notDeliver = item.getCount();
                        break;
                    case CommonUtil.WAIT_ACCEPT_GOODS:
                        waitAccept = item.getCount();
                        TLog.e("xx", "waitAccept="+waitAccept);
                        break;
                    case CommonUtil.COMPLETE_WAIT_COMMENT:
                        completeWaitComment = item.getCount();
                        break;
                    case 100:
                        couponNumber = item.getCount();
                        break;
                    case 200:
                        pointsTextView.setText(String.format(Locale.CHINESE,"%d 积分",item.getCount()));
                        break;
                    default:
                        break;
                }
            }
            TLog.e("xx", "waitAccept="+waitAccept);
            notPayedBadgeView.setBadge(notPayed);
            notSentBadgeView.setBadge(notSent+notDeliver);
            sentBadgeView.setBadge(waitAccept);
            notCommentedBadgeView.setBadge(completeWaitComment);
            if(couponNumber > 0){
                dotView.setVisibility(View.VISIBLE);
            }else{
                dotView.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void loadOrdersCount() {
        String url = URLApi.Mine.getOrderCounts();
        HttpRequest<ArrayList<OrderStatusCount>> req = new HttpRequest<>(url, listener);
        ListParserNoPageItems<OrderStatusCount> parser = new ListParserNoPageItems<>(OrderStatusCount.class);
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrdersCount();
    }

    private void updateUserInfo() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            nameTextView.setText(user.getNickName());
        }
    }

    private void openPage(Class c) {
        if(!checkLogin()){
            return;
        }
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.KEY_CONTENT, c);
        getActivity().startActivity(intent);
    }

    private void openOrders(String status) {
        if(!checkLogin()){
            return;
        }
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.KEY_CONTENT, OrdersViewController.class);
        intent.putExtra(OrdersViewController.KEY_ORDER_FORM, status);
        getActivity().startActivity(intent);
    }

    private boolean checkLogin(){
        if(!UserManager.getInstance().isLoggedIn()){
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,LoginViewController.class);
            getActivity().startActivity(intent);
            return false;
        }
        return true;
    }

    @OnClick(R.id.user_info_container)
    private OnClickListener userInfoButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!checkLogin()){
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, UpdateUsernameViewController.class);
            intent.putExtra(UpdateUsernameViewController.KEY_USER_NAME,UserManager.getInstance().getCurrentUser().getNickName());
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.messages_button)
    private OnClickListener messagesButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openPage(MessageListViewController.class);
        }
    };

    @OnClick(R.id.settings_button)
    private OnClickListener settingButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openPage(SettingsViewController.class);
//            Intent intent = new Intent(getActivity(),ContainerActivity.class);
//            intent.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
//            intent.putExtra(WebViewController.KEY_URL,"https://test2.tianpingpai.com");
//            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.not_payed_orders_button)
    private OnClickListener notPayedOrdersButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openOrders(CommonUtil.NOT_PAY_MONEY + "");
        }
    };

    @OnClick(R.id.pending_orders_button)
    private OnClickListener pendingOrdersButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openOrders("1," + CommonUtil.NOT_DELEVIER_GOODS);
        }
    };

    @OnClick(R.id.send_orders_button)
    private OnClickListener sendOrdersButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openOrders(CommonUtil.WAIT_ACCEPT_GOODS + "");
        }
    };

    @OnClick(R.id.not_commented_orders_button)
    private OnClickListener notCommentedOrdersButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openOrders(CommonUtil.COMPLETE_WAIT_COMMENT + "");
        }
    };

    @OnClick(R.id.all_orders_button)
    private OnClickListener allOrdersButtonListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            openPage(OrdersViewController.class);
        }
    };

    @OnClick(R.id.addresses_button)
    private OnClickListener addressesButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openPage(AddressListViewController.class);
        }
    };

    @OnClick(R.id.collections_button)
    private OnClickListener collectionsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openPage(CollectionViewController.class);
        }
    };

    @OnClick(R.id.points_store_button)
    private OnClickListener pointsStoreButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if(user == null){
                return;
            }
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
            intent.putExtra(WebViewController.KEY_URL,URLApi.getH5Base() + "/app/score/integral/shop?skip=0&accessToken=" + user.getAccessToken() );
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_NORMAL);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.check_points_button)
    private OnClickListener checkPointsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if(user == null){
                return;
            }
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
            String url;
            url = URLApi.getH5Base() + "/app/score/my/integral?skip=0&accessToken=" + user.getAccessToken();
            intent.putExtra(WebViewController.KEY_URL,url);
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_NORMAL);
            intent.putExtra(WebViewController.KEY_URL,URLApi.getH5Base() + "/app/score/my/integral?skip=2&accessToken=" + user.getAccessToken() );
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.points_orders_button)
    private OnClickListener pointsOrdersButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if(user == null){
                return;
            }
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_NORMAL);
            intent.putExtra(WebViewController.KEY_URL,URLApi.getH5Base() + "/app/score/exchange/list?skip=3&accessToken=" + user.getAccessToken() );
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.statistics_button)
    private OnClickListener statisticsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!checkLogin()){
                return;
            }
            Intent intent = new Intent(getActivity(), StatisticAct.class);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.invitation_code_button)
    private OnClickListener invitationCodeButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!checkLogin()){
                return;
            }
            String url = URLApi.getH5Base() + "/apply/buyer/invitation?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken();
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            intent.putExtra(WebViewController.KEY_URL, url);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.bills_button)
    private OnClickListener billsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openPage(JournalViewController.class);
        }
    };

    @OnClick(R.id.coupons_button)
    private OnClickListener couponsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            openPage(CouponListViewController.class);
        }
    };

    @OnClick(R.id.service_line_button)
    private OnClickListener serviceLineButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String serviceNumber = getActivity().getString(R.string.service_line);
            Intent telIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                    + serviceNumber));
            telIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(telIntent);
        }
    };
}
