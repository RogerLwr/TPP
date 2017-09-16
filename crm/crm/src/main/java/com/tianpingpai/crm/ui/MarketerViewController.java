package com.tianpingpai.crm.ui;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@Statistics(page = "基本信息")
@ActionBar(title = "我的资料")
@Layout(id = R.layout.fragment_my_info)
public class MarketerViewController extends CrmBaseViewController {

	@Binding(id = R.id.name_text_view)
	private TextView nameTextView;
	@Binding(id = R.id.contact_text_view)
	private TextView contactTextView;
	@Binding(id = R.id.department_text_view)
	private TextView departmentTextView;
	@Binding(id = R.id.role_text_view)
	private TextView roleTextView;

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		loadData();
	}

	private void loadData() {
		if (UserManager.getInstance().getCurrentUser() == null) {
			if(getActivity() == null){
				return;
			}
			getActivity().finish();
			Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
			return;
		}
		// updateUI(UserManager.getInstance().getCurrentUser());
		//
		HttpRequest<ModelResult<UserModel>> req = new HttpRequest<>(
				URLApi.User.getInfo(), listener);
		ModelParser<UserModel> parser = new ModelParser<>(UserModel.class);
		req.setParser(parser);
		req.addParam("phone", UserManager.getInstance().getCurrentUser()
				.getPhone());
		req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
				.getAccessToken());
		showLoading();
		VolleyDispatcher.getInstance().dispatch(req);
	}
	
	private ResultListener<ModelResult<UserModel>> listener = new ResultListener<ModelResult<UserModel>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<UserModel>> request,
				ModelResult<UserModel> data) {
			hideLoading();
			Log.e("xx", "data" + data.getModel());
			if (data.isSuccess()) {
				updateUI(data.getModel());
			} else {
				Toast.makeText(ContextProvider.getContext(), data.getDesc() + "",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private ResultListener<ModelResult<UserModel>> updateInfoListener = new ResultListener<ModelResult<UserModel>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<UserModel>> request,
				ModelResult<UserModel> data) {
			hideLoading();
			Log.e("xx", "data" + data.getModel());
			if (data.isSuccess()) {
				Toast.makeText(ContextProvider.getContext(), "更新成功",
						Toast.LENGTH_SHORT).show();
				UserManager.getInstance().notifyEvent(UserEvent.UserInfoUpdate, request.getAttachment(UserModel.class));
				getActivity().finish();
			} else {
				Toast.makeText(ContextProvider.getContext(), data.getDesc() + "",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void update() {
		if (TextUtils.isEmpty(nameTextView.getText())) {
			Toast.makeText(ContextProvider.getContext(), R.string.user_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(contactTextView.getText())) {
			Toast.makeText(ContextProvider.getContext(), R.string.password_cannot_be_empty, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		UserModel user = UserManager.getInstance().getCurrentUser();
		if(user == null){
			Toast.makeText(ContextProvider.getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
			return;
		}

		HttpRequest<ModelResult<UserModel>> req = new HttpRequest<>(
				URLApi.User.update(), updateInfoListener);
		req.setMethod(HttpRequest.POST);
		ModelParser<UserModel> parser = new ModelParser<>(
				UserModel.class);
		req.setParser(parser);
//		CommonErrorHandler handler = new CommonErrorHandler(this);
//		req.setErrorListener(handler);

		req.addParam("phone", contactTextView.getText().toString());
		req.addParam("display_name", nameTextView.getText().toString());
		req.addParam("position", user.getPosition());
		req.addParam("department_id", user.getDepartmentId()
				+ "");
		req.addParam("accessToken", user.getAccessToken());
		UserModel um = new UserModel();
		um.setDisplayName(nameTextView.getText().toString());
		req.setAttachment(um);
		showLoading();
		VolleyDispatcher.getInstance().dispatch(req);
	}

	private void updateUI(UserModel data) {
		if (nameTextView == null) {
			return;
		}
		nameTextView.setText(data.getDisplayName());
		contactTextView.setText(data.getPhone());

		String[] departments = { "", "市场部", "运营部", "招商部" };
		int index = Integer.parseInt(data.getDepartmentId());
		//  1市场部，2运营部 3是招商部
		// if(data.getDepartmentId() == 1){
		//
		// }
		departmentTextView.setText(departments[index]);
		roleTextView.setText(data.getPosition());
	}

	@OnClick(R.id.edit_button)
	private OnClickListener editButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			update();
		}
	};
}
