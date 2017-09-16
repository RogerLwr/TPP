package com.tianpingpai.buyer.adapter;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.utils.ColorUtil;
import com.tianpingpai.widget.StarRatingBar;

import java.util.List;

public class StoreAdapter extends ModelAdapter<Model> {

    public Activity mActivity;

    public void setShowCoupons(boolean showCoupons) {
        this.showCoupons = showCoupons;
    }

    private boolean showCoupons = true;

    public void setActivity(Activity a){
        this.mActivity = a;
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new StoreViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    private class StoreViewHolder implements ViewHolder<Model>{
        private Model model;
        @Binding(id = R.id.logo_image_view)
        private ImageView logoImageView;
        @Binding(id = R.id.store_name_text_view,format = "{{sale_name}}")
        private TextView storeNameTextView;
        @Binding(id = R.id.rating_bar)
        private StarRatingBar ratingBar;
        @Binding(id = R.id.address_text_view)
        TextView addrTextView;
        @Binding(id = R.id.description_text_view)
        private TextView descriptionTextView;
        @Binding(id = R.id.starting_price_text_view)
        private TextView startingPriceTextView;
        @Binding(id = R.id.sale_number_text_view)
        private TextView saleNumberTextView;
        @Binding(id = R.id.fav_number_text_view)
        private TextView favNumberTextView;
        @Binding(id = R.id.coupon_container)
        private LinearLayout couponContainer;
        @Binding(id = R.id.rating_bar_container)
        private View ratingBarContainer;
        @Binding(id = R.id.store_rest)
        private TextView storeRest;

        private Binder binder = new Binder();

        private StoreViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_store, null);
            binder.bindView(this,view);
            ratingBar.setFullStartRes(R.drawable.star_big);
            ratingBar.setEmptyStarRes(R.drawable.star_empty);
        }

        private View view;

        @Override
        public void setModel(Model model) {
            this.model = model;
            binder.bindData(model);

            if(1 == model.getInt("rest")){
                ratingBarContainer.setVisibility(View.VISIBLE);
                storeRest.setText(""+model.getString("salerStatusDesc"));
                storeRest.setVisibility(View.GONE);
            }else if(2 == model.getInt("rest")){
                ratingBarContainer.setVisibility(View.GONE);
                storeRest.setText(""+model.getString("salerStatusDesc"));
                storeRest.setVisibility(View.VISIBLE);
            }else {
                ratingBarContainer.setVisibility(View.VISIBLE);
                storeRest.setText(""+model.getString("salerStatusDesc"));
                storeRest.setVisibility(View.GONE);
            }

            String description = model.getString("description");
            if(TextUtils.isEmpty(description)){
                descriptionTextView.setVisibility(View.GONE);
            }else{
                descriptionTextView.setText(description);
            }
            if(model != null  && !TextUtils.isEmpty(model.getString("address").trim())){
                addrTextView.setVisibility(View.VISIBLE);
                addrTextView.setText(model.getString("address").trim());
            }else {
                addrTextView.setVisibility(View.GONE);
            }
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating((float) model.getDouble("score"));
            logoImageView.setVisibility(View.GONE);
            /*String logoUrl = model.getString("img");
            if(TextUtils.isEmpty(logoUrl)){
                logoImageView.setVisibility(View.GONE);
            }else{
                logoImageView.setVisibility(View.VISIBLE);
                ImageLoader.load(logoUrl, logoImageView);
            }*/

            int minAmount = model.getInt("minAmount");
            int freight = model.getInt("freight");
            SpannableStringBuilder ssb = new SpannableStringBuilder("起送价");
            ssb.setSpan(new ForegroundColorSpan(ContextProvider.getContext().getResources().getColor(R.color.gray_99)), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            int length = ssb.length();
            ssb.append("￥");
            ssb.append(String.valueOf(minAmount));
            ssb.setSpan(new ForegroundColorSpan(ContextProvider.getContext().getResources().getColor(R.color.green)), length, ssb.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            startingPriceTextView.setText(ssb);

            ssb = new SpannableStringBuilder("月售" + model.getInt("recentlySalesNum") + "单");
            ssb.setSpan(new ForegroundColorSpan(ContextProvider.getContext().getResources().getColor(R.color.red_ff6)), 2, ssb.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            saleNumberTextView.setText(ssb);
            ssb = new SpannableStringBuilder(model.getInt("favouriteNum") + "收藏");
            ssb.setSpan(new ForegroundColorSpan(ContextProvider.getContext().getResources().getColor(R.color.red_ff6)), 0, ssb.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            favNumberTextView.setText(ssb);

            Model modelPromotion = model.getModel("attachedProperties");
            if(modelPromotion != null && showCoupons){

                List<Model> modelPromotions = modelPromotion.getList("promotions", Model.class);
                couponContainer.removeAllViews();

                for(Model m:modelPromotions){
                    View couponView = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_store_coupon,null);
                    TextView labelTextView = (TextView) couponView.findViewById(R.id.label_text_view);
                    TextView couponTextView = (TextView) couponView.findViewById(R.id.coupon_text_view);
                    String label = m.getString("label");
                    labelTextView.setText(label);

                    GradientDrawable myGrad3 = (GradientDrawable)labelTextView.getBackground();
                    String bgColor = m.getString("bgcolor");
                    myGrad3.setColor(ColorUtil.getColorByString(bgColor));
                    couponTextView.setText(m.getString("title"));
                    couponTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    couponContainer.addView(couponView);
                }
            }
            Log.e("store","setModel");
        }

        @Override
        public View getView() {
            return view;
        }

    }
}
