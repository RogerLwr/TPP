package com.tianpingpai.widget;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.utils.DimensionUtil;

import java.util.ArrayList;

@ActionBar(hidden = true)
public class NotificationControl extends BaseViewController {

    private ArrayList<String> texts = new ArrayList<>();
    private int index;

    {
        setLayoutId(R.layout.foundation_notification_control);
    }

    private static final long TRANSLATE_DURATION = 400;
    private NotificationView notificationView;

    private Animation showAnimation,hideAnimation;

    FrameLayout ss ;
    private AdapterView.OnItemClickListener onItemClickListener;

    public void setOnNotificationClickListener(AdapterView.OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        Log.e("xx","noti");
        notificationView = (NotificationView) rootView.findViewById(R.id.notification_view);

        showAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_from_bottom);
        hideAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_to_top);

        String text = "There are several methods available in the Reflection API that can be used to retrieve annotations.  是不是 点点 565  766  跏  量";
        texts.add(text);
        texts.add("重要】开始有效时间和结束有效时间都没有办法编辑分秒；系统默认当前分秒；要求：分秒默认值为0分0秒；开始时间要晚于当前时间");
        texts.add(" it works without quickly pressing the button, and works without the animation then you may be seeing how animations actually work. From my understanding what is displayed in the animation is separate from the View objects they are animat");

        notificationView.setAnimationListener(listener);
        ss = (FrameLayout) rootView.findViewById(R.id.test_container);
        showAnimation.setFillAfter(true);
        hideAnimation.setFillAfter(true);

        index = -1;
        showNext();
    }

    private NotificationView.OnAnimationListener listener = new NotificationView.OnAnimationListener() {
        @Override
        public void onAnimationFinished() {
            Log.e("xx","complete");
            showNext();
        }
    };

    private void showNext(){
        NotificationView notificationBackView = new NotificationView(getActivity());
        notificationBackView.setAnimation(showAnimation);
        notificationBackView.setWidth(ss.getWidth());

        if(index++ == texts.size() - 1){
            index = 0;
        }

        String text = texts.get(index);
        notificationBackView.setText(text);
        ss.addView(notificationBackView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionUtil.dip2px(50)));
        notificationBackView.start();

        hideAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_to_top);
        hideAnimation.reset();
        notificationView.setAnimation(hideAnimation);

        showAnimation.startNow();
        hideAnimation.startNow();

        final View oldView = notificationView;
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ss.removeView(oldView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        notificationView = notificationBackView;
        notificationView.setAnimationListener(listener);
        notificationView.setClickable(true);
        notificationView.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(onItemClickListener  != null){
                onItemClickListener.onItemClick(null,null,index,0);
            }
        }
    };
}
