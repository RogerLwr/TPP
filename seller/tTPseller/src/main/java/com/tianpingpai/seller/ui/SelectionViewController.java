package com.tianpingpai.seller.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.BillTypesAdapter;
import com.tianpingpai.seller.adapter.SelectionAdapter;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Layout(id = R.layout.vc_selection)
public class SelectionViewController extends BaseViewController {

    private TextView emptyTextView;

    @Binding(id = R.id.title_text_view)
    private TextView titleTextView;

    private String mTitle;

    public void setBillTitle(String title){
        mTitle = title;
    }

    boolean isBillType;
    public void setIsBillType(boolean isBillType){
        this.isBillType = isBillType;
    }
    public void setSelectionSet(HashSet<Model> selectionSet){
        billTypesAdapter.setSelectedBillTypes(selectionSet);
    }

    private SelectionAdapter selectionAdapter = new SelectionAdapter();
    private BillTypesAdapter billTypesAdapter = new BillTypesAdapter();

    public interface OnSelectionListener {
        void onSelected(int position);
//        void onMultiSelectionConfirmed(Collection<Integer> ids);
    }

    private int selectionPos;

    public void setSelectionPos(int selectionPos) {
        this.selectionPos = selectionPos;
    }

    private HashSet<Integer> selections;
    public void setSelections(HashSet<Integer> selections){
        this.selections = selections;
    }

    public void setInCount(int inCount){
        billTypesAdapter.setInCount(inCount);
    }

    public void setData(String[] data) {
        selectionAdapter.setData(data);
    }
    public void setBillTypes(List<Model> modelData) {
        billTypesAdapter.setBillTypes(modelData);
    }

    public interface OnBackListener{
        /**
         * 返回上个界面 并把上个页面的选中 的背景颜色去掉
         */
        void onBackClick();
    }
    OnBackListener onBackListener;
    public void setOnBackListener(OnBackListener onBackListener){
        this.onBackListener = onBackListener;
    }

    public void setOnSelectionListener(OnSelectionListener onSelectionListener) {
        this.onSelectionListener = onSelectionListener;
    }

    private boolean multiSelection;
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }
    private OnSelectionListener onSelectionListener;

    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewTransitionManager().popViewController(SelectionViewController.this);
            if(onBackListener != null) {
                onBackListener.onBackClick();
            }
        }
    };

    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onSelectionListener != null){
            }
            if(onBackListener != null) {
                onBackListener.onBackClick();
            }
            getViewTransitionManager().popViewController(SelectionViewController.this);
        }
    };

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        hideActionBar();
        getView().findViewById(R.id.back_button).setOnClickListener(backButtonListener);
        View doneButton = getView().findViewById(R.id.done_button);
        titleTextView.setText("筛选"+mTitle);
        doneButton.setOnClickListener(doneButtonListener);
        ListView selectionListView = (ListView) getView().findViewById(R.id.selection_list_view);
        emptyTextView = (TextView)getView().findViewById(R.id.empty_text_view);
        selectionListView.setOnItemClickListener(onItemClickListener);
        if(isBillType){

//            loadBillTypes();
            selectionListView.setAdapter(billTypesAdapter);

        }else {

//            selectionAdapter.setSelections(selections);
            selectionAdapter.setSelectionPos(selectionPos);
            selectionAdapter.setMultiSelection(multiSelection);
            selectionListView.setAdapter(selectionAdapter);
        }

    }


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

                billTypesAdapter.setInCount(inCount);
                billTypesAdapter.setBillTypes(billTypes);

            }else{



            }
            hideLoading();
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(!multiSelection){
                if (onSelectionListener != null) {
                    onSelectionListener.onSelected(i);
                    if(onBackListener != null) {
                        onBackListener.onBackClick();
                    }
                }
                if(selectionAdapter != null){
                    selectionAdapter.setSelectionPos(i);
                    selectionAdapter.notifyDataSetChanged();
                }
//                getViewTransitionManager().popViewController(SelectionViewController.this);
            }else{

                if(isBillType){
                    billTypesAdapter.toggleSelection(i);
                }else {
                    selectionAdapter.toggleSelection(i);
                }

            }
        }
    };





}
