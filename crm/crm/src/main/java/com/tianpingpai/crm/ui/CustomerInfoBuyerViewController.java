package com.tianpingpai.crm.ui;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.soundcloud.android.crop.Crop;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerAdapter;
import com.tianpingpai.crm.adapter.SelectListAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SelectCityViewController;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
@Statistics(page = "买家资料")
@ActionBar(title = "买家资料详情")
@Layout(id = R.layout.view_controller_buyer_info)
public class CustomerInfoBuyerViewController extends CrmBaseViewController {


    @Binding(id = R.id.user_id_text_view)
    private TextView userIdTextView;
    @Binding(id = R.id.phone_edt)
    private EditText phoneEditText;
    @Binding(id = R.id.name_edt)
    private EditText nameEditText;
    @Binding(id = R.id.store_name_edt)
    private EditText storeNameEditText;
    @Binding(id = R.id.store_address_edt)
    private EditText storeAddressEditText;

    @Binding(id = R.id.at_area_tv)
    private TextView atAreaTextView;
    @Binding(id = R.id.at_market_tv)
    private TextView atMarketTextView;

    @Binding(id = R.id.turnover_spinner)
    private Spinner turnoverSpinner;
    @Binding(id = R.id.purchase_spinner)
    private Spinner purchaseSpinner;
    @Binding(id = R.id.buyer_type_spinner)
    private Spinner buyerTypeSpinner;

    @Binding(id = R.id.seat_number)
    private EditText seatNumberEditText;

    @Binding(id = R.id.customer_image_button)
    private ImageView photoImageView;
    @Binding(id = R.id.clear_image_button)
    private ImageView deleteButton;

    @Binding(id = R.id.submit_button)
    private Button submitButton;


    private boolean isNotSubmit;


    private int id;
    private int areaCityId;
    private String marketId;


    private int turnoverId;
    private int purchaseId;
    private int buyerTypeId;

    private SelectListAdapter turnoverAdapter = new SelectListAdapter();
    private SelectListAdapter purchaseAdapter = new SelectListAdapter();
    private SelectListAdapter buyerTypeAdapter = new SelectListAdapter();

    //客户在骑牛存储的图片地址
    private String netImageUrl;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        CustomerModel customerModel = (CustomerModel)getActivity().getIntent().getSerializableExtra(CustomerAdapter.KEY_CUSTOMER_INFO);
        isNotSubmit = getActivity().getIntent().getBooleanExtra(CustomerAdapter.KEY_IS_NOT_SUBMIT, false);

        turnoverSpinner.setAdapter(turnoverAdapter);
        purchaseSpinner.setAdapter(purchaseAdapter);
        buyerTypeSpinner.setAdapter(buyerTypeAdapter);

        if(isNotSubmit){
            submitButton.setVisibility(View.GONE);
        }

        getCustomerData(customerModel.getId()+"");

    }

    private void getCustomerData(String id){

        String url = URLApi.getBaseUrl() + "/crm/customer/getCustomerInfo";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,upDataListener);
        req.setParser(new GenericModelParser());
        req.addParam("id", id);
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();

    }

    private HttpRequest.ResultListener<ModelResult<Model>> upDataListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {

            hideLoading();

            if(1 == data.getCode()){
                UserManager.getInstance().loginExpired(getActivity());
                getActivity().finish();
                return;
            }
            if(data.isSuccess()){
                setUserData(data);

            }else {
                Toast.makeText(getActivity(), data.getDesc(), Toast.LENGTH_SHORT).show();
                showEmptyView();
            }
        }
    };

    private void setUserData(ModelResult<Model> data){

        hideKeyboard();
        phoneEditText.setEnabled(false);
        nameEditText.setEnabled(false);
//        storeNameEditText.setEnabled(false);
//        storeAddressEditText.setEnabled(false);
//        atAreaTextView.setClickable(false);
//        atMarketTextView.setEnabled(false);
        loadSelectedList();

        id = data.getModel().getInt("id");
        userIdTextView.setText(data.getModel().getInt("user_id") + "");
        phoneEditText.setText(data.getModel().getString("phone"));
        nameEditText.setText(data.getModel().getString("display_name"));
        storeNameEditText.setText(data.getModel().getString("sale_name"));
        storeAddressEditText.setText(data.getModel().getString("sale_address"));
        atAreaTextView.setText(data.getModel().getString("regionName"));
        atMarketTextView.setText(data.getModel().getString("market_name"));

        areaCityId = data.getModel().getInt("region");
        marketId = data.getModel().getInt("market_id")+"";

        selectMarketId.add(data.getModel().getInt("market_id"));

        turnoverId = data.getModel().getInt("sales");
        purchaseId = data.getModel().getInt("purchase");
        buyerTypeId = data.getModel().getInt("categoryId");

        seatNumberEditText.setText(data.getModel().getInt("seats")+"");

        netImageUrl = data.getModel().getString("photo");
        com.tianpingpai.http.util.ImageLoader.load(netImageUrl, customerImageView);
        clearImageButton.setVisibility(View.VISIBLE);
        imageUrl = getOldImageKey(netImageUrl);



    }

    private void loadSelectedList() {
        String url = ContextProvider.getBaseURL() + "/crm/customer/getSelectedList";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, selectListener);
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> selectListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if (data.isSuccess()) {
                Model model = data.getModel();
                Log.e("xx", "277----------model=" + model);
                List<Model> turnoverModels = model.getList("1", Model.class);
                turnoverAdapter.setModels(turnoverModels);
                Log.e("xx", "212------turnoverId = " + turnoverId);
                for(int i = 0; i<turnoverModels.size();i++){
                    if(turnoverModels.get(i).getInt("id") == turnoverId){
                        turnoverSpinner.setSelection(i);
                        break;
                    }
                }
                turnoverAdapter.notifyDataSetChanged();

                List<Model> purchaseModels =  model.getList("2", Model.class);
                purchaseAdapter.setModels(purchaseModels);
                for(int i = 0; i<purchaseModels.size();i++){
                    if(purchaseModels.get(i).getInt("id") == purchaseId){
                        purchaseSpinner.setSelection(i);
                        break;
                    }
                }
                purchaseAdapter.notifyDataSetChanged();

                List<Model> buyerTypeModels =  model.getList("3", Model.class);
                buyerTypeAdapter.setModels(buyerTypeModels);
                for(int i = 0; i<buyerTypeModels.size();i++){
                    if(buyerTypeModels.get(i).getInt("id") == buyerTypeId){
                        buyerTypeSpinner.setSelection(i);
                        break;
                    }
                }
                buyerTypeAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_LONG).show();
            }
        }
    };


    @OnClick(R.id.submit_button)
    private View.OnClickListener submitListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            submit();
        }
    };


    private String seatNumber;
    private void submit(){

        if(areaCityId == 0){
            Toast.makeText(ContextProvider.getContext(), R.string.no_arae_city_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        Model turnoverModel = turnoverAdapter.getItem(turnoverSpinner.getSelectedItemPosition());
        if(turnoverModel == null){
            Toast.makeText(ContextProvider.getContext(), R.string.no_turnover_selected, Toast.LENGTH_SHORT).show();
            return;
        }else {
            turnoverId = turnoverModel.getInt("id");
            Log.e("xx", "435-------id=" + turnoverId);
        }
        Model purchaseModel = purchaseAdapter.getItem(purchaseSpinner.getSelectedItemPosition());
        if(purchaseModel == null){
            Toast.makeText(ContextProvider.getContext(), R.string.no_purchase_selected, Toast.LENGTH_SHORT).show();
            return;
        }else {
            purchaseId = purchaseModel.getInt("id");
        }
        Model buyerTypeModel = buyerTypeAdapter.getItem(buyerTypeSpinner.getSelectedItemPosition());
        if(buyerTypeModel == null){
            Toast.makeText(ContextProvider.getContext(), R.string.no_buyer_type_selected, Toast.LENGTH_SHORT).show();
            return;
        }else {
            buyerTypeId = buyerTypeModel.getInt("id");
        }

        seatNumber = seatNumberEditText.getText().toString();
        if(TextUtils.isEmpty(seatNumber)){
            Toast.makeText(getActivity(),"请填写座位数！",Toast.LENGTH_SHORT).show();
            return;
        }


        if (localImagePath != null) {
            getQiNiuToken();
        }else if(imageUrl != null && !TextUtils.isEmpty(imageUrl)){
            upDataCustomerInfo();
        } else {
            Toast.makeText(ContextProvider.getContext(), R.string.no_image_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void upDataCustomerInfo(){
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.Customer.updateInfo(), updateListener);
        req.setMethod(HttpRequest.POST);

        req.addParam("id", id + "");
        req.addParam("display_name", nameEditText.getText().toString());
        req.addParam("sale_name", storeNameEditText.getText().toString());
        req.addParam("sale_address", storeAddressEditText.getText().toString());
        req.addParam("region", ""+areaCityId);
        req.addParam("sales", ""+ turnoverId);
        req.addParam("purchase", ""+ purchaseId);
        req.addParam("buyer_category", buyerTypeId + "");
        req.addParam("seats",  seatNumber);
        req.addParam("market_id", marketId + "");

        if (imageUrl == null) {
            req.addParam("photo", "");
        } else {
            req.addParam("photo", imageUrl);
        }
        ModelParser<Void> parser = new ModelParser<>(Void.class);
        req.setParser(parser);
        showLoading();
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Void>> updateListener =  new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request,
                             ModelResult<Void> data) {
            hideLoading();
            if(data.isSuccess()){
                deleteOld();
                Toast.makeText(ContextProvider.getContext(), "修改成功！", Toast.LENGTH_SHORT).show();
//				CustomerManager.getInstance().notifyEvent(CustomerEvent.CustomerInfoUpdate, request.getAttachment(CustomerModel.class));
                getActivity().finish();
            }else{
                Log.e("error====",data.getDesc());
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    // 截图功能
    private String localImagePath;
    private String imageUrl;
    private String oldImageKey;
    private boolean isDelete = false;
    public static final int REQUEST_CODE_PICK_IMAGE = 12;//TODO
    public static final int REQUEST_CODE_CROP_IMAGE = 13;//TODO

    @Binding(id = R.id.clear_image_button)
    private View clearImageButton;

    @Binding(id = R.id.customer_image_button)
    private ImageView customerImageView;

    @OnClick(R.id.clear_image_button)
    private View.OnClickListener clearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 有老图片 或者 新选择的图片
            if (!TextUtils.isEmpty(oldImageKey) || !TextUtils.isEmpty(localImagePath)) {
                final ActionSheetDialog dialog = new ActionSheetDialog();
                dialog.setActivity(getActivity());
                dialog.setActionSheet(new ActionSheet());
                dialog.setTitle("您确定要删除图片吗?");
                dialog.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isDelete = true;
                        imageUrl = null;
                        localImagePath = null;
                        customerImageView.setImageResource(R.drawable.ic_151020_add_prod_img);
                        clearImageButton.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    };

    @OnClick(R.id.customer_image_button)
    private View.OnClickListener pickImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().startActivityForResult(createDefaultOpenableIntent(), REQUEST_CODE_PICK_IMAGE);
        }
    };

    private Intent createDefaultOpenableIntent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.putExtra(Intent.EXTRA_TITLE, "选择文件");
        i.setType("*/*");

        Intent chooser = createChooserIntent(createCameraIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }

    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "选择文件");
        return chooser;
    }

    Uri camUri;

    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() + File.separator + "e-photos");
        cameraDataDir.mkdirs();
        String mPhotoFilePath = cameraDataDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
        File f = new File(mPhotoFilePath);
        camUri = Uri.fromFile(f);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoFilePath)));
        return cameraIntent;
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(ContextProvider.getContext().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(320, 320).start(getActivity(), REQUEST_CODE_CROP_IMAGE);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            customerImageView.setImageURI(Crop.getOutput(result));
            //TODO
            localImagePath = new File(ContextProvider.getContext().getCacheDir(), "cropped").getAbsolutePath();
            clearImageButton.setVisibility(View.VISIBLE);
//			if (customerModel != null) {
//				isDelete = !TextUtils.isEmpty(customerModel.getImageUrl());
            if(null==netImageUrl){
                isDelete = true;
            } else {
                isDelete = false;
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(ContextProvider.getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(Activity a,int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a,requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (data != null) {
                camUri = data.getData();
            }
            beginCrop(camUri);
        } else if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            handleCrop(resultCode, data);
        }
    }

    private HttpRequest.ResultListener<ModelResult<String>> qiNiuTokenListener = new HttpRequest.ResultListener<ModelResult<String>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<String>> request, ModelResult<String> data) {
            if (data.isSuccess()) {
                uploadImage(data.getModel());
            } else {
                setEnabled(true);
                if (data.getCode() == HttpResult.CODE_AUTH) {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), ContainerActivity.class);
                        intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                        getActivity().startActivity(intent);
                    } else {
                        Toast.makeText(ContextProvider.getContext(), "请登录后重试!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ContextProvider.getContext(), "上传图片失败!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void getQiNiuToken() {
        HttpRequest<ModelResult<String>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/image/token", qiNiuTokenListener);
        req.setParser(new ModelParser<>(String.class));
        setEnabled(false);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_LONG).show();
                setEnabled(true);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void setEnabled(boolean enabled) {
        submitButton.setEnabled(enabled);
        if (enabled) {
            hideLoading();
        } else {
//			setIndicatorCancelable(false);
            showLoading();
        }
    }

    private void uploadImage(String token) {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), "登录后请重试!", Toast.LENGTH_LONG).show();
            setEnabled(true);
            return;
        }
        Toast.makeText(ContextProvider.getContext(), "正在上传图片,请稍候!", Toast.LENGTH_SHORT).show();
        String curTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE).format(new Date());
        String mNewImageKey = "crm/buyer/" + user.getId() + "/detail/" + curTime + ".png";
        //		}
        Map<String, String> params = new HashMap<>();
        params.put("seller:userTel", "" + user.getPhone());
        final UploadOptions opt = new UploadOptions(params, null, true, null, null);
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(localImagePath, mNewImageKey, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    imageUrl = key;
                    localImagePath = null;
                    Toast.makeText(ContextProvider.getContext(), "上传图片成功", Toast.LENGTH_SHORT).show();
                    upDataCustomerInfo();
                } else {
                    String error = info.error;
                    if (error.equals("expired token")) {
                        getQiNiuToken();//TODO
                    } else {
                        setEnabled(true);
                        Toast.makeText(ContextProvider.getContext(), "上传图片错误:" + error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, opt);
    }

    private HttpRequest.ResultListener<ModelResult<Void>> deleteListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            //TODO
        }
    };

    private void deleteOld() {
        if (oldImageKey == null || imageUrl == null) {
            return;
        }
        //TODO change to post request
        if (isDelete) {
            String url = ContextProvider.getBaseURL() + "/api/image/delete";
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, deleteListener);
//			req.addParam("key", getOldImageKey(customerModel));
            req.addParam("key",oldImageKey);
            VolleyDispatcher.getInstance().dispatch(req);
        }
    }

    public java.lang.String getOldImageKey(String image) {
        if (image != null) {
            String[] keys = image.split(".com/");
            oldImageKey = keys[keys.length - 1];
        } else {
            oldImageKey = "";
        }
        return oldImageKey;
    }


    private SelectCityViewController selectCityViewController;
    private SelectCityViewController.OnSelectCityListener onSelectCityListener = new SelectCityViewController.OnSelectCityListener() {
        @Override
        public void onSelectCity(Model model) {
            atAreaTextView.setText(selectCityViewController.getSelectedCity().getString("name") + "-" + model.getString("name"));
            areaCityId = model.getInt("area_id");

        }
    };

    @OnClick(R.id.at_area_tv)
    private View.OnClickListener areaIdSelectOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            ActionSheet actionSheet = getActionSheet(true);
            actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
            actionSheet.setHeight(DimensionUtil.dip2px(300));
            selectCityViewController = new SelectCityViewController();
            selectCityViewController.setActionSheet(actionSheet);
            selectCityViewController.setActivity(getActivity());
            selectCityViewController.setOnSelectCityListener(onSelectCityListener);
            actionSheet.setViewController(selectCityViewController);
            actionSheet.show();
            hideKeyboard();

        }
    };


    @OnClick(R.id.at_market_tv)
    private View.OnClickListener ccc = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            ActionSheet atAreaActionSheet = new ActionSheet();
            atAreaActionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
            atAreaActionSheet.setHeight(getView().getHeight());
            SelectAreaViewController selectMarketViewController = new SelectAreaViewController();
            selectMarketViewController.setIsOnly(true);
            selectMarketViewController.setSelectedId(selectMarketId);
            selectMarketViewController.setActionSheet(atAreaActionSheet);
            selectMarketViewController.setActivity(getActivity());
            selectMarketViewController.setOnSelectAreaListener(onSelectMarketListener);
            atAreaActionSheet.setViewController(selectMarketViewController);
            atAreaActionSheet.show(getActivity().getSupportFragmentManager(), "");
        }
    };


    private ArrayList<Integer> selectMarketId = new ArrayList<>();
    private SelectAreaViewController.OnSelectAreaListener onSelectMarketListener = new SelectAreaViewController.OnSelectAreaListener(){
        @Override
        public void onSelectArea(HashMap<Integer, String> selections) {

            selectMarketId.clear();
            Iterator<Map.Entry<Integer ,String>> iter = selections.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry<Integer,String> entry = (Map.Entry) iter.next();
                String s = entry.getValue();
                String [] ss = s.split("abc");
                selectMarketId.add(Integer.parseInt(ss[1]));
                marketId = ss[1];
                atMarketTextView.setText(ss[0]);
            }
        }
    };

}
