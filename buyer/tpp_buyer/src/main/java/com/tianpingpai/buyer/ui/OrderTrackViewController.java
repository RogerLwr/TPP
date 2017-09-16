package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;

import java.util.List;

/**
 * 订单追踪页面
 */
@Statistics(page = "订单追踪")
@Layout(id = R.layout.ui_order_track)
public class OrderTrackViewController extends BaseViewController{

    public static final String KEY_ORDER_ID = "key.orderId";
    public static final String KEY_SALER_PHONE = "key.salerPhone";
    public static final String KEY_SALER_TIME = "key.SALER_TIME";

    @Binding(id = R.id.order_id_text_view)
    private TextView orderIdTextView;
    @Binding(id = R.id.saler_tel_text_view)
    private TextView salerTelTV;
    @Binding(id = R.id.order_dt_text_view)
    private TextView orderTimeTextView;

    @Binding(id = R.id.track_onecontext)
    private TextView oneText;
    @Binding(id = R.id.track_onetime)
    private TextView oneTime;
    @Binding(id = R.id.track_twocontext)
    private TextView twoText;
    @Binding(id = R.id.track_twotime)
    private TextView twoTime;

    private long orderID;
    private String salerPhone, orderTime;
    private ModelStatusListener<ModelEvent, OrderModel> orderStatusListener = new ModelStatusListener<ModelEvent, OrderModel>() {
        @Override
        public void onModelEvent(ModelEvent event, OrderModel model) {
            loadData();
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userLoginListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if (event == UserEvent.Login && model != null) {
                loadData();
            }
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        orderID = a.getIntent().getLongExtra(KEY_ORDER_ID, -1);
        salerPhone = a.getIntent().getStringExtra(KEY_SALER_PHONE);
        orderTime = a.getIntent().getStringExtra(KEY_SALER_TIME);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        setActionBarLayout(R.layout.ab_title_white);
        setTitle("订单追踪");

        orderIdTextView.setText(String.valueOf(orderID));
        salerTelTV.setText(String.valueOf(salerPhone));
        orderTimeTextView.setText(String.valueOf(orderTime));
        UserManager.getInstance().registerListener(userLoginListener);
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OrderManager.getInstance().unregisterListener(orderStatusListener);
        UserManager.getInstance().unregisterListener(userLoginListener);
    }

    private void loadData() {
        String url = URLUtil.GET_ORDER_FORM_TRACE_URL;
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("order_id", orderID + "");
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
            if (data.isSuccess()) {
                List<Model> models = data.getModels();
                for (int i = 0; i < models.size(); i++) {
                    if(i == 1){
                        oneText.setText(models.get(i).getString("desc"));
                        oneTime.setText(models.get(i).getString("oper_dt"));
                    }else if(i == 0){
                        twoText.setText(models.get(i).getString("desc"));
                        twoTime.setText(models.get(i).getString("oper_dt"));
                    }

                }
            } else {
                ResultHandler.handleError(data, OrderTrackViewController.this);
            }
            showContent();
        }
    };
}
