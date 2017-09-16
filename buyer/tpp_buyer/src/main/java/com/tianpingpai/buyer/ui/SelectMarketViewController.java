package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.model.AddressModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.location.LocationEvent;
import com.tianpingpai.location.LocationManager;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ViewControllerAdapter;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SelectCityViewController;

import java.util.ArrayList;

@ActionBar(layout = R.layout.ab_title_green,title = "选择商圈")
@Layout(id = R.layout.vc_select_market)
public class SelectMarketViewController extends BaseViewController {

    private CurrentLocationMarketViewController currentLocationMarketViewController = new CurrentLocationMarketViewController();
    private AddressMarketViewController addressMarketViewController = new AddressMarketViewController();

    private LocationManager locationManager = new LocationManager();
    private ViewControllerAdapter adapter;
    private MarketModel currentMarket;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        currentMarket = MarketManager.getInstance().getCurrentMarket();
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        adapter = new ViewControllerAdapter();
        ArrayList<BaseViewController> viewControllers = new ArrayList<>();
        viewControllers.add(currentLocationMarketViewController);
        viewControllers.add(addressMarketViewController);
        adapter.setTitles(new String[]{"按当前位置", "按收货地址"});
        for (BaseViewController vc : viewControllers) {
            vc.setActivity(getActivity());
        }
        adapter.setViewControllers(viewControllers);
        viewPager.setAdapter(adapter);
        currentLocationMarketViewController.setSelectMarketViewController(this);
        addressMarketViewController.setSelectMarketViewController(this);

        tabLayout.setupWithViewPager(viewPager);
        loadCities();
        locationManager.registerListener(locationListener);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        locationManager.unregisterListener(locationListener);
        adapter.destroyView();
    }

    private ModelStatusListener<LocationEvent, LocationModel> locationListener = new ModelStatusListener<LocationEvent, LocationModel>() {
        @Override
        public void onModelEvent(LocationEvent event, LocationModel model) {
            switch (event) {
                case OnReceiveLocation:
                    handleCity();
                    break;
                case OnTimeOut:
                    Toast.makeText(ContextProvider.getContext(), "获取位置失败", Toast.LENGTH_LONG).show();
                    showSelectCityActionSheet();
                    break;
            }

        }
    };

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            AddressModel addressModel = (AddressModel) data.getSerializableExtra(AddressListViewController.KEY_ADDRESS_MODEL);
            addressMarketViewController.setAddress(addressModel);
        }
    }

    private void loadCities() {
        String url = URLUtil.ADDRESS_URL;
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, addressesListener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(false);
        req.setParser(parser);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
//                showSelectCityActionSheet();
                //TODO toast
                Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private ListResult<Model> cities;
    private HttpRequest.ResultListener<ListResult<Model>> addressesListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            hideLoading();
            if (data.isSuccess()) {
                cities = data;
                handleCity();
                currentLocationMarketViewController.startLocate();
            } else {
                showSelectCityActionSheet();
            }
        }
    };

    private void handleCity() {
        LocationModel lastLocation = LocationManager.getLastLocation();
        if (lastLocation != null) {
            if (cities != null) {
                for (Model city : cities.getModels()) {
                    if (lastLocation.getCityName() != null) {
                        if (lastLocation.getCityName().equals(city.getString("name"))) {
                            setCity(city, false);
                        }
                    }
                }
            }
        } else {
            locationManager.requestLocation(5000);
        }
    }

    public Model matchCity(LocationModel location) {
        if (location != null && cities != null) {
            for (Model city : cities.getModels()) {
                if (location.getCityName() == null) {
                    return null;
                }
                if (location.getCityName().equals(city.getString("name"))) {
                    return city;
                }
            }
        }
        return null;
    }

    private void setCity(Model city, boolean load) {
        currentLocationMarketViewController.setCity(city, load);
    }

    public void showSelectCityActionSheet() {
        ActionSheet actionSheet = getActionSheet(true);
        actionSheet.setHeight(DimensionUtil.dip2px(300));
        SelectCityViewController selectCityViewController = new SelectCityViewController();
        selectCityViewController.setActivity(getActivity());
        selectCityViewController.setCityOnly(true);
        selectCityViewController.setActionSheet(actionSheet);
        selectCityViewController.setCityResult(cities);
        actionSheet.setViewController(selectCityViewController);
        selectCityViewController.setOnSelectCityListener(new SelectCityViewController.OnSelectCityListener() {
            @Override
            public void onSelectCity(Model model) {
                setCity(model, true);
            }
        });
        actionSheet.show();
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        if(super.onBackKeyDown(a)){
            return true;
        }
        if (MarketManager.getInstance().getCurrentMarket() != null && currentMarket == null) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
            getActivity().startActivity(intent);
        }
        getActivity().finish();
        return true;
    }

    public void selectMarket(MarketModel marketModel) {
        MarketManager.getInstance().setCurrentMarket(marketModel);
        if (currentMarket == null) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
            getActivity().startActivity(intent);
        }
        getActivity().finish();
    }
}
