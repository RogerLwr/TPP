package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class NoticeAdapter extends ModelAdapter<Model> {
    @Override
    protected ModelAdapter.ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new ViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    private class ViewHolder implements ModelAdapter.ViewHolder<Model> {
        private View view;

        @Binding(id = R.id.tv_notice_content,format = "{{content}}")
        private TextView noticeContentTextView;

        @Binding(id = R.id.start_time_text_view,format = "{{beginTime}}")
        private TextView beginTimeTextView;

        @Binding(id = R.id.end_time_text_view,format = "{{_endTime}}")
        private TextView endTimeTextView;

        private ImageView outOfDateImageView;

        private Binder binder = new Binder();

        private ViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_notice, null);
            binder.bindView(this, view);
            outOfDateImageView = (ImageView) view.findViewById(R.id.iv_out_of_date);

        }

        @Override
        public void setModel(Model model) {
            //TODO
            model.set("_endTime",model.get("endTime"));
            binder.bindData(model);
            if(model.getBoolean("valid")){
                outOfDateImageView.setVisibility(View.INVISIBLE);
            }else {
                outOfDateImageView.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public View getView() {
            return view;
        }
    }
}
