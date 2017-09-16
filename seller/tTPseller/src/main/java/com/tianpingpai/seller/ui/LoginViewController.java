package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.TextValidator;

@ActionBar(hidden = true)
@SuppressWarnings("unused")
@Statistics(page = "登录")
@Layout(id = R.layout.ui_login)
public class LoginViewController extends BaseViewController {

    public static final String KEY_USER_NAME = "key.username";
    public static final String KEY_PASSWORD = "key.password";

    public static final int REQUEST_CODE_RETRIEVE_PASSWORD = 103;

    @Binding(id = R.id.phone_edit_text)
    private EditText phoneEditText;
    @Binding(id = R.id.password_edit_text)
    private EditText passwordEditText;
    @Binding(id = R.id.login_button)
    private View loginButton;
    @Binding(id = R.id.agree_checkbox)
    private CheckBox agreeCheckBox;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        phoneEditText.setText(getActivity().getIntent().getStringExtra(KEY_USER_NAME));
        passwordEditText.setText(getActivity().getIntent().getStringExtra(KEY_PASSWORD));
        passwordEditText.addTextChangedListener(passWordWatcher);

        TextView agreementTV = (TextView) rootView.findViewById(R.id.agree_tv);
        agreementTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        agreementTV.getPaint().setAntiAlias(true);
        agreementTV.setOnClickListener(agreementButtonListener);

        UserManager.getInstance().registerListener(userStatusListener);
        showContent();
    }

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RETRIEVE_PASSWORD && resultCode == Activity.RESULT_OK && data != null) {
            String username = data.getStringExtra(KEY_USER_NAME);
            String password = data.getStringExtra(KEY_PASSWORD);
            passwordEditText.setText(username);
            passwordEditText.setText(password);
        }
    }

    private View.OnClickListener agreementButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
//            i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, AgreementViewController.class);
            String url = URLApi.getWebBaseUrl() + "/html/app/manage.html";
            i.putExtra(WebViewController.KEY_URL, url);
            getActivity().startActivity(i);
        }
    };

    @OnClick(R.id.register_button)
    private View.OnClickListener registerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
//            getActivity().startActivityForResult(registerIntent, REQUEST_CODE_RETRIEVE_PASSWORD);
        }
    };

    @OnClick(R.id.forget_password_button)
    private View.OnClickListener forgetPasswordButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, RetrievePasswordViewController.class);
            getActivity().startActivityForResult(intent, REQUEST_CODE_RETRIEVE_PASSWORD);
        }
    };

    @OnClick(R.id.login_button)
    private View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            Intent intent = new Intent(getActivity(),ContainerActivity.class);
//            intent.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
//            intent.putExtra(WebViewController.KEY_URL,"http://192.168.1.196/app/app_header_test/test.html");
//            getActivity().startActivity(intent);
//
//            if(true){
//                return;
//            }

            String userName = phoneEditText.getText().toString().trim();
            String userPwd = passwordEditText.getText().toString().trim();

            if (!TextValidator.isMobileNumber(userName)) {
                Toast.makeText(ContextProvider.getContext(), "请填写正确手机号码!", Toast.LENGTH_LONG).show();
                return;
            }

            if (userPwd.length() < 6) {
                Toast.makeText(ContextProvider.getContext(), "密码太短了!", Toast.LENGTH_LONG).show();
                return;
            }

            if (!agreeCheckBox.isChecked()) {
                Toast.makeText(ContextProvider.getContext(), "请勾选天平派商家管理办法!", Toast.LENGTH_LONG).show();
                return;
            }

            hideKeyboard();
            UserManager.getInstance().login(userName, userPwd);
            getView().findViewById(R.id.login_button).setEnabled(false);
            showLoading();

        }
    };

    private ModelStatusListener<UserEvent, UserModel> userStatusListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event) {
                case Login:
//                    hideLoading();
                    if (getActivity() != null) {
                        Intent i = NavUtils.getParentActivityIntent(getActivity());
                        Log.e("xx", "up:" + i);
                        if (i == null) {
                            Intent intent = new Intent(getActivity(), ContainerActivity.class);
                            intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
                            getActivity().startActivity(intent);
                        }
                        getActivity().finish();
                    }
                    break;
                case LoginFailed:
                    hideLoading();
                    loginButton.setEnabled(true);
                default:
                    break;
            }
        }
    };

    private TextWatcher passWordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.w("xx", "73----------------" + s);
            if (s.toString().length() < 6) {
                loginButton.setEnabled(false);
            } else {
                loginButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
