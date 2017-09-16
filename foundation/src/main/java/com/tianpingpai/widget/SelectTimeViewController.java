package com.tianpingpai.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.adapter.DateAdapter;
import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CyclicAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectTimeViewController extends BaseViewController {
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
    private TimeAdapter timeAdapter = new TimeAdapter();

    private ListView timeListView;

    private boolean showDateOnly;

    public void setShowDateOnly(boolean show){
        this.showDateOnly = show;
    }

    public void setStartDate(int startDate) {
        dateAdapter.setStartDate(startDate);
    }


    public void setDuration(int duration) {
        dateAdapter.setDuration(duration);
    }


    {
        int hour = dateAdapter.getStartHour(0);
        dateAdapter.setStartHour(0);
        if(hour >= 20){
            timeAdapter.setStart(0);
        }else {
            timeAdapter.setStart(dateAdapter.getStartHour(0));
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView dateListView = (ListView) rootView.findViewById(R.id.date_list_view);
        dateListView.setAdapter(dateAdapter);
        dateListView.setOnItemClickListener(dateItemClickListener);

        timeListView = (ListView) rootView.findViewById(R.id.time_list_view);
        timeListView.setAdapter(timeAdapter);
        timeListView.setOnItemClickListener(timeItemClickListener);
        CyclicAdapter.setupWithListView(timeListView);

        View actionBar = setActionBarLayout(R.layout.ab_select_city);
        actionBar.findViewById(R.id.ab_right_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(R.id.ab_right_button).setEnabled(true);
        actionBar.findViewById(R.id.ab_close_button).setOnClickListener(closeButtonListener);
        actionBar.findViewById(R.id.ab_right_button).setOnClickListener(okayButtonListener);

        if(showDateOnly){
            timeListView.setVisibility(View.GONE);
        }
    }

    private AdapterView.OnItemClickListener dateItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dateAdapter.setSelection(position);
            timeAdapter.setStart(dateAdapter.getStartHour(position));
            CyclicAdapter.scrollToStart(timeListView);
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
        String hour = String.format("%02d:00:00",timeAdapter.getHour()) ;
        return dateAdapter.getTime(hour);
    }
    public String getSelectedDate(){
        return dateAdapter.getDate();
    }
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public String getSelectedDateAddDays(int day){
            Date date;
        try {
            date = dateFormat.parse(dateAdapter.getDate());
            Date newDate = new Date( date.getTime() + (1000 * 60 * 60 * 24 * day ) );
            return dateFormat.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateFormat.format(new Date());
    }
    public int getSelectPosition(){  // 根据开始时间选中的第几个 来控制 结束时间的起始值
        return dateAdapter.getSelection();
    }
}
