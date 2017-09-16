package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.CustomerClaimViewController;
import com.tianpingpai.crm.ui.CustomerDetailViewController;
import com.tianpingpai.crm.ui.CustomerLogViewController;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.parser.ParserDescNoResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.user.UserModel;

public class CustomerClaimAdapter extends ModelAdapter<Model> {

    public void setClaimNum(int claimNum) {
        this.claimNum = claimNum;
    }

    private int claimNum;
    private FragmentActivity activity;

    public void setActivity(FragmentActivity a){
        this.activity = a;
    }


    private CustomerClaimViewController ccc;
    @SuppressWarnings("unused")
    private class CustomerClaimViewHolder implements ViewHolder<Model>{
        @Binding(id = R.id.store_name_text_view,format = "{{sale_name}}")
        private TextView store_name_text_view;
        @Binding(id = R.id.register_status_text_view, format = "{{is_register}}")
        private TextView registerStatusTextView;
        @Binding(id = R.id.user_name_text_view, format = "客户姓名: {{display_name}}")
        private TextView usernameTextView;
        @Binding(id = R.id.contanct_text_view, format = "客户电话: {{phone}}")
        private TextView contactTextView;
        @Binding(id = R.id.address_text_view, format = "客户地址: {{sale_address}}")
        private TextView addressTextView;
        @Binding(id = R.id.user_type_text_view, format = "客户类型: {{user_type}}")
        private TextView userTypeTextView;
        @Binding(id = R.id.marget_manager_text_view, format = "{{manage_name}}")
        private TextView margetManagerTextView;

        Button customerClaimBtn;
        Button lookLogButton;

        private View view;
        private Model model;
        Binder binder = new Binder();

        CustomerClaimViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this, view);

            customerClaimBtn = (Button)v.findViewById(R.id.customer_claim_button);
            lookLogButton = (Button) v.findViewById(R.id.look_log_button);
        }

        //认领客户
        @OnClick(R.id.customer_claim_button)
        private View.OnClickListener customerClaimOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDialog("",false,model);
            }
        };

        //查看日志
        @OnClick(R.id.look_log_button)
        private View.OnClickListener lookLogOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ContainerActivity.class);
                i.putExtra(ContainerActivity.KEY_CONTENT,CustomerLogViewController.class);
                CustomerModel customerModel = new CustomerModel();
                customerModel.setUserId(model.getInt("user_id"));
                customerModel.setUserType(model.getInt("user_type"));
                i.putExtra(CustomerDetailViewController.KEY_CUSTOMER,customerModel);
                activity.startActivity(i);

            }
        };

        @Override
        public void setModel(Model m) {
            this.model = m;
            binder.bindData(model);
            userTypeTextView.setText(model.getString("user_type").equals("0") ? "客户类型: 卖家" : "客户类型: 买家");
            registerStatusTextView.setText(model.getString("is_register").equals("0") ? "未注册" : "已注册");
            margetManagerTextView.setText(model.getString("manage_name").equals("") ? "无" : model.getString("manage_name"));
            if(0==model.getInt("claim_flag")){
                customerClaimBtn.setBackgroundColor(ContextProvider.getContext().getResources().getColor(R.color.gray_66));
//                customerClaimBtn.setBackground(ContextProvider.getContext().getResources().getDrawable(R.drawable.btn_150916_green_conners5_selector));
                customerClaimBtn.setClickable(false);
            }else{
                customerClaimBtn.setBackgroundDrawable(ContextProvider.getContext().getResources().getDrawable(R.drawable.btn_150916_green_conners5_selector));
//                customerClaimBtn.setBackgroundColor(ContextProvider.getContext().getResources().getColor(R.color.gray_66));
                customerClaimBtn.setClickable(true);
            }

        }

        @Override
        public View getView() {
            return view;
        }
    }


    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View convertView = activity.getLayoutInflater().inflate(R.layout.item_customer_claim, null);
        return new CustomerClaimViewHolder(convertView);
    }

    private void showDialog(String message, final boolean hide,final Model model){
        final ActionSheetDialog dialog = new ActionSheetDialog();
        dialog.setActionSheet(new ActionSheet());
        dialog.setMessage(message);
        dialog.setCancelButtonHidden(hide);
        dialog.setActivity(activity);
        dialog.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认认领客户
                dialog.dismiss();
//                        ccc.loadCustomers(1);
                if(!hide){
                    claimCustomer(model);
                }
            }
        });
        dialog.setTitle("确定认领该用户吗?");
        dialog.show();
    }

    private void claimCustomer(Model model){
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String url = URLApi.getBaseUrl()+"/crm/customer/claim";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url ,claimListener);
        req.setParser(new ParserDescNoResult());
        req.setAttachment(model);
        req.addParam("id",model.getInt("id")+"");
        req.addParam("accessToken",user.getAccessToken());
        VolleyDispatcher.getInstance().dispatch(req);
        Log.e("url====",req.getUrl());
    }

    private HttpRequest.ResultListener<ModelResult<Model>> claimListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                Toast.makeText(ContextProvider.getContext(),"认领成功!",Toast.LENGTH_LONG).show();
                getModels().remove(request.getAttachment(Model.class));
                notifyDataSetChanged();
                claimNum++;
            }else{
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_LONG).show();
            }
        }
    };
}
