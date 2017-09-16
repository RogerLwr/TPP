package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.CommentModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
public class CustomerVisitAdapter extends ModelAdapter<Model>{

    @Override
    protected ViewHolder onCreateViewHolder(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View convertView = inflater.inflate(R.layout.item_comment1509, null);
        return new CustomerVisitViewHolder(convertView);
    }

    @SuppressWarnings("unused")
    private class CustomerVisitViewHolder implements ModelAdapter.ViewHolder<Model>{

        private View view;

        @Binding(id = R.id.name_text_view,format = "{{marketer_name}}")
        private TextView nameTextView;
        @Binding(id = R.id.date_text_view,format = "{{created_time}}")
        private TextView dateTextView;
        @Binding(id = R.id.content_text_view,format = "拜访内容:{{content}}")
        private TextView contentTextView;
        @Binding(id = R.id.address_text_view,format = "拜访地址:{{position}}")
        private TextView addressTextView;
        private CommentModel comment;
        Binder binder = new Binder();

        CustomerVisitViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this, view);

        }

        @Override
        public void setModel(Model m) {
            binder.bindData(m);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
