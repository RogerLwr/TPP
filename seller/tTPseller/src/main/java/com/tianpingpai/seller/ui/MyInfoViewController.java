package com.tianpingpai.seller.ui;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SelectCityViewController;

import java.util.List;

@SuppressWarnings("unused")
@ActionBar(title = "我的信息")
@Statistics(page = "我的信息")
@Layout(id = R.layout.ui_my_info)
public class MyInfoViewController extends BaseViewController {

    private int areaID;

    private SelectCategoriesViewController selectCategoriesViewController = new SelectCategoriesViewController();

    private final SelectCategoriesViewController.OnSelectedListener onSelectedListener = new SelectCategoriesViewController.OnSelectedListener() {
        @Override
        public void onSelected() {
            categoryET.setText(selectCategoriesViewController.getNameString());
            stringIds = selectCategoriesViewController.getIdString();
        }
    };

    @Binding(id = R.id.real_name_edit_text, format = "{{display_name}}")
    private EditText realNameEditText;
    @Binding(id = R.id.store_name_edit_text, format = "{{sale_name}}")
    private EditText saleNameEditText;
    @Binding(id = R.id.user_area_text_view, format = "{{area}}")
    private TextView userAreaTV;
    @Binding(id = R.id.address_edit_text, format = "{{address}}")
    private EditText addressET;
    @Binding(id = R.id.phone_edit_text, format = "{{phone}}")
    private EditText phoneEditText;
    @Binding(id = R.id.description_edit_text, format = "{{description}}")
    private EditText descriptionEditText;
    @Binding(id = R.id.et_freight, format = "{{freight}}")
    private EditText freightEditText;
    @Binding(id = R.id.et_startingPrice, format = "{{startingPrice}}")
    private EditText startingPriceEditText;
    @Binding(id = R.id.et_minAmount, format = "{{minAmount}}")
    private EditText minAmountEditText;
    @Binding(id = R.id.category_edit_text)
    private EditText categoryET;
//    @Binding(id = R.id.et_delivery_price)
//    private EditText etDeliveryPrice;
    @Binding(id = R.id.user_area_text_view)
    private TextView selectAreaButton;

    @Binding(id = R.id.support_deliver_button)
    private RadioButton supportDeliverButton;
    @Binding(id = R.id.support_deliver_false_0_button)
    private RadioButton supportDeliverFalse0Button;

    @Binding(id = R.id.business_radio_button)
    private RadioButton businessRadioButton;
    @Binding(id = R.id.rest_radio_button)
    private RadioButton restRadioButton;

    private String stringIds;

    @OnClick(R.id.submit_button)
    private View.OnClickListener onSubmitBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String realNameString = realNameEditText.getText().toString().trim();
            String saleNameStr = saleNameEditText.getText().toString().trim();
            String addressStr = addressET.getText().toString().trim();
//            String contact_way = phoneEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String shop_des = descriptionEditText.getText().toString().trim();
            freight = freightEditText.getText().toString().trim();
            startingPrice = startingPriceEditText.getText().toString().trim();
            minAmount = minAmountEditText.getText().toString().trim();
//            final String str_delivery_price = etDeliveryPrice.getText().toString().trim();

            if (TextUtils.isEmpty(realNameString)) {
                toast("姓名不能为空");
                return;
            }
            if (TextUtils.isEmpty(saleNameStr)) {
                toast("店铺名不能为空");
                return;
            }
            if (areaID == 0) {
                toast("您还未选择送货地址");
                return;
            }
            if (TextUtils.isEmpty(addressStr)) {
                toast("店铺地址不能为空");
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                toast("联系电话不能为空");
                return;
            }

            if (TextUtils.isEmpty(shop_des)) {
                toast("描述不能为空");
                return;
            }

            String categoryIds = stringIds;
            if (TextUtils.isEmpty(categoryIds)) {
                toast("请至少选择一个经营品类");
                return;
            }
            if (TextUtils.isEmpty(freight)) {
                toast("运费不能为空");
                return;
            }
            if (TextUtils.isEmpty(startingPrice)) {
                toast("起送费不能为空");
                return;
            }
            if (TextUtils.isEmpty(minAmount)) {
                toast("免运费配送金额不能为空");
                return;
            }

            if( Integer.parseInt(minAmount) < Integer.parseInt(startingPrice) ){
                toast("免运费配送金额不能小于起送费");
                return;
            }

//            int delivery_price = CommonUtil.DEFAULT_ARG;
//            try {
//                delivery_price = Integer.parseInt(str_delivery_price);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            //是否配送，0表示不配送，1表示配送
            int is_delivery = supportDeliverButton.isChecked() ? 1 : 0;

            int shopStatus = businessRadioButton.isChecked() ? CommonUtil.ShopStatus.shopBusiness : CommonUtil.ShopStatus.shopRest;

            saveSaleInfo(phone, null, null,
                    realNameString, saleNameStr, addressStr, areaID, categoryIds,
                    shop_des, is_delivery, 1, shopStatus);

        }
    };
    private View.OnClickListener onCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectCategoriesViewController.setActivity(getActivity());
            getActionSheet(true).setViewController(selectCategoriesViewController);
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).show();
        }
    };
    private String freight;
    private String startingPrice;
    private String minAmount;

    private void toast(String er) {
        Toast.makeText(ContextProvider.getContext(), er, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        selectCategoriesViewController.setOnSelectedListener(onSelectedListener);

        categoryET.setOnClickListener(onCategoryClickListener);
        selectAreaButton.setOnClickListener(selectRegionButtonListener);

        loadUserInfo();

    }

    private void loadUserInfo() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            String url = ContextProvider.getBaseURL() + "/api/user/getSaleUserDetailInfo.json";
            HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, userInfoListener);
            req.addParam("phone", UserManager.getInstance().getCurrentUser().getPhone());
            req.setParser(new GenericModelParser());
            VolleyDispatcher.getInstance().dispatch(req);
            showLoading();
        }
    }

    private HttpRequest.ResultListener<ModelResult<Model>> userInfoListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            Model m = data.getModel();
            if (data.isSuccess()) {
                getBinder().bindData(m);
                areaID = m.getInt("area_id");
                List<Model> categorys = m.getList("categorys", Model.class);
                String str_category = "";
                if(m.getBoolean("is_delivery")){
                    supportDeliverButton.setChecked(true);
                }else {
                    supportDeliverFalse0Button.setChecked(true);
                }
                if(m.getInt("rest") == CommonUtil.ShopStatus.shopBusiness){
                    businessRadioButton.setChecked(true);
                }else {
                    restRadioButton.setChecked(true);
                }
                stringIds = "";
                for (int j = 0; j < categorys.size(); j++) {
                    str_category = str_category + categorys.get(j).getString("name") + ",";
                    selectCategoriesViewController.select(categorys.get(j).getInt("category_id"));
                    stringIds += categorys.get(j).getInt("category_id") + ",";
                }
                if (str_category.length() > 1) {
                    categoryET.setText(str_category.substring(0, str_category.length() - 1));
                }
            }
            hideLoading();
        }
    };


    private View.OnClickListener selectRegionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard();
            ActionSheet actionSheet = getActionSheet(true);
            actionSheet.setHeight(DimensionUtil.dip2px(300));
            final SelectCityViewController selectCityViewController = new SelectCityViewController();
            selectCityViewController.setCityOnly(false);
            selectCityViewController.setActivity(getActivity());
            selectCityViewController.setActionSheet(actionSheet);
            actionSheet.setViewController(selectCityViewController);
            selectCityViewController.setOnSelectCityListener(new SelectCityViewController.OnSelectCityListener() {
                @Override
                public void onSelectCity(Model model) {
                    //TODO
                    String text = selectCityViewController.getSelectedCity().getString("name") + "-" +
                            model.getString("name");
                    selectAreaButton.setText(text);
                    areaID = model.getInt("area_id");
                }
            });
            actionSheet.show();
        }
    };

    /**
     * 保存卖家信息接口
     */
    private void saveSaleInfo(String phone, String deliver_phone, String password, String display_name, String sale_name,
                              String address, int area_id, String category_ids, String description, int is_delivery, int is_accept, int rest) {

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.SALER_UPDATE, saveListener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(false);
        req.setMethod(HttpRequest.POST);
        if (!TextUtils.isEmpty(phone)) {
            req.addParam("phone", phone);
        }
        if (!TextUtils.isEmpty(deliver_phone)) {
            req.addParam("deliver_phone", deliver_phone);
        }
        if (!TextUtils.isEmpty(password)) {
            req.addParam("password", password);
        }
        req.addParam("display_name", display_name);
        req.addParam("sale_name", sale_name);
        req.addParam("address", address);
        req.addParam("area_id", String.valueOf(area_id));
        req.addParam("category_ids", category_ids);
        req.addParam("freight", freight);
        req.addParam("startingPrice", startingPrice);
        req.addParam("minAmount", minAmount);
        req.addParam("description", description);
        req.addParam("is_delivery", is_delivery + "");
        req.addParam("is_accept", String.valueOf(is_accept));
        req.addParam("rest", String.valueOf(rest));

        req.setParser(parser);
        showLoading();
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> saveListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if (data.isSuccess()) {
                hideLoading();
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                UserManager.getInstance().notifyEvent(UserEvent.UserInfoUpdate, null);
                if(getActivity() != null){
                    getActivity().finish();
                }
            }
        }
    };
}
