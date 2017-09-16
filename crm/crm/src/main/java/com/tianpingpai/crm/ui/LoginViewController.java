package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@Statistics(page = "登录")
@Layout(id = R.layout.fragment_login1509)
public class LoginViewController extends BaseViewController {

    private EditText usernameEditText;
    private EditText passwordEditText;

    public static final String KEY_USER_NAME = "key.username";
    public static final String KEY_PASSWORD = "key.password";
    public static final String KEY_IS_TO_HOME = "Key.isToHome";// 是否跳首页
    boolean isToHome = true;
    public static final int REQUEST_CODE_RETRIEVE_PASSWORD = 103;

    @OnClick(R.id.password_button)
    private View.OnClickListener forgetPasswordButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, UpdatePasswordViewController.class);
            getActivity().startActivityForResult(intent, REQUEST_CODE_RETRIEVE_PASSWORD);
        }
    };

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RETRIEVE_PASSWORD && resultCode == Activity.RESULT_OK && data != null) {
            String username = data.getStringExtra(KEY_USER_NAME);
            String password = data.getStringExtra(KEY_PASSWORD);
            usernameEditText.setText(username);
            passwordEditText.setText(password);
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        isToHome = getActivity().getIntent().getBooleanExtra(KEY_IS_TO_HOME, true);
        View actionBarView = setActionBarLayout(R.layout.ab_title_green);
        Toolbar toolbar = (Toolbar) actionBarView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        setTitle(R.string.login);
        usernameEditText = (EditText) rootView.findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) rootView.findViewById(R.id.password_edit_text);

//		showContent();
        UserManager.getInstance().registerListener(userEventListener);
        UserModel userModel = UserManager.getInstance().getCurrentUser();
        String phone = ContextProvider.getContext().getSharedPreferences("currentUser", 100).getString("phoneNumber", "");
        if (!"".equals(phone)) {
            usernameEditText.setText(phone);
        }
    }

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        UserManager.getInstance().unregisterListener(userEventListener);
    }

    long submitButtonLastClick = 0;

    @OnClick(R.id.login_button)
    private OnClickListener loginButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (System.currentTimeMillis() - submitButtonLastClick < 2000) {
                return;
            }
            submitButtonLastClick = System.currentTimeMillis();
            if (TextUtils.isEmpty(usernameEditText.getText())) {
                Toast.makeText(ContextProvider.getContext(), R.string.user_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(passwordEditText.getText())) {
                Toast.makeText(ContextProvider.getContext(), R.string.password_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            showSubmitting();
            hideKeyboard();
            UserManager.getInstance().login(username, password);
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userEventListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event) {
                case Login:
                    hideSubmitting();
                    Toast.makeText(ContextProvider.getContext(), R.string.login_sucess, Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        Intent i = new Intent(getActivity(), ContainerActivity.class);
                        i.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
                        if (isToHome) {
                            getActivity().startActivity(i);
                        }
                        getActivity().finish();
                    }
                    break;
                case LoginFailed:
                    hideSubmitting();
                    break;
                case Logout:
                    break;
                default:
                    break;
            }
        }
    };
}
