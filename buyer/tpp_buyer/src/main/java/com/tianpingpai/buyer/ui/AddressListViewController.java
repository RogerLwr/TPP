package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.AddressAdapter;
import com.tianpingpai.buyer.manager.AddressManager;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.AddressModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;

@Statistics(page = "地址列表")
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_address)
@Layout(id = R.layout.ui_address_list)
public class AddressListViewController extends BaseViewController {

    public static final String KEY_IS_SELECTION_MODE = "key.isSelectionMode";
    public static final String KEY_ADDRESS_MODEL = "key.address";

    private boolean selectionMode;
    private AddressAdapter addressAdapter = new AddressAdapter();

    private SwipeRefreshLayout refreshLayout;
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData();
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        selectionMode = a.getIntent().getBooleanExtra(KEY_IS_SELECTION_MODE, false);
        addressAdapter.setSelectionMode(selectionMode);
        addressAdapter.setFragment(this);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        View actionBarView = setActionBarLayout(R.layout.ab_title_white);
        TextView addButton = (TextView) actionBarView.findViewById(R.id.ab_right_button);
        addButton.setOnClickListener(addButtonListener);
        if (selectionMode) {
            setTitle("选择收货地址");
            addButton.setText("管理");
        } else {
            setTitle("地址管理");
            addButton.setText("新增");
        }
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(refreshListener);
        ListView addressListView = (ListView) rootView.findViewById(R.id.address_list_view);
        addressListView.setAdapter(addressAdapter);
        addressAdapter.setOnSetDefaultAddressListener(defaultButtonListener);
        if (selectionMode) {
            addressListView.setOnItemClickListener(addressAdapter);
        }
        if (selectionMode) {
            addressListView.setDividerHeight(DimensionUtil.dip2px(0.5f));
        }
        AddressManager.getInstance().registerListener(addressStateListener);
        showContent();
        refreshLayoutControl.triggerRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AddressManager.getInstance().unregisterListener(addressStateListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayoutControl.triggerRefresh();
    }

    private void loadData() {
        if (!UserManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
            getActivity().startActivity(intent);
            getActivity().finish();
            return;
        }
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLUtil.OWNDES_URL, listener);
        req.addParam("phone", UserManager.getInstance().getCurrentUser().getPhone());
        req.setParser(new GenericModelParser());
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    @Override
    protected void onReloadData() {
        super.onReloadData();
        loadData();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
//            hideLoading();
            refreshLayout.setRefreshing(false);
            if(data.isSuccess()){
                ArrayList<Model> addresses = (ArrayList<Model>) data.getModel().getList("receiveAddress", Model.class);
                addressAdapter.setModels(addresses);
                if(addresses.isEmpty()){
                    showEmptyView();
                }else{
                    hideEmptyView();
                }
            }else {
                ResultHandler.handleError(data, AddressListViewController.this);
            }
            showContent();
        }
    };

    private View.OnClickListener addButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            if (selectionMode) {
                intent.putExtra(ContainerActivity.KEY_CONTENT, AddressListViewController.class);
            } else {
                intent.putExtra(ContainerActivity.KEY_CONTENT, EditAddressViewController.class);
            }
            getActivity().startActivity(intent);
        }
    };

    private HttpRequest.ResultListener<ModelResult<Void>> saveListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), "设置默认地址成功", Toast.LENGTH_SHORT).show();
                refreshLayoutControl.triggerRefresh();
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AddressAdapter.OnSetDefaultAddressListener defaultButtonListener = new AddressAdapter.OnSetDefaultAddressListener() {
        @Override
        public void onSetDefaultAddress(Model model) {
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLUtil.EDITORADDRESS_URL, saveListener);
            req.setMethod(HttpRequest.POST);
            req.addParam("id", model.getInt("id") + "");
            req.addParam("consignee", model.getString("consignee"));
            req.addParam("phone", model.getString("phone"));
            req.addParam("address", model.getString("address"));
            req.addParam("area", model.getString("area") + "");
            req.setParser(new ModelParser<>(Void.class));
            String lat = model.getString("lat");
            if (!TextUtils.isEmpty(lat)) {
                req.addParam("lat", lat);
            }

            String lng = model.getString("lng");
            if (!TextUtils.isEmpty(lng)) {
                req.addParam("lng", lng);
            }

            req.addParam("detail", model.getString("detail") + "");
            VolleyDispatcher.getInstance().dispatch(req);
        }
    };

    public void editAddress(Model address) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.KEY_CONTENT, EditAddressViewController.class);
        AddressModel addressModel = AddressModel.fromJSONModle(address);
        intent.putExtra(EditAddressViewController.KEY_ADDRESS_MODEL, addressModel);
        getActivity().startActivity(intent);
    }

    public void selectModel(Model address) {
        Intent data = new Intent();
        AddressModel addressInfo = AddressModel.fromJSONModle(address);
        data.putExtra(KEY_ADDRESS_MODEL, addressInfo);
        if(getActivity() != null){
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        }
    }

    public void deleteAddress(final Model address) {
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog();
        actionSheetDialog.setActionSheet(getActionSheet(true));
        actionSheetDialog.setTitle("确定删除吗？");
        actionSheetDialog.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = URLUtil.DELETEADDRESS_URL;
                HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, deleteListener);
                req.setMethod(HttpRequest.POST);
                req.addParam("id", address.getInt("id") + "");
                req.setParser(new ModelParser<>(Void.class));
                VolleyDispatcher.getInstance().dispatch(req);
                actionSheetDialog.dismiss();
            }
        });
        actionSheetDialog.show();
    }

    private HttpRequest.ResultListener<ModelResult<Void>> deleteListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if (data.isSuccess()) {
                refreshLayoutControl.triggerRefresh();
                Toast.makeText(ContextProvider.getContext(), "删除成功!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ContextProvider.getContext(), "" + data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ModelStatusListener<ModelEvent, AddressModel> addressStateListener = new ModelStatusListener<ModelEvent, AddressModel>() {
        @Override
        public void onModelEvent(ModelEvent event, AddressModel model) {
            switch (event) {
                case OnModelUpdate:
                    refreshLayoutControl.triggerRefresh();
                    break;
            }
        }
    };
}
