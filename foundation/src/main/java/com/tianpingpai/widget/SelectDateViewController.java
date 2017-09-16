package com.tianpingpai.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;

import java.util.Calendar;

public class SelectDateViewController extends BaseViewController {

    private NumberAdapter yearAdapter = new NumberAdapter();
    private NumberAdapter monthAdapter = new NumberAdapter();
    private NumberAdapter dayAdapter = new NumberAdapter();

    {
        setLayoutId(R.layout.vc_select_date);
        yearAdapter.setRange(2015, 2016);
        yearAdapter.setFormat("%d年");
        monthAdapter.setRange(1, 12);
        monthAdapter.setFormat("%d月");
        dayAdapter.setFormat("%d日");
        Calendar calendar = Calendar.getInstance();
        yearAdapter.setSelection(1);
        selectMonth(calendar.get(Calendar.MONTH));
        dayAdapter.setSelection(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void selectMonth(int month){
        monthAdapter.setSelection(month - 1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,month - 1);
        calendar.set(Calendar.YEAR,yearAdapter.getSelectedNumber());
        dayAdapter.setRange(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        dayAdapter.notifyDataSetChanged();
    }

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    private ActionSheet actionSheet;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public OnSelectListener onSelectListener;

    private boolean showDay = false;
    public void setShowDay(boolean showDay){
        this.showDay = showDay;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView yearListView = (ListView) rootView.findViewById(R.id.year_list_view);
        yearListView.setAdapter(yearAdapter);
        yearListView.setOnItemClickListener(yearAdapter);

        ListView monthListView = (ListView) rootView.findViewById(R.id.month_list_view);
        monthListView.setAdapter(monthAdapter);
        monthListView.setOnItemClickListener(monthItemClickListener);
        monthListView.setSelection(monthAdapter.getSelection());

        ListView dayListView = (ListView) rootView.findViewById(R.id.day_list_view);
        dayListView.setAdapter(dayAdapter);
        dayListView.setSelection(dayAdapter.getSelection());
        dayListView.setOnItemClickListener(dayAdapter);
        if(showDay){
            dayListView.setVisibility(View.VISIBLE);
        }

        View actionBar = setActionBarLayout(R.layout.ab_select_city);
        setTitle("选择日期");

        actionBar.findViewById(R.id.ab_close_button).setOnClickListener(closeButtonListener);

        TextView rightButton = (TextView) actionBar.findViewById(R.id.ab_right_button);
        rightButton.setText("确定");
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setClickable(true);
        rightButton.setEnabled(true);
        rightButton.setOnClickListener(okayButtonListener);

    }

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };

    private View.OnClickListener okayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
            if (onSelectListener != null) {
                onSelectListener.onSelect();
            }
        }
    };

    private AdapterView.OnItemClickListener monthItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectMonth(monthAdapter.getItem(position));
            dayAdapter.setSelection(0);
        }
    };

    public int getSelectedYear() {
        return yearAdapter.getSelectedNumber();
    }

    public int getSelectedMonth() {
        return monthAdapter.getSelectedNumber();
    }

    public int getSelectedDay(){
        return dayAdapter.getSelectedNumber();
    }
}
