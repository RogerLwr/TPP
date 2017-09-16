package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ButtonGroup;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;

import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.NumberUtils;
import com.tianpingpai.utils.PriceFormat;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

@SuppressWarnings("unused")
@ActionBar(title = "价格单位", rightText = "保存")
@Layout(id = R.layout.ui_select_price)
public class SelectUnitViewController extends BaseViewController {

    public static final String KEY_UNIT = "unit";//类型为Model
    public static final String KEY_SPEC = "spec";//类型为Model
    public static final String KEY_NET_QUALITY = "netQuality";//类型为Model
    public static final String KEY_PRICE = "price";//类型为double;
    public static final String KEY_CATEGORY_ID = "categoryId";

    @Binding(id = R.id.unit_container)
    private FlowLayout unitContainer;
    @Binding(id = R.id.specs_title_container)
    private View specsTitleContainer;
    @Binding(id = R.id.specs_container)
    private FlowLayout specsContainer;
    @Binding(id = R.id.specs_divider_view)
    private View specsDividerView;
    @Binding(id = R.id.net_quality_title_container)
    private View netQualityTitleContainer;
    @Binding(id = R.id.net_quality_container)
    private FlowLayout netQualityContainer;
    @Binding(id = R.id.net_quality_divider_view)
    private View netQualityDividerView;
    @Binding(id = R.id.price_edit_text)
    private EditText priceEditText;
    @Binding(id = R.id.unit_text_view)
    private TextView unitTextView;
    private View addSpecsButton;
    private View addNetQualityButton;

    private int categoryId;
    private boolean editable;

    private ButtonGroup unitButtonContainer = new ButtonGroup();
    private ButtonGroup specsButtonContainer = new ButtonGroup();
    private ButtonGroup netQualityButtonContainer = new ButtonGroup();
    private List<Model> unitList;
    private Model selectedUnit;
    private int specSelectionIndex = -1;
    private int netQualitySelectionIndex;

    private Model unit;
    private Model spec;
    private Model quality;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        categoryId = getActivity().getIntent().getIntExtra(KEY_CATEGORY_ID, -1);
        unit = getActivity().getIntent().getParcelableExtra(KEY_UNIT);
        spec = getActivity().getIntent().getParcelableExtra(KEY_SPEC);
        quality = getActivity().getIntent().getParcelableExtra(KEY_NET_QUALITY);
        double price = getActivity().getIntent().getDoubleExtra(KEY_PRICE, 0);

        if (price > 0) {
            priceEditText.setText(PriceFormat.format(price));
        }

        unitButtonContainer.setOnCheckedChangeListener(unitCheckChangedListener);
        specsButtonContainer.setOnCheckedChangeListener(specsCheckedChangeListener);
        netQualityButtonContainer.setOnCheckedChangeListener(netQualityCheckedChangeListener);
        loadData();
    }

    private CompoundButton.OnCheckedChangeListener unitCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                select(unitButtonContainer.indexOf(buttonView), false);
            }
        }
    };

    private View.OnClickListener addSpecsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AddSpecViewController vc = new AddSpecViewController();
            vc.setOnModelsSelectListener(new OnModelsSelectListener() {
                @Override
                public void onModelsSelect(Model... models) {
                    Model m = models[0];
                    List<Model> standList = selectedUnit.getList("stand_list", Model.class);
                    standList.add(m);
                    addSpec(m, true);
                }

                @Override
                public boolean isValid(Model... models) {
                    Model m = models[0];
                    List<Model> standList = selectedUnit.getList("stand_list", Model.class);
                    String name = m.getString("unit_name");
                    String value = m.getString("value");
                    for (Model stand : standList) {
                        if (name.equals(stand.getString("unit_name")) && NumberUtils.equals(value, stand.getString("value"))) {
                            return false;
                        }
                    }
                    return true;
                }
            });
            List<Model> stanList = selectedUnit.getList("stand_list", Model.class);
            vc.setActivity(getActivity());
            vc.setUnitList(stanList);
            getActionSheet(true).setViewController(vc);
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).show();
        }
    };

    private View.OnClickListener addNetQualityButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AddSpecViewController vc = new AddSpecViewController();
            vc.setTitle("添加净含量");
            vc.setOnModelsSelectListener(
                    new OnModelsSelectListener() {
                        @Override
                        public void onModelsSelect(Model... models) {
                            Model m = models[0];
                            List<Model> standList = selectedUnit.getList("weight_list", Model.class);
                            standList.add(m);
                            addQuality(m, true);
                        }

                        @Override
                        public boolean isValid(Model... models) {
                            Model m = models[0];
                            List<Model> standList = selectedUnit.getList("weight_list", Model.class);
                            String name = m.getString("unit_name");
                            String value = m.getString("value");
                            for (Model stand : standList) {
                                if (name.equals(stand.getString("unit_name")) && NumberUtils.equals(value, stand.getString("value"))) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }

            );
            List<Model> stanList = selectedUnit.getList("weight_list", Model.class);
            vc.setActivity(getActivity());
            vc.setUnitList(stanList);
            getActionSheet(true).setViewController(vc);
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).show();
        }
    };

    private void select(int index, boolean autoSelect) {
        if (unitList != null && index < unitList.size()) {
            Model unit = unitList.get(index);
            specSelectionIndex = -1;
            netQualitySelectionIndex = -1;//TODO

            selectedUnit = unit;
            unitButtonContainer.select(index);
            unitTextView.setText(String.format("元/%s", unit.getString("unit_name")));
            List<Model> specs = unit.getList("stand_list", Model.class);
            specsButtonContainer.clear();
            netQualityButtonContainer.clear();

            if (specs == null || specs.isEmpty()) {
                specsTitleContainer.setVisibility(View.GONE);
                specsContainer.setVisibility(View.GONE);
                specsDividerView.setVisibility(View.GONE);
            } else {
                specsTitleContainer.setVisibility(View.VISIBLE);
                specsContainer.setVisibility(View.VISIBLE);
                specsDividerView.setVisibility(View.VISIBLE);
                specsContainer.removeAllViews();

                boolean foundSpec = false;
                Log.e("xx", "autoSelect" + autoSelect);
                Log.e("xx", "spc" + spec);
                for (Model m : specs) {
                    if (spec != null && autoSelect && spec.getInt("id") == m.getInt("id")) {
                        foundSpec = true;
                        break;
                    }
                }
                if (!foundSpec && spec != null && autoSelect) {
                    specs.add(spec);
                }
                for (int i = 0; i < specs.size(); i++) {
                    Model m = specs.get(i);
                    if (spec != null && autoSelect && spec.getInt("id") == m.getInt("id")) {
                        addSpec(m, true);
                    } else {
                        addSpec(m, false);
                    }
                }
                specsContainer.removeView(addSpecsButton);
                if (editable) {
                    specsContainer.addView(addSpecsButton);
                }
            }

            List<Model> weights = unit.getList("weight_list", Model.class);

            Log.e("xx", "quality" + quality);
            if (weights == null || weights.isEmpty()) {
                netQualityTitleContainer.setVisibility(View.GONE);
                netQualityContainer.setVisibility(View.GONE);
                netQualityDividerView.setVisibility(View.GONE);
            } else {
                netQualityTitleContainer.setVisibility(View.VISIBLE);
                netQualityContainer.setVisibility(View.VISIBLE);
                netQualityDividerView.setVisibility(View.VISIBLE);
                netQualityContainer.removeAllViews();

                boolean foundQuality = false;
                for (int i = 0; i < weights.size(); i++) {
                    Model m = weights.get(i);
                    if (quality != null && autoSelect && quality.getInt("id") == m.getInt("id")) {
                        foundQuality = true;
                        break;
                    }
                }
                if (autoSelect && !foundQuality && quality != null) {
                    weights.add(quality);
                }

                for (int i = 0; i < weights.size(); i++) {
                    Model m = weights.get(i);
                    if (quality != null && autoSelect && quality.getInt("id") == m.getInt("id")) {
//                        view.setChecked(true);
                        addQuality(m, true);
                    } else {
                        addQuality(m, false);
                    }

                }
                netQualityContainer.removeView(addNetQualityButton);
                if (editable) {
                    netQualityContainer.addView(addNetQualityButton);
                }
            }
        }
    }

    private void addSpec(Model m, boolean select) {
        specsContainer.removeView(addSpecsButton);
        CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_single_selection_green, null);
        specsContainer.addView(view);
        String name = m.getString("value") + m.getString("unit_name");
        view.setText(name);
        specsButtonContainer.add(view, view);
        if (select) {
            view.setChecked(true);
        } else {
            if (m.getBoolean("checked")) {
                view.setChecked(true);
            }
        }
        if (editable) {
            specsContainer.addView(addSpecsButton);
        }
    }

    private void addQuality(Model m, boolean select) {
        netQualityContainer.removeView(addNetQualityButton);
        CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_single_selection_green, null);
        netQualityContainer.addView(view);
        String name = m.getString("value") + m.getString("unit_name");
        view.setText(name);
        netQualityButtonContainer.add(view, view);
        if (select) {
            view.setChecked(true);
        } else {
            if (m.getBoolean("checked")) {
                view.setChecked(true);
            }
        }
        if (editable) {
            netQualityContainer.addView(addNetQualityButton);
        }
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideLoading();
            if(data.isSuccess()) {
                editable = data.getModel().getBoolean("editable");
                unitList = data.getModel().getList("list", Model.class);
                if (unitList == null || unitList.isEmpty()) {

                    return;
                }
                for (Model m : unitList) {
                    CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_single_selection_green, null);
                    unitContainer.addView(view);
                    view.setText(m.getString("unit_name"));
                    unitButtonContainer.add(view, view);
                }
                addSpecsButton = getActivity().getLayoutInflater().inflate(R.layout.view_add_circle_button, null);
                addSpecsButton.setOnClickListener(addSpecsButtonListener);
                addNetQualityButton = getActivity().getLayoutInflater().inflate(R.layout.view_add_circle_button, null);
                addNetQualityButton.setOnClickListener(addNetQualityButtonListener);

                for (int i = 0; i < unitList.size(); i++) {
                    Model m = unitList.get(i);
                    if (unit != null && unit.getInt("unit_id") == m.getInt("unit_id")) {
                        select(i, true);
                        break;
                    }
                }
            }else{
                ResultHandler.handleError(data,SelectUnitViewController.this);
            }
        }
    };

    private void loadData() {
        String url = ContextProvider.getBaseURL() + "/api/sku/goods/getGoodsPriceUnitByID";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("category_id", String.valueOf(categoryId));
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        req.setErrorListener(new CommonErrorHandler(this));
        showLoading();
    }

    @OnClick(R.id.ab_right_button)
    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(selectedUnit == null){
                return;
            }
            if (specSelectionIndex == -1 && specsContainer.getVisibility() == View.VISIBLE) {//TODO
                Toast.makeText(ContextProvider.getContext(), "必须选择一个规格", Toast.LENGTH_LONG).show();
                return;
            }
            if (netQualitySelectionIndex == -1 && netQualityContainer.getVisibility() == View.VISIBLE) {//TODO
                Toast.makeText(ContextProvider.getContext(), "必须选择一个净我重量", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(priceEditText.getText())) {
                Toast.makeText(ContextProvider.getContext(), "价格不能为空!", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(KEY_PRICE, Double.parseDouble(priceEditText.getText().toString()));
            intent.putExtra(KEY_UNIT, selectedUnit);
            List<Model> standList = selectedUnit.getList("stand_list", Model.class);
            if (standList != null && specSelectionIndex > 0) {
                intent.putExtra(KEY_SPEC, standList.get(specSelectionIndex));
            }
            List<Model> weightList = selectedUnit.getList("weight_list", Model.class);
            if (weightList != null && netQualitySelectionIndex > 0) {
                intent.putExtra(KEY_NET_QUALITY, weightList.get(netQualitySelectionIndex));
            }
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    };

    private CompoundButton.OnCheckedChangeListener specsCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            specSelectionIndex = specsButtonContainer.indexOf(buttonView);
        }
    };

    private CompoundButton.OnCheckedChangeListener netQualityCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            netQualitySelectionIndex = netQualityButtonContainer.indexOf(buttonView);
        }
    };
}
