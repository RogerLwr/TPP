package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.model.Promotion;
import com.tianpingpai.buyer.model.StoreModel;
import com.tianpingpai.buyer.model.StoreModel.Notification;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.model.CommentModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.ColorUtil;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.StarRatingBar;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Statistics(page = "店铺详情")
@ActionBar(hidden = true)
@Layout(id = R.layout.ui_store_info)
public class StoreDetailViewController extends BaseViewController {
    private StoreDataContainer dataContainer;

    @Binding(id = R.id.thumbnail_image_view)
    private ImageView avatarImageView;
    @Binding(id = R.id.rating_bar)
    private StarRatingBar ratingBar;
    @Binding(id = R.id.name_text_view,format = "{{shop_name}}")
    private TextView nameTextView;
    @Binding(id = R.id.phone_text_view,format = "手机号: {{phone}}")
    private TextView phoneTextView;
    @Binding(id = R.id.category_desc_text_view, format = "主营: {{category_desc}}")
    private TextView categoryDescTextView;

    @Binding(id = R.id.description_text_view,format = "店铺简介: {{shop_desc}}")
    private TextView descriptionTextView;
    @Binding(id = R.id.address_edit_text,format = "店铺地址: {{shop_addr}}")
    private TextView addressTextView;
    @Binding(id = R.id.ratings_text_view,format = "{{score | money}}分")
    private TextView ratingsTextView;
    @Binding(id = R.id.order_number_text_view,format = "{{order_num}}单")
    private TextView orderNumberTextView;
    @Binding(id = R.id.comment_number_text_view,format = "{{comment_num}}人评论")
    private TextView commentNumberTextView;
    @Binding(id = R.id.fav_number_text_view,format = "{{favorite_num}}次")
    private TextView favNumberTextView;
    @Binding(id = R.id.min_amount_text_view,format = "{{minAmount | money}}元")
    private TextView minAmountTextView;
    @Binding(id = R.id.freight_text_view,format = "{{freight|money}}元")
    private TextView freightTextView;
    @Binding(id = R.id.notification_text_view)
    private TextView notificationTextView;
    @Binding(id = R.id.label_text_view)
    private TextView labelTextView;
    @Binding(id = R.id.coupon_text_view)
    private TextView couponTextView;
    @Binding(id = R.id.notification_container)
    private View notificationContainer;
    @Binding(id = R.id.coupon_container)
    private ViewGroup couponContainer;

    @Binding(id = R.id.address_container)
    private View addressContainer;
    @Binding(id = R.id.description_container)
    private View descriptionContainer;
    @Binding(id = R.id.license_status_text_view)
    private TextView licenseStatusTextView;
    @Binding(id = R.id.sub_stores_container)
    private LinearLayout subStoresContainer;

    //comment section
    @Binding(id = R.id.comment_container)
    private View commentContainer;
    @Binding(id = R.id.comment_store_name_text_view)
    private TextView commentStoreNameTextView;
    @Binding(id = R.id.comment_text_view)
    private TextView commentTextView;
    @Binding(id = R.id.comment_rating_bar)
    private StarRatingBar commentRatingBar;

    private int salerId;

    private ModelStatusListener<ModelEvent, StoreModel> listener = new ModelStatusListener<ModelEvent, StoreModel>() {
        @Override
        public void onModelEvent(ModelEvent event, StoreModel model) {
            if (event == ModelEvent.OnModelGet) {
                if(getActivity() != null){
                    fillData(model);
                }
            }
        }
    };

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        ratingBar.setEmptyStarRes(R.drawable.star_empty);
        ratingBar.setFullStartRes(R.drawable.star_big);
        commentRatingBar.setEmptyStarRes(R.drawable.star_empty);
        commentRatingBar.setFullStartRes(R.drawable.star_big);
        dataContainer.getStoreManager().registerListener(listener);
    }

    private void fillData(StoreModel model) {
        salerId = model.getId();
        getBinder().bindData(model);
        categoryDescTextView.setText(String.format("主营: %s",model.getCategoryDesc()));
        ArrayList<Promotion> promotions = model.getPromotions();
        if(promotions != null && promotions.size() != 0){
            Promotion promotion = promotions.get(0);
            couponTextView.setText(promotion.getTitle());
            labelTextView.setText(promotion.getLabel());
            GradientDrawable myGrad = (GradientDrawable)labelTextView.getBackground();
            String bgColor = promotion.getBgcolor();
            myGrad.setColor(ColorUtil.getColorByString(bgColor));

            if(promotions.size() > 0){
                for(int i = 0; i<promotions.size(); i++){
                    View view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_promotion, null);
                    TextView labelTV = (TextView)view.findViewById(R.id.label_text_view);
                    TextView couponTV = (TextView)view.findViewById(R.id.coupon_text_view);
                    labelTV.setText(promotions.get(i).getLabel());
                    GradientDrawable Grad = (GradientDrawable)labelTV.getBackground();
                    String colorString = promotions.get(i).getBgcolor();
                    Grad.setColor(ColorUtil.getColorByString(colorString));
                    couponTV.setText(promotions.get(i).getTitle());
//                    DisplayMetrics metric = new DisplayMetrics();
                    if(getActivity() != null){
//                        StoreDetailViewController.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionUtil.dip2px(40));
                        couponContainer.addView(view, params);

                        if(i != promotions.size()-1 ){
                            View viewLine = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.line_left10 ,null);
                            ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionUtil.dip2px((float)0.6));
                            couponContainer.addView(viewLine, params2);
                        }

                    }
                }
            }

        }else {
            couponContainer.setVisibility(View.GONE);
        }
//        freightTextView.setText(PriceFormat.format(model.getFreight()));
        ratingBar.setRating((float) model.getRating());
        if(dataContainer.isMultiShop() || dataContainer.isMultiShopWithId()){
            addressContainer.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(model.getShopDesc())) {
            descriptionContainer.setVisibility(View.GONE);
        }

        if (model.getNotifications() == null || model.getNotifications().isEmpty()) {
            notificationContainer.setVisibility(View.GONE);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < model.getNotifications().size(); i++) {
                Notification n = model.getNotifications().get(i);
                sb.append(n.getContent());
                if (i != model.getNotifications().size() - 1) {
                    sb.append("\n");
                }
            }
            notificationTextView.setText(sb.toString());
        }

        if (model.getSubStores() != null && !model.getSubStores().isEmpty()) {
            subStoresContainer.setVisibility(View.VISIBLE);
            int size = model.getSubStores().size();

            for (int i = 0; i < size; i++) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.item_sub_store, null);
                subStoresContainer.addView(v);
                StoreModel sm = model.getSubStores().get(i);
                TextView nameTextView = (TextView) v.findViewById(R.id.name_text_view);
                nameTextView.setText(sm.getName());

                TextView addressTextView = (TextView) v.findViewById(R.id.address_edit_text);
                String address = "地址:" + sm.getAddress();
                addressTextView.setText(address);

                RatingBar ratingBar = (RatingBar) v.findViewById(R.id.rating_bar);
                ratingBar.setRating((float) sm.getRating());

                TextView typeTextView = (TextView) v.findViewById(R.id.category_text_view);
                typeTextView.setText(sm.getCategoryDesc());
                if (i == size - 1) {
                    v.findViewById(R.id.bottom_line_view).setVisibility(View.INVISIBLE);
                }
                v.setOnClickListener(new StoreItemOnClickListener(sm));
            }
        }

        if (!TextUtils.isEmpty(model.getLisence())) {
            licenseStatusTextView.setText("");
            Log.e("xx", "license:" + model.getLisence() + "   size:" + model.getLisence().length());
        } else {
            licenseStatusTextView.setText("暂无");
            licenseStatusTextView.setVisibility(View.GONE);
        }

        CommentModel cm;
        if (model.getRecentComments() != null && !model.getRecentComments().isEmpty()) {
            cm = model.getRecentComments().get(0);
            if (TextUtils.isEmpty(cm.getContent())) {
                String content;
                if (model.getRating() >= 4) {
                    content = "好评";
                } else if (model.getRating() >= 3) {
                    content = "中评";
                } else {
                    content = "差评";
                }
                cm.setContent(content);
            }
            commentContainer.setVisibility(View.VISIBLE);
            commentStoreNameTextView.setText(cm.getUsername());
            commentTextView.setText(cm.getContent());
            commentRatingBar.setRating(cm.getScore());
            commentContainer.findViewById(R.id.more_comments_container).setOnClickListener(new CommentListener());
        } else {
            commentContainer.setVisibility(View.GONE);
        }
    }

    public void setDataContainer(StoreDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    @OnClick(R.id.lisence_button_container)
    private OnClickListener licenseButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (dataContainer.getStoreModel() != null && !TextUtils.isEmpty(dataContainer.getStoreModel().getLisence())) {
                Uri uri = Uri.parse(dataContainer.getStoreModel().getLisence());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
            }
        }
    };

    private class StoreItemOnClickListener implements OnClickListener {
        StoreModel store;
        StoreItemOnClickListener(StoreModel sm) {
            this.store = sm;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, StoreViewController.class);
            i.putExtra(StoreDataContainer.KEY_STORE_ID, store.getId());
            i.putExtra(StoreDataContainer.KEY_STORE_TYPE, store.getType());
            getActivity().startActivity(i);
        }
    }

    private class CommentListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,CommentListViewController.class);
            intent.putExtra(CommentListViewController.KEY_ID, salerId);
            getActivity().startActivity(intent);
        }
    }
}
