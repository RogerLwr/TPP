package com.tianpingpai.seller.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.model.Coupon;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

public class SelectCouponAdapter extends ModelAdapter<Model> {
    public Activity mActivity;
    private Model selectCoupon;

    private View notUseCouponView;

    public void setActivity(Activity a) {
        this.mActivity = a;
    }

    public Model getFirstSelectCoupon() {
        ArrayList<Model> coupons = getModels();
        if(coupons == null){
            return null;
        }
        for (Model c : coupons) {
            if (c.getBoolean("valid")) {
                selectCoupon = c;
                return c;
            }
        }
        return null;
    }

    @Override
    public void setModels(ArrayList<Model> models) {
//        models.add(0,null);
        super.setModels(models);
    }

    public Model getSelectCoupon() {
        return selectCoupon;
    }

    public void setSelectCoupon(Model selectCoupon) {
        this.selectCoupon = selectCoupon;
        notifyDataSetChanged();
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CouponViewHolder(inflater);
    }

//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getCount() {
//        return super.getCount();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if(getItem(position) == null){
//            return 0;
//        }
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if(position == 0){
//            if(notUseCouponView == null){
//                notUseCouponView = View.inflate(ContextProvider.getContext(),R.layout.item_not_use_coupon,null);
//            }
//            View couponCheckedImageView = notUseCouponView.findViewById(R.id.coupon_checked_image_view);
//            if(selectCoupon == null){
//                couponCheckedImageView.setVisibility(View.VISIBLE);
//            }else{
//                couponCheckedImageView.setVisibility(View.INVISIBLE);
//            }
//            return notUseCouponView;
//        }
//        return super.getView(position, convertView, parent);
//    }

    @SuppressWarnings("unused")
    private class CouponViewHolder implements ViewHolder<Model> {
        @Binding(id = R.id.invalid_view)
        private View invalidView;
        @Binding(id = R.id.money_text_view,format = "{{money}}")
        private TextView moneyTextView;
        @Binding(id = R.id.coupon_name_text_view,format = "{{title}}")
        private TextView couponNameTextView;
        @Binding(id = R.id.coupon_id_text_view,format = "代金券号:{{cid}}")
        private TextView couponIdTV;
        @Binding(id = R.id.out_date_time_text_view,format = "有效时间:{{__endTime}}")
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
        public void setModel(Model model) {
            if(model != null){
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
                statusImageView.setVisibility(View.INVISIBLE);
                usedTimeTV.setVisibility(View.GONE);
                usedOrderIdTV.setVisibility(View.GONE);

                boolean isValid = model.getBoolean("valid");
                if (isValid) {
                    invalidView.setVisibility(View.INVISIBLE);
                } else {
                    invalidView.setVisibility(View.VISIBLE);
                }
                if (selectCoupon != null && selectCoupon.getInt("id") == model.getInt("id")) {
                    couponCheckedImageView.setVisibility(View.VISIBLE);
                } else {
                    couponCheckedImageView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
