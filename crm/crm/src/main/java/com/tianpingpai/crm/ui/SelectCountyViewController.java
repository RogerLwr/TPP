package com.tianpingpai.crm.ui;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.widget.CityAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectCountyViewController extends BaseViewController{

    public interface OnSelectCityListener {
        void onSelectCity(Model model);
    }

    {
        setLayoutId(com.tianpingpai.foundation.R.layout.vc_select_city);
    }
    Model all = new Model();

    private OnSelectCityListener onSelectCityListener;
    private ListResult<Model> cityResult;
    private boolean cityOnly;

    private CityAdapter cityAdapter = new CityAdapter();
    private CityAdapter regionAdapter = new CityAdapter();
    private CityAdapter secondLevelRegionAdapter = new CityAdapter();

    public Model getSelectedCity(){
        return cityAdapter.getSelection();
    }

    public Model getSelectedRegion(){
        return regionAdapter.getSelection();
    }

    private View loadingView;
    private View failureView;

    public void setCityResult(ListResult<Model> cities){
        this.cityResult = cities;
        cityAdapter.setData(cities);
        if(cities != null && !cities.getModels().isEmpty()){
            Model firstCity = cities.getModels().get(0);
            cityAdapter.setSelection(firstCity);
            if(firstCity != null){
                selectCity(firstCity);
            }
        }
    }

    public void setOnSelectCityListener(OnSelectCityListener cityListener) {
        this.onSelectCityListener = cityListener;
    }

    public void setCityOnly(boolean cityOnly){
        this.cityOnly = cityOnly;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        loadingView = rootView.findViewById(com.tianpingpai.foundation.R.id.loading_container);
        failureView = rootView.findViewById(com.tianpingpai.foundation.R.id.failure_view);
        failureView.setOnClickListener(failureButtonListener);
        ListView cityListView = (ListView) rootView.findViewById(com.tianpingpai.foundation.R.id.city_list_view);
        cityListView.setAdapter(cityAdapter);
        cityListView.setOnItemClickListener(cityListItemClickListener);
        hideActionBar();
        ListView regionListView = (ListView) rootView.findViewById(com.tianpingpai.foundation.R.id.region_list_view);
        regionListView.setOnItemClickListener(regionListItemClickListener);
        ListView secondLevelListView = (ListView) rootView.findViewById(com.tianpingpai.foundation.R.id.second_level_regions_list_view);
        if(cityOnly){
            regionListView.setVisibility(View.GONE);
            secondLevelListView.setVisibility(View.GONE);
            setTitle("选择城市");
        }else{
            setTitle("选择区域");
        }
        regionListView.setAdapter(regionAdapter);

        secondLevelListView.setAdapter(secondLevelRegionAdapter);
        secondLevelListView.setOnItemClickListener(secondLevelRegionListItemClickListener);

        cityAdapter.setSelectionMode(!cityOnly);
        regionAdapter.setSelectionMode(true);
        secondLevelRegionAdapter.setSelectionMode(true);
        all.set("name","全部");
        all.set("area_id",-1);
        if(cityResult == null) {
            loadData();
        }else{
            cityAdapter.setData(cityResult);
        }
    }

    private void selectCity(Model city){
        cityAdapter.setSelection(city);
        List<Model> areas = city.getList("childAreas", Model.class);
        regionAdapter.setModels((ArrayList<Model>) areas);
        if(areas != null && areas.size() > 0){
            regionAdapter.setSelection(areas.get(0));
            selectRegion(areas.get(0));
        }
    }

    private void selectRegion(Model region){
        List<Model> areas = region.getList("childAreas", Model.class);
        Model allModel = new Model();
        allModel.set("name",region.getString("name")+"全部");
        allModel.set("area_id", region.getInt("area_id"));
        ArrayList<Model> allAreas = new ArrayList<>();
        allAreas.add(allModel);
        allAreas.addAll(areas);
        secondLevelRegionAdapter.setModels(allAreas);
        if(areas != null && areas.size() > 0){
            secondLevelRegionAdapter.setSelection(null);
        }
    }

    private void loadData() {
        String url = ContextProvider.getBaseURL() + "/api/area/getAll.json";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, addressesListener);
        JSONListParser parser = new JSONListParser();

        parser.setPaged(false);
        req.setParser(parser);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                loadingView.setVisibility(View.GONE);
                failureView.setVisibility(View.VISIBLE);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        Log.e("url==",req.getUrl());
        loadingView.setVisibility(View.VISIBLE);
        failureView.setVisibility(View.GONE);
    }

    private HttpRequest.ResultListener<ListResult<Model>> addressesListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            loadingView.setVisibility(View.GONE);
            if (!data.isSuccess()) {
                failureView.setVisibility(View.VISIBLE);
            } else {
                ArrayList<Model> list = new ArrayList<>();
                list.add(all);
                list.addAll(data.getModels());
                data.setModels(list);
                setCityResult(data);
            }
        }
    };

    private View.OnClickListener failureButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loadData();
        }
    };

    private AdapterView.OnItemClickListener cityListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position==0){
                onSelectCityListener.onSelectCity(all);
                regionAdapter.clear();
                secondLevelRegionAdapter.clear();
            }
            if(cityOnly){
                if (onSelectCityListener != null) {
                    onSelectCityListener.onSelectCity(cityAdapter.getItem(position));
                }
//                actionSheet.dismiss();
            }else{
                Model city = cityAdapter.getItem(position);
                selectCity(city);
            }
        }
    };

    private AdapterView.OnItemClickListener regionListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Model city = regionAdapter.getItem(position);
            regionAdapter.setSelection(city);
            selectRegion(city);
        }
    };

    private AdapterView.OnItemClickListener secondLevelRegionListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Model region = secondLevelRegionAdapter.getItem(position);
            secondLevelRegionAdapter.setSelection(region);
            onSelectCityListener.onSelectCity(secondLevelRegionAdapter.getSelection());

        }
    };
}
