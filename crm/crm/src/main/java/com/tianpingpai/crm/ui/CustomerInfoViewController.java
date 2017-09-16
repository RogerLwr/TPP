package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.tianpingpai.adapter.MarketAdapter;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.SelectListAdapter;
import com.tianpingpai.crm.adapter.UserTypeAdapter;
import com.tianpingpai.fragment.QRCodeDisplayViewController;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SelectCityViewController;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
@Statistics(page = "客户资料")
@ActionBar(hidden = true)
@Layout(id = R.layout.view_controller_customer_info)
public class CustomerInfoViewController extends BaseViewController {

	private MarketAdapter marketAdapter = new MarketAdapter();

	public static final String KEY_CUSTOMER = "key.Customer";
	public static final String KEY_IS_NOT_SUBMIT = "key.isNotSubmit";

	//是否能提交
	private boolean isNotSub;

	private long customerId;

    @Binding(id = R.id.add_string_container)
	private LinearLayout addStringContainer;
	@Binding(id = R.id.turnover_spinner)
	private Spinner turnoverSpinner;//日营业额 下拉
	@Binding(id = R.id.purchase_spinner)
	private Spinner purchaseSpinner;//日采购额 下拉
	@Binding(id = R.id.buyer_type_spinner)
	private Spinner buyerTypeSpinner;//业态 下拉
	@Binding(id = R.id.num_edit_text)
	private EditText numEditText;
	@Binding(id = R.id.addressdes_city)
	private TextView addressDesCity;
    @Binding(id = R.id.user_type_text_view)
    private TextView userTypeTextView;
    @Binding(id = R.id.at_area_market_text_view)
	private TextView AtAreaMarketTextView;

	private int areaCityId = 0;

    @Binding(id = R.id.submit_button)
	private View submitButton;
    @Binding(id = R.id.customer_name_edit_text)
	private EditText usernameEditText;
    @Binding(id = R.id.store_address_edit_text)
	private EditText addressEditText;
    @Binding(id = R.id.store_name_edit_text)
	private EditText storeNameEditText;
    @Binding(id = R.id.to_send_amount_edt)
	private EditText toSendAmountEditText;
    @Binding(id = R.id.area_spinner)
	private Spinner marketSpinner;

	private SelectListAdapter turnoverAdapter = new SelectListAdapter();
	private SelectListAdapter purchaseAdapter = new SelectListAdapter();
	private SelectListAdapter buyerTypeAdapter = new SelectListAdapter();


	private CustomerModel customer;
	private int turnoverId;
	private int purchaseId;
	private int buyerTypeId;
	private String seatNum;
	private UserModel user;
    private String toSendString="";

	//客户在骑牛存储的图片地址
	private String netImageUrl;
	//所属商圈的id
	private String marketId;
	private ArrayList<Integer> selectMarketId = new ArrayList<>();
    //选中的可配送商圈
	private ArrayList<Integer> selectId;

	@Override
	public void onActivityCreated(Activity a) {
		super.onActivityCreated(a);
		a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void didSetContentView(Activity a) {
		super.didSetContentView(a);
		hideActionBar();//TODO
	}

    @Binding(id = R.id.sale_code_button)
	private View qrCodeButton;

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		showContent();
		Spinner userTypeSpinner = (Spinner) rootView
				.findViewById(R.id.user_type_spinner);
		UserTypeAdapter ua = new UserTypeAdapter();
        ua.setData(new String[]{"买家", "卖家"});
		userTypeSpinner.setAdapter(ua);

		customer = (CustomerModel) getActivity().getIntent()
				.getSerializableExtra(KEY_CUSTOMER);
		isNotSub = getActivity().getIntent().getBooleanExtra(CustomerInfoViewController.KEY_IS_NOT_SUBMIT,false);

		customerId = customer.getId();
		// 1,是买家   0，是卖家
		if(1==customer.getUserType()){
			getCustomerInfo(1);
		}else if(0==customer.getUserType()){
			getCustomerInfo(0);
		}

		View areaCityContainer = rootView.findViewById(R.id.arae_city_container);
		View turnoverContainer = rootView.findViewById(R.id.turnover_container);
		View purchaseContainer = rootView.findViewById(R.id.purchase_container);
		View buyerTypeContainer = rootView.findViewById(R.id.buyer_type_container);
		View seatNumContainer = rootView.findViewById(R.id.seat_num_container);
		View storePhotoContainer = rootView.findViewById(R.id.store_photo_container);
		View toSendAllDistrictContainer = rootView.findViewById(R.id.to_send_all_district_container);
		View areaContainer = rootView.findViewById(R.id.area_container);
		View toSendAmountLLContainer = rootView.findViewById(R.id.to_send_amount_ll_container);
		LinearLayout AtAreaMarketContainer = (LinearLayout)rootView.findViewById(R.id.at_area_container);

		usernameEditText.setText(customer.getDisplayName());
		storeNameEditText.setText(customer.getSaleName());

		TextView contactEditText = (TextView) rootView
				.findViewById(R.id.contract_number_text_view);
		contactEditText.setText(customer.getPhone());
		addressEditText.setText(customer.getSaleAddress());

		marketSpinner.setAdapter(marketAdapter);
		submitButton.setOnClickListener(submitButtonListener);

		switch (customer.getUserType()){
			case CustomerModel.USER_TYPE_BUYER:
				userTypeTextView.setText("买家");
				turnoverSpinner.setAdapter(turnoverAdapter);
				purchaseSpinner.setAdapter(purchaseAdapter);
				buyerTypeSpinner.setAdapter(buyerTypeAdapter);
				toSendAllDistrictContainer.setVisibility(View.GONE);
				toSendAmountLLContainer.setVisibility(View.GONE);
				AtAreaMarketContainer.setVisibility(View.VISIBLE);

				break;
			case CustomerModel.USER_TYPE_SELLER:
				userTypeTextView.setText("卖家");
				areaCityContainer.setVisibility(View.GONE);
				turnoverContainer.setVisibility(View.GONE);
				purchaseContainer.setVisibility(View.GONE);
				buyerTypeContainer.setVisibility(View.GONE);
				seatNumContainer.setVisibility(View.GONE);
				storePhotoContainer.setVisibility(View.GONE);
				areaContainer.setVisibility(View.GONE);
				addStringContainer.setVisibility(View.VISIBLE);
				AtAreaMarketContainer.setVisibility(View.VISIBLE);
				AtAreaMarketContainer.setClickable(false);
				storeNameEditText.setEnabled(false);
				addressEditText.setEnabled(false);
				break;
		}


		if(isNotSub){
			submitButton.setVisibility(View.GONE);
		}

		qrCodeButton.setOnClickListener(qtCodeButtonListener);

		configureActionBar(rootView);

		if(customer.getUserType() == CustomerModel.USER_TYPE_BUYER){
			userTypeSpinner.setSelection(0);
		}else{
			userTypeSpinner.setSelection(1);
		}

		ArrayList<MarketModel> markets = MarketManager.getInstance().getMarkets() ;
		if(markets != null){
			for(int i = 0;i < markets.size();i++){
				MarketModel m = markets.get(i);
				if(m.getId() == customer.getMarketId()){
					marketSpinner.setSelection(i);
					Log.e("xx","market index =" + i);
					break;
				}
			}
		}
		//TODO userType market selection;
	}
	//卖家的网络请求listener
	private ResultListener<ModelResult<Model>> customerInfoSellerListener = new ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
			if(data.isSuccess()){
				qrCodeButton.setVisibility(View.VISIBLE);
				Log.e("xxx", "273------------model=" + data.getModel());
				Model cm = data.getModel().getModel("customer");

				String tosendamount = data.getModel().get("min_amount", Integer.class) + "";
				toSendAmountEditText.setText(tosendamount);
				AtAreaMarketTextView.setText(data.getModel().get("market",String.class));
				ArrayList<Model> ll = (ArrayList<Model>)data.getModel().getList("markets", Model.class);
				selectId = new ArrayList<>();
				for(int i = 0; i<ll.size(); i++){
					TextView tv = new TextView(getActivity());
					tv.setText(ll.get(i).get("name",String.class));
					selectId.add(ll.get(i).get("id",Integer.class));
					toSendString = toSendString + ll.get(i).get("id",Integer.class)+",";
					addStringContainer.addView(tv);
				}
				if(toSendString.length()>0){
					toSendString = toSendString.substring(0,toSendString.length()-1);
					Log.e("------======----",toSendString);
				}

				selectMarketId.add(cm.getInt("market_id"));


			} else {
				Toast.makeText(ContextProvider.getContext(), "错误:" + data.getDesc(),
						Toast.LENGTH_SHORT).show();
			}
			hideLoading();
		}
	};


	//买家的网络请求监听
	private ResultListener<ModelResult<Model>> customerInfoBuyerListener = new ResultListener<ModelResult<Model>>(){
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
			if(data.isSuccess()){
				Model cm = data.getModel();
				if(null!=cm){
					numEditText.setText(cm.getInt("seats")+"");
					addressDesCity.setText(cm.getString("regionName"));
					areaCityId = cm.getInt("region");
					netImageUrl = cm.getString("photo");
					com.tianpingpai.http.util.ImageLoader.load(netImageUrl, customerImageView);
					clearImageButton.setVisibility(View.VISIBLE);
					imageUrl = getOldImageKey(netImageUrl);
					loadSelectedList();
					AtAreaMarketTextView.setText(cm.getString("market_name"));
					selectMarketId.add(cm.getInt("market_id"));
					marketId = cm.getInt("market_id")+"";
					turnoverId = cm.getInt("sales");
					purchaseId = cm.getInt("purchase");
					buyerTypeId = cm.getInt("categoryId");
				}
			}else{
				Toast.makeText(ContextProvider.getContext(), "错误:" + data.getDesc(),
						Toast.LENGTH_SHORT).show();
			}
			hideLoading();
		}
	};


	private void getCustomerInfo(int i){
		showLoading();
		String url = URLApi.getBaseUrl() + "/crm/customer/getCustomerInfo";
		if(i==0){
			//卖家
			HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, customerInfoSellerListener);
			GenericModelParser parser = new GenericModelParser();
			req.addParam("id", customerId + "");
			req.setParser(parser);
//			req.setErrorListener(new CommonErrorHandler(this));
			VolleyDispatcher.getInstance().dispatch(req);
		}else if(i==1){
			//买家
			HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, customerInfoBuyerListener);
//			ModelParser<CustomerModel> parser = new ModelParser<>(CustomerModel.class);
			GenericModelParser parser = new GenericModelParser();
			req.addParam("id", customerId + "");
			req.setParser(parser);
//			req.setErrorListener(new CommonErrorHandler(this));
			VolleyDispatcher.getInstance().dispatch(req);

		}
	}

	private void loadSelectedList() {
		String url = ContextProvider.getBaseURL() + "/crm/customer/getSelectedList";
		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, selectListener);
		req.setParser(new GenericModelParser());
		VolleyDispatcher.getInstance().dispatch(req);
	}

	private ResultListener<ModelResult<Model>> selectListener = new ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
		if (data.isSuccess()) {
			Model model = data.getModel();
			Log.e("xx", "277----------model=" + model);
			List<Model> turnoverModels = model.getList("1", Model.class);
			turnoverAdapter.setModels(turnoverModels);
//			int turnoverId = customerModel.getSales();
			Log.e("xx", "212------turnoverId = " + turnoverId);
			for(int i = 0; i<turnoverModels.size();i++){
				if(turnoverModels.get(i).getInt("id") == turnoverId){
					turnoverSpinner.setSelection(i);
					break;
				}
			}
			turnoverAdapter.notifyDataSetChanged();

			List<Model> purchaseModels =  model.getList("2", Model.class);
			purchaseAdapter.setModels(purchaseModels);
//			int purchaseId = customerModel.getPurchase();
			for(int i = 0; i<purchaseModels.size();i++){
				if(purchaseModels.get(i).getInt("id") == purchaseId){
					purchaseSpinner.setSelection(i);
					break;
				}
			}

			purchaseAdapter.notifyDataSetChanged();

			List<Model> buyerTypeModels =  model.getList("3", Model.class);
			buyerTypeAdapter.setModels(buyerTypeModels);
//			int buyerTypeId = customerModel.getCategoryId();
			for(int i = 0; i<buyerTypeModels.size();i++){
				if(buyerTypeModels.get(i).getInt("id") == buyerTypeId){
					buyerTypeSpinner.setSelection(i);
					break;
				}
			}
			buyerTypeAdapter.notifyDataSetChanged();

		} else {
			Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_LONG).show();
		}
		}
	};

	private ResultListener<ModelResult<Void>> updateListener =  new ResultListener<ModelResult<Void>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Void>> request,
							 ModelResult<Void> data) {
			hideLoading();
			if(data.isSuccess()){
				deleteOld();
				Toast.makeText(ContextProvider.getContext(), "修改成功！", Toast.LENGTH_SHORT).show();
//				CustomerManager.getInstance().notifyEvent(CustomerEvent.CustomerInfoUpdate, request.getAttachment(CustomerModel.class));
				getActivity().finish();
			}else{
				Log.e("error====",data.getDesc());
				Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
			}
		}
	};

	private OnClickListener submitButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if(isNotSub){
				Toast.makeText(getActivity().getApplicationContext(),"不可提交",Toast.LENGTH_SHORT).show();
				return;
			}
			user = UserManager.getInstance().getCurrentUser();

			if(user == null){
				Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
				return;
			}

			if(customer.getUserType() == CustomerModel.USER_TYPE_BUYER){
				if(areaCityId == 0){
					Toast.makeText(ContextProvider.getContext(), R.string.no_arae_city_selected, Toast.LENGTH_SHORT).show();
					return;
				}

				Model turnoverModel = turnoverAdapter.getItem(turnoverSpinner.getSelectedItemPosition());
				if(turnoverModel == null){
					Toast.makeText(ContextProvider.getContext(), R.string.no_turnover_selected, Toast.LENGTH_SHORT).show();
					return;
				}else {
					turnoverId = turnoverModel.getInt("id");
					Log.e("xx", "435-------id=" + turnoverId);
				}
				Model purchaseModel = purchaseAdapter.getItem(purchaseSpinner.getSelectedItemPosition());
				if(purchaseModel == null){
					Toast.makeText(ContextProvider.getContext(), R.string.no_purchase_selected, Toast.LENGTH_SHORT).show();
					return;
				}else {
					purchaseId = purchaseModel.getInt("id");
				}
				Model buyerTypeModel = buyerTypeAdapter.getItem(buyerTypeSpinner.getSelectedItemPosition());
				if(buyerTypeModel == null){
					Toast.makeText(ContextProvider.getContext(), R.string.no_buyer_type_selected, Toast.LENGTH_SHORT).show();
					return;
				}else {
					buyerTypeId = buyerTypeModel.getInt("id");
				}

				seatNum = numEditText.getText().toString().trim();
				if (TextUtils.isEmpty(seatNum)){
					Toast.makeText(ContextProvider.getContext(), R.string.no_num_seat_selected, Toast.LENGTH_SHORT).show();
				}
				if (localImagePath != null) {
					getQiNiuToken();
				}else if(imageUrl != null && !TextUtils.isEmpty(imageUrl)){
					submitCustomer();
				} else {
					Toast.makeText(ContextProvider.getContext(), R.string.no_image_selected, Toast.LENGTH_SHORT).show();
				}
			}else
				submitCustomer();

		}
	};

	private void submitCustomer(){

		HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.Customer.updateInfo(), updateListener);
		req.setMethod(HttpRequest.POST);
		if(customer.getUserType() == CustomerModel.USER_TYPE_BUYER){
            MarketModel marget = marketAdapter.getItem(marketSpinner.getSelectedItemPosition());
			req.addParam("region", ""+areaCityId);
			req.addParam("sales", ""+ turnoverId);
			req.addParam("purchase", ""+ purchaseId);
			req.addParam("buyer_category", buyerTypeId + "");
			req.addParam("seats", ""+ seatNum);
			req.addParam("market_id", marketId);
			if ( TextUtils.isEmpty(imageUrl) ) {
				req.addParam("photo", imageUrl);
			} else {
				req.addParam("photo", imageUrl);
			}

			try {
				CustomerModel customerCopy = customer.clone();
				customerCopy.setDisplayName(usernameEditText.getText().toString());
				customerCopy.setSaleName(storeNameEditText.getText().toString());
				customerCopy.setSaleAddress(addressEditText.getText().toString());
				customerCopy.setMarketId(marget.getId());
				req.setAttachment(customerCopy);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}else{
			req.addParam("min_amount",toSendAmountEditText.getText().toString());
			req.addParam("markets",toSendString);
			req.addParam("market_id",null);
		}
		req.addParam("id", customer.getId() + "");
		req.addParam("display_name", usernameEditText.getText().toString());
		req.addParam("sale_name", storeNameEditText.getText().toString());
		req.addParam("sale_address", addressEditText.getText().toString());
		req.addParam("accessToken", user.getAccessToken());

		ModelParser<Void> parser = new ModelParser<>(Void.class);
		req.setParser(parser);
		showLoading();
		VolleyDispatcher.getInstance().dispatch(req);
	}

	private OnClickListener qtCodeButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String url = ContextProvider.getBaseURL() + "/m/prod/homepage?user_id=" + customerId;
			Intent intent = new Intent(getActivity(), ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT,QRCodeDisplayViewController.class);
			intent.putExtra(QRCodeDisplayViewController.KEY_TEXT, url);
			getActivity().startActivity(intent);
		}
	};

	private ActionSheet actionSheet;
	SelectCityViewController selectCityViewController;
	private SelectCityViewController.OnSelectCityListener onSelectCityListener = new SelectCityViewController.OnSelectCityListener() {
		@Override
		public void onSelectCity(Model model) {
			addressDesCity.setText(model.getString("name"));
			areaCityId = model.getInt("area_id");
		}
	};
	@OnClick(R.id.addressdes_city)
	private View.OnClickListener onCityClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			if(actionSheet == null) {
				actionSheet = new ActionSheet();
				actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
				actionSheet.setHeight(DimensionUtil.dip2px(300));
				selectCityViewController = new SelectCityViewController();
				selectCityViewController.setActionSheet(actionSheet);
				selectCityViewController.setActivity(getActivity());
				selectCityViewController.setOnSelectCityListener(onSelectCityListener);
				actionSheet.setViewController(selectCityViewController);
			}
			actionSheet.show();

			hideKeyboard();
		}
	};

	@Override
	public boolean onBackKeyDown(Activity a) {
		if (actionSheet != null && actionSheet.isShowing()){
			actionSheet.dismiss();
			return true;
		}
		return super.onBackKeyDown(a);
	}

	// 截图功能
	private String localImagePath;
	private String imageUrl;
	private String oldImageKey;
	private boolean isDelete = false;
	public static final int REQUEST_CODE_PICK_IMAGE = 12;//TODO
	public static final int REQUEST_CODE_CROP_IMAGE = 13;//TODO

	@Binding(id = R.id.clear_image_button)
	private View clearImageButton;

	@Binding(id = R.id.customer_image_button)
	private ImageView customerImageView;

	@OnClick(R.id.clear_image_button)
	private View.OnClickListener clearButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 有老图片 或者 新选择的图片
			if (!TextUtils.isEmpty(oldImageKey) || !TextUtils.isEmpty(localImagePath)) {
				final ActionSheetDialog dialog = new ActionSheetDialog();
				dialog.setActivity(getActivity());
				dialog.setActionSheet(new ActionSheet());
				dialog.setTitle("您确定要删除图片吗?");
				dialog.setPositiveButtonListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						isDelete = true;
						imageUrl = null;
						localImagePath = null;
						customerImageView.setImageResource(R.drawable.ic_151020_add_prod_img);
						clearImageButton.setVisibility(View.INVISIBLE);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		}
	};

	@OnClick(R.id.customer_image_button)
	private View.OnClickListener pickImageListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			getActivity().startActivityForResult(createDefaultOpenableIntent(), REQUEST_CODE_PICK_IMAGE);
		}
	};

	private Intent createDefaultOpenableIntent() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.putExtra(Intent.EXTRA_TITLE, "选择文件");
		i.setType("*/*");

		Intent chooser = createChooserIntent(createCameraIntent());
		chooser.putExtra(Intent.EXTRA_INTENT, i);
		return chooser;
	}

	private Intent createChooserIntent(Intent... intents) {
		Intent chooser = new Intent(Intent.ACTION_CHOOSER);
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
		chooser.putExtra(Intent.EXTRA_TITLE, "选择文件");
		return chooser;
	}

	Uri camUri;

	private Intent createCameraIntent() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File cameraDataDir = new File(externalDataDir.getAbsolutePath() + File.separator + "e-photos");
		cameraDataDir.mkdirs();
		String mPhotoFilePath = cameraDataDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
		File f = new File(mPhotoFilePath);
		camUri = Uri.fromFile(f);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoFilePath)));
		return cameraIntent;
	}


	private void beginCrop(Uri source) {
		Uri destination = Uri.fromFile(new File(ContextProvider.getContext().getCacheDir(), "cropped"));
		Crop.of(source, destination).asSquare().withMaxSize(320, 320).start(getActivity(), REQUEST_CODE_CROP_IMAGE);
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == Activity.RESULT_OK) {
			customerImageView.setImageURI(Crop.getOutput(result));
			//TODO
			localImagePath = new File(ContextProvider.getContext().getCacheDir(), "cropped").getAbsolutePath();
			clearImageButton.setVisibility(View.VISIBLE);
//			if (customerModel != null) {
//				isDelete = !TextUtils.isEmpty(customerModel.getImageUrl());
			if(null==netImageUrl){
				isDelete = true;
			} else {
				isDelete = false;
			}
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(ContextProvider.getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityResult(Activity a,int requestCode, int resultCode, Intent data) {
		super.onActivityResult(a,requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_PICK_IMAGE) {
			if (data != null) {
				camUri = data.getData();
			}
			beginCrop(camUri);
		} else if (requestCode == REQUEST_CODE_CROP_IMAGE) {
			handleCrop(resultCode, data);
		}
	}

	private HttpRequest.ResultListener<ModelResult<String>> qiNiuTokenListener = new HttpRequest.ResultListener<ModelResult<String>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<String>> request, ModelResult<String> data) {
			if (data.isSuccess()) {
				uploadImage(data.getModel());
			} else {
				setEnabled(true);
				if (data.getCode() == HttpResult.CODE_AUTH) {
					if (getActivity() != null) {
						Intent intent = new Intent(getActivity(), ContainerActivity.class);
						intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
						getActivity().startActivity(intent);
					} else {
						Toast.makeText(ContextProvider.getContext(), "请登录后重试!", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(ContextProvider.getContext(), "上传图片失败!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private void getQiNiuToken() {
		HttpRequest<ModelResult<String>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/image/token", qiNiuTokenListener);
		req.setParser(new ModelParser<>(String.class));
		setEnabled(false);
		req.setErrorListener(new HttpRequest.ErrorListener() {
			@Override
			public void onError(HttpRequest<?> request, HttpError error) {
				Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_LONG).show();
				setEnabled(true);
			}
		});
		VolleyDispatcher.getInstance().dispatch(req);
	}

	private void setEnabled(boolean enabled) {
		submitButton.setEnabled(enabled);
		if (enabled) {
			hideLoading();
		} else {
//			setIndicatorCancelable(false);
			showLoading();
		}
	}

	private void uploadImage(String token) {
		UserModel user = UserManager.getInstance().getCurrentUser();
		if (user == null) {
			Toast.makeText(ContextProvider.getContext(), "登录后请重试!", Toast.LENGTH_LONG).show();
			setEnabled(true);
			return;
		}
		Toast.makeText(ContextProvider.getContext(), "正在上传图片,请稍候!", Toast.LENGTH_SHORT).show();
		String curTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE).format(new Date());
		String mNewImageKey = "crm/buyer/" + user.getId() + "/detail/" + curTime + ".png";
		//		}
		Map<String, String> params = new HashMap<>();
		params.put("seller:userTel", "" + user.getPhone());
		final UploadOptions opt = new UploadOptions(params, null, true, null, null);
		UploadManager uploadManager = new UploadManager();
		uploadManager.put(localImagePath, mNewImageKey, token, new UpCompletionHandler() {
			public void complete(String key, ResponseInfo info, JSONObject response) {
				if (info.isOK()) {
					imageUrl = key;
					Toast.makeText(ContextProvider.getContext(), "上传图片成功", Toast.LENGTH_SHORT).show();
					submitCustomer();
				} else {
					String error = info.error;
					if (error.equals("expired token")) {
						getQiNiuToken();//TODO
					} else {
						setEnabled(true);
						Toast.makeText(ContextProvider.getContext(), "上传图片错误:" + error, Toast.LENGTH_LONG).show();
					}
				}
			}
		}, opt);
	}

	private HttpRequest.ResultListener<ModelResult<Void>> deleteListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
			//TODO
		}
	};

	private void deleteOld() {
		if (oldImageKey == null || imageUrl == null) {
			return;
		}
		//TODO change to post request
		if (isDelete) {
			String url = ContextProvider.getBaseURL() + "/api/image/delete";
			HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, deleteListener);
//			req.addParam("key", getOldImageKey(customerModel));
			req.addParam("key",oldImageKey);
			VolleyDispatcher.getInstance().dispatch(req);
		}
	}

	public java.lang.String getOldImageKey(String image) {
		if (image != null) {
			String[] keys = image.split(".com/");
			oldImageKey = keys[keys.length - 1];
		} else {
			oldImageKey = "";
		}
		return oldImageKey;
	}

	@OnClick(R.id.to_send_all_district)
	private View.OnClickListener toSendAllDistrictOnClickListener = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			ActionSheet toSendAllActionSheet = new ActionSheet();
			toSendAllActionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			toSendAllActionSheet.setHeight(getView().getHeight());
			SelectAreaViewController selectAreaViewController = new SelectAreaViewController();
			selectAreaViewController.setIsOnly(false);
			selectAreaViewController.setSelectedId(selectId);
			selectAreaViewController.setActionSheet(toSendAllActionSheet);
			selectAreaViewController.setActivity(getActivity());
			selectAreaViewController.setOnSelectAreaListener(onSelectAreaListener);
			toSendAllActionSheet.setViewController(selectAreaViewController);
			toSendAllActionSheet.show();
		}
	};

	private SelectAreaViewController.OnSelectAreaListener onSelectAreaListener = new SelectAreaViewController.OnSelectAreaListener(){

		@Override
		public void onSelectArea(HashMap<Integer ,String> hm) {
			toSendString = "";

			selectId.clear();
			addStringContainer.removeAllViews();
			Iterator<Map.Entry<Integer ,String>> iter = hm.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer,String> entry = (Map.Entry) iter.next();
//				Integer key = entry.getKey();
//				String val = entry.getValue();
//				toSendString = toSendString+entry.getValue()+",";
//				sendlist.add(entry.getValue());
				String s = entry.getValue();
				String []s1 = s.split("abc");
				toSendString = toSendString+s1[1]+",";
				TextView tv = new TextView(getActivity());
				tv.setText(s1[0]);
				addStringContainer.addView(tv);
				selectId.add(Integer.parseInt(s1[1]));
			}
			if(toSendString.length()>0){
				toSendString = toSendString.substring(0,toSendString.length()-1);
			}

		}
	};

	@OnClick(R.id.at_area_container)
	private View.OnClickListener atMarketIdSelectOnClickListener = new View.OnClickListener(){
		@Override
		public void onClick(View view) {
			ActionSheet atAreaActionSheet = new ActionSheet();
			atAreaActionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			atAreaActionSheet.setHeight(getView().getHeight());
			SelectAreaViewController selectMarketViewController = new SelectAreaViewController();
			selectMarketViewController.setIsOnly(true);
			selectMarketViewController.setSelectedId(selectMarketId);
			selectMarketViewController.setActionSheet(atAreaActionSheet);
			selectMarketViewController.setActivity(getActivity());
			selectMarketViewController.setOnSelectAreaListener(onSelectmarketListener);
			atAreaActionSheet.setViewController(selectMarketViewController);
			atAreaActionSheet.show(getActivity().getSupportFragmentManager(), "");
		}
	};

	private SelectAreaViewController.OnSelectAreaListener onSelectmarketListener = new SelectAreaViewController.OnSelectAreaListener(){
		@Override
		public void onSelectArea(HashMap<Integer, String> selections) {

			selectMarketId.clear();
			Iterator<Map.Entry<Integer ,String>> iter = selections.entrySet().iterator();
			while (iter.hasNext()){
				Map.Entry<Integer,String> entry = (Map.Entry) iter.next();
				String s = entry.getValue();
				String [] ss = s.split("abc");
				selectMarketId.add(Integer.parseInt(ss[1]));
				marketId = ss[1];
				AtAreaMarketTextView.setText(ss[0]);

			}

		}
	};
}
