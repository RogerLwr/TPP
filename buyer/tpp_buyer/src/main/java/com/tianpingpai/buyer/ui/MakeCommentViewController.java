package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

@SuppressWarnings("unused")
@Statistics(page = "评价")
@ActionBar(title = "评价")
@Layout(id = R.layout.ui_make_comment)
public class MakeCommentViewController extends BaseViewController {

    public static final String KEY_ORDER_ID = "key.orderID";
    public static final String KEY_SELLER_ID = "key.sellerID";
    public static final String KEY_STORE_NAME = "key.storeName";

    @Binding(id = R.id.store_name_text_view)
    private TextView storeNameTextView;
    @Binding(id = R.id.quality_rating_bar)
    private RatingBar qualityRatingBar;
    @Binding(id = R.id.quality_text_view)
    private TextView qualityTextView;
    @Binding(id = R.id.deliver_speed_rating_bar)
    private RatingBar deliverSpeedRatingBar;
    @Binding(id = R.id.deliver_speed_text_view)
    private TextView deliverSpeedTextView;
    @Binding(id = R.id.service_rating_bar)
    private RatingBar serviceRatingBar;
    @Binding(id = R.id.service_text_view)
    private TextView serviceTextView;
    @Binding(id = R.id.comment_edit_text)
    private EditText commentEditText;
    @Binding(id = R.id.checkbox_button)
    private ImageView checkboxButton;

    private Binder binder = new Binder();

    private long orderId;
    private String sellerId;
    private String storeName;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        orderId = a.getIntent().getLongExtra(KEY_ORDER_ID, -1);
        sellerId = a.getIntent().getStringExtra(KEY_SELLER_ID);
        storeName = a.getIntent().getStringExtra(KEY_STORE_NAME);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        binder.bindView(this, rootView);
        storeNameTextView.setText(storeName);

        qualityRatingBar.setOnRatingBarChangeListener(new RatingChangeListener(qualityTextView));
        deliverSpeedRatingBar.setOnRatingBarChangeListener(new RatingChangeListener(deliverSpeedTextView));
        serviceRatingBar.setOnRatingBarChangeListener(new RatingChangeListener(serviceTextView));

        qualityRatingBar.setRating(0);
        deliverSpeedRatingBar.setRating(0);
        serviceRatingBar.setRating(0);
        showContent();
    }

    @OnClick(R.id.submit_button)
    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null){
                return;
            }

            String content = commentEditText.getText().toString().trim();
//            if(TextUtils.isEmpty(content)){
//                Toast.makeText(ContextProvider.getContext(),"评价内容不能为空!",Toast.LENGTH_SHORT).show();
//                return;
//            }
            int quality = (int)qualityRatingBar.getRating()*2;
            int deliver = (int)deliverSpeedRatingBar.getRating()*2;
            int service = (int)serviceRatingBar.getRating()*2;

            if(quality < 1 ){
                Toast.makeText(ContextProvider.getContext(),"请给商品质量打分",Toast.LENGTH_SHORT).show();
                return;
            }
            if(deliver < 1 ){
                Toast.makeText(ContextProvider.getContext(),"请给发货速度打分",Toast.LENGTH_SHORT).show();
                return;
            }
            if(service < 1 ){
                Toast.makeText(ContextProvider.getContext(),"请给服务态度打分",Toast.LENGTH_SHORT).show();
                return;
            }
            HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLUtil.ADD_COMMENT_URL,submitListener);
            req.setMethod(HttpRequest.POST);
            req.addParam("saler_id", String.valueOf(sellerId));
            req.addParam("user_id", user.getUserID());//TODO
            req.addParam("order_id", String.valueOf(orderId));
            req.addParam("content", content);
            req.addParam("speed", String.valueOf(deliver));
            req.addParam("service", String.valueOf(service));
            req.addParam("quality", String.valueOf(quality));
            req.addParam("anonymous",String.valueOf(anonymous));
            req.setErrorListener(new CommonErrorHandler(MakeCommentViewController.this));
            req.setParser(new GenericModelParser());
            VolleyDispatcher.getInstance().dispatch(req);
            showSubmitting();
        }
    };

    public HttpRequest.ResultListener<ModelResult<Model>> submitListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, final ModelResult<Model> data) {
            hideSubmitting();
            if(data.isSuccess()){
                OrderModel orderModel = new OrderModel();
                orderModel.setId(orderId);
                OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, orderModel);
                Model couponModel = data.getModel();

                int toWhere = couponModel.getInt("toWhere");
                if(1 == toWhere){//去APP拆红包
                    Intent intent = new Intent(getActivity(),ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, GetCouponViewController.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("couponData",couponModel);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().finish();

                }else if(2 == toWhere){//去网页拆红包
                    UserModel userModel = UserManager.getInstance().getCurrentUser();
                    Intent intent = new Intent(getActivity(),ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
                    String couponUrl = URLApi.getBaseUrl()+"/apply/bonus/coupon?order_id="+orderId;
                    intent.putExtra(WebViewController.KEY_URL,couponUrl);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }else{
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    private class RatingChangeListener implements RatingBar.OnRatingBarChangeListener{
        private TextView textView;
        RatingChangeListener(TextView tv){
            this.textView = tv;
        }

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

            if(rating <= 3.0){
                textView.setText("差评");
            }else if( rating == 4.0 ){
                textView.setText("中评");
            }else if( rating >= 4.0  ){
                textView.setText("好评");
            }


        }
    }

    boolean isCheck = true;
    private int anonymous = 2;
    @OnClick(R.id.checkbox_button)
    private View.OnClickListener checkboxButtonOnClick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(isCheck){
                checkboxButton.setImageResource(R.drawable.checkbox_unchecked);
                isCheck = false;
                anonymous = 1;
            }else {
                checkboxButton.setImageResource(R.drawable.checkbox_checked);
                anonymous = 2;
                isCheck = true;
            }
        }
    };
}
