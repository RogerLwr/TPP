package com.tianpingpai.seller.ui;


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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.soundcloud.android.crop.Crop;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.ProductManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.PriceFormat;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@ActionBar(title = "添加商品")
@Statistics(page = "编辑商品")
@Layout(id = R.layout.view_controller_edit_product)
public class EditProductViewController extends BaseViewController {

    public static final String KEY_PRODUCT = "key.product";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ADD = "key.add";

    private boolean isAdd = false;// 是增加商品还是 编辑商品

    public static final int REQUEST_CODE_PICK_IMAGE = 12;
    public static final int REQUEST_CODE_CROP_IMAGE = 13;
    public static final int REQUEST_CODE_PICK_UNIT = 21;
    public static final int REQUEST_CODE_PICK_PROPERTY = 22;
    public static final int REQUEST_CODE_SET_PROMOTION = 23;

    @Binding(id = R.id.product_category_text_view, format = "{{t_name}}-{{p_name}}-{{c_name}}")
    private TextView productCategoryTextView;
    @Binding(id = R.id.product_category_right_arrows)
    private ImageView productCategoryRightArrows;

    @Binding(id = R.id.product_name_container)
    private View productNameContainer;
    @Binding(id = R.id.product_name_text_view, format = "{{goods_name}}")
    private TextView productNameTextView;
    @Binding(id = R.id.product_image_view)
    private ImageView prodImageView;
    @Binding(id = R.id.set_promotion_btn, format = "设置促销")
    private Button setPromotionBtn;
    @Binding(id = R.id.edit_img_button)
    private View editImageButton;
    @Binding(id = R.id.product_property_text_view)
    private TextView productPropertyTextView;
    @Binding(id = R.id.shelve_button_container)
    private RadioGroup shelveBtnContainer;
    @Binding(id = R.id.specs_unit_text_view)
    private TextView specsUnitTextView;
    @Binding(id = R.id.specs_text_view)
    private TextView specsTextView;
    @Binding(id = R.id.price_edit_text)
    private EditText priceEditText;
    @Binding(id = R.id.unit_text_view)
    private TextView unitTextView;
    @Binding(id = R.id.coupon_unit_container)
    private View couponUnitContainer;
    @Binding(id = R.id.coupon_price_edit_text)
    private EditText couponPriceEditText;
    @Binding(id = R.id.coupon_price_text_view)
    private TextView couponPriceTextView;
    @Binding(id = R.id.coupon_unit_text_view)
    private TextView couponUnitTextView;

    @Binding(id = R.id.promotion_container)
    private View promotionContainer;
    @Binding(id = R.id.sales_status_text_view, format = "【{{sales_status}}】")
    private TextView salesStatusTextView;
    @Binding(id = R.id.sales_time_text_view, format = "{{sales_time}}")
    private TextView salesTimeTextView;
    @Binding(id = R.id.on_shelve_button)
    private RadioButton onShelveButton;
    @Binding(id = R.id.images_container)
    private LinearLayout imagesContainer;
    @Binding(id = R.id.submit_button)
    private Button submitButton;
    @Binding(id = R.id.product_img_container)
    private View productImageContainer;

    private ProductModel productModel;
    private Model category;
    private Model selectedPropertyModel;

    private String localImagePath;
    private String imageUrl;
    private String oldImageKey;
    private ActionSheetDialog actionSheetDialog;

    private Model selectedUnit;
    private Model selectedSpecs;
    private Model selectedNetQuality;
    private String remark;
    private double price;

    private ModelStatusListener<ModelEvent, ProductModel> onModelStatusChangeListener = new ModelStatusListener<ModelEvent, ProductModel>() {
        @Override
        public void onModelEvent(ModelEvent event, ProductModel model) {
            switch (event){
                case OnModelUpdate:
                    if (productModel != null && productModel.getPid() != 0 && getActivity() != null) { //有PID的促销的 商品详情 直接finish
                        getActivity().finish();
                    }else {  //否则刷新处理
                        getGoodsInfoByID();
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        productModel = (ProductModel) a.getIntent().getSerializableExtra(KEY_PRODUCT);
        isAdd = a.getIntent().getBooleanExtra(KEY_ADD, false);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        category = getActivity().getIntent().getParcelableExtra(KEY_CATEGORY);
        if (isAdd) {
            setTitle("添加商品");
            productCategoryRightArrows.setVisibility(View.VISIBLE);
            setPromotionBtn.setVisibility(View.GONE);
        } else {
            setTitle("修改商品");
            productCategoryRightArrows.setVisibility(View.GONE);
            if (productModel != null && !TextUtils.isEmpty(productModel.getImageUrl())) {
                com.tianpingpai.http.util.ImageLoader.load(productModel.getImageUrl(), prodImageView);
            }
//            imageUrl = getOldImageKey(productModel);
            if (productModel != null) {
                if (productModel.getStatus() == 1) {
                    shelveBtnContainer.check(R.id.on_shelve_button);
                } else {
                    shelveBtnContainer.check(R.id.off_shelve_button);
                }
                productNameTextView.setText(productModel.getName());
                updateUnit(productModel.getUnit());
//                specsUnitTextView.setText("元/" + productModel.getUnit());
                priceEditText.setText(PriceFormat.format(productModel.getCouponPrice()));
//                unitTextView.setText("元/" + productModel.getUnit());
//                couponUnitTextView.setText("元/" + productModel.getUnit());
                productPropertyTextView.setText(productModel.getDescription());
                String categoryName = productModel.getfCategoryName() + "-" + productModel.getpCategoryName();
            }
        }
        getGoodsInfoByID();
        ProductManager.getInstance().registerListener(onModelStatusChangeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProductManager.getInstance().unregisterListener(onModelStatusChangeListener);
    }

    private void getGoodsInfoByID() {
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/sku/goods/getGoodsInfoByID", goodsInfoResultListener);
        if (isAdd) {
            req.addParam("category_id", category.getInt("categoryId") + "");
        } else {
            req.addParam("product_id", productModel.getId() + "");
            req.addParam("category_id", productModel.getCategoryId() + "");
            if (productModel.getPid() != 0) {
                req.addParam("pid", productModel.getPid() + "");
            }
        }
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    int salesStatusInt;
    int categoryId;
    int showLength = 10;

    private HttpRequest.ResultListener<ModelResult<Model>> goodsInfoResultListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if (data.isSuccess()) {
                Model model = data.getModel();
                categoryId = model.getInt("category_id");
                getBinder().bindData(data.getModel());
                String nameEditable = model.getString("is_name");
                if (model.getBoolean("is_name")) {
                    productNameContainer.setEnabled(true);
                } else {
                    productNameContainer.setEnabled(false);
                }

                boolean imageEditable = model.getBoolean("img_editable");
                productImageContainer.setEnabled(imageEditable);

                if(!imageEditable){
                    editImageButton.setVisibility(View.GONE);
                }else{
                    editImageButton.setVisibility(View.VISIBLE);
                }

                if (!isAdd) {
                    if (model.getInt("status") == 1) {
                        shelveBtnContainer.check(R.id.on_shelve_button);
                    } else {
                        shelveBtnContainer.check(R.id.off_shelve_button);
                    }

                    selectedUnit = new Model();
                    Model.copy(model,selectedUnit,"unit_id","unit_name");
                    price = model.getDouble("price");

                    List<Model> attrList = model.getList("attr_list", Model.class);
                    if (attrList != null) {
                        selectedPropertyModel = new Model(); // 值回传 带着选中的值˛
                        List<Model> selectedModels = new ArrayList<>();
                        selectedPropertyModel.setList("selectedModels", selectedModels);

                        String attrValue = "";
                        int nextAttrID = 0;
                        int attr_id = 0;
                        for (int i = 0; i < attrList.size(); i++) {

                            Model attrModel = attrList.get(i);

                            Model model1 = new Model();
                            model1.set("attr_id", attrModel.getInt("attr_id"));
                            model1.set("id", attrModel.getInt("id"));
                            model1.set("value", attrModel.getString("attr_value"));
                            model1.set("name", attrModel.getString("attr_name"));
                            String attrType = attrModel.getString("attr_type");
                            model1.set("type", attrType);
                            if ("quality".equals(attrType)) {
                                model1.set("level", attrModel.getString("data_level"));
                            }
                            selectedModels.add(model1);

                            if (attrModel.getString("attr_type").equals("quality")) {
                                if (attrModel.getString("attr_value").length() >= showLength) {
                                    attrValue += "品质: 【" + attrModel.getString("data_level") + "】 " + attrModel.getString("attr_value").substring(0, showLength) + "...";
                                } else {
                                    attrValue += "品质: 【" + attrModel.getString("data_level") + "】 " + attrModel.getString("attr_value") + "";
                                }
                            } else {
                                attrValue += attrModel.getString("attr_value");
                            }
                            // 不同 属性分类加换行符
                            attr_id = attrModel.getInt("attr_id");
                            if (i != attrList.size() - 1) {
                                nextAttrID = attrList.get(i + 1).getInt("attr_id");
                            } else {
                                nextAttrID = -1;
                            }
                            if (nextAttrID != attr_id) {
                                attrValue += "\n";
                            }
                        }
                        descStr = model.getString("new_remark");
                        if (!TextUtils.isEmpty(descStr)) {
                            if (descStr.length() >= showLength) {

                                attrValue += "商品描述: " + descStr.substring(0, showLength) + "...";
                            } else {

                                attrValue += "商品描述: " + descStr;
                            }
                        }
                        productPropertyTextView.setText(attrValue);
                    }

                    List<Model> priceList = model.getList("price_list",Model.class);
                    if(priceList != null){
                        for(Model m:priceList){
                            String type = m.getString("attr_type");
                            if("weight".equals(type)){
                                selectedNetQuality = m;
                                selectedNetQuality.set("value",m.get("attr_value"));
                            }
                            if("stand".equals(type)){
                                selectedSpecs = m;
                                selectedSpecs.set("value",m.get("attr_value"));
                            }
                        }
                    }
                    salesStatusInt = model.getInt("sales_status_init");

//                    if ( salesStatusInt != PromotionDetailViewController.SALES_STATUS_2_EXAMINE_FAIL ) { //  不能修改
                    couponPriceEditText.setVisibility(View.GONE);
                    couponPriceTextView.setVisibility(View.VISIBLE);
//                    } else {
//                        couponPriceEditText.setVisibility(View.VISIBLE);
//                        couponPriceTextView.setVisibility(View.GONE);
//                    }
                    submitButton.setText("保存");
                    submitButton.setEnabled(true);

                    if (salesStatusInt == PromotionDetailViewController.SALES_STATUS_0_NO_PROMOTION) { //没有促销 不显示促销价
                        promotionContainer.setVisibility(View.GONE);
                        setPromotionBtn.setVisibility(View.VISIBLE);
                        couponUnitContainer.setVisibility(View.GONE);
                        setPromotionBtn.setText("设置促销");
                    } else {
                        couponUnitContainer.setVisibility(View.VISIBLE);
                        setPromotionBtn.setVisibility(View.GONE);
                        couponPriceEditText.setText(""+ model.getDouble("salesPrice"));
                        couponPriceTextView.setText(""+ model.getDouble("salesPrice"));
                        switch (salesStatusInt) {
                            case PromotionDetailViewController.SALES_STATUS_1_WAIT_EXAMINE:
                                salesTimeTextView.setText(model.getString("sales_info"));
                                promotionContainer.setVisibility(View.VISIBLE);
                                break;
                            case PromotionDetailViewController.SALES_STATUS_2_EXAMINE_FAIL:
                                salesTimeTextView.setText("审核时间: " + model.getString("sales_time"));
                                setPromotionBtn.setVisibility(View.GONE);
                                promotionContainer.setVisibility(View.VISIBLE);
                                break;
                            case PromotionDetailViewController.SALES_STATUS_3_EXAMINE_SUCCESS:
//                                salesTimeTextView.setText("结束时间: "+model.getString("sales_time"));
                                salesTimeTextView.setVisibility(View.GONE);
                                promotionContainer.setVisibility(View.VISIBLE);

                                break;
                            case PromotionDetailViewController.SALES_STATUS_4_PROMOTIONING:
                                salesTimeTextView.setVisibility(View.GONE);
                                break;
                            case PromotionDetailViewController.SALES_STATUS_5_PROMOTION_OVER:
                                salesTimeTextView.setText("结束时间: " + model.getString("sales_time"));
                                promotionContainer.setVisibility(View.VISIBLE);
                                break;
                            case PromotionDetailViewController.SALES_STATUS_6_PROMOTION_CANCEL:
                                salesTimeTextView.setText("取消时间: " + model.getString("sales_time"));
                                promotionContainer.setVisibility(View.VISIBLE);
                                setPromotionBtn.setVisibility(View.GONE);
                                break;

                        }
                    }
                } else {
                    productPropertyTextView.setText("");
                    if (selectedPropertyModel != null) {
                        List<Model> selectedModels = selectedPropertyModel.getList("selectedModels", Model.class);
                        if (selectedModels != null) {
                            selectedModels.clear();
                        }
                    }
                    descStr = "";
                    if (model.getList("imgs", String.class) != null && model.getList("imgs", String.class).size() > 0) {
                        String img = model.getList("imgs", String.class).get(0);
                        com.tianpingpai.http.util.ImageLoader.load(img, prodImageView, R.drawable.ic_add_image, R.drawable.ic_add_prod_img);
                    }
                    promotionContainer.setVisibility(View.GONE);
                }

                double price = model.getDouble("price");
                priceEditText.setText("" + price);
                updateUnit(model.getString("unit_name"));
                updatePrice();
                images = model.getList("imgs", String.class);
//                if(images != null && !images.isEmpty()){
//                    for(String url:images){
//                        View cv = getActivity().getLayoutInflater().inflate(R.layout.view_product_edit_image,null);
//                        ImageView iv = (ImageView) cv.findViewById(R.id.product_image_view);
//                        ImageLoader.load(url,iv,R.drawable.default_loading,R.drawable.default_loading);//TODO
//                        imagesContainer.addView(cv,imagesContainer.getChildCount() - 1);
//                    }
//                }

            }
            hideLoading();
        }
    };

    List<String> images;

    @OnClick(R.id.product_name_container)
    private View.OnClickListener productNameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputTextViewController vc = new InputTextViewController();
            vc.setActivity(getActivity());
            vc.setTitle("商品名称");
            vc.setNewRemark(productNameTextView.getText().toString());
            vc.setOnInputCompleteListener(new InputTextViewController.OnInputCompleteListener() {
                @Override
                public void onInputComplete(String text) {
                    productNameTextView.setText(text);
                }
            });
            getActionSheet(true).setViewController(vc);
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).show();
        }
    };

    @OnClick(R.id.unit_container)
    private View.OnClickListener unitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPromotion()) {
                Toast.makeText(ContextProvider.getContext(), "促销中的商品不允许编辑", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(ContextProvider.getContext(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SelectUnitViewController.class);
            intent.putExtra(SelectUnitViewController.KEY_CATEGORY_ID, categoryId);
            intent.putExtra(SelectUnitViewController.KEY_UNIT, selectedUnit);
            intent.putExtra(SelectUnitViewController.KEY_SPEC, selectedSpecs);
            intent.putExtra(SelectUnitViewController.KEY_NET_QUALITY, selectedNetQuality);
            intent.putExtra(SelectUnitViewController.KEY_PRICE, price);
            getActivity().startActivityForResult(intent, REQUEST_CODE_PICK_UNIT);
        }
    };

    @OnClick(R.id.prod_property_container)
    private View.OnClickListener prodPropertyContainerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isPromotion()) {
                Toast.makeText(ContextProvider.getContext(), "促销中的商品不允许编辑", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(ContextProvider.getContext(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, EditProductPropertyViewController.class);
            if (category == null && categoryId == 0) {
                Toast.makeText(ContextProvider.getContext(), "非SKU商品不可编辑商品属性", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (categoryId == 0) {
                    categoryId = category.getInt("categoryId");
                }
                intent.putExtra(EditProductPropertyViewController.KEY_CATEGORY_ID, categoryId);
            }
            intent.putExtra(EditProductPropertyViewController.KEY_SELECTED_ALL_MODEL, selectedPropertyModel);
            if (!TextUtils.isEmpty(descStr)) {
                intent.putExtra(EditProductPropertyViewController.KEY_DESC, descStr);
            }
            getActivity().startActivityForResult(intent, REQUEST_CODE_PICK_PROPERTY);
        }
    };

    @OnClick(R.id.category_container)
    private View.OnClickListener categoryContainerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isAdd) {
                Toast.makeText(ContextProvider.getContext(), "编辑商品时不能选择分类", Toast.LENGTH_LONG).show();
                return;
            }
            if (isPromotion()) {
                Toast.makeText(ContextProvider.getContext(), "促销中的商品不允许编辑", Toast.LENGTH_LONG).show();
                return;
            }
            SelectCategoryViewController vc = new SelectCategoryViewController();
            currentPopupViewController = vc;
            vc.setActionSheet(getActionSheet(true));
            vc.setCategoryId(-1);
            vc.setOnSelectCategoryListener(new SelectCategoryViewController.OnSelectCategoryListener() {
                @Override
                public void onSelectCategory(Model category) {

                    //TODO 是不是还是原来的品类?
                    if (category.getInt("categoryId") != categoryId) {
                        selectedUnit = null;
                        selectedNetQuality = null;
                        selectedSpecs = null;
                    }

                    EditProductViewController.this.category = category;
                    getGoodsInfoByID();
                }
            });
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).setViewController(vc);
            getActionSheet(true).show();
        }
    };

    @OnClick(R.id.coupon_price_text_view)
    private View.OnClickListener couponPriceOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(ContextProvider.getContext(), "不允许编辑促销价", Toast.LENGTH_LONG).show();
            return;
        }
    };

    @OnClick(R.id.product_img_container)
    private View.OnClickListener productImgContainerOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isPromotion()) {
                Toast.makeText(ContextProvider.getContext(), "促销中的商品不允许编辑", Toast.LENGTH_LONG).show();
                return;
            }
            getActivity().startActivityForResult(createDefaultOpenableIntent(), REQUEST_CODE_PICK_IMAGE);
        }
    };

    @OnClick(R.id.set_promotion_btn)
    private View.OnClickListener setPromotionBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ContextProvider.getContext(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SetPromotionViewController.class);
            intent.putExtra(SetPromotionViewController.KEY_PRODUCT, productModel);
            getActivity().startActivityForResult(intent, REQUEST_CODE_SET_PROMOTION);
        }
    };
    @OnClick(R.id.promotion_container)
    private View.OnClickListener promotionContainerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ContextProvider.getContext(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, PromotionDetailViewController.class);
            intent.putExtra(PromotionDetailViewController.KEY_PRODUCT, productModel);
            if (!isAdd && productModel.getPid() != 0) {
                intent.putExtra(PromotionDetailViewController.KEY_PID, productModel.getPid());
            }
            intent.putExtra(PromotionDetailViewController.KEY_SALES_STATUS_INT, salesStatusInt);
            getActivity().startActivity(intent);
        }
    };


    private HttpRequest.ResultListener<ModelResult> submitListener = new HttpRequest.ResultListener<ModelResult>() {
        @Override
        public void onResult(HttpRequest<ModelResult> request, ModelResult data) {
            hideSubmitting();
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), "上传成功", Toast.LENGTH_LONG).show();
                getActivity().finish();
                ProductManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, null);
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_LONG).show();
            }
        }
    };

    @OnClick(R.id.submit_button)
    private View.OnClickListener submitButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO 商品名称

//            if (isPromotion()) {
//                Toast.makeText(ContextProvider.getContext(), "促销中的商品不允许编辑", Toast.LENGTH_LONG).show();
//                return;
//            }

            if (selectedPropertyModel == null || TextUtils.isEmpty(productPropertyTextView.getText().toString())) {
                if( !isPickProperty ){
                    Toast.makeText(ContextProvider.getContext(), "属性不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (selectedUnit == null || TextUtils.isEmpty(unitTextView.getText().toString())) {
                Toast.makeText(ContextProvider.getContext(), "单位不能为空!", Toast.LENGTH_LONG).show();
                return;
            }
            if (localImagePath != null) {
                getQiNiuToken();
            } else {
                submitProduct();
            }
            showSubmitting();

        }
    };

    private HttpRequest.ResultListener<ModelResult<String>> qiNiuTokenListener = new HttpRequest.ResultListener<ModelResult<String>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<String>> request, ModelResult<String> data) {
            if (data.isSuccess()) {
                uploadImage(data.getModel());
            } else {
                setEnabled(true);
                Toast.makeText(ContextProvider.getContext(), "上传图片失败!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void setEnabled(boolean enabled) {

    }

    private void getQiNiuToken() {
        HttpRequest<ModelResult<String>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/image/token", qiNiuTokenListener);
        req.setParser(new ModelParser<>(String.class));
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void uploadImage(String token) {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), "登录后请重试!", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(ContextProvider.getContext(), "正在上传图片,请稍候!", Toast.LENGTH_SHORT).show();
        String curTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mNewImageKey = "saler/" + user.getUserID() + "/prod/" + curTime + ".png";
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
                    submitProduct();
                } else {
                    String error = info.error;
                    if (error.equals("expired token")) {
                        getQiNiuToken();//TODO
                    } else {
                        Toast.makeText(ContextProvider.getContext(), "上传图片错误:" + error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, opt);
    }

    private void submitProduct() {
        String url = ContextProvider.getBaseURL() + "/api/sku/goods/saveGoodsInfo";
        HttpRequest<ModelResult> req = new HttpRequest<>(url, submitListener);
        req.setMethod(HttpRequest.POST);
        req.addParam("goods_name", productNameTextView.getText().toString());
        req.addParam("category_id", categoryId + "");
        price = Double.parseDouble(priceEditText.getText().toString());
        req.addParam("price", "" + price);
        req.addParam("unit_id", selectedUnit.getInt("unit_id") + "");

//        if (salesStatusInt == PromotionDetailViewController.SALES_STATUS_0_NO_PROMOTION) { //没有促销 不显示促销价 不提交到服务器
//
//        }else if( !isPromotion() ){ //不是 正在促销 可编辑促销价
//            String salesPrice = couponPriceEditText.getText().toString();
//            req.addParam("salesPrice", salesPrice);
//        }

        if (productModel != null) {
            req.addParam("product_id", productModel.getId() + "");
        }
        if (imageUrl != null) {
            req.addParam("imgs", imageUrl);
        } else {
            String imgs = "";
            for (String s : images) {
                imgs += s;
                imgs += ",";
            }
            req.addParam("imgs", imgs);
        }

        req.addParam("status", onShelveButton.isChecked() ? "true" : "false");
        if (!TextUtils.isEmpty(descStr)) {
            req.addParam("remarks", descStr);
        }

        String attributes;

        List<Model> attributesOriginal = selectedPropertyModel.getList("selectedModels", Model.class);
        ArrayList<Model> attributesCopy;
        if(attributesOriginal != null) {
            attributesCopy = new ArrayList<>(attributesOriginal);
        }else{
            attributesCopy = new ArrayList<>();
        }
        if(selectedNetQuality != null){
            Model weight = new Model();
            selectedNetQuality.set("type","weight");
            Model.copy(selectedNetQuality,weight,"id","attr_id","value","type","level","unit_id","unit_name");
            attributesCopy.add(weight);
        }
        if(selectedSpecs != null){
            Model spec = new Model();
            selectedSpecs.set("type","stand");
            Model.copy(selectedSpecs,spec,"id","attr_id","value","type","level","unit_id","unit_name");
            attributesCopy.add(spec);
        }
        selectedPropertyModel.setList("selectedModels",attributesCopy);

        if (selectedPropertyModel != null) {
            attributes = selectedPropertyModel.valueString(selectedPropertyModel.getList("selectedModels", Model.class));
        } else {
            attributes = "";
        }
        selectedPropertyModel.setList("selectedModels",attributesOriginal);
        req.addParam("attributes", attributes);
        req.setParser(new GenericModelParser());
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        showSubmitting();
    }


    //TODO move to some other place
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

    private Uri camUri;

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
            prodImageView.setImageURI(null);
            prodImageView.setImageURI(Crop.getOutput(result));
            Log.e("xx","" + Crop.getOutput(result));
            //TODO
            localImagePath = new File(ContextProvider.getContext().getCacheDir(), "cropped").getAbsolutePath();
//            if (productModel != null) {
//                isDelete = !TextUtils.isEmpty(productModel.getImageUrl());
//            } else {
//                isDelete = false;
//            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(ContextProvider.getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUnit(String unit){
//        specsUnitTextView.setText("元/" + unit);
        unitTextView.setText("元/" + unit);
        couponUnitTextView.setText("元/" + unit);
    }

    private void updatePrice(){
        if (selectedNetQuality != null) {
            StringBuilder sb = new StringBuilder(selectedUnit.getString("unit_name"));
            sb.append(" (");
            if (selectedSpecs != null) {
                String value = selectedSpecs.getString("value");
                if(TextUtils.isEmpty(value)){
                    value = selectedSpecs.getString("attr_value");
                }
                if(!TextUtils.isEmpty(value)){
                    sb.append(value);
                }
                sb.append(selectedSpecs.getString("unit_name"));
                sb.append("*");
            }


            if (selectedNetQuality != null) {
                String value = selectedNetQuality.getString("value");
                if(TextUtils.isEmpty(value)){
                    value = selectedNetQuality.getString("attr_value");
                }
                if(!TextUtils.isEmpty(value)){
                    sb.append(value);
                }
                sb.append(selectedNetQuality.getString("unit_name"));
            }
            sb.append(")");

            specsTextView.setText(sb.toString());
        } else {
            if (selectedSpecs != null) {
                StringBuilder sb = new StringBuilder(selectedUnit.getString("unit_name"));
                sb.append(" (");
                String value = selectedSpecs.getString("value");
                if(TextUtils.isEmpty(value)){
                    value = selectedSpecs.getString("attr_value");
                }
                if(!TextUtils.isEmpty(value)){
                    sb.append(value);
                }
                sb.append(selectedSpecs.getString("unit_name"));
                sb.append(")");
                specsTextView.setText(sb.toString());
            }else{
                specsTextView.setText("无");
            }

        }
    }

    private boolean isPickProperty = false;
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
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

        if (requestCode == REQUEST_CODE_PICK_UNIT) {
            Log.e("xx", "data:" + data);
            selectedUnit = data.getParcelableExtra(SelectUnitViewController.KEY_UNIT);
            selectedSpecs = data.getParcelableExtra(SelectUnitViewController.KEY_SPEC);
            selectedNetQuality = data.getParcelableExtra(SelectUnitViewController.KEY_NET_QUALITY);
            price = data.getDoubleExtra(SelectUnitViewController.KEY_PRICE, 0);

//            specsUnitTextView.setText(price + "元/" + selectedUnit.getString("unit_name"));
            priceEditText.setText(""+price);
            updateUnit(selectedUnit.getString("unit_name"));
            updatePrice();
            setIsEditStatus(true);

        }

        if (requestCode == REQUEST_CODE_PICK_PROPERTY) {

            selectedPropertyModel = data.getParcelableExtra(EditProductPropertyViewController.KEY_SELECTED_ALL_MODEL);
            Log.e("xx", "297-----" + selectedPropertyModel);
            String propertyStr = "";
            List<Model> selectedModels = selectedPropertyModel.getList("selectedModels", Model.class);
            int nextAttrID = 0;
            int attr_id = 0;
            if (selectedModels != null) {
                for (int i = 0; i < selectedModels.size(); i++) {
                    Model model = selectedModels.get(i);
                    if (model.getString("name") != null) {
                        propertyStr = propertyStr + model.getString("name") + ": ";
                    }
                    if (model.getString("level") != null) {
                        propertyStr = propertyStr + "【" + model.getString("level") + "】";
                    }

                    if (model.getString("value").length() >= showLength) {
                        propertyStr = propertyStr + model.getString("value").substring(0, showLength) + "...";
                    } else {
                        propertyStr = propertyStr + model.getString("value") + " ";
                    }
                    attr_id = model.getInt("attr_id");
                    if (i != selectedModels.size() - 1) {
                        nextAttrID = selectedModels.get(i + 1).getInt("attr_id");
                    } else {
                        nextAttrID = -1;
                    }
                    if (nextAttrID != attr_id) {
                        propertyStr += "\n";
                    }
                }
            }
            String remark = data.getStringExtra(EditProductPropertyViewController.KEY_DESC);
            if (!TextUtils.isEmpty(remark)) {
                propertyStr += "商品描述:" + remark;
                descStr = remark;
            }
            setIsEditStatus(true);
            productPropertyTextView.setText(propertyStr);
            isPickProperty = true;
        }

        if (requestCode == REQUEST_CODE_SET_PROMOTION) { //设置促销

        }

    }

    String descStr;
    private BaseViewController currentPopupViewController;

    @Override
    public boolean onBackKeyDown(Activity a) {
        if (isEditStatus()) {

//            Toast.makeText(ContextProvider.getContext(), "正在编辑", Toast.LENGTH_LONG).show();

            if (getActionSheet(false) != null && currentPopupViewController != actionSheetDialog) {
                return getActionSheet(false).handleBack(a);
            }

            if (actionSheetDialog == null) {
                actionSheetDialog = new ActionSheetDialog();
                actionSheetDialog.setActionSheet(getActionSheet(true));
                actionSheetDialog.setTitle("正在编辑,是否退出?");
                actionSheetDialog.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        actionSheetDialog.dismiss();
                        getActivity().finish();

                    }
                });
                actionSheetDialog.setNegativeButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionSheetDialog.dismiss();
                    }
                });
            }
            currentPopupViewController = actionSheetDialog;
            actionSheetDialog.show();

            return true;
        }
        return false;
    }

    private boolean isEditStatus;

    private void setIsEditStatus(boolean status) {
        this.isEditStatus = status;
    }

    private boolean isEditStatus() {

        return isEditStatus;

    }

    // 是否促销中 或者 审核成功 状态 如果是 不允许编辑促销价 (也可根据字段 editable 判断)
    private boolean isPromotion() {
        if (salesStatusInt == PromotionDetailViewController.SALES_STATUS_4_PROMOTIONING || salesStatusInt == PromotionDetailViewController.SALES_STATUS_3_EXAMINE_SUCCESS) {
            return true;
        }
        return false;
    }
}
