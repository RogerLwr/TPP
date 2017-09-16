package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ModelAdapter;

@SuppressWarnings("unused")
public class ProductGradeAdapter extends ModelAdapter<Model> {



    public void setUnreadEnvelopeCount(int unreadEnvelopeCount) {
        this.unreadEnvelopeCount = unreadEnvelopeCount;
        notifyDataSetChanged();
    }

    private int unreadEnvelopeCount = 0;

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new ProductGradeViewHolder(inflater);
    }

    boolean isShowMyScore = false;

    public void setIsShowMyScore(boolean isShowMyScore) {
        this.isShowMyScore = isShowMyScore;
        notifyDataSetChanged();
    }

    private class ProductGradeViewHolder implements ViewHolder<Model> {

        private View view;


        ProductGradeViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_product_grade, null);
        }

        @Override
        public void setModel(Model model) {

        }

        @Override
        public View getView() {
            return view;
        }
    }
}
