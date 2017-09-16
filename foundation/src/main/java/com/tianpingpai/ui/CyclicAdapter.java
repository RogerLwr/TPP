package com.tianpingpai.ui;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

public class CyclicAdapter extends BaseAdapter {

    private Adapter wrappedAdapter;
    private AdapterView.OnItemClickListener oldItemClickListener;

    public static CyclicAdapter setupWithListView(AdapterView listView){
        CyclicAdapter adapter = new CyclicAdapter();
        adapter.setWrappedAdapter( listView.getAdapter());
        listView.setAdapter(adapter);
        scrollToStart(listView);
        adapter.oldItemClickListener = listView.getOnItemClickListener();
        listView.setOnItemClickListener(adapter.onItemClickListener);
        return adapter;
    }

    public static void scrollToStart(AdapterView<?> listView){
        int half = Integer.MAX_VALUE / 2;
        CyclicAdapter adapter = (CyclicAdapter) listView.getAdapter();
        int count = adapter.wrappedAdapter.getCount();
        for(int i = 0;i < count ;i++){
            int index = half + i;
//            Log.e("xx","count:" + count);
//            Log.e("xx","index:" + index + "==" + (index % count));
            if(index % count == 0){
                half = index;
                break;
            }
        }
        listView.setSelection(half);
    }

    public void setWrappedAdapter(Adapter wrappedAdapter){
        this.wrappedAdapter = wrappedAdapter;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        wrappedAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        wrappedAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public int mapPosition(int position){
        return position % wrappedAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return wrappedAdapter.getItem(mapPosition(position));
    }

    @Override
    public long getItemId(int position) {
        return wrappedAdapter.getItemId(mapPosition(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return wrappedAdapter.getView(mapPosition(position),convertView,parent);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(oldItemClickListener != null){
                oldItemClickListener.onItemClick(parent,view,mapPosition(position),id);
            }
        }
    };
}
