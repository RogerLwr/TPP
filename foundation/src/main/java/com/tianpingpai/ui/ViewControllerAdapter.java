package com.tianpingpai.ui;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianpingpai.core.ContextProvider;

import java.util.ArrayList;
import java.util.List;

public class ViewControllerAdapter extends PagerAdapter {

    private List<? extends ViewController> viewControllers;

    private ArrayList<String> titles = new ArrayList<>();

    public void setTitles(ArrayList<String> titles){
        this.titles.clear();
        this.titles.addAll(titles);
    }

    public void setTitles(String[] titles){
        this.titles.clear();
        for(String t:titles){
            this.titles.add(t);
        }
    }

    public void setViewControllers(List<? extends ViewController> vcs) {
        this.viewControllers = vcs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return viewControllers == null ? 0 : viewControllers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Log.e("xx", "init");
        ViewController vc = viewControllers.get(position);
        if (vc.getView() == null) {
            Log.e("xx", "null onCreate");
            vc.createView(LayoutInflater.from(ContextProvider.getContext()), container);
        }
        View view = vc.getView();
        vc.resume();
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e("xx", "destroyItem");
        View view = (View) object;
        container.removeView(view);
    }

    public void destroyView(){
        for(ViewController vc:viewControllers){
            vc.destroyView();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position < titles.size()){
            return titles.get(position);
        }
        return super.getPageTitle(position);
    }
}
