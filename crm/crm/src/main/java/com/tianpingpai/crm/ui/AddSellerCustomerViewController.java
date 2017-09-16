package com.tianpingpai.crm.ui;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@ActionBar(title = "添加卖家客户")
@Layout(id = R.layout.view_controller_add_seller_customer)
public class AddSellerCustomerViewController extends CrmBaseViewController {

    @Binding(id = R.id.check_container)
    private View checkContainer;
    @Binding(id = R.id.main_container)
    private View mainContainer;
    @Binding(id = R.id.check_phone_text_view)
    private EditText checkPhoneTextView;


    @Binding(id = R.id.customer_phone_text_view)
    private TextView customerPhoneTextView;
    @Binding(id = R.id.customer_name_edit_text)
    private EditText customerNameEditText;
    @Binding(id = R.id.store_name_edit_text)
    private EditText storeNameEditText;
    @Binding(id = R.id.store_address_edit_text)
    private EditText storeAddressEditText;
    @Binding(id = R.id.seller_at_market_text_view)
    private TextView sellerAtMarketTextView;
    @Binding(id = R.id.to_send_amount)
    private EditText toSendAmount;
    @Binding(id = R.id.distribution_market_container)
    private LinearLayout addMarketsContainer;

    private Model model;

    private ArrayList<Integer> toSendSelectIds = new ArrayList<>();
    private String toSendString="";

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

    }

    @OnClick(R.id.check_customer_button)
    private View.OnClickListener checkCustomerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            check();
        }
    };

    private void check() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = checkPhoneTextView.getText().toString();

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(ContextProvider.getContext(), R.string.invalid_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.Customer.check(), checkListener);
        req.addParam("phone", phone);
        req.addParam("user_type", "" + CustomerModel.USER_TYPE_SELLER);
        req.addParam("accessToken", user.getAccessToken());

        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        hideKeyboard();
        showLoading();
    }


    private HttpRequest.ResultListener<ModelResult<Model>> checkListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request,
                             ModelResult<Model> data) {
            hideLoading();
            if (!data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), "" + data.getDesc(), Toast.LENGTH_LONG).show();
                return;
            }

            Model cm = data.getModel();
            if (cm == null) {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
                return;
            }
            String phone = cm.getModel("customer").getString("phone");
            Log.e("xx", "phone=" + phone + "model:" + cm);
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(ContextProvider.getContext(), "该用户不存在,无法添加", Toast.LENGTH_LONG).show();
            } else {
                model = cm.getModel("customer");
                customerPhoneTextView.setText(phone);
                customerNameEditText.setText(model.getString("display_name"));
                storeNameEditText.setText(model.getString("sale_name"));
                storeAddressEditText.setText(model.getString("address"));
                toSendAmount.setText(model.getInt("minAmount")+"");
                checkContainer.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);


                sellerAtMarketTextView.setText(cm.getString("market"));
                List<Model> toSendMarkets = cm.getList("markets",Model.class);

                boolean isHave = false;
                for (Model m:toSendMarkets){
                    toSendString = toSendString + m.getInt("id") + ",";
                    toSendSelectIds.add(m.getInt("id"));
                    if(addMarketsContainer.getChildCount() < 5){
                        TextView tv = new TextView(getActivity());
                        tv.setPadding(30,0,0,0);
                        tv.setTextSize(14);
                        tv.setText(m.getString("name"));
                        addMarketsContainer.addView(tv);
                    }else{
                        if(!isHave){
                            TextView last = new TextView(getActivity());
                            last.setPadding(30, 0, 0,0);
                            last.setTextSize(14);
                            last.setText("......");
                            addMarketsContainer.addView(last);
                            isHave = true;
                        }
                    }
                }

                toSendString = toSendString.substring(0,toSendString.length()-1);
            }
        }
    };



    @OnClick(R.id.add_market_button)
    private View.OnClickListener toSendAllDistrictOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
			ActionSheet actionSheet = new ActionSheet();
            actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
            actionSheet.setHeight(getView().getHeight());
            SelectAreaViewController selectAreaViewController = new SelectAreaViewController();
            selectAreaViewController.setIsOnly(false);
            selectAreaViewController.setSelectedId(toSendSelectIds);
            selectAreaViewController.setActionSheet(actionSheet);
            selectAreaViewController.setActivity(getActivity());
            selectAreaViewController.setOnSelectAreaListener(onSelectAreaListener);
            actionSheet.setViewController(selectAreaViewController);
            actionSheet.show();

        }
    };

    private SelectAreaViewController.OnSelectAreaListener onSelectAreaListener = new SelectAreaViewController.OnSelectAreaListener(){

        @Override
        public void onSelectArea(HashMap<Integer ,String> hm) {
            boolean isHave = false;
            toSendString = "";
            toSendSelectIds.clear();
            addMarketsContainer.removeAllViews();

            for(Integer key:hm.keySet()){
                String s = hm.get(key);
                String []s1 = s.split("abc");
                toSendString = toSendString+s1[1]+",";
                if(addMarketsContainer.getChildCount() < 5){
                    TextView tv = new TextView(getActivity());
                    tv.setPadding(30,0,0,0);
                    tv.setTextSize(14);
                    tv.setText(s1[0]);
                    addMarketsContainer.addView(tv);
                }else{
                    if(!isHave){
                        TextView last = new TextView(getActivity());
                        last.setPadding(30, 0, 0,0);
                        last.setTextSize(14);
                        last.setText("......");
                        addMarketsContainer.addView(last);
                        isHave = true;
                    }
                }
                toSendSelectIds.add(Integer.parseInt(s1[1]));
            }
            if(toSendString.length()>0){
                toSendString = toSendString.substring(0,toSendString.length()-1);
            }
        }
    };

    @OnClick(R.id.submit_button)
    private View.OnClickListener submitOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            submit();
        }
    };

    private void submit(){
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("------------+", model.toString());
        if (model == null) {
            return;
        }

        String phone = customerPhoneTextView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getActivity(), "手机号不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerName = customerNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(customerName)) {
            Toast.makeText(getActivity(), "客户姓名不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        String storeName = storeNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(storeName)) {
            Toast.makeText(getActivity(), "商店名称不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        String storeAddress = storeAddressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(storeAddress)) {
            Toast.makeText(getActivity(), "商店地址不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountString = toSendAmount.getText().toString().trim();
        if(TextUtils.isEmpty(amountString)){
            Toast.makeText(getActivity(),"起送金额不能为空!",Toast.LENGTH_SHORT).show();
            return;
        }

        if("".equals(toSendString) || toSendString == null){
            Toast.makeText(getActivity(),"可配送商圈不能为空!",Toast.LENGTH_SHORT).show();
            return;
        }
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.Customer.add(), updateLister);
        req.setMethod(HttpRequest.POST);
        req.addParam("phone", phone);
        req.addParam("sale_address", storeAddress);
        req.addParam("display_name", customerName);
        req.addParam("sale_name", storeName);
        req.addParam("markets",toSendString);
        req.addParam("market_id",model.getInt("market_id")+"");
        req.addParam("user_type", 0 + "");
        req.addParam("is_register", "1");
        req.addParam("min_amount",amountString);
        req.addParam("user_id", model.getInt("user_id") + "");
        req.addParam("accessToken", user.getAccessToken());
        ModelParser<Void> parser = new ModelParser<>(Void.class);
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        showSubmitting();

    }

    private HttpRequest.ResultListener<ModelResult<Void>> updateLister = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request,
                             ModelResult<Void> data) {
            hideLoading();
            if (data.getCode() == 1) {
                UserManager.getInstance().loginExpired(getActivity());
            }
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), R.string.add_customer_success, Toast.LENGTH_SHORT).show();
                if (getActivity() != null)
                    getActivity().finish();
            }
        }
    };
}
