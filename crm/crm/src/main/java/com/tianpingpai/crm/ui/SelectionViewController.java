package com.tianpingpai.crm.ui;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.SelectionAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.user.UserModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@Layout(id = R.layout.vc_selection)
public class SelectionViewController extends BaseViewController {

    private TextView emptyTextView;

    private SelectionAdapter selectionAdapter = new SelectionAdapter();

    public interface OnSelectionListener {
        void onSelected(int position);
        void onMultiSelectionConfirmed(Collection<Integer> ids);
    }

    private int selection;

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void setData(String[] data) {
        selectionAdapter.setData(data);
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

    public void setSubMarkers(HashMap<UserModel,HashSet<UserModel>> subs){
        selectionAdapter.setSelectedSub(subs);
    }

    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    private boolean multiSelection;
    private OnSelectionListener onSelectionListener;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
        selectionAdapter.setParent(user);
    }

    private UserModel user;

    public void setSelectionSet(HashSet<UserModel> selectionSet){
        selectionAdapter.setSelectedUsers(selectionSet);
    }


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
                onSelectionListener.onMultiSelectionConfirmed(selectionAdapter.getSelection());
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
        if(multiSelection){
            doneButton.setVisibility(View.VISIBLE);
        }else{
            doneButton.setVisibility(View.GONE);
        }
        doneButton.setOnClickListener(doneButtonListener);
        ListView selectionListView = (ListView) getView().findViewById(R.id.selection_list_view);
        emptyTextView = (TextView)getView().findViewById(R.id.empty_text_view);
        selectionAdapter.setSelection(selection);
        selectionListView.setAdapter(selectionAdapter);
        selectionListView.setOnItemClickListener(onItemClickListener);
        loadMarketers();
    }

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
                getViewTransitionManager().popViewController(SelectionViewController.this);
            }else{
                UserModel user = selectionAdapter.getMarketers().get(i);
                if(user == UserManager.getInstance().getCurrentUser()){
                    selectionAdapter.toggleSelection(i);
                    return;
                }

                ii = i;
                loadPuisneData(user);

//                    }else{
//                        selectionAdapter.toggleSelection(i);
//                    }
//                }
            }
        }
    };

    private int ii;

    private HttpRequest.ResultListener<ListResult<UserModel>> marketersListener = new HttpRequest.ResultListener<ListResult<UserModel>>() {
        @Override
        public void onResult(HttpRequest<ListResult<UserModel>> request, ListResult<UserModel> data) {
            if(data.isSuccess()){
                if(request.getAttachment(UserModel.class) == UserManager.getInstance().getCurrentUser()){
                    data.getModels().add(0,UserManager.getInstance().getCurrentUser());
                }
                selectionAdapter.setMarketers(data.getModels());
                Log.e("model_size",data.getModels().size()+"");
                if(data.getModels().size()==0){
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private void loadMarketers(){
        String url = ContextProvider.getBaseURL() + "/crm/marketer/getSubMarketer";
        if(user == null){
            return;//TODO
        }
        HttpRequest<ListResult<UserModel>> req = new HttpRequest<>(url,marketersListener);
        req.addParam("marketer_id",user.getId() + "");
        req.addParam("pageNo","1");
        req.addParam("pageSize","500");
        req.setAttachment(user);
        req.setParser(new ListParser<>(UserModel.class));
        VolleyDispatcher.getInstance().dispatch(req);
    }


    private void loadPuisneData(UserModel userModel){
        String url = ContextProvider.getBaseURL() + "/crm/marketer/getSubMarketer";
        HttpRequest<ListResult<UserModel>> req = new HttpRequest<>(url,marketersPuisneListener);
        req.addParam("marketer_id",userModel.getId() + "");
        req.addParam("pageNo","1");
        req.addParam("pageSize", "500");
        req.setAttachment(userModel);
        req.setParser(new ListParser<>(UserModel.class));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<UserModel>> marketersPuisneListener = new HttpRequest.ResultListener<ListResult<UserModel>>() {
        @Override
        public void onResult(HttpRequest<ListResult<UserModel>> request, ListResult<UserModel> data) {
        if(data.isSuccess()){
            Log.e("model_size",data.getModels().size()+"");
            if(data.getModels().size()!=0){

                UserModel um = request.getAttachment(UserModel.class);
                if(um!=null){
                    SelectionViewController svc = new SelectionViewController();
                    svc.setViewTransitionManager(getViewTransitionManager());
                    svc.setUser(um);
                    svc.setMultiSelection(true);
                    svc.setSelectionSet(selectionAdapter.getSelectedUserSet());
                    svc.setSubMarkers(selectionAdapter.getSubMarketers());
                    svc.setActivity(getActivity());
                    getViewTransitionManager().pushViewController(svc);
                    svc.setOnSelectionListener(new OnSelectionListener() {
                        @Override
                        public void onSelected(int position) {

                        }

                        @Override
                        public void onMultiSelectionConfirmed(Collection<Integer> ids) {
                            selectionAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }else{
                selectionAdapter.toggleSelection(ii);
            }
        }
        }
    };

}
