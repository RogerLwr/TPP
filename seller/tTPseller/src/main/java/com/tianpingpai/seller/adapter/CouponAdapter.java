package com.tianpingpai.seller.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class CouponAdapter extends ModelAdapter<Model> {

    public Activity mActivity;

    public void setActivity(Activity a){
        this.mActivity = a;
    }
    private int status = 1;
    public void setCouponStatus(int status){
        this.status = status;
    }



    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CouponViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    private class CouponViewHolder implements ViewHolder<Model> {
        private Model model;
        @Binding(id = R.id.bg_container)
        private LinearLayout bgContainer;

        @Binding(id = R.id.coupon_checked_image_view)
        private ImageView checkedImageView;

        @Binding(id = R.id.money_text_view,format = "{{money}}")
        private TextView moneyTextView;
        @Binding(id = R.id.money_container)
        private View moneyContainer;
        @Binding(id = R.id.coupon_name_text_view,format = "{{title}}")
        private TextView couponNameTextView;
        @Binding(id = R.id.coupon_id_text_view,format = "优惠卷号:{{cid}}")
        private TextView couponIdTV;
        @Binding(id = R.id.out_date_time_text_view)
        private TextView outDateTimeTV;
        @Binding(id = R.id.coupon_des_text_view,format = "{{description}}")
        private TextView couponDesTV;
        @Binding(id = R.id.used_time_text_view,format = "使用时间:{{updateTime}}")
        private TextView usedTimeTV;
        @Binding(id = R.id.used_order_id_text_view,format = "使用订单:{{orderId}}")
        private TextView usedOrderIdTV;
        @Binding(id = R.id.status_image_view)
        private ImageView statusImageView;

        private Binder binder = new Binder();

        CouponViewHolder(LayoutInflater inflater){
//            if(status == CommonUtil.Coupon.OUT_DATE){
//                view = inflater.inflate(R.layout.item_coupon_out_date, null);
//            }else {

                view = inflater.inflate(R.layout.item_coupon, null);
//            }
            binder.bindView(this,view);
        }

        private View view;

        @Override
        public void setModel(Model model) {
            this.model = model;
            if(model.getInt("coupon_type") == 0){ // 固定金额优惠券
//                model.set("money", model.getDouble("money"));
            }else if(model.getInt("coupon_type") == 1){  //百分比折扣券
                if(model.getInt("coupon_rate") % 10 == 0){
                    model.set("money", ( model.getInt("coupon_rate") / 10 )+"折");
                }else {
                    model.set("money", model.getInt("coupon_rate")+"折");
                }
            }
            binder.bindData(model);

            String endTime = "有效时间:"+model.getString("beginTime") +" - " + model.getString("endTime");
            outDateTimeTV.setText(endTime);
            moneyContainer.setBackgroundResource(R.drawable.ic_coupon_circle);
            bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg1601);
            checkedImageView.setVisibility(View.GONE);

//            usedOrderIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
//            usedTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));

            couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));

            switch (status){

                case CommonUtil.Coupon.NOT_USE:
                    usedTimeTV.setVisibility(View.GONE);
                    usedOrderIdTV.setVisibility(View.GONE);
                    break;
                case CommonUtil.Coupon.USED:
                    usedTimeTV.setVisibility(View.VISIBLE);
                    usedOrderIdTV.setVisibility(View.VISIBLE);
                    moneyContainer.setBackgroundResource(R.drawable.ic_coupon_circle_out_date);
                    bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg_out_date);
                    couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    break;
                case CommonUtil.Coupon.OUT_DATE:
                    moneyContainer.setBackgroundResource(R.drawable.ic_coupon_circle_out_date);
                    bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg_out_date);
                    couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    usedTimeTV.setVisibility(View.GONE);
                    usedOrderIdTV.setVisibility(View.GONE);
                    break;

            }
            if(status == CommonUtil.Coupon.NOT_USE){
//                statusImageView.setVisibility(View.INVISIBLE);

            }else {
//                statusImageView.setVisibility(View.VISIBLE);
                if(status == CommonUtil.Coupon.USED){
//                    statusImageView.setImageResource(R.drawable.ic_coupon_used);

                }else if( status == CommonUtil.Coupon.OUT_DATE ){
//                    statusImageView.setImageResource(R.drawable.ic_out_of_date);
                }
            }
        }
        @Override
        public View getView() {
            return view;
        }
    }
}
