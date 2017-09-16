package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.SignInAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "签到列表")
@Layout(id = R.layout.fragment_sign_in_list)
public class SignInListViewController extends CrmBaseViewController{

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SignInAdapter signInAdapter = new SignInAdapter();

    private SwipeRefreshLayout refreshLayout;

    public static final int signInRequestCode = 110;

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SignInViewController.class);
            Model model = signInAdapter.getItem(i);
            LocationModel location = new LocationModel();
            location.setLatitude(Double.parseDouble(model.getString("lat")));
            location.setLongitude(Double.parseDouble(model.getString("lng")));
            location.setAddress(model.getString("position"));

            intent.putExtra(SignInViewController.KEY_LOCATION, location);
            getActivity().startActivityForResult(intent, signInRequestCode);
        }
    };

    @Override
    public void onActivityResult(Activity a,int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a,requestCode, resultCode, data);
        Log.e("xx", "55-------requestCode=" + requestCode + ", resultCode=" + resultCode);
        if(requestCode == signInRequestCode && resultCode == Activity.RESULT_OK){
            loadData(1);
        }
    }


    public void configureActionBar() {
        View actionBarView = setActionBarLayout(R.layout.ab_back_title_right_sign_in);
        ImageView rightButton = (ImageView) actionBarView.findViewById(R.id.ab_right_button);
        setTitle("签到列表");
        rightButton.setOnClickListener(signInButtonListener);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        configureActionBar();
        ListView signInListView = (ListView) rootView.findViewById(R.id.sign_in_list_view);
        signInListView.setAdapter(signInAdapter);
        signInListView.setOnItemClickListener(itemClickListener);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(true);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefresh();
        showContent();
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(1);
        }
    };

    private void loadData(int page) {
        showLoading();
        String url = ContextProvider.getBaseURL() + "/crm/marketer/signList";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("pageNo", page + "");
        req.addParam("pageSize", "20");
        req.setAttachment(page);
        req.setParser(new JSONListParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
            }
            if (request.getAttachment(Integer.class) == 1) {
                signInAdapter.clear();
            }
            signInAdapter.setData(data);
            showContent();
            refreshLayout.setRefreshing(false);
            hideLoading();
        }
    };

    private View.OnClickListener signInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SignInViewController.class);
            getActivity().startActivityForResult(intent, signInRequestCode);
        }
    };
}
