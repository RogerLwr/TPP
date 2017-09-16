package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.VersionManager;
import com.tianpingpai.model.VersionModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.DimensionUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

import java.util.ArrayList;

@ActionBar(hidden = true)
@Layout(id = R.layout.ui_main)
@Statistics(page = "主页")
public class MainViewController extends BaseViewController {
	
    private FrameLayout contentContainer;
    private ArrayList<BaseViewController> viewControllers;
    private BaseViewController currentViewController;

    private int tabSelectedColor = ContextProvider.getContext().getResources().getColor(R.color.green);
    private int tabDeselectedColor = ContextProvider.getContext().getResources().getColor(R.color.gray_99);

    private StoreWindowViewController storeWindowFragment = new StoreWindowViewController();

    public StoreWindowViewController getStoreWindowFragment(){
        return storeWindowFragment;
    }

    public void showStoreWindow(){
        RadioButton rb = (RadioButton) getView().findViewById(R.id.products_tab);
        rb.setChecked(true);
    }

    private ModelStatusListener<UserEvent, UserModel> userInfoListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {

            if (event == UserEvent.Logout){
                Log.e("xx", "127--------登出跳登录");
                toLoginActivity();
            }
        }
    };

    private void toLoginActivity() {
        Intent i = new Intent(getActivity(), ContainerActivity.class);
        i.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        contentContainer = (FrameLayout) rootView.findViewById(R.id.vc_content_container);

        viewControllers = new ArrayList<>();
        HomeViewController homeFragment = new HomeViewController();
        homeFragment.setMainViewController(this);
        viewControllers.add(homeFragment);
        viewControllers.add(new OrdersViewController());

        viewControllers.add(storeWindowFragment);
        viewControllers.add(new MineViewController());

        for (BaseViewController bvc : viewControllers) {
            bvc.setActivity(getActivity());
        }

        View tabContainer = rootView.findViewById(R.id.tab_container);
        int[] buttonIds = new int[]{R.id.home_tab, R.id.orders_tab, R.id.products_tab, R.id.mine_tab};
        for (int i = 0; i < buttonIds.length; i++) {
            int id = buttonIds[i];
            RadioButton rb = (RadioButton) tabContainer.findViewById(id);
            rb.setOnCheckedChangeListener(new TabSelectionListener(i));
            rb.setChecked(i == 0);
        }
        checkUpdate();
        UserManager.getInstance().registerListener(userInfoListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(viewControllers != null){
            for (BaseViewController bvc : viewControllers) {
                bvc.destroyView();
            }
        }
        UserManager.getInstance().unregisterListener(userInfoListener);
    }

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        if(currentViewController != null && currentViewController.getView() != null){
            currentViewController.resume();
        }
    }

    class TabSelectionListener implements CompoundButton.OnCheckedChangeListener {
        private int index;
        TabSelectionListener(int index) {
            this.index = index;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                buttonView.setTextColor(tabSelectedColor);
                BaseViewController vc = viewControllers.get(index);
                if (vc.getView() == null) {
                    vc.onCreateView(getActivity().getLayoutInflater(), contentContainer);
                }
                if(currentViewController != null){
                    currentViewController.pause();
                }
                contentContainer.removeAllViewsInLayout();
                contentContainer.addView(vc.getView());
                vc.resume();
                currentViewController = vc;
//                for(int i = 0;i < viewControllers.size();i++){
//                    if(i == index){
//                        addQualityViewController.resume();
//                    }
//                }
            }else{
                buttonView.setTextColor(tabDeselectedColor);
            }
        }
    }


    private HttpRequest.ResultListener<ModelResult<VersionModel>> updateListener = new HttpRequest.ResultListener<ModelResult<VersionModel>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<VersionModel>> request,
                             ModelResult<VersionModel> data) {
//			hideLoading();
            if (data.isSuccess()) {
                final VersionModel model = data.getModel();
                if (model == null) {
                    return;
                }
                if (model.getUpdateType() == VersionModel.UPDATE_TYPE_UMENG) {
                    prepare4UmengUpdate();
//					UmengUpdateAgent.forceUpdate(ContextProvider.getContext());
                } else if (model.getUpdateType() == VersionModel.UPDATE_TYPE_SELF) {
                    if (model.shouldUpdate()) {// TODO
                        // TODO any flag not to show again?
                        final ActionSheetDialog dialog = new ActionSheetDialog();
                        ActionSheet as = new ActionSheet();
                        as.setHeight(DimensionUtil.dip2px(300));
                        as.setActivity(getActivity());
                        dialog.setActionSheet(as);
                        dialog.setTitle("发现新版本，请更新！");
                        dialog.setPositiveButtonListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VersionManager.getInstance().update(model);
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButtonListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (model.forceUpdate()) {
                                    getActivity().finish();
                                    Toast.makeText(ContextProvider.getContext(),
                                            "非常抱歉，您需要更新应用才能继续使用", Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                        dialog.setActivity(getActivity());
                        dialog.show();
                    }
                }
            } else {
                prepare4UmengUpdate();
            }
        }
    };

    public void checkUpdate() {
        String url = ContextProvider.getBaseURL()
                + "/api/version/getVersion.json";
        HttpRequest<ModelResult<VersionModel>> req = new HttpRequest<>(
                url, updateListener);
        ModelParser<VersionModel> parser = new ModelParser<>(
                VersionModel.class);
        req.addParam("user_type", "0");// 用户类型，0为卖家版，1为买家版
        req.addParam("ptype", "1");// 终端类型，1为安卓，2为ios
        req.setParser(parser);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                prepare4UmengUpdate();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void prepare4UmengUpdate() {
        MobclickAgent.updateOnlineConfig(getActivity());
        // 获取友盟在线参数
        String update_mode = MobclickAgent.getConfigParams(getActivity(), "upgrade_mode");
        Log.e("zz","update_mode"+update_mode);
        if (update_mode == null || update_mode.equals("")) {
            return;
        }

        UmengUpdateAgent.setDeltaUpdate(false); //增量更新 为false  表示全量更新
        // 转换为数组
        String[] mUpdateModeArray = update_mode.split(",");

        UmengUpdateAgent.setUpdateOnlyWifi(false); // 在任意网络环境下都进行更新自动提醒

        int curr_version_name = 0;
        try {
            curr_version_name = ContextProvider.getContext().getPackageManager().getPackageInfo(ContextProvider.getContext().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private long lastClickedAt;
    @Override
    public boolean onBackKeyDown(Activity a) {
        if (lastClickedAt > System.currentTimeMillis() - 2000) {
            return super.onBackKeyDown(a);
        }
        lastClickedAt = System.currentTimeMillis();
        Toast.makeText(getActivity().getApplicationContext(), "请再按一次退出", Toast.LENGTH_SHORT).show();
        return true;
    }
}
