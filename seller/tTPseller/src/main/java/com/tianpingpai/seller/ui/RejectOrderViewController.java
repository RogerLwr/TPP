package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@ActionBar(title = "拒绝原因")
@Statistics(page = "拒绝订单")
@Layout(id = R.layout.ui_reject_order)
public class RejectOrderViewController extends BaseViewController {

    public static final String KEY_ORDER_ID = "key.orderId";
    int[] optionIds = {
            R.id.no_products_button,
            R.id.invalid_distance_button,
            R.id.no_delivery_button,
            R.id.insufficient_money_button,
            R.id.other_button,
    };

    String[] reasons = {
            "无货",
            "距离太远",
            "暂无配送人员",
            "订单金额太小",
            "其它"
    };

    int checkIndex = -1;
    private EditText reasonEditText;
    private long orderID;

    private View submitButton;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        orderID = a.getIntent().getLongExtra(KEY_ORDER_ID,-1);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        for(int vid:optionIds){
            CheckedTextView ctv = (CheckedTextView) rootView.findViewById(vid);
            ctv.setOnClickListener(optionButtonOnclickListener);
        }
        showContent();
        submitButton = rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(submitButtonListener);
        reasonEditText = (EditText) rootView.findViewById(R.id.reason_edit_text);
    }

    private View.OnClickListener optionButtonOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0;i < optionIds.length;i++){
                int vid = optionIds[i];
                CheckedTextView ctv = (CheckedTextView) getView().findViewById(vid);
                ctv.setOnClickListener(optionButtonOnclickListener);
                if(ctv.getId() == v.getId()){
                    checkIndex = i;
                    ctv.setChecked(true);
                }else{
                    ctv.setChecked(false);
                }
            }
        }
    };

    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if(user == null){
                Toast.makeText(ContextProvider.getContext(),"请重新登录!",Toast.LENGTH_SHORT).show();
                return;
            }

            String reason = null;
            if(checkIndex != -1){
                reason = reasons[checkIndex];
            }
            if(checkIndex == 4){
                reason = reasonEditText.getText().toString();
            }
            if(TextUtils.isEmpty(reason)){
                Toast.makeText(ContextProvider.getContext(),"理由不能为空!",Toast.LENGTH_SHORT).show();
                return;
            }
            String url = URLApi.REFUSE_ORDER_URL;
            if(user.getGrade() == UserModel.GRADE_1){
                url = URLApi.SaleOrder.DEL_ORDER;
            }
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url,listener);
            req.setMethod(HttpRequest.POST);
            req.addParam("order_id", String.valueOf(orderID));
            req.addParam("user_type", String.valueOf(UserModel.USER_TYPE_SELLER));
            req.addParam("reason", reason);
            req.setAttachment(orderID);
            req.setParser(new ModelParser<>(Void.class));
            CommonErrorHandler errorHandler = new CommonErrorHandler((RejectOrderViewController.this));
            req.setErrorListener(new CommonErrorHandler((RejectOrderViewController.this)){
                @Override
                public void onError(HttpRequest<?> request, HttpError error) {
                    super.onError(request, error);
                    submitButton.setEnabled(true);
                }
            });
            req.setErrorListener(errorHandler);
            VolleyDispatcher.getInstance().dispatch(req);
            showLoading();
            submitButton.setEnabled(false);
        }
    };

    private HttpRequest.ResultListener<ModelResult<Void>> listener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            submitButton.setEnabled(true);
            if(data.isSuccess()){
                if(getActivity() != null) {
                    getActivity().finish();
                }
                OrderModel order = new OrderModel();
                order.setId(orderID);
                OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,order);
            }else{
                ResultHandler.handleError(data, RejectOrderViewController.this);
            }
        }
    };
}
