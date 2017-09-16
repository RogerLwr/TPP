package com.tianpingpai.buyer.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.SelectMarketAdapter;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.location.LocationEvent;
import com.tianpingpai.location.LocationManager;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.utils.JsonObjectMapper;

@ActionBar(hidden = true)
@Layout(id = R.layout.vc_market_list)
public class CurrentLocationMarketViewController extends BaseViewController {

    private LocationManager locationManager = new LocationManager();
    private SelectMarketAdapter adapter = new SelectMarketAdapter();

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadMarkets(page);
        }
    };

    private View loadingContainer;
    private SelectMarketViewController selectMarketViewController;

    public void setSelectMarketViewController(SelectMarketViewController vc){
        this.selectMarketViewController = vc;
        adapter.setSelectMarketViewController(vc);
    }

    private Model currentCity;
    private LocationModel currentLocation;

    public void setCity(Model city,boolean load){
        this.currentCity = city;
        adapter.setCity(city);
        adapter.setCurrentAddress(null);
        if(load) {
            loadMarkets(1);
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        adapter.setLocateButtonListener(locateButtonListener);
        adapter.setPageControl(pageControl);
        loadingContainer = rootView.findViewById(R.id.loading_container);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(marketClickListener);
        locationManager.stop();
        locationManager.registerListener(mLocationListener);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        locationManager.unregisterListener(mLocationListener);
    }

    public void startLocate(){
        locationManager.start();
        locationManager.requestLocation(5000);
    }

    @Override
    public void showLoading() {
        loadingContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingContainer.setVisibility(View.INVISIBLE);
    }

    private void loadMarkets(int page) {
        String url = ContextProvider.getBaseURL() + "/api/market/list";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, marketsListener);
        req.addParam("pageSize", 5 + "");
        req.addParam("pageNo", page + "");

        if (currentLocation != null && currentLocation.getLatitude() > 10) {
            req.addParam("lat", currentLocation.getLatitude() + "");
            req.addParam("lng", currentLocation.getLongitude() + "");
        }

        if(currentCity != null){
            req.addParam("area_id",currentCity.getInt("area_id") + "");
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

    private View.OnClickListener locateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(LocationManager.getLastLocation() != null){
                LocationModel model = LocationManager.getLastLocation();
                handleLocation(model);
            }else {
                locationManager.requestLocation(5000);
            }
        }
    };

    private void handleLocation(LocationModel model){
        this.currentLocation = model;
        adapter.setLat(model.getLatitude());
        adapter.setLng(model.getLongitude());
        adapter.setCurrentAddress(model.getAddress());
        Model city = selectMarketViewController.matchCity(model);
        if(city != null){
            currentCity = city;
            adapter.setCity(city);
            loadMarkets(1);
        }else{
            if(model.getCityName() == null){
                Toast.makeText(ContextProvider.getContext(),"定位失败！！",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ContextProvider.getContext(),"您当前所在城市尚未开通！",Toast.LENGTH_LONG).show();
            }
            selectMarketViewController.showSelectCityActionSheet();
        }
    }

    private ModelStatusListener<LocationEvent, LocationModel> mLocationListener = new ModelStatusListener<LocationEvent, LocationModel>() {
        @Override
        public void onModelEvent(LocationEvent event, LocationModel model) {
            switch (event) {
                case OnReceiveLocation:
                    if (model != null) {
                        handleLocation(model);
                    }
                    break;
                case OnTimeOut:
                    Toast.makeText(ContextProvider.getContext(), "定位超时！", Toast.LENGTH_LONG).show();
                    break;
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
}
