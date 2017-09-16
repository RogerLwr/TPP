package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.parser.ParserDescNoResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.MobileUtil;

@SuppressWarnings("unused")
@ActionBar(title = "验证手机号")
@Layout(id = R.layout.fragment_update_pwd)
public class UpdatePasswordViewController extends BaseViewController {

    public static final int REQUEST_CODE_RETRIEVE_PASSWORD = 104;

    public static final String KEY_USER_NAME = "Key.username";
    public static final String KEY_CODE = "Key.code";
    public static final String KEY_ACCESS_TOKEN = "Key.accessToken";

    @Binding(id = R.id.username_edit_text)
    private EditText usernameEditText;
    @Binding(id = R.id.verify_code_edit_text)
    private EditText verifyCodeEditText;
    @Binding(id = R.id.get_verify_code_text)
    private TextView getVerifyCodeTextView;

    private Handler handler = new Handler();
    private long counterStartTime = 0;

    private String netCode;

    @OnClick(R.id.get_verify_code_text)
    private View.OnClickListener getVerifyCodeTextClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            String userName = usernameEditText.getText().toString().trim();
			if(!MobileUtil.isMoblieNum(userName)){
				Toast.makeText(ContextProvider.getContext(), "请填写正确手机号码!", Toast.LENGTH_SHORT).show();
				return;
			}
			HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.getBaseUrl()+"/crm/marketer/send_verify_code", getValidationListener);
            req.setParser(new ParserDescNoResult());
			req.addParam("phone", userName);
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
        getVerifyCodeTextView.setEnabled(false);
    }

    private Runnable counterRun = new Runnable() {
        @Override
        public void run() {
            int secondsPassed = (int) ((System.currentTimeMillis() - counterStartTime) / 1000);
            secondsPassed = 60 - secondsPassed;
            if(secondsPassed < 0){//TODO define a constant
                handler.removeCallbacks(this);
                getVerifyCodeTextView.setEnabled(true);
                getVerifyCodeTextView.setText("重新获取");
            }else{
                handler.postDelayed(this,1000);
                getVerifyCodeTextView.setText(String.format("重新获取(%d)", secondsPassed ));
            }
        }
    };
    @OnClick(R.id.submit_button)
    private View.OnClickListener submitButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            String userName = usernameEditText.getText().toString().trim();
            String code = verifyCodeEditText.getText().toString().trim();

            if(!MobileUtil.isMoblieNum(userName)){
                Toast.makeText(ContextProvider.getContext(),"请填写手机号！",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(code)){
                Toast.makeText(ContextProvider.getContext(),"请填写验证码！",Toast.LENGTH_SHORT).show();
                return;
            }

            String url = URLApi.getBaseUrl()+"/api/verify/check";
            HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,checkListener);
            ParserDescNoResult parse = new ParserDescNoResult();
            req.setParser(parse);
            req.addParam("phone",userName);
            req.addParam("code", code);
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, NextUpdatePWDViewController.class);
            i.putExtra(KEY_USER_NAME, userName);
            i.putExtra(KEY_CODE, code);
            req.setAttachment(i);
            VolleyDispatcher.getInstance().dispatch(req);

        }
    };

    private HttpRequest.ResultListener<ModelResult<Model>> checkListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                String accessToken = data.getModel().getString("result");
                if("".equals(accessToken)||null==accessToken){
                    Toast.makeText(getActivity(),data.getDesc(),Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = request.getAttachment(Intent.class);
                    intent.putExtra(KEY_ACCESS_TOKEN,accessToken);
                    getActivity().startActivityForResult(intent,REQUEST_CODE_RETRIEVE_PASSWORD);
                }
            }
        }
    };

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_RETRIEVE_PASSWORD && resultCode == Activity.RESULT_OK && data != null){
            getActivity().setResult(Activity.RESULT_OK,data);
            getActivity().finish();
        }
    }
}
