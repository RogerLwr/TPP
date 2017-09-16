package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.manager.BalanceManager;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.PriceFormat;

@Layout(id = R.layout.ui_draw_cash_success)
@Statistics(page = "提现成功")
public class DrawCashSuccessViewController extends BaseViewController {

	TextView mDrawTypeTV, mDrawTypeNameTV, mDrawTipTV;
	EditText mDrawMoneyET;
	Button mDrawSubmitBtn;
	public static final String KEY_MONEY = "key.money";
	public static final String KEY_DATA = "key.data";
	double money;
	Model data;
	
	@Override
	public void onActivityCreated(Activity a) {
		super.onActivityCreated(a);
		money = a.getIntent().getDoubleExtra(KEY_MONEY, 0);
		data = a.getIntent().getParcelableExtra(KEY_DATA);
		Log.e("xx", "35--------------money="+money+", data="+data.getString("name"));

	}

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		setActionBarLayout(R.layout.ab_title_white);
		setTitle("提现申请成功");
		showContent();
		mDrawTypeTV = (TextView) rootView.findViewById(R.id.draw_type_tv);
		mDrawTypeNameTV = (TextView) rootView.findViewById(R.id.draw_type_name_tv);
		mDrawTipTV = (TextView) rootView.findViewById(R.id.draw_tip_tv);
		mDrawMoneyET = (EditText) rootView.findViewById(R.id.draw_money_et);
		mDrawSubmitBtn = (Button) rootView.findViewById(R.id.draw_submit_btn);
		mDrawSubmitBtn.setOnClickListener(submitButtonListener);
		mDrawTypeNameTV.setText(data.getString("name"));
		if( data.getInt("type") ==  1){
			mDrawTypeTV.setText("支付宝");
		}else{
			mDrawTypeTV.setText("银行卡");
		}
		mDrawMoneyET.setText(PriceFormat.format(money));
		configureActionBar(rootView);
	}

	private OnClickListener submitButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			getActivity().finish();
			BalanceManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,null);
		}
	};

}
