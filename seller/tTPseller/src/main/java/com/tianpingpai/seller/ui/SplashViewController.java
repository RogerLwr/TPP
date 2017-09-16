package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.utils.Settings;

@ActionBar(hidden = true)
@Layout(id = R.layout.ui_splash)
public class SplashViewController extends BaseViewController {

    private Handler handler = new Handler();

    private Runnable startRun = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                Class<?> fragmentClass;
                if (Settings.getInstance().isFirstOpen()) {
                    fragmentClass = GuideViewController.class;
                } else if (UserManager.getInstance().isLoggedIn()) {
                    fragmentClass = MainViewController.class;
                } else {
                    fragmentClass = LoginViewController.class;
                }
                intent.putExtra(ContainerActivity.KEY_CONTENT, fragmentClass);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        handler.postDelayed(startRun, 2000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(startRun);
    }

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        handler.removeCallbacks(startRun);
    }
}
