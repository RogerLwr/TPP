package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.PhotoAdapter;
import com.tianpingpai.crm.photo.PickOrTakeImageActivity;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.ContractModel;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.widget.FixedGridView;

import java.util.ArrayList;

@SuppressWarnings("unused")
@ActionBar(title = "上传资料")
@Layout(id = R.layout.view_controller_contract_update)
public class UploadContractViewController extends BaseViewController {

    public final static String KEY_IS_CHECK_OUT = "key.is_check";

    public final static String KEY_CONTRACT_ITEM_MODEL = "key.contract_item_model";

    private CustomerModel customerModel;
    private ContractModel contractModel;

    private boolean isCheck = false;

    @Binding(id = R.id.contract_id_edt)
    private EditText contractIdEdt;
    @Binding(id = R.id.business_license_name_edt)
    private EditText businessLicenseNameEdt;
    @Binding(id = R.id.shop_name_edt)
    private EditText shopNameEdt;
    @Binding(id = R.id.legal_name_edt)
    private EditText legalNameEdt;
    @Binding(id = R.id.legal_ID_number_edt)
    private EditText legalIDNumberEdt;
    @Binding(id = R.id.linkman_phone_edt)
    private EditText linkmanPhoneeEdt;
    @Binding(id = R.id.city_edt)
    private EditText cityEdt;

    @Binding(id = R.id.legal_id_gridview)
    private FixedGridView legalIdGridView;
    @Binding(id= R.id.contract_gridview)
    private FixedGridView contractGridView;

    private PhotoAdapter legalIdAdapter = new PhotoAdapter();
    private PhotoAdapter contractAdapter = new PhotoAdapter();

    @Binding(id = R.id.legal_id_image_delete_btn)
    private Button legalIdImageDeleteBtn;

    @Binding(id = R.id.contract_image_delete_btn)
    private Button contractImageDeleteBtn;


    @Binding(id = R.id.submit_btn)
    private Button submitBtn;

    @Binding(id = R.id.up_view)
    private View upview;

    @Binding(id = R.id.loading_image_view)
    private ImageView loadImage;


    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        customerModel = (CustomerModel) getActivity().getIntent().getSerializableExtra(ContractStatusViewController.KEY_CUSTOMER);
//        contractModel = (ContractModel)getActivity().getIntent().getParcelableExtra(KEY_CONTRACT_ITEM_MODEL);
        contractModel = (ContractModel)getActivity().getIntent().getSerializableExtra(KEY_CONTRACT_ITEM_MODEL);
        isCheck = getActivity().getIntent().getBooleanExtra(KEY_IS_CHECK_OUT,false);

        if(isCheck){
            int status = contractModel.getStatus();
            switch (status){
                case 0:
                    legalIdImageDeleteBtn.setVisibility(View.GONE);
                    contractImageDeleteBtn.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                    setNoEdit();
                    break;
                case 1:
                    legalIdImageDeleteBtn.setVisibility(View.GONE);
                    contractImageDeleteBtn.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                    setNoEdit();
                    break;
                case 2:
                    break;
            }
//            contractId = getActivity().getIntent().getIntExtra(KEY_CONTRACT_ID,0);
//            int contractId = contractModel.getInt("id");
            int contractId = contractModel.getId();
            loadDate(contractId);

            legalIdAdapter.setQiNiuPath("/contract/");
            legalIdAdapter.setUpView(upview, loadImage);
            legalIdAdapter.setCustomerModel(customerModel);
            legalIdAdapter.setActivity(getActivity());
            legalIdAdapter.setNumber(2);
            legalIdAdapter.setGridView(legalIdGridView);
            legalIdAdapter.setIsNetPicture(true);
            legalIdAdapter.setStatus(2111);//TODO
            legalIdGridView.setAdapter(legalIdAdapter);

            contractAdapter.setQiNiuPath("/contract/");
            contractAdapter.setUpView(upview, loadImage);
            contractAdapter.setCustomerModel(customerModel);
            contractAdapter.setActivity(getActivity());
            contractAdapter.setGridView(contractGridView);
            contractAdapter.setNumber(3);
            contractAdapter.setIsNetPicture(true);
            contractAdapter.setStatus(2222);//TODO
            contractGridView.setAdapter(contractAdapter);
        }else{

            legalIdImageDeleteBtn.setVisibility(View.GONE);
            contractImageDeleteBtn.setVisibility(View.GONE);

            legalIdAdapter.setQiNiuPath("/contract/");
            legalIdAdapter.setUpView(upview, loadImage);
            legalIdAdapter.setCustomerModel(customerModel);
            legalIdAdapter.setActivity(getActivity());
            legalIdAdapter.setGridView(legalIdGridView);
            legalIdAdapter.setNumber(2);
            legalIdAdapter.setStatus(2111);//TODO
            legalIdGridView.setAdapter(legalIdAdapter);

            contractAdapter.setQiNiuPath("/contract/");
            contractAdapter.setUpView(upview,loadImage);
            contractAdapter.setCustomerModel(customerModel);
            contractAdapter.setActivity(getActivity());
            contractAdapter.setGridView(contractGridView);
            contractAdapter.setNumber(3);
            contractAdapter.setStatus(2222);//TODO
            contractGridView.setAdapter(contractAdapter);
        }


    }

    private void setNoEdit(){
        contractIdEdt.setEnabled(false);
        contractIdEdt.clearFocus();
        businessLicenseNameEdt.setEnabled(false);
        businessLicenseNameEdt.clearFocus();
        shopNameEdt.setEnabled(false);
        shopNameEdt.clearFocus();
        legalNameEdt.setEnabled(false);
        legalNameEdt.clearFocus();
        legalIDNumberEdt.setEnabled(false);
        legalIDNumberEdt.clearFocus();
        linkmanPhoneeEdt.setEnabled(false);
        linkmanPhoneeEdt.clearFocus();
        cityEdt.setEnabled(false);
        cityEdt.clearFocus();

        hideKeyboard();
    }

    public void loadDate(int id){

        String url = URLApi.getBaseUrl() + "/crm/customer/get_contract_detail";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,datelistener);
        req.setParser(new GenericModelParser());
        req.addParam("id",""+id);
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();

    }

    Model model;
    private HttpRequest.ResultListener<ModelResult<Model>> datelistener = new HttpRequest.ResultListener<ModelResult<Model>>(){
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){

                model = data.getModel();
                Log.e("xx", "225------model=" + model.toString());
                if(data.getModel()!=null){
                    setDate(model);
                }
                hideLoading();
            }
        }
    };

    private void setDate(Model m){
        contractIdEdt.setText(m.getString("contract_id"));
        businessLicenseNameEdt.setText(m.getString("license_name"));
        shopNameEdt.setText(m.getString("shop_name"));
        legalNameEdt.setText(m.getString("legal_name"));
        legalIDNumberEdt.setText(m.getString("legal_idcard"));
        linkmanPhoneeEdt.setText(m.getString("phone"));
        cityEdt.setText(m.getString("address"));
        ArrayList<String> legalidcardlist = (ArrayList<String>)m.getList("legal_idcard_pictures",String.class);
        ArrayList<String> contractlist = (ArrayList<String>)m.getList("contract_pictures",String.class);


        legalIdAdapter.setNetDate(legalidcardlist);
        contractAdapter.setNetDate(contractlist);

    }


    //提交按钮
    @OnClick(R.id.submit_btn)
    private View.OnClickListener submitOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            submit();
        }
    };

    private UserModel user;
    public void submit(){
        user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
            return;
        }
//        payee_name_edt,ID_number_edt,contract_id_edt,bank_card_number_edt,opening_bank_edt
        if(TextUtils.isEmpty(contractIdEdt.getText().toString())){
            Toast.makeText(getActivity(),"合同编号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(businessLicenseNameEdt.getText().toString())){
            Toast.makeText(getActivity(),"营业执照名称不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(shopNameEdt.getText().toString())){
            Toast.makeText(getActivity(),"店铺名称不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(legalNameEdt.getText().toString())){
            Toast.makeText(getActivity(),"法人姓名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(legalIDNumberEdt.getText().toString())){
            Toast.makeText(getActivity(),"法人身份证号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(linkmanPhoneeEdt.getText().toString())){
            Toast.makeText(getActivity(),"联系人手机号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(cityEdt.getText().toString())){
            Toast.makeText(getActivity(),"所在城市不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> legalIdPictures = legalIdAdapter.getUpLoadOkDate();
        if(legalIdPictures.size()<=1){
            Toast.makeText(getActivity(), "请上传身份证图片", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> contractPictures = contractAdapter.getUpLoadOkDate();
        if(contractPictures.size()==0){
            Toast.makeText(getActivity(), "请上传合同图片", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        submitTotal();
    }

    private void submitTotal(){
        //接口
        String url = URLApi.getBaseUrl()+"/crm/customer/edit_contract";
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, submitListener);
        req.setMethod(HttpRequest.POST);
        if(isCheck){
            req.addParam("id", "" + contractModel.getId());
            req.addParam("user_id", "" + customerModel.getUserId());
        }else{
            req.addParam("user_id", "" +customerModel.getUserId());
        }
        req.addParam("contract_id",contractIdEdt.getText().toString().trim());
        req.addParam("license_name",businessLicenseNameEdt.getText().toString().trim());
        req.addParam("shop_name",shopNameEdt.getText().toString().trim());
        req.addParam("legal_name",legalNameEdt.getText().toString().trim());
        req.addParam("legal_idcard",legalIDNumberEdt.getText().toString().trim());
        req.addParam("phone",linkmanPhoneeEdt.getText().toString().trim());
        req.addParam("address",cityEdt.getText().toString().trim());
        req.addParam("legal_idcard_pictures",legalIdAdapter.getUpLoadKey());
        req.addParam("contract_pictures",contractAdapter.getUpLoadKey());
        req.addParam("accessToken", user.getAccessToken());
        ModelParser<Void> parser = new ModelParser<>(Void.class);
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        submitBtn.setClickable(false);
    }


    private HttpRequest.ResultListener<ModelResult<Void>> submitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {

        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            submitBtn.setClickable(true);
            if (data.isSuccess()) {
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), R.string.add_customer_success, Toast.LENGTH_SHORT).show();
                if (getActivity() != null)
                    getActivity().finish();
                ModelManager.getModelInstance().notifyEvent(ModelEvent.OnModelUpdate, new Model());
            }else{

                hideLoading();
                Log.e("提交信息失败",data.getCode()+data.getDesc());
                Toast.makeText(getActivity(),"提交信息失败"+data.getCode()+data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);

        if (resultCode == PickOrTakeImageActivity.RESULT_OK) {
            Bundle b = data.getExtras();
//            String path = b.getString("data");
            ArrayList<String> list = b.getStringArrayList("data");
            if(null==list||list.size()==0){
                return;
            }
            String path = list.get(0);
            switch (requestCode){
                //TODO
                case 2111:
                    legalIdAdapter.setDate(list,true);
                    break;
                case 2222:
                    contractAdapter.setDate(list,true);
                    break;
            }
        }
    }

    @OnClick(R.id.legal_id_image_delete_btn)
    private View.OnClickListener legalIdDeleteOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            legalIdAdapter.setIsNetPicture(false);
            legalIdAdapter.clearKeyDate();
        }
    };

    @OnClick(R.id.contract_image_delete_btn)
    private View.OnClickListener contractDeleteOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            contractAdapter.setIsNetPicture(false);
            contractAdapter.clearKeyDate();
        }
    };
}
