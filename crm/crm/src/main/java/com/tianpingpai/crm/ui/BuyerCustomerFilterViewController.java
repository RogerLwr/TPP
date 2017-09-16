package com.tianpingpai.crm.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerFilterAdapter;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.user.UserModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@ActionBar(hidden = true)
@Layout(id = R.layout.vc_customers_filter)
public class BuyerCustomerFilterViewController extends BaseViewController {

    private CustomerFilterAdapter adapter = new CustomerFilterAdapter(CustomerModel.USER_TYPE_BUYER);

    private ActionSheet actionSheet;
    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }
    public interface OnConditionSelectedListener{
        void onConditionSelected();
    }

    OnConditionSelectedListener onConditionSelectedListener;

    public void setOnConditionSelectedListener(OnConditionSelectedListener listener){
        onConditionSelectedListener = listener;
    }

    public CustomerFilterAdapter getAdapter(){
        return adapter;
    }

    private HashSet<UserModel> selectedUserSet = new HashSet<>();
    private HashMap<UserModel,HashSet<UserModel>> subs = new HashMap<>();

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
            SelectionViewController selectionViewController;
            selectionViewController = new SelectionViewController();
            String[] data = null;

            adapter.setSelection(i);
            int position = 0;
            switch (i) {
//                case 0:
//                    data = CustomerFilterAdapter.userTypes;
//                    position = adapter.selectedUserType;
//                    break;
                case 0:
                    MarketListViewController marketListViewController = new MarketListViewController();
                    marketListViewController.setActivity(getActivity());
                    marketListViewController.setListener(new MarketListViewController.MarketSelectionListener() {
                        @Override
                        public void onMarketSelected(MarketModel marketModel) {
                            adapter.setMarket(marketModel);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    marketListViewController.setOnBackListener(new MarketListViewController.OnBackListener() {
                        @Override
                        public void onBackClick() {
                            adapter.setSelection(-1); //返回时把选中的颜色恢复成白色
                        }
                    });
                    getViewTransitionManager().pushViewController(marketListViewController);
                    return;
                case 1:
                    data = CustomerFilterAdapter.timeTypes;
                    position = adapter.selectedOrderTime;
//                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    selectionViewController.setMultiSelection(true);
                    selectionViewController.setUser(UserManager.getInstance().getCurrentUser());
                    selectionViewController.setSelectionSet(selectedUserSet);
                    selectionViewController.setSubMarkers(subs);
                    break;
                case 3:
                    data = CustomerFilterAdapter.customerSource;
                    position = adapter.selectedCustomerSource;
                    adapter.notifyDataSetChanged();
                    break;

                case 4:
                    data = CustomerFilterAdapter.customerStatus;
                    position = adapter.selectedCustomerStatus;
                    adapter.notifyDataSetChanged();
                    break;
                case 5:
                    data = CustomerFilterAdapter.notOrder;
                    position = adapter.selectedNotOrderStatus;
                    adapter.notifyDataSetChanged();
                    break;
            }

            selectionViewController.setOnSelectionListener(new SelectionViewController.OnSelectionListener() {
                @Override
                public void onSelected(int position) {
                    switch (i) {
                        case 0:
//                            adapter.selectedUserType = position;
                            break;
                        //ky
                        case 1:
                            adapter.selectedOrderTime = position;
                            break;
                        //ky
                        case 2:
                            //TODO
                            break;
                        case 3:
                            adapter.selectedCustomerSource = position;
                            break;
                        case 4:
                            adapter.selectedCustomerStatus = position;
                            break;
                        case 5:
                            adapter.selectedNotOrderStatus = position;
                            break;
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onMultiSelectionConfirmed(Collection<Integer> ids) {
                    adapter.notifyDataSetChanged();
                    adapter.setMarketers(selectedUserSet);
                }
            });

            selectionViewController.setData(data);
            selectionViewController.setActivity(getActivity());
            selectionViewController.setSelection(position);
            selectionViewController.setOnBackListener(new SelectionViewController.OnBackListener() {
                @Override
                public void onBackClick() {
                    adapter.setSelection(-1); //返回时把选中的颜色恢复成白色
                }
            });
            getViewTransitionManager().pushViewController(selectionViewController);
        }
    };

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        hideActionBar();
        ListView listView = (ListView) getView().findViewById(R.id.filter_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);

        getView().findViewById(R.id.back_button).setOnClickListener(backButtonListener);
        getView().findViewById(R.id.done_button).setOnClickListener(doneButtonListener);

    }

    public int getTimeType() {
        return adapter.selectedOrderTime - 1;
    }

    public int getUserType() {
        if(adapter.selectedUserType == 0){
            return -1;
        }
        if(adapter.selectedUserType == 1){
            return 0;
        }
        return 1;
    }

    public String getMarketerIds() {
        String s = "";
        for (UserModel user : selectedUserSet) {
            s += user.getId() + ",";
        }
        return s;
    }

    public void onCurrentUserChange(){
        if(selectedUserSet.isEmpty()){
            UserModel user = UserManager.getInstance().getCurrentUser();
            if(user != null){
                selectedUserSet.add(user);
            }
        }
    }

    {
        UserModel um = UserManager.getInstance().getCurrentUser();
        if (um != null) {
            this.selectedUserSet = new HashSet<>();
            this.selectedUserSet.add(um);
            adapter.setMarketers(selectedUserSet);
        }
    }

    View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };

    View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onConditionSelectedListener.onConditionSelected();
            actionSheet.dismiss();
        }
    };
}
