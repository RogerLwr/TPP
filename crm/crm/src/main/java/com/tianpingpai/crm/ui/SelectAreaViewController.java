package com.tianpingpai.crm.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.SelectDistrictAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.widget.CityAdapter;
import com.umeng.fb.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
@SuppressWarnings("unused")
public class SelectAreaViewController extends BaseViewController {

    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }

    private String titleString = "选择配送商圈";


    private HashMap<Integer ,String> selections = new HashMap<>();
    private ArrayList<Integer> selected;
    private ActionSheet actionSheet;

    private CityAdapter cityAdapter;
    private SelectDistrictAdapter districtAdapter;

    public Model getSelectedCity(){
        return cityAdapter.getSelection();
    }

    public OnSelectAreaListener onSelectAreaListener;

    {
        setLayoutId(R.layout.vc_select_area);
    }

    private boolean isOnly;

    public void setIsOnly(boolean isOnly){
        this.isOnly = isOnly;
    }

    public void setSelectedId(ArrayList<Integer> selected){
        this.selected = selected;
    }
    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    public interface OnSelectAreaListener {

        void onSelectArea(HashMap<Integer ,String> selections);
    }

    public void setOnSelectAreaListener(OnSelectAreaListener areaListener) {
        this.onSelectAreaListener = areaListener;
    }

    private View loadingView;
    private View failureView;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        ListView provincialLvContainer = (ListView) rootView.findViewById(R.id.provincial_lv_container);
        ListView districtLvContainer = (ListView) rootView.findViewById(R.id.district_lv_container);

        View actionBar = setActionBarLayout(com.tianpingpai.foundation.R.layout.ab_select_city);
        loadingView = rootView.findViewById(com.tianpingpai.foundation.R.id.loading_container);
        failureView = rootView.findViewById(com.tianpingpai.foundation.R.id.failure_view);
        failureView.setOnClickListener(failureButtonListener);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_close_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setEnabled(true);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_close_button).setOnClickListener(closeButtonListener);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setOnClickListener(okayButtonListener);
        setTitle(titleString);
        cityAdapter = new CityAdapter();
        cityAdapter.setSelectionMode(true);
        provincialLvContainer.setAdapter(cityAdapter);
        provincialLvContainer.setOnItemClickListener(cityListItemClickListener);
        districtAdapter = new SelectDistrictAdapter();
        districtAdapter.setOnly(isOnly);
        districtAdapter.setSelected(selected);
        districtLvContainer.setAdapter(districtAdapter);
        getAddressList();

    }

    @Override
    protected void onReloadData() {
        super.onReloadData();
        getAddressList();
    }

    private AdapterView.OnItemClickListener cityListItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//            int i =cityAdapter.getSelection().get("area_id",Integer.class);
            int i = cityAdapter.getItem(position).get("area_id",Integer.class);
            Log.e("--------------",cityAdapter.getItem(position).toString());
            cityAdapter.setSelection(cityAdapter.getItem(position));
            districtAdapter.notifyDataSetChanged();
            districtAdapter.getSelection().clear();
            loadDistrictData(i);
        }
    };

    private View.OnClickListener failureButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getAddressList();
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
            SelectAreaViewController.this.selections = districtAdapter.getSelection();

            if(onSelectAreaListener != null){
                onSelectAreaListener.onSelectArea(districtAdapter.getSelection());
            }
        }
    };

    private void getAddressList(){

        String url = ContextProvider.getBaseURL() + "/api/area/getAreaDataList.json";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, addressesListener);
        JSONListParser parser = new JSONListParser();

        parser.setPaged(false);
        req.setParser(parser);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                loadingView.setVisibility(View.GONE);
                failureView.setVisibility(View.VISIBLE);
//                hideLoading();
                showNetworkError();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        loadingView.setVisibility(View.VISIBLE);
        failureView.setVisibility(View.GONE);
//        showLoading();

    }
    private HttpRequest.ResultListener<ListResult<Model>> addressesListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            loadingView.setVisibility(View.GONE);
//            hideLoading();
            if(data.isSuccess()){
                setCityResult(data);
            }
        }
    };

    public void setCityResult(ListResult<Model> cities){
        cityAdapter.clear();
        cityAdapter.setData(cities);
        if(cities != null && !cities.getModels().isEmpty()){
            Model firstCity = cities.getModels().get(0);
            cityAdapter.setSelection(firstCity);
            loadDistrictData(cityAdapter.getSelection().getInt("area_id"));
        }
    }

    public void loadDistrictData(int id){

        String url = ContextProvider.getBaseURL() + "/api/market/list";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, districtListener);
        JSONListParser parser = new JSONListParser();

        parser.setPaged(true);
        req.addParam("area_id",id+"");
        req.addParam("pageSize",""+1000);
        req.setParser(parser);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
//                hideLoading();
                showNetworkError();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> districtListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if (data.isSuccess()) {
                setDistrictResult(data);
            }
        }
    };

    public void setDistrictResult(ListResult<Model> distric){

        districtAdapter.setData(distric.getModels());
        districtAdapter.notifyDataSetChanged();
    }
}
