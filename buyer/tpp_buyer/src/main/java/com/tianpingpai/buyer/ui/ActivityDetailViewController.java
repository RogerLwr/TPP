package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.ImagePagerAdapter;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.ActiivtyDetailParser;
import com.tianpingpai.buyer.model.ActivityModel;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.widget.CirclePageIndicator;
import com.tianpingpai.widget.RangeInputFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Statistics(page = "活动详情")
@ActionBar(title = "活动详情")
@Layout(id = R.layout.fragment_activity_detail)
public class ActivityDetailViewController extends BaseViewController {

	public static final String KEY_ACTIVITY_ID = "key.ActivityID";
	private ActivityModel activityModel;
	private Handler mHandler = new Handler();

	private ImagePagerAdapter imageAdapter = new ImagePagerAdapter();

    @Binding(id = R.id.name_text_view,format = "{{name}}")
	private TextView nameTextView;
    @Binding(id = R.id.price_text_view)
	private TextView priceTextView;
    @Binding(id = R.id.original_price_text_view)
	private TextView originalPriceTextView;
    @Binding(id = R.id.count_down_text_view)
	private TextView countDownTextView;
    @Binding(id = R.id.rule_text_view,format = "{{rule}}")
	private TextView ruleTextView;
    @Binding(id = R.id.desc_text_view,format = "{{introduction}}")
	private TextView descTextView;
    @Binding(id = R.id.store_container)
	private View storeContainer;
    @Binding(id = R.id.store_name_text_view)
	private TextView storeNameTextView;
    @Binding(id = R.id.store_address_text_view)
	private TextView addressTextView;
    @Binding(id = R.id.space_view)
	private View spaceView;
    @Binding(id = R.id.product_number_container)
	private View productNumberContainer;
    @Binding(id = R.id.product_number_edit_text)
	private EditText productNumberEditText;
    @Binding(id = R.id.rating_bar)
	private RatingBar ratingBar;
    @Binding(id = R.id.buy_now_button)
	private TextView buyButton;
	
	long startTime = 0;
	long timeDiff = 0;
	private Runnable updateCountDownRun = new Runnable() {
		@Override
		public void run() {
			mHandler.postDelayed(updateCountDownRun, 1000);
			if (timeDiff == 0 || countDownTextView == null) {
				return;
			}
			long diff = timeDiff - (System.currentTimeMillis() - startTime);//TODO
			if (diff > 0) {
				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);
				String text = "距活动结束还有" + diffDays + "天"
						+ diffHours + "时" + diffMinutes + "分" + diffSeconds
						+ "秒";
				countDownTextView.setText(text);
			} else {
				buyButton.setEnabled(false);
				productNumberContainer.setVisibility(View.GONE);
				productNumberEditText.setVisibility(View.GONE);
				spaceView.setVisibility(View.VISIBLE);
				countDownTextView.setText("活动已结束！");
				mHandler.removeCallbacks(updateCountDownRun);
			}
		}
	};
	
	private ResultListener<ModelResult<ActivityModel>> resultListener = new ResultListener<ModelResult<ActivityModel>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<ActivityModel>> request,
				ModelResult<ActivityModel> data) {
			hideLoading();
			if(getActivity() == null){
				return;
			}
			if(data.isSuccess()){
				showContent();
				ActivityModel model = data.getModel();
				activityModel = model;
                getBinder().bindData(model);
				setTitle(model.getName());

				if(model.getLimitNumber() > 0){
					productNumberEditText.setFilters(new InputFilter[]{new RangeInputFilter(0, (int) model.getLimitNumber())});
					int intVersion = (int) model.getLimitNumber();
					//去掉小数点
					String limit = model.getLimitNumber() - intVersion > 0.0001 ? model.getLimitNumber() + "" : intVersion + "";
					String hint = "最多购买" + limit + model.getUnit();
					productNumberEditText.setHint(hint);
				}
				
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
				
				startTime = System.currentTimeMillis();
				try {
					Date date = format.parse(model.getEndTime());
					Date startDate = format.parse(model.getCurrentTime());
					timeDiff = date.getTime() - startDate.getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				ProductModel product = model.getProduct();
				if(product != null){
					String couponPrice = "￥" + PriceFormat.format(model.getProduct().getCouponPrice());
					if (!TextUtils.isEmpty(model.getUnit())) {
						couponPrice += "/" + model.getUnit();
					}

					String price = "￥" + PriceFormat.format(product.getCouponPrice());
					if (!TextUtils.isEmpty(model.getUnit())) {
						price += "/" + model.getUnit();
					}
					originalPriceTextView.setText(price);
					priceTextView.setText(couponPrice);
				}else{
					originalPriceTextView.setVisibility(View.INVISIBLE);
				}


				SellerModel seller = model.getSeller();
				if(seller != null){
					storeNameTextView.setText(seller.getSaleName());
					addressTextView.setText(seller.getAddress());
					ratingBar.setProgress((int) seller.getRating());
				}else{
					storeContainer.setVisibility(View.GONE);
				}

				// ArrayList<String> urls = new ArrayList<String>();
				imageAdapter.setData(model.getImages());
				int width = getView().getWidth();
				int height = (int) (536.0 / 1080 * width);

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
						height);
				lp.width = width;
				lp.height = height;
				getView().findViewById(R.id.banner_pager_container).setLayoutParams(
						lp);

				if (isLimited()) {
					buyButton.setText("加入购物车");
					productNumberContainer.setVisibility(View.VISIBLE);
					productNumberEditText.setVisibility(View.VISIBLE);
					spaceView.setVisibility(View.GONE);
				} else {
					buyButton.setText("立即抢购 ");
					productNumberContainer.setVisibility(View.GONE);
					productNumberEditText.setVisibility(View.GONE);
					spaceView.setVisibility(View.VISIBLE);
				}
				updateCountDownRun.run();
			}else{
				ResultHandler.handleError(data, ActivityDetailViewController.this);
			}
		}
	};

	public void onResume() {
		super.onResume();
		mHandler.post(updateCountDownRun);
	}

    @OnClick(R.id.visit_button)
	private OnClickListener visitButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getActivity(),ContainerActivity.class);
			i.putExtra(ContainerActivity.KEY_CONTENT, StoreViewController.class);
			i.putExtra(StoreDataContainer.KEY_STORE_ID, activityModel.getProduct().getSellerId());
			i.putExtra(StoreDataContainer.KEY_STORE_TYPE,StoreDataContainer.STORE_TYPE_NORMAL);
			getActivity().startActivity(i);
		}
	};
	
	public void onActivityDestroyed(android.app.Activity a) {
		super.onActivityDestroyed(a);
		UserManager.getInstance().unregisterListener(loginListener);
	}
	
	private boolean isLimited(){
		return activityModel.getProduct() != null && activityModel.getProduct().getId() > 0;
	}
	
	private ModelStatusListener<UserEvent, UserModel> loginListener = new ModelStatusListener<UserEvent, UserModel>() {
		@Override
		public void onModelEvent(UserEvent event, UserModel model) {
			switch (event) {
			case Login:
				toConfirmOrderFragment();
				break;
			default:
				break;
			}
		}
	};
	
	private void toConfirmOrderFragment(){
		int productNumber = 0;
		try{
			productNumber = Integer.parseInt(productNumberEditText.getText().toString());
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		if(productNumber == 0){
			Toast.makeText(ContextProvider.getContext(), "数量不能为空哦！", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i = new Intent(getActivity(),
				ContainerActivity.class);

		ArrayList<ProductModel> products = new ArrayList<>();
		products.add(activityModel.getProduct());
		activityModel.getProduct().setProductNum(productNumber);
		activityModel.getProduct().setCartStatus(ProductModel.STATUS_IN_ORDER_IN_TIME);
		
//		ShoppingCartManager.getInstance().clearInTimeOrder();
//		ShoppingCartManager.getInstance().save(products);
        ShoppingCartManager.getInstance().setInTimeOrders(products);
		
		if(!UserManager.getInstance().isLoggedIn()){
			i = new Intent(getActivity(),ContainerActivity.class);
			i.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
			UserManager.getInstance().registerListener(loginListener);
		}else{
			i.putExtra(ContainerActivity.KEY_CONTENT,
					ConfirmOrderViewController.class);
			i.putExtra(ConfirmOrderViewController.KEY_ORDER_TYPE, ConfirmOrderViewController.ORDER_TYPE_IN_TIME);
		}
		
		getActivity().startActivity(i);
	}

    @OnClick(R.id.buy_now_button)
	private OnClickListener buyButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isLimited()) {
				toConfirmOrderFragment();
			} else {
				long activityID = getActivity().getIntent().getLongExtra(
						KEY_ACTIVITY_ID, -1);
				Intent i = new Intent(getActivity(),
						ContainerActivity.class);
				i.putExtra(ContainerActivity.KEY_CONTENT,
						ActivityFormViewController.class);
				i.putExtra(ActivityFormViewController.KEY_ACTIVITY_ID, activityID);
				if(activityModel
						.getProduct() != null){
					i.putExtra(ActivityFormViewController.KEY_PRODUCT_NAME, activityModel
							.getProduct().getName());
				}
				i.putExtra(ActivityFormViewController.KEY_LIMIT, activityModel.getLimitNumber());
				i.putExtra(ActivityFormViewController.KEY_UNIT, activityModel.getUnit());
				getActivity().startActivity(i);
			}
		}
	};
	
	@Override
	protected void onConfigureView(View rootView) {
		ViewPager bannerPager = (ViewPager) rootView.findViewById(R.id.banner_pager);
		CirclePageIndicator pageIndicator = (CirclePageIndicator) rootView
				.findViewById(R.id.page_indicator);
		pageIndicator.setViewPager(bannerPager);
		bannerPager.setAdapter(imageAdapter);
		long activityID = getActivity().getIntent().getLongExtra(
				KEY_ACTIVITY_ID, -1);
		if(activityID == -1){
			try{
				activityID = ( getActivity().getIntent().getIntExtra("id", -1) );
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		getBinder().bindView(this,rootView);
		originalPriceTextView.setPaintFlags(originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		rootView.findViewById(R.id.toggle_rule_button).setOnClickListener(

				new ToggleButtonListener(rootView
						.findViewById(R.id.rule_text_view)));
		rootView.findViewById(R.id.toggle_desc_button).setOnClickListener(
                new ToggleButtonListener(rootView
                        .findViewById(R.id.desc_text_view)));
		
		setChevron(rootView.findViewById(R.id.toggle_rule_button),true);
		setChevron(rootView.findViewById(R.id.toggle_desc_button), true);
		loadDetail(activityID);
	}
	
	private void loadDetail(long id) {
		HttpRequest<ModelResult<ActivityModel>> req = new HttpRequest<>(
				URLApi.Activity.getDetail(), resultListener);
		req.addParam("id", id + "");// TODO
		ActiivtyDetailParser parser = new ActiivtyDetailParser();
		req.setParser(parser);
		showLoading();
		VolleyDispatcher.getInstance().dispatch(req);
	}
	
	private void setChevron(View view,boolean open){
		TextView tv = (TextView) view;
		int res = open ? R.drawable.chevron_right_orange:R.drawable.chevron_down_orange;
		Drawable d = getActivity().getResources().getDrawable(res);
        if(d != null) {
            if (open) {
                d.setBounds(0, 0, 29, 52);
            } else {
                d.setBounds(0, 0, 52, 29);
            }
            tv.setCompoundDrawables(null, null, d, null);
        }
	}

	private class ToggleButtonListener implements OnClickListener {
		private View targetView;

		public ToggleButtonListener(View view) {
			targetView = view;
		}

		@Override
		public void onClick(View v) {
			if (targetView.getVisibility() == View.VISIBLE) {
				targetView.setVisibility(View.GONE);
				setChevron(v,true);
			} else {
				targetView.setVisibility(View.VISIBLE);
				setChevron(v,false);
			}
		}
	}
}
