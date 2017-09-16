package com.tianpingpai.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.adapter.DateAdapter;
import com.tianpingpai.adapter.DeliverTimeAdapter;
import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;

public class SelectDeliverTimeViewController extends BaseViewController {

    public interface OnSelectedListener{
        void onSelected();
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    private OnSelectedListener onSelectedListener;

    {
        setLayoutId(R.layout.vc_select_deliver_time);
    }

    private DateAdapter dateAdapter = new DateAdapter();
    private DeliverTimeAdapter timeAdapter = new DeliverTimeAdapter();

    {
        int hour = dateAdapter.getStartHour(0);
        if(hour > 3){
            dateAdapter.setSelection(1);
        }
        if(hour >= 20){
            timeAdapter.setStart(0);
        }else {
            timeAdapter.setStart(dateAdapter.getStartHour(dateAdapter.getSelection()));
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView dateListView = (ListView) rootView.findViewById(R.id.date_list_view);
        dateListView.setAdapter(dateAdapter);
        dateListView.setOnItemClickListener(dateItemClickListener);

        ListView timeListView = (ListView) rootView.findViewById(R.id.time_list_view);
        timeListView.setAdapter(timeAdapter);
        timeListView.setOnItemClickListener(timeItemClickListener);

        View actionBar = setActionBarLayout(R.layout.ab_select_city);
        actionBar.findViewById(R.id.ab_right_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(R.id.ab_right_button).setEnabled(true);
        actionBar.findViewById(R.id.ab_close_button).setOnClickListener(closeButtonListener);
        actionBar.findViewById(R.id.ab_right_button).setOnClickListener(okayButtonListener);
    }

    private AdapterView.OnItemClickListener dateItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dateAdapter.setSelection(position);
            timeAdapter.setStart(dateAdapter.getStartHour(position));
        }
    };

    private AdapterView.OnItemClickListener timeItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            timeAdapter.setSelection(position);
        }
    };

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet actionSheet = (ActionSheet) getViewTransitionManager();
            actionSheet.dismiss();
        }
    };

    private View.OnClickListener okayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet actionSheet = (ActionSheet) getViewTransitionManager();
            actionSheet.dismiss();
            onSelectedListener.onSelected();
        }
    };

    public String getSelectedTime(){
        return dateAdapter.getTime(timeAdapter.getHour());
    }
}
