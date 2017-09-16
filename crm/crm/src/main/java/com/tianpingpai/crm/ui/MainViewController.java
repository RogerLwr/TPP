package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;

import com.tianpingpai.manager.UserManager;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.ViewController;
import com.tianpingpai.ui.ViewControllerAdapter;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


@Layout(id = R.layout.fragment_newmain)
@Statistics(page = "主页")
public class MainViewController extends CrmBaseViewController{

    RadioButton[] tabs = {null, null, null, null, null};
    ArrayList<ViewController> viewControllers = new ArrayList<>();
    private HomeViewController homeViewController = new HomeViewController();
    private OrdersViewController ordersViewController = new OrdersViewController();
    private ViewControllerAdapter viewControllerAdapter = new ViewControllerAdapter();

    {
        viewControllers.add(homeViewController);
        viewControllers.add(ordersViewController);
        CustomerViewController cvc = new CustomerViewController();
        viewControllers.add(cvc);
        WorkViewController wvc = new WorkViewController();
        viewControllers.add(wvc);
        MineViewController mvc = new MineViewController();
        viewControllers.add(mvc);
    }

    private ViewPager viewPager;

    public int getContentHeight(){
        return getView().getHeight();
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        hideActionBar();
        homeViewController.setMainFragment(this);
        ordersViewController.setMainFragment(this);

        tabs[0] = (RadioButton) rootView.findViewById(R.id.home_tab);
        tabs[1] = (RadioButton) rootView.findViewById(R.id.orders_tab);
        tabs[2] = (RadioButton) rootView.findViewById(R.id.clients_tab);
        tabs[3] = (RadioButton) rootView.findViewById(R.id.work_tab);
        tabs[4] = (RadioButton) rootView.findViewById(R.id.mine_tab);

        for (int i = 0; i < tabs.length; i++) {
            RadioButton tab = tabs[i];
            tab.setOnCheckedChangeListener(new TabChangeListener(i));
        }

        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
//        ViewPagerScroller scroller = new ViewPagerScroller(getActivity());
//        scroller.setScrollDuration(50);
//        scroller.initViewPagerScroll(viewPager);
        viewPager.setEnabled(false);

        for (ViewController vc : viewControllers) {
            vc.setActivity(getActivity());
        }
        viewControllerAdapter.setViewControllers(viewControllers);
        viewPager.setAdapter(viewControllerAdapter);

        viewPager.removeOnPageChangeListener(onPageChangeListener);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setEnabled(false);

//        UmengUpdateAgent.forceUpdate(getActivity());
        prepare4UmengUpdate();
        //检查是否登录
        checkLogin();
        showContent();

    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            tabs[position].setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void checkLogin() {
        if (UserManager.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(getActivity(),
                    ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,
                    LoginViewController.class);
            getActivity().startActivity(i);
        }
    }

    private class TabChangeListener implements CompoundButton.OnCheckedChangeListener {
        int mIndex;

        TabChangeListener(int index) {
            this.mIndex = index;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                viewPager.setCurrentItem(mIndex, true);
            }
        }
    }

    private long lastClickedAt;

    @Override
    public boolean onBackKeyDown(Activity a) {
        if (homeViewController.actionSheet != null && homeViewController.actionSheet.isShowing()) {
            homeViewController.actionSheet.dismiss();
            return true;
        }
        if (ordersViewController.actionSheet != null && ordersViewController.actionSheet.isShowing()) {
            ordersViewController.actionSheet.dismiss();
            return true;
        }

        if (lastClickedAt > System.currentTimeMillis() - 2000) {
            return super.onBackKeyDown(a);
        }
        lastClickedAt = System.currentTimeMillis();
        Toast.makeText(getActivity().getApplicationContext(), "请再按一次退出", Toast.LENGTH_SHORT).show();

        return true;
    }

    private void prepare4UmengUpdate() {
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
}
