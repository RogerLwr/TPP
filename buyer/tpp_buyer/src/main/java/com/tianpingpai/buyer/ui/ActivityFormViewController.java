package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.SimpleResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.widget.RangeInputFilter;
import com.tianpingpai.ui.Layout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@SuppressWarnings("unused")
@Layout(id = R.layout.fragment_activity_form)
public class ActivityFormViewController extends BaseViewController{

	public static final String KEY_ACTIVITY_ID = "key.ActivityId";
	public static final String KEY_PRODUCT_NAME = "key.ProductName";
	public static final String KEY_LIMIT = "key.Limit";
	public static final String KEY_UNIT = "key.Unit";

	@Binding(id = R.id.name_edit_text)
	private EditText nameEditText;
	@Binding(id = R.id.address_edit_text)
	private EditText addressEditText;
	@Binding(id = R.id.mobile_edit_text)
	private EditText mobileEditText;
	@Binding(id = R.id.number_edit_text)
	private EditText numberEditText;

	private long activityId = -1;
	private String productName = null;
	private double limit = 0;
	private String unit = null;

	@Override
	public void onActivityCreated(Activity a) {
		super.onActivityCreated(a);
		activityId = a.getIntent().getLongExtra(KEY_ACTIVITY_ID, -1);
		productName = a.getIntent().getStringExtra(KEY_PRODUCT_NAME);
		limit = a.getIntent().getDoubleExtra(KEY_LIMIT, 0);
		unit = a.getIntent().getStringExtra(KEY_UNIT);
	}

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		autoFill();
		showContent();
	}

	private void autoFill() {
		UserModel user = UserManager.getInstance().getCurrentUser();
		if (user != null) {
			mobileEditText.setText(user.getPhone());
		}
		
		if(limit > 0){
			numberEditText.setFilters(new InputFilter[]{new RangeInputFilter(0, (int) limit)});
			int intVersion = (int) limit;
			String limitText = limit - intVersion > 0.0001 ? limit + "" : intVersion + "";
			numberEditText.setHint("每人最多购买" + limitText + unit);
		}
	}

	private ResultListener<ModelResult<SimpleResult>> listener = new ResultListener<ModelResult<SimpleResult>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<SimpleResult>> request,
				ModelResult<SimpleResult> data) {
			if (data.isSuccess()) {
				if (data.getModel().isOK()) {
					Toast.makeText(ContextProvider.getContext(), "抢购成功！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ContextProvider.getContext(),
							data.getModel().getMessage(), Toast.LENGTH_SHORT)
							.show();
				}
			} else{
				ResultHandler.handleError(data, ActivityFormViewController.this);
			}
		}
	};

	@OnClick(R.id.submit_button)
	private OnClickListener submitButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (checkEmpty(nameEditText, "姓名不能为空")
					|| checkEmpty(addressEditText, "地址不能为空")
					|| checkEmpty(mobileEditText, "手机号码不能为空")
					|| checkEmpty(numberEditText, "采购数量不能为空")) {
				return;
			}

			int num = 0;
			try{
				num = Integer.parseInt(numberEditText.getText().toString());
			}catch(Exception e){
                e.printStackTrace();
			}
			if(num == 0){
				Toast.makeText(ContextProvider.getContext(), "采购数量不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			String url = URLApi.Activity.join();

			HttpRequest<ModelResult<SimpleResult>> req = new HttpRequest<>(
					url, listener);

			UserModel user = UserManager.getInstance().getCurrentUser();
			if (user != null) {
				req.addParam("accessToken", user.getAccessToken());
			}

			req.addParam("activityId", "" + activityId);
			req.addParam("number", "" + num);
			try {
				if(productName != null){
					req.addParam("product_name", "" + URLEncoder.encode(productName,"UTF-8"));
				}
				req.addParam("name", URLEncoder.encode(nameEditText.getText().toString(),"UTF-8"));
				req.addParam("phone", URLEncoder.encode(mobileEditText.getText().toString(),"UTF-8"));
				req.addParam("address", URLEncoder.encode(addressEditText.getText().toString(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			ModelParser<SimpleResult> parser = new ModelParser<>(
					SimpleResult.class);
			req.setParser(parser);
			VolleyDispatcher.getInstance().dispatch(req);
		}
	};

	private boolean checkEmpty(EditText editText, String toast) {
		if (TextUtils.isEmpty(editText.getText())) {
			Toast.makeText(ContextProvider.getContext(), toast, Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
}
