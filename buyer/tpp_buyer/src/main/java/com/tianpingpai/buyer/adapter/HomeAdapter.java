package com.tianpingpai.buyer.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brother.tpp.tools.ParseHrefUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.ActivityModel;
import com.tianpingpai.buyer.model.Promotion;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.ui.ActivityDetailViewController;
import com.tianpingpai.buyer.ui.HomeViewController;
import com.tianpingpai.buyer.ui.LoginViewController;
import com.tianpingpai.buyer.ui.WebViewController;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.model.GadgetModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.utils.ColorUtil;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.utils.PriceFormat;
import com.tianpingpai.widget.CirclePageIndicator;
import com.tianpingpai.widget.CirclePageIndicator.OnPageItemClickListener;
import com.tianpingpai.widget.StarRatingBar;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewHolder")
public class HomeAdapter extends BaseAdapter {

    class ItemInfo {
        ItemInfo(int t) {
            this.type = t;
        }

        int type;
        String title;
    }

    private ArrayList<ItemInfo> infoList = new ArrayList<>();

    private static final int ITEM_TYPE_BANNER = 0;
    private static final int ITEM_TYPE_FUNCTION = 1;
    private static final int ITEM_TYPE_CATEGORY = 2;
    private static final int ITEM_TYPE_TITLE = 3;
    private static final int ITEM_TYPE_ACTIVITY = 4;
    private static final int ITEM_TYPE_MORE = 5;
    private static final int ITEM_TYPE_STORES = 6;
    private static final int ITEM_TYPE_ACTIVITY_NEW = 8;

    private int green = ContextProvider.getContext().getResources().getColor(R.color.green);
    private int grayE3 = ContextProvider.getContext().getResources().getColor(R.color.gray_e6);

    private String notificationString;
    public String getNotificationString() {
        return notificationString;
    }
    private String notificationHref;

    public String getNotificationHref() {
        return notificationHref;
    }

    private ArrayList<GadgetModel> gadgets;

    private ArrayList<GadgetModel.Item> bannerItems;
    private ArrayList<GadgetModel.Item> functionItems;
    private ArrayList<GadgetModel.Item> categoryItems;
    private ArrayList<GadgetModel.Item> storeItems;
    private ArrayList<GadgetModel.Item> newActivities;
    private ListResult<ActivityModel> activities;

    private HomeViewController home;

    public HomeViewController setHome(HomeViewController h) {
        return home = h;
    }

    private FragmentActivity getActivity() {
        return home.getActivity();
    }

    private int listViewWidth;

    public void setListViewWidth(int lw) {
        listViewWidth = lw;
    }

    public void addActivities(ListResult<ActivityModel> a) {
        if (a != null && a.getModels() != null && !a.getModels().isEmpty()) {
            this.activities = a;
            notifyDataSetChanged();
        }
    }

    public void setData(ArrayList<GadgetModel> pm) {
        ArrayList<GadgetModel> copy = new ArrayList<>();
//        this.gadgets = pm;
        notificationString = null;
        infoList.clear();
        this.activities = null;
        for (GadgetModel g : pm) {
            switch (g.getType()) {
                case GadgetModel.TYPE_BANNER:
                    copy.add(g);
                    bannerItems = g.getContent();
                    infoList.add(new ItemInfo(ITEM_TYPE_BANNER));
                    break;
                case GadgetModel.TYPE_FUNCTION:
                    copy.add(g);
                    functionItems = g.getContent();
                    infoList.add(new ItemInfo(ITEM_TYPE_FUNCTION));
                    break;
                case GadgetModel.TYPE_CATEGORY:
                    copy.add(g);
                    categoryItems = g.getContent();
                    infoList.add(new ItemInfo(ITEM_TYPE_CATEGORY));
                    break;
                case GadgetModel.TYPE_TITLE:
                    copy.add(g);
                    ItemInfo item = new ItemInfo(ITEM_TYPE_TITLE);
                    item.title = g.getTitle();
                    if (!g.getContent().isEmpty()) {
                        item.title = g.getContent().get(0).getTitle();
                    }
                    infoList.add(item);
                    break;
                case GadgetModel.TYPE_STORES:
                    copy.add(g);
                    storeItems = g.getContent();
                    infoList.add(new ItemInfo(ITEM_TYPE_STORES));
                    break;
                case GadgetModel.TYPE_ACTIVITY:
                    copy.add(g);
                    break;
                case GadgetModel.TYPE_NOTIFICATION:
                    if (g.getContent() != null && !g.getContent().isEmpty()) {
                        notificationString = g.getContent().get(0).getData().getContent();
                        notificationHref = g.getContent().get(0).getUrl();
                    }else{
                        notificationString = null;
                    }
                    break;
                case GadgetModel.TYPE_ACTIVITY_NEW:
                    copy.add(g);
                    infoList.add(new ItemInfo(ITEM_TYPE_ACTIVITY_NEW));
                    newActivities = g.getContent();
                    break;
                default:
                    break;
            }
        }
        this.gadgets = copy;
        notifyDataSetChanged();
        bannerAdapter.notifyDataSetChanged();
        categoryViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count;
        count = infoList == null ? 0 : infoList.size();
        if (activities != null) {
//            Log.e("xx", "a size:" + activities.getModels().size());
            if (activities.getModels().size() % 2 == 0) {
                count += activities.getModels().size() / 2;
            } else {
                count += (activities.getModels().size() + 1) / 2;
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < infoList.size()) {
            return infoList.get(position).type;
        }
        return ITEM_TYPE_ACTIVITY;
    }

    @Override
    public int getViewTypeCount() {
        if (gadgets == null) {
            return 1;
        }
        // return gadgets.size() + 1;
        return 7;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null && convertView instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) convertView;
            Log.e("xx", "position:" + position + " itemType:"
                    + getItemViewType(position) + vg.getChildCount());
        }
        if (position >= infoList.size()) {
            convertView = LayoutInflater.from(ContextProvider.getContext())
                    .inflate(R.layout.item_home_type_activity, null);
            configureActivityType(convertView, position);
            return convertView;
        }
        switch (infoList.get(position).type) {

            case ITEM_TYPE_BANNER:
                convertView = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_home_type_banner, null);
                configureBannerType(convertView);
                break;
            case ITEM_TYPE_FUNCTION:
                convertView = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_home_type_function, null);
                configureFunctionType(convertView);
                break;
            case ITEM_TYPE_CATEGORY:
                convertView = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_home_type_category, null);
                configureCategoryType(convertView);
                break;
            case ITEM_TYPE_TITLE:
                convertView = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_home_title, null);
                Log.e("xx", "pos:" + position);

                TextView tv = (TextView) convertView.findViewById(R.id.title_text_view);
                ItemInfo info = infoList.get(position);
                tv.setText(info.title);
                tv.setTextSize(16);
                break;
            case ITEM_TYPE_ACTIVITY:
                // if (convertView == null) {
                convertView = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_home_type_activity, null);
                // }
                configureActivityType(convertView, position);
                break;
            case ITEM_TYPE_MORE:
                if (convertView == null) {
                    convertView = LayoutInflater
                            .from(ContextProvider.getContext())
                            .inflate(
                                    com.tianpingpai.foundation.R.layout.item_loading_footer,
                                    null);
                }
                break;
            case ITEM_TYPE_STORES:
                convertView = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_home_type_stores, null);
                configureStoresType(convertView);
                break;
            case ITEM_TYPE_ACTIVITY_NEW:
                convertView = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_home_type_activity_new, null);
                configureActivityNew(convertView);
                break;
            default:
                break;
        }

        return convertView;
    }


    private MyAdapter bannerAdapter = new MyAdapter();

    private void configureBannerType(View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(bannerAdapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator) view
                .findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager);
        pageIndicator.setOnPageItemClickListener(bannerItemClickListener);
        pageIndicator.setSelectedColor(green);
        pageIndicator.setUnselectedColor(Color.WHITE);

        int height = (int) (332.0 / 1080 * listViewWidth);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                listViewWidth, height);
        lp.width = listViewWidth;
        lp.height = height;
        view.setLayoutParams(lp);
    }

    private void configureActivityType(View view, int position) {
        if (gadgets == null) {
            return;
        }
        int gadgetsSize = gadgets.size();
//        if(notificationString != null){
//            gadgetsSize--;
//        }
        if (view == null) {
            return;
        }
        int index = (position - gadgetsSize + 1) * 2;
        ImageView leftImageView = (ImageView) view
                .findViewById(R.id.left_image_view);
        ActivityModel activity = activities.getModels().get(index);
        if (activity.getImages() != null && activity.getImages().size() > 0) {
            String url = activity.getImages().get(0);// TODO
            Log.e("xx", "url:" + url + "imageView:" + leftImageView);
            ImageLoader.load(url, leftImageView, R.drawable.default_loading, R.drawable.default_loading);
        }

        leftImageView.setTag(activity);

        leftImageView.setOnClickListener(activityItemOnClickListener);
        leftImageView.setLayoutParams(new LinearLayout.LayoutParams(listViewWidth / 2, listViewWidth / 2));

        TextView leftNameTextView = (TextView) view
                .findViewById(R.id.left_name_text_view);
        leftNameTextView.setText(activity.getProductName());

        TextView leftPriceTextView = (TextView) view
                .findViewById(R.id.left_price_text_view);
        leftPriceTextView.setText(String.format("￥%s/%s", PriceFormat.format(activity.getPrice()), activity.getUnit()));
        index++;
        if (index < activities.getModels().size()) {
            ImageView rightImageView = (ImageView) view
                    .findViewById(R.id.right_image_view);
            ActivityModel a2 = activities.getModels().get(index);
            if (a2.getImages() != null && !a2.getImages().isEmpty()) {
                String url = a2.getImages().get(0);
                rightImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.load(url, rightImageView, R.drawable.default_loading, R.drawable.default_loading);
            } else {
                rightImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                rightImageView.setImageResource(R.drawable.default_loading);
            }
            rightImageView.setTag(a2);

            rightImageView.setOnClickListener(activityItemOnClickListener);
            rightImageView.setLayoutParams(new LinearLayout.LayoutParams(
                    listViewWidth / 2, listViewWidth / 2));
            TextView rightNameTextView = (TextView) view
                    .findViewById(R.id.right_name_text_view);
            rightNameTextView.setText(a2.getProductName());
            TextView rightPriceTextView = (TextView) view
                    .findViewById(R.id.right_price_text_view);
            rightPriceTextView.setText(String.format("￥%s/%s", PriceFormat.format(a2.getPrice()), a2.getUnit()));
        }
    }

    private void configureStoresType(View view) {
        LinearLayout ll = (LinearLayout) view;
        ll.removeAllViews();
        int size =  storeItems == null ? 0 : storeItems.size();
        for(int i = 0;i < size;i++){
            View dividerView = View.inflate(ContextProvider.getContext(),R.layout.view_divider_e9,null);
            ll.addView(dividerView);
            View itemView = storesAdapter.getView(i,null,null);
            ll.addView(itemView);
            itemView.setTag(i);
            itemView.setOnClickListener(storeItemOnClickListener);
        }
    }

    private OnClickListener storeItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (!UserManager.getInstance().isLoggedIn()) {
                ActionSheet as = new ActionSheet();
                as.setHeight(home.getContentHeight());
                LoginViewController loginViewController = new LoginViewController();
                loginViewController.setActionSheet(as);
                loginViewController.setActivity(getActivity());
                as.setViewController(loginViewController);
                as.show();
                return;
            }
            String url = storeItems.get(position).getUrl();
            Log.e("xx", "url=" + url);
            ParseHrefUtil.handleURL(getActivity(), url);
        }
    };

    private void configureActivityNew(View view) {
        LinearLayout linearLayout = (LinearLayout) view;
        View item ;
        if(1 == newActivities.size()){
            item = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_home_activity_new_one, null);
            ImageView oneImageView = (ImageView) item.findViewById(R.id.left_image_view);
            GadgetModel.Item leftItem = newActivities.get(0);
            oneImageView.setTag(leftItem);
            oneImageView.setOnClickListener(newActivityItemOnClickListener);
            String leftImageUrl = leftItem.getImage() + URLApi.getImageTP(750, 160);
            ImageLoader.load(leftImageUrl, oneImageView, R.drawable.default_loading, R.drawable.default_loading);

            int height = (int) (160 * 1.0 / 750 * listViewWidth);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(listViewWidth, height);
            linearLayout.addView(item, params);
        }else if(2 == newActivities.size()){
            item = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_home_activity_new, null);
            ImageView leftImageView = (ImageView) item.findViewById(R.id.left_image_view);
            ImageView rightImageView = (ImageView) item.findViewById(R.id.right_image_view);

            GadgetModel.Item leftItem = newActivities.get(0);
            leftImageView.setTag(leftItem);
            leftImageView.setOnClickListener(newActivityItemOnClickListener);
            GadgetModel.Item rightItem = newActivities.get(1);
            rightImageView.setTag(rightItem);
            rightImageView.setOnClickListener(newActivityItemOnClickListener);

            String leftImageUrl = leftItem.getImage() + URLApi.getImageTP(375, 160);
            ImageLoader.load(leftImageUrl, leftImageView, R.drawable.default_loading, R.drawable.default_loading);
            String rightImageUrl = rightItem.getImage() + URLApi.getImageTP(375, 160);
            ImageLoader.load(rightImageUrl, rightImageView, R.drawable.default_loading, R.drawable.default_loading);

            int height = (int) (160 * 1.0 / 750 * listViewWidth);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(listViewWidth, height);
            linearLayout.addView(item, params);
        }

        View shadowView = new View(ContextProvider.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionUtil.dip2px(0.5f));
        linearLayout.addView(shadowView, params);
    }

    private void configureCategoryType(View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(categoryViewPagerAdapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator) view
                .findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager);
        pageIndicator.setInterceptGesture(false);
        pageIndicator.setSelectedColor(green);
        pageIndicator.setUnselectedColor(grayE3);
        pageIndicator.stopAnimation();

        int height = DimensionUtil.dip2px(156);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                listViewWidth, height);
        lp.width = listViewWidth;
        lp.height = height;
        view.setLayoutParams(lp);
    }

    int[] functionIds = {R.id.function_0,R.id.function_1,R.id.function_2,R.id.function_3};
    private void configureFunctionType(View cv) {
        LinearLayout gv = (LinearLayout) cv.findViewById(R.id.grid_view);
        ArrayList<GadgetModel.Item> functions =functionItems;
        for(int i = 0;i < functionIds.length;i++){
            View view = gv.findViewById(functionIds[i]);
            view.setClickable(true);
            view.setTag(i);
            view.setOnClickListener(functionItemOnClickListener);
            if(i < functions.size()) {
                view.setVisibility(View.VISIBLE);
                ImageView iv = (ImageView) view.findViewById(R.id.image_view);
                String url = gadgets.get(1).getContent().get(i).getImage();
                ImageLoader.load(url, iv);

                TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
                nameTextView.setText(functionItems.get(i).getTitle());
            }else{
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    private BaseAdapter storesAdapter = new BaseAdapter() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(ContextProvider.getContext())
                    .inflate(R.layout.item_home_store, null);

            ImageView iv = (ImageView) view.findViewById(R.id.logo_image_view);
            String url = storeItems.get(position).getImage();// TODO
//            ImageLoader.load(url, iv, R.drawable.ic_default_store, R.drawable.ic_default_store);
            TextView nameTextView = (TextView) view.findViewById(R.id.store_name_text_view);
            nameTextView.setText(storeItems.get(position).getTitle());
            TextView addrTextView = (TextView) view.findViewById(R.id.address_text_view);
            TextView descTextView = (TextView) view.findViewById(R.id.description_text_view);
            StarRatingBar ratingBar = (StarRatingBar) view.findViewById(R.id.rating_bar);
//            ratingBar.setFullStartRes(R.drawable.star_big);
//            ratingBar.setEmptyStarRes(R.drawable.star_empty);
            TextView sellNumberTextView = (TextView) view.findViewById(R.id.sale_number_text_view);


//            TextView addressTextView = (TextView) view.findViewById(R.id.address_edit_text);
            GadgetModel.Data data = storeItems.get(position).getData();
            if(data != null  && !TextUtils.isEmpty(data.getAddress().trim())){
                addrTextView.setVisibility(View.VISIBLE);
                addrTextView.setText(data.getAddress().trim());
            }else {
                addrTextView.setVisibility(View.GONE);
            }
            if (data != null && !TextUtils.isEmpty(data.getDescription())) {
                descTextView.setText(data.getDescription());
                ratingBar.setRating((float) data.getRatings());
//                addressTextView.setText(data.getAddress());
            } else {
                descTextView.setText("");
                descTextView.setVisibility(View.GONE);
            }

            LinearLayout couponContainer = (LinearLayout) view.findViewById(R.id.coupon_container);

            if (data != null && data.getPromotions() != null) {
                List<Promotion> modelPromotions = data.getPromotions();
                couponContainer.removeAllViews();
                for (Promotion m : modelPromotions) {
                    View couponView = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_store_coupon, null);
                    TextView labelTextView = (TextView) couponView.findViewById(R.id.label_text_view);
                    TextView couponTextView = (TextView) couponView.findViewById(R.id.coupon_text_view);
                    String label = m.getLabel();
                    labelTextView.setText(label);

                    GradientDrawable myGrad3 = (GradientDrawable) labelTextView.getBackground();
                    String bgColor = m.getBgcolor();
                    myGrad3.setColor(ColorUtil.getColorByString(bgColor));
                    couponTextView.setText(m.getTitle());
                    couponContainer.addView(couponView);
                }
            }

            SpannableStringBuilder ssb = new SpannableStringBuilder("已售" + data.getOrderNum() + "单");
            ssb.setSpan(new ForegroundColorSpan(green), 2, ssb.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            sellNumberTextView.setText(ssb);

            return view;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return storeItems == null ? 0 : storeItems.size();
        }
    };

    private int pageSize = 6;

    private class CategoryAdapter extends BaseAdapter {
        public void setCategoryPage(int categoryPage) {
            this.categoryPage = categoryPage;
        }

        private int categoryPage = 0;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            position = position + (categoryPage) * pageSize;
            View view = convertView;
            if (convertView == null) {
                view = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_home_category, null);
            }
            ImageView iv = (ImageView) view.findViewById(R.id.image_view);
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postTranslate(100, 0);
            iv.setImageMatrix(matrix);
            String url = categoryItems.get(position).getImage();// TODO
            ImageLoader.load(url, iv, R.drawable.default_loading, R.drawable.default_loading);

            TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            nameTextView.setText(categoryItems.get(position).getTitle());

            TextView descTextView = (TextView) view.findViewById(R.id.desc_text_view);
            descTextView.setText(categoryItems.get(position).getData().getDescription());
            return view;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            if (categoryItems == null) {
                return 0;
            }
            Log.e("xx", "page:" + categoryPage);
            if (categoryPage == 0) {//TODO
                Log.e("xx", "6");
                return 8;
            }
            int left = categoryItems.size() - (categoryPage) * pageSize;
            Log.e("xx", "6|" + (categoryItems.size() - (categoryPage + 1) * pageSize));
            return left > 8 ? 8 : left;
//            return categoryItems == null ? 0 : categoryItems.size();
        }
    }


    private PagerAdapter categoryViewPagerAdapter = new PagerAdapter() {

        LayoutInflater inflater = LayoutInflater.from(ContextProvider.getContext());
        private View[] views = new View[7];

        {
            for (int i = 0; i < 7; i++) {
                ViewGroup gl = (ViewGroup) inflater.inflate(R.layout.item_home_type_category_item_viewpager, null);
                views[i] = gl;
            }
        }

        @Override
        public int getCount() {
            return categoryItems == null ? 0 : categoryItems.size() / 6 + 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views[position
                    % views.length]);

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View iv = views[position % views.length];
            CategoryAdapter categoryAdapter = (CategoryAdapter) iv.getTag();
            if (categoryAdapter == null) {
                categoryAdapter = new CategoryAdapter();
                iv.setTag(categoryAdapter);
            }
            categoryAdapter.setCategoryPage(position);
            int[] ids = {R.id.category_0, R.id.category_1, R.id.category_2, R.id.category_3, R.id.category_4, R.id.category_5};
            for (int i = 0; i < ids.length; i++) {
                View v = iv.findViewById(ids[i]);
                if(i < categoryAdapter.getCount()) {
                    categoryAdapter.getView(i, v, null);

                    int index = i + (position) * pageSize;
                    v.setTag(index);
                    v.setOnClickListener(categoryItemOnClickListener);
                    v.setVisibility(View.VISIBLE);
                }else{
                    v.setVisibility(View.INVISIBLE);
                }
            }

            ViewPager vp = (ViewPager) container;
            vp.removeView(iv);
            ViewGroup parent = (ViewGroup) iv.getParent();
            if (parent != null) {
                parent.removeView(iv);
            }
            vp.addView(iv, 0);
            return iv;
        }
    };

    private OnClickListener functionItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            String url = functionItems.get(position).getUrl();
            Log.e("url====", url + "");
            //TODO
            if(!ParseHrefUtil.handleURL(getActivity(), url)){
                Intent intent = new Intent(getActivity(),ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
                intent.putExtra(WebViewController.KEY_URL,url);
//                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_NORMAL);
                getActivity().startActivity(intent);
            }
        }
    };

    private OnClickListener categoryItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            String url = categoryItems.get(position).getUrl();
            Log.e("xx", "url=" + url);
            if (categoryItems.get(position).isDisabled()) {
                return;
            }
            ParseHrefUtil.handleURL(getActivity(), url);
//            if(ParseHrefUtil.handleURL(getActivity(),url)){
//                URI uri = null;
//                try {
//                    uri = new URI(url);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//                int queryIndex = url.indexOf("?");
//                if("/store/list".equals(uri.getPath())){
//                    if (queryIndex != -1) {
//                        String queryStrings = url.substring(queryIndex + 1);
//                        String[] paramStrings = queryStrings.split("&");
//
//                        for (String ps : paramStrings) {
//                            int index = ps.indexOf("=");
//                            String key = ps.substring(0, index);
//                            String value = ps.substring(index + 1);
//                            if("category_id".equals(key)){
//                                Model m = new Model();
//                                int vv = Integer.parseInt(value);
//                                m.set(key,vv);
//                                home.mainFragment.setShopData(m);
//                            }
//                            Log.e("xx", "===key:" + key + " := " + value);
//                        }
//                    }
//                }
//            }
        }
    };

    public class MyAdapter extends PagerAdapter {
        ImageView[] mImageViews = {
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),
                new ImageView(ContextProvider.getContext()),};

        @Override
        public int getCount() {
            return bannerItems == null ? 0 : bannerItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position
                    % mImageViews.length]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = mImageViews[position % mImageViews.length];
            iv.setImageResource(R.drawable.ic_home);
            ViewPager vp = (ViewPager) container;
            vp.removeView(iv);
            ViewGroup parent = (ViewGroup) iv.getParent();
            if (parent != null) {
                parent.removeView(iv);
            }
            vp.addView(iv, 0);
            String url = bannerItems.get(position).getImage();
            if (!url.contains("?")) {
                url += "?imageMogr2/auto-orient/thumbnail/" + 1080 + "x" + 332 + "!/strip/quality/80/format/jpg/interlace/1";//TODO
            }
            ImageLoader.load(url, iv, R.drawable.loading_banner, R.drawable.loading_banner);
            return iv;
        }
    }

    private OnClickListener activityItemOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(),
                    ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,
                    ActivityDetailViewController.class);
            ActivityModel activity = (ActivityModel) v.getTag();
            i.putExtra(ActivityDetailViewController.KEY_ACTIVITY_ID, activity.getId());
            getActivity().startActivity(i);
        }
    };

    private OnClickListener newActivityItemOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            GadgetModel.Item activity = (GadgetModel.Item) v.getTag();
            if (!ParseHrefUtil.handleURL(getActivity(), activity.getUrl())) {
                Intent i;
                i = new Intent(getActivity(), ContainerActivity.class);
                i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                i.putExtra(WebViewController.KEY_URL, activity.getUrl());
                getActivity().startActivity(i);
            }
        }
    };

    private OnPageItemClickListener bannerItemClickListener = new OnPageItemClickListener() {

        @Override
        public void onPageItemClicked(int position) {
            if (bannerItems == null || bannerItems.isEmpty()) {
                return;
            }
            GadgetModel.Item banner = bannerItems.get(position);
            String url = banner.getUrl();
            if (TextUtils.isEmpty(url)) {
                return;
            }
            if (!ParseHrefUtil.handleURL(getActivity(), url)) {
                Intent i;
                i = new Intent(getActivity(), ContainerActivity.class);
                i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                i.putExtra(WebViewController.KEY_URL, url);
                getActivity().startActivity(i);
            }
        }
    };

    public void clear() {
        this.infoList.clear();
        this.activities = null;
        this.gadgets = null;
        this.bannerItems = null;
        this.functionItems = null;
        this.categoryItems = null;
        notifyDataSetChanged();
        bannerAdapter.notifyDataSetChanged();
        categoryViewPagerAdapter.notifyDataSetChanged();
    }
}
