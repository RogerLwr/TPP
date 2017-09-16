package com.tianpingpai.viewController;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.CollectionProductAdapter;
import com.tianpingpai.buyer.adapter.CommonUseProductAdapter;
import com.tianpingpai.buyer.adapter.ProductAdapter;
import com.tianpingpai.buyer.manager.ShoppingCartEvent;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.buyer.ui.ResultHandler;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.tools.Tools;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetMessage;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.PriceFormat;
import com.tianpingpai.widget.TagCloudView;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Layout(id = R.layout.vc_prod_detail)
public class ProdDetailViewController extends BaseViewController {


    @Binding(id = R.id.attr_view_group)
    private TagCloudView attrViewGroup;
    @Binding(id = R.id.attr_view)
    private LinearLayout attrView;
    @Binding(id = R.id.name_text_view, format = "{{productName}}")
    private TextView nameTextView;
    @Binding(id = R.id.price_text_view, format = "{{couponPrice}}/{{unit}}")
    private TextView priceTextView;
    @Binding(id = R.id.original_price_text_view,format = "{{couponPrice}}/{{unit}}")
    private TextView originalPriceTextView;
    @Binding(id = R.id.standard_text_view,format = "{{standWeight}}")
    private TextView standardTextView;
    //TODO
    @Binding(id = R.id.limit_text_view,format = "{{limitType}}{{limitNum}}{{unit}}")
    private TextView limitTextView;
    @Binding(id = R.id.stock_container)
    private View stockContainer;
    @Binding(id = R.id.stock_text_view,format = "库存{{stock}}{{unit}}")
    private TextView stockTextView;
    @Binding(id = R.id.time_text_view,format = "{{days}}天{{hours}}时{{minutes}}分")
    private TextView timeTextView;
    @Binding(id = R.id.sale_num_text_view, format = "已售{{quantity}}{{unit}}")
    private TextView saleNumberTextView;
    @Binding(id = R.id.des_text_view, format = "商品描述: {{description}}")
    private TextView desTextView;
    @Binding(id = R.id.original_price_container)
    private View originalPriceContainer;

    private TextView numberTextView;
    private View minusButton;
    @Binding(id = R.id.promotion_container)
    private View promotionContainer;
    @Binding(id = R.id.promotion_price_text_view,format = "{{salesPrice}}")
    private TextView promotionPriceTextView;
    @Binding(id = R.id.promotion_unit_text_view,format = "/{{unit}}")
    private TextView promotionUnitTextView;

    @Binding(id = R.id.time_status_text_view)
    private TextView timeStatusTextView;

    @Binding(id = R.id.bottom_container)
    private View bottomContainer;
    @Binding(id = R.id.price_sum_text_view)
    private TextView priceSumTextView;

    private ImageView prodImageView;
    private ImageView collectButton;

    public ActionSheetMessage actionSheetMessage;

    private Model prodModel;
    private boolean isAdded = false;
    private long prodId;

    public void setShowButtonContainer(boolean showButtonContainer) {
        this.showButtonContainer = showButtonContainer;
    }

    private boolean showButtonContainer = false;

    private ProductAdapter prodAdapter;

    public void setProdAdapter(ProductAdapter prodAdapter) {
        this.prodAdapter = prodAdapter;
    }

    private CollectionProductAdapter collectionProductAdapter;
    public void setCollectionProductAdapter(CollectionProductAdapter collectionProductAdapter){
        this.collectionProductAdapter = collectionProductAdapter;
    }

    private CommonUseProductAdapter commonUseProductAdapter;

    public void setCommonUseProductAdapter(CommonUseProductAdapter commonUseProductAdapter) {
        this.commonUseProductAdapter = commonUseProductAdapter;
    }

    private CommonUseProductAdapter.ProductNumberChangeListener numChangeListener;

    public void setProductNumberChangeListener(CommonUseProductAdapter.ProductNumberChangeListener l) {
        this.numChangeListener = l;
    }

    public void setProdId(long prodId) {
        this.prodId = prodId;
    }
    private ProductModel mModel;
    public void setModel(ProductModel mModel){
        this.mModel = mModel;
    }

    private Model collectModel ;
    public void setCollectModel(Model model){
        this.collectModel = model;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        hideActionBar();
        getData(prodId);
        numberTextView = (TextView) rootView
                .findViewById(R.id.number_text_view);
        numberTextView.setOnClickListener(numberTextListener);
        if(mModel.getProductNum() != 0){
            numberTextView.setText(String.valueOf(mModel.getProductNum()));
        }

        minusButton = rootView.findViewById(R.id.minus_button);
        rootView.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 1;
                try{
                    num = Integer.parseInt(numberTextView.getText().toString());
                    num++;
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
                num = Math.max(1, num);
                setProductNum(num,true);
                if(numChangeListener != null){
                    numChangeListener.onProductNumberChange(mModel);
                }
                updateBottomPanel(num);
            }
        });

        rootView.findViewById(R.id.minus_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 0;
                try {
                    num = Integer.parseInt(numberTextView.getText().toString());
                    num--;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                num = Math.max(0, num);
                setProductNum(num,true);
                if(numChangeListener != null){
                    numChangeListener.onProductNumberChange(mModel);
                }
                updateBottomPanel(num);
            }
        });

        prodImageView = (ImageView)rootView.findViewById(R.id.prod_img);
        collectButton = (ImageView)rootView.findViewById(R.id.collect_button);

        originalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );
        prodImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //TODO
                if(Build.VERSION.SDK_INT >= 16) {
                    prodImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else{
                    prodImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int height = (int) (470 / 750.0f * getView().getWidth());
                prodImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
            }
        });

        if(showButtonContainer){
            bottomContainer.setVisibility(View.VISIBLE);
        }

        ShoppingCartManager.getInstance().registerListener(cartListener);
        ActionSheet as = (ActionSheet) getViewTransitionManager();
        as.setActionSheetListener(new ActionSheet.ActionSheetListener() {
            @Override
            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                ShoppingCartManager.getInstance().unregisterListener(cartListener);
            }
        });
    }

    private ModelStatusListener<ShoppingCartEvent, ProductModel> cartListener = new ModelStatusListener<ShoppingCartEvent, ProductModel>() {
        @Override
        public void onModelEvent(ShoppingCartEvent event, ProductModel model) {
            switch (event){
                case OnSync:
                    Toast.makeText(ContextProvider.getContext(),"成功加入购物车",Toast.LENGTH_LONG).show();
                    break;
                case OnFailure:
                    Toast.makeText(ContextProvider.getContext(),"加入购物车失败!",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @SuppressWarnings("unused")
    @OnClick(R.id.close_button)
    private View.OnClickListener onCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(collectModel != null){
                collectModel.set("prod_num",mModel.getProductNum());
            }
            ActionSheet as = (ActionSheet)getViewTransitionManager();
            if(prodAdapter != null){
                prodAdapter.notifyDataSetChanged();
            }
            if(commonUseProductAdapter != null){
                commonUseProductAdapter.notifyDataSetChanged();
            }
            if(collectionProductAdapter !=null){
                collectionProductAdapter.notifyDataSetChanged();
                ShoppingCartManager.getInstance().notifyEvent(ShoppingCartEvent.OnProductNumberChange,null);
            }
            as.dismiss();
        }
    };

    private View.OnClickListener numberTextListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            View vv = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_number, null);
            final Dialog d = new AlertDialog.Builder(getActivity()).setView(vv).create();
            final EditText numberEditText = (EditText) vv.findViewById(R.id.number_edit_text);
            if(mModel.getProductNum() != 0){
                numberEditText.setText(String.valueOf(mModel.getProductNum()));
            }

            vv.findViewById(R.id.decrease_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = 0;
                    try{
                        num = Integer.parseInt(numberEditText.getText().toString());
                        num--;
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    num = Math.max(0, num);
                    numberEditText.setText(String.valueOf(num));
                    updateBottomPanel(num);
                }
            });

            vv.findViewById(R.id.increase_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = 1;
                    try{
                        num = Integer.parseInt(numberEditText.getText().toString());
                        num++;
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    num = Math.max(1, num);
                    updateBottomPanel(num);
                }
            });

            vv.findViewById(R.id.okay_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = 0;
                    if(!TextUtils.isEmpty(numberEditText.getText())){
                        try {
                            num = Integer.parseInt(numberEditText.getText()
                                    .toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();// TODO
                            num = 0;
                        }
                    }
                    setProductNum(num, true);
                    updateBottomPanel(num);
                    if(numChangeListener != null){
                        numChangeListener.onProductNumberChange(mModel);
                    }
                    d.dismiss();

                }
            });

            vv.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
            numberEditText.setSelectAllOnFocus(true);
            Tools.softInput();

        }
    };

    private void setProductNum(int num, boolean notify){
        if(num == 0){
            numberTextView.setText("");
        }else{
            numberTextView.setText(String.valueOf(num));
        }
        mModel.setProductNum(num);
        if(notify){
            ShoppingCartManager.getInstance().mark(mModel);
        }

        if(num > 0){
            minusButton.setVisibility(View.VISIBLE);
        }else{
            minusButton.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.collect_button)
    private View.OnClickListener onIsAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isAdded){
                delete(prodModel);
            }else {
                addCommonUse(prodModel);
            }
        }
    };

    private void getData(long productId){

        String url = ContextProvider.getBaseURL() + "/api/prod/detail.json";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("id", "" + productId);
        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        //			req.setErrorListener(new CommonErrorHandler(this));
        showLoading();
        VolleyDispatcher.getInstance().dispatch(req);

    }
    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {

        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request,
                             ModelResult<Model> data) {
            if(data.isSuccess()){
                getBinder().bindData(data.getModel());

                isAdded = data.getModel().getBoolean("common");

                prodModel = data.getModel();
                mModel.setCategoryId(prodModel.getInt("pcategory_id"));
                if( !TextUtils.isEmpty(prodModel.getString("image")) ){
                    WindowManager wm = (WindowManager) ContextProvider.getContext().getSystemService(Context.WINDOW_SERVICE);
                    int width = wm.getDefaultDisplay().getWidth();
                    DisplayMetrics dm = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(dm);
                    int width2 = dm.widthPixels;
                    float density = dm.density;
                    Log.w("xx", "195-----------width="+width+",width2="+width2+",density="+density);
//                    ViewGroup.LayoutParams params = prodImageView.getLayoutParams();
//                    params.width = (int)(density*200);
//                    params.height = (int)(density*160);
                    prodImageView.setVisibility(View.VISIBLE);
                    ImageLoader.load(prodModel.getString("image"), prodImageView);
                }else{
                    prodImageView.setImageResource(R.drawable.ic_151111_prod_img_default);
                }

                if(isAdded){
                    collectButton.setImageResource(R.drawable.ic_collected);
                }else {
                    collectButton.setImageResource(R.drawable.ic_collect);
                }

                hideLoading();
                TLog.e("xx", "95-----------prodName=" + prodModel.getString("prod_name"));

                boolean sales = prodModel.getBoolean("sales");
                boolean isStarted = !prodModel.getBoolean("soon");
                int green = getActivity().getResources().getColor(R.color.green);
                int orange = getActivity().getResources().getColor(R.color.orange);
                final List<String> list = prodModel.getList("attrInfo",String.class);
                boolean standWeightEmpty = com.tianpingpai.utils.TextUtils.isEmpty(prodModel.getString("standWeight"));
                if(sales){
                    promotionContainer.setVisibility(View.VISIBLE);
                    if(isStarted){
                        priceTextView.setVisibility(View.GONE);
                        timeStatusTextView.setText("距结束仅剩:");
                        promotionContainer.setBackgroundColor(Color.parseColor("#ec3d62"));
                    }else{
                        timeStatusTextView.setText("距开始还有:");
                        promotionContainer.setBackgroundColor(Color.parseColor("#0baf78"));
                        originalPriceContainer.setVisibility(View.GONE);
                    }
                    //限购
                    int limit = prodModel.getInt("limitNum");
                    if(limit > 0){
                        SpannableStringBuilder limitStringBuilder = new SpannableStringBuilder("限购");
                        limitStringBuilder.append(String.valueOf(limit));
                        limitStringBuilder.setSpan(new ForegroundColorSpan(green),2,limitStringBuilder.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        limitStringBuilder.append(prodModel.getString("unit"));
                        limitTextView.setText(limitStringBuilder);
                    }else{
                        limitTextView.setVisibility(View.GONE);
                    }

                    if(standWeightEmpty && limit <= 0){
                        standardTextView.setVisibility(View.GONE);
                    }
                }else{
                    promotionContainer.setVisibility(View.GONE);
                    timeTextView.setVisibility(View.GONE);
                    originalPriceTextView.setVisibility(View.GONE);
                    standardTextView.setVisibility(View.GONE);
                    stockContainer.setVisibility(View.GONE);
                    String text = prodModel.getString("couponPrice") + "/" + prodModel.getString("unit");
                    priceTextView.setText(text);
                }

                if(list != null && !list.isEmpty()){
                    StringBuilder sb = new StringBuilder();
                    for(String s:list){
                        sb.append(s);
                        sb.append("\n");
                    }
                    desTextView.setText(sb.toString());
                }else{
                    desTextView.setText(prodModel.getString("description"));
                }

                if(standWeightEmpty){
                    standardTextView.setVisibility(View.GONE);
                }

                attrViewGroup.setTags(list);
            }
        }
    };

    private HttpRequest.ResultListener<ModelResult<Model>> deleteListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request,
                             ModelResult<Model> data) {
            if(data.isSuccess()){
                isAdded = false;
                collectButton.setImageResource(R.drawable.ic_collect);
                Toast.makeText(ContextProvider.getContext(), "取消收藏成功！", Toast.LENGTH_SHORT).show();
                if (collectionProductAdapter != null) {
                    collectionProductAdapter.getModels().remove(collectModel);
                    collectionProductAdapter.notifyDataSetChanged();
                }

            }else{
                ResultHandler.handleError(data,ProdDetailViewController.this);
            }
            hideLoading();
        }
    };

    private void delete(Model prodModel){
        String url = URLUtil.DEL_COMMONUSE_URL;
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, deleteListener);
        UserModel user = UserManager.getInstance().getCurrentUser();
        if(user != null){
            req.addParam("accessToken", user.getAccessToken());
        }

        req.addParam("saler_id", prodModel.getString("sale_user_id") + "");
        req.setAttachment(prodModel);
        req.addParam("prod_id", prodModel.getLong("id") + "");
        req.setMethod(HttpRequest.POST);
        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void addCommonUse(Model prodModel){
        String url = URLUtil.ADD_COMMONUSE_URL;

        UserModel user = UserManager.getInstance().getCurrentUser();
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,submitListener);
        req.addParam("saler_id", prodModel.getString("sale_user_id") + "");
        req.addParam("prod_id", prodModel.getLong("id") + "");
        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        if(user != null){
            req.addParam("accessToken", user.getAccessToken());
        }
        req.setErrorListener(new CommonErrorHandler(ProdDetailViewController.this));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> submitListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request,
                             ModelResult<Model> data) {
            if(data.isSuccess()){
                isAdded = true;
                collectButton.setImageResource(R.drawable.ic_collected);
                Toast.makeText(ContextProvider.getContext(), "收藏成功！", Toast.LENGTH_SHORT).show();
            }else{
                ResultHandler.handleError(data,ProdDetailViewController.this);
            }
            hideSubmitting();
        }

    };

    @OnClick(R.id.add_to_cart_button)
    private View.OnClickListener addToCartButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int productNumber;
            try{
                productNumber = Integer.parseInt(numberTextView.getText().toString());
            }catch(NumberFormatException e){
                productNumber = 0;
            }

            if(productNumber == 0){
                Toast.makeText(ContextProvider.getContext(), "商品数量不能为0", Toast.LENGTH_SHORT).show();
                return;
            }

            mModel.setProductNum(productNumber);
            TLog.e("xx", "496--------mModel="+mModel);
            ArrayList<ProductModel> products = new ArrayList<>();
            products.add(mModel);
            ShoppingCartManager.getInstance().addToShoppingCart(products);

        }
    };

    private void updateBottomPanel(int num){
        double sum = mModel.getCouponPrice() * num;
        priceSumTextView.setText("￥" + PriceFormat.format(sum));
    }
}
