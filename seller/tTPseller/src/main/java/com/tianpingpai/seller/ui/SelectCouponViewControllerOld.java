package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.SelectCouponAdapterOld;
import com.tianpingpai.seller.model.Coupon;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;

import java.util.ArrayList;

@SuppressWarnings("unused")
@ActionBar(title = "选择优惠券")
@Layout(id = R.layout.ui_select_coupon)
@EmptyView(imageResource = R.drawable.ic_coupon_empty_view, text = R.string.empty_coupons)
public class SelectCouponViewControllerOld extends BaseViewController {

    public static final String KEY_COUPONS = "Key.coupons";
    public static final String KEY_SELECT_COUPON = "Key.selectCoupon";

    @Binding(id = R.id.empty_container)
    private ViewGroup emptyView;
    private ArrayList<Coupon> coupons;
    private SelectCouponAdapterOld adapter = new SelectCouponAdapterOld();

    @Override
    protected ViewGroup getEmptyContainer() {
        return emptyView;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        coupons = (ArrayList<Coupon>) getActivity().getIntent().getSerializableExtra(KEY_COUPONS);
        Coupon selectCoupon = (Coupon) getActivity().getIntent().getSerializableExtra(KEY_SELECT_COUPON);
        ListView listView = (ListView) rootView.findViewById(R.id.coupons_list_view);
        listView.setAdapter(adapter);
        if (selectCoupon != null) {
            adapter.setSelectCoupon(selectCoupon);
        } else {
            adapter.getFirstSelectCoupon();

        }
        listView.setOnItemClickListener(OnItemClickListener);
        rootView.findViewById(R.id.okay_button).setOnClickListener(OnClickListener);

        adapter.setModels(coupons);
        showContent();

        if (coupons.size() > 0) {
            hideEmptyView();
            listView.setVisibility(View.VISIBLE);
        } else {
            showEmptyView();
            listView.setVisibility(View.GONE);
        }
    }


    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra(SelectPaymentViewController.KEY_SELECT_COUPON, adapter.getSelectCoupon());
            SelectCouponViewControllerOld.this.getActivity().setResult(Activity.RESULT_OK, intent);
            SelectCouponViewControllerOld.this.getActivity().finish();

        }
    };
    private AdapterView.OnItemClickListener OnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Coupon c = coupons.get(i);
            Log.e("xx", "57-------------" + c);
            if (c == null || c.isValid()) {
                adapter.setSelectCoupon(c);
            }
        }
    };
}
