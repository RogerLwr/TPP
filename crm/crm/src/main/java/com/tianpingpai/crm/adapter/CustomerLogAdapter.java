package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

@SuppressWarnings("unused")
public class CustomerLogAdapter extends ModelAdapter<Model> {

    private Activity activity;
    public void setActivity(Activity a){
        this.activity = a;
    }

    @Override
    protected ViewHolder onCreateViewHolder(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View convertView = activity.getLayoutInflater().inflate(R.layout.item_customer_log, null);
        return new CustomerLogViewHolder(convertView);
    }

    @SuppressWarnings("unused")
    private class CustomerLogViewHolder implements ViewHolder<Model>{

        private View view;
        private Model model;
        private Binder binder = new Binder();

        @Binding(id = R.id.log_textView_title,format = "{{created}}")
        private TextView textView1;
        @Binding(id = R.id.log_textView_content,format = "{{description}}")
        private TextView textView2;

        CustomerLogViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model m) {
            this.model = m;
            binder.bindData(m);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
