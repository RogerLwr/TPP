package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.seller.SellerUrlInterceptor;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.adapter.HomeActionAdapter;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.AutoResizeActivity;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.animation.ExpandAnimation;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.BadgeView;
import com.tianpingpai.widget.CirclePageIndicator;
import com.tianpingpai.widget.NotificationControl;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.List;

@SuppressWarnings("unused")
@Statistics(page = "首页")
@ActionBar(hidden = true)
@Layout(id = R.layout.ui_home)
public class HomeViewController extends BaseViewController {

    private static final int GADGET_TYPE_BANNER = 11;
    private static final int GADGET_TYPE_NOTIFICATION = 12;

    private MainViewController mainViewController;
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private HomeActionAdapter adapter = new HomeActionAdapter();
    private MyAdapter bannerAdapter = new MyAdapter();

    @Binding(id = R.id.banner_container)
    private View bannerContainer;
    @Binding(id = R.id.content_scroll_view)
    private ScrollView contentScrollView;
    @Binding(id = R.id.income_text_view, format = "{{sales | money}}")
    private TextView incomeTextView;
    @Binding(id = R.id.pending_orders_badge_view)
    private BadgeView pendingOrdersBadgeView;
    @Binding(id = R.id.not_received_orders_badge_view)
    private BadgeView notReceivedOrdersBadgeView;
    @Binding(id = R.id.purchase_not_received_badge_view)
    private BadgeView purchaseNotReceivedOrdersBadgeView;
    @Binding(id = R.id.purchase_not_commented_badge_view)
    private BadgeView purchaseNotCommentedOrdersBadgeView;

    @Binding(id = R.id.divider_view)
    private View dividerView;
    @Binding(id = R.id.all_orders_button)
    private View allOrderButton;
    @Binding(id = R.id.purchase_not_received_orders_button)
    private View purchaseNotReceivedButton;
    @Binding(id = R.id.purchase_not_commented_orders_button)
    private View purchaseNotCommentedButton;
//    @Binding(id = R.id.notification_container)
//    private FrameLayout notificationContainer;

    private NotificationControl notificationControl = new NotificationControl();

    private List<Model> banners;

    private ModelStatusListener<ModelEvent, OrderModel> orderStatusListener = new ModelStatusListener<ModelEvent, OrderModel>() {
        @Override
        public void onModelEvent(ModelEvent event, OrderModel model) {
            switch (event) {
                case OnModelUpdate:
                    refreshLayoutControl.triggerRefresh();
                    break;
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData();
            loadGadgets();
        }
    };

    private Handler handler = new Handler();

    private Runnable startRun = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
                // 根据包名卸载旧的app
                PackageManager packageManager;
                packageManager = getActivity().getPackageManager();
                String pkgName = "com.brother.tppseller";
                Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
                if (intent != null) {
                    final ActionSheetDialog dialog = new ActionSheetDialog();
                    dialog.setActionSheet(getActionSheet(true));
                    dialog.setTitle("是否卸载旧的商家版吗？");
                    dialog.setPositiveButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:com.brother.tppseller"));
                            getActivity().startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userStatusListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event) {
                case Login:
                    adapter.reset();
                    refreshLayoutControl.triggerRefresh();
                    break;
            }
        }
    };

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    protected void onConfigureView(final View rootView) {
        super.onConfigureView(rootView);
        CirclePageIndicator pageIndicator = (CirclePageIndicator) rootView.findViewById(R.id.page_indicator);
        ViewPager bannerViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        pageIndicator.setViewPager(bannerViewPager);
        configureBannerType(rootView);
        pageIndicator.setOnPageItemClickListener(new CirclePageIndicator.OnPageItemClickListener() {
            @Override
            public void onPageItemClicked(int position) {
                Log.e("xx", "ssss" + position);

                Model m = banners.get(position);
                String url = m.getString("href");
                Log.e("xx", "url:" + url);
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                    intent.putExtra(WebViewController.KEY_URL, url);
                    getActivity().startActivity(intent);
                }
            }
        });

        GridView gridView = (GridView) rootView.findViewById(R.id.action_grid_view);
        adapter.reset();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(actionItemClickListener);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);

        refreshLayoutControl.triggerRefreshDelayed();
        OrderManager.getInstance().registerListener(orderStatusListener);
        UserManager.getInstance().registerListener(userStatusListener);
        handler.postDelayed(startRun, 500);

        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null && user.getGrade() == UserModel.GRADE_1) {
            dividerView.setVisibility(View.GONE);
            purchaseNotReceivedButton.setVisibility(View.GONE);
            purchaseNotCommentedButton.setVisibility(View.GONE);
        } else {
            dividerView.setVisibility(View.VISIBLE);
            allOrderButton.setVisibility(View.GONE);
        }

        notificationControl.setActivity(getActivity());
        notificationControl.createView(getActivity().getLayoutInflater(), null);
//        notificationContainer.addView(notificationControl.getView(),
//                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        notificationControl.setTitle("");
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        OrderManager.getInstance().unregisterListener(orderStatusListener);
        UserManager.getInstance().unregisterListener(userStatusListener);
    }

    private void configureBannerType(final View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(bannerAdapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator) view
                .findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager);
        pageIndicator.setSelectedColor(getActivity().getResources().getColor(R.color.green));
        pageIndicator.setUnselectedColor(Color.WHITE);

        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int listViewWidth = getView().getWidth();
                int height = (int) (420.0 / 750 * listViewWidth);
                Log.e("ww", "w:" + listViewWidth + ",h:" + height);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        listViewWidth, height);
                lp.width = listViewWidth;
                lp.height = height;
                view.findViewById(R.id.banner_container).setLayoutParams(lp);
            }
        });
    }

    private void loadData() {
        String url = ContextProvider.getBaseURL() + "/api/saler/dashboard/order/count";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.setParser(new GenericModelParser());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            if (data.isSuccess()) {
                getBinder().bindData(data.getModel());
                String number = data.getModel().getInt("unconfirmed") + "";//TODO
                pendingOrdersBadgeView.setText(number);
                notReceivedOrdersBadgeView.setText(data.getModel().getInt("delivered") + "");
                purchaseNotReceivedOrdersBadgeView.setText(data.getModel().getInt("unreceived") + "");
                purchaseNotCommentedOrdersBadgeView.setText(data.getModel().getInt("uncomment") + "");
            }
        }
    };

    private HttpRequest.ResultListener<ModelResult<Model>> gadgetsListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideLoading();
            if (!data.isSuccess()) {
                return;
            }
            List<Model> gadgets = data.getModel().getList("gadgets", Model.class);
            List<Model> notifications = null;
            if (gadgets != null) {
                for (Model m : gadgets) {
                    int type = m.getInt("type");
                    if (type == GADGET_TYPE_BANNER) {
                        banners = m.getList("content", Model.class);
                        bannerAdapter.notifyDataSetChanged();
                    }
                    if (type == GADGET_TYPE_NOTIFICATION) {
                        notifications = m.getList("content", Model.class);
                    }
                }
                if (banners != null && !banners.isEmpty()) {
                    bannerContainer.setVisibility(View.VISIBLE);
                } else {
                    bannerContainer.setVisibility(View.GONE);
                }
                if (notifications != null && !notifications.isEmpty()) {
                    final StringBuilder content = new StringBuilder();
                    String title = "";
                    String url = null;
                    for (Model m : notifications) {
                        title = m.getString("title");
                        if (m.getModel("data") != null) {
                            content.append(m.getModel("data").getString("content"));
                            content.append("      ");
                        }
                        url = m.getString("href");
                    }
                    final String urlString = url;
                    final String titleNotification = title;
                    final String finalTitle = title;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            contentScrollView.scrollTo(0, 0);
                            TextView view = (TextView) getView().findViewById(R.id.notification_text_view);
                            view.setText(finalTitle);
                            ExpandAnimation heightAnim = new ExpandAnimation(view, 0, DimensionUtil.dip2px(32));
                            heightAnim.setDuration(2000);
                            view.startAnimation(heightAnim);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(urlString)) {
                                        as = getActionSheet(true);
                                        WindowManager wm = HomeViewController.this.getActivity().getWindowManager();
                                        DisplayMetrics dm = new DisplayMetrics();
                                        wm.getDefaultDisplay().getMetrics(dm);
                                        as.setHeight(dm.heightPixels);// 设置为屏幕高度
                                        NotificationViewController nvc = new NotificationViewController();
                                        nvc.setNotificationStrAndTitle(content.toString(), titleNotification);
                                        nvc.setActivity(HomeViewController.this.getActivity());
                                        as.setViewController(nvc);
                                        as.show();
                                        return;
                                    }
                                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                                    intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                                    intent.putExtra(WebViewController.KEY_URL, urlString);
                                    getActivity().startActivity(intent);
                                }
                            });
                        }
                    }, 500);
                }
            }
        }
    };

    private void loadGadgets() {
        String url = ContextProvider.getBaseURL() + "/api/dashboard/saler";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, gadgetsListener);
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    ActionSheet as;

    @OnClick(R.id.test_text_view)
    private View.OnClickListener testTextViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
//<<<<<<< HEAD
//            intent.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/groupbuy/upstream/list?accessToken=" + user.getAccessToken());
//            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
//=======
            intent.putExtra(WebViewController.KEY_URL, URLApi.getWebBaseUrl() + "/groupbuy/upstream/list?accessToken=" + user.getAccessToken());
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
//>>>>>>> 网页api域名改成m
            getActivity().startActivity(intent);
        }
    };
    @OnClick(R.id.todo_orders_button)
    private View.OnClickListener todoOrdersButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrdersByStatusViewController.class);
            intent.putExtra(OrdersByStatusViewController.KEY_ORDERS_STATE, "1,2");
            intent.putExtra(OrdersByStatusViewController.KEY_TITLE, "待处理订单");
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.not_received_orders_button)
    private View.OnClickListener offlineOrdersButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrdersByStatusViewController.class);
            intent.putExtra(OrdersByStatusViewController.KEY_ORDERS_STATE, "3");//TODO
            intent.putExtra(OrdersByStatusViewController.KEY_TITLE, "待收货订单");
            intent.putExtra(OrdersByStatusViewController.KEY_IS_48HOURS, false);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.all_orders_button)
    private View.OnClickListener allOrdersButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrdersByStatusViewController.class);
            intent.putExtra(OrdersByStatusViewController.KEY_ORDERS_STATE, "-1");//TODO
            intent.putExtra(OrdersByStatusViewController.KEY_TITLE, "全部订单");
            intent.putExtra(OrdersByStatusViewController.KEY_IS_48HOURS,false);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.purchase_not_received_orders_button)
    private View.OnClickListener purchaseNotReceivedOrdersButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
//<<<<<<< HEAD
//            intent.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/saler/upstream/order/list?delivery=2&accessToken=" + user.getAccessToken());
//            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
//=======
            intent.putExtra(WebViewController.KEY_URL, URLApi.getWebBaseUrl() + "/saler/upstream/order/list?delivery=2&accessToken=" + user.getAccessToken());
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
//>>>>>>> 网页api域名改成m
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.purchase_not_commented_orders_button)
    private View.OnClickListener purchaseNotCommentedOrdersButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
//<<<<<<< HEAD
//            intent.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/saler/upstream/order/ordereval?accessToken=" + user.getAccessToken());
//            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
//=======
            intent.putExtra(WebViewController.KEY_URL, URLApi.getWebBaseUrl() + "/saler/upstream/order/ordereval?accessToken=" + user.getAccessToken());
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
//>>>>>>> 网页api域名改成m
            getActivity().startActivity(intent);
        }
    };

    private AdapterView.OnItemClickListener actionItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!UserManager.getInstance().isLoggedIn()) {
                Toast.makeText(ContextProvider.getContext(), "请登录后重试!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent;
            if (adapter.getItem(position).target == WebViewController.class) {
                intent = new Intent(getActivity(), AutoResizeActivity.class);
            } else {

                intent = new Intent(getActivity(), ContainerActivity.class);
            }
            Intent srcIntent = adapter.getIntent(position);
            if (srcIntent != null) {
                intent.putExtras(adapter.getIntent(position));
//                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_NORMAL);
                intent.putExtra(WebViewController.KEY_URL_INTERCEPTOR, SellerUrlInterceptor.class);
            } else {
                intent = null;
                mainViewController.getStoreWindowFragment().setEditMode(true);
                mainViewController.showStoreWindow();
            }
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }
    };

    public class MyAdapter extends PagerAdapter {
        ImageView[] mImageViews = {
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),};

        @Override
        public int getCount() {
            return banners == null ? 0 : banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position
                    % mImageViews.length]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = mImageViews[position % mImageViews.length];
            iv.setTag(banners.get(position));
            iv.setOnClickListener(bannerImageOnClickListener);
//            iv.setImageResource(R.drawable.ic_home);
            ViewPager vp = (ViewPager) container;
            vp.removeView(iv);
            ViewGroup parent = (ViewGroup) iv.getParent();
            if (parent != null) {
                parent.removeView(iv);
            }
            vp.addView(iv, 0);

            String url = banners.get(position).getString("image");
            if (url != null && !url.contains("?")) {
                url += "?imageMogr2/auto-orient/thumbnail/" + 750 + "x" + 420 + "!/strip/quality/80/format/jpg/interlace/1";//TODO
            }
            ImageLoader.load(url, iv, R.drawable.loading_0, R.drawable.loading_0);//TODO
            return iv;
        }

        private View.OnClickListener bannerImageOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model m = (Model) v.getTag();
                String url = m.getString("href");
                Log.e("xx", "url:" + url);
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                    intent.putExtra(WebViewController.KEY_URL, url);
                    getActivity().startActivity(intent);
                }
            }
        };
    }
}
