package com.tianpingpai.seller.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.VersionManager;
import com.tianpingpai.model.VersionModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import cn.jpush.android.api.JPushInterface;

@SuppressWarnings("unused")
@ActionBar(title = "设置")
@Statistics(page = "设置")
@Layout(id = R.layout.ui_settings)
public class SettingsViewController extends BaseViewController {

    public static final String KEY_PUSH = "ispush";

    private SharedPreferences setting;

    private ModelStatusListener<UserEvent, UserModel> userInfoListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {

            if (event == UserEvent.Logout){
                getActivity().finish();
            }
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        rootView.findViewById(R.id.logout_button).setOnClickListener(
                logoutButtonListener);

        TextView replaceAccessTokenButton = (TextView) rootView.findViewById(R.id.replace_access_token_button);

        if( !URLApi.IS_DEBUG && URLApi.REPLACE_ACCESS_TOKEN){
            replaceAccessTokenButton.setVisibility(View.VISIBLE);
        }else {
            replaceAccessTokenButton.setVisibility(View.GONE);
        }

        CompoundButton pushToggleButton = (CompoundButton) rootView
                .findViewById(R.id.push_toggle_button);
        pushToggleButton.setOnCheckedChangeListener(pushCheckChangeListener);
        setting = getActivity().getSharedPreferences("tpp_login",
                Context.MODE_PRIVATE);
        pushToggleButton.setChecked(setting.getBoolean(KEY_PUSH, true));
        TextView versionTextView = (TextView) rootView.findViewById(R.id.version_text_view);
        UserManager.getInstance().registerListener(userInfoListener);
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            versionTextView.setText(versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userInfoListener);
    }

    @OnClick(R.id.replace_access_token_button)
    private OnClickListener replaceAccessTokenButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, ReplaceAccessTokenViewController.class);
            getActivity().startActivity(intent);
        }
    };


    @OnClick(R.id.help_button)
    private OnClickListener helpButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, HelpViewController.class);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.feedback_button)
    private OnClickListener feedbackButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            FeedbackAgent agent = new FeedbackAgent(getActivity());
            agent.startFeedbackActivity();
        }
    };

    @OnClick(R.id.about_us_button)
    private OnClickListener aboutUsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, AboutViewController.class);
            getActivity().startActivity(i);
        }
    };

    @OnClick(R.id.check_update_button)
    private OnClickListener checkUpdateButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            checkUpdate(true);
        }
    };

    @OnClick(R.id.logout_button)
    private OnClickListener logoutButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActionSheet(getActionSheet(true));
            dialog.setTitle("确定要退出登录吗？");
            dialog.setPositiveButtonListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "已成功退出登录", Toast.LENGTH_SHORT).show();
                    UserManager.getInstance().logout();
                }
            });
            dialog.show();
        }
    };

    private OnCheckedChangeListener pushCheckChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            Editor ed = setting.edit();
            if (isChecked) {
                ed.putBoolean(KEY_PUSH, true);
                JPushInterface.resumePush(ContextProvider.getContext());
            } else {
                ed.putBoolean(KEY_PUSH, false);
                JPushInterface.stopPush(ContextProvider.getContext());
            }
            ed.commit();
        }
    };

    //TODO
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

                if (!model.shouldUpdate()) {
                    Toast.makeText(ContextProvider.getContext(), "您当前使用的是最新版本", Toast.LENGTH_LONG).show();
                }

                if (model.getUpdateType() == VersionModel.UPDATE_TYPE_UMENG) {
                    UmengUpdateAgent.forceUpdate(getActivity());
                } else if (model.getUpdateType() == VersionModel.UPDATE_TYPE_SELF) {
                    if (model.shouldUpdate()) {// TODO
                        // TODO any flag not to show again?
                        final ActionSheetDialog dialog = new ActionSheetDialog();
                        dialog.setTitle("发现新版本，请更新！");
                        dialog.setPositiveButtonListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VersionManager.getInstance().update(model);
                                dialog.dismiss();
                            }
                        });
                        dialog.setActionSheet(getActionSheet(true));
                        dialog.show();
                    }
                }
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
        req.setParser(parser);
        req.setAttachment(showDialog);
        req.addParam("user_type", "0");//用户类型，0为卖家版，1为买家版
        req.addParam("ptype", "1");//终端类型，1为安卓，2为ios
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        if (showDialog) {
            showLoading();
        }
    }
}
