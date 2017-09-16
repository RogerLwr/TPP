package com.tianpingpai.seller.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.ProductQualityAdapter;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ButtonGroup;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.Tools;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑 商品属性
 */
@Statistics(page = "添加属性")
@Layout(id = R.layout.view_controller_add_property)
@ActionBar(title = "添加属性", rightText = "保存")
public class AddPropertyViewController extends BaseViewController {

    public static final String KEY_CATEGORY_ID = "categoryId";
    public static final String KEY_ATTR_ID = "Key.ID";
    private int attrID;

    public static final String KEY_ID = "Id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MODE = "mode";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SELECTED_VALUE = "model";
    public static final String KEY_IDS = "key.ids";
    public static final String KEY_VALUES = "key.values";
    public static final String KEY_LEVEL = "key.level";
    public static final String KEY_REQUIRED = "required";

    public static final int REQUEST_CODE = 101;

    private int categoryId;
    private String name;
    private int attrMode;
    private String attrType;
    private boolean required = true;
    private boolean editable = false;

    private List<Model> selectedModels = new ArrayList<>();
    private ArrayList<Integer> selectedIds = new ArrayList<>();
    private ArrayList<String> selectedValues = new ArrayList<>();


    private int selectedPosition = -1;
    @Binding(id = R.id.required_text_view)
    private TextView requiredTextView;
    @Binding(id = R.id.add_property_container)
    private View addPropertyContainer;
    @Binding(id = R.id.fill_blank_container)
    private View fillBlankContainer;
    @Binding(id = R.id.value_edit_text)
    private EditText valueEditText;
    @Binding(id = R.id.word_count_text_view)
    private TextView wordCountTextView;

    @Binding(id = R.id.flow_container)
    private FlowLayout flowContainer;
    @Binding(id = R.id.name_text_view)
    private TextView nameTextView;
    @Binding(id = R.id.list_view)
    private ListView listView;

    private ProductQualityAdapter productQualityAdapter = new ProductQualityAdapter();

    private ButtonGroup singleButtonGroup = new ButtonGroup();

    private ArrayList<Model> listModel;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        categoryId = a.getIntent().getIntExtra(KEY_CATEGORY_ID, 0);
        name = a.getIntent().getStringExtra(KEY_NAME);
        attrID = a.getIntent().getIntExtra(KEY_ATTR_ID, 0);
        attrMode = a.getIntent().getIntExtra(KEY_MODE, 0);
        attrType = a.getIntent().getStringExtra(KEY_TYPE);
        required = a.getIntent().getBooleanExtra(KEY_REQUIRED, true);
        Model selectedWithValueModel = a.getIntent().getParcelableExtra(EditProductPropertyViewController.KEY_SELECTED_ALL_MODEL);
        if (selectedWithValueModel.getList("selectedModels", Model.class) != null) {
            selectedModels = selectedWithValueModel.getList("selectedModels", Model.class);
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        singleButtonGroup.setOnCheckedChangeListener(singleSelectedCheckChangedListener);
        nameTextView.setText(name);

        valueEditText.addTextChangedListener(valueTextWatcher);
        productQualityAdapter.setAddViewOnClickListener(addViewOnClickListener);

        if (required) {

            requiredTextView.setVisibility(View.VISIBLE);
            if (attrMode == 1) {
                requiredTextView.setText("(必填) (单选)");
            }
            if (attrMode == 2) {
                requiredTextView.setText("(必填) (多选)");
            }


        } else {
            if (attrMode == 3) {
                requiredTextView.setVisibility(View.INVISIBLE);
            }
        }

        showContent();
        getGoodsAttrInfo();

        if ("quality".equals(attrType)) {
            setTitle("添加品质");
        } else {
            setTitle("添加属性");
        }
    }

    private AddQualityViewController addQualityViewController;

    private AddQualityViewController.OnQualityOnConfirmListener onQualityOnConfirmListener = new AddQualityViewController.OnQualityOnConfirmListener() {
        @Override
        public boolean onQualityConfirm(String qualityName, String qualityDescription) {
            for(Model quality:productQualityAdapter.getModels()){
                if(quality.getString("level").equals(qualityName)){
                    return false;
                }
            }
            Model m = new Model();
            m.set("level", qualityName);
            m.set("value", qualityDescription);
            m.set("editable",true);
            int position = productQualityAdapter.getModels().size();
            productQualityAdapter.getModels().add(m);
            productQualityAdapter.notifyDataSetChanged();
            selectedPosition = position;
            productQualityAdapter.setSelectedPosition(position);
            return true;
        }
    };

    private View.OnClickListener addViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (addQualityViewController == null) {
                addQualityViewController = new AddQualityViewController();
                addQualityViewController.setOnQualityConfirmListener(onQualityOnConfirmListener);
            }
            addQualityViewController.setActivity(getActivity());
            getActionSheet(true).setViewController(addQualityViewController);
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).show();
        }
    };

    private TextWatcher valueTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int length = valueEditText.getText().toString().length();
            if (length > 20) {
                wordCountTextView.setTextColor(wordCountTextView.getResources().getColor(R.color.red));
            } else {
                wordCountTextView.setTextColor(wordCountTextView.getResources().getColor(R.color.gray_99));

            }
            wordCountTextView.setText(length + "/20");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private CompoundButton.OnCheckedChangeListener singleSelectedCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                selectedPosition = singleButtonGroup.indexOf(buttonView);
            }
        }
    };


    @OnClick(R.id.ab_right_button)
    private View.OnClickListener onRightBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            switch (attrMode) {
                case CommonUtil.Sku.attrMode1singleSelect:
                    if ("quality".equals(attrType)) {
                        selectedPosition = productQualityAdapter.getSelectedPosition();
                    }
                    if (selectedPosition == -1) {
                        Toast.makeText(ContextProvider.getContext(), "您还没有选择", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Model model = listModel.get(selectedPosition);
                    selectedIds.add(model.getInt("id"));
                    selectedValues.add(model.getString("value"));
                    if ("quality".equals(attrType)) {
                        intent.putExtra(KEY_LEVEL, model.getString("level"));
                        intent.putExtra(KEY_SELECTED_VALUE, "【" + model.getString("level") + "】" + model.getString("value"));
                    } else {
                        intent.putExtra(KEY_SELECTED_VALUE, model.getString("value"));
                    }
                    break;
                case CommonUtil.Sku.attrMode2multiSelect:
                    String value = "";
                    int size = addMultiViews.size();
                    for (int i = 0; i < size; i++) {
                        CompoundButton multiView = addMultiViews.get(i);
                        if (multiView.isChecked()) {
                            selectedIds.add(listModel.get(i).getInt("id"));
                            selectedValues.add(listModel.get(i).getString("value"));
                            value = value + listModel.get(i).getString("value") + " ";
                        }
                    }
                    if (selectedIds.size() == 0) {
                        Toast.makeText(ContextProvider.getContext(), "您还没有选择,请选择1到多个", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra(KEY_SELECTED_VALUE, value);
                    break;
                case CommonUtil.Sku.attrMode3fillBlank:

                    int length = valueEditText.getText().toString().length();
                    if (length > 20 || length == 0) {
                        Toast.makeText(ContextProvider.getContext(), "字数必须大于0,且小于20", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String valueStr = valueEditText.getText().toString();
                    intent.putExtra(KEY_SELECTED_VALUE, valueStr);
                    selectedIds.add(listModel.get(0).getInt("id"));
                    selectedValues.add(valueStr);

                    break;
            }

            intent.putExtra(KEY_ATTR_ID, attrID);
            intent.putIntegerArrayListExtra(KEY_IDS, selectedIds);
            intent.putStringArrayListExtra(KEY_VALUES, selectedValues);
            if (getActivity() != null) {
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    };

    private void getGoodsAttrInfo() {

        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/sku/goods/getGoodsAttrInfo", goodsAttrInfoResultListener);
        req.setParser(new GenericModelParser());
        req.addParam("category_id", categoryId + "");
        req.addParam("attr_id", attrID + "");
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();

    }

    private HttpRequest.ResultListener<ModelResult<Model>> goodsAttrInfoResultListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if (data.isSuccess()) {
                editable = data.getModel().getBoolean("editable");
                listModel = (ArrayList) data.getModel().getList("list", Model.class);

                for (Model model : selectedModels) {
                    if (model.getInt("attr_id") == attrID) {
                        if (model.getInt("id") == 0) { //新增的属性 加到listModel
                            listModel.add(model);
                        }
                    }
                }

                switch (attrMode) {
                    case CommonUtil.Sku.attrMode1singleSelect:

                        if ("quality".equals(attrType)) {
                            listView.setVisibility(View.VISIBLE);
                            listView.setAdapter(productQualityAdapter);
                            productQualityAdapter.setEditable(editable);
                            listView.setOnItemClickListener(onItemClickListener);
                            if (selectedModels != null) {
                                for (int i = 0; i < selectedModels.size(); i++) {
                                    String level = selectedModels.get(i).getString("level");
                                    if(level != null){
                                        for (int j = 0; j < listModel.size(); j++) {
                                            if (selectedModels.get(i).getInt("id") == listModel.get(j).getInt("id") &&  level.equals(listModel.get(j).getString("level"))) {
                                                selectedPosition = j;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            productQualityAdapter.setSelectedPosition(selectedPosition);
                            productQualityAdapter.setModels(listModel);
                        } else {
                            singleSelect(listModel);
                        }

                        break;
                    case CommonUtil.Sku.attrMode2multiSelect:
                        multiSelected(listModel);
                        break;
                    case CommonUtil.Sku.attrMode3fillBlank:
                        flowContainer.setVisibility(View.GONE);
                        addPropertyContainer.setBackgroundColor(addPropertyContainer.getResources().getColor(R.color.gray_f4));
                        fillBlankContainer.setVisibility(View.VISIBLE);
                        valueEditText.setHint(listModel.get(0).getString("value"));
                        for (Model model : selectedModels) {
                            if (model.getInt("id") == listModel.get(0).getInt("id")) {
                                valueEditText.setText(model.getString("value"));
                            }
                        }

                        break;
                }

            }
            hideLoading();
        }
    };


    private void singleSelect(ArrayList<Model> listModel) {
        for (int i = 0; i < listModel.size(); i++) {
            CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_single_selection_green, null);
            flowContainer.addView(view);
            for (Model model : selectedModels) {
                if (model.getInt("id") == listModel.get(i).getInt("id") && model.getString("value").equals(listModel.get(i).getString("value"))) {
                    view.setChecked(true);
                    selectedPosition = i;
                }
            }
            view.setText("" + listModel.get(i).getString("value"));
            singleButtonGroup.add(view, view);
            addSingleViews.add(view);
        }
        if(editable) {
            View addSingleButton = getActivity().getLayoutInflater().inflate(R.layout.view_add_circle_button, null);
            addSingleButton.setOnClickListener(addSingleButtonListener);
            flowContainer.addView(addSingleButton);
        }
    }

    private List<CompoundButton> addMultiViews = new ArrayList<>();

    private void multiSelected(ArrayList<Model> listModel) {
        for (int i = 0; i < listModel.size(); i++) {
            CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_multiple_selection_green, null);
            flowContainer.addView(view);
            view.setText("" + listModel.get(i).getString("value"));
            for (Model model : selectedModels) {
                if (model.getInt("id") == listModel.get(i).getInt("id") && model.getString("value").equals(listModel.get(i).getString("value"))) {
                    view.setChecked(true);
//                    selectedPosition = i;
                }
            }
            addMultiViews.add(view);
        }
        if(editable) {
            View addMultiButton = getActivity().getLayoutInflater().inflate(R.layout.view_add_rect_button, null);
            addMultiButton.setOnClickListener(addMultiButtonListener);
            flowContainer.addView(addMultiButton);
        }
    }

    private List<CompoundButton> addSingleViews = new ArrayList<>();

    private View.OnClickListener addSingleButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View vv = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_property, null);
            final Dialog d = new AlertDialog.Builder(getActivity()).setView(vv).create();
            final EditText valueEditText = (EditText) vv.findViewById(R.id.value_edit_text);
            vv.findViewById(R.id.okay_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = valueEditText.getText().toString();
                    if (!TextUtils.isEmpty(value)) {
                        Model model = new Model();
                        model.set("id", 0);
                        model.set("value", value);
                        listModel.add(model);
                        CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_single_selection_green, null);
                        int addIndex = listModel.size() - 1;
                        flowContainer.addView(view, addIndex);
                        selectedPosition = listModel.size() - 1;
                        for (CompoundButton view1 : addSingleViews) {
                            view1.setChecked(false);
                        }
                        addSingleViews.add(view);
                        view.setChecked(true);
                        singleButtonGroup.add(view, view);
                        view.setText("" + value);
                    }
                    d.dismiss();
                }
            });

            vv.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
            valueEditText.setSelectAllOnFocus(true);
            Tools.softInput();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(d.getWindow().getAttributes());

//            lp.width = DimensionUtil.dip2px(wm.getDefaultDisplay().getWidth() - 800);
            lp.width = wm.getDefaultDisplay().getWidth() - 80;
            lp.height = 700;
            d.getWindow().setAttributes(lp);
        }
    };

    WindowManager wm = (WindowManager) ContextProvider.getContext().getSystemService(Context.WINDOW_SERVICE);

    private View.OnClickListener addMultiButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View vv = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_property, null);
            final Dialog d = new AlertDialog.Builder(getActivity()).setView(vv).create();
            final EditText valueEditText = (EditText) vv.findViewById(R.id.value_edit_text);
            vv.findViewById(R.id.okay_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = valueEditText.getText().toString();
                    if (!TextUtils.isEmpty(value)) {
                        Model model = new Model();
                        model.set("id", 0);
                        model.set("value", value);
                        listModel.add(model);
                        CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_multiple_selection_green, null);
                        int addIndex = listModel.size() - 1;
                        flowContainer.addView(view, addIndex);
//                        for (CompoundButton view1: addMultiViews){
//                            view1.setChecked(false);
//                        }
                        addMultiViews.add(view);
                        view.setChecked(true);
                        view.setText("" + value);
                    }
                    d.dismiss();

                }
            });

            vv.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
            valueEditText.setSelectAllOnFocus(true);
            Tools.softInput();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(d.getWindow().getAttributes());

//            lp.width = DimensionUtil.dip2px(wm.getDefaultDisplay().getWidth() - 800);
            lp.width = wm.getDefaultDisplay().getWidth() - 80;
            lp.height = 700;
            d.getWindow().setAttributes(lp);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
            final Model m = productQualityAdapter.getItem(i);
            if (!m.getBoolean("editable")) {
                return;
            }

            AddQualityViewController vc = new AddQualityViewController();
            vc.setActivity(getActivity());
            vc.setOnQualityConfirmListener(new AddQualityViewController.OnQualityOnConfirmListener() {
                @Override
                public boolean onQualityConfirm(String qualityName, String qualityDescription) {
//                    int id = m.getInt("id");
//                    for(Model quality:productQualityAdapter.getModels()){
//                        if(quality.getInt("id") != id){
//                            if(quality.getString("level").equals(qualityName)){
//                                return false;
//                            }
//                        }
//                    }
                    m.set("level", qualityName);
                    m.set("value", qualityDescription);
                    productQualityAdapter.setSelectedPosition(i);
                    productQualityAdapter.notifyDataSetChanged();
                    return true;//TODO
                }
            });
            vc.setName(m.getString("level"));
            vc.setDescription(m.getString("value"));
            getActionSheet(true).setViewController(vc);
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).show();
        }
    };

}
