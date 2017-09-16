package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;

@ActionBar(hidden = true)
@Layout(id = R.layout.ui_guide)
public class GuideViewController extends BaseViewController {

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyAdapter());
        showContent();
        hideActionBar();
    }

    int[] items = {
            R.drawable.guide_0,
            R.drawable.guide_1,
            R.drawable.guide_2,
    };

    int[] bgColors = {
            Color.parseColor("#FFFFFF"),//  50bb83
            Color.parseColor("#fe6152"), //  238aef
            Color.parseColor("#ffb332"),
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
            return items == null ? 0 : items.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
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
            iv.setLayoutParams(new ViewPager.LayoutParams());
            ViewPager vp = (ViewPager) container;
            vp.removeView(iv);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewGroup parent = (ViewGroup) iv.getParent();
            if (parent != null) {
                parent.removeView(iv);
            }
            vp.addView(iv, 0, new ViewPager.LayoutParams());
            int res = items[position];
            iv.setBackgroundColor(bgColors[position]);
            iv.setImageResource(res);
            if (position == items.length - 1) {
                iv.setOnClickListener(lastItemClickListener);
            }
            return iv;
        }

        View.OnClickListener lastItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserManager.getInstance().isLoggedIn()){
                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }else{

                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        };
    }
}
