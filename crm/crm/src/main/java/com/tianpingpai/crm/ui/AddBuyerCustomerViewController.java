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
import com.tianpingpai.crm.adapter.SelectListAdapter;
import com.tianpingpai.crm.adapter.StoreCategoryAdapter;
import com.tianpingpai.crm.parser.CategoriesParser;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.StoreCategoryModel;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
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
import java.util.Map;


@SuppressWarnings("unused")
@ActionBar(title = "添加买家客户")
@Layout(id = R.layout.view_controller_add_buyer_customer)
public class AddBuyerCustomerViewController extends CrmBaseViewController {

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
    @Binding(id = R.id.address_city)
    private TextView addressCity;
    @Binding(id = R.id.store_name_edit_text)
    private EditText storeNameEditText;
    @Binding(id = R.id.store_address_edit_text)
    private EditText storeAddressEditText;
    @Binding(id = R.id.buyer_at_market_text_view)
    private TextView buyerAtMarketTextView;

    @Binding(id = R.id.turnover_spinner)
    private Spinner turnoverSpinner;
    @Binding(id = R.id.purchase_spinner)
    private Spinner purchaseSpinner;
    @Binding(id = R.id.buyer_type_spinner)
    private Spinner buyerTypeSpinner;
    @Binding(id = R.id.num_edit_text)
    private EditText numEditText;

    @Binding(id = R.id.submit_button)
    private Button submitButton;

    private Model model;

    private SelectListAdapter turnoverAdapter = new SelectListAdapter();
    private SelectListAdapter purchaseAdapter = new SelectListAdapter();
    private SelectListAdapter buyerTypeAdapter = new SelectListAdapter();

    private StoreCategoryAdapter categoryAdapter = new StoreCategoryAdapter();
    private int turnoverId;
    private int purchaseId;
    private int buyerTypeId;
    private String seatNum;
    private String phone;
    private String storeAddress;
    private String customerName;
    private String storeName;
    private UserModel user;


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        turnoverSpinner.setAdapter(turnoverAdapter);
        purchaseSpinner.setAdapter(purchaseAdapter);
        buyerTypeSpinner.setAdapter(buyerTypeAdapter);

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
                storeNameEditText.setText(model.getString("shop_name"));
                loadStoreTypes();
                loadSelectedList();

                checkContainer.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);
            }
        }
    };

    private void loadStoreTypes() {
        String url = ContextProvider.getBaseURL() + "/crm/customer/buyerCategory";
        HttpRequest<ListResult<StoreCategoryModel>> req = new HttpRequest<>(url, categoryListener);
        req.setParser(new CategoriesParser());
        VolleyDispatcher.getInstance().dispatch(req);
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
                turnoverAdapter.notifyDataSetChanged();
                purchaseAdapter.setModels(model.getList("2", Model.class));
                purchaseAdapter.notifyDataSetChanged();
                buyerTypeAdapter.setModels(model.getList("3", Model.class));
                buyerTypeAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private HttpRequest.ResultListener<ListResult<StoreCategoryModel>> categoryListener = new HttpRequest.ResultListener<ListResult<StoreCategoryModel>>() {
        @Override
        public void onResult(HttpRequest<ListResult<StoreCategoryModel>> request, ListResult<StoreCategoryModel> data) {
            if (data.getCode() == 1) {
                UserManager.getInstance().loginExpired(getActivity());
            }
            if (data.isSuccess()) {
                categoryAdapter.setData(data);
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_LONG).show();
            }
        }
    };



    private SelectCityViewController selectCityViewController;
    private SelectCityViewController.OnSelectCityListener onSelectCityListener = new SelectCityViewController.OnSelectCityListener() {
        @Override
        public void onSelectCity(Model model) {
            addressCity.setText(selectCityViewController.getSelectedCity().getString("name") + "-" + model.getString("name"));
            areaCityId = model.getInt("area_id");

        }
    };
    @OnClick(R.id.address_city)
    private View.OnClickListener onCityClickListener = new View.OnClickListener() {
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


    @OnClick(R.id.submit_button)
    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submit();
        }
    };

    @OnClick(R.id.check_customer_button)
    private View.OnClickListener checkButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
        req.addParam("user_type", "" + CustomerModel.USER_TYPE_BUYER);
        req.addParam("accessToken", user.getAccessToken());

        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        hideKeyboard();
        showLoading();
    }

    private int areaCityId = 0;

    private void submit() {
        user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("------------+", model.toString());
        if (model == null) {
            return;
        }

        phone = customerPhoneTextView.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getActivity(), "手机号不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        customerName = customerNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(customerName)) {
            Toast.makeText(getActivity(), "客户姓名不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        storeName = storeNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(storeName)) {
            Toast.makeText(getActivity(), "商店名称不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        storeAddress = storeAddressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(storeAddress)) {
            Toast.makeText(getActivity(), "商店地址不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        if("".equals(marketId) || marketId == null){
            Toast.makeText(getActivity(), "请选择所属商圈!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (areaCityId == 0) {
            Toast.makeText(ContextProvider.getContext(), R.string.no_arae_city_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        Model turnoverModel = turnoverAdapter.getItem(turnoverSpinner.getSelectedItemPosition());
        if (turnoverModel == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.no_turnover_selected, Toast.LENGTH_SHORT).show();
            return;
        } else {
            turnoverId = turnoverModel.getInt("id");
            Log.e("xx", "435-------id=" + turnoverId);
        }
        Model purchaseModel = purchaseAdapter.getItem(purchaseSpinner.getSelectedItemPosition());
        if (purchaseModel == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.no_purchase_selected, Toast.LENGTH_SHORT).show();
            return;
        } else {
            purchaseId = purchaseModel.getInt("id");
        }
        Model buyerTypeModel = buyerTypeAdapter.getItem(buyerTypeSpinner.getSelectedItemPosition());
        if (buyerTypeModel == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.no_buyer_type_selected, Toast.LENGTH_SHORT).show();
            return;
        } else {
            buyerTypeId = buyerTypeModel.getInt("id");
        }

        seatNum = numEditText.getText().toString().trim();
        if (TextUtils.isEmpty(seatNum)) {
            Toast.makeText(ContextProvider.getContext(), R.string.no_num_seat_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        if (localImagePath != null) {
            getQiNiuToken();
        } else {
            Toast.makeText(ContextProvider.getContext(), R.string.no_image_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void submitCustomer() {

        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.Customer.add(), updateLister);
        req.setMethod(HttpRequest.POST);
        req.addParam("region", "" + areaCityId);
        req.addParam("sales", "" + turnoverId);
        req.addParam("purchase", "" + purchaseId);
        req.addParam("buyer_category", buyerTypeId + "");
        req.addParam("seats", "" + seatNum);
        req.addParam("market_id",marketId+"");
        if (imageUrl == null) {
            req.addParam("photo", "");
        } else {
            req.addParam("photo", imageUrl);
        }
        req.addParam("phone", phone);
        req.addParam("sale_address", storeAddress);
        req.addParam("display_name", customerName);
        req.addParam("sale_name", storeName);

        req.addParam("user_type", model.get("user_type", Integer.class) + "");

        if (this.model.getInt("user_id") == 0) {
            req.addParam("is_register", "0");
        } else {
            req.addParam("is_register", "1");
        }

        req.addParam("user_id", this.model.getInt("user_id") + "");
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
            hideSubmitting();
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
                dialog.setActionSheet(new ActionSheet());
                dialog.setActivity(getActivity());
                dialog.setTitle("您确定要删除图片吗?");
                dialog.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isDelete = true;
                        imageUrl = null;
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
            /*if (customerModel != null) {
                isDelete = !TextUtils.isEmpty(customerModel.getImageUrl());
            } else {
                isDelete = false;
            }*/
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(ContextProvider.getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);
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
//            setIndicatorCancelable(false);
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
        String curTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
                    Toast.makeText(ContextProvider.getContext(), "上传图片成功", Toast.LENGTH_SHORT).show();
                    submitCustomer();
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


    private String marketId;
    private ArrayList<Integer> selectMarketId = new ArrayList<>();

    @OnClick(R.id.at_market_container)
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
            selectMarketViewController.setOnSelectAreaListener(onSelectmarketListener);
            atAreaActionSheet.setViewController(selectMarketViewController);
            atAreaActionSheet.show(getActivity().getSupportFragmentManager(), "");
        }
    };

    private SelectAreaViewController.OnSelectAreaListener onSelectmarketListener = new SelectAreaViewController.OnSelectAreaListener(){
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
                buyerAtMarketTextView.setText(ss[0]);
            }
        }
    };
}
