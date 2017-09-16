package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.tianpingpai.crm.R;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
@Layout(id = R.layout.fragment_main_customer)
public class CustomerViewController extends CrmBaseViewController {

    @Binding(id=R.id.my_buyer_container)
    private RelativeLayout myBuyerContainer;
    @Binding(id=R.id.my_seller_container)
    private RelativeLayout mySellerContainer;
    @Binding(id=R.id.check_repeat_container)
    private RelativeLayout checkRepeatContainer;

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        View actionBar = setActionBarLayout(R.layout.ab_title_green);

        Toolbar toolbar = (Toolbar)actionBar.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        setTitle("客户");

    }
    @OnClick(R.id.my_buyer_container)
    private View.OnClickListener myBuyerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, BuyerCustomersViewController.class);
            intent.putExtra(BuyerCustomersViewController.KEY_DATE_TYPE, "-1");
            intent.putExtra(BuyerCustomersViewController.KEY_USER_TYPE, "1");
            intent.putExtra(BuyerCustomersViewController.KEY_TITLE, "我的买家");
            intent.putExtra(BuyerCustomersViewController.KEY_IS_ITEM_CLICK, true);
            getActivity().startActivity(intent);


        }
    };
    @OnClick(R.id.my_seller_container)
    private View.OnClickListener mySellerOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SellerCustomersViewController.class);
            intent.putExtra(SellerCustomersViewController.KEY_DATE_TYPE, "-1");
            intent.putExtra(SellerCustomersViewController.KEY_USER_TYPE, "0");
            intent.putExtra(SellerCustomersViewController.KEY_TITLE, "我的卖家");
//            intent.putExtra(CustomerInfoViewController.KEY_IS_NOT_SUBMIT,true);//是否屏蔽
            intent.putExtra(SellerCustomersViewController.KEY_IS_ITEM_CLICK, true);
            getActivity().startActivity(intent);

        }
    };
    @OnClick(R.id.check_repeat_container)
    private View.OnClickListener checkRepeatOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, CheckRepeatCustomerViewController.class);
            intent.putExtra(SellerCustomersViewController.KEY_DATE_TYPE, "-1");
            intent.putExtra(SellerCustomersViewController.KEY_USER_TYPE, "1");
            intent.putExtra(SellerCustomersViewController.KEY_TITLE, "查重客户");
            intent.putExtra(SellerCustomersViewController.KEY_IS_ITEM_CLICK, true);
            intent.putExtra(SellerCustomersViewController.KEY_IS_VISIBLE_BTN,true);
            getActivity().startActivity(intent);
        }
    };
}
