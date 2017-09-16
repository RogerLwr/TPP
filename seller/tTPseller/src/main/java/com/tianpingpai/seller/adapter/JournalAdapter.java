package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class JournalAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new JournalViewHolder();
    }

    @SuppressWarnings("unused")
    private class JournalViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.type_text_view,format = "{{title}}")
        private TextView typeTextView;
        @Binding(id = R.id.amount_text_view,format = "{{mny|money}}")
        private TextView amountTextView;
        @Binding(id = R.id.date_text_view,format = "{{time}}")
        private TextView dateTextView;
        @Binding(id = R.id.status_text_view,format = "{{status}}")
        private TextView statusTextView;

        private Binder binder = new Binder();

        {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_journal,null);
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

    public boolean shouldLoad(){
        return getData() == null;
    }
}
