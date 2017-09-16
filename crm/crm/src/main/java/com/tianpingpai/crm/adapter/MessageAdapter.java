package com.tianpingpai.crm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class MessageAdapter extends ModelAdapter {

    @Override
    protected ViewHolder onCreateViewHolder(LayoutInflater inflater) {
        return new MessageAdapterViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    private class MessageAdapterViewHolder implements ViewHolder<Model>{
        private View view;
        @Binding(id = R.id.title_text_view,format = "{{type_name}}")
        private TextView titleTextView;
        @Binding(id = R.id.time_text_view,format = "{{send_dt}}")
        private TextView timeTextView;
        @Binding(id = R.id.content_text_view,format = "{{content}}")
        private TextView contentTextView;

        private Binder binder = new Binder();

        private MessageAdapterViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_message,null,false);
            binder.bindView(this,view);
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


