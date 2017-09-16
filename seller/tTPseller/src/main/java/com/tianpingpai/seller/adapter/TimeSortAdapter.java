package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.model.TimeSortModel;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class TimeSortAdapter extends ModelAdapter<TimeSortModel> {
    @Override
    protected ModelAdapter.ViewHolder<TimeSortModel> onCreateViewHolder(LayoutInflater inflater) {
        return new TimeSortViewHolder(inflater);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @SuppressWarnings("unused")
    private class TimeSortViewHolder implements ModelAdapter.ViewHolder<TimeSortModel> {
        private View view;

        @Binding(id = R.id.time_sort_text_view)
        private TextView timeSortTextView;

        private Binder binder = new Binder();

        private TimeSortViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_time_sort, null);
            binder.bindView(this, view);
        }

        @Override
        public void setModel(TimeSortModel timeSortModel) {

            timeSortTextView.setText(timeSortModel.getTimeSortName());

        }

        @Override
        public View getView() {
            return view;
        }
    }
}
