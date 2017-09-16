package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;
import java.util.HashSet;

@Statistics(page = "常用")
@Layout(id = R.layout.ui_common_use)
public class CommonUseViewController extends BaseViewController {
	private int shopId;
	private int shopType;
	private HashSet<ProductModel> selectionSet = new HashSet<>();
	
	private CommonUseProductAdapter productAdapter = new CommonUseProductAdapter();
	private ProductCategoryAdapter categoryAdapter = new ProductCategoryAdapter();
	
	private TextView priceSumTextView;
	private TextView categoryNumberTextView;
	private Spinner categorySpinner;

	private SwipeRefreshLayout refreshLayout;
	private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

	private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			loadCommonUseData();
		}
	};
	
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
				ResultHandler.handleError(data, CommonUseViewController.this);
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
	
	private OnItemSelectedListener categorySelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			CategoryModel category = categoryAdapter.getItem(position);
			if(category.getId() == -1){
				productAdapter.setData(result);
			}else{
				ArrayList<ProductModel> products = new ArrayList<>();
				for(ProductModel pm:result.getModels()){
					if(pm.getFirstCategoryId() == category.getId()){
						products.add(pm);
					}
				}
				productAdapter.setModels(products);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};

	@Override
	public void onActivityCreated(Activity a) {
		super.onActivityCreated(a);
		Intent intent = a.getIntent();
//		shop_name = intent.getStringExtra("shop_name");
		shopId = intent.getIntExtra("shop_id", CommonUtil.DEFAULT_ARG);
		shopType = intent.getIntExtra("shop_type", CommonUtil.DEFAULT_ARG);
		productAdapter.setProductNumberChangeListener(productNumberChangeListener);
		productAdapter.setActivity((FragmentActivity) a);
	}
	
	@Override
	public void didSetContentView(Activity a) {
		super.didSetContentView(a);
		View actionBar = setActionBarLayout(R.layout.ab_common_use);
		actionBar.findViewById(R.id.ab_add_button).setOnClickListener(addButtonListener);
		categorySpinner = (Spinner) actionBar.findViewById(R.id.ab_category_spinner);
		categorySpinner.setAdapter(categoryAdapter);
		if(shopType == -1){
			actionBar.findViewById(R.id.ab_add_button).setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		initView();
		refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
		refreshLayoutControl.setOnRefreshListener(refreshListener);
		refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
	}
	
	private void initView(){
		ListView productListView = (ListView) getView().findViewById(R.id.product_list_view);
		productListView.setAdapter(productAdapter);

		priceSumTextView = (TextView) getView().findViewById(R.id.price_sum_text_view);
		categoryNumberTextView = (TextView) getView().findViewById(R.id.category_num_text_view);

		getView().findViewById(R.id.add_to_cart_button).setOnClickListener(addToCartButtonListener);
		loadCommonUseData();
	}
	
	@Override
	public void onActivityResult(Activity a,int requestCode, int resultCode, Intent data) {
		super.onActivityResult(a,requestCode, resultCode, data);
		Log.w("xx", "191---------------CommonUse");
		if(resultCode == Activity.RESULT_OK){
			loadCommonUseData();//TODO save selected
		}else{
			//TODO 未登录?
			getActivity().finish();
		}
	}

	private OnClickListener addButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(),ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT, SelectProductsViewController.class);
			intent.putExtra("shop_id", shopId);
			int catId = getActivity().getIntent().getIntExtra("category_id",-1);
			intent.putExtra("category_id", catId);
			int shopType = getActivity().getIntent().getIntExtra("shop_type",-1);
			intent.putExtra("shop_type", shopType);
			
			getActivity().startActivityForResult(intent, CommonUtil.REQUST_CODE_COMMON_USE_ADD);
		}
	};
	
	private OnClickListener addToCartButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			for(ProductModel pm:selectionSet){
				pm.setCategoryId(pm.getSecondCategoryID());
			}
			ShoppingCartManager.getInstance().addToShoppingCart(selectionSet);
			Intent intent = new Intent(getActivity(),ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT, ShoppingCartViewController.class);
			getActivity().startActivity(intent);
			getActivity().finish();
		}
	};

    //TODO
	ListResult<ProductModel> result;
	private ResultListener<ListResult<ProductModel>> listener = new ResultListener<ListResult<ProductModel>>() {
		@Override
		public void onResult(HttpRequest<ListResult<ProductModel>> request,
				ListResult<ProductModel> data) {
			result = data;//TODO
			showContent();
			hideLoading();
			if(data.isSuccess()){
				productAdapter.clear();
				productAdapter.setData(data);
				updateSpinner();
				selectionSet.clear();
				if(categoryNumberTextView != null){
					updateBottomPanel();
				}
                if(productAdapter.isEmpty()){
                    showEmptyView();
                }else{
                    hideEmptyView();
                }
			}else{
				ResultHandler.handleError(data, CommonUseViewController.this);
			}
			if (refreshLayout != null) {
				refreshLayout.setRefreshing(false);
			}
		}
	};

	private void loadCommonUseData(){
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
		showLoading();
	}
	
	private void updateBottomPanel(){
		SpannableStringBuilder style = new SpannableStringBuilder("已选品类:"
				+ selectionSet.size() + "");
		style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 5,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(
				new ForegroundColorSpan(getActivity().getResources().getColor(
						R.color.orange_f6)), 5, style.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		categoryNumberTextView.setText(style);
		
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
		priceSumTextView.setText(style);
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
		categorySpinner.setOnItemSelectedListener(null);
		categoryAdapter.setModels(categories);
		categorySpinner.setOnItemSelectedListener(categorySelectedListener);
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
