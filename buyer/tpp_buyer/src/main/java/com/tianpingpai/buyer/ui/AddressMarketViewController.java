package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.SelectMarketAdapter;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.AddressModel;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.JsonObjectMapper;

import java.util.ArrayList;

@Layout(id = R.layout.vc_market_list)
public class AddressMarketViewController extends BaseViewController{

    private SelectMarketAdapter adapter = new SelectMarketAdapter();
    private AddressModel address;
    private View loadingContainer;
    private SelectMarketViewController selectMarketViewController;

    public void setAddress(AddressModel address){
        this.address = address;
        adapter.setAddress(address);
        loadMarkets(1);
    }

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadMarkets(page);
        }
    };

    @Override
    public void showLoading() {
        loadingContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        hideActionBar();
        loadingContainer = rootView.findViewById(R.id.loading_container);
        adapter.setPageControl(pageControl);
        adapter.setSelectAddressButtonListener(selectAddressButtonListener);
        adapter.setShowCurrentLocation(false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(marketClickListener);
        if(address == null){
            loadAddressData();
        }
        UserManager.getInstance().registerListener(loginListener);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(loginListener);
    }

    private ModelStatusListener<UserEvent, UserModel> loginListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if(event == UserEvent.Login){
                loadAddressData();
            }
        }
    };

    private void loadMarkets(int page) {
        String url = ContextProvider.getBaseURL() + "/api/market/list";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, marketsListener);
        req.addParam("pageSize", 5 + "");
        req.addParam("pageNo", page + "");

        if (this.address != null && address.getLat() != null && !address.getLat().equals("")) {
            req.addParam("lat", address.getLat());
            req.addParam("lng", address.getLng());
        }

        req.setAttachment(page);
        req.setParser(new JSONListParser());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                adapter.setLoading(false);
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        if(page == 1) {
            showLoading();
        }
    }

    private HttpRequest.ResultListener<ListResult<Model>> marketsListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            adapter.setLoading(false);
            hideLoading();
            if (data.isSuccess()) {
                if (request.getAttachment(Integer.class) == 1) {
                    adapter.clear();
                }
                adapter.addMarkets(data);
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_LONG).show();
            }
        }
    };
    private AdapterView.OnItemClickListener marketClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                Model market = adapter.getItem(position);
                MarketModel marketModel = new MarketModel();
                JsonObjectMapper.map(market, marketModel);
                selectMarketViewController.selectMarket(marketModel);
            }
        }
    };

    private View.OnClickListener selectAddressButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, AddressListViewController.class);
            intent.putExtra(AddressListViewController.KEY_IS_SELECTION_MODE, true);
            getActivity().startActivityForResult(intent, 1);
        }
    };

    private void loadAddressData() {
        if(!UserManager.getInstance().isLoggedIn()){
            return;
        }
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLUtil.OWNDES_URL, listener);
        req.addParam("phone", UserManager.getInstance().getCurrentUser().getPhone());
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                ArrayList<Model> addresses = (ArrayList<Model>) data.getModel().getList("receiveAddress", Model.class);
                if(addresses != null && !addresses.isEmpty()){
                    AddressModel am = new AddressModel();
                    JsonObjectMapper.map(addresses.get(0),am);
                    setAddress(am);
                }
            }
        }
    };

    public void setSelectMarketViewController(SelectMarketViewController selectMarketViewController) {
        this.selectMarketViewController = selectMarketViewController;
        adapter.setSelectMarketViewController(selectMarketViewController);
    }
}
