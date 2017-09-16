package com.tianpingpai.crm.ui;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.OrderProductAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONModelMapper;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Statistics(page = "订单详情")
@ActionBar(titleRes = R.string.t_order_detail)
@Layout(id = R.layout.fragment_order_detail1509)
public class OrderDetailViewController extends CrmBaseViewController{

	public static final String KEY_ORDER = "key.Order";
	public static final String KEY_TYPE = "key.type";
	public static final String KEY_GRADE = "key.grade";
	public static final String KEY_IS_HAVE_GRADE = "key.is_have_grade";
	private boolean isHaveGrade ;

	private Model order = new Model();

    @Binding(id = R.id.scroll_view_container)
	private ScrollView scrollViewContainer;
	@Binding(id = R.id.order_id_text_view,format = "{{order_id}}")
	private TextView orderIdTextView;
    @Binding(id = R.id.order_status_text_view,format = "{{status_name}}")
	private TextView orderStatusTextView;
    @Binding(id = R.id.store_name_text_view,format = "卖家店铺:{{sale_name}}")
	private TextView storeNameTextView;
    @Binding(id = R.id.order_time_text_view,format = "下单时间:{{order_dt}}")
	private TextView orderTimeTextView;
    @Binding(id = R.id.prod_mny_text_view,format = "+￥{{prod_mny|money}}")
	private TextView prodMnyTextView;
    @Binding(id = R.id.deliver_mny_text_view,format = "+￥{{deliver_mny|money}}")
	private TextView deliverMnyTextView;
//    @Binding(id = R.id.promotion_mny_text_view,format = "-￥{{coupon_mny|money}}")
	@Binding(id = R.id.promotion_mny_text_view)
	private TextView promotionMnyTextView;
    @Binding(id = R.id.amount_text_view,format = "￥{{act_mny|money}}")
	private TextView amountTextView;

	@Binding(id = R.id.bonus_mny_text_view,format = "-￥{{youhui_mny}}")
	private TextView bonusMnyTextView;
	@Binding(id = R.id.balance_mny_text_view,format = "{{balance}}")
	private TextView balanceMnyTextView;

    @Binding(id = R.id.buyer_name_text_view,format = "收货人:{{recevier_name}}")
	private TextView buyerNameTextView;
    @Binding(id = R.id.b_shop_name_text_view,format = "买家店铺:{{b_shop_name}}")
	private TextView bShopNameTV;
    @Binding(id = R.id.seller_name_text_view,format = "卖家:{{s_user_name}}")
	private TextView sellerNameTextView;
    @Binding(id = R.id.pay_type_text_view)
	private TextView payTypeTextView;
    @Binding(id = R.id.deliver_type_text_view,format = "配送方式:{{deliver_name}}")
	private TextView deliverTypeTextView;
    @Binding(id = R.id.deliver_address_text_view,format = "收货地址:{{address}}")
	private TextView deliverAddressTextView;
    @Binding(id = R.id.deliver_time_text_view,format = "送货时间:{{deliver_dt}}")
	private TextView deliverTimeTextView;
    @Binding(id = R.id.buyer_phone_text_view,format = "{{telephone}}")
	private TextView buyerPhoneTextView;
    @Binding(id = R.id.seller_phone_text_view,format = "{{saler_phone}}")
	private TextView sellerPhoneTextView;
    @Binding(id = R.id.saler_address_text_view,format = "卖家地址:{{saler_addr}}")
	private TextView sellerAddressTextView;
    @Binding(id = R.id.remark_text_view)
	private TextView remarkTextView;
    @Binding(id = R.id.remark_container)
	private View remarkContainer;

	@Binding(id = R.id.coupon_container)
	private View couponContainer;
	@Binding(id = R.id.deliver_container)
	private View deliverContainer;
	@Binding(id = R.id.bonus_container)
	private View bonusContainer;
	@Binding(id = R.id.balance_container)
	private View balanceContainer;

	private OrderProductAdapter adapter = new OrderProductAdapter();

	private int grade;
	private int type;

	@Override
	protected void onConfigureView(View v) {
		super.onConfigureView(v);
		String json = getActivity().getIntent().getStringExtra(KEY_ORDER);
		Log.e("json","======"+json);
		isHaveGrade = getActivity().getIntent().getBooleanExtra(KEY_IS_HAVE_GRADE,false);
		type = getActivity().getIntent().getIntExtra(KEY_TYPE, -1);
		grade = getActivity().getIntent().getIntExtra(KEY_GRADE,-1);

		try {
			JSONObject jobj = new JSONObject(json);
			JSONModelMapper.mapObject(jobj,order);
		} catch (JSONException e) {
			e.printStackTrace();
		}


		ListView productsListView = (ListView) v
				.findViewById(R.id.products_list_view);
		productsListView.setAdapter(adapter);

//		refresh(order);
		getDetail();

	}

	private void refresh(Model model) {
        getBinder().bindData(model);
        double couponMny = model.getDouble("coupon_mny");
        if(model.getString("coupon_mny") != null){
            couponMny  = Double.parseDouble(model.getString("coupon_mny"));
        }

		double bonusMny = model.getDouble("youhui_mny");
		if(model.getString("youhui_mny") != null&&(!"".equals(model.getString("youhui_mny")))){
			bonusMny = Double.parseDouble(model.getString("youhui_mny"));
			Log.e("zoule==------","en");
		}

		double balanceMny = model.getDouble("balance");
		if(model.getString("balance") !=null){
			balanceMny = Double.parseDouble(model.getString("balance"));
		}

		if(couponMny==0){
			couponContainer.setVisibility(View.GONE);
		}else{
			promotionMnyTextView.setText("-￥"+couponMny);
		}

		if(bonusMny==0){
			bonusContainer.setVisibility(View.GONE);
		}

		if(balanceMny==0){
			balanceContainer.setVisibility(View.GONE);
//			balanceMnyTextView.setText("-￥"+balanceMny);
			Log.e("balanceMnyzoule==------", "dayu0");
		}else{

			Log.e("balanceMnyzoule==------", "xiaoyu0");
		}

		if(1==grade){
			sellerAddressTextView.setText(model.getString("saler_address"));
		}else{
			sellerAddressTextView.setText(model.getString("saler_addr"));
		}
		if(1==grade){
			payTypeTextView.setText(model.getInt("pay_type")==0 ? "支付方式:在线支付-"+model.getString("pay_status"):"支付方式:货到付款-"+model.getString("pay_status"));
		}else{
			payTypeTextView.setText(model.getInt("pay_type")==0 ? "支付方式:在线支付-"+model.getString("payStatus"):"支付方式:货到付款-"+model.getString("payStatus"));
		}

		String remark = model.getString("remark");
		if (remark == null || remark.trim().equals("")) {
			remark = "";
		}
		if(TextUtils.isEmpty(remark)){
			remarkContainer.setVisibility(View.VISIBLE);
			remarkTextView.setText("无");
		}else{
			remarkContainer.setVisibility(View.VISIBLE);
			remarkTextView.setText(remark);
		}
		adapter.setModels((ArrayList<Model>) model.getList("prod_list", Model.class));
	}

	private ResultListener<ModelResult<Model>> listener = new ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request,
				ModelResult<Model> data) {
			hideLoading();
			if(data.getCode()==1){
				UserManager.getInstance().loginExpired(getActivity());
				return;
			}
			if (data.isSuccess()) {
//				data.getModel().setBuyerName(order.getBuyerName());// TODO
//				if(null==data.getModel()){
//
//				}
				refresh(data.getModel());
				scrollViewContainer.smoothScrollTo(0, 0);
				showContent();
//				Log.e("xx", "detail:" + data.getModel().getProductList().size());
			} else {
				Toast.makeText(ContextProvider.getContext(),
						data.getDesc() + "", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void getDetail() {
		showLoading();
		UserModel user = UserManager.getInstance().getCurrentUser();
		if (user == null) {
			Toast.makeText(ContextProvider.getContext(), R.string.login_first,
					Toast.LENGTH_SHORT).show();
			return;
		}
		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(
				URLApi.Order.getDetail(), listener);
		req.setMethod(HttpRequest.GET);
		CommonErrorHandler handler = new CommonErrorHandler(this);
		req.setErrorListener(handler);

		if(isHaveGrade){
			req.addParam("type", type+"");
			req.addParam("grade",grade+"");
		}
		req.addParam("order_id", order.getString("order_id") + "");
		req.addParam("accessToken", user.getAccessToken());
		req.setParser(new GenericModelParser());
		VolleyDispatcher.getInstance().dispatch(req);
	}
}
