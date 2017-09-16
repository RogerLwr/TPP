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
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.MobileUtil;
import com.tianpingpai.utils.TextValidator;

@SuppressWarnings("unused")
@ActionBar(title = "修改密码")
@Layout(id= R.layout.fragment_revise_password)
public class RevisePasswordViewController extends CrmBaseViewController{

    @Binding(id=R.id.old_password_edt)
    private EditText oldPasswordEdt;
    @Binding(id=R.id.new_password_edt1)
    private EditText newPasswordEdt1;
    @Binding(id=R.id.new_password_edt2)
    private EditText newPasswordEdt2;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();

    }
    @OnClick(R.id.confirmation_new_pw)
    private View.OnClickListener confirmationNewPasswordOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            String phone = UserManager.getInstance().getCurrentUser().getPhone();
            String oldPassword = oldPasswordEdt.getText().toString().trim();
            String password1 = newPasswordEdt1.getText().toString().trim();
            String password2 = newPasswordEdt2.getText().toString().trim();

            if(!MobileUtil.isPWDNum(oldPassword)){
                Toast.makeText(ContextProvider.getContext(), "请输入正确的旧密码！", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!TextValidator.isPasswordValid(password1)){
                Toast.makeText(ContextProvider.getContext(),"请输入6位以上的数字和字母组合！",Toast.LENGTH_SHORT).show();
                return;
            }

            if(!TextValidator.isPasswordValid(password2)){
                Toast.makeText(ContextProvider.getContext(),"请输入6位以上的数字和字母组合！",Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password1.equals(password2)){
                Toast.makeText(ContextProvider.getContext(),"两次新密码输入不一致！",Toast.LENGTH_SHORT).show();
                return;
            }

            //			HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/crm/marketer/resetPwd",submitListener);
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/crm/marketer/updatePwd",submitListener);
            req.setParser(new ModelParser<>(Void.class));
            req.setMethod(HttpRequest.POST);
            //			req.addParam("user_type", "0");
            //            Intent intent = new Intent();
            //            intent.putExtra(KEY_USER_NAME, userName);
            //            intent.putExtra(KEY_PASSWORD, password);
            //            req.setAttachment(intent);
            req.addParam("phone", phone);
            req.addParam("old_password", oldPassword);
            req.addParam("new_password", password2);
            //			req.addParam("password", password);//code
            //			req.addParam("verify_code", oldPWD);//code
            VolleyDispatcher.getInstance().dispatch(req);
        }
    };

    public HttpRequest.ResultListener<ModelResult<Void>> submitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if(data.isSuccess()){
                if(getActivity() != null){
                    Toast.makeText(ContextProvider.getContext(), "修改成功.", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK,request.getAttachment(Intent.class));
                    getActivity().finish();
                }
            }else{
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };
}
