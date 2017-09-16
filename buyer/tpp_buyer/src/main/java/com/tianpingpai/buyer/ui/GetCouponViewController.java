package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;


@SuppressWarnings("unused")
@Statistics(page = "商品评价")
@ActionBar(title = "商品评价")
@Layout(id = R.layout.vc_get_coupon)
public class GetCouponViewController extends BaseViewController {

    public final static String keyCoupon = "key.coupon";

    @Binding(id = R.id.no_have_score_container)
    private View noHaveScoreContainer;
    @Binding(id = R.id.have_score_container)
    private View haveScoreContainer;

    @Binding(id = R.id.no_score_coupon_text_view)
    private TextView noScoreCouponTextView;
    @Binding(id = R.id.order_score_text_view)
    private TextView orderScoreTextView;
    @Binding(id = R.id.coupon_text_view)
    private TextView couponTextView;
    @Binding(id = R.id.coupon_number_text_view)
    private TextView couponNumberTextView;
    @Binding(id = R.id.score_number_text_view)
    private TextView scoreNumberTextView;
    @Binding(id = R.id.check_coupon_text_view)
    private TextView checkCouponTextView;
    @Binding(id = R.id.hint_text_view)
    private TextView hintTextView;

    private Model couponModel;

    private int couponType;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        Bundle bundle = getActivity().getIntent().getExtras();
        couponModel = bundle.getParcelable("couponData");

        Log.e("couponData===", couponModel.toString());

        couponType = couponModel.getInt("couponType");
        Model orderScoreTitle = couponModel.getModel("orderScore");

        if (orderScoreTitle == null) {
            noHaveScoreContainer.setVisibility(View.VISIBLE);
            haveScoreContainer.setVisibility(View.GONE);
            initView1();
        } else {
            noHaveScoreContainer.setVisibility(View.GONE);
            haveScoreContainer.setVisibility(View.VISIBLE);
            initView2(orderScoreTitle);
        }
    }

    private void initView1() {
        Model couponTitle = couponModel.getModel("rewardContent");
        if (couponTitle != null) {
            String before = couponTitle.getString("before");
            String value = couponTitle.getString("value");
            String after = couponTitle.getString("after");

            String text = before + value + after;
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            ssb.setSpan(new ForegroundColorSpan(Color.RED),before.length(),before.length()+value.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            noScoreCouponTextView.setText(ssb);

        }else {

        }
        String rewardToScore = couponModel.getString("rewardToScore");
        scoreNumberTextView.setText(rewardToScore);
        Log.e("rewardToScore===", rewardToScore+"");
        if(!TextUtils.isEmpty(rewardToScore)){
            scoreNumberTextView.setVisibility(View.VISIBLE);
            Log.e("rewardToScore===","VISIBLE");
        }
        couponNumberTextView.setText(couponModel.getString("reward"));
        checkCouponTextView.setText(couponModel.getString("urlContent"));
        hintTextView.setText(couponModel.getString("hint"));
    }


    private void initView2(Model orderSModel) {

        String orderBefore = orderSModel.getString("before");
        String orderValue = orderSModel.getString("value");
        String orderAfter = orderSModel.getString("after");

        String orderScoreText = orderBefore + orderValue + orderAfter;
        SpannableStringBuilder orderScoreSSB = new SpannableStringBuilder(orderScoreText);
        orderScoreSSB.setSpan(new ForegroundColorSpan(Color.RED),orderBefore.length(),orderBefore.length()+orderValue.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        orderScoreTextView.setText(orderScoreSSB);

        Model couponTitle = couponModel.getModel("rewardContent");
        if (couponTitle != null) {

            String couponBefore = couponTitle.getString("before");
            String couponValue = couponTitle.getString("value");
            String couponAfter = couponTitle.getString("after");

            String text = couponBefore + couponValue + couponAfter;
            SpannableStringBuilder couponSSB = new SpannableStringBuilder(text);
            couponSSB.setSpan(new ForegroundColorSpan(Color.RED),couponBefore.length(),couponBefore.length()+couponValue.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            couponTextView.setText(couponSSB);

        }else{

        }
        String rewardToScore = couponModel.getString("rewardToScore");
        scoreNumberTextView.setText(rewardToScore);
        Log.e("rewardToScore===",rewardToScore+"");
        if(!TextUtils.isEmpty(rewardToScore)){
            scoreNumberTextView.setVisibility(View.VISIBLE);
            Log.e("rewardToScore2===", "VISIBLE");
        }
        couponNumberTextView.setText(couponModel.getString("reward"));
        checkCouponTextView.setText(couponModel.getString("urlContent"));
        hintTextView.setText(couponModel.getString("hint"));
    }


    @OnClick(R.id.go_coupon_view)
    private View.OnClickListener goCouponListOnClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UserModel user = UserManager.getInstance().getCurrentUser();

            if (1 == couponType) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, CouponListViewController.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            } else if (2 == couponType) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                String url = couponModel.getString("viewUrl");
                intent.putExtra(WebViewController.KEY_URL,url);
                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_NORMAL);
                getActivity().startActivity(intent);
                getActivity().finish();

            }
        }
    };

}
