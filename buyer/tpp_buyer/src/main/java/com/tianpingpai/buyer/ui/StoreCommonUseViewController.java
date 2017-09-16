package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.CommonUseProductAdapter;
import com.tianpingpai.buyer.adapter.CommonUseProductAdapter.ProductNumberChangeListener;
import com.tianpingpai.buyer.adapter.ProductCategoryAdapter;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.CategoryModel;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.AnimationTool;
import com.tianpingpai.widget.BadgeView;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;
import java.util.HashSet;

@Statistics(page = "常用")
@ActionBar(hidden = true)
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_common_use)
@Layout(id = R.layout.ui_store_common_use)
public class StoreCommonUseViewController extends BaseViewController {
	private int shopId;
	private int shopType;
	public static final String KEY_STORE_TYPE = "type";
	public static final int STORE_TYPE_NORMAL = 1;
	private HashSet<ProductModel> selectionSet = new HashSet<>();


	AnimationTool animationTool = new AnimationTool();
	private ImageView moveImageView;
	private View toShoppingCartView;

	public void setToShoppingCartView(View toShoppingCartView){
		this.toShoppingCartView = toShoppingCartView;
	}

	private CommonUseProductAdapter productAdapter = new CommonUseProductAdapter();
	private ProductCategoryAdapter categoryAdapter = new ProductCategoryAdapter();

	private TextView freightTextView;
	private TextView minAmountTextView;

	private TextView priceSumTextView;
	private BadgeView categoryNumberTextView;
	private StoreDataContainer dataContainer;
	private SwipeRefreshLayout refreshLayout;
	private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

	public void setDataContainer(StoreDataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}
	public void parseParams(Intent intent) {
		shopId = intent.getIntExtra("shop_id", -1);
		if (shopId == -1) {
			shopId = intent.getIntExtra("id", -1);
		}
		if (shopId == -1) {
			shopId = intent.getIntExtra("user_id", -1);// from bar code?
		}

		shopType = intent.getIntExtra(KEY_STORE_TYPE, STORE_TYPE_NORMAL);
		if( shopType == STORE_TYPE_NORMAL){
			loadData();
		}
	}


	private ProductNumberChangeListener productNumberChangeListener = new ProductNumberChangeListener() {
		@Override
		public void onProductNumberChange(ProductModel pm) {
			if(pm.getProductNum() > 0){
				selectionSet.add(pm);
			}else{
				selectionSet.remove(pm);
			}
			updateBottomPanel();
		}
	};
	private ResultListener<ModelResult<Void>> deleteListener = new ResultListener<ModelResult<Void>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Void>> request,
							 ModelResult<Void> data) {
			if(data.isSuccess()){
				ProductModel product = request.getAttachment(ProductModel.class);
				productAdapter.remove(product);
				Toast.makeText(ContextProvider.getContext(), "删除成功！", Toast.LENGTH_SHORT).show();
			}else{
				ResultHandler.handleError(data, StoreCommonUseViewController.this);
			}
		}
	};

	private void delete(ProductModel product){
		String url = URLUtil.DEL_COMMONUSE_URL;
		HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, deleteListener);
		UserModel user = UserManager.getInstance().getCurrentUser();
		if(user != null){
			req.addParam("accessToken", user.getAccessToken());
		}

		req.addParam("saler_id", product.getSellerId() + "");
		req.setAttachment(product);
		req.addParam("prod_id", product.getId() + "");
		req.setMethod(HttpRequest.POST);
		ModelParser<Void> parser = new ModelParser<>(Void.class);
		req.setParser(parser);
		VolleyDispatcher.getInstance().dispatch(req);
	}

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		productAdapter.setProductNumberChangeListener(productNumberChangeListener);
		productAdapter.setActivity(getActivity());
		initView();

		if(dataContainer.getStoreModel() != null){

			int freight = dataContainer.getStoreModel().getFreight();
			String text = "￥"
					+ dataContainer.getStoreModel().getMinAmount();
			if(freight <= 0){
				text += "起送";
			}else{
				text += "免运费";
			}
			minAmountTextView.setText(text);
			String freightStr = "运费:￥"
					+ dataContainer.getStoreModel().getFreight();
			freightTextView.setText(freightStr);
		}


	}

	private void initView(){
		ListView productListView = (ListView) getView().findViewById(R.id.product_list_view);
		productListView.setAdapter(productAdapter);
		freightTextView = (TextView) getView().findViewById(R.id.freight_text_view);
		minAmountTextView = (TextView) getView().findViewById(R.id.min_amount_text_view);
		refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
		refreshLayoutControl.setOnRefreshListener(refreshListener);
		refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);

		categoryNumberTextView = (BadgeView)getView().findViewById(R.id.category_num_text_view);

		priceSumTextView = (TextView) getView().findViewById(R.id.price_sum_text_view);

		getView().findViewById(R.id.add_to_cart_button).setOnClickListener(addToCartButtonListener);

		productAdapter.setBuyCartView(categoryNumberTextView);

	}

	private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			loadData();
		}
	};

	private OnClickListener addToCartButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			if(selectionSet.size() == 0){
//				return;
//			}

			for(ProductModel pm:selectionSet){
				pm.setCategoryId(pm.getSecondCategoryID());
				TLog.e("xx", "192------"+pm);
			}


			ShoppingCartManager.getInstance().addToShoppingCart(selectionSet);
			/*

			Intent intent = new Intent(getActivity(),
					ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT,
					ShoppingCartViewController.class);
			intent.putExtra("position", 2);
			intent.putExtra("is_shop_to_car", CommonUtil.SHOP_TO_CAR);
			getActivity().startActivity(intent);

//			getActivity().finish();

			*/

			//动画执行
			int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
			categoryNumberTextView.getLocationInWindow(start_location);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
			int[] stop_location = new int[2];
			toShoppingCartView.getLocationInWindow(stop_location);
			moveImageView = new ImageView(getActivity());
			moveImageView.setImageResource(R.drawable.selector_add_button);
			animationTool.setAnim(moveImageView, start_location, stop_location, getActivity());
		}
	};


	ListResult<ProductModel> result;
	private ResultListener<ListResult<ProductModel>> listener = new ResultListener<ListResult<ProductModel>>() {
		@Override
		public void onResult(HttpRequest<ListResult<ProductModel>> request,
							 ListResult<ProductModel> data) {
			result = data;//TODO
//			showContent();
//			hideLoading();
			if(data.isSuccess()){
				productAdapter.clear();
				selectionSet.clear();
				if(categoryNumberTextView != null){
					updateBottomPanel();
				}
				productAdapter.setData(data);
				updateSpinner();
                if(productAdapter.isEmpty()){
                    showEmptyView();
                }else{
                    hideEmptyView();
                }
			}else{
				ResultHandler.handleError(data, StoreCommonUseViewController.this);
			}
			if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
		}
	};

	private void loadData(){
		UserModel user = UserManager.getInstance().getCurrentUser();

		String url = URLUtil.COMMON_USE_URL;
		HttpRequest<ListResult<ProductModel>> req = new HttpRequest<>(url,listener);
		if(shopType == -1){
			req.addParam("type", "all");
		}else{
			req.addParam("saler_id", shopId + "");
			req.addParam("type", String.valueOf(shopType));
		}

		if(user != null){
			req.addParam("accessToken", user.getAccessToken());
		}

		ListParser<ProductModel> parser  = new ListParser<>(ProductModel.class);
		req.setParser(parser);
		VolleyDispatcher.getInstance().dispatch(req);
//		showLoading();
	}

	private void updateBottomPanel(){
		SpannableStringBuilder style = new SpannableStringBuilder("已选品类:"
				+ selectionSet.size() + "");
		style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 5,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(
				new ForegroundColorSpan(ContextProvider.getContext().getResources().getColor(
						R.color.orange_f6)), 5, style.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		categoryNumberTextView.setText(""+selectionSet.size());

		double totalPrice = 0;
		for(ProductModel pm:selectionSet){
			totalPrice += pm.getCouponPrice() * pm.getProductNum();
		}
		style = new SpannableStringBuilder("合计:￥" + PriceFormat.format(totalPrice));
		style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(
				new ForegroundColorSpan(getActivity().getResources().getColor(
						R.color.orange_f6)), 3, style.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		priceSumTextView.setText("￥ " + PriceFormat.format(totalPrice));

	}

	public void showDeleteDialog(final ProductModel productModel){
		final ActionSheetDialog dialog = new ActionSheetDialog();
		dialog.setActionSheet(getActionSheet(true));
		Log.w("xx", "76--------------长按删除");
		dialog.setTitle("确定删除？");
		dialog.setPositiveButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete(productModel);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void updateSpinner(){
		ArrayList<CategoryModel> categories = new ArrayList<>();
		categories.add(new CategoryModel(-1, "全部"));
		for(ProductModel pm:productAdapter.getModels()){
			CategoryModel cm = new CategoryModel(pm.getFirstCategoryId(),pm.getCategoryName());
			if(!categories.contains(cm)){
				categories.add(cm);
			}
		}
//		categorySpinner.setOnItemSelectedListener(null);
		categoryAdapter.setModels(categories);
//		categorySpinner.setOnItemSelectedListener(categorySelectedListener);
	}

	@Override
	public boolean onBackKeyDown(Activity a) {
		productAdapter.notifyDataSetChanged();
		updateBottomPanel();
		if(productAdapter.as != null){
			return productAdapter.as.handleBack(a);
		}
		return super.onBackKeyDown(a);
	}

}
