package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.widget.ContainsEmojiEditText;
import com.tianpingpai.fragment.QRCodeDisplayViewController;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@Statistics(page = "我的")
@Layout(id = R.layout.fragment_main_mine)
public class MineViewController extends CrmBaseViewController {

    @Binding(id = R.id.username_text_view)
    private TextView usernameTextView;
    @Binding(id = R.id.avatar_image_view)
    private ImageView avatarImageView;

    private UserModel userModel;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        hideActionBar();
        UserManager.getInstance().registerListener(userInfoListener);
        loadData();
    }



    //我的资料修改界面
    @OnClick(R.id.avatar_image_view)
    private View.OnClickListener avatarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!UserManager.getInstance().isLoggedIn()) {
//                toLoginActivity();
                UserManager.getInstance().logout();
            } else {
                Intent i = new Intent(getActivity(), ContainerActivity.class);
                i.putExtra(ContainerActivity.KEY_CONTENT, MarketerViewController.class);
                getActivity().startActivity(i);
            }
        }
    };

    private void updateUserInfo(UserModel user) {
        if (usernameTextView == null) {
            return;
        }
        if (user != null) {
            usernameTextView.setText(user.getDisplayName());
        } else {
            usernameTextView.setText(R.string.not_logged_in_yet);
        }
        getAccessToken();
    }


    private void loadData() {
        if (UserManager.getInstance().getCurrentUser() == null) {
            if (getActivity() == null) {
                return;
            }
            getActivity().finish();
            Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
            return;
        }
        HttpRequest<ModelResult<UserModel>> req = new HttpRequest<>(
                URLApi.User.getInfo(), listener);
        ModelParser<UserModel> parser = new ModelParser<>(UserModel.class);
        req.setParser(parser);
        req.addParam("phone", UserManager.getInstance().getCurrentUser()
                .getPhone());
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        showLoading();
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<UserModel>> listener = new HttpRequest.ResultListener<ModelResult<UserModel>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<UserModel>> request,
                             ModelResult<UserModel> data) {
            hideLoading();
            userModel = data.getModel();
            Log.e("xx", "data" + data.getModel());
            if (data.isSuccess()) {
                updateUserInfo(data.getModel());
            }else if(data.getCode()==1){
//                UserManager.getInstance().logout();
                UserManager.getInstance().loginExpired(getActivity());
            }

            else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc() + "",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    //通讯企录
    @OnClick(R.id.contacts_container)
    private View.OnClickListener phoneContainerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
//            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseUrl() + "/crm/marketer/getContacts?accessToken="+UserManager.getInstance().getCurrentUser().getAccessToken());
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/marketer/getCommonInfo?type=1&accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken());
            getActivity().startActivity(i);
        }
    };

    //常用信息
    @OnClick(R.id.common_use_info_container)
    private View.OnClickListener commonUseIfoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/marketer/getCommonInfo?type=2&" + "accessToken=" + getAccessToken());
            getActivity().startActivity(i);
        }
    };

    //常见问题
    @OnClick(R.id.question_container)
    View.OnClickListener questionContainerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
//            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseUrl() + "/crm/marketer/getCommonQuestion");
            i.putExtra(WebViewController.KEY_URL, ContextProvider.getBaseURL() + "/crm/marketer/common/problem");
            getActivity().startActivity(i);
        }
    };

    //修改密码
    @OnClick(R.id.change_password_container)
    private View.OnClickListener revisePasswordOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, RevisePasswordViewController.class);
//            getActivity().startActivityForResult(intent, REQUEST_CODE_RETRIEVE_PASSWORD);
            getActivity().startActivity(intent);
        }
    };

    //消息列表
    @OnClick(R.id.messages_button)
    private View.OnClickListener messageButton = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,MessagesListViewController.class);
            getActivity().startActivity(intent);
        }
    };

    //设置
    @OnClick(R.id.settings_button)
    private View.OnClickListener settingOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, SettingsViewController.class);
            getActivity().startActivity(i);
        }
    };

    private String getAccessToken() {

        String accessToken = "";
        UserModel currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            accessToken = currentUser.getAccessToken();
        }

        return accessToken;
    }

    private ModelStatusListener<UserEvent, UserModel> userInfoListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if (event == UserEvent.Logout) {
                getActivity().finish();
                Log.e("mineviewcontroller","yunxingle");
            }
            if (event == UserEvent.UserInfoUpdate) {
                loadData();
            }
            updateUserInfo(userModel);
        }
    };
    /*

    @OnClick(R.id.buyer_RE_code)
    private View.OnClickListener buyerCode = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            String url = "";
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,QRCodeDisplayViewController.class);
            intent.putExtra(QRCodeDisplayViewController.KEY_TEXT, url);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.seller_RE_code)
    private View.OnClickListener sellerCode = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

        }
    };
    */
}
