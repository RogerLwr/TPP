package com.tianpingpai.seller.ui;


import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

/**
 * 团购详情 页面
 */
@ActionBar(title = "团购详情")
@Statistics(page = "团购详情")
@Layout(id = R.layout.view_controller_groupon_detail)
public class GroupDetailViewController extends BaseViewController {

    public static final String KEY_GROUP_ID = "key.group_id";

    private ProductModel productModel;
    private int groupId;

    @Binding(id = R.id.group_price_text_view, format = "¥{{group_price}}")
    private TextView groupPriceTextView;
    @Binding(id = R.id.price_text_view, format = "¥{{price}}")
    private TextView priceTextView;

    @Binding(id = R.id.num_text_view, format = "{{num}}")
    private TextView numTextView;

    @Binding(id = R.id.group_num_text_view, format = "{{group_num}}")
    private TextView groupNumTextView;

    @Binding(id = R.id.limit_num_text_view, format = "{{limit_num}}")
    private TextView limitNumTextView;
//
    @Binding(id = R.id.start_time_text_view, format = "{{start_time}}")
     private TextView startTimeTextView;
    @Binding(id = R.id.end_time_text_view, format = "{{_end_time}}")
     private TextView endTimeTextView;

//    Binder binder = new Binder();

    int salesStatusInt;
    public static final String KEY_SALES_STATUS_INT = "key.statusInt";

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        salesStatusInt = a.getIntent().getIntExtra(KEY_SALES_STATUS_INT, 0);
        groupId = a.getIntent().getIntExtra(KEY_GROUP_ID, 0);

    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
//        binder.bindView(this, rootView);
        showContent();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void loadData() {
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/groupbuy/info", groupDetailListener);

        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        if(groupId != 0){
            req.addParam("group_id", groupId + "");
        }
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    HttpRequest.ResultListener<ModelResult<Model>> groupDetailListener  = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                data.getModel().set("_end_time", data.getModel().getString("end_time"));
                getBinder().bindData(data.getModel());
            }
            hideLoading();
        }
    };

}