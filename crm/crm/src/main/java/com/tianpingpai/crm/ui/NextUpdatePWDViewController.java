package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.TextValidator;

@SuppressWarnings("unused")
@ActionBar(title = "设置新密码")
@Layout(id = R.layout.view_controller_next_updata_pwd)
public class NextUpdatePWDViewController extends BaseViewController{

    private String userName;
    private String code;
    private String accessToken;

    @Binding(id = R.id.new_password_edit_text)
    private EditText passwordEditText;
    @Binding(id = R.id.reconfirm_password_edit_text)
    private EditText reconfirmPasswordEditText;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        userName = getActivity().getIntent().getStringExtra(UpdatePasswordViewController.KEY_USER_NAME);
        code = getActivity().getIntent().getStringExtra(UpdatePasswordViewController.KEY_CODE);
        accessToken = getActivity().getIntent().getStringExtra(UpdatePasswordViewController.KEY_ACCESS_TOKEN);
    }

    @OnClick(R.id.login_button)
    private View.OnClickListener submitButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            String password = passwordEditText.getText().toString().trim();
            String reconfirmPassword = reconfirmPasswordEditText.getText().toString().trim();
            if(!TextValidator.isPasswordValid(password)){
                Toast.makeText(ContextProvider.getContext(), "请输入新密码!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!TextValidator.isPasswordValid(reconfirmPassword)){
                Toast.makeText(ContextProvider.getContext(), "请再次输入新密码!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!password.equals(reconfirmPassword)){
                Toast.makeText(ContextProvider.getContext(), "两次输入密码不一致!", Toast.LENGTH_SHORT).show();
                return;
            }

            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/crm/marketer/update_password",submitListener);
            req.setParser(new ModelParser<>(Void.class));
            req.setMethod(HttpRequest.POST);
            Intent intent = new Intent();
            intent.putExtra(LoginViewController.KEY_USER_NAME, userName);
            intent.putExtra(LoginViewController.KEY_PASSWORD, password);
            req.setAttachment(intent);
            req.addParam("phone", userName);
            req.addParam("verify_code", code);
            req.addParam("password", password);
            req.addParam("token",accessToken);
            VolleyDispatcher.getInstance().dispatch(req);
        }
    };

    public HttpRequest.ResultListener<ModelResult<Void>> submitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if(data.isSuccess()){
                if(getActivity() != null){
                    Toast.makeText(ContextProvider.getContext(), "修改成功,请登录.", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK,request.getAttachment(Intent.class));
                    getActivity().finish();
                }
            }else{
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };
}
