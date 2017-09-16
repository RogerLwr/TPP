package com.tianpingpai.seller.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.model.Coupon;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.utils.PriceFormat;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class SelectCouponAdapterOld extends ModelAdapter<Coupon> {
    public Activity mActivity;
    private Coupon selectCoupon;

    public void setActivity(Activity a) {
        this.mActivity = a;
    }

    public Coupon getFirstSelectCoupon() {
        ArrayList<Coupon> coupons = getModels();
        if(coupons == null){
            return null;
        }
        for (Coupon c : coupons) {
            if (c.isValid()) {
                selectCoupon = c;
                return c;
            }
        }
        return null;
    }

    @Override
    public void setModels(ArrayList<Coupon> models) {
        models.add(0,null);
        super.setModels(models);
    }

    public Coupon getSelectCoupon() {
        return selectCoupon;
    }

    public void setSelectCoupon(Coupon selectCoupon) {
        this.selectCoupon = selectCoupon;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position) == null){
            return 2;
        }
        return 0;
    }

    private View notUseCouponView;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0){
            if(notUseCouponView == null){
                notUseCouponView = View.inflate(ContextProvider.getContext(),R.layout.item_not_use_coupon,null);
            }
            View couponCheckedImageView = notUseCouponView.findViewById(R.id.coupon_checked_image_view);
            if(selectCoupon == null){
                couponCheckedImageView.setVisibility(View.VISIBLE);
            }else{
                couponCheckedImageView.setVisibility(View.INVISIBLE);
            }
            return notUseCouponView;
        }
        return super.getView(position, convertView, parent);
    }

    @Override
    protected ViewHolder<Coupon> onCreateViewHolder(LayoutInflater inflater) {
        return new CouponViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    private class CouponViewHolder implements ViewHolder<Coupon> {
        @Binding(id = R.id.invalid_view)
        private View invalidView;

        @Binding(id = R.id.money_text_view,format = "{{money|money}}")
        private TextView moneyTextView;
        @Binding(id = R.id.coupon_name_text_view)
        private TextView couponNameTextView;
        @Binding(id = R.id.coupon_id_text_view,format = "开始时间:{{beginTime}}")
        private TextView couponIdTV;
        @Binding(id = R.id.out_date_time_text_view)
        private TextView outDateTimeTV;
        @Binding(id = R.id.coupon_des_text_view,format = "描  述:{{description}}")
        private TextView couponDesTV;
        @Binding(id = R.id.used_time_text_view)
        private TextView usedTimeTV;
        @Binding(id = R.id.used_order_id_text_view)
        private TextView usedOrderIdTV;

        @Binding(id = R.id.status_image_view)
        private ImageView statusImageView;
        @Binding(id = R.id.coupon_checked_image_view)
        private ImageView couponCheckedImageView;

        private Binder binder = new Binder();

        CouponViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_coupon, null);
            binder.bindView(this, view);
        }

        private View view;

        @Override
        public void setModel(Coupon model) {
//            double money = model.getMoney();
            binder.bindData(model);
//            moneyTextView.setText("￥" + money);
            String limiter = PriceFormat.format(model.getLimit()) + "";
            String rule = model.getRule();
//            couponNameTextView.setText("" + PriceFormat.format(money) + "元代金卷（满" + limiter + "可用)" + rule);
            couponNameTextView.setText(model.getTitle());
//            couponIdTV.setText("开始时间:" + model.getBeginTime());
            outDateTimeTV.setText(String.format("过期时间:%s", model.getEndTime()));
//            couponDesTV.setText("描  述:" + model.getDescription());
            statusImageView.setVisibility(View.INVISIBLE);
            usedTimeTV.setVisibility(View.GONE);
            usedOrderIdTV.setVisibility(View.GONE);

            if (model.isValid()) {
                invalidView.setVisibility(View.INVISIBLE);
            } else {
                invalidView.setVisibility(View.VISIBLE);
            }

            if (selectCoupon != null && selectCoupon.getId() == model.getId()) {
                couponCheckedImageView.setVisibility(View.VISIBLE);
            } else {
                couponCheckedImageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public View getView() {
            return view;
        }

    }
}
