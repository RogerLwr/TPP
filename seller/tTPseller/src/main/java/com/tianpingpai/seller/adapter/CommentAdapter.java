package com.tianpingpai.seller.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class CommentAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CommentViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    private class CommentViewHolder implements ViewHolder<Model> {
        private View view;

        @Binding(id = R.id.buyer_name_text_view, format = "{{username}}")
        private TextView buyerNameTextView;
        @Binding(id = R.id.time_text_view,format = "{{createdTime}}")
        private TextView timeTextView;
        @Binding(id = R.id.content_text_view)
        private TextView contentTextView;
        @Binding(id = R.id.rating_bar)
        private RatingBar ratingBar;
        private Binder binder = new Binder();

        private CommentViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_comment, null);
            binder.bindView(this, view);
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
            String content = model.getString("content");
            double rating = model.getDouble("score");
            ratingBar.setRating((float) (rating/2) );
            if (TextUtils.isEmpty(content)) {
                if (rating <= 3.0) {
                    content = "差评";
                } else if (rating > 3.0 && rating <= 7.0) {
                    content = "中评";
                } else if (rating > 7.0 || rating <= 10.0) {
                    content = "好评";
                }
            }
            contentTextView.setText(content);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
