package com.tianpingpai.crm.ui;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerClaimAdapter;
import com.tianpingpai.crm.adapter.DirectorAdapter;
import com.tianpingpai.crm.parser.ClaimJSONListParser;
import com.tianpingpai.crm.parser.ClaimListResult;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;

//客户认领
@SuppressLint("InflateParams")
@SuppressWarnings("unused")
@Layout(id = R.layout.view_controller_customer_claim)
public class CustomerClaimViewController extends CrmBaseViewController {

    private boolean isSearchClose ;

    @Binding(id = R.id.type_button)
    private TextView typeButton;
    @Binding(id = R.id.time_button)
    private TextView timeButton;
    @Binding(id = R.id.city_button)
    private TextView cityButton;
    @Binding(id = R.id.director_button)
    private TextView directorButton;

    @Binding(id = R.id.type_image)
    private ImageButton typeImage;
    @Binding(id = R.id.time_image)
    private ImageButton timeImage;
    @Binding(id = R.id.city_image)
    private ImageButton cityImage;
    @Binding(id = R.id.director_image)
    private ImageButton directorImage;

    @Binding(id = R.id.sort_container)
    private View sortContainer;
    @Binding(id = R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;

    private View topv;
    private PopupWindow popupWindow;
    private View typeWindowView;
    private View timeWindowView;
    private View cityWindowView;
    private View directorWindowView;

    private String [] typeArray = {"卖家","买家"};
    private String [] timeArray = {"全部","今日","本周","本月"};
    private ArrayAdapter<String> typeAdapter = null;
    private ArrayAdapter<String> timeAdapter = null;

    private DirectorAdapter directorAdapter = new DirectorAdapter();
    private CustomerClaimAdapter customerClaimAdapter = new CustomerClaimAdapter();

    SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(isSearchClose){
                loadCustomers(1);
            }else{
                search(searchText,1);
            }
        }
    };

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadCustomers(page);
        }
    };

    private ModelAdapter.PageControl<Model> searchPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            search(searchText, page);
        }
    };
    private HttpRequest.ResultListener<ClaimListResult<Model>> customerListener = new HttpRequest.ResultListener<ClaimListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ClaimListResult<Model>> request, ClaimListResult<Model> data) {
            if(request.getAttachment(Boolean.class)== Boolean.TRUE){
                customerClaimAdapter.clear();
            }
            if(data.isSuccess()){
                customerClaimAdapter.setData(data);
                customerClaimAdapter.setClaimNum(data.getNum());
            }
            hideLoading();
            refreshLayout.setRefreshing(false);
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        topv = setActionBarLayout(R.layout.ab_back_title_right);
        TextView rightBtn = (TextView)topv.findViewById(R.id.ab_right_button);
        SearchView searchView = (SearchView)topv.findViewById(R.id.search_view);
        searchView.setQueryHint("请输入电话号码");
        rightBtn.setVisibility(View.GONE);
        setTitle("客户认领");

        typeAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_expandable_list_item_1,typeArray);
        timeAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_expandable_list_item_1,timeArray);

        directorAdapter.setActivity(getActivity());
//        cityAdapter.setActivity(getActivity());
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        final ListView customerListView = (ListView)rootView.findViewById(R.id.customers_claim_list_view);
        customerClaimAdapter.setActivity(getActivity());
        customerClaimAdapter.setPageControl(mPageControl);
        customerListView.setAdapter(customerClaimAdapter);
        loadCustomers(1);

        isSearchClose = true;
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearchClose = true;
                setTitle("客户认领");
                customerClaimAdapter.setPageControl(mPageControl);
                refreshLayoutControl.triggerRefresh();
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("");
                isSearchClose = false;
                customerClaimAdapter.setPageControl(searchPageControl);
            }
        });

        searchView.setOnQueryTextListener(searchQueryListener);
        searchView.setVisibility(View.VISIBLE);

    }

    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String q) {
            searchText = q;
            search(q, 1);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String arg0) {
            return false;
        }
    };
    private String searchText;
    private void search(String key,int page){
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String url = URLApi.getBaseUrl()+"/crm/customer/droped_customers";
        HttpRequest<ClaimListResult<Model>> req = new HttpRequest<>(url,customerListener);
//        JSONListParser parser = new JSONListParser();
        ClaimJSONListParser parser = new ClaimJSONListParser();
        req.setParser(parser);
        req.addParam("user_type", userType + "");
        req.addParam("query_condition", key);
        req.addParam("date_type",dateType+"");
        req.addParam("marketer_id",marketerId+"");
        req.addParam("area_id",areaId+"");
        req.addParam("accessToken", user.getAccessToken());
        if (page == 1) {// 1 means refresh
            req.setAttachment(Boolean.TRUE);
            showLoading();
        }
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private int userType = 1;
    private int dateType = -1;
    private int marketerId = -1;
    private int areaId = -1;

    public void loadCustomers(int page){
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String url = URLApi.getBaseUrl()+"/crm/customer/droped_customers";
        HttpRequest<ClaimListResult<Model>> req = new HttpRequest<>(url,customerListener);
//        JSONListParser parser = new JSONListParser();
        ClaimJSONListParser parser = new ClaimJSONListParser();
        req.setParser(parser);
        req.addParam("pageNo",page+"");
        req.addParam("user_type", userType + "");
        req.addParam("date_type",dateType+"");
        req.addParam("area_id",areaId+"");
        req.addParam("marketer_id",marketerId+"");
        req.addParam("accessToken",user.getAccessToken());
        if (page == 1) {// 1 means refresh
            req.setAttachment(Boolean.TRUE);
            showLoading();
        }
        VolleyDispatcher.getInstance().dispatch(req);
        Log.e("url====",req.getUrl());
    }


    private void showTypePopupWindow(){
        if(typeWindowView == null){
            typeWindowView = getActivity().getLayoutInflater().inflate(R.layout.time_view_sort, null);
            typeWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(typeWindowView);
            ListView listView = (ListView) typeWindowView.findViewById(R.id.sort_condition_list_view);
            listView.setAdapter(typeAdapter);
            listView.setOnItemClickListener(typeSortItemClickListener);
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(typeWindowView);
        popupWindow.showAsDropDown(sortContainer, 0, 2);
    }

    private void showTimePopupWindow() {
        if (timeWindowView == null) {
            timeWindowView = getActivity().getLayoutInflater().inflate(R.layout.time_view_sort, null);
            timeWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(timeWindowView);
            ListView listView = (ListView) timeWindowView.findViewById(R.id.sort_condition_list_view);
            listView.setAdapter(timeAdapter);
            listView.setOnItemClickListener(timeSortItemClickListener);
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(timeWindowView);
        popupWindow.showAsDropDown(sortContainer, 0, 2);
    }
    private void showCityPopupWindow() {
        if (cityWindowView == null) {
            final SelectCountyViewController selectCityViewController = new SelectCountyViewController();
            selectCityViewController.setActivity(getActivity());
            selectCityViewController.createView(getActivity().getLayoutInflater(), null);
            cityWindowView = selectCityViewController.getView();
            cityWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(cityWindowView);
            selectCityViewController.setOnSelectCityListener(new SelectCountyViewController.OnSelectCityListener() {
                @Override
                public void onSelectCity(Model model) {
                    cityButton.setText(model.getString("name"));
                    areaId = model.getInt("area_id");
                    refresh();
                    popupWindow.dismiss();
                }
            });
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(cityWindowView);
        popupWindow.showAsDropDown(sortContainer, 0, 2);
    }

    private void showDirectorPopupWindow() {
        if (directorWindowView == null) {
            directorWindowView = getActivity().getLayoutInflater().inflate(R.layout.time_view_sort, null);
            directorWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(directorWindowView);
            ListView listView = (ListView) directorWindowView.findViewById(R.id.sort_condition_list_view);
            listView.setAdapter(directorAdapter);
//            listView.setAdapter(timeAdapter);
            listView.setOnItemClickListener(directorSortItemClickListener);
            loadDirector();
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(directorWindowView);
        popupWindow.showAsDropDown(sortContainer, 0, 2);
    }

    private View.OnClickListener dismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();

            }
        }
    };

    private PopupWindow.OnDismissListener onPopupWindowDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            typeImage.setBackgroundResource(R.drawable.ad);
            timeImage.setBackgroundResource(R.drawable.ad);
            cityImage.setBackgroundResource(R.drawable.ad);
            directorImage.setBackgroundResource(R.drawable.ad);
        }
    };

    private PopupWindow getPopupWindow(){
        if(popupWindow==null){
            popupWindow = new PopupWindow();
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setOnDismissListener(onPopupWindowDismissListener);
            popupWindow.setWidth(getView().getWidth());//TODO

            popupWindow.setHeight(getView().getHeight()-sortContainer.getHeight()-topv.getHeight());
            return popupWindow;
        }
        return popupWindow;
    }
    @OnClick(R.id.type_button)
    private View.OnClickListener typeOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            showTypePopupWindow();
            typeImage.setBackgroundResource(R.drawable.ar);
        }
    };

    @OnClick(R.id.time_button)
    private View.OnClickListener timeOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            showTimePopupWindow();
            timeImage.setBackgroundResource(R.drawable.ar);
        }
    };
    @OnClick(R.id.city_button)
    private View.OnClickListener cityOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            showCityPopupWindow();
            cityImage.setBackgroundResource(R.drawable.ar);
        }
    };
    @OnClick(R.id.director_button)
    private View.OnClickListener directorOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            showDirectorPopupWindow();
            directorImage.setBackgroundResource(R.drawable.ar);
        }
    };

    private AdapterView.OnItemClickListener typeSortItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            typeButton.setText(typeArray[i]);
            userType = i;
            popupWindow.dismiss();
            refresh();
        }
    };

    private AdapterView.OnItemClickListener timeSortItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            timeButton.setText(timeArray[i]);
            dateType = i-1;
            popupWindow.dismiss();
            refresh();
        }
    };

    private AdapterView.OnItemClickListener directorSortItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            directorButton.setText(directorAdapter.getItem(i).getString("display_name"));
            marketerId = directorAdapter.getItem(i).getInt("marketer_id");
            popupWindow.dismiss();
            refresh();
        }
    };

    private void refresh(){
        loadCustomers(1);
    }

    private void loadDirector(){
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String url = URLApi.getBaseUrl()+"/crm/customer/list_chief";//ListResult<Model>

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,directorlistener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(false);
        req.setParser(parser);
        req.addParam("accessToken", user.getAccessToken());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> directorlistener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.isSuccess()){
                ArrayList<Model> list = new ArrayList<>();
                Model m = new Model();
                m.set("marketer_id",-1);
                m.set("display_name","全部");
                list.add(m);
                list.addAll(data.getModels());
                directorAdapter.setModels(list);
            }
        }
    };
}
