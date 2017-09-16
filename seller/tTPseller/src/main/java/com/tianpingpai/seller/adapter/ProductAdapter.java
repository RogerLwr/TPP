package com.tianpingpai.seller.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.ui.EditProductViewController;
import com.tianpingpai.seller.ui.EditStockViewController;
import com.tianpingpai.seller.ui.PromotionDetailViewController;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.PriceFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends ModelAdapter<ProductModel> {

    private static final int CHANGED_COLOR = Color.RED;
    private static final int DEFAULT_COLOR = Color.BLACK;

    private Activity a;

    private HashMap<Long, Model> changedMap = new HashMap<>();

    public ArrayList<Model> getChangedModels(){
        ArrayList<Model> arrayList = new ArrayList<>();
        for(Long id:changedMap.keySet()){
            arrayList.add(changedMap.get(id));
        }
        return arrayList;
    }

    RefreshListener refreshListener;

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }
    // 增加库存 刷新列表
    public interface RefreshListener{
        void refreshProduct();
    }

    public void clearChanged(){
        changedMap.clear();
    }

    @Override
    protected com.tianpingpai.ui.ModelAdapter.ViewHolder<ProductModel> onCreateViewHolder(
            LayoutInflater inflater) {
        return new ProductViewHolder(inflater);
    }

    public Activity getActivity() {
        return a;
    }

    public void setActivity(Activity a) {
        this.a = a;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    public boolean editMode = false;

    class ProductViewHolder implements ViewHolder<ProductModel> {
        private Binder binder = new Binder();

        private View view;
        @Binding(id = R.id.thumbnail_image_view)
        private ImageView thumbnailImageView;
        @Binding(id = R.id.product_container)
        private RelativeLayout productContainer;
        @Binding(id = R.id.down_image_view)
        private ImageView downImageView;
        @Binding(id = R.id.name_text_view)
        private TextView nameTextView;
        @Binding(id = R.id.specs_unit_text_view)
        private TextView priceTextView;
        @Binding(id = R.id.desc_text_view)
        private TextView descTextView;
        @Binding(id = R.id.attr_info_text_view)
        private TextView attrInfoTextView;

        @Binding(id = R.id.promotion_container)
        private View promotionContainer;
        @Binding(id = R.id.promotion_status_text_view)
        private TextView promotionStatusTextView;
        @Binding(id = R.id.promotion_time_text_view)
        private TextView promotionTimeTextView;
        @Binding(id = R.id.promotion_price_text_view)
        private TextView promotionPriceTextView;

        @Binding(id = R.id.promotion_info_text_view)
        private TextView promotionInfoTextView;
        @Binding(id = R.id.promotion_unit_text_view)
        private TextView promotionUnitTextView;
        @Binding(id = R.id.prod_status_text_view)
        private TextView prodStatusTextView;

        @Binding(id = R.id.edit_stock_btn)
        private Button editStockBtn;

        @Binding(id = R.id.new_price_text_view)
        private TextView newPriceTextView;

        @Binding(id = R.id.stock_text_view)
        private TextView stockTextView;

        @Binding(id = R.id.stand_weight_text_view)
        private TextView standWeightTextView;

        private ProductModel mModel;
        private ActionSheet actionSheet;

        public void setActionSheet(ActionSheet actionSheet) {
            this.actionSheet = actionSheet;
        }

        public ProductViewHolder(LayoutInflater inflater) {
//            if(.getfCategoryId() == -1){
            view = inflater.inflate(R.layout.item_product_promotion, null);
//            }else {
//                view = inflater.inflate(R.layout.item_product, null);
//            }
            binder.bindView(this, view);
            newPriceTextView.addTextChangedListener(newPriceChangeListener);
        }

        TextWatcher newPriceChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Model m = changedMap.get(mModel.getId());
                if (m == null) {
                    m = new Model();
                }
                double price ;
                try{
                    price = Double.parseDouble(s.toString());
                    m.set("coupon_price",price);
                    m.set("prod_id",mModel.getId());
                    changedMap.put(mModel.getId(),m);
                }catch(Exception e){
                    e.printStackTrace();
                    changedMap.remove(mModel.getId());
                }

            }
        };

        @Override
        public void setModel(ProductModel model) {
            this.mModel = model;
            Log.e("xx", "url:" + model.getImageUrl());
            if(mModel.getfCategoryId() != -1){

                if (TextUtils.isEmpty(model.getImageUrl()) || model.getfCategoryId() == -1) {
                    this.thumbnailImageView.setVisibility(View.GONE);
                } else {
                    this.thumbnailImageView.setVisibility(View.VISIBLE);
                    ImageLoader.load(model.getImageUrl(), this.thumbnailImageView, R.drawable.img_loading, R.drawable.img_loading);
                }
                if(model.getStatus() == 0){
                    downImageView.setVisibility(View.VISIBLE);
                }else{
                    downImageView.setVisibility(View.INVISIBLE);
                }
            }else {
                this.thumbnailImageView.setVisibility(View.GONE);
                downImageView.setVisibility(View.INVISIBLE);
            }

            nameTextView.setText(model.getName());
            SpannableStringBuilder ssb = new SpannableStringBuilder("￥" + PriceFormat.format(model.getCouponPrice()) );
            ForegroundColorSpan span=new ForegroundColorSpan(Color.parseColor("#ff6000"));
            ssb.setSpan(span, 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append("/");
            ssb.append(model.getUnit());
            priceTextView.setText(ssb);
            if (TextUtils.isEmpty(model.getDescription())) {
                descTextView.setVisibility(View.GONE);
            } else {
                descTextView.setVisibility(View.VISIBLE);
                descTextView.setText("描述:" + model.getDescription());
            }
            productContainer.setBackgroundResource(R.color.white);

            if( !TextUtils.isEmpty(model.getSalesStatus())){
                promotionTimeTextView.setText(model.getStartTime() + "日0点 - " + model.getEndTime() + "日0点");
                promotionStatusTextView.setText("【" + model.getSalesStatus() + "】");
                promotionPriceTextView.setText(model.getCouponPrice()+"");
                promotionInfoTextView.setText( "元/" + model.getUnit() + "  " + model.getLimit());
                if( !TextUtils.isEmpty(model.getAttrStand()) && !TextUtils.isEmpty(model.getAttrWeight())){
                    standWeightTextView.setVisibility(View.VISIBLE);
                    standWeightTextView.setText(model.getUnit() + "(" + model.getAttrStand() + "*" + model.getAttrWeight() + ")");

                }else if(TextUtils.isEmpty(model.getAttrStand())){
                    standWeightTextView.setVisibility(View.VISIBLE);
                    standWeightTextView.setText(model.getUnit() + "(" + model.getAttrWeight() + ")");
                }else if(TextUtils.isEmpty(model.getAttrWeight())){
                    standWeightTextView.setVisibility(View.VISIBLE);
                    standWeightTextView.setText(model.getUnit() + "(" + model.getAttrStand() + ")");
                }
                if( TextUtils.isEmpty(model.getAttrStand()) && TextUtils.isEmpty(model.getAttrWeight())){
                    standWeightTextView.setVisibility(View.GONE);
                }
                promotionUnitTextView.setText(model.getUnit());
                editStockBtn.setText("编辑库存");

                if(model.getColor().equals("red")){
                    stockTextView.setTextColor(stockTextView.getResources().getColor(R.color.red));
                    editStockBtn.setVisibility(View.VISIBLE);
                }else if(model.getColor().equals("green")){
                    stockTextView.setTextColor(stockTextView.getResources().getColor(R.color.green_0c));
                    editStockBtn.setVisibility(View.GONE);
                }else {
                    stockTextView.setTextColor(stockTextView.getResources().getColor(R.color.gray_33));
                    editStockBtn.setVisibility(View.GONE);
                }
                if(model.getSalesStatus().equals("审核失败") || model.getSalesStatus().equals("取消促销")){
                    promotionContainer.setBackgroundColor(promotionContainer.getResources().getColor(R.color.red_ffd));
                }else {
                    promotionContainer.setBackgroundColor(promotionContainer.getResources().getColor(R.color.gray_ee));
                }

                promotionContainer.setVisibility(View.VISIBLE);
                stockTextView.setText("" + model.getStock());
            }else{
                editStockBtn.setVisibility(View.GONE);
                standWeightTextView.setVisibility(View.GONE);
                promotionContainer.setVisibility(View.GONE);
                prodStatusTextView.setVisibility(View.GONE);
            }

            if(model.getAttrList() != null){
                List<Model> attrModels = model.getAttrList();
                String attrValue = "";
                for(int i = 0; i<attrModels.size(); i++){
                    Model attrModel = attrModels.get(i);
                    if(attrModel.getString("attr_type").equals("quality")){
                        attrValue += "品牌: 【"+attrModel.getString("modify_level")+ "】 " +attrModel.getString("modify_value") + "\n";
                    }else if( !attrModel.getString("attr_type").equals("stand") && !attrModel.getString("attr_type").equals("weight")){
                        attrValue += ""+attrModel.getString("modify_value")+" ";
                    }
                }
                attrInfoTextView.setVisibility(View.VISIBLE);
                attrInfoTextView.setText(attrValue);
            }else {
                attrInfoTextView.setVisibility(View.GONE);
            }

            if (isEditMode() && mModel.getfCategoryId() != -1) {
                double price = model.getCouponPrice();
                Model m = changedMap.get(mModel.getId());
                newPriceTextView.removeTextChangedListener(newPriceChangeListener);
                if (m != null) {
                    price = m.getDouble("coupon_price");
                    newPriceTextView.setTextColor(CHANGED_COLOR);
                } else {
                    newPriceTextView.setTextColor(DEFAULT_COLOR);
                }
                newPriceTextView.setText(PriceFormat.format(price) + "");
                newPriceTextView.addTextChangedListener(newPriceChangeListener);
                newPriceTextView.setVisibility(View.VISIBLE);
                priceTextView.setVisibility(View.INVISIBLE);
            } else if(mModel.getfCategoryId() == -1){
                priceTextView.setVisibility(View.GONE);
                newPriceTextView.setVisibility(View.GONE);
            }else {
                priceTextView.setVisibility(View.VISIBLE);
                newPriceTextView.setVisibility(View.GONE);
            }

        }

        @OnClick(R.id.right_container)
        private View.OnClickListener rightContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getActivity(),ContainerActivity.class);
                mIntent.putExtra(ContainerActivity.KEY_CONTENT, EditProductViewController.class);
                mIntent.putExtra(EditProductViewController.KEY_PRODUCT, mModel);
                mIntent.putExtra(EditProductViewController.KEY_ADD, false);
                getActivity().startActivity(mIntent);
            }
        };

        @OnClick(R.id.promotion_container)
        private View.OnClickListener promotionContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getActivity(),ContainerActivity.class);
//            mIntent.putExtra(ContainerActivity.KEY_CONTENT, EditProductFragment.class);
                Log.e("xx", "283-------" + mModel);
                if(mModel.getfCategoryId() == -1){
                    mIntent.putExtra(ContainerActivity.KEY_CONTENT, PromotionDetailViewController.class);
                    mIntent.putExtra(PromotionDetailViewController.KEY_PRODUCT, mModel);
                    mIntent.putExtra(PromotionDetailViewController.KEY_PID, mModel.getPid());
                }
                getActivity().startActivity(mIntent);
            }
        };

        @OnClick(R.id.edit_stock_btn)
        private View.OnClickListener editStockBtnOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("xx", "-----"+mModel);

                actionSheet = new ActionSheet();
                EditStockViewController editStockViewController = new EditStockViewController();
                editStockViewController.setStock(mModel.getStock(), mModel.getName(), mModel.getPid(), mModel.getUnit());
                editStockViewController.setOnStockClickListener(new EditStockViewController.OnStockClickListener() {
                    @Override
                    public void onOkClick() {
                        actionSheet.dismiss();
                        if (refreshListener != null) {
                            refreshListener.refreshProduct();
                        }
                    }

                    @Override
                    public void onBgContainerClick() {
                        actionSheet.dismiss();
                    }
                });

                actionSheet.setActivity((FragmentActivity) a);
                actionSheet.setViewController(editStockViewController);
                actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
                actionSheet.setCancelable(true);

                WindowManager wm = a.getWindowManager();

                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);

                actionSheet.setHeight(dm.heightPixels);
                actionSheet.setWidth(dm.widthPixels);
                actionSheet.show();

            }
        };

        @Override
        public View getView() {
            return view;
        }
    }
}
