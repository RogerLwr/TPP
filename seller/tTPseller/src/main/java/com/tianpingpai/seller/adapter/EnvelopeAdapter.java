package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EnvelopeAdapter extends ModelAdapter<Model> {

    private ListView listView;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat printFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new EnvelopeViewHolder();
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @SuppressWarnings("unused")
    private class EnvelopeViewHolder implements ViewHolder<Model> {

        private View view;
        @Binding(id = R.id.type_text_view)
        private TextView typeTextView;
        @Binding(id = R.id.amount_text_view,format = "￥{{mny|money}}")
        private TextView amountTextView;
        @Binding(id = R.id.origin_text_view,format = "{{description}}")
        private TextView originTextView;
        @Binding(id = R.id.date_text_view)
        private TextView dateTextView;
        @Binding(id = R.id.status_text_view)
        private TextView statusTextView;
        @Binding(id = R.id.open_button)
        private View openButton;
        private Model model;

        private EnvelopeViewHolder() {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_envelope, null);
            binder.bindView(this,view);
        }

        private Binder binder = new Binder();

        @Override
        public void setModel(Model model) {
            this.model = model;
            binder.bindData(model);
            String type = model.getInt("bonus_type") == 1 ? "系统红包" : "订单红包";
            typeTextView.setText(type);

            try {
                Date date = dateFormat.parse(model.getString("giving_time"));
                dateTextView.setText(printFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //TODO 1:open 2:not_open 3:expired
            int status = model.getInt("status");
            String statusText;
            String endTime = model.getString("end_time");
            try {
                Date end = dateFormat.parse(endTime);
                if(end.before(new Date())){
                    status = 3;
                    model.set("status",3);//TODO
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (status == 1) {
                statusText = "已领取";
                openButton.setVisibility(View.GONE);
                amountTextView.setVisibility(View.VISIBLE);
                statusTextView.setVisibility(View.VISIBLE);
            } else if (status == 2) {
                statusText = "未打开";
                amountTextView.setVisibility(View.GONE);
                statusTextView.setVisibility(View.GONE);
                openButton.setVisibility(View.VISIBLE);
            } else {
                amountTextView.setVisibility(View.GONE);
                statusTextView.setVisibility(View.VISIBLE);
                openButton.setVisibility(View.GONE);
                statusText = "已过期";
            }

            statusTextView.setText(statusText);
        }

        @Override
        public View getView() {
            return view;
        }

        @OnClick(R.id.open_button)
        View.OnClickListener openButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getModels().indexOf(model);
                listView.performItemClick(view, position, 0);
                view.performClick();
            }
        };
    }
}
