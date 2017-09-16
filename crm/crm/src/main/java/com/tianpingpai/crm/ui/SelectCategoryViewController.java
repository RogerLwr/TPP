package com.tianpingpai.crm.ui;


import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.SelectCategoryAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CategoryInfo;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class SelectCategoryViewController extends BaseViewController{

    private String title = "请选择主营品类";
    private boolean isOnly = true;

    public void setTitle(String title){
        this.title = title;
    }

    public void setOnly(boolean isOnly){
        this.isOnly = isOnly;
    }

    private HashMap<Integer ,String> selections = new HashMap<>();
    private ArrayList<Integer> selected;
    private ActionSheet actionSheet;

    {
        setLayoutId(R.layout.vc_select_category);
    }

    private SelectCategoryAdapter categoryAdapter ;

    public void setSelectedCategory(ArrayList<Integer> selected){
        this.selected = selected;
    }
    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    public interface OnSelectCategoryListener {

        void onSelectCategory(HashMap<Integer ,String> selections);
    }


    private OnSelectCategoryListener onSelectCategoryListener;
    public void setOnSelectCategoryListener(OnSelectCategoryListener onSelectCategoryListener) {
        this.onSelectCategoryListener = onSelectCategoryListener;
    }

    private View loadingView;
    private View failureView;
    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        ListView categoryContainer = (ListView) rootView.findViewById(R.id.category_lv_container);

        View actionBar = setActionBarLayout(com.tianpingpai.foundation.R.layout.ab_select_city);
        loadingView = rootView.findViewById(com.tianpingpai.foundation.R.id.loading_container);
        failureView = rootView.findViewById(com.tianpingpai.foundation.R.id.failure_view);
        failureView.setOnClickListener(failureButtonListener);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_close_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setEnabled(true);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_close_button).setOnClickListener(closeButtonListener);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setOnClickListener(okayButtonListener);
        setTitle(title);
        categoryAdapter = new SelectCategoryAdapter();
        categoryAdapter.setOnly(isOnly);
        categoryAdapter.setSelected(selected);
        categoryContainer.setAdapter(categoryAdapter);
        getCategoryList();

    }

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
            SelectCategoryViewController.this.selections = categoryAdapter.getSelection();

            if(onSelectCategoryListener != null){
                onSelectCategoryListener.onSelectCategory(categoryAdapter.getSelection());
            }
        }
    };

    private void getCategoryList(){
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.PROD_FIRST_DATA_LIST, categoryListener);
        JSONListParser parser = new JSONListParser();
        req.setParser(parser);

        CommonErrorHandler handler = new CommonErrorHandler(this);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                loadingView.setVisibility(View.GONE);
                failureView.setVisibility(View.VISIBLE);
//                hideLoading();
                showNetworkError();
            }
        });

        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());

        VolleyDispatcher.getInstance().dispatch(req);
        loadingView.setVisibility(View.VISIBLE);
        failureView.setVisibility(View.GONE);
//        showLoading();
    }

    private HttpRequest.ResultListener<ListResult<Model>> categoryListener = new HttpRequest.ResultListener<ListResult<Model>>(){
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            loadingView.setVisibility(View.GONE);
            if(data.isSuccess()){
                Log.e("data",data.getModels().toString());
                categoryAdapter.setData(data.getModels());
                categoryAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(getActivity(),data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener failureButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getCategoryList();
        }
    };


}
