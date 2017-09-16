package com.tianpingpai.buyer.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.MobileUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.TextValidator;

@SuppressWarnings("unused")
@Statistics(page = "找回密码")
@ActionBar(layout = R.layout.ab_title_green,title = "找回密码")
@Layout(id = R.layout.ui_retrieve_password)
public class RetrievePasswordViewController extends BaseViewController {

    public static final String KEY_USER_NAME = "key.username";
    public static final String KEY_PASSWORD = "key.password";

    @Binding(id = R.id.phone_edit_text)
    private EditText phoneEditText;
    @Binding(id = R.id.new_password_edit_text)
    private EditText newPasswordEditText;
    @Binding(id = R.id.validation_code_edit_text)
    private EditText validationCodeEditText;
    @Binding(id = R.id.get_validation_code_button)
    private TextView getValidationCodeButton;

    private Handler handler = new Handler();
    private long counterStartTime = 0;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        phoneEditText.requestFocus();
        showContent();
    }

    @OnClick(R.id.get_validation_code_button)
    private View.OnClickListener getValidationCodeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String userName = phoneEditText.getText().toString().trim();
            if(!MobileUtil.isMobileNumber(userName)){
                Toast.makeText(ContextProvider.getContext(),"请填写正确手机号码!",Toast.LENGTH_SHORT).show();
                return;
            }
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLUtil.VALIDATE_URL,getValidationListener);
            req.addParam("phone", userName);
            req.addParam("user_type",UserModel.USER_TYPE_BUYER + "");
            req.addParam("action_type","2");//1注册行为2忘记密码行为
            req.setParser(new ModelParser<>(Void.class));
            VolleyDispatcher.getInstance().dispatch(req);
        }
    };

    private HttpRequest.ResultListener<ModelResult<Void>> getValidationListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if(data.isSuccess()){
                startCounter();
            }else{
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void startCounter(){
        handler.removeCallbacks(counterRun);
        counterStartTime = System.currentTimeMillis();
        handler.post(counterRun);
        getValidationCodeButton.setEnabled(false);
    }

    private Runnable counterRun = new Runnable() {
        @Override
        public void run() {
            int secondsPassed = (int) ((System.currentTimeMillis() - counterStartTime) / 1000);
            secondsPassed = 60 - secondsPassed;
            if(secondsPassed < 0){//TODO define a constant
                handler.removeCallbacks(this);
                getValidationCodeButton.setEnabled(true);
            }else{
                handler.postDelayed(this,1000);
                getValidationCodeButton.setText(String.format("重新获取(%d)", secondsPassed));
            }
        }
    };

    @OnClick(R.id.submit_button)
    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String userName = phoneEditText.getText().toString().trim();
            String password = newPasswordEditText.getText().toString().trim();
            String validationCode = validationCodeEditText.getText().toString();
            if (!TextValidator.isPasswordValid(password)) {
                Toast.makeText(ContextProvider.getContext(), "请填写6位以上的密码！", Toast.LENGTH_SHORT).show();
                Toast.makeText(ContextProvider.getContext(), "必须包含数字和字母！！", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(validationCode)){
                Toast.makeText(ContextProvider.getContext(),"请输入正确的验证码！",Toast.LENGTH_SHORT).show();
                return;
            }
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLUtil.FORGET_URL,submitListener);
            req.setParser(new ModelParser<>(Void.class));
            req.setMethod(HttpRequest.POST);
            req.addParam("user_type", UserModel.USER_TYPE_BUYER + "");
            Intent intent = new Intent();
            intent.putExtra(KEY_USER_NAME,userName);
            intent.putExtra(KEY_PASSWORD,password);
            req.setAttachment(intent);
            req.addParam("phone", userName);
            req.addParam("password", password);
            req.addParam("verify_code", validationCode);
            VolleyDispatcher.getInstance().dispatch(req);
            showSubmitting();
        }
    };

    public HttpRequest.ResultListener<ModelResult<Void>> submitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            hideSubmitting();
            if(data.isSuccess()){
                if(getActivity() != null){
                    Toast.makeText(ContextProvider.getContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK,request.getAttachment(Intent.class));
                    getActivity().finish();
                }
            }else{
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };
}
