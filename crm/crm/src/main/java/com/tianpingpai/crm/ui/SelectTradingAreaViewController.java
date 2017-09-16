package com.tianpingpai.crm.ui;

import android.view.View;
import android.widget.ListView;

import com.tianpingpai.crm.R;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;

/**
 * Created by kongyi on 15/12/24.
 */

@Layout(id = R.layout.view_controller_select_trading_area)
public class SelectTradingAreaViewController extends BaseViewController {

    @Binding(id = R.id.left_list_view)
    private ListView leftListView;
    @Binding(id = R.id.right_list_view)
    private ListView rightListView;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
    }


}
