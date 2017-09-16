package com.tianpingpai.buyer.ui;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.tianpingpai.buyer.R;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.utils.DimensionUtil;
//import com.tianpingpai.widget.NotificationControl;

@Layout(id = R.layout.ui_test)
public class TestViewController extends BaseViewController {

    @Binding(id = R.id.test_container)
    private FrameLayout container ;

//    private NotificationControl notificationControl = new NotificationControl();

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
//        notificationControl.setActivity(getActivity());
//        notificationControl.createView(getActivity().getLayoutInflater(),null);
//        container.addView(notificationControl.getView(),new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionUtil.dip2px(50)));
//        Log.e("xx","rootView" + getView());
//        notificationControl.setOnNotificationClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("xx","positon:" + position);
//            }
//        });
    }
}
