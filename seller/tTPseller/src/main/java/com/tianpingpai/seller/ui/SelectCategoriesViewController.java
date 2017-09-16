package com.tianpingpai.seller.ui;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.seller.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.seller.adapter.FirstCategorySelectionAdapter;
import com.tianpingpai.seller.tools.TLog;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;

import java.util.ArrayList;

@Layout(id = R.layout.ui_select_categories)
public class SelectCategoriesViewController extends BaseViewController {

    public String getNameString() {
        ArrayList<Model> selection = adapter.getSelection();
        String str = "";
        for(int i = 0;i < selection.size();i++){
            Model m = selection.get(i);
            str += m.getString("name");
            if(i != selection.size() - 1){
                str += ",";
            }
        }
        return str;
    }

    public String getIdString(){
        ArrayList<Model> selection = adapter.getSelection();
        String str = "";
        for(int i = 0;i < selection.size();i++){
            Model m = selection.get(i);
            str += m.getInt("category_id") + "";
            if(i != selection.size() - 1){
                str += ",";
            }
        }
        TLog.e("选择经营品类", "49------idString="+str);
        return str;
    }

    public interface OnSelectedListener{
        void onSelected();
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    private OnSelectedListener onSelectedListener;
    private FirstCategorySelectionAdapter adapter = new FirstCategorySelectionAdapter();
    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet as = (ActionSheet) getViewTransitionManager();
            as.dismiss();
        }
    };

    private View.OnClickListener okayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onSelectedListener.onSelected();
            ActionSheet as = (ActionSheet) getViewTransitionManager();
            as.dismiss();
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        View abView = setActionBarLayout(R.layout.ab_title_green);
        Toolbar toolbar = (Toolbar) abView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(backButtonListener);
        setTitle("选择经营品类");
        ListView categoriesListView = (ListView) rootView.findViewById(R.id.categories_list_view);
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(onItemClickListener);
        rootView.findViewById(R.id.okay_button).setOnClickListener(okayButtonListener);
        if(adapter.shouldLoad()) {
            loadCategories();
        }
    }

    private void loadCategories() {
        showLoading();
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.PROD_FIRST_DATA_LIST, categoriesListener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(true);
        req.setParser(parser);

        VolleyDispatcher.getInstance().dispatch(req);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.toggleSelection(position);
        }
    };

    public void select(int id){
        adapter.select(id);
    }

    private HttpRequest.ResultListener<ListResult<Model>> categoriesListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if (data.isSuccess()) {
                adapter.clear();
                adapter.setData(data);
                hideLoading();
            }
        }
    };
}
