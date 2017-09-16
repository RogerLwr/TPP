package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.HomeAdapter;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.ActivityModel;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.buyer.parser.ActivityListParser;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ErrorListener;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.GadgetModel;
import com.tianpingpai.model.GadgetModel.Item;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.PageModel;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.tools.Storage;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.viewController.HomeNotificationViewController;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "主页")
@Layout(id = R.layout.ui_home)
public class HomeViewController extends BaseViewController {

    public static final int REQUEST_CODE_SCAN_BAR_CODE = 100;
	private String activitiesUrl;

	public void setMainFragment(MainViewController mainFragment) {
		this.mainFragment = mainFragment;
	}

	public MainViewController mainFragment;

	private SwipeRefreshLayout refreshLayout;
	private SwipeRefreshLayoutControl refreshControl = new SwipeRefreshLayoutControl();
	private HomeAdapter homeAdapter = new HomeAdapter();

	private TextView marketButton;

	private void initActionBar(View rootView) {
//		rootView.findViewById(R.id.search_button).setOnClickListener(searchButtonListener);
		rootView.findViewById(R.id.search_edit_text).setOnClickListener(searchButtonListener);
		rootView.findViewById(R.id.add_button).setOnClickListener(addButtonListener);
		marketButton = (TextView) rootView.findViewById(R.id.market_button);
		rootView.findViewById(R.id.market_button).setOnClickListener(marketButtonListener);
		updateMarketSpinner();
	}


	public int getContentHeight() {
		return mainFragment.getView().getHeight();
	}

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
        View actionBarView = setActionBarLayout(R.layout.ab_home);
		initActionBar(actionBarView);
		homeAdapter.setHome(this);
		ListView homeListView = (ListView) rootView
				.findViewById(R.id.home_list_view);
		homeListView.setAdapter(homeAdapter);

		refreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.refresh_layout);

		refreshControl.setSwipeRefreshLayout(refreshLayout);
		refreshControl.setOnRefreshListener(refreshListener);

		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		homeAdapter.setListViewWidth(width);
		loadData();
		MarketManager.getInstance().registerListener(marketListener);
        UserManager.getInstance().registerListener(userListener);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MarketManager.getInstance().unregisterListener(marketListener);
        UserManager.getInstance().unregisterListener(userListener);
	}

    private ModelStatusListener<UserEvent, UserModel> userListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if(event == UserEvent.Logout){
                getActivity().finish();
            }
        }
    };

	private ModelStatusListener<ModelEvent, MarketModel> marketListener = new ModelStatusListener<ModelEvent, MarketModel>() {
		@Override
		public void onModelEvent(ModelEvent event, MarketModel model) {
			updateMarketSpinner();
			homeAdapter.clear();
			refreshControl.triggerRefresh();
		}
	};


	private ErrorListener errorListener = new CommonErrorHandler(this);

	private void updateMarketSpinner() {
		MarketModel market = MarketManager.getInstance().getCurrentMarket();
		if(market != null){
			marketButton.setText(market.getName());
		}
	}

	private void loadData() {
		String url = URLApi.dashBoard();
		HttpRequest<ModelResult<PageModel>> req = new HttpRequest<>(
				url, listener);
		ModelParser<PageModel> parser = new ModelParser<>(
				PageModel.class);
		MarketModel currentMarket = MarketManager.getInstance()
				.getCurrentMarket();
		if (currentMarket != null) {
			req.addParam("market_id", currentMarket.getId() + "");
		}
		req.setParser(parser);
		req.setErrorListener(errorListener);
		VolleyDispatcher.getInstance().dispatch(req);
		showLoading();
	}

	ActionSheet as;

	private ResultListener<ModelResult<PageModel>> listener = new ResultListener<ModelResult<PageModel>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<PageModel>> request,
				ModelResult<PageModel> data) {
			refreshLayout.setRefreshing(false);
			hideLoading();
			if (data.isSuccess()) {
				homeAdapter.setData(data.getModel().getGadgets());
				boolean loadData = false;
				for (GadgetModel g : data.getModel().getGadgets()) {
					if (g.getType() == GadgetModel.TYPE_ACTIVITY) {
						if(g.getContent() != null && !g.getContent().isEmpty()){
							Item item = g.getContent().get(0);
							activitiesUrl = item.getUrl();
							loadActivities(1);
							loadData = true;
						}
						break;
					}
				}
				if(!loadData){
					hideLoading();
				}
				String notificationString = homeAdapter.getNotificationString();
				Log.e("notification","======"+notificationString);
                if(notificationString != null ){
                    String key = "note_" + MarketManager.getInstance().getCurrentMarket().getId();
                    String savedNote = Storage.getInstance().getString(key);
                    if(!notificationString.equals(savedNote) && getActivity() != null){
//					if(!show && getActivity() != null){
						Log.e("youtanchuang","chuxianle====="+notificationString);
						Storage.getInstance().putString(key, notificationString);
						as = getActionSheet(true);
						WindowManager wm = HomeViewController.this.getActivity().getWindowManager();
						DisplayMetrics dm = new DisplayMetrics();
						wm.getDefaultDisplay().getMetrics(dm);
						as.setHeight(dm.heightPixels);// 设置为屏幕高度
						HomeNotificationViewController nvc = new HomeNotificationViewController();
						nvc.setNotificationStr(notificationString);
						nvc.setHrefString(homeAdapter.getNotificationHref());
						nvc.setActivity(HomeViewController.this.getActivity());
						as.setViewController(nvc);
						as.show();
                    }
                }
			}
		}
	};

	private OnRefreshListener refreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			loadData();
		}
	};
	private ResultListener<ListResult<ActivityModel>> activitiesListener = new ResultListener<ListResult<ActivityModel>>() {
		@Override
		public void onResult(HttpRequest<ListResult<ActivityModel>> request,
				ListResult<ActivityModel> data) {
			if(data.isSuccess()){
				homeAdapter.addActivities(data);
			}
		}
	};

	private void loadActivities(int page) {
		HttpRequest<ListResult<ActivityModel>> req = new HttpRequest<>(
				activitiesUrl, activitiesListener);
		MarketModel currentMarket = MarketManager.getInstance()
				.getCurrentMarket();
		if (currentMarket != null) {
			req.addParam("market_id", currentMarket.getId() + "");
		}
		ActivityListParser parser = new ActivityListParser();
		req.setParser(parser);
		req.setAttachment(page);
		req.setErrorListener(new ErrorListener() {
			@Override
			public void onError(HttpRequest<?> request, HttpError error) {
				hideLoading();
			}
		});
		VolleyDispatcher.getInstance().dispatch(req);
	}

    @Override
    protected void onReloadData() {
        super.onReloadData();
        loadData();
    }

	private TextView.OnEditorActionListener searchEditTextListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//				Toast.makeText(ContextProvider.getContext(), "你点了软键盘回车按钮", Toast.LENGTH_SHORT).show();
				Intent intentSearch = new Intent(getActivity(),
						ContainerActivity.class);
				intentSearch.putExtra(ContainerActivity.KEY_CONTENT,
						SearchViewController.class);
				intentSearch.putExtra(SearchViewController.KEY_WORD, v.getText().toString());
				getActivity().startActivity(intentSearch);
				return true;
			}

			return false;
		}
	};

    private OnClickListener searchButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intentSearch = new Intent(getActivity(),
					ContainerActivity.class);
			intentSearch.putExtra(ContainerActivity.KEY_CONTENT,
					SearchViewController.class);
			getActivity().startActivity(intentSearch);
		}
	};

	private OnClickListener addButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intentMarket = new Intent(getActivity(),
                    CaptureActivity.class);
            getActivity().startActivityForResult(intentMarket, REQUEST_CODE_SCAN_BAR_CODE);
		}
	};
	
	private OnClickListener marketButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(),ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT, SelectMarketViewController.class);
			getActivity().startActivity(intent);
		}
	};
}
