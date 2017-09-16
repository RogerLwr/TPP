package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.crm.R;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

@SuppressWarnings("unused")
@ActionBar(title = "设置")
@Layout(id=R.layout.ui_settings)
public class SettingsViewController extends CrmBaseViewController{

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        TextView versionNumberTextView = (TextView) rootView.findViewById(R.id.version_number_text_view);
        versionNumberTextView.setText(getVersion());
        showContent();
    }

    public String getVersion() {
        try {
                PackageManager manager = getActivity().getPackageManager();
                PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
                String version = info.versionName;
                return "" + version;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

    @OnClick(R.id.check_update)
    private View.OnClickListener checkUpOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            checkUpdate(true);
        }
    };

    @OnClick(R.id.logout_button)
    private View.OnClickListener logOutOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setTitle(getActivity().getResources().getString(R.string.logout_confirm));
            dialog.setActivity(getActivity());
            dialog.setActionSheet(getActionSheet(true));
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    UserManager.getInstance().logout();
//                    UserManager.getInstance().loginExpired(getActivity());
                    toLoginActivity();
                }
            });
            dialog.show();
        }
    };

    private void toLoginActivity() {
        Intent i = new Intent(getActivity(), ContainerActivity.class);
        i.putExtra(ContainerActivity.KEY_CONTENT,
                LoginViewController.class);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    public void checkUpdate(boolean showDialog) {
        showLoading();
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        UmengUpdateAgent.showUpdateDialog(getActivity(), updateInfo);
                        break;
                    case UpdateStatus.No: // has no update
                        Toast.makeText(getActivity(), "没有更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        Toast.makeText(getActivity(), "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout: // time out
                        Toast.makeText(getActivity(), "超时", Toast.LENGTH_SHORT).show();
                        break;
                }
                hideLoading();
            }
        });
        UmengUpdateAgent.update(getActivity());
    }
}
