package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.tianpingpai.model.BankModel;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.utils.MobileUtil;
import com.tianpingpai.utils.TextValidator;
import com.tianpingpai.widget.FixedGridView;

import java.util.ArrayList;
//@SuppressWarnings("unused")
@ActionBar(title = "上传资料")
@Layout(id = R.layout.view_controller_update_bank_card)
public class UploadBankCardViewController extends BaseViewController {

    public final static String KEY_BANK_ITEM_MODEL = "key.bank_item_model";

    public final static String KEY_IS_CHECK_OUT = "key.is_check";
    private boolean isCheckOut = false;

    private CustomerModel cm;
    private BankModel bm;
    private UserModel user;


    private int ispublic = 0;

    @Binding(id = R.id.ID_number_title_container)
    private View IDNumberTitleContainer;

    @Binding(id = R.id.bank_card_myGridView)
    private FixedGridView bankCardMyGridView;
    @Binding(id = R.id.no_Id_myGridView)
    private FixedGridView noIdMyGridView;
    @Binding(id = R.id.certificate_myGridView)
    private FixedGridView certificateMyGridView;

    @Binding(id = R.id.bank_image_delete_btn)
    private Button bankImageDeleteBtn;
    @Binding(id = R.id.no_id_image_delete_btn)
    private Button noIdImageDeleteBtn;
    @Binding(id = R.id.certificate_image_delete_btn)
    private Button certificateImageDeleteBtn;

    @Binding(id = R.id.select_bank_container)
    private View selectBankContainer;


    private PhotoAdapter bankCardPhotoAdapter = new PhotoAdapter();
    private PhotoAdapter noIdPhotoAdapter;
    private PhotoAdapter certificatePhotoAdapter;

    @Binding(id = R.id.ID_number_container)
    private LinearLayout IDNumberContainer;

    @Binding(id = R.id.account_type_radio_group)
    private RadioGroup selectRadioGroup;

    @Binding(id = R.id.public_account_button)
    private RadioButton publicRadioButton;
    @Binding(id = R.id.private_account_button)
    private RadioButton privateRadioButton;


    @Binding(id = R.id.payee_name_edt)
    private EditText payeeNameEdt;
    @Binding(id = R.id.id_edit_text)
    private EditText IDNumberEdt;
    @Binding(id = R.id.contract_id_edt)
    private EditText contractIdEdt;
    @Binding(id = R.id.bank_card_number_edit_text)
    private EditText bankCardNumberEdt;
    @Binding(id = R.id.branch_edit_text)
    private EditText openingBankEdt;
    @Binding(id = R.id.bank_province_edt)
    private EditText bankProvinceEdt;
    @Binding(id = R.id.bank_city_edt)
    private EditText bankCityEdt;

    @Binding(id = R.id.bank_name_text_view)
    private TextView bankNameTextView;

    @Binding(id = R.id.submit_btn)
    private Button submitButton;

    @Binding(id = R.id.up_view)
    private View upview;

    @Binding(id = R.id.loading_image_view)
    private ImageView loadImage;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        cm = (CustomerModel) getActivity().getIntent().getSerializableExtra(ContractDetailViewController.KEY_CUSTOMER);
        isCheckOut = getActivity().getIntent().getBooleanExtra(KEY_IS_CHECK_OUT, false);
        bm = (BankModel) getActivity().getIntent().getSerializableExtra(KEY_BANK_ITEM_MODEL);

        //判断是否是查看资料
        if (isCheckOut) {
            int status = bm.getStatus();
            switch (status){
                case 0:
                    bankImageDeleteBtn.setVisibility(View.GONE);
                    noIdImageDeleteBtn.setVisibility(View.GONE);
                    certificateImageDeleteBtn.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                    setNoEdit();
                    break;
                case 1:
                    bankImageDeleteBtn.setVisibility(View.GONE);
                    noIdImageDeleteBtn.setVisibility(View.GONE);
                    certificateImageDeleteBtn.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                    setNoEdit();
                    break;
                case 2:
                    break;
            }

            loadDate(bm.getId());
//            deleteAlButton.setVisibility(View.VISIBLE);

            selectRadioGroup.setClickable(false);
            privateRadioButton.setClickable(false);
            publicRadioButton.setClickable(false);

            bankCardPhotoAdapter.setQiNiuPath("/bankcard/");
            bankCardPhotoAdapter.setUpView(upview, loadImage);
            bankCardPhotoAdapter.setCustomerModel(cm);
            bankCardPhotoAdapter.setActivity(getActivity());
            bankCardPhotoAdapter.setGridView(bankCardMyGridView);
            bankCardPhotoAdapter.setNumber(2);
            bankCardPhotoAdapter.setIsNetPicture(true);
            bankCardPhotoAdapter.setStatus(1111);
            bankCardMyGridView.setAdapter(bankCardPhotoAdapter);

            noIdPhotoAdapter = new PhotoAdapter();
            noIdPhotoAdapter.setQiNiuPath("/bankcard/");
            noIdPhotoAdapter.setUpView(upview, loadImage);
            noIdPhotoAdapter.setCustomerModel(cm);
            noIdPhotoAdapter.setActivity(getActivity());
            noIdPhotoAdapter.setGridView(noIdMyGridView);
            noIdPhotoAdapter.setNumber(2);
            noIdPhotoAdapter.setIsNetPicture(true);
            noIdPhotoAdapter.setStatus(1222);
            noIdMyGridView.setAdapter(noIdPhotoAdapter);

            certificatePhotoAdapter = new PhotoAdapter();
            certificatePhotoAdapter.setQiNiuPath("/bankcard/");
            certificatePhotoAdapter.setUpView(upview, loadImage);
            certificatePhotoAdapter.setCustomerModel(cm);
            certificatePhotoAdapter.setActivity(getActivity());
            certificatePhotoAdapter.setGridView(certificateMyGridView);
            certificatePhotoAdapter.setNumber(3);
            certificatePhotoAdapter.setIsNetPicture(true);
            certificatePhotoAdapter.setStatus(1333);
            certificateMyGridView.setAdapter(certificatePhotoAdapter);

        } else {
            selectRadioGroup.setOnCheckedChangeListener(selectOnCheckedChangeListener);
            privateRadioButton.setChecked(true);

            bankImageDeleteBtn.setVisibility(View.GONE);
            noIdImageDeleteBtn.setVisibility(View.GONE);
            certificateImageDeleteBtn.setVisibility(View.GONE);

            bankCardPhotoAdapter = new PhotoAdapter();
            bankCardPhotoAdapter.setQiNiuPath("/bankcard/");
            bankCardPhotoAdapter.setUpView(upview, loadImage);
            bankCardPhotoAdapter.setCustomerModel(cm);
            bankCardPhotoAdapter.setActivity(getActivity());
            bankCardPhotoAdapter.setGridView(bankCardMyGridView);
            bankCardPhotoAdapter.setNumber(2);
            bankCardPhotoAdapter.setStatus(1111);
            bankCardMyGridView.setAdapter(bankCardPhotoAdapter);


            noIdPhotoAdapter = new PhotoAdapter();
            noIdPhotoAdapter.setQiNiuPath("/bankcard/");
            noIdPhotoAdapter.setUpView(upview, loadImage);
            noIdPhotoAdapter.setCustomerModel(cm);
            noIdPhotoAdapter.setActivity(getActivity());
            noIdPhotoAdapter.setGridView(noIdMyGridView);
            noIdPhotoAdapter.setNumber(2);
            noIdPhotoAdapter.setStatus(1222);
            noIdMyGridView.setAdapter(noIdPhotoAdapter);

            certificatePhotoAdapter = new PhotoAdapter();
            certificatePhotoAdapter.setQiNiuPath("/bankcard/");
            certificatePhotoAdapter.setUpView(upview, loadImage);
            certificatePhotoAdapter.setCustomerModel(cm);
            certificatePhotoAdapter.setActivity(getActivity());
            certificatePhotoAdapter.setGridView(certificateMyGridView);
            certificatePhotoAdapter.setNumber(3);
            certificatePhotoAdapter.setStatus(1333);
            certificateMyGridView.setAdapter(certificatePhotoAdapter);
        }
    }


    private void setNoEdit(){
        payeeNameEdt.setEnabled(false);
        payeeNameEdt.clearFocus();
        IDNumberEdt.setEnabled(false);
        IDNumberEdt.clearFocus();
        contractIdEdt.setEnabled(false);
        contractIdEdt.clearFocus();
        bankCardNumberEdt.setEnabled(false);
        bankCardNumberEdt.clearFocus();
        openingBankEdt.setEnabled(false);
        openingBankEdt.clearFocus();
        bankProvinceEdt.setEnabled(false);
        bankProvinceEdt.clearFocus();
        bankCityEdt.setEnabled(false);
        bankCityEdt.clearFocus();

        selectBankContainer.setClickable(false);

        hideKeyboard();
    }

    public void loadDate(int id){
        if (UserManager.getInstance().getCurrentUser() == null) {
            return;
        }
        String url = URLApi.getBaseUrl() + "/crm/customer/get_bankcard_detail";
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
    public void setDate(Model m) {
        if(0 == m.getInt("is_public")){
            ispublic = 0;
            privateRadioButton.setChecked(true);
            IDNumberEdt.setText(m.getString("id_no"));
            IDNumberTitleContainer.setVisibility(View.VISIBLE);
            noIdMyGridView.setVisibility(View.VISIBLE);
        }else{
            ispublic = 1;
            publicRadioButton.setChecked(true);
            IDNumberContainer.setVisibility(View.GONE);
            IDNumberTitleContainer.setVisibility(View.GONE);
            noIdMyGridView.setVisibility(View.GONE);
        }
        payeeNameEdt.setText(m.getString("name"));
        contractIdEdt.setText(m.getString("contract_id"));
        bankNameTextView.setText(m.getString("bank"));
        bankCardNumberEdt.setText(m.getString("card_no"));
        openingBankEdt.setText(m.getString("opening_bank_address"));
        bankProvinceEdt.setText(m.getString("bank_province"));
        bankCityEdt.setText(m.getString("bank_city"));

        ArrayList<String> banklist = (ArrayList<String>)m.getList("bankcard_pictures",String.class);
        ArrayList<String> idlist = (ArrayList<String>)m.getList("idcard_pictures",String.class);
        ArrayList<String> certificatelist = (ArrayList<String>)m.getList("contract_pictures",String.class);

        bankCardPhotoAdapter.setNetDate(banklist);
        noIdPhotoAdapter.setNetDate(idlist);
        certificatePhotoAdapter.setNetDate(certificatelist);


    }

    private RadioGroup.OnCheckedChangeListener selectOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.public_account_button) {
                //选择对公账号
                IDNumberContainer.setVisibility(View.GONE);
                IDNumberTitleContainer.setVisibility(View.GONE);
                noIdMyGridView.setVisibility(View.GONE);
                ispublic = 1;

            } else if (checkedId == R.id.private_account_button) {
                //选择对私账号
                IDNumberContainer.setVisibility(View.VISIBLE);
                IDNumberTitleContainer.setVisibility(View.VISIBLE);
                noIdMyGridView.setVisibility(View.VISIBLE);
                ispublic = 0;
            }
        }
    };

    //选择银行
    @OnClick(R.id.select_bank_container)
    private View.OnClickListener selectBankOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet actionSheet = getActionSheet(true);
            actionSheet.setHeight(getView().getHeight());
            actionSheet.setWidth(getView().getWidth());
            SelectBankViewController sbvc = new SelectBankViewController();
            sbvc.setActivity(getActivity());
            sbvc.setActionSheet(actionSheet);
            sbvc.setOnSelectBankListener(new SelectBankViewController.OnSelectBankListener() {
                @Override
                public void onSelectBank(String s, int id) {
                    bankNameTextView.setText(s);
                }
            });
            actionSheet.setViewController(sbvc);
            actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
            actionSheet.show();
        }
    };


    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);

        if (resultCode == PickOrTakeImageActivity.RESULT_OK) {
            Bundle b = data.getExtras();
//            String path = b.getString("data");
            ArrayList<String> list = b.getStringArrayList("data");
            if(list!=null&&list.size()!=0){
                String path = list.get(0);
            }
            switch (requestCode) {

                case 1111:
                    bankCardPhotoAdapter.setDate(list,true);
                    break;
                case 1222:
                    noIdPhotoAdapter.setDate(list,true);
                    break;
                case 1333:
                    certificatePhotoAdapter.setDate(list,true);
                    break;
            }
        }
    }

    @OnClick(R.id.submit_btn)
    private View.OnClickListener submitButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submit();
        }
    };

    public void submit() {
        user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(payeeNameEdt.getText().toString().trim())) {
            Toast.makeText(getActivity(), "请输入收款人姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ispublic == 0) {
            if (TextUtils.isEmpty(IDNumberEdt.getText().toString().trim())) {
                Toast.makeText(getActivity(), "请输入收款人身份证号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (TextUtils.isEmpty(contractIdEdt.getText().toString().trim())) {
            Toast.makeText(getActivity(), "请输入合同编号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bankCardNumberEdt.getText().toString().trim())) {
            Toast.makeText(getActivity(), "请输入收款人银行卡号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(openingBankEdt.getText().toString().trim())) {
            Toast.makeText(getActivity(), "请输入银行卡开户行", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bankProvinceEdt.getText().toString().trim())) {
            Toast.makeText(getActivity(), "请输入开户行省份", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bankCityEdt.getText().toString().trim())) {
            Toast.makeText(getActivity(), "请输入开户行城市", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(bankNameTextView.getText().toString().trim())) {
            Toast.makeText(getActivity(), "请选择银行", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> bankCardPictures = bankCardPhotoAdapter.getUpLoadOkDate();
        if(bankCardPictures.size()<=1){
            Toast.makeText(getActivity(), "请上传银行卡图片", Toast.LENGTH_SHORT).show();
            return;
        }

        if(0==ispublic){
            ArrayList<String> noIdPictures = noIdPhotoAdapter.getUpLoadOkDate();
            if(noIdPictures.size()<=1){
                Toast.makeText(getActivity(), "请上传身份证图片", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ArrayList<String>certificatePictures = certificatePhotoAdapter.getUpLoadOkDate();
        if(certificatePictures.size()==0){
            Toast.makeText(getActivity(), "请上传结款凭证图片", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        submitTotal();
    }

    private void submitTotal(){
        String url = URLApi.getBaseUrl() +"/crm/customer/edit_bankcard";
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, updateLister);
        req.setMethod(HttpRequest.POST);
        if(isCheckOut){
            req.addParam("id",""+bm.getId());
            req.addParam("user_id",""+bm.getUserId());
        }else{
            req.addParam("user_id", "" + cm.getUserId());
        }
        req.addParam("name", payeeNameEdt.getText().toString().trim());
        if(ispublic == 0){
            req.addParam("ID_no",IDNumberEdt.getText().toString().trim());
            req.addParam("idcard_pictures", noIdPhotoAdapter.getUpLoadKey());
        }else{
            req.addParam("ID_no","");
        }
        req.addParam("contract_id",contractIdEdt.getText().toString().trim());
        req.addParam("bank",bankNameTextView.getText().toString());
        req.addParam("card_no",bankCardNumberEdt.getText().toString().trim());
        req.addParam("opening_bank_address",openingBankEdt.getText().toString().trim());
        req.addParam("bank_province",bankProvinceEdt.getText().toString().trim());
        req.addParam("bank_city",bankCityEdt.getText().toString().trim());
        req.addParam("is_public",""+ispublic);
        req.addParam("bankcard_pictures", bankCardPhotoAdapter.getUpLoadKey());
        req.addParam("contract_pictures", certificatePhotoAdapter.getUpLoadKey());
        req.addParam("accessToken", user.getAccessToken());
        ModelParser<Void> parser = new ModelParser<>(Void.class);
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        submitButton.setClickable(false);
    }

    private HttpRequest.ResultListener<ModelResult<Void>> updateLister = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request,
                             ModelResult<Void> data) {
            submitButton.setClickable(true);
            if (data.isSuccess()) {
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), R.string.add_customer_success, Toast.LENGTH_SHORT).show();
                if (getActivity() != null){
                    getActivity().finish();
                    ModelManager.getModelInstance().notifyEvent(ModelEvent.OnModelUpdate, new Model());
                }
            }else{
                hideLoading();
                Log.e("提交信息失败", data.getCode() + data.getDesc());
                Toast.makeText(getActivity(),"提交信息失败"+data.getCode()+data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    @OnClick(R.id.bank_image_delete_btn)
    private View.OnClickListener bankImageDeleteBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            bankCardPhotoAdapter.setIsNetPicture(false);
            bankCardPhotoAdapter.clearKeyDate();

        }
    };
    @OnClick(R.id.no_id_image_delete_btn)
    private View.OnClickListener noIdImageDeleteBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            noIdPhotoAdapter.setIsNetPicture(false);
            noIdPhotoAdapter.clearKeyDate();
        }
    };
    @OnClick(R.id.certificate_image_delete_btn)
    private View.OnClickListener certificateImageDeleteBtnOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            certificatePhotoAdapter.setIsNetPicture(false);
            certificatePhotoAdapter.clearKeyDate();
        }
    };

//    @SuppressWarnings("unused")
//    @Layout(id=R.layout.fragment_revise_password)
//    public static class RevisePasswordViewController extends CrmBaseViewController {
//
//
//        @Binding(id=R.id.old_password_edt)
//        private EditText oldPasswordEdt;
//        @Binding(id=R.id.new_password_edt1)
//        private EditText newPasswordEdt1;
//        @Binding(id=R.id.new_password_edt2)
//        private EditText newPasswordEdt2;
//
//        @Override
//        protected void onConfigureView(View rootView) {
//            super.onConfigureView(rootView);
//            showContent();
//
//        }
//        @OnClick(R.id.confirmation_new_pw)
//        private View.OnClickListener confirmationNewPasswordOnClickListener = new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                String phone = UserManager.getInstance().getCurrentUser().getPhone();
//                String oldPassword = oldPasswordEdt.getText().toString().trim();
//                String password1 = newPasswordEdt1.getText().toString().trim();
//                String password2 = newPasswordEdt2.getText().toString().trim();
//
//                if(!MobileUtil.isPWDNum(oldPassword)){
//                    Toast.makeText(ContextProvider.getContext(),"请输入正确的旧密码！",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(!TextValidator.isPasswordValid(password1)){
//                    Toast.makeText(ContextProvider.getContext(),"请输入6位以上的数字和字母组合！",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(!TextValidator.isPasswordValid(password2)){
//                    Toast.makeText(ContextProvider.getContext(),"请输入6位以上的数字和字母组合！",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(!password1.equals(password2)){
//                    Toast.makeText(ContextProvider.getContext(),"两次新密码输入不一致！",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//    //			HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/crm/marketer/resetPwd",submitListener);
//                HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/crm/marketer/updatePwd",submitListener);
//                req.setParser(new ModelParser<>(Void.class));
//                req.setMethod(HttpRequest.POST);
//    //			req.addParam("user_type", "0");
//    //            Intent intent = new Intent();
//    //            intent.putExtra(KEY_USER_NAME, userName);
//    //            intent.putExtra(KEY_PASSWORD, password);
//    //            req.setAttachment(intent);
//                req.addParam("phone", phone);
//                req.addParam("old_password", oldPassword);
//                req.addParam("new_password", password2);
//    //			req.addParam("password", password);//code
//    //			req.addParam("verify_code", oldPWD);//code
//                VolleyDispatcher.getInstance().dispatch(req);
//            }
//        };
//
//        public HttpRequest.ResultListener<ModelResult<Void>> submitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
//            @Override
//            public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
//                if(data.isSuccess()){
//                    if(getActivity() != null){
//                        Toast.makeText(ContextProvider.getContext(), "修改成功.", Toast.LENGTH_SHORT).show();
//                        getActivity().setResult(Activity.RESULT_OK,request.getAttachment(Intent.class));
//                        getActivity().finish();
//                    }
//                }else{
//                    Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//    }
}




