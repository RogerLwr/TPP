package com.tianpingpai.crm.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.OrderFilterCategoryAdapter;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.user.UserModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@Layout(id = R.layout.vc_order_filter)
public class OrdersFilterViewController extends BaseViewController {
    // 订单状态  商家类型
    OrderFilterCategoryAdapter adapter = new OrderFilterCategoryAdapter();

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

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        hideActionBar();
        ListView listView = (ListView) getView().findViewById(R.id.filter_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);

        getView().findViewById(R.id.back_button).setOnClickListener(backButtonListener);
        getView().findViewById(R.id.done_button).setOnClickListener(doneButtonListener);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {

            SelectionViewController selectionViewController;
            selectionViewController = new SelectionViewController();

            String[] data = null;
            adapter.setSelection(i);
            int position = 0;
            switch (i) {
                case 0:
                    data = OrderFilterCategoryAdapter.orderTypes;
                    position = adapter.selectedOrderState;
                    break;
                case 1:
                    data = OrderFilterCategoryAdapter.userTypes;
                    position = adapter.selectedUserType;
                    break;
                case 2:
                    data = OrderFilterCategoryAdapter.timeTypes;
                    position = adapter.selectedOrderTime;
                    break;
                case 3:
                    selectionViewController.setMultiSelection(true);
                    selectionViewController.setUser(UserManager.getInstance().getCurrentUser());
                    selectionViewController.setSelectionSet(selectedUserSet);
                    break;
            }
            selectionViewController.setData(data);
            selectionViewController.setOnBackListener(new SelectionViewController.OnBackListener() {
                @Override
                public void onBackClick() {
                    adapter.setSelection(-1); //返回时把选中的颜色恢复成白色
                }
            });
            selectionViewController.setSubMarkers(subs);

            selectionViewController.setOnSelectionListener(new SelectionViewController.OnSelectionListener() {
                @Override
                public void onSelected(int position) {
                    switch (i) {
                        case 0:
                            adapter.selectedOrderState = position;//TODO
                            break;
                        case 1:
                            adapter.selectedUserType = position;
                            break;
                        case 2:
                            adapter.selectedOrderTime = position;
                            break;
                        case 3:
                            //TODO
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
            selectionViewController.setSelection(position);
            selectionViewController.setActivity(getActivity());
            getViewTransitionManager().pushViewController(selectionViewController);
        }
    };

    public int getTimeType() {
        return adapter.selectedOrderTime - 1;
    }

    public String getOrderStatus(){
        return adapter.orderTypes[adapter.selectedOrderState];
    }

    public int getOrderType() {
        return adapter.selectedOrderState - 1;
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

    private HashSet<UserModel> selectedUserSet = new HashSet<>();
    private HashMap<UserModel,HashSet<UserModel>> subs = new HashMap<>();

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
