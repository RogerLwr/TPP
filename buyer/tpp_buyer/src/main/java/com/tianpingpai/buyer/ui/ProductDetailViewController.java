package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.model.SellerProductDetail;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Statistics(page = "商品详情")
@ActionBar(title = "商品详情")
@Layout(id = R.layout.ui_product_detail)
public class ProductDetailViewController extends BaseViewController {
	
	public static final String KEY_PRODUCT_ID = "key.ProductId";
	
	private SellerProductDetail productModel;

	@Binding(id = R.id.name_text_view,format = "{{prod_name}}")
	private TextView nameTextView;
	@Binding(id = R.id.price_text_view,format = "{{coupon_price}}/{{unit}}")
	private TextView priceTextView;
	@Binding(id = R.id.origin_text_view,format = "产地:{{addr}}")
	private TextView originTextView;
	@Binding(id = R.id.desc_text_view,format = "描述：{{description}}")
	private TextView descTextView;
	@Binding(id = R.id.store_name_text_view,format = "商家店名：{{sale_name}}")
	private TextView storeNameTextView;
	@Binding(id = R.id.store_address_text_view,format = "商家地址：{{sale_addr}}")
	private TextView storeAddressTextView;
	@Binding(id = R.id.store_desc_text_view,format = "店家描述：{{shop_desc}}")
	private TextView storeDescTextView;
	@Binding(id = R.id.sale_number_text_view,format = "销量：已售出{{sales}}")
	private TextView saleNumberTextView;
	@Binding(id = R.id.store_rating_bar)
	private RatingBar storeRatingBar;
	@Binding(id = R.id.number_edit_text)
	private EditText numberEditText;
	
	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		loadData();
	}
	
	private ResultListener<ModelResult<SellerProductDetail>> listener = new ResultListener<ModelResult<SellerProductDetail>>() {

		@Override
		public void onResult(HttpRequest<ModelResult<SellerProductDetail>> request,
				ModelResult<SellerProductDetail> data) {
			hideLoading();
			showContent();
			if(data.isSuccess()){
				SellerProductDetail p = data.getModel();
				productModel = p;
				getBinder().bindData(p);
				float rating = (float) (p.getRating() / 10.0 * 5);
				storeRatingBar.setRating(rating);
				storeRatingBar.setEnabled(false);
				ArrayList<ProductModel> list = new ArrayList<>();
				list.add(p);
				ShoppingCartManager.getInstance().syncProductNumberFromDb(list,false);
			}else{
				ResultHandler.handleError(data, ProductDetailViewController.this);
			}
		}
	};
	
	private void loadData(){
		long productId = getActivity().getIntent().getLongExtra(KEY_PRODUCT_ID, -1);
		if(productId == -1){
			productId = getActivity().getIntent().getLongExtra("id", -1);
		}
		String url = ContextProvider.getBaseURL() + "/api/prod/getSaleProdDetailInfo.json";
		HttpRequest<ModelResult<SellerProductDetail>> req = new HttpRequest<>(url, listener);
		req.addParam("id", "" + productId);
		ModelParser<SellerProductDetail> parser = new ModelParser<>(SellerProductDetail.class);
		req.setParser(parser);
		req.setErrorListener(new CommonErrorHandler(this));
		VolleyDispatcher.getInstance().dispatch(req);
		showLoading();
	}

	@OnClick(R.id.increase_button)
	private OnClickListener increaseButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int productNumber;
			try {
				productNumber = Integer
						.parseInt(numberEditText.getText()
								.toString());
			} catch (NumberFormatException ee) {
				productNumber = 0;
			}
			productNumber++;
			numberEditText.setText(String.valueOf(productNumber));
		}
	};

	@OnClick(R.id.decrease_button)
	private OnClickListener decreaseButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int productNumber;
			try {
				productNumber = Integer
						.parseInt(numberEditText.getText()
								.toString());
			} catch (NumberFormatException ee) {
				productNumber = 0;
			}
			productNumber--;
			productNumber = Math.max(productNumber, 0);
			numberEditText.setText(String.valueOf(productNumber));
		}
	};

	@OnClick(R.id.visit_button)
	private OnClickListener visitButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(),ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT, StoreViewController.class);
			intent.putExtra(StoreDataContainer.KEY_STORE_ID, productModel.getSellerId());
			intent.putExtra(StoreDataContainer.KEY_STORE_TYPE, StoreDataContainer.STORE_TYPE_NORMAL);
			intent.putExtra(StoreDataContainer.KEY_SHOP_NAME, productModel.getSellerName());
			getActivity().startActivity(intent);
		}
	};

	@OnClick(R.id.add_to_cart_button)
	private OnClickListener addToCartButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int productNumber;
			try{
				productNumber = Integer.parseInt(numberEditText.getText().toString());
			}catch(NumberFormatException e){
				productNumber = 0;
			}
			
			if(productNumber == 0){
				Toast.makeText(ContextProvider.getContext(), "商品数量不能为0", Toast.LENGTH_SHORT).show();
				return;
			}
			
			productModel.setProductNum(productNumber);
			ArrayList<ProductModel> products = new ArrayList<>();
			products.add(productModel);
			ShoppingCartManager.getInstance().addToShoppingCart(products);

			Intent intent = new Intent(getActivity(),ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT, ShoppingCartViewController.class);
			getActivity().startActivity(intent);
		}
	};
}
