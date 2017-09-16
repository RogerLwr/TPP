package com.tianpingpai.seller.ui;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.seller.manager.ShoppingCartManager;
import com.tianpingpai.seller.model.ShopRemark;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.PriceFormat;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StoreOrderViewHolder implements ModelAdapter.ViewHolder<SellerModel> {
    @Binding(id = R.id.store_name_text_view,format = "{{display_name}}")
    private TextView storeNameTextView;
    @Binding(id = R.id.coupon_amount_text_view)
    private TextView couponAmountTextView;
    @Binding(id = R.id.freight_text_view)
    private TextView freightTextView;
    @Binding(id = R.id.discount_amount_text_view,format = "￥-{{money|money}}")
    private TextView discountAmountTextView;
    @Binding(id = R.id.store_name_container)
    private View storeNameContainer;
    @Binding(id = R.id.expand_image_view)
    private ImageView expandImageView;
    @Binding(id = R.id.total_amount_text_view)
    private TextView totalAmountTextView;

    @Binding(id = R.id.products_container)
    private LinearLayout productsContainer;
    @Binding(id = R.id.store_sum_container)
    private View storeSumContainer;
    @Binding(id = R.id.payment_amount_text_view)
    private TextView paymentAmountTextView;
    @Binding(id = R.id.remark_text_view)
    private TextView remarkTextView;

    private LayoutInflater inflater;
    private View view;

    private int payType;

    public void setPayType(int payType){
        this.payType = payType;
    }

    private Binder binder = new Binder();

    public void setIsMulty(boolean multi){
        if(multi){
            productsContainer.setBackgroundColor(Color.WHITE);
        }else{
            productsContainer.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public StoreOrderViewHolder(LayoutInflater inflater) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.view_store_order, null);
        binder.bindView(this, view);
    }

    public SellerModel getModel(){
        return model;
    }

    SellerModel model;

    @Override
    public void setModel(SellerModel model) {
        this.model = model;
//        binder.bindData(model);
        storeNameTextView.setText(model.getDisplayName());
        freightTextView.setText(String.format("￥%s", PriceFormat.format(model.getFreight())));

        ShopRemark remark = ShoppingCartManager.getInstance()
                .getRemarkForSeller(model.getId());
        if(remark != null) {
            remarkTextView.setText(String.format("买家留言:%s", remark.getRemark()));
        }else {
            remarkTextView.setText("买家留言: 无");
        }

    }

    @Override
    public View getView() {
        return view;
    }

    private ArrayList<ProductModel> products;

    public void setProducts(ArrayList<ProductModel> products) {
        this.products = products;

        double total = 0;
        for (int i = 0; i < products.size(); i++) {
            ProductModel pm = products.get(i);
            total += pm.getCouponPrice() * pm.getProductNum();
            View view = inflater.inflate(R.layout.item_product_simple, null);
            productsContainer.addView(view);
            TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            nameTextView.setText(pm.getName());
            TextView numberTextView = (TextView) view.findViewById(R.id.number_text_view);
            numberTextView.setText(String.format("x%d",pm.getProductNum()));
            TextView priceTextView = (TextView) view.findViewById(R.id.specs_unit_text_view);
            priceTextView.setText(String.format("单价￥%s",PriceFormat.format(pm.getCouponPrice())));
        }
        totalAmountTextView.setText(String.format("￥%s",PriceFormat.format(total)));
        total += getFreight();
        paymentAmountTextView.setText(String.format("￥%s",PriceFormat.format(total)));

    }

    public void expand(){
        storeNameContainer.setEnabled(false);
        storeSumContainer.setVisibility(View.VISIBLE);
        expandImageView.setVisibility(View.GONE);
    }

    @OnClick(R.id.store_name_container)
    private View.OnClickListener storeNameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(storeSumContainer.getVisibility() == View.VISIBLE){
                storeSumContainer.setVisibility(View.GONE);
                //TODO
                expandImageView.setImageResource(R.drawable.ar);
            }else{
                storeSumContainer.setVisibility(View.VISIBLE);
                expandImageView.setImageResource(R.drawable.ad);
            }
        }
    };

    private double discount;

    public void setPromotion(Model p){
        Log.e("xx", "p" + p.toJsonString());
        List<Model> bestPromotions = p.getList("bestPromotions",Model.class);
        if(bestPromotions == null || bestPromotions.isEmpty()){
            return;//TODO
        }
        Model defaultPromotion = null;
        for(Model m:bestPromotions){
            if(m.getInt("payType") == payType){
                defaultPromotion = m;
                break;
            }
        }

        if(defaultPromotion != null){
            binder.bindData(p);

            double total = 0;
            for (int i = 0; i < products.size(); i++) {
                ProductModel pm = products.get(i);
                total += pm.getCouponPrice() * pm.getProductNum();
            }
            Number money = defaultPromotion.getNumber("money");
            couponAmountTextView.setVisibility(View.VISIBLE);

            if(model.getMoney() <= 0.001){
                couponAmountTextView.setVisibility(View.INVISIBLE);
            }else{
                couponAmountTextView.setText("已优惠" + money.toString() + "元");
            }
            total -= money.doubleValue();
            discount = money.doubleValue();
            total += model.getFreight();
            Log.e("xx","freight:" + model.getFreight());
            paymentAmountTextView.setText(String.format("￥%s",PriceFormat.format(total)));
        } else{
            couponAmountTextView.setVisibility(View.INVISIBLE);
            discount = 0;
            paymentAmountTextView.setText(String.format("￥%s",PriceFormat.format(getTotal())));
//            couponAmountTextView.setText("￥0.00");
        }
    }

    public double getTotal(){
        if(getDiscount() <= 0.001){
            couponAmountTextView.setVisibility(View.INVISIBLE);
        }else{
            couponAmountTextView.setVisibility(View.VISIBLE);
            couponAmountTextView.setText("已优惠" + PriceFormat.format(discount) + "元");
        }
        discountAmountTextView.setText(String.format("-￥%s", PriceFormat.format(discount)));
        return getOrderPrice() + getFreight() - getDiscount();
    }


    public double getDiscount(){
        return discount;
    }

    public double getFreight(){
        return model.getFreight();
    }

    public double getOrderPrice(){
        double total = 0;
        for (int i = 0; i < products.size(); i++) {
            ProductModel pm = products.get(i);
            total += pm.getCouponPrice() * pm.getProductNum();
        }
        return total;
    }

    public Model getSubmitParam(){
        Model seller = new Model();
        seller.set("s_user_id", model.getId());
        seller.set("shopName", model.getDisplayName());

        ArrayList<Model> productList = new ArrayList<>();
        for (ProductModel pm : products) {
            Model product = new Model();
            product.set("prod_id", pm.getId());
            product.set("prod_num", pm.getProductNum());
            product.set("price", pm.getCouponPrice());
            product.set("remark", pm.getComment());
            productList.add(product);
        }
        seller.setList("prod_list", productList);
        ShopRemark remark = ShoppingCartManager.getInstance()
                .getRemarkForSeller(model.getId());
        if (remark != null) {
            seller.set("remark", remark.getRemark().trim());
        }
        seller.set("mny", getTotal());
//        seller.set("freight",getFreight());
        seller.set("coupon_mny", getDiscount());
        seller.set("deliver_mny",((int)model.getFreight()));
        return seller;
    }
}
