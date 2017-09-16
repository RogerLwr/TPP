package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.location.LocationEvent;
import com.tianpingpai.location.LocationManager;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@Layout(id = R.layout.fragment_signin)
@Statistics(page = "签到")
public class SignInViewController extends BaseViewController {

    public static final String KEY_LOCATION = "key.location";
    private LocationModel location;

    private MapView mapView;

    private void showLocation(LocationModel model,boolean center){
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy((float) model.getAccuracy())
                .direction(0).latitude(model.getLatitude())//TODO direction
                .longitude(model.getLongitude()).build();
        mapView.getMap().setMyLocationData(locData);
        BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin);
        MyLocationConfiguration conf = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, des);
        mapView.getMap().setMyLocationConfigeration(conf);

        LocationManager.getInstance().stop();

        if(center){
            LatLng ll = new LatLng(model.getLatitude(),
                    model.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mapView.getMap().animateMapStatus(u);
        }
        Toast.makeText(getActivity(),model.getAddress(),Toast.LENGTH_LONG).show();
    }

    private ModelStatusListener<LocationEvent, LocationModel> locationListener = new ModelStatusListener<LocationEvent, LocationModel>() {
        @Override
        public void onModelEvent(LocationEvent event, LocationModel model) {
            showLocation(model,location == null);
            location = model;
        }
    };

    public HttpRequest.ResultListener<ModelResult<Void>> submitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            hideLoading();
            if(data.isSuccess()){
                Toast.makeText(ContextProvider.getContext(), "提交成功！", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }else{
                Toast.makeText(ContextProvider.getContext(),"" + data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(location == null ){
                Toast.makeText(ContextProvider.getContext(),"没有位置信息，不能提交!",Toast.LENGTH_SHORT).show();
                return;
            }

            if(!location.isValid()){
                Toast.makeText(ContextProvider.getContext(),"无效的位置信息，不能提交!",Toast.LENGTH_SHORT).show();
                return;
            }

            String url = ContextProvider.getBaseURL() + "/crm/marketer/sign";
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url,submitListener);
            req.setMethod(HttpRequest.POST);
            req.addParam("lat", location.getLatitude() + "");
            req.addParam("lng", location.getLongitude() + "");
            req.addParam("position", location.getAddress());
            Log.e("position-------",location.getAddress());
            req.addParam("phoneType", "android");
            req.setParser(new ModelParser<>(Void.class));
            req.setErrorListener(new CommonErrorHandler(SignInViewController.this));
            VolleyDispatcher.getInstance().dispatch(req);
            showLoading();
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        location = (LocationModel) a.getIntent().getSerializableExtra(KEY_LOCATION);
        LocationManager.getInstance();
        if(location == null){
            LocationManager.getInstance().registerListener(locationListener);
        }
    }

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        LocationManager.getInstance().unregisterListener(locationListener);
        LocationManager.getInstance().stop();
    }

    private void configureActionBar(){
        View actionBar = setActionBarLayout(R.layout.ab_back_title_right);
        TextView rightButton = (TextView) actionBar.findViewById(R.id.ab_right_button);
        rightButton.setText("提交");
        rightButton.setOnClickListener(submitButtonListener);
        if(location != null){
            rightButton.setVisibility(View.GONE);
            setTitle("查看地址");
        }else{
            setTitle("签到");
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        configureActionBar();
        mapView = (MapView) rootView.findViewById(R.id.map_view);
        mapView.getMap().setMyLocationEnabled(true);
        mapView.getMap().setOnMyLocationClickListener(myLocationOnClickListener);
        mapView.showZoomControls(true);
        if(location == null) {
            LocationManager.getInstance().start();
        }else{
            showLocation(location,true);
        }

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
                .zoomBy(12);
        mapView.getMap().animateMapStatus(mapStatusUpdate);
        showContent();
    }

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        mapView.onResume();
    }

    @Override
    public void onActivityPaused(Activity a) {
        super.onActivityPaused(a);
        mapView.onPause();
    }

    private BaiduMap.OnMyLocationClickListener myLocationOnClickListener = new BaiduMap.OnMyLocationClickListener() {
        @Override
        public boolean onMyLocationClick() {
            return false;
        }
    };
}
