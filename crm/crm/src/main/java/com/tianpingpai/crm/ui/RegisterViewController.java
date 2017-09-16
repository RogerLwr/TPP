package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CategoryInfo;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.AgreementViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.MobileUtil;
import com.tianpingpai.widget.SelectCityViewController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@Statistics(page = "注册卖家")
@ActionBar(title = "注册卖家")
@Layout(id = R.layout.fragment_register)
public class RegisterViewController extends CrmBaseViewController {

	private static final String TAG = "RegisterFragment";

	public static final String KEY_CHECK = "is_check";
	public static final String KEY_EDIT = "not_edit";
	public static final String KEY_USER_ID = "key.userId";

	@Binding(id = R.id.phone_edt)
	private EditText phoneEditText;
	@Binding(id = R.id.name_edt)
	private EditText nameEditText;
	@Binding(id = R.id.password_edt)
	private EditText passwordEditText;
	@Binding(id = R.id.again_password_edt)
	private EditText againPasswordEditText;
	@Binding(id = R.id.store_name_edt)
	private EditText storeNameEditText;
	@Binding(id = R.id.store_address_edt)
	private EditText storeAddressEditText;

	@Binding(id = R.id.at_area_tv)
	private TextView atAreaTextView;
	@Binding(id = R.id.at_market_tv)
	private TextView atMarketTextView;
	@Binding(id = R.id.main_category_tv)
	private TextView mainCategoryTextView;
	@Binding(id = R.id.operate_category_tv)
	private TextView operateCategoryTextView;

	@Binding(id = R.id.distribution_edt)
	private EditText distributionEditText;
	@Binding(id = R.id.min_distribution_edt)
	private EditText minDistributionEditText;

	@Binding(id = R.id.shop_describe_tv)
	private TextView shopDescribeTextView;
	@Binding(id = R.id.distribution_describe_tv)
	private TextView distributionDescribeTextView;

	@Binding(id = R.id.supplier_type_radio_group)
	private RadioGroup supplierTypeRadioGroup;
	@Binding(id = R.id.one_supplier_button)
	private RadioButton oneSupplierRadioGroup;
	@Binding(id = R.id.three_supplier_button)
	private RadioButton threeSupplierRadioGroup;
	@Binding(id = R.id.add_market_button)
	private ImageView add_market_button;
	@Binding(id = R.id.distribution_market_container)
	private LinearLayout distributionMarketContainer;
	@Binding(id = R.id.btn_register_submit)
	private Button submitButton;


	private int atAreaId = -1;
	private int atMarketId = -1;

	private String category = "";

	private int grade = 0;

	private ArrayList<Integer> operateCategoryIds = new ArrayList<>();
	private ArrayList<Integer> mainCategoryIds = new ArrayList<>();

	//已选择的可配送商圈
	private ArrayList<Integer> toSendSelectIds = new ArrayList<>();
	private String toSendString="";

	//已选择的所在商圈
	private ArrayList<Integer> selectMarketId = new ArrayList<>();
	private String marketId;

	private ActionSheet actionSheet = new ActionSheet();

	private boolean isCheck;
	private boolean notEdit;

	private String userId;
	
	public void onActivityCreated(android.app.Activity a) {
		super.onActivityCreated(a);
	}


	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		isCheck = getActivity().getIntent().getBooleanExtra(KEY_CHECK,false);
		notEdit = getActivity().getIntent().getBooleanExtra(KEY_EDIT,false);
		userId = getActivity().getIntent().getStringExtra(KEY_USER_ID);
		supplierTypeRadioGroup.setOnCheckedChangeListener(supplierListener);
		if(isCheck){
//			hideKeyboard();
			getRegisterData(userId);
		}else{
			supplierTypeRadioGroup.check(R.id.three_supplier_button);
		}
	}

	private void getRegisterData(String id){

		String url = URLApi.getBaseUrl() + "/crm/customer/get_register_saler_info";
		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,upDataListener);
		req.setParser(new GenericModelParser());
		req.addParam("user_id", id);
		VolleyDispatcher.getInstance().dispatch(req);
		showLoading();

	}

	private HttpRequest.ResultListener<ModelResult<Model>> upDataListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {

			hideLoading();

			if(1 == data.getCode()){
				UserManager.getInstance().loginExpired(getActivity());
				return;
			}
			if(data.isSuccess()){
				setUserData(data);
			}else {
				Toast.makeText(getActivity(),data.getDesc(),Toast.LENGTH_SHORT).show();
				showEmptyView();
			}
		}
	};

	private void setUserData(ModelResult<Model> data){

		hideKeyboard();
		Model customerModel = data.getModel();

		userId = customerModel.getInt("user_id")+"";
		phoneEditText.setText(customerModel.getString("phone"));
		nameEditText.setText(customerModel.getString("display_name"));
		storeNameEditText.setText(customerModel.getString("sale_name"));
		storeAddressEditText.setText(customerModel.getString("address"));
		atAreaTextView.setText(customerModel.getString("area"));
		atAreaId = customerModel.getInt("area_id");
		atMarketTextView.setText(customerModel.getString("market_name"));
		selectMarketId.add(customerModel.getInt("market_id"));
		marketId = customerModel.getInt("market_id")+"";
		mainCategoryTextView.setText(customerModel.getString("category_name"));
//		mainCategoryId = customerModel.getString("category_id");
		mainCategoryIds.add(Integer.parseInt(customerModel.getString("category_id")));
		category = customerModel.getString("category_name");
		List<Model> list = customerModel.getList("categorys",Model.class);
		String operateStringText = "";
		if(list !=null){
			for (Model m:list){
				operateStringText = operateStringText + m.getString("name") + ",";
//			operateCategoryIds = operateCategoryIds + m.getInt("category_id") + ",";
				operateCategoryIds.add(m.getInt("category_id"));
			}
		}
		if(operateStringText.length()>0){
			operateStringText = operateStringText.substring(0,operateStringText.length()-1);
		}
		operateCategoryTextView.setText(operateStringText);
//		operateCategoryIds = operateCategoryIds.substring(0,operateCategoryIds.length()-1);

		distributionEditText.setText(customerModel.getInt("freight")+"");
		minDistributionEditText.setText(customerModel.getInt("minAmount")+"");

		shopDescribeTextView.setText(customerModel.getString("description"));
		distributionDescribeTextView.setText(customerModel.getString("delivery_message"));

		grade = customerModel.getInt("grade");
		if(0 == grade){
			supplierTypeRadioGroup.check(R.id.three_supplier_button);
		}else if(1 == grade){
			supplierTypeRadioGroup.check(R.id.one_supplier_button);
		}

		List<Model> toSendMarkets = customerModel.getList("marketList",Model.class);

		boolean isHave = false;
		if(toSendMarkets !=null){
			for (Model m:toSendMarkets){
				toSendString = toSendString + m.getInt("id") + ",";
				toSendSelectIds.add(m.getInt("id"));
				if(distributionMarketContainer.getChildCount() < 5){
					TextView tv = new TextView(getActivity());
					tv.setPadding(30,0,0,0);
					tv.setTextSize(14);
					tv.setText(m.getString("name"));
					distributionMarketContainer.addView(tv);
				}else{
					if(!isHave){
						TextView last = new TextView(getActivity());
						last.setPadding(30, 0, 0,0);
						last.setTextSize(14);
						last.setText("......");
						distributionMarketContainer.addView(last);
						isHave = true;
					}
				}
			}
		}

		if(toSendString.length()>0){
			toSendString = toSendString.substring(0,toSendString.length()-1);
		}
		if(notEdit){
			submitButton.setVisibility(View.GONE);
		}else{

		}
	}

	private RadioGroup.OnCheckedChangeListener supplierListener = new RadioGroup.OnCheckedChangeListener(){
		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int i) {
			if(i == R.id.one_supplier_button){
				grade = 1;
			}else if(i == R.id.three_supplier_button){
				grade = 0;
			}
		}
	};

	@OnClick(R.id.at_area_tv)
	private View.OnClickListener onCityClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			hideKeyboard();
//			ActionSheet actionSheet = new ActionSheet();
			final SelectCityViewController selectCityViewController = new SelectCityViewController();
			selectCityViewController.setActivity(getActivity());
			selectCityViewController.setActionSheet(actionSheet);
			selectCityViewController.setOnSelectCityListener(new SelectCityViewController.OnSelectCityListener() {
				@Override
				public void onSelectCity(Model model) {
					atAreaTextView.setText(selectCityViewController.getSelectedCity().getString("name")+"-"+model.getString("name"));
					atAreaId = model.getInt("area_id");
					Log.e("xx", "201---------------city="+atAreaTextView.getText().toString()+",ared_id="+atAreaId);
					atAreaTextView.setTag(atAreaId);
				}
			});
			actionSheet.setViewController(selectCityViewController);
			actionSheet.show();
		}
	};

	@OnClick(R.id.operate_category_tv)
	private View.OnClickListener onCategoryClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			hideKeyboard();
			actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			actionSheet.setHeight(getView().getHeight());
			SelectCategoryViewController selectCategoryViewController = new SelectCategoryViewController();
			selectCategoryViewController.setActivity(getActivity());
			selectCategoryViewController.setActionSheet(actionSheet);
			selectCategoryViewController.setTitle("请选择经营品类");
			selectCategoryViewController.setOnly(false);
			selectCategoryViewController.setSelectedCategory(operateCategoryIds);
			selectCategoryViewController.setOnSelectCategoryListener(new SelectCategoryViewController.OnSelectCategoryListener() {
				@Override
				public void onSelectCategory(HashMap<Integer, String> selections) {
					operateCategoryIds.clear();
					String operateCategoryString = "";
					for (Integer key : selections.keySet()) {
						String s = selections.get(key);
						String[] ss = s.split("abc");
						operateCategoryIds.add(Integer.parseInt(ss[1]));
//						operateCategoryTextView.setText(ss[0]);
						operateCategoryString = operateCategoryString + ss[0];
					}
					operateCategoryTextView.setText(operateCategoryString.substring(0,operateCategoryString.length()-1));
				}
			});
			actionSheet.setViewController(selectCategoryViewController);
			actionSheet.show();

		}
	};

	@OnClick(R.id.main_category_tv)
	private View.OnClickListener onMainCategoryClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			hideKeyboard();
			actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			actionSheet.setHeight(getView().getHeight());
			SelectCategoryViewController selectCategoryViewController = new SelectCategoryViewController();
			selectCategoryViewController.setActivity(getActivity());
			selectCategoryViewController.setActionSheet(actionSheet);
			selectCategoryViewController.setTitle("请选择主营品类");
			selectCategoryViewController.setOnly(true);
			selectCategoryViewController.setSelectedCategory(mainCategoryIds);
			selectCategoryViewController.setOnSelectCategoryListener(new SelectCategoryViewController.OnSelectCategoryListener(){
				@Override
				public void onSelectCategory(HashMap<Integer, String> selections) {
					mainCategoryIds.clear();
					for (Integer key : selections.keySet()) {
						String s = selections.get(key);
						String[] ss = s.split("abc");
						mainCategoryIds.add(Integer.parseInt(ss[1]));
						mainCategoryTextView.setText(ss[0]);
					}
				}
			});
			actionSheet.setViewController(selectCategoryViewController);
			actionSheet.show();

		}
	};

	@OnClick(R.id.btn_register_submit)
	private View.OnClickListener onRegisterClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			registerUser();
		}

	};

	/**
	 * 注册用户
	 */
	private void registerUser() {

		String phone = phoneEditText.getText().toString().trim();
		String display_name = nameEditText.getText().toString().trim();
		String pwd = passwordEditText.getText().toString().trim();
		String pwd_2 = againPasswordEditText.getText().toString().trim();

		String sale_name = storeNameEditText.getText().toString().trim();
		String shopAddress = storeAddressEditText.getText().toString().trim();

		String freight = distributionEditText.getText().toString().trim();
		String minAmount = minDistributionEditText.getText().toString().trim();

		String storeDescribe = shopDescribeTextView.getText().toString().trim();
		String deliveryMessage = distributionDescribeTextView.getText().toString().trim();

		if (!MobileUtil.isMoblieNum(phone)) {
			Toast.makeText(getActivity(), "请填写正确手机号码!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(display_name)) {
			Toast.makeText(getActivity(), "用户名不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!MobileUtil.isPWDNum(pwd)) {
			Toast.makeText(getActivity(), "请填写6位的密码！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!pwd.equals(pwd_2)) {
			Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(sale_name)) {
			Toast.makeText(getActivity(), "店铺名称不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(shopAddress)) {
			Toast.makeText(getActivity(), "商铺地址不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (atAreaId == -1) {
			Toast.makeText(getActivity(), "您还未选择所在区域", Toast.LENGTH_SHORT).show();
			return;
		}
		if ("".equals(marketId) || null == marketId) {
			Toast.makeText(getActivity(), "请选择所属商圈", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mainCategoryIds.isEmpty()) {
			Toast.makeText(getActivity(), "请选择一个主营品类", Toast.LENGTH_SHORT).show();
			return;
		}
		if (operateCategoryIds.isEmpty()) {
			Toast.makeText(getActivity(), "请至少选择一个经营品类", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(freight)) {
			Toast.makeText(getActivity(), "请填写配送费", Toast.LENGTH_SHORT).show();
			return;
		}

		if (TextUtils.isEmpty(minAmount)) {
			Toast.makeText(getActivity(), "请填写免配送费最低总价", Toast.LENGTH_SHORT).show();
			return;
		}

//		if(TextUtils.isEmpty(storeDescribe)){
//			Toast.makeText(getActivity(),"请填写店铺描述",Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if(TextUtils.isEmpty(deliveryMessage)){
//			Toast.makeText(getActivity(),"请填写配送说明",Toast.LENGTH_SHORT).show();
//			return;
//		}
		if (-1 == grade) {
			Toast.makeText(getActivity(), " 请选择供应商级别", Toast.LENGTH_SHORT).show();
			return;
		}

		if ("".equals(toSendString) || null == toSendString) {
			Toast.makeText(getActivity(), "请选择可配送商圈", Toast.LENGTH_SHORT).show();
			return;
		}

		String operateCategoryIdsParam = "";
		for (int i :operateCategoryIds){
			operateCategoryIdsParam = operateCategoryIdsParam + i + ",";
		}
		operateCategoryIdsParam = operateCategoryIdsParam.substring(0,operateCategoryIdsParam.length()-1);

		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(
				URLApi.Customer.register, registerListener);
		ModelParser<Model> parser = new ModelParser<>(Model.class);
		req.setParser(parser);
		req.setMethod(HttpRequest.POST);
		if (isCheck) {
			req.addParam("user_id", userId);
		}
		req.addParam("phone", phone);
		req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
				.getAccessToken());
		req.addParam("password", pwd);
		req.addParam("display_name", display_name);
		req.addParam("sale_name", sale_name);
		req.addParam("address", shopAddress);
		req.addParam("area_id", String.valueOf(atAreaId));
		req.addParam("category_id", mainCategoryIds.get(0)+"");
		req.addParam("category", category);
		req.addParam("category_ids", operateCategoryIdsParam);
		req.addParam("market_id", "" + marketId);//ky
		req.addParam("freight", freight);
		req.addParam("description", "" + storeDescribe);
		req.addParam("delivery_message", "" + deliveryMessage);
		req.addParam("minAmount", minAmount);//ky
		req.addParam("grade", "" + grade);
		req.addParam("markets", toSendString);
		req.addParam("marketer_id", UserManager.getInstance().getCurrentUser().getMarketerId() + "");

		VolleyDispatcher.getInstance().dispatch(req);
		showLoading();

	}

	private HttpRequest.ResultListener<ModelResult<Model>> registerListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request,
							 ModelResult<Model> data) {
			hideLoading();
			Log.e("xx", "data" + data.getModel());
			if (data.isSuccess()) {
				Toast.makeText(ContextProvider.getContext(), "注册成功",
						Toast.LENGTH_SHORT).show();
				getActivity().finish();
				ModelManager.getModelInstance().notifyEvent(ModelEvent.OnModelUpdate, new Model());
			} else {
				Toast.makeText(ContextProvider.getContext(), "注册失败:"+data.getDesc() ,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@OnClick(R.id.add_market_button)
	private View.OnClickListener toSendAllDistrictOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
//			ActionSheet toSendAllActionSheet = new ActionSheet();
			actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			actionSheet.setHeight(getView().getHeight());
			SelectAreaViewController selectAreaViewController = new SelectAreaViewController();
			selectAreaViewController.setIsOnly(false);
			selectAreaViewController.setSelectedId(toSendSelectIds);
			selectAreaViewController.setActionSheet(actionSheet);
			selectAreaViewController.setActivity(getActivity());
			selectAreaViewController.setOnSelectAreaListener(onSelectAreaListener);
			actionSheet.setViewController(selectAreaViewController);
			actionSheet.show();

		}
	};

	private SelectAreaViewController.OnSelectAreaListener onSelectAreaListener = new SelectAreaViewController.OnSelectAreaListener() {

		@Override
		public void onSelectArea(HashMap<Integer, String> hm) {
			boolean isHave = false;
			toSendString = "";
			toSendSelectIds.clear();
			distributionMarketContainer.removeAllViews();

			for (Integer key : hm.keySet()) {
				String s = hm.get(key);
				String[] s1 = s.split("abc");
				toSendString = toSendString + s1[1] + ",";
				if (distributionMarketContainer.getChildCount() < 5) {
					TextView tv = new TextView(getActivity());
					tv.setPadding(30, 0, 0, 0);
					tv.setTextSize(14);
					tv.setText(s1[0]);
					distributionMarketContainer.addView(tv);
				} else {
					if (!isHave) {
						TextView last = new TextView(getActivity());
						last.setPadding(30, 0, 0, 0);
						last.setTextSize(14);
						last.setText("......");
						distributionMarketContainer.addView(last);
						isHave = true;
					}
				}
				toSendSelectIds.add(Integer.parseInt(s1[1]));
			}
			if (toSendString.length() > 0) {
				toSendString = toSendString.substring(0, toSendString.length() - 1);
			}
		}
	};

	@OnClick(R.id.at_market_tv)
	private View.OnClickListener marketIdOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
//			ActionSheet toSendAllActionSheet = new ActionSheet();
			actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			actionSheet.setHeight(getView().getHeight());
			SelectAreaViewController selectAreaViewController = new SelectAreaViewController();
			selectAreaViewController.setIsOnly(true);
			selectAreaViewController.setTitleString("请选择所属商圈");
			selectAreaViewController.setSelectedId(selectMarketId);
			selectAreaViewController.setActionSheet(actionSheet);
			selectAreaViewController.setActivity(getActivity());
			selectAreaViewController.setOnSelectAreaListener(onSelectMarketListener);
			actionSheet.setViewController(selectAreaViewController);
			actionSheet.show();
		}
	};

	private SelectAreaViewController.OnSelectAreaListener onSelectMarketListener = new SelectAreaViewController.OnSelectAreaListener() {
		@Override
		public void onSelectArea(HashMap<Integer, String> selections) {
			marketId = null;
			selectMarketId.clear();

			for (Integer key : selections.keySet()) {
				String s = selections.get(key);
				String[] ss = s.split("abc");
				selectMarketId.add(Integer.parseInt(ss[1]));
				marketId = ss[1];
				atMarketTextView.setText(ss[0]);
			}
		}
	};

	String shopDescribeContent;
	@OnClick(R.id.shop_describe_tv)
	private View.OnClickListener shopDescribeOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
//			ActionSheet shopDescribeActionSheet = new ActionSheet();
			actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			actionSheet.setHeight(getView().getHeight());
			InputTextViewController inputTextViewController = new InputTextViewController();
			inputTextViewController.setViewControllerTitle("店铺描述");
			inputTextViewController.setActivity(getActivity());
			inputTextViewController.setActionSheet(actionSheet);
			inputTextViewController.setContentText(shopDescribeContent);
			inputTextViewController.setOnContentTextListener(shopDescribeListener);
			actionSheet.setViewController(inputTextViewController);
			actionSheet.show();
		}
	};

	private InputTextViewController.OnContentTextListener shopDescribeListener = new InputTextViewController.OnContentTextListener() {
		@Override
		public void onGetText(String content) {
			shopDescribeContent = null;
			shopDescribeContent = content;
			shopDescribeTextView.setText(content);
		}
	};


	String distributionContent;
	@OnClick(R.id.distribution_describe_tv)
	private View.OnClickListener distributionOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
//			ActionSheet distributionDescribeActionSheet = new ActionSheet();
			actionSheet.setStyle(ActionSheet.STYLE_BOTTOM_TO_TOP);
			actionSheet.setHeight(getView().getHeight());
			InputTextViewController inputTextViewController = new InputTextViewController();
			inputTextViewController.setViewControllerTitle("配送说明");
			inputTextViewController.setActivity(getActivity());
			inputTextViewController.setActionSheet(actionSheet);
			inputTextViewController.setContentText(distributionContent);
			inputTextViewController.setOnContentTextListener(distributionListener);
			actionSheet.setViewController(inputTextViewController);
			actionSheet.show();
		}
	};

	private InputTextViewController.OnContentTextListener distributionListener = new InputTextViewController.OnContentTextListener() {
		@Override
		public void onGetText(String content) {
			distributionContent = null;
			distributionContent = content;
			distributionDescribeTextView.setText(content);
		}
	};

	@Override
	public boolean onBackKeyDown(Activity a) {
		if (actionSheet != null && actionSheet.isShowing()) {
			actionSheet.dismiss();
			return true;
		}
		return super.onBackKeyDown(a);
	}

}
