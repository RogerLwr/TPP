package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
@ActionBar(title = "客户详情")
@Layout(id = R.layout.customer_detail_view_controller)
public class CustomerDetailViewController extends BaseViewController {

    public static final String KEY_CUSTOMER = "key.Customer";

    public static final String KEY_IS_VISITED = "key.IsVisited";

    @Binding(id = R.id.sale_name_text_view,format = "{{sale_name}}")
    private TextView saleNameTextView;
    @Binding(id = R.id.contact_text_view,format = "客户电话:{{phone}}")
    private TextView contactTextView;
    @Binding(id = R.id.store_name_text_view,format = "店铺名称:{{sale_name}}")
    private TextView storeNameTextView;
    @Binding(id = R.id.store_address_text_view,format = "客户地址:{{sale_address}}")
    private TextView storeAddressTextView;
    @Binding(id = R.id.user_type_text_view)
    private TextView userTypeTextView;

//    @Binding(id = R.id.look_customer_serial_container)
//    private View lookCustomerSerialContainer;
//    @Binding(id = R.id.look_visit_log_container)
//    private View lookVisitLogContainer;
    @Binding(id = R.id.money_record_container)
    private View moneyRecordContainer;
//    @Binding(id = R.id.customer_log_container)
//    private View customerLogContainer;

    private CustomerModel customerModel;
    private boolean isVisited;

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
//        View actionBarView = setActionBarLayout(R.layout.ab_back_title_right_add_visit);
//        setTitle("客户详情");
//        actionBarView.findViewById(R.id.ab_right_button).setVisibility(View.GONE);
        customerModel = (CustomerModel)getActivity().getIntent().getSerializableExtra(KEY_CUSTOMER);
        isVisited = getActivity().getIntent().getBooleanExtra(KEY_IS_VISITED,false);
        Log.e("kehuid=========", "" + customerModel.getId());
        if(customerModel.getUserType()==1){
            moneyRecordContainer.setVisibility(View.GONE);
        }
        getBinder().bindData(customerModel);
        int type = customerModel.getUserType();
        if(0==type){
            userTypeTextView.setText("客户类型:卖家");
        }else{
            userTypeTextView.setText("客户类型:买家");
        }
    }

    @OnClick(R.id.look_customer_serial_container)
    private View.OnClickListener lookCustomerSerialContainerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,CustomerSerialViewController.class);
            intent.putExtra(KEY_CUSTOMER, customerModel);
            getActivity().startActivity(intent);

        }
    };

    //查看拜访记录
    @OnClick(R.id.look_visit_log_container)
    private View.OnClickListener lookVisitLogContainerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,CustomerVisitViewController.class);
            intent.putExtra(KEY_IS_VISITED,isVisited);
            intent.putExtra(KEY_CUSTOMER,customerModel);
            getActivity().startActivity(intent);
        }
    };

    //查看提现记录
    @OnClick(R.id.money_record_container)
    private View.OnClickListener moneyRecordContainerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,WithdrawalViewController.class);
            intent.putExtra(KEY_CUSTOMER, customerModel);
            getActivity().startActivity(intent);
        }
    };

    //查看客户日志
    @OnClick(R.id.customer_log_container)
    private View.OnClickListener customerLogContainerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,CustomerLogViewController.class);
            intent.putExtra(KEY_CUSTOMER, customerModel);
            getActivity().startActivity(intent);
        }
    };

    //查看客户优惠券
    @OnClick(R.id.customer_coupon_container)
    private View.OnClickListener customerCouponOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,CustomerCouponViewController.class);
            intent.putExtra(KEY_CUSTOMER,customerModel);
            getActivity().startActivity(intent);
        }
    };
}
