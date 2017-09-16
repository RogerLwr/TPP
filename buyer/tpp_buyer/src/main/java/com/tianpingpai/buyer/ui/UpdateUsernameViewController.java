package com.tianpingpai.buyer.ui;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@Layout(id = R.layout.ui_update_user_name)
@ActionBar(title = "修改昵称")
@Statistics(page = "更新用户名")
public class UpdateUsernameViewController extends BaseViewController {

    public static final String KEY_USER_NAME = "key.userName";

    private EditText userNameEditText;
    private View submitButton;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        userNameEditText = (EditText) rootView.findViewById(R.id.user_name_edit_text);
        submitButton = rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(submitButtonListener);

        String username = getActivity().getIntent().getStringExtra(KEY_USER_NAME);
        userNameEditText.setText(username);
        showContent();
    }

    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(userNameEditText.getText())) {
                Toast.makeText(getActivity(), "昵称不能为空！",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getActivity(), "请先登录！",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String url = ContextProvider.getBaseURL() + "/api/user/updateBuyUserInfo.json";
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, submitListener);
            req.setMethod(HttpRequest.POST);
            req.setAttachment(userNameEditText.getText().toString());
            req.addParam("display_name", userNameEditText.getText().toString());
            req.setParser(new ModelParser<>(Void.class));
            req.addParam("phone", user.getPhone());
            VolleyDispatcher.getInstance().dispatch(req);
            req.setErrorListener(new CommonErrorHandler(UpdateUsernameViewController.this));
            submitButton.setEnabled(false);
            showSubmitting();
        }
    };

    public HttpRequest.ResultListener<ModelResult<Void>> submitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            submitButton.setEnabled(true);
            hideSubmitting();
            if (data.isSuccess()) {
                UserModel user = UserManager.getInstance().getCurrentUser();
                if (user != null) {
                    String username = request.getAttachment(String.class);
                    user.setNickName(username);
                    UserManager.getInstance().saveUser(user);
                }
                if (getActivity() != null) {
                    getActivity().finish();
                }
                Toast.makeText(ContextProvider.getContext(), "修改昵称成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
