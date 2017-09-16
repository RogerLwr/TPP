package com.tianpingpai.buyer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
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
    private class CouponViewHolder implements ViewHolder<Model>{
        @Binding(id = R.id.money_text_view,format = "{{money}}")
        private TextView moneyTextView;
        @Binding(id = R.id.coupon_name_text_view,format = "{{title}}")
        private TextView couponNameTextView;
        @Binding(id = R.id.coupon_id_text_view,format = "代金卷号:{{cid}}")
        private TextView couponIdTV;
        @Binding(id = R.id.out_date_time_text_view,format = "有效时间:{{__endTime}}")
        private TextView outDateTimeTV;
        @Binding(id = R.id.coupon_des_text_view,format = "描  述:{{description}}")
        private TextView couponDesTV;
        @Binding(id = R.id.used_time_text_view,format = "使用时间:{{updateTime}}")
        private TextView usedTimeTV;
        @Binding(id = R.id.used_order_id_text_view,format = "使用订单:{{orderId}}")
        private TextView usedOrderIdTV;
        @Binding(id = R.id.status_image_view)
        private ImageView statusImageView;
        @Binding(id = R.id.bg_container)
        private View bgContainer;

        private CouponViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_coupon, null);
            binder.bindView(this,view);

        }

        private View view;
        private Binder binder = new Binder();

        @Override
        public void setModel(Model model) {
            model.set("__endTime", model.getString("beginTime") +" - " + model.getString("endTime"));
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
            if(status == 1){
                usedTimeTV.setVisibility(View.GONE);
                usedOrderIdTV.setVisibility(View.GONE);
            }else {
                if(status == 2){
                    usedTimeTV.setVisibility(View.VISIBLE);
                    usedOrderIdTV.setVisibility(View.VISIBLE);
                }else if( status == 3 ){
                    usedTimeTV.setVisibility(View.GONE);
                    usedOrderIdTV.setVisibility(View.GONE);
                }
            }

            bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg1601);
            usedOrderIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            usedTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));

            couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));

            switch (status){
                case CommonUtil.Coupon.NOT_USE:
                    usedTimeTV.setVisibility(View.GONE);
                    usedOrderIdTV.setVisibility(View.GONE);
                    moneyTextView.setBackgroundResource(R.drawable.ic_coupon_circle);
                    break;
                case CommonUtil.Coupon.USED:
                    moneyTextView.setBackgroundResource(R.drawable.ic_coupon_circle);
                    usedTimeTV.setVisibility(View.VISIBLE);
                    usedOrderIdTV.setVisibility(View.VISIBLE);
                    break;
                case CommonUtil.Coupon.OUT_DATE:
                    moneyTextView.setBackgroundResource(R.drawable.ic_coupon_circle_out_date);
                    bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg_out_date);
                    couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_99));
                    usedTimeTV.setVisibility(View.GONE);
                    usedOrderIdTV.setVisibility(View.GONE);
                    break;
            }
        }
        @Override
        public View getView() {
            return view;
        }
    }
}
