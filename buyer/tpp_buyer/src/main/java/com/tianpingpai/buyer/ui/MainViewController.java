package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.tools.ParseHrefUtil;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.tools.TLog;
import com.dtr.zxing.activity.CaptureActivity;
import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ErrorListener;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.manager.VersionManager;
import com.tianpingpai.model.VersionModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

@SuppressWarnings("unused")
@Statistics(page = "主页")
@ActionBar(hidden = true)
@Layout(id = R.layout.ui_main)
public class MainViewController extends BaseViewController {

    public static final String Key_pos = "position";
    private int position;

    private HomeViewController homeFragment = new HomeViewController();
    private StoreListViewController storeListFragment = new StoreListViewController();
    private ShoppingCartViewController shoppingCartFragment = new ShoppingCartViewController();
    private MineViewController mineViewController = new MineViewController();

    private View currentContent;
    @Binding(id = R.id.mine_tab)
    private View mineTab;
    @Binding(id = R.id.home_tab)
    private View homeTab;
    @Binding(id = R.id.stores_tab)
    private View storesTab;
    @Binding(id = R.id.shopping_cart_tab)
    private View shopCartTab;
    private View currentTab;
    private View[] tabs = new View[4];

    private int[] tabNormalImageResources = {
            R.drawable.ic_tab_home_normal,
            R.drawable.ic_tab_stores_normal,
            R.drawable.ic_tab_shopping_cart_normal,
            R.drawable.ic_tab_mine_normal,
    };

    private int[] tabHighlightImageResources = {
            R.drawable.ic_tab_home_highlight,
            R.drawable.ic_tab_stores_highlight,
            R.drawable.ic_tab_shopping_cart_highlight,
            R.drawable.ic_tab_mine_highlight,
    };

    private void showViewController(BaseViewController viewController,View tabView){
        if(currentTab == tabView){
            return;
        }
        currentTab = tabView;
        if(viewController.getView() == null){
            viewController.setActivity(getActivity());
            viewController.createView(getActivity().getLayoutInflater(),null);
        }

        ViewGroup parent = (ViewGroup) currentContent.getParent();
        parent.removeView(currentContent);
        parent.addView(viewController.getView(),0,currentContent.getLayoutParams());
        currentContent = viewController.getView();
        viewController.resume();

        for(int i = 0;i < tabs.length;i++){
            View tab = tabs[i];
            ImageView tabImageVew = (ImageView) tab.findViewById(R.id.tab_image_view);
            TextView nameTextView = (TextView) tab.findViewById(R.id.tab_name_text_view);
            if(tab == tabView){
                TLog.e("xx", "102-------tab=pos=" + i);
                if (i == 3) {
                    OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, new OrderModel());
                }
                tabImageVew.setImageResource(tabHighlightImageResources[i]);
                nameTextView.setTextColor(getActivity().getResources().getColor(R.color.green));
            }else {
                tabImageVew.setImageResource(tabNormalImageResources[i]);
                nameTextView.setTextColor(getActivity().getResources().getColor(R.color.gray_66));
            }
        }
    }

    void showHomeFragment() {
        homeFragment.setMainFragment(this);
        showViewController(homeFragment, homeTab);
    }

    public void showShopFragment() {
        showViewController(storeListFragment,storesTab);
    }

    private void showShopCarFragment() {
        shoppingCartFragment.setMainFragment(this);
        showViewController(shoppingCartFragment, shopCartTab);
    }

    private void showMineFragment() {
        showViewController(mineViewController,mineTab);
    }

    public void setShopData(Model model){
        if(model != null) {
            storeListFragment.categoryId = model.getInt("category_id");
            showShopFragment();
            storeListFragment.loadData(1);
            Log.e("okokokok",""+model.getInt("categoryId"));
        }
    }

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        position = a.getIntent().getIntExtra(Key_pos,0);
        if (MarketManager.getInstance().getCurrentMarket() == null) {
            if (MarketManager.getInstance().getCurrentMarket() == null) {
                Intent i = new Intent(a, ContainerActivity.class);
                i.putExtra(ContainerActivity.KEY_CONTENT,
                        SelectMarketViewController.class);
                a.startActivity(i);
                a.finish();
            }
        }
    }

    private void prepare4UmengUpdate() {
        if(getActivity() == null){
            return;
        }
        MobclickAgent.updateOnlineConfig(getActivity());
        // 获取友盟在线参数
        String update_mode = MobclickAgent.getConfigParams(getActivity(), "upgrade_mode");
        if (update_mode == null || update_mode.equals("")) {
            return;
        }

        // 转换为数组
        String[] mUpdateModeArray = update_mode.split(",");
        UmengUpdateAgent.setUpdateOnlyWifi(false); // 在任意网络环境下都进行更新自动提醒

        int curr_version_name = 0;
        try {
            curr_version_name = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        TLog.w("xx", "152-----------curr_version_name=" + curr_version_name);
        boolean result = false;
        for (int i = 0; i < mUpdateModeArray.length; i += 2) {
            if (mUpdateModeArray[i].equals(String.valueOf(curr_version_name))) {
                if (mUpdateModeArray[i + 1].equals("F")) {
                    UmengUpdateAgent.forceUpdate(ContextProvider.getContext()); // 调用umeng更新接口
                    result = true;
                    // 对话框按键的监听，对于强制更新的版本，如果用户未选择更新的行为，关闭app
                    UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {

                        @Override
                        public void onClick(int status) {
                            switch (status) {
                                case UpdateStatus.Update:
                                    break;
                                default:
                                    getActivity().finish();
                                    // 友盟自动更新目前还没有提供在代码里面隐藏/显示更新对话框的
                                    Toast.makeText(ContextProvider.getContext(),
                                            "非常抱歉，您需要更新应用才能继续使用", Toast.LENGTH_LONG)
                                            .show();
                            }
                        }
                    });
                }
                break; // 只要找到对应的版本号，即结束循环
            }
        }
        if (!result) {
            UmengUpdateAgent.update(ContextProvider.getContext()); // 调用umeng更新接口
        }
    }


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        currentContent = rootView.findViewById(R.id.content_holder);
        tabs[0] = homeTab;
        tabs[1] = storesTab;
        tabs[2] = shopCartTab;
        tabs[3] = mineTab;
        showContent();
        hideLoading();
        switch (position){
            case 0:
                showHomeFragment();
                break;
            case 1:
                showShopFragment();
                break;
            case 2:
                showShopCarFragment();
//                showCollectionFragment();
                break;
            case 3:
                showMineFragment();
                break;
            default:
                showHomeFragment();
                break;
        }

        checkUpdate(false);
        UserManager.getInstance().registerListener(expiredListener);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        homeFragment.destroyView();
        storeListFragment.destroyView();
        shoppingCartFragment.destroyView();
//        collectionFragment.destroyView();
        mineViewController.destroyView();
        UserManager.getInstance().unregisterListener(expiredListener);
    }

    private ModelStatusListener<UserEvent, UserModel> expiredListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if(event == UserEvent.LoginExpired){
                if(getActivity() == null  || getActivity().isFinishing()){
                    return;
                }
                getActivity().finish();
            }
        }
    };

    @OnClick(R.id.home_tab)
    private OnClickListener homeTabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showHomeFragment();
        }
    };

    @OnClick(R.id.stores_tab)
    private OnClickListener storesTabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showShopFragment();
        }
    };

    @OnClick(R.id.shopping_cart_tab)
    private OnClickListener shoppingCartTabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showShopCarFragment();
//            showCollectionFragment();
        }
    };
    @OnClick(R.id.mine_tab)
    private OnClickListener mineTabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showMineFragment();
        }
    };

    private ResultListener<ModelResult<VersionModel>> updateListener = new ResultListener<ModelResult<VersionModel>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<VersionModel>> request,
                             ModelResult<VersionModel> data) {
            hideLoading();
            if (data.isSuccess()) {
                final VersionModel model = data.getModel();
                if (model == null) {
                    return;
                }
                if (model.getUpdateType() == VersionModel.UPDATE_TYPE_UMENG) {
                    prepare4UmengUpdate();
                } else if (model.getUpdateType() == VersionModel.UPDATE_TYPE_SELF) {
                    if (model.shouldUpdate()) {
                        // TODO any flag not to show again?
                        final ActionSheetDialog dialog = new ActionSheetDialog();
                        dialog.setActionSheet(getActionSheet(true));
                        dialog.setTitle("发现新版本，请更新！");
                        dialog.setPositiveButtonListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VersionManager.getInstance().update(model);
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButtonListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (model.forceUpdate()) {
                                    getActivity().finish();
                                }
                            }
                        });
                        dialog.show();
                    }
                }
            } else {
                prepare4UmengUpdate();
            }
        }
    };

    public void checkUpdate(boolean showDialog) {
        String url = ContextProvider.getBaseURL()
                + "/api/version/getVersion.json";
        HttpRequest<ModelResult<VersionModel>> req = new HttpRequest<>(
                url, updateListener);
        ModelParser<VersionModel> parser = new ModelParser<>(
                VersionModel.class);
        req.addParam("user_type", "1");// 用户类型，0为卖家版，1为买家版
        req.addParam("ptype", "1");// 终端类型，1为安卓，2为ios
        req.setParser(parser);
        req.setErrorListener(new ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                try {
                    prepare4UmengUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        if (showDialog) {
            showLoading();
        }
    }

    private void copyUrl(final String url) {
        new AlertDialog.Builder(getActivity()).setMessage(url)
                .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(
                                null, url));
                        Toast.makeText(ContextProvider.getContext(), "已复制到剪贴板！",
                                Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    @Override
    public void onActivityResult(Activity a,int requestCode, int resultCode, Intent data) {
        if (requestCode == HomeViewController.REQUEST_CODE_SCAN_BAR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final String url = data
                        .getStringExtra(CaptureActivity.KEY_RESULT_STRING);
                if (url != null) {
                    if (!ParseHrefUtil.handleURL(getActivity(), url)) {
                        if (Patterns.WEB_URL.matcher(url).matches()) {
                            copyUrl(url);
                        } else {
                            copyUrl(url);
                        }
                    }
                }
            }
        }
        if (requestCode == ShoppingCartViewController.REQUEST_CODE_CONFIRM) {
            showHomeFragment();
        }
    }
}
