package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.manager.ProductManager;
import com.tianpingpai.manager.SearchHistoryManager;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SearchHistoryModel;
import com.tianpingpai.seller.adapter.ProductAdapter;
import com.tianpingpai.seller.adapter.SearchHistoryProdAdapter;
import com.tianpingpai.seller.adapter.SearchProdAdapter;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.DimensionUtil;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Layout(id = R.layout.ui_search)
@Statistics(page = "商品搜索")
public class SearchViewController extends BaseViewController {

    static ArrayList<ProductModel> searchList;
    private View searchButton;

    @Binding(id = R.id.layout_search_history)
    private RelativeLayout searchHistoryLayout;
    @Binding(id = R.id.search_history_list_view)
    private ListView searchHistoryListView;
    @Binding(id = R.id.clear_search_history_tv)
    private TextView clearSearchHistoryTV;

    @OnClick(R.id.clear_search_history_tv)
    private OnClickListener clearSearchHistoryClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            ActionSheet actionSheet = new ActionSheet();
            actionSheet.setActivity(getActivity());
            actionSheet.setHeight(DimensionUtil.dip2px(300));
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActionSheet(actionSheet);
            dialog.setTitle("您确定要删除历史记录吗?");
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchHistoryManager.getInstance().mSearchHistoryDao.clear();
                    searchHistoryAdapter.setModels(SearchHistoryManager.getInstance().getMarkets());
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    public static void setSearchList(ArrayList<ProductModel> s) {
        searchList = s;
    }

    private ArrayList<ProductModel> products;

    private ProductAdapter inAppSearchAdapter = new ProductAdapter();
    private SearchProdAdapter inAppAutoSearchAdapter = new SearchProdAdapter();
    private SearchHistoryProdAdapter searchHistoryAdapter = new SearchHistoryProdAdapter();
    private EditText keywordEditText;
    private ListView productListView;
    private ListView autoSearchListView;


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        View actionBarView = setActionBarLayout(R.layout.ab_search);
        keywordEditText = (EditText) actionBarView
                .findViewById(R.id.keyword_edit_text);

        searchButton = actionBarView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(
                searchButtonListener);

        keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    searchButton.performClick();
                }
                return false;
            }
        });
        productListView = (ListView) rootView
                .findViewById(R.id.product_list_view);
        autoSearchListView = (ListView) rootView.findViewById(R.id.auto_search_list_view);

        products = searchList;
        searchList = null;
        if (products == null) {
            getActivity().finish();
        }
        keywordEditText.addTextChangedListener(watcher);
        keywordEditText.setOnFocusChangeListener(onFoucusChangeListener);
        productListView.setOnItemClickListener(searchItemOnClickListener);
        productListView.setAdapter(inAppSearchAdapter);
        inAppSearchAdapter.setActivity(getActivity());

        autoSearchListView.setOnItemClickListener(autoSearchItemOnClickListener);
        autoSearchListView.setAdapter(inAppAutoSearchAdapter);
        inAppAutoSearchAdapter.setActivity(getActivity());

        searchHistoryListView.setOnItemClickListener(searchHistoryItemOnClickListener);
        searchHistoryListView.setAdapter(searchHistoryAdapter);
        searchHistoryAdapter.setActivity(getActivity());

        configureActionBar(rootView);
        ProductManager.getInstance().registerListener(productStatusListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProductManager.getInstance().unregisterListener(productStatusListener);
    }

    private View.OnFocusChangeListener onFoucusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b){
                Log.e("xx", "151------获得焦点");
                String queryString = keywordEditText.getText().toString();
                if (TextUtils.isEmpty(queryString)) {
                    // 调数据库历史搜索
                    ArrayList<SearchHistoryModel> searchHistoryModels = SearchHistoryManager.getInstance().getMarkets();

                    Log.e("xx", "159------" + searchHistoryModels);
                    if(searchHistoryModels.size() > 0){
                        searchHistoryAdapter.setModels(searchHistoryModels);
                        autoSearchListView.setVisibility(View.INVISIBLE);
                        productListView.setVisibility(View.INVISIBLE);
                        searchHistoryLayout.setVisibility(View.VISIBLE);
                        searchHistoryAdapter.notifyDataSetChanged();
                    }

                }
            }
        }
    };

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.e("xx", "148------" + charSequence);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    searchInApp();
            String queryString = keywordEditText.getText().toString();
            if (TextUtils.isEmpty(queryString)) {
                // 调数据库历史搜索
                ArrayList<SearchHistoryModel> searchHistoryModels = SearchHistoryManager.getInstance().mSearchHistoryDao.getAll();

                Log.e("xx", "159------" + searchHistoryModels);

                if(searchHistoryModels.size() > 0){
                    searchHistoryAdapter.setModels(searchHistoryModels);
                    autoSearchListView.setVisibility(View.INVISIBLE);
                    productListView.setVisibility(View.INVISIBLE);
                    searchHistoryLayout.setVisibility(View.VISIBLE);
                    searchHistoryAdapter.notifyDataSetChanged();
                }

            }else{
                ArrayList<ProductModel> models = searchInApp();
                if(models != null){
                    inAppAutoSearchAdapter.setModels(models);
                    autoSearchListView.setVisibility(View.VISIBLE);
                    productListView.setVisibility(View.INVISIBLE);
                    searchHistoryLayout.setVisibility(View.INVISIBLE);
                    inAppAutoSearchAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private ModelStatusListener<ModelEvent, ProductModel> productStatusListener = new ModelStatusListener<ModelEvent, ProductModel>() {
        @Override
        public void onModelEvent(ModelEvent event, ProductModel model) {
            switch (event) {
                case OnModelsGet:
                    products = searchList;
//                    searchInApp();
                    ArrayList<ProductModel> models = searchInApp();
                    if(models != null){
                        inAppSearchAdapter.setModels(models);
                    }
                    inAppSearchAdapter.notifyDataSetChanged();
                    autoSearchListView.setVisibility(View.INVISIBLE);
                    productListView.setVisibility(View.VISIBLE);
                    searchHistoryLayout.setVisibility(View.INVISIBLE);
                    break;
            }

        }
    };

    private OnClickListener searchButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            searchInApp();
            String queryString = keywordEditText.getText().toString();
            if (TextUtils.isEmpty(queryString)) {
                Toast.makeText(ContextProvider.getContext(), "输入的内容不能为空",Toast.LENGTH_LONG).show();
            }else{
                ArrayList<ProductModel> models = searchInApp();
                if(models != null){
                    inAppSearchAdapter.setModels(models);
                    autoSearchListView.setVisibility(View.INVISIBLE);
                    productListView.setVisibility(View.VISIBLE);
                    searchHistoryLayout.setVisibility(View.INVISIBLE);

                    if(models.size() > 0){
                        SearchHistoryModel searchHistoryModel = new SearchHistoryModel();
                        searchHistoryModel.setName(queryString);
                        Log.e("xx", "267------searchHistoryModel="+searchHistoryModel);
                        SearchHistoryManager.getInstance().save(searchHistoryModel);
                    }
                }
            }
        }
    };

    private ArrayList<ProductModel> searchInApp() {
        String queryString = keywordEditText.getText().toString();
        if (TextUtils.isEmpty(queryString)) {

            return null;
        }
        ArrayList<ProductModel> matchedProducts = new ArrayList<>();

        for (ProductModel pm : products) {
            if (pm.getName().contains(queryString)) {
                matchedProducts.add(pm);

            }
        }

        return matchedProducts;
    }

    private OnItemClickListener searchItemOnClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, EditProductViewController.class);
            intent.putExtra(EditProductViewController.KEY_PRODUCT, inAppSearchAdapter.getItem(position));
            getActivity().startActivity(intent);
        }
    };
    private OnItemClickListener autoSearchItemOnClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            String keyName = inAppAutoSearchAdapter.getItem(position).getName();
            keywordEditText.setText(keyName);
            searchButton.performClick();

        }
    };
    private OnItemClickListener searchHistoryItemOnClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            String keyName = searchHistoryAdapter.getItem(position).getName();
            keywordEditText.setText(keyName);
            searchButton.performClick();

        }
    };
}
