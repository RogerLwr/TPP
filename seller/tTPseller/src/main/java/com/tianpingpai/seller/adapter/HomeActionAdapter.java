package com.tianpingpai.seller.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.seller.SellerUrlInterceptor;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.seller.ui.BillsViewController;
import com.tianpingpai.seller.ui.CommentListViewController;
import com.tianpingpai.seller.ui.DrawCashViewController;
import com.tianpingpai.seller.ui.MyGroupShoppingViewController;
import com.tianpingpai.seller.ui.MyJournalViewController;
import com.tianpingpai.seller.ui.MyShopQRCodeViewController;
import com.tianpingpai.seller.ui.SelectCategoryViewController;
import com.tianpingpai.seller.ui.WebViewController;
import com.tianpingpai.ui.ContainerActivity;

import java.util.ArrayList;

public class HomeActionAdapter extends BaseAdapter {

    ArrayList<Item> items = new ArrayList<>();

    public void reset() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            items.clear();
            if (user.getGrade() == UserModel.GRADE_3) {  // 3级供货商 才可以 上游采购
                Item upstream = new Item(R.drawable.ic_purchase, R.string.upstream, WebViewController.class);
                upstream.url = URLApi.getWebBaseUrl() + "/saler/upstream/list?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken();
//                upstream.url = "http://192.168.0.138/tianpingpai_app/active.html";
                items.add(upstream);
            }
            Item myActivities = new Item(R.drawable.ic_home_my_activities, R.string.my_activities, WebViewController.class);
            myActivities.url = URLApi.getWebBaseUrl() + "/apply/app/activity/my_activity?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken();
            items.add(myActivities);
            if (user.getGrade() == UserModel.GRADE_1) {
                Item newProduct = new Item(R.drawable.ic_home_new_product, R.string.new_product, SelectCategoryViewController.class);
//                Item newProduct = new Item(R.drawable.ic_home_new_product, R.string.new_product, EditProductFragment.class);
                items.add(newProduct);
            }

            Item comment = new Item(R.drawable.ic_home_comment, R.string.my_comments, CommentListViewController.class);
            items.add(comment);

//            Item myJournal = new Item(R.drawable.ic_home_my_journal, R.string.my_journals, MyJournalViewController.class);
            Item myJournal = new Item(R.drawable.ic_home_my_journal, R.string.my_journals, BillsViewController.class);
//            myJournal.url = ContextProvider.getBaseURL() + "/apply/saler/bill?accessToken=" + UserManager.getInstance().getCurrentUser().getAccessToken();
            items.add(myJournal);

            if (user.getGrade() != UserModel.GRADE_3) {
                Item groupBuy = new Item(R.drawable.ic_home_group_shopping, R.string.group_shopping, MyGroupShoppingViewController.class);
                items.add(groupBuy);
            }else{
//                Item batchEdit = new Item(R.drawable.ic_home_batch_edit, R.string.batch_edit, null);
//                items.add(batchEdit);
                Item grade3GroupBuy = new Item(R.drawable.ic_home_group_shopping, R.string.group_shopping, WebViewController.class);
                grade3GroupBuy.url = URLApi.getWebBaseUrl() + "/groupbuy/upstream/list?accessToken=" + user.getAccessToken();
                items.add(grade3GroupBuy);
            }
            if (user.getGrade() == UserModel.GRADE_1) { //1级供货商才有 我的微店
                Item advertise = new Item(R.drawable.ic_home_my_shop_code, R.string.my_shop_code, MyShopQRCodeViewController.class);
//            advertise.url = ContextProvider.getBaseURL() + "/apply/app/sale/marketing";
                items.add(advertise);
                notifyDataSetChanged();
            }else{
                Item drawCash = new Item(R.drawable.ic_home_draw_cash, R.string.draw_cash, DrawCashViewController.class);
                items.add(drawCash);
            }

//                Item test = new Item(R.drawable.ic_purchase, R.string.upstream, WebViewController.class);
//            test.url = URLApi.getWebBaseUrl() + "/m/weixin/saler/info?saler_id=1";
////                upstream.url = "http://192.168.0.138/tianpingpai_app/active.html";
//                items.add(test);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    public Intent getIntent(int position) {
        return getItem(position).getIntent();
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(ContextProvider.getContext(), R.layout.item_home_action, null);
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.icon_image_view);
        iconImageView.setImageResource(getItem(position).icon);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
        nameTextView.setText(getItem(position).title);
        return convertView;
    }

    public static class Item {
        int icon;
        public String title;
        public Class target;
        String url;

        Item(@DrawableRes int icon, @StringRes int title, Class target) {
            this.icon = icon;
            this.title = ContextProvider.getContext().getString(title);
            this.target = target;
        }

        public Intent getIntent() {
            if (target == null) {
                return null;
            }
            Intent intent = new Intent();
            if(icon == R.drawable.ic_purchase || icon == R.drawable.ic_home_group_shopping){
                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
            }else{
                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_NORMAL);
            }
            intent.putExtra(ContainerActivity.KEY_CONTENT, target);

            intent.putExtra(WebViewController.KEY_URL_INTERCEPTOR, SellerUrlInterceptor.class);
            intent.putExtra(WebViewController.KEY_URL, url);
            return intent;
        }
    }
}
