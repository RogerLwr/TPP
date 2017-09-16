package com.tianpingpai.seller.ui;


import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.PropertyAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

import java.util.ArrayList;
import java.util.List;

/**
 *  编辑 商品属性
 */
@Statistics(page = "商品属性")
@ActionBar(title = "添加商品属性",rightText = "保存")
@Layout(id = R.layout.view_controller_edit_prod_property)
public class EditProductPropertyViewController extends BaseViewController{

    public static final String KEY_CATEGORY_ID = "categoryId";
    public static final String KEY_SELECTED_ALL_MODEL = "Key.allModel";
    public static final String KEY_DESC = "remark";

    private int categoryId;
    private ArrayList<Integer> selectedIds;
    private ArrayList<String> selectedValues;

    @Binding(id = R.id.list_view)
    private ListView listView;

    private PropertyAdapter propertyAdapter = new PropertyAdapter();
    private String attrType;
    private String descStr;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        categoryId = a.getIntent().getIntExtra(KEY_CATEGORY_ID, 0);
        if(a.getIntent().getParcelableExtra(KEY_SELECTED_ALL_MODEL) != null){
            selectedWithValueModel = a.getIntent().getParcelableExtra(KEY_SELECTED_ALL_MODEL);
        }
        if(a.getIntent().getStringExtra(KEY_DESC) != null){
            descStr = a.getIntent().getStringExtra(KEY_DESC);
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        listView.setAdapter(propertyAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        getGoodsAttrByID();
    }

    @OnClick(R.id.ab_right_button)
    private View.OnClickListener onRightBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (Model model:models){
                if( TextUtils.isEmpty(model.getString("value")) && model.getBoolean("required")){
                    Toast.makeText(ContextProvider.getContext(), model.getString("name")+"不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Intent intent = new Intent();
            Log.e("xx","sm:" + selectedWithValueModel);
            intent.putExtra(KEY_SELECTED_ALL_MODEL, selectedWithValueModel);
            intent.putExtra(KEY_DESC, propertyAdapter.getRemark());
            if (getActivity() != null) {
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    };

    private void getGoodsAttrByID(){
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/sku/goods/getGoodsAttrByID", goodsAttrListener);
        req.addParam("category_id", "" + categoryId);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(true);
        parser.setIsList(true);
        req.setParser(parser);
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();

    }

    ArrayList<Model> models;

    private HttpRequest.ResultListener<ListResult<Model>> goodsAttrListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.isSuccess()){
                models = data.getModels();
                propertyAdapter.setModels(models);
                if(selectedWithValueModel != null){
                    propertyAdapter.setSelectedWithValueModel(selectedWithValueModel);
                }
                if( !TextUtils.isEmpty(descStr) ){
                    propertyAdapter.setRemark(descStr);
                }else {
                    propertyAdapter.notifyDataSetChanged();
                }
            }else{
                ResultHandler.handleError(data,EditProductPropertyViewController.this);
            }
            hideLoading();
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(propertyAdapter.isRemark(propertyAdapter.getItem(i))){
                InputTextViewController vc = new InputTextViewController();
                vc.setActivity(getActivity());
                vc.setNewRemark(descStr);
                vc.setOnInputCompleteListener(new InputTextViewController.OnInputCompleteListener() {
                    @Override
                    public void onInputComplete(String text) {
                        propertyAdapter.setRemark(text);
                        descStr = text;

                    }
                });
                getActionSheet(true).setViewController(vc);
                getActionSheet(true).setHeight(getView().getHeight());
                getActionSheet(true).show();
                return;
            }
            Intent intent = new Intent(ContextProvider.getContext(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, AddPropertyViewController.class);
            selectedModel = propertyAdapter.getItem(i);
            intent.putExtra(AddPropertyViewController.KEY_NAME, selectedModel.getString("name"));
            intent.putExtra(AddPropertyViewController.KEY_CATEGORY_ID, categoryId);
            attrId = selectedModel.getInt("id");
            intent.putExtra(AddPropertyViewController.KEY_ATTR_ID, attrId);
            attrMode = selectedModel.getInt("mode");
            intent.putExtra(AddPropertyViewController.KEY_MODE, attrMode);
            attrType = selectedModel.getString("type");
            intent.putExtra(AddPropertyViewController.KEY_TYPE, attrType);
            intent.putExtra(AddPropertyViewController.KEY_REQUIRED,selectedModel.getBoolean("required"));
            intent.putExtra(KEY_SELECTED_ALL_MODEL, selectedWithValueModel);
            getActivity().startActivityForResult(intent, AddPropertyViewController.REQUEST_CODE);
        }
    };

    Model selectedWithValueModel = new Model(); // 值回传 带着选中的值
    List<Model> selectedModels = new ArrayList<>();
    int attrMode;
    int attrId;
    Model selectedModel;

    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);
        if (requestCode == AddPropertyViewController.REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){

                setIsEditStatus(true);

                if(selectedWithValueModel.getList("selectedModels", Model.class) != null){
                    selectedModels = selectedWithValueModel.getList("selectedModels", Model.class);
                }else {
                    selectedWithValueModel.setList("selectedModels", selectedModels);
                }

                Log.e("xx", "188------"+selectedModels);

                String selectedValue = data.getStringExtra(AddPropertyViewController.KEY_SELECTED_VALUE);
                int attrID = data.getIntExtra(AddPropertyViewController.KEY_ATTR_ID, 0);
                selectedIds = data.getIntegerArrayListExtra(AddPropertyViewController.KEY_IDS);
                selectedValues = data.getStringArrayListExtra(AddPropertyViewController.KEY_VALUES);

                //看selectedModels 是否包含这个 id(attrID) 包含 则删除 不包含 则 new 一个Model
                ArrayList<Model> deleteList = new ArrayList<>();
                for(Model singleModel:selectedModels ){

                    if(singleModel.getInt("attr_id") == attrID){
                        deleteList.add(singleModel);
                    }
                }
                selectedModels.removeAll(deleteList);

                switch (attrMode) {
                    case CommonUtil.Sku.attrMode1singleSelect:


                        Model model1 = new Model();
                        model1.set("attr_id", attrID);
                        model1.set("id", selectedIds.get(0));
                        model1.set("value", selectedValues.get(0));
//                        model1.set("name", selectedModel.getString("name"));
                        model1.set("type", selectedModel.getString("type"));
                        if ("quality".equals(attrType)){
                            model1.set("level", data.getStringExtra(AddPropertyViewController.KEY_LEVEL));
                        }
                        selectedModels.add(model1);

                        break;
                    case CommonUtil.Sku.attrMode2multiSelect:

                        // 新增
                        for (int j=0; j<selectedIds.size(); j++ ){
                            Model model = new Model();
                            model.set("attr_id", attrID);
                            model.set("id", selectedIds.get(j));
                            model.set("value", selectedValues.get(j));
                            model.set("type", selectedModel.getString("type"));
                            if(j == 0){
//                                model.set("name", selectedModel.getString("name"));
                            }
                            selectedModels.add(model);

                        }

                        break;
                    case CommonUtil.Sku.attrMode3fillBlank:

                        Model model3 = new Model();
                        model3.set("attr_id", attrID);
                        model3.set("id", selectedIds.get(0));
                        model3.set("value", selectedValues.get(0));
//                        model3.set("name", selectedModel.getString("name"));
                        model3.set("type", selectedModel.getString("type"));
                        selectedModels.add(model3);
                        break;
                }

                selectedModel.set("value", selectedValue);
                Log.e("xx", "select:" + attrID + "value:" + selectedValue);

//                propertyAdapter.clear();
//                propertyAdapter.setModels(models);
                propertyAdapter.setSelectedWithValueModel(selectedWithValueModel);
                propertyAdapter.notifyDataSetChanged();
            }
        }
    }


    private ActionSheetDialog actionSheetDialog;
    @Override
    public boolean onBackKeyDown(Activity a) {
        if (isEditStatus()) {
            if (actionSheetDialog == null) {
                actionSheetDialog = new ActionSheetDialog();
                actionSheetDialog.setActionSheet(getActionSheet(true));
                actionSheetDialog.setTitle("正在编辑,是否退出?");
                actionSheetDialog.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionSheetDialog.dismiss();
                        getActivity().finish();

                    }
                });
                actionSheetDialog.setNegativeButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionSheetDialog.dismiss();
                    }
                });
            }
            actionSheetDialog.show();

            return true;
        }
        return false;
    }

    private boolean isEditStatus;

    private void setIsEditStatus(boolean status){
        this.isEditStatus = status;
    }

    private boolean isEditStatus() {

        return isEditStatus;

    }

}
