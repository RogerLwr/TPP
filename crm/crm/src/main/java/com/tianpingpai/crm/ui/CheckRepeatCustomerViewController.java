package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.SearchHistoryManager;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.SearchHistoryModel;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
@SuppressWarnings("unused")
@Layout(id= R.layout.view_controller_check_repeat)
public class CheckRepeatCustomerViewController extends CrmBaseViewController {

    public static final String KEY_DATE_TYPE = "Key.dateType";
    public static final String KEY_IS_ITEM_CLICK = "Key.isClick"; //是否可点击 (只有冲个人中心进来  item 才可以点击)
    public static final String KEY_TITLE = "Key.title";
    public static final String KEY_IS_VISIBLE_BTN = "Key.isVisibleBtn";

    private String dateType;
    private View emptyView;
    private ImageView deleteBtn;

    @Binding(id=R.id.clear_button)
    private TextView clearButton;
    @Binding(id=R.id.history_container)
    private LinearLayout historyContainer;

    @Binding(id=R.id.content_text_list)
    private ListView customersListView;
    @Binding(id=R.id.histroy_list)
    private ListView historyListView;

    private EditText searchContainer;

    private ArrayList<SearchHistoryModel> historyData;
    private ArrayList<SearchHistoryModel> newData = new ArrayList<>();

    private HistoryAdapter ha = new HistoryAdapter();
    private CustomerAdapter customersAdapter = new CustomerAdapter();

    private String queryKey = "";

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            search(queryKey,page,false);
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        dateType = getActivity().getIntent().getStringExtra(KEY_DATE_TYPE);
        String title = getActivity().getIntent().getStringExtra(KEY_TITLE);
        boolean isItemClick = getActivity().getIntent().getBooleanExtra(KEY_IS_ITEM_CLICK, false);
        boolean isVisibleBtn = getActivity().getIntent().getBooleanExtra(KEY_IS_VISIBLE_BTN, true);

        loadHistoryData();

        ha.setAdapterData(historyData);
        historyListView.setAdapter(ha);
        historyListView.setOnItemClickListener(historyOnItemClickListener);

        customersAdapter.setActivity(getActivity());
        customersAdapter.setPageControl(mPageControl);
        customersAdapter.setIsItemClick(isItemClick);
        customersAdapter.setIsVisibleBtn(isVisibleBtn);
        customersAdapter.setDateType(dateType);
        customersListView = (ListView) rootView.findViewById(R.id.content_text_list);
        customersListView.setAdapter(customersAdapter);

        View topView = setActionBarLayout(R.layout.ab_search_history_title);
        TextView rightBtn = (TextView) topView.findViewById(R.id.ab_right_button);
        rightBtn.setText("搜索");
        setTitle(title);

        emptyView = rootView.findViewById(R.id.empty_view);

        searchContainer = (EditText)topView.findViewById(R.id.input_search_container);
        deleteBtn = (ImageView)topView.findViewById(R.id.delete_search_key);
        deleteBtn.setOnClickListener(deleteSearchKeyOnClickListener);
        searchContainer.addTextChangedListener(searchTextChangedListener);

        rightBtn.setOnClickListener(searchButtonListener);

    }

    private TextWatcher searchTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                ha.setAdapterData(historyData);
                ha.notifyDataSetChanged();
                historyContainer.setVisibility(View.VISIBLE);
                customersListView.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
            }else{
                deleteBtn.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

            String key = s.toString().trim();
            if("".equals(key)){
                return ;
            }
            if(historyData.size()==0){
                return ;
            }

            newData.clear();
            for(int i = 0;i<historyData.size();i++){
                if(historyData.get(i).getName().contains(key)){
                    if(!historyData.get(i).getName().equals(key)){
                        newData.add(historyData.get(i));
                    }
                }
            }
            ha.setAdapterData(newData);
            ha.notifyDataSetChanged();
        }
    };

    public void loadHistoryData(){

        historyData = SearchHistoryManager.getInstance().getMarkets();
        if(historyData.size()==0){
            clearButton.setVisibility(View.GONE);
        }else{
            clearButton.setVisibility(View.VISIBLE);
        }

    }

    View.OnClickListener deleteSearchKeyOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            searchContainer.setText("");
        }
    };

    @OnClick(R.id.clear_button)
    View.OnClickListener clearHistoryOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            SearchHistoryManager.getInstance().mSearchHistoryDao.clearByStoreId(UserManager.getInstance().getCurrentUser().getId());
            clearButton.setVisibility(View.GONE);
            historyData.clear();
            ha.notifyDataSetChanged();
            historyListView.setVisibility(View.GONE);
        }
    };

    private AdapterView.OnItemClickListener historyOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            historyContainer.setVisibility(View.GONE);
            search(historyData.get(position).getName(), 1 ,false);
            searchContainer.setText(historyData.get(position).getName());
            customersListView.setVisibility(View.VISIBLE);
        }
    };

    class HistoryAdapter extends BaseAdapter{

        private ArrayList<SearchHistoryModel> data;

        public void setAdapterData(ArrayList<SearchHistoryModel> data){
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HistoryViewHolder hvh ;
            if(convertView==null){
                convertView = View.inflate(getActivity().getApplicationContext(),R.layout.item_history,null);
                hvh = new HistoryViewHolder();
                hvh.itemTextView=(TextView)convertView.findViewById(R.id.item_history_text_view);
                convertView.setTag(hvh);
            }else{
                hvh = (HistoryViewHolder)convertView.getTag();
            }
            hvh.itemTextView.setText(data.get(position).getName());

            return convertView;
        }
    }

    class HistoryViewHolder {
        TextView itemTextView;
    }

    View.OnClickListener searchButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if("".equals(searchContainer.getText().toString().trim())){
                return ;
            }
            String q = searchContainer.getText().toString().trim();
            search(q, 1 ,true);
            historyContainer.setVisibility(View.GONE);
            customersListView.setVisibility(View.VISIBLE);
        }
    };


    HttpRequest.ResultListener<ListResult<Model>> customersListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request,
                             ListResult<Model> data) {
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
            }
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                customersAdapter.clear();
            }
            if (data.isSuccess()) {
                if(data.getModels().size()!=0){

                    SearchHistoryModel shm = new SearchHistoryModel();
                    shm.setName(request.getAttachment(String.class));
                    shm.setId(UserManager.getInstance().getCurrentUser().getId());
                    SearchHistoryManager.getInstance().setCurrentMarket(shm);
                    customersAdapter.setData(data);
                    customersAdapter.setDateType(dateType);
                    String key = request.getAttachment(String.class);
//                    searchContainer.setText(key);
                    Selection.setSelection(searchContainer.getText(), key.length());
                    historyData.add(shm);
                    if (customersAdapter.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }

                    InputMethodManager m = (InputMethodManager) searchContainer.getContext().
                            getSystemService(FragmentActivity.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };

    private void search(String queryString, int page , boolean isSave) {
        queryKey = queryString;
        if (UserManager.getInstance().getCurrentUser() == null) {
            customersAdapter.setData(null);
//            refreshLayout.setRefreshing(false);
            return;
        }
        if(null==queryKey||"".equals(queryKey)){
            Toast.makeText(getActivity(),"搜索内容不能为空!",Toast.LENGTH_SHORT).show();
            return;
        }
        HttpRequest<ListResult<Model>> req;
        if(isSave){
            req = new HttpRequest<>(URLApi.Customer.getQueryCustomer(), customersListener);
        }else{
            req = new HttpRequest<>(URLApi.Customer.getQueryCustomer(), noCustomersListener);
        }
        try {
            req.addParam("query_condition", URLEncoder.encode(queryString, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONListParser parser = new JSONListParser();
        req.setParser(parser);
        req.setAttachment(queryString);

        req.addParam("user_type", "-1");//默认买家和卖家
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("pageSize", String.valueOf(10));
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());
        if (page == 1) {// 1 means refresh
            req.setAttachment(Boolean.TRUE);
        }
        VolleyDispatcher.getInstance().dispatch(req);
        Log.e("xx", "loading customers");
    }



    HttpRequest.ResultListener<ListResult<Model>> noCustomersListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request,
                             ListResult<Model> data) {
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
            }
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                customersAdapter.clear();
            }
            if (data.isSuccess()) {
                if(data.getModels()!=null && data.getModels().size()!=0){

                    SearchHistoryModel shm = new SearchHistoryModel();
                    shm.setName(request.getAttachment(String.class));
                    shm.setId(UserManager.getInstance().getCurrentUser().getId());
                    SearchHistoryManager.getInstance().setCurrentMarket(shm);
                    customersAdapter.setData(data);
                    customersAdapter.setDateType(dateType);
                    if (customersAdapter.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }
                    InputMethodManager m = (InputMethodManager) searchContainer.getContext().getSystemService(FragmentActivity.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };
}
