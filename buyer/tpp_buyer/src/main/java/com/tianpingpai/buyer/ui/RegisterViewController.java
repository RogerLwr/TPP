package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.MobileUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.location.LocationEvent;
import com.tianpingpai.location.LocationManager;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.utils.TextValidator;
import com.tianpingpai.widget.SelectCityViewController;

@SuppressWarnings("unused")
@Statistics(page = "注册")
@ActionBar(title = "注册")
@Layout(id = R.layout.vc_register)
public class RegisterViewController extends BaseViewController {

    private Model city;

    @Binding(id = R.id.first_step_container)
    private View firstStepContainer;
    @Binding(id = R.id.city_text_view)
    private TextView cityTextView;
    @Binding(id = R.id.phone_edit_text)
    private EditText phoneEditText;
    @Binding(id = R.id.validation_code_edit_text)
    private EditText validationCodeEditText;
    @Binding(id = R.id.get_validation_code_button)
    private TextView getValidationCodeButton;

    @Binding(id = R.id.second_step_container)
    private View secondStepContainer;
    @Binding(id = R.id.customer_name_edit_text)
    private EditText customerNameEditText;
    @Binding(id = R.id.store_name_edit_text)
    private EditText storeNameEditText;
    @Binding(id = R.id.password_edit_text)
    private EditText passwordEditText;
    @Binding(id = R.id.password_repeat_edit_text)
    private EditText passwordRepeatEditText;
    @Binding(id = R.id.agreement_check_box)
    private CheckBox agreementCheckBox;
    @Binding(id = R.id.agreement_button)
    private TextView agreementButton;
    @Binding(id = R.id.service_line_text_view)
    private TextView serviceLineTextView;
    @Binding(id = R.id.submit_button)
    private View submitButton;

    private Handler handler = new Handler();
    private long counterStartTime = 0;
    private LocationManager locationManager = new LocationManager();
    private ListResult<Model> cities;

    private String validationToken;
    private ModelStatusListener<UserEvent, UserModel> loginListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if (event == UserEvent.Login) {
                Toast.makeText(ContextProvider.getContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK, null);//TODO
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, WelcomeViewController.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }
    };
    private ModelStatusListener<LocationEvent, LocationModel> locationListener = new ModelStatusListener<LocationEvent, LocationModel>() {
        @Override
        public void onModelEvent(LocationEvent event, LocationModel model) {
            if(model != null){
                handleCity();
            }
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        cityTextView.setText(getActivity().getResources().getString(R.string.current_city, ""));
        UserManager.getInstance().registerListener(loginListener);
        locationManager.registerListener(locationListener);
        loadCities();
        agreementButton.setText(Html.fromHtml("我已阅读，并同意遵守<u>天平派用户协议</u>"));
        serviceLineTextView.setText(Html.fromHtml("<u>" + ContextProvider.getContext().getString(R.string.service_line) + "<u>"));
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(loginListener);
    }

    private void loadCities(){
        String url = URLUtil.ADDRESS_URL;
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, addressesListener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(false);
        req.setParser(parser);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> addressesListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if (data.isSuccess()) {
                cities = data;
                handleCity();
            }
        }
    };

    private void handleCity(){
        LocationModel lastLocation = LocationManager.getLastLocation();
        if(lastLocation != null){
            if(cities != null){
                for(Model city:cities.getModels()){
                    if(city.getString("name").equals(lastLocation.getCityName())){
                        setCity(city);
                    }
                }
            }
        }else{
            locationManager.requestLocation(5000);
        }
    }

    private void setCity(Model city){
        this.city = city;
        cityTextView.setText(getActivity().getResources().getString(R.string.current_city, city.get("name")));
    }

    @OnClick(R.id.service_line_text_view)
    private View.OnClickListener contactButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + ContextProvider.getContext().getString(R.string.service_line)));
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.select_city_button)
    private View.OnClickListener selectCityButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet actionSheet = getActionSheet(true);
            actionSheet.setHeight(DimensionUtil.dip2px(300));
            SelectCityViewController selectCityViewController = new SelectCityViewController();
            selectCityViewController.setActivity(getActivity());
            selectCityViewController.setCityOnly(true);
            selectCityViewController.setActionSheet(actionSheet);
            selectCityViewController.setCityResult(cities);
            actionSheet.setViewController(selectCityViewController);
            selectCityViewController.setOnSelectCityListener(new SelectCityViewController.OnSelectCityListener() {
                @Override
                public void onSelectCity(Model model) {
                    setCity(model);
                }
            });
            actionSheet.show();
        }
    };

    @OnClick(R.id.next_step_button)
    private View.OnClickListener nextStepButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (city == null) {
                Toast.makeText(ContextProvider.getContext(), "城市不能为空！", Toast.LENGTH_LONG).show();
                return;
            }

            if (!TextValidator.isMobileNumber(phoneEditText.getText())) {
                Toast.makeText(ContextProvider.getContext(), "请输入有效的手机号！！", Toast.LENGTH_LONG).show();
                return;
            }

            if (validationCodeEditText.length() < 4) {
                Toast.makeText(ContextProvider.getContext(), "请输入有效的验证码！！", Toast.LENGTH_LONG).show();
                return;
            }
            hideKeyboard();
            validateFirstStep();
        }
    };

    private void showFirstStep() {
        firstStepContainer.setVisibility(View.VISIBLE);
        secondStepContainer.setVisibility(View.GONE);
    }

    private void showSecondStep() {
        secondStepContainer.setVisibility(View.VISIBLE);
        firstStepContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.get_validation_code_button)
    private View.OnClickListener getValidationCodeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String userName = phoneEditText.getText().toString().trim();
            if (!MobileUtil.isMobileNumber(userName)) {
                Toast.makeText(ContextProvider.getContext(), "请填写正确手机号码!", Toast.LENGTH_SHORT).show();
                return;
            }
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLUtil.VALIDATE_URL, getValidationListener);
            req.addParam("phone", userName);
            req.setParser(new ModelParser<>(Void.class));
            req.addParam("action_type", "1");//1注册行为2忘记密码行为
            req.addParam("user_type",UserModel.USER_TYPE_BUYER + "");
            req.setErrorListener(new HttpRequest.ErrorListener() {
                @Override
                public void onError(HttpRequest<?> request, HttpError eror) {
                    getValidationCodeButton.setEnabled(true);
                }
            });
            VolleyDispatcher.getInstance().dispatch(req);
            getValidationCodeButton.setEnabled(false);
        }
    };

    private HttpRequest.ResultListener<ModelResult<Void>> getValidationListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            getValidationCodeButton.setEnabled(true);
            startCounter();
            if (!data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void startCounter() {
        handler.removeCallbacks(counterRun);
        counterStartTime = System.currentTimeMillis();
        handler.post(counterRun);
        getValidationCodeButton.setEnabled(false);
        getValidationCodeButton.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_66));
    }

    private Runnable counterRun = new Runnable() {
        @Override
        public void run() {
            int secondsPassed = (int) ((System.currentTimeMillis() - counterStartTime) / 1000);
            secondsPassed = 60 - secondsPassed;
            if (secondsPassed < 0) {//TODO define a constant
                handler.removeCallbacks(this);
                getValidationCodeButton.setEnabled(true);
                getValidationCodeButton.setText("重新获取");
                getValidationCodeButton.setTextColor(getActivity().getResources().getColor(R.color.green));
            } else {
                handler.postDelayed(this, 1000);
                String text = "重新获取(" + secondsPassed + ")";
                getValidationCodeButton.setText(text);
                getValidationCodeButton.setTextColor(getActivity().getResources().getColor(R.color.gray_66));
            }
        }
    };

    @OnClick(R.id.agreement_button)
    private View.OnClickListener agreementButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, AgreementViewController.class);
            getActivity().startActivity(i);
        }
    };

    @OnClick(R.id.submit_button)
    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!passwordEditText.getText().toString().equals(passwordRepeatEditText.getText().toString())) {
                Toast.makeText(ContextProvider.getContext(), "两次输入的密码不一致！！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!agreementCheckBox.isChecked()){
                Toast.makeText(ContextProvider.getContext(), "同意协议才能注册！", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String validationCode = validationCodeEditText.getText().toString();
            String username = phoneEditText.getText().toString();
            String storeName = storeNameEditText.getText().toString();
            String customerName = customerNameEditText.getText().toString().trim();

            if(password.length() < 6){
                Toast.makeText(ContextProvider.getContext(), "请填写6位以上的密码！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextValidator.isPasswordValid(password)) {
                Toast.makeText(ContextProvider.getContext(), "必须包含数字和字母！！", Toast.LENGTH_SHORT).show();
                return;
            }


            if (TextUtils.isEmpty(validationCode)) {
                Toast.makeText(ContextProvider.getContext(), "请输入正确的验证码！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(storeName)) {
                Toast.makeText(ContextProvider.getContext(), "饭店名称不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(customerName)){
                Toast.makeText(ContextProvider.getContext(),"姓名不能为空!",Toast.LENGTH_SHORT).show();
                return;
            }

            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLUtil.REGISTER_URL, submitListener);
            req.setParser(new ModelParser<>(Void.class));
            req.setMethod(HttpRequest.POST);
            req.addParam("user_type", UserModel.USER_TYPE_BUYER + "");
            req.addParam("phone", phone);
            req.addParam("password", password);
            req.addParam("verify_code", validationCode);
            req.addParam("username", "");
            req.addParam("token", validationToken);
            req.addParam("storeName", storeName);
            req.addParam("display_name",customerName);
            req.addParam("areaId", city.getInt("area_id") + "");
            req.setErrorListener(new HttpRequest.ErrorListener() {
                @Override
                public void onError(HttpRequest<?> request, HttpError error) {
                    submitButton.setEnabled(true);
                }
            });
            Intent intent = new Intent();
            intent.putExtra(RetrievePasswordViewController.KEY_USER_NAME, phone);
            intent.putExtra(RetrievePasswordViewController.KEY_PASSWORD, password);
            req.setAttachment(intent);
            VolleyDispatcher.getInstance().dispatch(req);
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
                if (getActivity() != null) {
                    UserManager.getInstance().login(request.getParams().get("phone"), request.getParams().get("password"));
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private HttpRequest.ResultListener<ModelResult<String>> validateFirstStepListener = new HttpRequest.ResultListener<ModelResult<String>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<String>> request, ModelResult<String> data) {
            if (data.isSuccess() && !TextUtils.isEmpty(data.getModel())) {
                validationToken = data.getModel();
                showSecondStep();
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private void validateFirstStep() {
        String url = ContextProvider.getBaseURL() + "/api/verify/check";
        HttpRequest<ModelResult<String>> req = new HttpRequest<>(url, validateFirstStepListener);
        req.setMethod(HttpRequest.POST);
        req.addParam("phone", phoneEditText.getText().toString());
        req.addParam("code", validationCodeEditText.getText().toString());
        req.setParser(new ModelParser<>(String.class));
        VolleyDispatcher.getInstance().dispatch(req);
    }
}
