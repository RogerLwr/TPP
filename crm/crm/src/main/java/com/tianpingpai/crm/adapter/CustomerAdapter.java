package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.CustomerDetailViewController;
import com.tianpingpai.crm.ui.CustomerInfoBuyerViewController;
import com.tianpingpai.crm.ui.CustomerInfoSellerViewController;
import com.tianpingpai.crm.ui.CustomerOrderListViewController;
import com.tianpingpai.crm.ui.CustomerInfoViewController;
import com.tianpingpai.crm.ui.OrderDetailViewController;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.CustomerEvent;
import com.tianpingpai.manager.CustomerManager;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@SuppressLint("InflateParams")
public class CustomerAdapter extends ModelAdapter<Model> {


	public static final String KEY_CUSTOMER_INFO = "key.CustomerInfo";
	public static final String KEY_IS_NOT_SUBMIT = "key.isNotSubmit";

	private boolean isApprovalGone ;
	public void setIsApprovalGone(boolean b){
		this.isApprovalGone = b;
	}

	private boolean isCheck = true;
	public void setIsCheck(boolean b){
		this.isCheck = b;
	}

	private ListResult<Model> savedResult;

	public void setActivity(FragmentActivity activity) {
		this.activity = activity;
	}

	FragmentActivity activity;

	private String dateType ;

	private boolean isItemClick = false;
	private boolean isVisited = false;
	private boolean isVisibleBtn = true;

	public void setIsVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public void setIsItemClick(boolean isItemClick) {
		this.isItemClick = isItemClick;
	}

	public void setDateType(String dateType){
		this.dateType = dateType;
	}

	public void setIsVisibleBtn(boolean b){
		this.isVisibleBtn=b;
	}

	public void save(){
		this.savedResult = getData();
		setModels(null);
	}

	public void restore(){
		setData(savedResult);
	}

	private class CustomerViewHolder implements OnClickListener, ViewHolder<Model>{
		private View view;
		private int isMine;

		@Binding(id = R.id.store_name_text_view, format = "{{sale_name}}")
		TextView storeNameTextView;

		@Binding(id = R.id.register_status_text_view, format = "{{is_register}}")
		TextView registerStatusTextView;

		@Binding(id = R.id.user_id_text_view,format = "客户ID:{{user_id}}")
		TextView userIdTextView;

		@Binding(id = R.id.user_name_text_view, format = "客户姓名: {{display_name}}")
		TextView usernameTextView;

		@Binding(id = R.id.contact_text_view, format = "客户电话: {{phone}}")
		TextView contactTextView;

		@Binding(id = R.id.address_text_view, format = "客户地址: {{sale_address}}")
		TextView addressTextView;

		@Binding(id = R.id.user_type_text_view, format = "客户类型: {{user_type}}")
		private TextView userTypeTextView;

		@Binding(id = R.id.marget_manager_text_view, format = "{{manage_name}}")
		TextView margetManagerTextView;

		Button customerInfoBtn;
		Button lookOrderButton;
		Model model;

		Binder binder = new Binder();

		CustomerViewHolder(View v){
			v.setTag(this);
			view = v;
			binder.bindView(this, view);
			customerInfoBtn = (Button) v.findViewById(R.id.customer_info_button);
			lookOrderButton = (Button) v.findViewById(R.id.look_order_button);
			v.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			Log.e("xx", "onClick");
			Intent i =new Intent(activity,ContainerActivity.class);
			CustomerModel cm = new CustomerModel();
			getCustomerModel( cm ,model);
			Log.e("跳转时：=====", "" + cm.getId() + cm.getUserId());

			Log.e("gread=====", model.getInt("grade")+"");
			//跳拜访和流水
			i.putExtra(ContainerActivity.KEY_CONTENT, CustomerDetailViewController.class);
			i.putExtra(CustomerDetailViewController.KEY_CUSTOMER, cm);
			if(!isCheck){
				if(0==model.get("is_mine",Integer.class)){
					i.putExtra(CustomerDetailViewController.KEY_IS_VISITED,false);
					Log.e("my-------==========",model.get("is_mine",Integer.class)+"");
				}else{
					i.putExtra(CustomerDetailViewController.KEY_IS_VISITED,true);
					Log.e("my-------==========", model.get("is_mine", Integer.class) + "");
				}
			}else{
				if(0==model.get("is_subordinate",Integer.class)){
					i.putExtra(CustomerDetailViewController.KEY_IS_VISITED,false);
				}else{
					i.putExtra(CustomerDetailViewController.KEY_IS_VISITED,true);
				}
			}
			activity.startActivity(i);
		}

		private void getCustomerModel(CustomerModel cm,Model model){
			cm.setId(model.getInt("id"));
			cm.setUserId(model.getInt("user_id"));
			cm.setPhone(model.getString("phone"));
			cm.setDisplayName(model.getString("display_name"));
			cm.setSaleName(model.getString("sale_name"));
			cm.setSaleAddress(model.getString("sale_address"));
			cm.setUserType(Integer.parseInt(model.getString("user_type")));
			cm.setMarketId(model.getInt("market_id"));
			cm.setGrade(model.getInt("grade"));
		}

		private ResultListener<ModelResult<Void>> listener = new ResultListener<ModelResult<Void>>() {

			@Override
			public void onResult(HttpRequest<ModelResult<Void>> request,
								 ModelResult<Void> data) {
				if(data.isSuccess()){
					Model m = request.getAttachment(Model.class);
					m.set("status", CustomerModel.STATUS_APPROVED);
					notifyDataSetChanged();
				}else{
					Log.e("xx","error:" + data.getDesc());
				}
			}
		};


		@OnClick(R.id.customer_info_button)
		private OnClickListener customerInfoBtnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Model model = (Model) v.getTag();
				Intent i =new Intent(activity,ContainerActivity.class);
				CustomerModel cm = new CustomerModel();
				getCustomerModel(cm, model);

				Log.e("gread=====", model.getInt("grade")+"");

//				i.putExtra(ContainerActivity.KEY_CONTENT, CustomerInfoViewController.class);
				if(cm.getUserType()==1){
					i.putExtra(ContainerActivity.KEY_CONTENT, CustomerInfoBuyerViewController.class);
				}else{
					i.putExtra(ContainerActivity.KEY_CONTENT, CustomerInfoSellerViewController.class);
				}
				i.putExtra(KEY_CUSTOMER_INFO, cm);
				if(0==model.get("is_mine",Integer.class)){
					i.putExtra(KEY_IS_NOT_SUBMIT,true);
					Log.e("my-------==========",model.get("is_mine",Integer.class)+"");
				}else{
					i.putExtra(KEY_IS_NOT_SUBMIT,false);
					Log.e("my-------==========", model.get("is_mine", Integer.class) + "");
				}
//				if("0".equals(model.getString("user_type"))){
//					if(0==model.getInt("grade")){
//						i.putExtra(CustomerOrderListViewController.KEY_ORDER_TYPE, 3);
//					}else if(1==model.getInt("grade")){
//						i.putExtra(CustomerOrderListViewController.KEY_ORDER_TYPE, 2);
//					}
//					i.putExtra(OrderDetailViewController.KEY_IS_HAVE_GRADE, true);
//				}
				activity.startActivity(i);
			}
		};

		@OnClick(R.id.look_order_button)
		private OnClickListener lookOrderButtonListener = new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Model m = (Model) v.getTag();
				if("查看订单".equals(((Button) v).getText().toString())   ){
					Log.e("xx", "127-------点击了 查看订单" + m);
					Intent intent = new Intent(activity, ContainerActivity.class);
					intent.putExtra(ContainerActivity.KEY_CONTENT, CustomerOrderListViewController.class);
					intent.putExtra(CustomerOrderListViewController.KEY_DATE_TYPE, dateType);
					Log.e("dateType","-------"+dateType);
					String title;
					title = m.getString("display_name")+"的订单";
					intent.putExtra(CustomerOrderListViewController.KEY_TITLE, title);
					CustomerModel cm = new CustomerModel();
					getCustomerModel(cm, m);
					if("0".equals(m.getString("user_type"))){
						if(0==m.getInt("grade")){
							intent.putExtra(CustomerOrderListViewController.KEY_ORDER_TYPE, 3);
						}else if(1==m.getInt("grade")){
							intent.putExtra(CustomerOrderListViewController.KEY_ORDER_TYPE, 2);
						}
						intent.putExtra(OrderDetailViewController.KEY_IS_HAVE_GRADE, true);
					}
					intent.putExtra(CustomerOrderListViewController.KEY_CUSTOMER, cm);
					activity.startActivity(intent);

				}else {
					if(0==model.get("is_mine",Integer.class)){
						Toast.makeText(ContextProvider.getContext(),"不能审批当前客户",Toast.LENGTH_SHORT).show();
						return ;
					}

					final ActionSheetDialog dialog = new ActionSheetDialog();
					dialog.setActionSheet(new ActionSheet());
					dialog.setActivity(activity);
					dialog.setPositiveButtonListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
							UserModel user = UserManager.getInstance().getCurrentUser();
							HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLApi.Customer.approve(),listener);
							ModelParser<Void> parser = new ModelParser<>(Void.class);
							req.setParser(parser);
							req.addParam("accessToken", user.getAccessToken());
							req.addParam("phone", m.getString("phone"));
							req.addParam("user_type", m.getString("user_type") + "");
							req.setAttachment(m);
							VolleyDispatcher.getInstance().dispatch(req);
						}
					});
					dialog.setTitle("确定审批该用户吗?");
					dialog.show();

				}
			}
		};
		@Override
		public void setModel(Model c) {
			this.model = c;
			binder.bindData(model);
			userTypeTextView.setText(model.getString("user_type").equals("0") ? "客户类型: 卖家" : "客户类型: 买家");
			registerStatusTextView.setText(model.getString("is_register").equals("0") ? "未注册" : "已注册");

			int status = c.getInt("status");
			Log.e("xx", "209-------status=" + status);
			if("1".equals(model.getString("user_type"))){
				if (status == CustomerModel.STATUS_NEED_APPROVE) {
					lookOrderButton.setText("审批");
				} else {
					lookOrderButton.setText("查看订单");
				}
			}else{
				lookOrderButton.setText("查看订单");
			}

			if(isApprovalGone){
				lookOrderButton.setText("查看订单");
			}

			if(!isVisibleBtn){
				lookOrderButton.setVisibility(View.INVISIBLE);
				customerInfoBtn.setVisibility(View.INVISIBLE);
			}

			customerInfoBtn.setTag(c);
			lookOrderButton.setTag(c);
		}
		@Override
		public View getView() {
			return view;
		}

	}

	private HttpRequest.ResultListener<ModelResult<Void>> deleteListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Void>> request,
							 ModelResult<Void> data) {
			if (data.isSuccess()) {
				Toast.makeText(ContextProvider.getContext(), "删除成功！", Toast.LENGTH_SHORT)
						.show();
				CustomerManager.getInstance().notifyEvent(CustomerEvent.CustomerInfoUpdate, new CustomerModel());
//				getActivity().finish();
			} else {
				Toast.makeText(ContextProvider.getContext(), "错误:" + data.getDesc(),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
		View convertView = activity.getLayoutInflater().inflate(R.layout.item_client, null);
		return new CustomerViewHolder(convertView);
	}
}
