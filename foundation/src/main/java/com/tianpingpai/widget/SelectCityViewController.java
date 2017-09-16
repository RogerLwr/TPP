package com.tianpingpai.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;

import java.util.ArrayList;
import java.util.List;

public class SelectCityViewController extends BaseViewController {

    public interface OnSelectCityListener {
        void onSelectCity(Model model);
    }

    {
        setLayoutId(R.layout.vc_select_city);
    }

    private OnSelectCityListener onSelectCityListener;
    private ActionSheet actionSheet;
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

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        loadingView = rootView.findViewById(R.id.loading_container);
        failureView = rootView.findViewById(R.id.failure_view);
        failureView.setOnClickListener(failureButtonListener);
        ListView cityListView = (ListView) rootView.findViewById(R.id.city_list_view);
        cityListView.setAdapter(cityAdapter);
        cityListView.setOnItemClickListener(cityListItemClickListener);
        View actionBar = setActionBarLayout(R.layout.ab_select_city);
        actionBar.findViewById(R.id.ab_close_button).setOnClickListener(closeButtonListener);
        actionBar.findViewById(R.id.ab_right_button).setOnClickListener(okayButtonListener);
        ListView regionListView = (ListView) rootView.findViewById(R.id.region_list_view);
        regionListView.setOnItemClickListener(regionListItemClickListener);
        ListView secondLevelListView = (ListView) rootView.findViewById(R.id.second_level_regions_list_view);
        if(cityOnly){
            regionListView.setVisibility(View.GONE);
            secondLevelListView.setVisibility(View.GONE);
            setTitle("选择城市");
        }else{
            setTitle("选择区域");
            actionBar.findViewById(R.id.ab_right_button).setVisibility(View.VISIBLE);
            actionBar.findViewById(R.id.ab_right_button).setEnabled(true);
        }
        regionListView.setAdapter(regionAdapter);

        secondLevelListView.setAdapter(secondLevelRegionAdapter);
        secondLevelListView.setOnItemClickListener(secondLevelRegionListItemClickListener);

        cityAdapter.setSelectionMode(!cityOnly);
        regionAdapter.setSelectionMode(true);
        secondLevelRegionAdapter.setSelectionMode(true);
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
        secondLevelRegionAdapter.setModels((ArrayList<Model>) areas);
        if(areas != null && areas.size() > 0){
            secondLevelRegionAdapter.setSelection(areas.get(0));
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
            if(cityOnly){
                if (onSelectCityListener != null) {
                    onSelectCityListener.onSelectCity(cityAdapter.getItem(position));
                }
                actionSheet.dismiss();
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
        }
    };

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };

    private View.OnClickListener okayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
            if(cityAdapter.getCount() == 0){
                return;
            }
            if(onSelectCityListener != null){
                onSelectCityListener.onSelectCity(secondLevelRegionAdapter.getSelection());
            }
        }
    };
}
