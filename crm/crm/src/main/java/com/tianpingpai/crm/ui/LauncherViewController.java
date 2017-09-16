package com.tianpingpai.crm.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.tianpingpai.crm.R;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@Statistics(page = "启动页")
@ActionBar(hidden = true)
@Layout(id = R.layout.fragment_launcher)
public class LauncherViewController extends BaseViewController {

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (UserManager.getInstance().getCurrentUser() == null) {
                if(getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                    intent.putExtra(LoginViewController.KEY_IS_TO_HOME, true);
                    getActivity().startActivity(intent);
                    handler.removeCallbacks(this);
                    getActivity().finish();
                    Log.e("启动页","youkaishiqidongle");
                }
            }else{
                if(getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
                    getActivity().startActivity(intent);
                    handler.removeCallbacks(this);
                    getActivity().finish();
                }
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        handler.postDelayed(runnable, 2000);
    }
}
