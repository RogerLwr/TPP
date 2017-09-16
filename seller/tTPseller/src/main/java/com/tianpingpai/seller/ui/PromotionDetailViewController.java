package com.tianpingpai.seller.ui;


import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.ProductManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

/**
 * 设置 促销 页面
 */
@ActionBar(title = "审核详情")
@Statistics(page = "促销详情")
@Layout(id = R.layout.view_controller_promotion_detail)
public class PromotionDetailViewController extends BaseViewController {

    public static final String KEY_PRODUCT = "key.product";
    public static final String KEY_PID = "key.pid";

    private ProductModel productModel;
    private long pid;

    @Binding(id = R.id.examine_container)
    private View examineContainer;
    @Binding(id = R.id.reason_text_view)
    private TextView reasonTextView;
    @Binding(id = R.id.time_text_view)
    private TextView timeTextView;

    @Binding(id = R.id.specs_unit_text_view, format = "{{price}}")
    private TextView priceTextView;
    @Binding(id = R.id.number_text_view, format = "{{number}}")
    private TextView numberTextView;
    @Binding(id = R.id.begin_end_time_text_view, format = "{{startTime}}日至 {{_endTime}}日")
    private TextView beginEndTimeTextView;

    @Binding(id = R.id.limit_container)
    private View limitContainer;
    @Binding(id = R.id.limit_type_text_view, format = "{{limitType}}")
    private TextView limitTypeTextView;
    @Binding(id = R.id.limit_num_text_view, format = "{{limitNum}}")
    private TextView limitNumTextView;

    @Binding(id = R.id.reset_promotion_btn)
            private Button resetPromotionBtn;
    int salesStatusInt;
    public static final String KEY_SALES_STATUS_INT = "key.statusInt";
    /**
     * 0不是优惠商品 1待审核(促销审核中)  2 审核失败  3审核成功    4促销中  5促销结束  6促销被取消     已卖光,
     */
    public static final int SALES_STATUS_0_NO_PROMOTION = 0;
    public static final int SALES_STATUS_1_WAIT_EXAMINE = 1;
    public static final int SALES_STATUS_2_EXAMINE_FAIL = 2;
    public static final int SALES_STATUS_3_EXAMINE_SUCCESS = 3;
    public static final int SALES_STATUS_4_PROMOTIONING = 4;
    public static final int SALES_STATUS_5_PROMOTION_OVER = 5;
    public static final int SALES_STATUS_6_PROMOTION_CANCEL = 6;

    private ModelStatusListener<ModelEvent, ProductModel> onModelStatusChangeListener = new ModelStatusListener<ModelEvent, ProductModel>() {
        @Override
        public void onModelEvent(ModelEvent event, ProductModel model) {
            switch (event){
                case OnModelUpdate:
                    if(getActivity() != null){
                        getActivity().finish();
                    }
                    break;
            }
        }
    };


    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        productModel = (ProductModel) a.getIntent().getSerializableExtra(KEY_PRODUCT);
        salesStatusInt = a.getIntent().getIntExtra(KEY_SALES_STATUS_INT, 0);
        pid = a.getIntent().getLongExtra(KEY_PID, 0);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        ProductManager.getInstance().registerListener(onModelStatusChangeListener);
        getPromotionDetail();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProductManager.getInstance().unregisterListener(onModelStatusChangeListener);
    }



    private void getPromotionDetail() {
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/sales/detail", promotionRuleListener);

        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        if( pid != 0){
            req.addParam("pid", pid + "");
        }
        req.addParam("prod_id", productModel.getId() + "");
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }


    private HttpRequest.ResultListener<ModelResult<Model>> promotionRuleListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                Model model = data.getModel();
                Model detailModel = model.getModel("detail");

                if(salesStatusInt == 0){
                    salesStatusInt = detailModel.getInt("statusInt");
                }
                if(salesStatusInt == SALES_STATUS_2_EXAMINE_FAIL || salesStatusInt == SALES_STATUS_6_PROMOTION_CANCEL || salesStatusInt == SALES_STATUS_5_PROMOTION_OVER){
                    resetPromotionBtn.setVisibility(View.VISIBLE);
                }else {
                    resetPromotionBtn.setVisibility(View.GONE);
                }

                detailModel.set("_endTime", detailModel.getString("endTime"));

                if( TextUtils.isEmpty(detailModel.getString("reason"))){
                    examineContainer.setVisibility(View.GONE);
                }else{
                    reasonTextView.setText(detailModel.getString("reason"));
                    if(salesStatusInt == SALES_STATUS_2_EXAMINE_FAIL){
                        timeTextView.setText(detailModel.getString("examineTime"));
                    }else if(salesStatusInt == SALES_STATUS_5_PROMOTION_OVER){

                    }else if(salesStatusInt == SALES_STATUS_6_PROMOTION_CANCEL){
                        timeTextView.setText(detailModel.getString("cancelTime"));
                    }
                }
                if( TextUtils.isEmpty(detailModel.getString("limitType"))){
                    limitContainer.setVisibility(View.GONE);
                }

                getBinder().bindData(detailModel);

                setTitle(detailModel.getString("status"));
            }else{
                ResultHandler.handleError(data,PromotionDetailViewController.this);
            }
            hideLoading();
        }
    };

    @OnClick(R.id.reset_promotion_btn)
    View.OnClickListener resetPromotionBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ContextProvider.getContext(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SetPromotionViewController.class);
            intent.putExtra(SetPromotionViewController.KEY_PRODUCT, productModel);
            if(salesStatusInt == SALES_STATUS_2_EXAMINE_FAIL){
                intent.putExtra(SetPromotionViewController.KEY_IS_EXAMINE_FAIL, true);
            }
            getActivity().startActivity(intent);
        }
    };


}