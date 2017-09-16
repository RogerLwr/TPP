package com.tianpingpai.crm.viewcontroller1030;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerClaimAdapter;
import com.tianpingpai.crm.ui.CrmBaseViewController;
import com.tianpingpai.crm.ui.SelectDateViewController;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;

/**
 * Created by yi on 2015/11/4.
 */
//客户认领
@Layout(id = R.layout.view_controller_customer_claim)
public class CustomerClaimViewController extends CrmBaseViewController {


    private ListView customerListView;

    private TextView rightBtn;

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadCustomers(page);
        }
    };


    @Override
    protected void onConfigureView() {
        super.onConfigureView();

        View topv = setActionBarLayout(R.layout.ab_back_title_right);
        rightBtn = (TextView)topv.findViewById(R.id.ab_right_button);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(dataOnClickListener);
        setTitle("客户认领");

        View rootview = getView();
        customerListView = (ListView)rootview.findViewById(R.id.customers_claim_list_view);
        CustomerClaimAdapter cca = new CustomerClaimAdapter();




    }

    @Override
    public void didSetContentView(Activity a) {
        super.didSetContentView(a);
//        View topview = setActionBarLayout(R.layout.ab_back_title_right);
//        rightBtn = (TextView)topview.findViewById(R.id.ab_right_button);
//        rightBtn.setVisibility(View.VISIBLE);
//        rightBtn.setOnClickListener(dataOnClickListener);
//        setTitle("客户认领");
    }


    public void loadCustomers(int page){



    }

    private View.OnClickListener dataOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ActionSheet actionSheet = getActionSheet(true);
            actionSheet.setHeight(getView().getHeight());
            actionSheet.setWidth(getView().getWidth() * 3 / 4);
            SelectDateViewController selectDateViewController = new SelectDateViewController();
            selectDateViewController.setActivity(getActivity());
            selectDateViewController.setActionSheet(actionSheet);
            selectDateViewController.setOnSelectDateListener(new SelectDateViewController.OnSelectDateListener() {
                @Override
                public void onSelectDate(String s, int id) {
                    rightBtn.setText(s);
//                    loadData(id - 1);
                }
            });

            actionSheet.setViewController(selectDateViewController);
            actionSheet.setStyle(ActionSheet.STYLE_RIGHT_TO_LEFT);
            actionSheet.show();
        }
    };

}
