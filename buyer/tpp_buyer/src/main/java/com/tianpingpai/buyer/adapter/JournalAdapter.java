package com.tianpingpai.buyer.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

public class JournalAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new JournalViewHolder();
    }
    @SuppressWarnings("unused")
    private class JournalViewHolder implements ViewHolder<Model> {

        private View view;

        @Binding(id = R.id.journal_title_text_view,format = "{{title}}")
        private TextView journalTitleTextView;
        @Binding(id = R.id.goods_money_text_view,format = "¥{{mny}}")
        private TextView goodsMoneyTextView;
        @Binding(id = R.id.pay_time_text_view,format = "{{time}}")
        private TextView payTimeTextView;
        @Binding(id = R.id.journal_description_text_view,format = "{{description}}")
        private TextView journalDescriptionTextView;
        @Binding(id = R.id.order_status_text_view,format = "{{status}}")
        private TextView orderStatusTextView;

        private Model m = new Model();
        private Binder binder = new Binder();

        JournalViewHolder(){
            view = View.inflate(ContextProvider.getContext(), R.layout.item_journal,null);
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model model) {
            this.m = model;
            binder.bindData(model);
            if(TextUtils.isEmpty(model.getString("description"))){
                journalDescriptionTextView.setText("描述:无");
            }else{
                journalDescriptionTextView.setText("描述:"+model.getString("description"));
            }
        }

        @Override
        public View getView() {
            return view;
        }

        @OnClick(R.id.orderId_linearlayoutview)
        private View.OnClickListener orderIdOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        };
    }
}
