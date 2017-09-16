package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.MainViewController;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@Layout(id = R.layout.fragment_guide)
@Statistics(page = "引导页")
@ActionBar(hidden = true)
public class GuideViewController extends BaseViewController{

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyAdapter());
        showContent();
    }

    int[] items = {
            R.drawable.spl,
    };

    int[] bgColors = {
            Color.parseColor("#00000000")
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
            ViewPager vp = (ViewPager) container;
            vp.removeView(iv);
            ViewGroup parent = (ViewGroup) iv.getParent();
            if (parent != null) {
                parent.removeView(iv);
            }
            vp.addView(iv, 0);
            int res = items[position];
            iv.setBackgroundColor(bgColors[position]);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(res);
            if (position == items.length - 1) {
                iv.setOnClickListener(lastItemClickListener);
            }
            return iv;
        }

        private View.OnClickListener lastItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        };
    }
}
