package com.tianpingpai.crm.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListStringParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

@SuppressWarnings("unused")
public class SelectBankViewController extends BaseViewController {
    public interface OnSelectBankListener {
        void onSelectBank(String s,int id);
    }
    {
        setLayoutId(R.layout.vc_select_date);
    }

    private BanksAdapter bankAdapter;
    private OnSelectBankListener onSelectBankListener;
    private ActionSheet actionSheet;


    public void setOnSelectBankListener(OnSelectBankListener bankListener) {
        this.onSelectBankListener = bankListener;

    }

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView bankListView = (ListView)rootView.findViewById(R.id.date_list_view);
        bankAdapter = new BanksAdapter();
        bankListView.setAdapter(bankAdapter);
        bankListView.setOnItemClickListener(bankListViewOnItemClickListener);
        View actionBar = setActionBarLayout(R.layout.ab_select_city);
        actionBar.findViewById(R.id.ab_close_button).setOnClickListener(closeButtonListener);
        loadDate();
    }

    public void loadDate(){
        String url = URLApi.getBaseUrl()+"/crm/customer/get_banklist";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,listener);
        JSONListStringParser parser = new JSONListStringParser();

        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>(){
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.isSuccess()){
                Log.e("adasdasad",data.getModels().toString());
                bankAdapter.setData(data);
            }
            hideLoading();
        }
    };

    private AdapterView.OnItemClickListener bankListViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            actionSheet.dismiss();
            if(onSelectBankListener!=null){
                onSelectBankListener.onSelectBank(bankAdapter.getItem(position).getString("name"), position);
            }
        }
    };

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };

    class BanksAdapter extends ModelAdapter<Model>{

        @Override
        protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
            @SuppressLint("InflateParams") View convertView = getActivity().getLayoutInflater().inflate(R.layout.item_date, null);
            return new BanksViewHolder(convertView);
        }

        private class BanksViewHolder implements ViewHolder<Model>{
            private View view;
            @Binding(id = R.id.item_date_tv)
            TextView bankName;
            Model model;
            Binder binder = new Binder();
            BanksViewHolder(View v){
                v.setTag(this);
                this.view = v;
                binder.bindView(this,view);
            }

            @Override
            public void setModel(Model m) {
                this.model = m;
//                binder.bindData(model);
                bankName.setText(model.getString("name"));
            }

            @Override
            public View getView() {
                return view;
            }
        }
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        Log.e("onBackKeyDown", "--------------");

        if (actionSheet != null && actionSheet.isShowing()){
            actionSheet.dismiss();

            return true;
        }else{
            Log.e("onBackKeyDown","--------------");

            return super.onBackKeyDown(a);
        }
    }
}
