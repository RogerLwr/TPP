package com.tianpingpai.crm.ui;

import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.SaleNumberModel;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@ActionBar(title = "客户流水")
@Layout(id = R.layout.view_controller_customer_serial)
public class CustomerSerialViewController extends BaseViewController {

    @Binding(id = R.id.today_orders_text_view ,format = "{{orderToday}}")
    private TextView todayOrdersTextView;
    @Binding(id = R.id.this_week_text_view ,format = "{{orderWeek}}")
    private TextView thisWeekOrdersTextView;
    @Binding(id = R.id.more_text_view ,format = "{{orderMore}}")
    private TextView moreTextView;
    @Binding(id = R.id.all_text_view ,format = "{{orderAll}}")
    private TextView allTextView;

    @Binding(id = R.id.today_fee_text_view ,format = "{{feeToday}}")
    private TextView todayFeeTextView;
    @Binding(id = R.id.week_fee_text_view ,format = "{{feeWeek}}")
    private TextView weekFeeTextView;
    @Binding(id = R.id.month_fee_text_view ,format = "{{feeMonth}}")
    private TextView monthFeeTextView;
    @Binding(id = R.id.more_fee_text_view ,format = "{{feeAll}}")
    private TextView allFeeTextView;

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        loadData();
    }

    public void loadData(){
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        CustomerModel customer = (CustomerModel) getActivity().getIntent()
                .getSerializableExtra(CustomerDetailViewController.KEY_CUSTOMER);
        HttpRequest<ModelResult<SaleNumberModel>> req = new HttpRequest<>(
                URLApi.Customer.getSaleNumber(), saleNumberListener);
        ModelParser<SaleNumberModel> parser = new ModelParser<>(
                SaleNumberModel.class);
        req.addParam("customer_id", customer.getId() + "");
        req.addParam("accessToken", user.getAccessToken());
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<SaleNumberModel>> saleNumberListener = new HttpRequest.ResultListener<ModelResult<SaleNumberModel>>() {

        @Override
        public void onResult(HttpRequest<ModelResult<SaleNumberModel>> request,
                             ModelResult<SaleNumberModel> data) {
            if (data.isSuccess()) {

                getBinder().bindData(data.getModel());
            }
        }
    };
}
