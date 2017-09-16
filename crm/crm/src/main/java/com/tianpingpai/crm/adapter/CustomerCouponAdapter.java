package com.tianpingpai.crm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.tool.CommonUtil;
import com.tianpingpai.crm.ui.OrderDetailViewController;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

import java.util.ArrayList;
import java.util.List;

public class CustomerCouponAdapter extends ModelAdapter {

    public Activity mActivity;

    private CustomerModel customerModel;

    public void setCustomerModel(CustomerModel customerModel){
        this.customerModel = customerModel;
    }

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

        @Binding(id = R.id.coupon_container)
        private View couponContainer;

        @Binding(id = R.id.money_text_view,format = "{{money}}")
        private TextView moneyTextView;
        @Binding(id = R.id.coupon_name_text_view,format = "{{title}}")
        private TextView couponNameTextView;
        @Binding(id = R.id.coupon_id_text_view,format = "优惠卷号:{{cid}}")
        private TextView couponIdTV;
        @Binding(id = R.id.out_date_time_text_view)
        private TextView outDateTimeTV;
        @Binding(id = R.id.coupon_des_text_view,format = "{{description}}")
        private TextView couponDesTV;
        @Binding(id = R.id.status_image_view)
        private ImageView statusImageView;
        @Binding(id = R.id.orders_id_container)
        private LinearLayout ordersIdContainer;

        @Binding(id = R.id.get_coupon_time_container,format = "获取时间:{{createdTime}}")
        private TextView getCouponTimeContainer;
        @Binding(id = R.id.use_coupon_time_container,format = "使用时间:{{modifiedTime}}")
        private TextView useCouponTimeContainer;

        private Binder binder = new Binder();

        CouponViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_customer_coupon, null);
            binder.bindView(this,view);
        }

        private View view;

        @Override
        public void setModel(Model model) {
            this.model = model;
            binder.bindData(model);
            String endTime = "过期时间:"+model.getString("endTime");
            outDateTimeTV.setText(endTime);
//            moneyTextView.setBackgroundResource(R.drawable.ic_coupon_circle);
            couponContainer.setBackgroundResource(R.drawable.ic_coupon_circle);
            bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg1601);
            checkedImageView.setVisibility(View.GONE);

            couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
            couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));

            switch (status){

                case CommonUtil.Coupon.NOT_USE:
                    ordersIdContainer.setVisibility(View.GONE);
                    useCouponTimeContainer.setVisibility(View.GONE);
                    break;
                case CommonUtil.Coupon.USED:
                    ordersIdContainer.removeAllViews();
                    List<Long> list = model.getList("orderIds",Long.class);
                    for(int i=0;i<list.size();i++){
                        View v = setButton(list.get(i)+"");
                        ordersIdContainer.addView(v);
                    }
//                    String orderId = model.getLong("tradeNo")+"";
//                    View v = setButton(orderId);
//                    ordersIdContainer.addView(v);
                    ordersIdContainer.setVisibility(View.VISIBLE);
                    useCouponTimeContainer.setVisibility(View.VISIBLE);
//                    moneyTextView.setBackgroundResource(R.drawable.ic_coupon_circle_out_date);
                    couponContainer.setBackgroundResource(R.drawable.ic_coupon_circle_out_date);
                    bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg_out_date);
                    couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                    couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                    outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                    couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                    break;
                case CommonUtil.Coupon.OUT_DATE:
                    ordersIdContainer.setVisibility(View.GONE);
                    useCouponTimeContainer.setVisibility(View.GONE);
//                    moneyTextView.setBackgroundResource(R.drawable.ic_coupon_circle_out_date);
                    couponContainer.setBackgroundResource(R.drawable.ic_coupon_circle_out_date);
                    bgContainer.setBackgroundResource(R.drawable.ic_coupon_bg_out_date);
                    couponNameTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                    couponIdTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                    outDateTimeTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                    couponDesTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
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

        View.OnClickListener toOrderOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, OrderDetailViewController.class);
                Model m = new Model();
//                String tradeId = model.getLong("tradeNo")+"";
                String tradeId = (String)view.getTag();
                Log.e("tradeNo========",""+tradeId);
                m.set("order_id", tradeId);
                intent.putExtra(OrderDetailViewController.KEY_ORDER, m.toJsonString());
                if(0==customerModel.getUserType()){
                    Log.e("type+gread",""+customerModel.getUserType()+"======="+customerModel.getGrade());
                    intent.putExtra(OrderDetailViewController.KEY_TYPE, "1");
                    intent.putExtra(OrderDetailViewController.KEY_GRADE,customerModel.getGrade());
                    intent.putExtra(OrderDetailViewController.KEY_IS_HAVE_GRADE,true);
                }
                mActivity.startActivity(intent);
            }
        };

        private View setButton(String orderId){
            TextView tv = new TextView(mActivity);
            tv.setText("使用订单:" + orderId);
            tv.setTextColor(mActivity.getResources().getColor(R.color.green));
            tv.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_tv_selector));
            tv.setTextSize(12);
            tv.setTag(orderId);
            tv.setOnClickListener(toOrderOnClickListener);
            return tv;
        }
    }


}
