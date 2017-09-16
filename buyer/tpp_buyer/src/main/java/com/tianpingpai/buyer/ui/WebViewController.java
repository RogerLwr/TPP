package com.tianpingpai.buyer.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.brother.tpp.tools.ParseHrefUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.manager.NoticeManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.JSONModelMapper;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.js.JSActionBar;
import com.tianpingpai.web.WVJBWebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
@Statistics(page = "网页")
@Layout(id = R.layout.ui_web)
public class WebViewController extends BaseViewController {

	public static final String KEY_URL = "key.Url";
	public static final String KEY_ACTION_BAR_STYLE = "key.actionBarStyle";

	public static final int ACTION_BAR_STYLE_NORMAL = 1;
	public static final int ACTION_BAR_STYLE_FLOAT = 2;
	public static final int ACTION_BAR_STYLE_HIDDEN = 3;

	private static final int REQUEST_CODE_SELECT_ADDRESS = 201;

	private WebView webView;
	private WVJBWebViewClient.WVJBHandler getCurrentUserHandler = new WVJBWebViewClient.WVJBHandler() {
		@Override
		public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
			UserModel currentUser = UserManager.getInstance().getCurrentUser();
			if(currentUser != null){
				JSONObject jobj = new JSONObject();
				try {
					jobj.put("accessToken",currentUser.getAccessToken());
					jobj.put("userID",currentUser.getUserID());
					jobj.put("userType",currentUser.getUserType());
					jobj.put("phone",currentUser.getPhone());
					jobj.put("nickName",currentUser.getNickName());
					callback.callback(jobj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private MyWebViewClient webViewClient = null;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		String url = getActivity().getIntent().getStringExtra(KEY_URL);
		int actionbarStyle = getActivity().getIntent().getIntExtra(KEY_ACTION_BAR_STYLE, ACTION_BAR_STYLE_NORMAL);
		Log.e(KEY_URL, "====" + url);
		setActionBarLayout(R.layout.ab_web);
		webView = (WebView) rootView.findViewById(R.id.web_view);
		webViewClient = new MyWebViewClient(webView);
		webView.loadUrl(url);
		webView.getSettings().setJavaScriptEnabled(true);
		showLoading();
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					hideLoading();
					showContent();
					setTitle(view.getTitle());
				}
			}
		});

		JSActionBar jsActionBar = new JSActionBar();
		jsActionBar.setViewController(this);
		jsActionBar.setWebView(webView);
		webView.setWebChromeClient(new WebChromeClient());


		webViewClient.enableLogging();
		webView.setWebViewClient(webViewClient);
		webViewClient.registerHandler("orderConfirm", orderConfirmHandler);
		webViewClient.registerHandler("addToShoppingCart",addToShoppingCartHandler);
		webViewClient.registerHandler("getUserAdddress",getUserAddressHandler);
		webViewClient.registerHandler("exit",exitHandler);
		webViewClient.registerHandler("getCurrentUser",getCurrentUserHandler);
		webView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
						webView.goBack();   //后退
						return true;    //已处理
					}
				}
				return false;
			}
		});

		jsActionBar.setWebClient(webViewClient);

		webViewClient.registerHandler("getCurrentUser",getCurrentUserHandler);
		if (actionbarStyle == ACTION_BAR_STYLE_FLOAT || actionbarStyle == ACTION_BAR_STYLE_HIDDEN) {
			hideActionBar();
		}

		NoticeManager.getInstance().registerListener(reloadListener);
	}

	@Override
	protected void onDestroyView() {
		super.onDestroyView();
		NoticeManager.getInstance().unregisterListener(reloadListener);
	}

	private WVJBWebViewClient.WVJBHandler getUserAddressHandler = new WVJBWebViewClient.WVJBHandler() {
		@Override
		public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
			Intent intent = new Intent(getActivity(),ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT,EditAddressViewController.class);
			getActivity().startActivityForResult(intent, REQUEST_CODE_SELECT_ADDRESS);
		}
	};

	private WVJBWebViewClient.WVJBHandler exitHandler = new WVJBWebViewClient.WVJBHandler() {
		@Override
		public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
			getActivity().finish();
		}
	};

	private WVJBWebViewClient.WVJBHandler orderConfirmHandler = new WVJBWebViewClient.WVJBHandler() {
		@Override
		public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
			if(data != null){
				Model model = new Model();
				JSONModelMapper.mapObject((JSONObject) data, model);
				Log.e("xx", "95-----------" + data.toString());
				Log.e("xx", "101--------------" + model.toString());

				Bundle b = new Bundle();
				List<Model> shopModels = model.getList("shops", Model.class);
				HashSet<ProductModel> products = new HashSet<>();

				for(Model shopModel: shopModels){
					List<Model> prodModels = shopModel.getList("products", Model.class);
					double sum = 0;
					for (Model m : prodModels) {
						//TODO
						sum += Double.parseDouble(m.getString("price")) * m.getInt("num");
						ProductModel productModel = new ProductModel();
						productModel.setId(Long.parseLong(m.getString("id")));
						productModel.setUnit(m.getString("unit"));
						productModel.setProductNum(m.getInt("num"));
						productModel.setCouponPrice(Double.parseDouble(m.getString("price")));
						productModel.setRemark(m.getString("remark"));
						productModel.setCategoryId(Integer.parseInt(m.getString("categoryId")));
						productModel.setName(m.getString("name"));
						productModel.setSellerId(Integer.parseInt(shopModel.getString("shopId")));
						productModel.setSellerName(shopModel.getString("shopName"));
						productModel.setCartStatus(ProductModel.STATUS_IN_ORDER_IN_TIME);
						products.add(productModel);
					}
					if (Integer.parseInt(shopModel.getString("minAmount")) > sum) {
//					total += sm.first.getFreight();
						b.putDouble(Integer.parseInt(shopModel.getString("shopId")) + "", Integer.parseInt(shopModel.getString("minAmount")));
					}
				}
				ShoppingCartManager.getInstance().clearShoppingCart();
				ShoppingCartManager.getInstance().setInTimeOrders(products);

				Intent intent = new Intent(getActivity(), ContainerActivity.class);
				intent.putExtra(ContainerActivity.KEY_CONTENT,
						ConfirmOrderViewController.class);
				intent.putExtra(ConfirmOrderViewController.KEY_FREIGHT,
						b);
				intent.putExtra(ConfirmOrderViewController.KEY_ORDER_TYPE, ConfirmOrderViewController.ORDER_TYPE_IN_TIME);
				getActivity().startActivity(intent);
			}
			callback.callback("1");
		}
	};


	private WVJBWebViewClient.WVJBHandler addToShoppingCartHandler = new WVJBWebViewClient.WVJBHandler(){
		@Override
		public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
			if(data != null){
				String sData = data.toString();
				try {
					JSONArray ja = new JSONArray(sData);
//					List<Model> list = new ArrayList<>();
					HashSet<ProductModel> products = new HashSet<>();
					for (int i=0;i<ja.length();i++){
						JSONObject jo = ja.getJSONObject(i);
						ProductModel productModel = new ProductModel();
						productModel.setId(Long.parseLong(jo.getString("prod_id")));
						productModel.setCategoryId(jo.getInt("category"));
						productModel.setSellerName(jo.getString("shopName"));
						productModel.setSellerId(jo.getInt("shopId"));
						productModel.setProductNum(jo.getInt("num"));
						productModel.setProductPrice(jo.getInt("coupon_price"));
						products.add(productModel);
						Log.e("productModel","---"+i+productModel.toString());
					}

					ShoppingCartManager.getInstance().addToShoppingCart(products);
					Intent intent = new Intent(getActivity(), ContainerActivity.class);
					intent.putExtra(ContainerActivity.KEY_CONTENT,ShoppingCartViewController.class);
//					intent.putExtra(ConfirmOrderViewController.KEY_ORDER_TYPE, ConfirmOrderViewController.ORDER_TYPE_IN_TIME);
					getActivity().startActivity(intent);

				}catch (Exception e){
					e.printStackTrace();
				}
			}
			callback.callback("1");
		}
	};

	class MyWebViewClient extends WVJBWebViewClient {
		public MyWebViewClient(WebView webView) {
			// support js send
			super(webView, new WVJBWebViewClient.WVJBHandler() {
				@Override
				public void request(Object data, WVJBResponseCallback callback) {
					Toast.makeText(ContextProvider.getContext(), "ObjC Received message from JS:" + data, Toast.LENGTH_LONG).show();
					callback.callback("Response for message from ObjC!");
				}
			});
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			setTitle(view.getTitle());
			hideLoading();
			showContent();

			String js = "javascript:" + readAsset("ActionBar.js");
			view.loadUrl(js);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if ("fake:://exit".equals(url)) {
				getActivity().finish();
				return false;
			}
			return ParseHrefUtil.handleURL(getActivity(), url) || super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}

	private ModelStatusListener<ModelEvent, Model> reloadListener = new ModelStatusListener<ModelEvent, Model>() {
		@Override
		public void onModelEvent(ModelEvent event, Model model) {
			if(event == ModelEvent.OnModelUpdate){
				if(webView != null){
					webView.reload();
				}
			}
		}
	};

	public String readAsset(String name) {
		StringBuilder buf = new StringBuilder();
		String result = null;
		InputStream json = null;
		try {
			json = getActivity().getAssets().open(name);
			BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				buf.append(str);
			}
			result = buf.toString();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
		super.onActivityResult(a, requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_SELECT_ADDRESS){
			if(webView != null){
//				webView.reload();

				UserModel current = UserManager.getInstance().getCurrentUser();
				String url = String.format(URLApi.getH5Base() + "/app/score/exchange/addressChoose?user_id=%s&user_type=%d",current.getUserID(),UserModel.USER_TYPE_BUYER);
				webView.loadUrl(url);
			}
		}
	}
}
