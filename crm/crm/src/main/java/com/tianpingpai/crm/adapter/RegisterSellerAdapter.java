package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.RegisterViewController;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
public class RegisterSellerAdapter extends ModelAdapter<Model> {

    private Activity activity;

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View convertView = activity.getLayoutInflater().inflate(R.layout.item_register_seller, null);
        return new RegisterViewHolder(convertView);
    }

    private class RegisterViewHolder implements ViewHolder<Model>{
        View view;
        Model model;
        Binder binder = new Binder();

        @Binding(id = R.id.shop_name_text_view,format = "{{sale_name}}")
        private TextView shopNameTextView;
        @Binding(id = R.id.check_status_text_view)
        private TextView checkStatusTextView;
        @Binding(id = R.id.seller_name_text_view,format = "客户姓名:{{display_name}}")
        private TextView sellerNameTextView;
        @Binding(id = R.id.seller_phone_text_view,format = "客户电话:{{phone}}")
        private TextView sellerPhoneTextView;
        @Binding(id = R.id.seller_address_text_view,format = "客户地址:{{address}}")
        private TextView sellerAddressTextView;
        @Binding(id = R.id.failed_reason_text_view,format = "失败原因:{{audit_remarks}}")
        private TextView failedReasonTextView;
        private Button lookButton;

        RegisterViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this,view);
            lookButton = (Button) view.findViewById(R.id.look_seller_button);
            lookButton.setOnClickListener(lookButtonListener);
        }
        private View.OnClickListener lookButtonListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, ContainerActivity.class);
                i.putExtra(ContainerActivity.KEY_CONTENT, RegisterViewController.class);
                i.putExtra(RegisterViewController.KEY_USER_ID,model.getInt("user_id")+"");
                i.putExtra(RegisterViewController.KEY_CHECK,true);
                if(1 == model.getInt("status") || 2 == model.getInt("status")){
                    i.putExtra(RegisterViewController.KEY_EDIT,true);
                }
                activity.startActivity(i);
            }
        };
        @Override
        public void setModel(Model model) {
            this.model = model;
            binder.bindData(model);
            if(1==model.getInt("status")){
                checkStatusTextView.setText("审核通过");
                failedReasonTextView.setVisibility(View.GONE);
                lookButton.setText("查看");
//                lookButton.setClickable(false);
            }else if(0==model.getInt("status")){
                checkStatusTextView.setText("审核失败");
                failedReasonTextView.setVisibility(View.VISIBLE);
                lookButton.setText("修改");
//                lookButton.setClickable(true);
            }else if(2==model.getInt("status")){
                checkStatusTextView.setText("待审核");
                failedReasonTextView.setVisibility(View.GONE);
                lookButton.setText("查看");
//                lookButton.setClickable(false);
            }

        }
        @Override
        public View getView() {
            return view;
        }
    }
}
