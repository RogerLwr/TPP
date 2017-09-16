package com.tianpingpai.crm.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
@SuppressWarnings("unused")
public class SelectDateViewController extends BaseViewController {

    public interface OnSelectDateListener {
        void onSelectDate(String s,int id);
    }

    {
        setLayoutId(R.layout.vc_select_date);
    }

    private DateAdapter dateAdapter;
    private String [] dates = {"全部","本日","本周","本月"};
    private OnSelectDateListener onSelectDateListener;
    private ActionSheet actionSheet;


    public void setOnSelectDateListener(OnSelectDateListener dateListener) {
        this.onSelectDateListener = dateListener;

    }

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        ListView dateListView = (ListView)rootView.findViewById(R.id.date_list_view);
        dateAdapter = new DateAdapter();
        dateListView.setAdapter(dateAdapter);
        dateListView.setOnItemClickListener(dateListViewOnItemClickListener);
        View actionBar = setActionBarLayout(R.layout.ab_select_city);
        actionBar.findViewById(R.id.ab_close_button).setOnClickListener(closeButtonListener);

    }


    private AdapterView.OnItemClickListener dateListViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            actionSheet.dismiss();
            if(onSelectDateListener!=null){
                onSelectDateListener.onSelectDate(dateAdapter.getItem(position),position);
            }
        }
    };

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };


    class DateAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return dates.length;
        }

        @Override
        public String getItem(int position) {
            return dates[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = View.inflate(getActivity().getApplicationContext(),R.layout.item_date,null);
            ((TextView)v.findViewById(R.id.item_date_tv)).setText(dates[position]);
            return v;
        }
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        Log.e("onBackKeyDown","--------------");

        if (actionSheet != null && actionSheet.isShowing()){
            actionSheet.dismiss();

            return true;
        }else{
            Log.e("onBackKeyDown","--------------");

            return super.onBackKeyDown(a);
        }
    }
}
