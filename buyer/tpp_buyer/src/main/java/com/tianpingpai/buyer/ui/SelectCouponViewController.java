package com.tianpingpai.buyer.ui;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.SelectCouponAdapter;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.widget.OnSelectListener;

import java.util.ArrayList;

@Layout(id = R.layout.ui_select_coupon)
public class SelectCouponViewController extends BaseViewController{

    @Binding(id = R.id.coupon_checked_image_view)
    private ImageView couponCheckedImageView;
    Boolean isUsedCoupon = false;

    private ArrayList<Model> coupons;

    public Model getSelectedCoupon() {
        return adapter.getSelectCoupon();
    }

    private Model selectedCoupon;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    private OnSelectListener onSelectListener;
    private ActionSheet actionSheet;

    public void setActionSheet(ActionSheet actionSheet){
        this.actionSheet = actionSheet;
    }

    private SelectCouponAdapter adapter = new SelectCouponAdapter();
    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };

    public void setCoupons(ArrayList<Model> coupons){
        this.coupons = coupons;
    }

    public void setSelectedCoupon(Model m){
        this.selectedCoupon = m;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView listView = (ListView) rootView.findViewById(R.id.coupons_list_view);
        listView.setAdapter(adapter);

        if(coupons != null){
            adapter.setModels(coupons);
        }
        if(selectedCoupon != null){
            adapter.setSelectCoupon(selectedCoupon);
            couponCheckedImageView.setVisibility(View.GONE);
            isUsedCoupon = true;
        }else {
            adapter.getFirstSelectCoupon();
        }
        listView.setOnItemClickListener(OnItemClickListener);
        rootView.findViewById(R.id.okay_button).setOnClickListener(OnClickListener);

        adapter.setModels(coupons);
        View actionBarView = setActionBarLayout(R.layout.ab_title_white);
        Toolbar toolbar = (Toolbar) actionBarView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(backButtonListener);
        setTitle("选择优惠券");
        showContent();
    }

    @OnClick(R.id.container_not_use_coupon)
    View.OnClickListener notUseCouponClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            isUsedCoupon = false;
            couponCheckedImageView.setVisibility(View.VISIBLE);
            selectedCoupon = null;
            adapter.setSelectCoupon(null);
        }
    };

    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            actionSheet.dismiss();
            onSelectListener.onSelect();
        }
    };
    private AdapterView.OnItemClickListener OnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Model c = coupons.get(i);
            if(c.getBoolean("valid")){
                adapter.setSelectCoupon(c);
                couponCheckedImageView.setVisibility(View.GONE);
                isUsedCoupon = true;
            }
        }
    };
}
