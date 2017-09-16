package com.tianpingpai.seller.ui;


import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.ProductManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SelectTimeViewController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 设置 促销 页面
 */
@SuppressWarnings("unused")
@ActionBar(title = "设置促销")
@Statistics(page = "设置促销")
@Layout(id = R.layout.view_controller_set_promotion)
public class SetPromotionViewController extends BaseViewController{

    public static final String KEY_PRODUCT = "key.product";
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private SelectTimeViewController startTimeController = new SelectTimeViewController();
    private SelectTimeViewController endTimeController = new SelectTimeViewController();

    @Binding(id = R.id.rule_text_view)
    private TextView ruleTextView;
    @Binding(id = R.id.specs_unit_text_view, format = "{{originPrice}}/{{unit}}")
    private TextView priceUnitTextView;
    @Binding(id = R.id.unit_weight_stand_text_view)
    private TextView unitWeightStandTextView;

    @Binding(id = R.id.unit_text_view, format = "元/{{unit}}")
    private TextView unitTextView;
    @Binding(id = R.id.unit1_text_view, format = "{{unit}}")
    private TextView unit1TextView;
    @Binding(id = R.id.unit2_text_view, format = "{{unit}}")
    private TextView unit2TextView;

    @Binding(id = R.id.promotion_price_edit_text)
    private EditText promotionPriceEditText;
    @Binding(id = R.id.promotion_number_edit_text)
    private EditText promotionNumberEditText;

    @Binding(id = R.id.begin_time_text_view)
    private TextView beginTimeTextView;
    @Binding(id = R.id.end_time_text_view)
    private TextView endTimeTextView;

    @Binding(id = R.id.limit_radio_group)
    private RadioGroup limitRadioGroup;

    @Binding(id = R.id.limit_yes_radio_btn)
    private RadioButton limitYesRadioBtn;
    @Binding(id = R.id.limit_no_radio_btn)
    private RadioButton limitNoRadioBtn;

    @Binding(id = R.id.type_1_buyer_one_day_limit_radio_btn)
    private RadioButton type1BuyerOneDayLimitRadioBtn;
    @Binding(id = R.id.type_2_one_buyer_limit_radio_btn)
    private RadioButton type2OneBuyerLimitRadioBtn;

    @Binding(id = R.id.limit_container)
    private View limitContainer;

    @Binding(id = R.id.limit_number_edit_text)
    private EditText limitNumberEditText;

    private ProductModel productModel;

    private Model ruleModel;

    boolean isExamineFail;
    public static final String KEY_IS_EXAMINE_FAIL = "key.isFail";

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        productModel = (ProductModel) a.getIntent().getSerializableExtra(KEY_PRODUCT);
        isExamineFail = a.getIntent().getBooleanExtra(KEY_IS_EXAMINE_FAIL, false);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        limitRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        getPromotionDetail();
        if(isExamineFail){
            //审核失败的促销，编辑后再次提交时，取消：促销开始时间必须距离现在24小时的限制，针对促销时间的其他限制仍保留
            startTimeController.setStartDate(1);
        }else {
            startTimeController.setStartDate(2);
        }
        startTimeController.setDuration(3);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
            switch (checkId){
                case R.id.limit_yes_radio_btn:
                    limitContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.limit_no_radio_btn:
                    limitContainer.setVisibility(View.GONE);
                    break;
            }
        }
    };

    boolean isBeginTimeSelected = false;
    private SelectTimeViewController.OnSelectedListener startTimeSelectionListener = new SelectTimeViewController.OnSelectedListener() {
        @Override
        public void onSelected() {
            beginTimeTextView.setText(startTimeController.getSelectedDate() + "");
            isBeginTimeSelected = true;
        }
    };

    private SelectTimeViewController.OnSelectedListener endTimeSelectedListener = new SelectTimeViewController.OnSelectedListener() {
        @Override
        public void onSelected() {
            endTimeTextView.setText(String.format("%s",endTimeController.getSelectedDate()));
        }
    };
    @OnClick(R.id.begin_time_text_view)
    private View.OnClickListener onBeginTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActionSheet(true).setViewController(startTimeController);
            getActionSheet(true).setHeight(DimensionUtil.dip2px(300));
            startTimeController.setActivity(getActivity());
            startTimeController.setShowDateOnly(true);
            startTimeController.setOnSelectedListener(startTimeSelectionListener);
            getActionSheet(true).show();
        }
    };

    @OnClick(R.id.end_time_text_view)
    private View.OnClickListener onEndTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if( !isBeginTimeSelected ){
                Toast.makeText(ContextProvider.getContext(), "请先选择开始时间", Toast.LENGTH_LONG).show();
                return;
            }
            if(isExamineFail){
                //审核失败的促销，编辑后再次提交时，取消：促销开始时间必须距离现在24小时的限制，针对促销时间的其他限制仍保留
                endTimeController.setStartDate(startTimeController.getSelectPosition() + 1);
            }else {
                endTimeController.setStartDate(startTimeController.getSelectPosition() + 2);
            }
            endTimeController.setDuration(5);
            getActionSheet(true).setViewController(endTimeController);
            getActionSheet(true).setHeight(DimensionUtil.dip2px(300));
            endTimeController.setShowDateOnly(true);
            endTimeController.setActivity(getActivity());
            endTimeController.setOnSelectedListener(endTimeSelectedListener);
            getActionSheet(true).show();
        }
    };

    long submitButtonLastClick = 0;
    @OnClick(R.id.submit_button)
    private View.OnClickListener onSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (System.currentTimeMillis() - submitButtonLastClick < 2000) {
                return;
            }
            submitButtonLastClick = System.currentTimeMillis();
            String priceStr = promotionPriceEditText.getText().toString();
            if (TextUtils.isEmpty(priceStr)) {
                Toast.makeText(ContextProvider.getContext(), "价格不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (priceStr.contains(".")) {
                if (priceStr.length() - 1 - priceStr.indexOf(".") > 2) {
                    Toast.makeText(ContextProvider.getContext(), "价格金额小数点不能超过2位", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            String promotionNumber = promotionNumberEditText.getText().toString();
            if (TextUtils.isEmpty(promotionNumber) || Integer.parseInt(promotionNumber) < ruleModel.getLong("prodFloorLimit") || Integer.parseInt(promotionNumber) > ruleModel.getLong("prodTopLimit")) {
                Toast.makeText(ContextProvider.getContext(), "数量最少" + ruleModel.getLong("prodFloorLimit") + ",最高" + ruleModel.getLong("prodTopLimit") + ",且不能为空", Toast.LENGTH_LONG).show();
                return;
            }

            Date endDate = null, beginDate = null;
            try {
                beginDate = dateFormat.parse(startTimeController.getSelectedDate());
                endDate = dateFormat.parse(endTimeController.getSelectedDate());
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(ContextProvider.getContext(), "请选择时间", Toast.LENGTH_SHORT).show();
                return;
            }

            if (endDate != null && endDate.after(beginDate)) {
                long days = (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24);
                if (days > 5 || days < 1) {

                    Toast.makeText(ContextProvider.getContext(), "活动时间必须大于24小时,且小于5天", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if(endDate.equals(beginDate)){

            }else {
                Toast.makeText(ContextProvider.getContext(), "结束时间不能早于开始时间或相同", Toast.LENGTH_SHORT).show();
                return;
            }

            if(limitYesRadioBtn.isChecked() && !type1BuyerOneDayLimitRadioBtn.isChecked() && !type2OneBuyerLimitRadioBtn.isChecked()){
                //都没选择的情况
                Toast.makeText(ContextProvider.getContext(), "请选择限购类型", Toast.LENGTH_LONG).show();
                return;
            }
            if (limitYesRadioBtn.isChecked() && TextUtils.isEmpty(limitNumberEditText.getText().toString())) {
                Toast.makeText(ContextProvider.getContext(), "限购数量不能为空", Toast.LENGTH_LONG).show();
                return;
            }

            if (limitYesRadioBtn.isChecked() && (Integer.parseInt(limitNumberEditText.getText().toString()) > Integer.parseInt(promotionNumber))) {
                Toast.makeText(ContextProvider.getContext(), "限购数量必须小于促销总数量", Toast.LENGTH_LONG).show();
                return;
            }
            setPromotion();
        }
    };

    private void getPromotionDetail() {
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/sales/detail", promotionRuleListener);
        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        req.addParam("prod_id", productModel.getId() + "");
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> promotionRuleListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {

            Model model = data.getModel();
            Model detailModel = model.getModel("detail");
            ruleModel = model.getModel("rule");

            if (TextUtils.isEmpty(detailModel.getString("priceInfo"))) {
                unitWeightStandTextView.setText(productModel.getUnit());
            } else {
                unitWeightStandTextView.setText(detailModel.getString("priceInfo"));
            }

            promotionNumberEditText.setHint("请填写数量(最少" + ruleModel.getLong("prodFloorLimit") + ",最高" + ruleModel.getLong("prodTopLimit") + ")");
            ruleTextView.setText("    促销商品不能超过" + ruleModel.getInt("salerLimit") + "种,且要足够优惠,否则审核无法通过,审核未通过率过高,您将被限制参加促销活动。\n    审核通过后，不允许再修改商品信息（包括上下架状态）");
            getBinder().bindData(detailModel);
            hideLoading();

        }
    };

    private void setPromotion() {

        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/sales/add", promotionListener);
        req.setParser(new GenericModelParser());
        req.setMethod(HttpRequest.POST);
        req.addParam("prod_id", productModel.getId() + "");
        req.addParam("price", promotionPriceEditText.getText().toString());
        req.addParam("number", promotionNumberEditText.getText().toString());
        req.addParam("start_time", startTimeController.getSelectedDate());
        req.addParam("end_time", endTimeController.getSelectedDateAddDays(1));
        req.addParam("is_limit", limitYesRadioBtn.isChecked() ? "1" : "0");//是否设置限制 1 是 0否
        req.addParam("limit_type", type1BuyerOneDayLimitRadioBtn.isChecked() ? "1" : "2"); //1 买家单日限购 2单个买家限购
        req.addParam("limit_num", limitNumberEditText.getText().toString());
        VolleyDispatcher.getInstance().dispatch(req);
        req.setErrorListener(new CommonErrorHandler(this));
        showSubmitting();

    }

    private HttpRequest.ResultListener<ModelResult<Model>> promotionListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), "提交成功,将在一个工作日内审核完成!", Toast.LENGTH_LONG).show();
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    ProductManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, new ProductModel());
                }
            } else {
                ResultHandler.handleError(data, SetPromotionViewController.this);
            }
            hideSubmitting();
        }
    };
}
