package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.tools.MobileUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserEvent;

@SuppressWarnings("unused")
@Layout(id = R.layout.vc_login)
public class LoginViewController extends BaseViewController {

    private ActionSheet actionSheet;

    @Binding(id = R.id.phone_edit_text)
    private EditText phoneEditText;
    @Binding(id = R.id.password_edit_text)
    private EditText passwordEditText;
    @Binding(id = R.id.submit_button)
    private TextView submitButton;
    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
    }

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        View actionBar = setActionBarLayout(R.layout.ab_title_green);
        Toolbar toolbar = (Toolbar) actionBar.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(backButtonListener);
        UserManager.getInstance().registerListener(userStatusListener);
        setTitle("用户登录");
        passwordEditText.addTextChangedListener(passWordWatcher);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userStatusListener);
    }

    private ModelStatusListener<UserEvent, UserModel> userStatusListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event) {
                case Login:
                    finish();
                    break;
                case LoginFailed:
                    hideSubmitting();
                    submitButton.setEnabled(true);
                    Log.e("submitButton","登陆失败触发了!!!");
                default:
                    break;
            }
        }
    };

    private void finish(){
        Log.e("xx", "finishe:");
        if(actionSheet != null){
            actionSheet.dismiss();
            Log.e("xx", "dimiss:");
        }else{
            if (getActivity() != null) {
                TaskStackBuilder builder = TaskStackBuilder.create(getActivity());
                int count = builder.getIntentCount();
                Log.e("xx","count:" + getActivity().getCallingActivity());
                if(getActivity().getCallingActivity() == null && UserManager.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(getActivity(),ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
                    getActivity().startActivity(intent);
                }
                getActivity().finish();
            }
        }
    }

    @OnClick(R.id.submit_button)
    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
            String userName = phoneEditText.getText().toString().trim();
            String userPwd = passwordEditText.getText().toString().trim();

            if (!MobileUtil.isMobileNumber(userName)) {
                Toast.makeText(ContextProvider.getContext(), "请填写正确手机号码!", Toast.LENGTH_LONG).show();
                return;
            }

            if (!MobileUtil.isPWDNum(userPwd)) {
                Toast.makeText(ContextProvider.getContext(), "密码太短了!", Toast.LENGTH_LONG).show();
                return;
            }

            UserManager.getInstance().login(userName, userPwd);
            submitButton.setEnabled(false);
            showSubmitting();
        }
    };

    @OnClick(R.id.register_button)
    private View.OnClickListener registerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, RegisterViewController.class);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.forget_password_button)
    private View.OnClickListener forgetPasswordButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, RetrievePasswordViewController.class);
            getActivity().startActivity(intent);
        }
    };

    private TextWatcher passWordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() < 6) {
                submitButton.setEnabled(false);
            } else {
                submitButton.setEnabled(true);
            }
            Log.e("submitButton","触发啦!!!!!!!!!");
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}
