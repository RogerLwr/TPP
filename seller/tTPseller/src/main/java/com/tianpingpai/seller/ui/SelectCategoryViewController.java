package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.SelectCategoryAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;
import java.util.List;


@ActionBar(title = "选择类目")
@Layout(id = R.layout.ui_select_category)
public class SelectCategoryViewController extends BaseViewController {

    interface OnSelectCategoryListener{
        void onSelectCategory(Model category);
    }

    public static final String KEY_CATEGORY = "category";

    public void setOnSelectCategoryListener(OnSelectCategoryListener onSelectCategoryListener) {
        this.onSelectCategoryListener = onSelectCategoryListener;
    }

    private OnSelectCategoryListener onSelectCategoryListener;

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    private ActionSheet actionSheet;

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    private int categoryId = -1;
    private Model category;

    private TabLayout categoryTabLayout;
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    private SelectCategoryAdapter adapter = new SelectCategoryAdapter();
    private List<Model> secondCategories;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        category = getActivity().getIntent().getParcelableExtra(KEY_CATEGORY);
        if(category != null){
            categoryId = category.getInt("secondCategoryId");
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setOnRefreshListener(refreshListener);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        ListView categoryListView = (ListView) rootView.findViewById(R.id.categories_list_view);
        categoryListView.setAdapter(adapter);
        categoryListView.setOnItemClickListener(categoryItemClickListener);
        categoryTabLayout = (TabLayout) rootView.findViewById(R.id.category_tab_layout);
        categoryTabLayout.setOnTabSelectedListener(onTabSelectedListener);
        refreshLayoutControl.triggerRefreshDelayed();
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadCategories();
        }
    };

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            selectCategoryAt(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void selectCategoryAt(int index){
        ArrayList<Model> categories = (ArrayList<Model>) secondCategories.get(index).getList("subCategories", Model.class);

        Model parent = secondCategories.get(index);
        for(Model m:categories){
            m.set("_parentId",parent.get("id"));
        }
        adapter.setModels(categories);
//        categoryTabLayout.getTabAt(index).select();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            if(data.isSuccess()){
                ArrayList<Model> categories;
                String orientation = data.getModel().getString("type");
                if("vertical".equals(orientation)) {
                    categories = (ArrayList<Model>) data.getModel().getList("categories", Model.class);
                    for(Model c:categories){
                        List<Model> subCategories = c.getList("subCategories",Model.class);
                        if(subCategories != null){
                            for(Model m:subCategories){
                                m.set("_parentId",c.get("id"));
                            }
                        }
                    }
                }else{
                    categoryTabLayout.setVisibility(View.VISIBLE);
                    secondCategories = data.getModel().getList("categories",Model.class);
                    categoryTabLayout.removeAllTabs();
                    if(secondCategories == null){
                        return;
                    }
                    for(Model m:secondCategories){
                        TabLayout.Tab tab = categoryTabLayout.newTab();

                        List<Model> subCategories = m.getList("subCategories", Model.class);
                        if(subCategories != null){
                            tab.setText(m.getString("name") + "("+ subCategories.size() + ")");
                        }else {
                            tab.setText(m.getString("name") + "(0)");
                        }
                        categoryTabLayout.addTab(tab);
                    }
                    if(secondCategories.size() <= 1){
                        categoryTabLayout.setVisibility(View.GONE);
                    }
                    selectCategoryAt(0);
                    categories = (ArrayList<Model>) data.getModel().getList("categories", Model.class).get(0).getList("subCategories", Model.class);


                }
                adapter.setModels(categories);
            }else{
                ResultHandler.handleError(data, SelectCategoryViewController.this);
            }
        }
    };

    private void loadCategories(){
        String url = ContextProvider.getBaseURL() + "/api/sku/category/getCategoryList";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,listener);
        if(categoryId > 0){
            req.addParam("category_id",String.valueOf(categoryId));
        }
        req.setParser(new GenericModelParser());
        CommonErrorHandler errorHandler = new CommonErrorHandler(this);
        errorHandler.setSwipeRefreshLayout(refreshLayoutControl.getSwipeRefreshLayout());
        req.setErrorListener(errorHandler);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private AdapterView.OnItemClickListener categoryItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Model model = adapter.getItem(position);
            int level = model.getInt("level");
            if(level == 2){
                if(actionSheet != null){
                    SelectCategoryViewController vc = new SelectCategoryViewController();
                    vc.setCategoryId(model.getInt("id"));
                    vc.setActivity(getActivity());
                    vc.setActionSheet(actionSheet);
                    vc.setOnSelectCategoryListener(onSelectCategoryListener);
                    actionSheet.pushViewController(vc);
                }else{
                    Intent intent = new Intent(getActivity(),ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, SelectCategoryViewController.class);
                    if(category == null){
                        category = new Model();
                    }
                    category.set("secondCategoryId",model.getInt("id"));
                    category.set("topCategoryId",model.getInt("_parentId"));
                    Log.e("xx", "model:" + model);
                    intent.putExtra(SelectCategoryViewController.KEY_CATEGORY,category);
                    getActivity().startActivity(intent);
                }
            }
            if(level == 3){
                if(actionSheet == null) {
                    Intent intent = new Intent(getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, EditProductViewController.class);
                    category.set("categoryId", model.getInt("id"));
                    intent.putExtra(EditProductViewController.KEY_CATEGORY, category);
                    intent.putExtra(EditProductViewController.KEY_ADD, true);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }else {
                    Model cm = new Model();
                    cm.set("categoryId",model.getInt("id"));
                    onSelectCategoryListener.onSelectCategory(cm);
                    actionSheet.dismiss();
                }
            }
        }
    };
}
