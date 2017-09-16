package com.tianpingpai.location;

import android.os.Handler;
import android.os.Looper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelManager;

public class LocationManager extends ModelManager<LocationEvent,LocationModel> {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LocationClient mLocationClient = new LocationClient(ContextProvider.getContext());
    private final BDLocationListener locationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LocationModel locationModel = fromBDLocation(bdLocation);
            mHandler.removeCallbacks(timeOutRun);
            lastLocation = locationModel;
            notifyEvent(LocationEvent.OnReceiveLocation, locationModel);
        }
    };

    private static LocationModel lastLocation;
    private boolean debug = false;

    {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);// 地图定位精度
        option.setCoorType("bd09ll");// 地图坐标系
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(locationListener);
        mLocationClient.start();//TODO
    }

    public void start(){
        mLocationClient.start();
    }

    public void stop(){
        mLocationClient.stop();
    }

    public void requestLocation(long timeout){
        mLocationClient.requestLocation();
        mHandler.postDelayed(timeOutRun,timeout);
    }

    public static LocationModel getLastLocation(){
        return lastLocation;
    }

    private Runnable timeOutRun = new Runnable() {
        @Override
        public void run() {
            mLocationClient.stop();
            notifyEvent(LocationEvent.OnTimeOut,null);
        }
    };

    private LocationModel fromBDLocation(BDLocation location){
        LocationModel locationModel = new LocationModel();
        locationModel.setLatitude(location.getLatitude());
        locationModel.setLongitude(location.getLongitude());
        locationModel.setCityName(location.getCity());
        locationModel.setAddress(location.getAddrStr());

        if(debug){
            locationModel.setLongitude(117.20);
            locationModel.setLatitude(39.13);
            locationModel.setCityName("天津市");
            locationModel.setCityName("天津市-塘沽滴");
        }
        return locationModel;
    }
}
