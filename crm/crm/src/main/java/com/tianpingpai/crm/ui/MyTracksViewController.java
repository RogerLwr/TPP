package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.location.LocationEvent;
import com.tianpingpai.location.LocationManager;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.MapInfo;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserModel;

import java.util.ArrayList;
import java.util.List;

@Statistics(page = "我的足迹")
@ActionBar(title = "我的足迹")
@Layout(id = R.layout.ui_my_tracks)
public class MyTracksViewController extends CrmBaseViewController {

    public static final String KEY_LOCATION = "key.location";
    private LocationModel location;
    public static final String KEY_DATE_TYPE = "Key.dateType";
    private String dateType;

    @Binding(id = R.id.name_date_text_view)
    private TextView nameDateTV;
    @Binding(id = R.id.visited_num_text_view)
    private TextView visitedNumTV;
    @Binding(id = R.id.date_foot_text_view)
    private TextView dateFootTV;


    private MapView mMapView;

    RelativeLayout mMarkerInfoLy;

    private void showLocation(LocationModel model,boolean center){
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy((float) model.getAccuracy())
                .direction(0).latitude(model.getLatitude())//TODO direction
                .longitude(model.getLongitude()).build();
        mMapView.getMap().setMyLocationData(locData);
        BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin);
        MyLocationConfiguration conf = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, des);
        mMapView.getMap().setMyLocationConfigeration(conf);

        LocationManager.getInstance().stop();

        if(center){
            LatLng ll = new LatLng(model.getLatitude(),
                    model.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mMapView.getMap().animateMapStatus(u);
        }
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
                getActivity().finish();
                Toast.makeText(ContextProvider.getContext(), "提交成功！", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ContextProvider.getContext(),"" + data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };

//    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if(location == null){
//                Toast.makeText(ContextProvider.getContext(),"没有位置信息，不能提交!",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            String url = ContextProvider.getBaseURL() + "/crm/marketer/sign";
//            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url,submitListener);
//            req.setMethod(HttpRequest.POST);
//            req.addParam("lat", location.getLatitude() + "");
//            req.addParam("lng", location.getLongtitude() + "");
//            req.addParam("position", location.getAddress());
//            req.addParam("phoneType", "android");
//            req.setParser(new ModelParser<>(Void.class));
////            req.setErrorListener(new CommonErrorHandler(MyFootFragment.this));
//            req.setErrorListener(new HttpRequest.ErrorListener() {
//                @Override
//                public void onError(HttpRequest<?> request, HttpError eror) {
//                    hideLoading();
//                    Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            VolleyDispatcher.getInstance().dispatch(req);
//            showLoading();
//        }
//    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        dateType = a.getIntent().getStringExtra(KEY_DATE_TYPE);
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


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        mMapView = (MapView) rootView.findViewById(R.id.map_view);
        mMarkerInfoLy = (RelativeLayout) rootView.findViewById(R.id.id_marker_info);
        mMapView.getMap().setMyLocationEnabled(true);
        mMapView.getMap().setOnMyLocationClickListener(myLocationOnClickListener);
        mMapView.showZoomControls(true);
        if(location == null) {
            LocationManager.getInstance().start();
        }else{
            showLocation(location,true);
        }

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
                .zoomBy(12);
        mMapView.getMap().animateMapStatus(mapStatusUpdate);

        checkLogin();
        UserModel user = UserManager.getInstance().getCurrentUser();
        if(user != null) {
            Log.e("xx", "186----------" + user.toString());
            if(dateType.equals("0")){
                nameDateTV.setText(user.getName() + "你今天");
                dateFootTV.setText("下面是你今天的足迹地图");
            }else if(dateType.equals("1")){
                nameDateTV.setText(user.getName() + "你本周");
                dateFootTV.setText("下面是你本周的足迹地图");
            }else if(dateType.equals("2")){
                nameDateTV.setText(user.getName()+"你本月");
                dateFootTV.setText("下面是你本月的足迹地图");
            }else{
                nameDateTV.setText(user.getName()+"你全部");
                dateFootTV.setText("下面是你全部的足迹地图");
            }
        }

        loadData();
        showContent();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onActivityPaused(Activity a) {
        super.onActivityPaused(a);
        mMapView.onPause();
    }

    private void checkLogin() {
        if (UserManager.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(getActivity(),
                    ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,
                    LoginViewController.class);
            getActivity().startActivity(i);
        }
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
            }
            if(data.isSuccess()){
                ArrayList<Model> models = data.getModels();
                if(models != null){
                    int size = models.size();
                    visitedNumTV.setText("拜访"+ size +"次");
                    Log.e("xx", "183--------models" + models);
                    makeMap(models, size);
                }
            }else{
                Toast.makeText(getActivity(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };


    private void loadData(){

        showLoading();
        String url = URLApi.getBaseUrl() + "/crm/marketer/mileage";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(false);
        req.setParser(parser);
        req.addParam("date_type", dateType);
        VolleyDispatcher.getInstance().dispatch(req);

    }
    BaiduMap mBaiduMap;
    private void makeMap(ArrayList<Model> models, int size){

        List<LatLng> pts = new ArrayList<>();
        mBaiduMap = mMapView.getMap();
        LatLng latLng = new LatLng(114,120);
        for (int i = 0; i < size; i++){
            Model model = models.get(i);

            //定义Maker坐标点
            String lat = model.getString("lat");
            String lng = model.getString("lng");
            if(!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)){


                LatLng point = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                latLng = point;
                MapInfo mapInfo = new MapInfo(Double.parseDouble(lat), Double.parseDouble(lng), R.drawable.ic_150922_mark_pop, model.getString("position"), model.getString("position"), 1456);

                //构建Marker图标
                BitmapDescriptor bitmap;
                if(i==0 || i==size-1){
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_150922_mark);
                    if(i==0){
                        mapInfo.setStratEndInfo("起点: ");
                    }else{
                        mapInfo.setStratEndInfo("终点: ");
                    }
                }else {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_150922_mark);
                }
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                Marker marker = (Marker)mMapView.getMap().addOverlay(option);


                Bundle bundle = new Bundle();
                bundle.putSerializable("info", mapInfo);
                marker.setExtraInfo(bundle);

                pts.add(point);
            }

        }

        if(size >=2 && size<= 1000){
            //构建用户绘制多边形的Option对象
            OverlayOptions polygonOption = new PolylineOptions()
                    .points(pts)
                    .color(Color.argb(255, 13, 95, 243));
//                    .color(R.color.blue_0d);
            //在地图上添加多边形Option，用于显示
            mMapView.getMap().addOverlay(polygonOption);
        }

        // 将地图移到到最后一个经纬度位置
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(u);

        //对Marker的点击
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //获得marker中的数据
                MapInfo info = (MapInfo) marker.getExtraInfo().get("info");

                Log.e("xx", "322----------info=" + info);

                InfoWindow mInfoWindow;
                //生成一个TextView用户在地图中显示InfoWindow
                TextView addrTV = new TextView(ContextProvider.getContext());
                addrTV.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.gray_33));
                addrTV.setBackgroundResource(R.drawable.ic_150922_mark_pop);
                addrTV.setPadding(30, 20, 30, 50);
                addrTV.setText(info.getStratEndInfo() + info.getName());

                BitmapDescriptor bdGround = BitmapDescriptorFactory.fromView(addrTV);

                //将marker所在的经纬度的信息转化成屏幕上的坐标
                final LatLng ll = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                Log.e("xx", "--!" + p.x + " , " + p.y);
                p.y -= 47;
                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                DisplayMetrics metric = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
                float density = metric.density;// 屏幕密度（0.75 / 1.0 / 1.5）
                //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                int y = (int) (-20 * density);
                Log.e("xx", "292--------density=" + density + ", y=" + y);
                //为弹出的InfoWindow添加点击事件
                mInfoWindow = new InfoWindow(bdGround, llInfo, y, new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        //隐藏InfoWindow
                        mBaiduMap.hideInfoWindow();
                    }
                });
                //显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);
                //设置详细信息布局为可见
//                mMarkerInfoLy.setVisibility(View.VISIBLE);
//                根据商家信息为详细信息布局设置信息
//                popupInfo(mMarkerInfoLy, info);
                return true;
            }
        });

        mMapView.getMap().setOnMapClickListener(mapListener);

    }

    /**
     * 根据info为布局上的控件设置信息
     */
    protected void popupInfo(RelativeLayout mMarkerLy, MapInfo info)
    {
        ViewHolder viewHolder = null;
        if (mMarkerLy.getTag() == null)
        {
            viewHolder = new ViewHolder();
            viewHolder.infoImg = (ImageView) mMarkerLy
                    .findViewById(R.id.info_img);
            viewHolder.infoName = (TextView) mMarkerLy
                    .findViewById(R.id.info_name);
            viewHolder.infoDistance = (TextView) mMarkerLy
                    .findViewById(R.id.info_distance);
            viewHolder.infoZan = (TextView) mMarkerLy
                    .findViewById(R.id.info_zan);

            mMarkerLy.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) mMarkerLy.getTag();
        viewHolder.infoImg.setImageResource(info.getImgId());
        viewHolder.infoDistance.setText(info.getDistance());
        viewHolder.infoName.setText(info.getName());
        viewHolder.infoZan.setText(info.getZan() + "");
    }

    private class ViewHolder
    {
        ImageView infoImg;
        TextView infoName;
        TextView infoDistance;
        TextView infoZan;
    }

    BaiduMap.OnMapClickListener mapListener = new BaiduMap.OnMapClickListener() {

        public void onMapClick(LatLng point){
//            //创建InfoWindow展示的view
//            Button button = new Button(ContextProvider.getContext());
//            button.setText(model.getString("position"));
//            button.setBackgroundResource(R.drawable.ic_150922_mark_pop);
//            DisplayMetrics metric = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
//            float density = metric.density;// 屏幕密度（0.75 / 1.0 / 1.5）
//            //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
//            int y = (int) (-60 * density);
//            Log.e("xx", "292--------density="+density+", y="+y);
//            InfoWindow mInfoWindow = new InfoWindow(button, point, y);
//            //显示InfoWindow
//            mMapView.getMap().showInfoWindow(mInfoWindow);
            mMarkerInfoLy.setVisibility(View.GONE);
            mBaiduMap.hideInfoWindow();
        }

        /*
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */

        public boolean onMapPoiClick(MapPoi poi){
            Log.e("xx", "321---------poi="+poi.toString());
            return false;
        }
    };

    private BaiduMap.OnMyLocationClickListener myLocationOnClickListener = new BaiduMap.OnMyLocationClickListener() {
        @Override
        public boolean onMyLocationClick() {
            return false;
        }
    };
}
