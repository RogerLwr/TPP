package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.ProductAdapter;
import com.tianpingpai.buyer.adapter.SearchHistoryProdAdapter;
import com.tianpingpai.buyer.adapter.SearchProdAdapter;
import com.tianpingpai.buyer.adapter.SearchProductAdapter;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.manager.SearchHistoryManager;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SearchHistoryModel;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter.PageControl;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.viewController.ProdDetailViewController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unused")
@Statistics(page = "搜索")
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_search)
@Layout(id = R.layout.ui_search)
public class SearchViewController extends BaseViewController {

	public static final String KEY_IN_APP_SEARCH = "key.Products";
	public static final String KEY_STORE_ID = "Key.storeId";
	public static final String KEY_WORD = "Key.word";
	private String keyWord;

	int storeId = -1;

	static ArrayList<ProductModel> searchList;
	private boolean inAppSearch;
	private View emptyView;

	public static void setSearchList(ArrayList<ProductModel> s) {
		searchList = s;
	}

	private ArrayList<ProductModel> products;

	private ProductAdapter inAppSearchAdapter = new ProductAdapter();

	{
		inAppSearchAdapter.setSearch(true);
	}

    private SearchProductAdapter adapter = new SearchProductAdapter();
    private PageControl<ProductModel> pageControl = new PageControl<ProductModel>() {
        @Override
        public void onLoadPage(int page) {
			TLog.e("xx", "82-------page=="+page);
			loadPage(page);
        }
    };

	private EditText keywordEditText;
    @Binding(id = R.id.product_list_view)
	private ListView productListView;
    @Binding(id = R.id.auto_search_list_view)
    private ListView autoSearchListView;
	@Binding(id = R.id.layout_search_history)
	private RelativeLayout searchHistoryLayout;
	@Binding(id = R.id.search_history_list_view)
	private ListView searchHistoryListView;
	@Binding(id = R.id.clear_search_history_tv)
	private TextView clearSearchHistoryTV;

	private View searchButton;

	private SearchProdAdapter inAppAutoSearchAdapter = new SearchProdAdapter();
	private SearchHistoryProdAdapter searchHistoryAdapter = new SearchHistoryProdAdapter();

	@OnClick(R.id.clear_search_history_tv)
	private OnClickListener clearSearchHistoryClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			final ActionSheetDialog dialog = new ActionSheetDialog();
			dialog.setActionSheet(getActionSheet(true));
			dialog.setTitle("您确定要删除历史记录吗?");
			dialog.setPositiveButtonListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					SearchHistoryManager.getInstance().mSearchHistoryDao.clearByStoreId(storeId);
					searchHistoryAdapter.setModels(SearchHistoryManager.getInstance().mSearchHistoryDao.getHistoryById(storeId));
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	};

	@Override
	public void onActivityCreated(Activity a) {
		super.onActivityCreated(a);
		keyWord = a.getIntent().getStringExtra(KEY_WORD);
	}

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
        showContent();
        inAppSearch = getActivity().getIntent().getBooleanExtra(KEY_IN_APP_SEARCH, false);
        storeId = getActivity().getIntent().getIntExtra(KEY_STORE_ID, -1);
        View actionBarView = setActionBarLayout(R.layout.ab_search);
		keywordEditText = (EditText) actionBarView.findViewById(R.id.keyword_edit_text);

        searchButton = actionBarView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(searchButtonListener);

        keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    searchButton.performClick();
                }
                return false;
            }
        });

		if (!inAppSearch) {
			productListView.setAdapter(adapter);
			productListView.setOnItemClickListener(searchItemOnClickListener);
			adapter.setPageControl(pageControl);
		} else {
			products = searchList;
			searchList = null;
			if (products == null) {
				getActivity().finish();
			}
			productListView.setAdapter(inAppSearchAdapter);
			inAppSearchAdapter.setActivity(getActivity());
		}

		keywordEditText.addTextChangedListener(watcher);
		keywordEditText.setOnFocusChangeListener(onFoucusChangeListener);
		autoSearchListView.setOnItemClickListener(autoSearchItemOnClickListener);
		autoSearchListView.setAdapter(inAppAutoSearchAdapter);
		inAppAutoSearchAdapter.setActivity(getActivity());

		searchHistoryListView.setOnItemClickListener(searchHistoryItemOnClickListener);
		searchHistoryListView.setAdapter(searchHistoryAdapter);
		searchHistoryAdapter.setActivity(getActivity());

		configureActionBar(rootView);


		if( !TextUtils.isEmpty(keyWord) ){
			keywordEditText.setText(keyWord);
			searchButton.performClick();
		}else {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					InputMethodManager m = (InputMethodManager) keywordEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}, 300);
		}

	}

	private ResultListener<ListResult<ProductModel>> searchListener = new ResultListener<ListResult<ProductModel>>() {
		@Override
		public void onResult(HttpRequest<ListResult<ProductModel>> request,
							 ListResult<ProductModel> data) {
			if (data.isSuccess()) {
				//TODO
				hideLoading();
				emptyView = getView().findViewById(R.id.empty_view);
				productListView.setEmptyView(emptyView);
				Boolean isFirst = request.getAttachment(Boolean.class);
				String keyWord = request.getAttachment(String.class);
				if (isFirst != null && isFirst) {
					adapter.clear();
				}
				autoSearchListView.setVisibility(View.GONE);
				productListView.setVisibility(View.VISIBLE);
				searchHistoryLayout.setVisibility(View.GONE);
				ArrayList<ProductModel> models = data.getModels();
				if (models.size() > 0) {
					SearchHistoryModel searchHistoryModel = new SearchHistoryModel();
					searchHistoryModel.setId(storeId);
					searchHistoryModel.setName(keyWord);
					Log.e("xx", "267------searchHistoryModel=" + searchHistoryModel);
					SearchHistoryManager.getInstance().save(searchHistoryModel);
				}
				adapter.setData(data);
                if(adapter.isEmpty()){
                    showEmptyView();
                }else{
                    hideEmptyView();
                }
			} else {
				hideLoading();
				ResultHandler.handleError(data, SearchViewController.this);
			}
		}
	};

	private void loadPage(int page) {
		String queryString = keywordEditText.getText().toString();
		if (TextUtils.isEmpty(queryString)) {
			Toast.makeText(ContextProvider.getContext(), "输入的内容不能为空",
					Toast.LENGTH_LONG).show();
			return;
		}
		HttpRequest<ListResult<ProductModel>> req = new HttpRequest<>(
				URLUtil.SEARCH_URL, searchListener);
		try {
			req.addParam("prod_name", URLEncoder.encode(queryString, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		req.addParam("page", "" + page);

		if (page == 1) {
			req.setAttachment(Boolean.TRUE);
			showLoading();
		}

		req.setAttachment(queryString);

		ListParser<ProductModel> parser = new ListParser<>(
				ProductModel.class);
		req.setParser(parser);
		MarketModel market = MarketManager.getInstance().getCurrentMarket();


		if (market != null) {

			Log.e("xx", "marketId;" + market.getId());
			req.addParam("market_id", market.getId() + "");
		}
		req.setErrorListener(new CommonErrorHandler(this));

		VolleyDispatcher.getInstance().dispatch(req);
	}

	private OnClickListener searchButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

			if (products == null) {
				loadPage(1);
			} else {
				String queryString = keywordEditText.getText().toString();
				if (TextUtils.isEmpty(queryString)) {
					Toast.makeText(ContextProvider.getContext(), "输入的内容不能为空", Toast.LENGTH_LONG).show();
				} else {
					ArrayList<ProductModel> models = searchInApp();
					if (models != null) {
						inAppSearchAdapter.setModels(models);
						autoSearchListView.setVisibility(View.GONE);
						productListView.setVisibility(View.VISIBLE);
						searchHistoryLayout.setVisibility(View.GONE);

						if (models.size() > 0) {
							SearchHistoryModel searchHistoryModel = new SearchHistoryModel();
							searchHistoryModel.setId(storeId);
							searchHistoryModel.setName(queryString);
							Log.e("xx", "267------searchHistoryModel=" + searchHistoryModel);
							SearchHistoryManager.getInstance().save(searchHistoryModel);
						}

					}
				}
			}
		}

	};

	private ArrayList<ProductModel> searchInApp() {
		String queryString = keywordEditText.getText().toString();
		if (TextUtils.isEmpty(queryString)) {
			Toast.makeText(ContextProvider.getContext(), "输入的内容不能为空",
					Toast.LENGTH_LONG).show();
			return null;
		}
		ArrayList<ProductModel> matchedProducts = new ArrayList<>();

		for (ProductModel pm : products) {
			if (pm.getName().contains(queryString)) {
				matchedProducts.add(pm);
			}
		}
		return matchedProducts;
//		inAppSearchAdapter.setModels(matchedProducts);
	}

	private View.OnFocusChangeListener onFoucusChangeListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View view, boolean b) {
			Log.e("xx", "319------获得焦点");
			if (b) {
//				if (inAppSearch) {
					String queryString = keywordEditText.getText().toString();
					if (TextUtils.isEmpty(queryString)) {
						// 调数据库历史搜索
						ArrayList<SearchHistoryModel> searchHistoryModels = SearchHistoryManager.getInstance().mSearchHistoryDao.getHistoryById(storeId);

						Log.e("xx", "159------" + searchHistoryModels);
//						if (searchHistoryModels.size() > 0) {
							searchHistoryAdapter.setModels(searchHistoryModels);
							autoSearchListView.setVisibility(View.GONE);
							productListView.setVisibility(View.GONE);
							searchHistoryLayout.setVisibility(View.VISIBLE);
							searchHistoryAdapter.notifyDataSetChanged();
//						}

					}
//				}
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

				String queryString = keywordEditText.getText().toString();
				if (TextUtils.isEmpty(queryString)) {
					// 调数据库历史搜索
					ArrayList<SearchHistoryModel> searchHistoryModels = SearchHistoryManager.getInstance().mSearchHistoryDao.getHistoryById(storeId);

					Log.e("xx", "159------" + searchHistoryModels);

					if (searchHistoryModels.size() > 0) {
						searchHistoryAdapter.setModels(searchHistoryModels);
						autoSearchListView.setVisibility(View.GONE);
						productListView.setVisibility(View.GONE);
						searchHistoryLayout.setVisibility(View.VISIBLE);
						if(emptyView != null){
							emptyView.setVisibility(View.GONE);
						}
						searchHistoryAdapter.notifyDataSetChanged();
					}

				} else {
					if(inAppSearch) {
					ArrayList<ProductModel> models = searchInApp();
					if (models != null) {
						inAppAutoSearchAdapter.setModels(models);
						autoSearchListView.setVisibility(View.VISIBLE);
						productListView.setVisibility(View.GONE);
						searchHistoryLayout.setVisibility(View.GONE);
						inAppAutoSearchAdapter.notifyDataSetChanged();
					}
				}
			}
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	};


	private OnItemClickListener searchItemOnClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
            ActionSheet as = new ActionSheet();
			as.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);// 设置为屏幕高度
			ProdDetailViewController pdvc = new ProdDetailViewController();
			pdvc.setActivity(SearchViewController.this.getActivity());
//			pdvc.setIsShowBottom(true);
			pdvc.setShowButtonContainer(true);
			pdvc.setProdId(adapter.getItem(position).getId());
			pdvc.setModel(adapter.getItem(position));
			as.setViewController(pdvc);
			as.show();
		}
	};

	private OnItemClickListener autoSearchItemOnClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
			String keyName = inAppAutoSearchAdapter.getItem(position).getName();
			keywordEditText.setText(keyName);
			keywordEditText.selectAll();//全选关键字
			searchButton.performClick();

		}
	};
	private OnItemClickListener searchHistoryItemOnClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
			String keyName = searchHistoryAdapter.getItem(position).getName();
			keywordEditText.setText(keyName);
			keywordEditText.selectAll();//全选关键字
			searchButton.performClick();
		}
	};
}

