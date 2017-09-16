package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.parser.ParserDescNoResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.dialog.DrawCashDialogViewController;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.TicketLoader;

import java.util.ArrayList;

@ActionBar(title = "提现到银行卡")////提现到支付宝
@Layout(id = R.layout.ui_draw_cash)
@Statistics(page = "提现")
public class DrawCashViewController extends BaseViewController {

	public static final String KEY_MONEY = "key.money";
	private static final String TITLE_BANK = "提现到银行卡";
	private static final String TITLE_ALIBABA = "提现到支付宝";

	private TextView mDrawTypeTV, mDrawTypeNameTV, mDrawTipTV;
	private EditText mDrawMoneyET;
	private Button mDrawSubmitBtn;

	private Model selectedModel;
	private double usableMoney; //可用余额
    private int type;
    private String ID;
    private Double money;
	private TicketLoader ticketLoader = new TicketLoader();

	@Override
	public void onActivityCreated(Activity a) {
		super.onActivityCreated(a);
		usableMoney = a.getIntent().getDoubleExtra(KEY_MONEY, 0);
	}

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		showContent();
		mDrawTypeTV = (TextView) rootView.findViewById(R.id.draw_type_tv);
		mDrawTypeNameTV = (TextView) rootView.findViewById(R.id.draw_type_name_tv);
		mDrawTypeNameTV.setOnClickListener(drawTypeNameClickListener);
		mDrawTipTV = (TextView) rootView.findViewById(R.id.draw_tip_tv);
		mDrawMoneyET = (EditText) rootView.findViewById(R.id.draw_money_et);
		mDrawSubmitBtn = (Button) rootView.findViewById(R.id.draw_submit_btn);
		mDrawSubmitBtn.setOnClickListener(submitButtonListener);

		mDrawMoneyET.setHint("当前可提现金额是" + usableMoney + "元");

		configureActionBar(rootView);

		ticketLoader.setTicketLoaderListener(new TicketLoader.TicketLoaderListener() {
			@Override
			public void onTicketLoaded(TicketLoader.Ticket t) {
				loadAccountData();
				loadData();
			}

			@Override
			public void onTicketFailed(HttpError error) {

			}
		});
		ticketLoader.load();
	}

	private void updateType(){
		if( selectedModel !=null && selectedModel.getInt("type") == 1){
			setTitle(TITLE_ALIBABA);
			mDrawTypeTV.setText("支付宝");
			mDrawTypeNameTV.setText(selectedModel.getString("name"));
			mDrawTipTV.setText(R.string.draw_cash_alibaba);
		}else{
			setTitle(TITLE_BANK);
			mDrawTypeTV.setText("银行卡");
			mDrawTypeNameTV.setText(selectedModel.getString("name"));
			mDrawTipTV.setText(R.string.draw_cash_bank);
		}
	}

	private void loadAccountData() {
		String url = ContextProvider.getBaseURL() + "/api/user/account";
		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, accountListener);
		req.setParser(new GenericModelParser());
		VolleyDispatcher.getInstance().dispatch(req);
		showLoading();
	}

	private HttpRequest.ResultListener<ModelResult<Model>> accountListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
			if (data.isSuccess()) {
				showContent();
				Model model = data.getModel();
				usableMoney = model.getDouble("balance");
				mDrawMoneyET.setHint("当前可提现金额是" + usableMoney + "元");
			} else {
				ResultHandler.handleError(data, DrawCashViewController.this);
			}
			hideLoading();
		}
	};

	private void loadData(){

		HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.CASH_ACCOUNTS, listener);
		JSONListParser parser = new JSONListParser();
		parser.setPaged(false);
		req.setParser(parser);
		VolleyDispatcher.getInstance().dispatch(req);
		showLoading();
	}

	private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
			hideLoading();
			if(data.isSuccess()){
				mModels = data.getModels();
				if(mModels != null && mModels.size() != 0){

					Log.e("xx", "33---------------size=" + mModels.size());
					Log.e("xx", "50---------------name=" + mModels.get(0).getString("name"));
					selectedModel = mModels.get(0);
					updateType();
				}else{
//					showDialog();
					showActionSheetDialog();
					mDrawSubmitBtn.setClickable(false);
				}
			}else{
                ResultHandler.handleError(data, DrawCashViewController.this);
            }
		}
	};

	private void showActionSheetDialog(){
		final ActionSheetDialog actionSheetDialog = new ActionSheetDialog();
		actionSheetDialog.setActionSheet(getActionSheet(true));
		actionSheetDialog.setPositiveButtonListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				actionSheetDialog.dismiss();
				getActivity().finish();
			}
		});
		actionSheetDialog.setNegativeButtonListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				actionSheetDialog.dismiss();
				getActivity().finish();
			}
		});
		actionSheetDialog.setTitle("您还没有绑定银行卡或支付宝\n请与我们客服联系\n4006 406 010");
		actionSheetDialog.show();
	}

	private void submit(){
		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.APPLY, submitListener);
		req.addParam("type", "" + type);
		req.addParam("id", ID);
		req.addParam("money", "" + money);
		//TODO
		req.addParam("ticket",ticketLoader.getTicket().getValue());

		req.setMethod(Request.Method.POST);
		ParserDescNoResult parser = new ParserDescNoResult();
		parser.setResultIsObj(false);
		req.setParser(parser);
		VolleyDispatcher.getInstance().dispatch(req);
		showLoading();
	}

	private ArrayList<Model> mModels;
	private HttpRequest.ResultListener<ModelResult<Model>> submitListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
			hideLoading();
			if(data.isSuccess()){
				showContent();
				Intent intent = new Intent(getActivity(), ContainerActivity.class);
				intent.putExtra(ContainerActivity.KEY_CONTENT, DrawCashSuccessViewController.class);
				intent.putExtra(DrawCashSuccessViewController.KEY_MONEY, money);
				intent.putExtra(DrawCashSuccessViewController.KEY_DATA, selectedModel);
				getActivity().startActivity(intent);
				getActivity().finish();
			}else if (data.getCode() == 7) {
                ticketLoader.getTicket().invalidate();
                Toast.makeText(ContextProvider.getContext(), "数据已过期,请重试!", Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else if (data.getCode() == 6) {
                submit();//TODO
            } else {
                ResultHandler.handleError(data, DrawCashViewController.this);
            }
		}
	};

	private DrawCashDialogViewController.SelectCardListener selectCardListener = new DrawCashDialogViewController.SelectCardListener() {
		@Override
		public void selectCard(Model m) {
			selectedModel = m;
			updateType();
		}
	};

	private OnClickListener drawTypeNameClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if( mModels != null && mModels.size() != 0){
				ActionSheet as = getActionSheet(true);
				DrawCashDialogViewController drawCashDialogViewController = new DrawCashDialogViewController();
				drawCashDialogViewController.setModels(mModels);
				drawCashDialogViewController.setActivity(getActivity());
				drawCashDialogViewController.setSelectCardListener(selectCardListener);
				as.setViewController(drawCashDialogViewController);
				as.show();

			}

		}
	};

	private OnClickListener submitButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String inputMoney = mDrawMoneyET.getText().toString();
			if( !TextUtils.isEmpty(inputMoney) ){
				Double money = Double.parseDouble(inputMoney);
				if(money > usableMoney){
					Toast.makeText(getActivity(), "金额不能大于可用余额"+usableMoney, Toast.LENGTH_SHORT).show();
				}else if(money < 0.01){
					Toast.makeText(getActivity(), "金额不能小于可用余额0.01元", Toast.LENGTH_SHORT).show();
				}else{
					DrawCashViewController.this.money = money;
					ID = selectedModel.getInt("id")+"";
					type = selectedModel.getInt("type");
					submit();
					mDrawSubmitBtn.setClickable(false);
                    hideKeyboard();
				}
			}else{
				Toast.makeText(getActivity(), "输入金额不能为空", Toast.LENGTH_SHORT).show();
			}
		}
	};
}
