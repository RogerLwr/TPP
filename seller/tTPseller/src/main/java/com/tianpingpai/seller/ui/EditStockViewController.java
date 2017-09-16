package com.tianpingpai.seller.ui;


import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
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
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

@SuppressWarnings("unused")
@ActionBar(hidden = true)
@Statistics(page = "编辑库存")
@Layout(id = R.layout.view_controller_edit_stock)
public class EditStockViewController extends BaseViewController {

    @Binding(id = R.id.name_text_view)
    private TextView nameTextView;
    @Binding(id = R.id.stock_text_view)
    private TextView stockTextView;
    @Binding(id = R.id.stock_edit_text)
    private EditText stockEditText;
    @Binding(id = R.id.unit_text_view)
    private TextView unitTextView;

    private String name;
    private int stock;
    private int stockNum;
    private long pID;
    private String unit;

    public interface OnStockClickListener {
        void onOkClick();
        void onBgContainerClick();
    }

    private  OnStockClickListener onStockClickListener;

    public void setOnStockClickListener(OnStockClickListener onStockClickListener) {
        this.onStockClickListener = onStockClickListener;
    }

    public void setStock(int stock, String name, long pID, String unit) {
        this.stock = stock;
        this.name = name;
        this.pID = pID;
        this.unit = unit;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        nameTextView.setText(name);
        stockTextView.setText(stock + "");
        unitTextView.setText(unit);
    }

    private void addStock(){
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl()+"/api/sales/add_stock", addStockResultListener);
        req.setMethod(HttpRequest.POST);
        req.addParam("pid", "" + pID);
        req.addParam("number", ""+stockNum);
        req.setParser(new GenericModelParser());
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        showSubmitting();

    }

    private HttpRequest.ResultListener<ModelResult<Model>> addStockResultListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {

            if(data.isSuccess()){
                onStockClickListener.onOkClick();
            }else{
                ResultHandler.handleError(data,EditStockViewController.this);
            }
            hideSubmitting();
        }
    };


    @OnClick(R.id.bg_container)
    private View.OnClickListener bgContainerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Toast.makeText(ContextProvider.getContext(), "点击了空白处", Toast.LENGTH_SHORT).show();
            onStockClickListener.onBgContainerClick();
        }
    };
    @OnClick(R.id.edit_stock_relative)
    private View.OnClickListener editStockRelativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    @OnClick(R.id.minus_button)
    private View.OnClickListener minusBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String stockStr = stockEditText.getText().toString();
            if( !TextUtils.isEmpty(stockStr) ){
                int stockNum = Integer.parseInt(stockStr);
                if(stockNum > 0){
                    stockNum --;
                    stockEditText.setText(""+stockNum);
                }
            }else {
                stockEditText.setText("0");
            }
        }
    };
    @OnClick(R.id.add_button)
    private View.OnClickListener addBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String stockStr = stockEditText.getText().toString();
            if( !TextUtils.isEmpty(stockStr) ){
                int stockNum = Integer.parseInt(stockStr);
                    stockNum ++;
                    stockEditText.setText(""+stockNum);
            }else{
                stockEditText.setText("0");
            }
        }
    };
    @OnClick(R.id.okay_button)
    private View.OnClickListener okayBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String stockStr = stockEditText.getText().toString();
            if( TextUtils.isEmpty(stockStr) ){
                Toast.makeText(ContextProvider.getContext(), "增加的库存数不能为空", Toast.LENGTH_LONG).show();
                return;
            }else {
                stockNum = Integer.parseInt(stockStr);
                if(stockNum == 0){
                    Toast.makeText(ContextProvider.getContext(), "增加的库存数不能为0", Toast.LENGTH_LONG).show();
                    return;
                }else if(stockNum > 10000){
                    Toast.makeText(ContextProvider.getContext(), "增加的库存数不能大于10000", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            addStock();
        }
    };
}
