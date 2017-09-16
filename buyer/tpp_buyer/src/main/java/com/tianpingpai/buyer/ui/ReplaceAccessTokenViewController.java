package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;

@ActionBar(title = "替换AccessToken",rightText = "保存")
@Layout(id = R.layout.ui_replace_access_token)
public class ReplaceAccessTokenViewController extends BaseViewController {

    @Binding(id = R.id.word_count_text_view)
    private TextView wordCountTextView;

    public interface OnInputCompleteListener{
        void onInputComplete(String text);
    }
    EditText phoneEditText;
    EditText editText;

    public void setOnInputCompleteListener(OnInputCompleteListener onInputCompleteListener) {
        this.onInputCompleteListener = onInputCompleteListener;
    }

    private OnInputCompleteListener onInputCompleteListener;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        phoneEditText = (EditText) rootView.findViewById(R.id.phone_edit_text);
        editText = (EditText) rootView.findViewById(R.id.edit_text);
        rootView.findViewById(R.id.ab_right_button).setOnClickListener(saveButtonListener);

    }

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel userModel = new UserModel();
            if(TextUtils.isEmpty(phoneEditText.getText().toString()) || TextUtils.isEmpty(editText.getText().toString())){
                Toast.makeText(ContextProvider.getContext(), "手机号和AccessToken都不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            userModel.setPhone(phoneEditText.getText().toString());
            userModel.setAccessToken(editText.getText().toString());
            UserManager.getInstance().saveUser(userModel);
            goHome();
        }
    };

    private void goHome(){
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
        getActivity().startActivity(intent);
    }

    private void loadUserInfo() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            String url = ContextProvider.getBaseURL() + "/api/user/getSaleUserDetailInfo.json";
            HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, userInfoListener);
            req.addParam("phone", UserManager.getInstance().getCurrentUser().getPhone());
            req.setParser(new GenericModelParser());
            VolleyDispatcher.getInstance().dispatch(req);
        }
    }

    private HttpRequest.ResultListener<ModelResult<Model>> userInfoListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            Model m = data.getModel();
            if (data.isSuccess()) {

            }
            hideLoading();
        }
    };

}
