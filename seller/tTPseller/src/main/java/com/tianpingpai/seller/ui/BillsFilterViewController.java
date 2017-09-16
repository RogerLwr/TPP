package com.tianpingpai.seller.ui;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.tianpingpai.seller.adapter.BillFilterCategoryAdapter;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.OnSelectListener;
import com.tianpingpai.widget.SelectDateViewController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Layout(id = R.layout.vc_bills_filter)
public class BillsFilterViewController extends BaseViewController {
    // 订单状态  商家类型
    BillFilterCategoryAdapter billFilterCategoryAdapter = new BillFilterCategoryAdapter();

    @Binding(id = R.id.start_time_text_view)
    private TextView beginTimeTextView;
    @Binding(id = R.id.end_time_text_view)
    private TextView endTimeTextView;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private ActionSheet actionSheet;
    private String beginDate;
    private String endDate;

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }
    public interface OnConditionSelectedListener{
        void onConditionSelected(String beginDate, String endDate, String categorys, String status);
    }

    OnConditionSelectedListener onConditionSelectedListener;

    public void setOnConditionSelectedListener(OnConditionSelectedListener listener){
        onConditionSelectedListener = listener;
    }

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        hideActionBar();
        ListView listView = (ListView) getView().findViewById(R.id.filter_list_view);
        billFilterCategoryAdapter.setSelections(selections);
        listView.setAdapter(billFilterCategoryAdapter);
        listView.setOnItemClickListener(itemClickListener);

//        beginTimeTextView.setText("2015-01-01");
//        endTimeTextView.setText(sdf.format(new Date()));

        getView().findViewById(R.id.back_button).setOnClickListener(backButtonListener);
        getView().findViewById(R.id.reset_button).setOnClickListener(resetButtonListener);
        getView().findViewById(R.id.done_button).setOnClickListener(doneButtonListener);
        loadBillTypes();

    }

    private HashSet<Model> selectedBillTypes = new HashSet<>();
    private HashSet<Integer> selections = new HashSet<>();

    SelectionViewController selectionViewController;

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {

            selectionViewController = new SelectionViewController();

            String[] data = null;
            billFilterCategoryAdapter.setSelection(i);
            int position = 0;
            selectionViewController.setBillTitle(billFilterCategoryAdapter.cats[i]);
            switch (i) {
                case 0:
                    selectionViewController.setBillTypes(billTypes);
                    selectionViewController.setInCount(inCount);
                    position = billFilterCategoryAdapter.selectedBillType;
                    selectionViewController.setIsBillType(true);
                    selectionViewController.setMultiSelection(true);
                    selectionViewController.setSelectionSet(selectedBillTypes);

                    break;
                case 1:
                    data = BillFilterCategoryAdapter.billStatus;
                    selectionViewController.setData(data);
                    position = billFilterCategoryAdapter.selectedBillStatus;
                    selectionViewController.setMultiSelection(false);
                    selectionViewController.setSelectionPos(position);
                    break;
            }
            selectionViewController.setOnBackListener(new SelectionViewController.OnBackListener() {
                @Override
                public void onBackClick() {
                    billFilterCategoryAdapter.setSelection(-1); //返回时把选中的颜色恢复成白色
                    Log.e("xx", "102----------billTypes=" + selectedBillTypes);
                    billFilterCategoryAdapter.setSelectedBillTypes(selectedBillTypes);
                    billFilterCategoryAdapter.setSelections(selections);
                }
            });

            selectionViewController.setOnSelectionListener(new SelectionViewController.OnSelectionListener() {
                @Override
                public void onSelected(int position) {
                    switch (i) {
                        case 0:
                            billFilterCategoryAdapter.selectedBillType = position;//TODO
                            break;
                        case 1:
                            billFilterCategoryAdapter.selectedBillStatus = position;
                            selectedPos = position;
                            break;
                    }
                    billFilterCategoryAdapter.notifyDataSetChanged();
                }

//                @Override
//                public void onMultiSelectionConfirmed(Collection<Integer> ids) {
//                    billFilterCategoryAdapter.notifyDataSetChanged();
//                }
            });
            selectionViewController.setSelectionPos(position);
            selectionViewController.setActivity(getActivity());
            getViewTransitionManager().pushViewController(selectionViewController);
        }
    };

    private int selectedPos = 0;

    View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };

    View.OnClickListener resetButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            beginDate = "";
            endDate = "";
            beginTimeTextView.setText("请选择");
            endTimeTextView.setText("请选择");
            selectedBillTypes.clear();
            billFilterCategoryAdapter.notifyDataSetChanged();

        }
    };

    View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(beginDate) || TextUtils.isEmpty(endDate)){
                Toast.makeText(ContextProvider.getContext(), "必须同时选择开始和结束日期", Toast.LENGTH_LONG).show();
                return;
//                beginDate = "2015-01-01";
//                endDate = CommonUtil.Date.sdf_yyyy_MM_dd.format(new Date());
            }
            if(TextUtils.isEmpty(getSelectedCategorys())){
                Toast.makeText(ContextProvider.getContext(), "您还未选择类型", Toast.LENGTH_LONG).show();
                return;
            }
            onConditionSelectedListener.onConditionSelected(beginDate, endDate, getSelectedCategorys(), getSelectedStatus());
            actionSheet.dismiss();
        }
    };

    public String getSelectedCategorys(){
        String categorys = "";
        if( selectedBillTypes != null){
            for (Model model :selectedBillTypes){
                categorys = categorys + model.getInt("id") + ",";
            }
            if( !TextUtils.isEmpty(categorys) ){
                categorys = categorys.substring(0, categorys.length()-1);
            }
        }
        return categorys;
    }

    public String getSelectedStatus(){
        return "" + (selectedPos - 1);
    }

    private SelectDateViewController startDateController = new SelectDateViewController();
    private SelectDateViewController endDateController = new SelectDateViewController();
    boolean isBeginTimeSelected = false;
    private OnSelectListener startDateSelectionListener = new OnSelectListener() {
        public void onSelect() {
            beginDate = startDateController.getSelectedYear()+ "-" + startDateController.getSelectedMonth() + "-"+startDateController.getSelectedDay();
            beginTimeTextView.setText(beginDate);
            isBeginTimeSelected = true;
        }
    };

    private OnSelectListener endDateSelectedListener = new OnSelectListener() {
        public void onSelect() {
            endDate = endDateController.getSelectedYear()+ "-" + endDateController.getSelectedMonth() + "-"+endDateController.getSelectedDay();
            endTimeTextView.setText(String.format("%s", endDate));
        }
    };
    @OnClick(R.id.start_time_text_view)
    private View.OnClickListener onBeginTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActionSheet(true).setViewController(startDateController);
            getActionSheet(true).setHeight(DimensionUtil.dip2px(300));
            startDateController.setOnSelectListener(startDateSelectionListener);
            startDateController.setActivity(getActivity());
            startDateController.setActionSheet(getActionSheet(true));
            startDateController.setShowDay(true);
//            getViewTransitionManager().pushViewController(startDateController);
            getActionSheet(true).show();
        }
    };

    @OnClick(R.id.end_time_text_view)
    private View.OnClickListener onEndTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if( !isBeginTimeSelected ){
                Toast.makeText(ContextProvider.getContext(), "请先选择开始日期", Toast.LENGTH_LONG).show();
                return;
            }
            getActionSheet(true).setViewController(endDateController);
            getActionSheet(true).setHeight(DimensionUtil.dip2px(300));
            endDateController.setActivity(getActivity());
            endDateController.setActionSheet(getActionSheet(true));
            endDateController.setShowDay(true);
            endDateController.setOnSelectListener(endDateSelectedListener);
            getActionSheet(true).show();
        }
    };

    private void loadBillTypes(){

        String url = ContextProvider.getBaseURL() + "/api/user/account/categorys";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,httpListener);
        req.setParser(new GenericModelParser());
        req.addParam("grade",String.valueOf(UserManager.getInstance().getCurrentUser().getGrade()));
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    int inCount = 0;
    List<Model> billTypes = new ArrayList<>();

    private HttpRequest.ResultListener<ModelResult<Model>> httpListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {

            if(data.isSuccess()){

                billTypes.clear();

                Model model = data.getModel();
                inCount = 0;
                Model typeIn = model.getModel("categorys_in");
                List<Model> typeInChilds = typeIn.getList("childCategorys", Model.class);
                for (Model typeInChild : typeInChilds){
                    typeInChild.set("parent_id", typeIn.getInt("id"));
                    typeInChild.set("parent_name", typeIn.getString("name"));

                    billTypes.add(typeInChild);
                    inCount ++;

                }


                Model typeOut = model.getModel("categorys_out");
                List<Model> typeOutChilds = typeOut.getList("childCategorys", Model.class);
                for (Model typeOutChild : typeOutChilds){
                    typeOutChild.set("parent_id", typeOut.getInt("id"));
                    typeOutChild.set("parent_name", typeOut.getString("name"));

                    billTypes.add(typeOutChild);

                }

                billFilterCategoryAdapter.setBillTypeModels(billTypes);

            }else{



            }
            hideLoading();
        }
    };


}
