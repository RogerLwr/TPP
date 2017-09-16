package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class SignInAdapter extends ModelAdapter<Model>{
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new SignInViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    private class SignInViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.name_text_view,format = "{{display_name}}")
        private TextView nameTextView;
        @Binding(id = R.id.time_text_view,format = "{{created_time}}    来自{{phone_type}}")
        private TextView timeTextView;
        @Binding(id = R.id.address_text_view,format = "我的位置:{{position}}")
        private TextView addressTextView;
        @Binding(id = R.id.sign_in_addr_text_view,format = "签到:{{position}}")
        private TextView signInAddressTextView;

        private Binder binder = new Binder();

        @SuppressLint("InflateParams")
        SignInViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_sign_in, null, false);
            binder.bindView(this,view);
            nameTextView.setText(UserManager.getInstance().getCurrentUser().getName());
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
