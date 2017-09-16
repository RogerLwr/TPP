package com.tianpingpai.seller.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.utils.DimensionUtil;

public class ChangeValueDialog extends DialogFragment {

    private EditText newPriceEditText;
    private TextView priceTextView;

    private View contentContainer;
    private View overlayContainer;
    private View resultContainer;

    public void setOrderID(long orderId) {
        this.orderId = orderId;
    }

    private long orderId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vc_change_price, container, false);
        view.findViewById(R.id.cancel_button).setOnClickListener(cancelButtonListener);
        view.findViewById(R.id.okay_button).setOnClickListener(okayButtonListener);
        newPriceEditText = (EditText) view.findViewById(R.id.promotion_price_edit_text);

        contentContainer = view.findViewById(R.id.content_container);
        overlayContainer = view.findViewById(R.id.overlay_container);
        resultContainer = view.findViewById(R.id.result_container);
        priceTextView = (TextView) resultContainer.findViewById(R.id.specs_unit_text_view);
        resultContainer.findViewById(R.id.close_button).setOnClickListener(cancelButtonListener);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int height = DimensionUtil.dip2px(320);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        }
    }

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismissAllowingStateLoss();
        }
    };

    private View.OnClickListener okayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            double price = 0;
            try {
                price = Double.parseDouble(newPriceEditText.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (price <= 0) {
                Toast.makeText(ContextProvider.getContext(), "价格不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append("新的价格是:￥" + price);
            ssb.setSpan(new ForegroundColorSpan(Color.RED), 6, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceTextView.setText(ssb);
            commit(price);
        }
    };

    private void commit(double price) {
        String url = ContextProvider.getBaseURL() + "/api/order/chgOrderMny.json";
        UserModel user = UserManager.getInstance().getCurrentUser();
        if(user != null && user.getGrade() == UserModel.GRADE_1){
            url = URLApi.SaleOrder.CHG_ORDER_MNY;
        }
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, commitListener);
        req.setMethod(HttpRequest.POST);
        req.setParser(new ModelParser<>(Void.class));
        req.addParam("order_id", orderId + "");
        req.addParam("chg_mny", price + "");
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                Toast.makeText(ContextProvider.getContext(),error.getErrorMsg(),Toast.LENGTH_SHORT).show();
                contentContainer.setVisibility(View.VISIBLE);
                overlayContainer.setVisibility(View.GONE);
                resultContainer.setVisibility(View.GONE);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        contentContainer.setVisibility(View.GONE);
        overlayContainer.setVisibility(View.VISIBLE);
        resultContainer.setVisibility(View.GONE);
    }

    private HttpRequest.ResultListener<ModelResult<Void>> commitListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if (data.isSuccess()) {
                OrderModel orderModel = new OrderModel();
                orderModel.setId(orderId);
                OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,orderModel);
                setCancelable(true);
                contentContainer.setVisibility(View.GONE);
                overlayContainer.setVisibility(View.GONE);
                resultContainer.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
                contentContainer.setVisibility(View.VISIBLE);
                overlayContainer.setVisibility(View.GONE);
                resultContainer.setVisibility(View.GONE);
            }
        }
    };
}
