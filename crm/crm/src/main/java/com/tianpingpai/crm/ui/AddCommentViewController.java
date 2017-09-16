package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.location.LocationEvent;
import com.tianpingpai.location.LocationManager;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unused")
@Statistics(page = "添加拜访")
@ActionBar(titleRes = R.string.title_add_comment)
@Layout(id = R.layout.fragment_add_comment)
public class AddCommentViewController extends CrmBaseViewController{

    private LocationModel location;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss", Locale.CHINA);

    @Binding(id = R.id.content_edit_text)
    private EditText contentEditText;
    @Binding(id = R.id.address_text_view)
    private TextView addressTextView;

    @Binding(id = R.id.visit_alone_button)
    private RadioButton visitAloneButton;

    private ModelStatusListener<LocationEvent, LocationModel> locationListener = new ModelStatusListener<LocationEvent, LocationModel>() {
        @Override
        public void onModelEvent(LocationEvent event, LocationModel model) {
            location = model;
            String address = "地址:" + model.getAddress();
            Log.e("定位地址=====",address);
            addressTextView.setText(address);
            LocationManager.getInstance().stop();

        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        CustomerModel customer = (CustomerModel) getActivity().getIntent().getSerializableExtra(CustomerDetailViewController.KEY_CUSTOMER);
        TextView nameTextView = (TextView) rootView.findViewById(R.id.name_text_view);
        TextView dateTextView = (TextView) rootView.findViewById(R.id.date_text_view);

        dateTextView.setText(dateFormat.format(new Date()));
        nameTextView.setText(customer.getDisplayName());
        showContent();
        LocationManager.getInstance().registerListener(locationListener);
        LocationManager.getInstance().start();
        visitAloneButton.setChecked(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocationManager.getInstance().unregisterListener(locationListener);
        LocationManager.getInstance().stop();
    }

    private ResultListener<ModelResult<Void>> listener = new ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request,
                             ModelResult<Void> data) {
            hideLoading();
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), R.string.add_comment_success, Toast.LENGTH_SHORT).show();
                if(getActivity() != null){
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
//                    JsonModelManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, new Model());
                    ModelManager.getModelInstance().notifyEvent(ModelEvent.OnModelUpdate,new Model());
                }
            } else {
                Toast.makeText(getActivity(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @OnClick(R.id.submit_button)
    private OnClickListener submitButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
                return;
            }

            final String content = contentEditText.getText().toString();
            if(TextUtils.isEmpty(content)){
                Toast.makeText(ContextProvider.getContext(), "内空为空，不能提交!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (location == null) {
                Toast.makeText(ContextProvider.getContext(), "没有位置信息，不能提交!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(location.getAddress())){
                Toast.makeText(ContextProvider.getContext(), "没有位置信息，请查看你的定位权限是否开启!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!location.isValid()){
                Toast.makeText(ContextProvider.getContext(),"无效的位置信息，不能提交!",Toast.LENGTH_SHORT).show();
                return;
            }

            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActivity(getActivity());
            dialog.setActionSheet(getActionSheet(true));
            dialog.setTitle("确定提交?");
            dialog.setPositiveButtonListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    submit(content);
                    dialog.dismiss();
                    Log.e("yitijiao", "ok");
                }
            });
            dialog.show();
        }
    };

    private void submit(String content){
        CustomerModel customer = (CustomerModel) getActivity().getIntent().getSerializableExtra(CustomerDetailViewController.KEY_CUSTOMER);
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.Customer.addComment(), listener);
        req.setMethod(HttpRequest.POST);
        CommonErrorHandler handler = new CommonErrorHandler(AddCommentViewController.this);
        req.setErrorListener(handler);
        req.addParam("content", content);
        req.addParam("customer_id", customer.getId() + "");


        int type = visitAloneButton.isChecked() ? 1 : 2;
        req.addParam("type",""+type);
        req.addParam("lat", location.getLatitude() + "");
        req.addParam("lng", location.getLongitude() + "");
        req.addParam("position", location.getAddress());
        req.addParam("phoneType", "android");
        req.setParser(new ModelParser<>(Void.class));

        ModelParser<Void> parser = new ModelParser<>(Void.class);
        req.setParser(parser);
        showLoading();
        VolleyDispatcher.getInstance().dispatch(req);
    }

    @OnClick(R.id.address_text_view)
    private OnClickListener addressButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(location == null){
                Toast.makeText(ContextProvider.getContext(),"正在定位..",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SignInViewController.class);
            intent.putExtra(SignInViewController.KEY_LOCATION, location);
            getActivity().startActivity(intent);
        }
    };
}
