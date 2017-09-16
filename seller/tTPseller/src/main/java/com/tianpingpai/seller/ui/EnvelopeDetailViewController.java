package com.tianpingpai.seller.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.PriceFormat;

@ActionBar(layout = R.layout.ab_title_green)
@Statistics(page = "红包详情")
@Layout(id = R.layout.ui_envelope_detail)
public class EnvelopeDetailViewController extends BaseViewController {

    public static final String KEY_ID = "id";

    private TextView typeTextView;
    private TextView dateTextView;
    private TextView amountTextView;
    private TextView nameTextView;//TODO
    private TextView statusTextView;

    private View ruleContainer;
    private TextView ruleTextView;

    private int id;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        id = getActivity().getIntent().getIntExtra(KEY_ID, -1);
        typeTextView = (TextView) rootView.findViewById(R.id.type_text_view);
        dateTextView = (TextView) rootView.findViewById(R.id.date_text_view);
        amountTextView = (TextView) rootView.findViewById(R.id.amount_text_view);
        nameTextView = (TextView) rootView.findViewById(R.id.name_text_view);
        statusTextView = (TextView) rootView.findViewById(R.id.status_text_view);

        ruleContainer = rootView.findViewById(R.id.rule_container);
        ruleTextView = (TextView) rootView.findViewById(R.id.rule_text_view);

        showContent();
        loadData();
    }

    private void loadData() {
        String url = ContextProvider.getBaseURL() + "/api/bonus/info";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.setParser(new GenericModelParser());
        req.addParam("id", id + "");
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideLoading();
            if (data.isSuccess()) {
                showContent();
                Model model = data.getModel();
                String type = model.getInt("bonus_type") == 1 ? "系统红包" : "订单红包";
                typeTextView.setText(type);

                amountTextView.setText(String.format("￥%s", PriceFormat.format( model.getDouble("mny"))));
//              originTextView.setText(model.getString("description"));
                dateTextView.setText(model.getString("giving_time"));
                statusTextView.setText(model.getString("order_status"));

                String rule = model.getString("rule");
                if (!TextUtils.isEmpty(rule)) {
                    ruleContainer.setVisibility(View.VISIBLE);
                    ruleTextView.setText(rule);
                } else {
                    nameTextView.setVisibility(View.GONE);
//                  typeTextView.setVisibility(View.GONE);
                }
            } else {
                ResultHandler.handleError(data, EnvelopeDetailViewController.this);
            }

        }
    };
}
