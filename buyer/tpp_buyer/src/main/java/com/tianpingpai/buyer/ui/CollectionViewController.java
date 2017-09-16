package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.ViewControllerAdapter;
import com.tianpingpai.widget.BadgeView;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Statistics(page = "收藏")
@ActionBar(layout = R.layout.ab_collection ,title = "收藏")
@Layout(id = R.layout.ui_collection)
public class CollectionViewController extends BaseViewController {

    private CollectionProductViewController collectionProductFragment = new CollectionProductViewController();
    private CollectionStoreViewController collectionStoreFragment = new CollectionStoreViewController();

    private BaseViewController currentViewController = collectionProductFragment;

    private BadgeView badgeView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        badgeView = (BadgeView) rootView.findViewById(R.id.cart_badge_view);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        ViewControllerAdapter adapter = new ViewControllerAdapter();
        ArrayList<BaseViewController> viewControllers = new ArrayList<>();
        collectionProductFragment.setMainViewController(this);
        collectionStoreFragment.setMainViewController(this);
        viewControllers.add(collectionProductFragment);
        viewControllers.add(collectionStoreFragment);
        adapter.setViewControllers(viewControllers);
        for (BaseViewController vc : viewControllers) {
            vc.setActivity(getActivity());
        }
        adapter.setTitles(new String[]{"商品", "店铺"});

        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.removeOnPageChangeListener(pageChangeListener);
        viewPager.addOnPageChangeListener(pageChangeListener);
    }

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        collectionProductFragment.onActivityResumed(a);
        collectionStoreFragment.onActivityResumed(a);
    }

    @Override
    public void onActivityPaused(Activity a) {
        super.onActivityPaused(a);
        collectionProductFragment.onActivityPaused(a);
        collectionStoreFragment.onActivityPaused(a);
    }

    @OnClick(R.id.shopping_cart_view)
    private View.OnClickListener goBuyerCartListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),
                    ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,
                    ShoppingCartViewController.class);
            intent.putExtra("position", 2);
            intent.putExtra("is_shop_to_car", CommonUtil.SHOP_TO_CAR);
            getActivity().startActivity(intent);

        }
    };

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0){
                currentViewController = collectionProductFragment;
            }else if(position ==1){
                currentViewController = collectionStoreFragment;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onBackKeyDown(Activity a) {

        return currentViewController.onBackKeyDown(a);
    }

    private int productNum;
    private int storeNum;

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }

    public void setStoreNum(int storeNum) {
        this.storeNum = storeNum;
    }

    public void refreshView(boolean two){
        String s1 ;
        String s2 ;
        if(productNum == 0){
            s1 = "商品";
        }else{
            s1 = "商品"+"("+productNum+")";
        }
        if(storeNum == 0){
            s2 = "店铺";
        }else{

            s2 = "店铺"+"("+storeNum+")";
        }
        tabLayout.getTabAt(0).setText(s1);
        tabLayout.getTabAt(1).setText(s2);
    }
    public void refreshBadgeView(int num){
        badgeView.setBadge(num);
    }
}
