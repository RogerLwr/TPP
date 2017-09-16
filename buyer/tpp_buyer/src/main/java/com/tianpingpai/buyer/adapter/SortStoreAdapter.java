package com.tianpingpai.buyer.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.model.SortInfo;
import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

public class SortStoreAdapter extends ModelAdapter<SortInfo>{

    private ArrayList<SortInfo> sortOptions = new ArrayList<>();

    {
        SortInfo sortDefault = new SortInfo();
        sortDefault.setName("默认排序");
        sortDefault.setSort(CommonUtil.SORT_DEFAULT);
        sortDefault.setIs_selected(true);
        sortOptions.add(sortDefault);

        SortInfo bestRatings = new SortInfo();
        bestRatings.setName("评价最好");
        bestRatings.setSort(CommonUtil.SORT_SPEED);
        sortOptions.add(bestRatings);

        SortInfo bestService = new SortInfo();
        bestService.setName("服务最佳");
        bestService.setSort(CommonUtil.SORT_SERVIECE);
        sortOptions.add(bestService);

        SortInfo bestQuality = new SortInfo();
        bestQuality.setName("质量最优");
        bestQuality.setSort(CommonUtil.SORT_QUALITY);
        sortOptions.add(bestQuality);

        SortInfo Recommended = new SortInfo();
        Recommended.setName("推荐排序");
        Recommended.setSort(CommonUtil.SORT_RECOMMEND);
        sortOptions.add(Recommended);
        setModels(sortOptions);
    }

    public ArrayList<SortInfo> categoryList;

    public boolean shouldLoadCategories(){
        return categoryList == null;
    }

    public void setCategoryList(ArrayList<SortInfo> cats){
        SortInfo all = new SortInfo();
        all.setCategory_id("-1");
        all.setName("全部");
        all.setIs_selected(false);
        cats.add(0, all);
        this.categoryList = cats;
    }

    public void showCategoryList(){
        clear();
        setModels(categoryList);
    }

    public ArrayList<SortInfo> getCategoryList(){
        return categoryList;
    }

    public void setSelectedCategory(int position){
        for (SortInfo cat:categoryList){
            cat.setIs_selected(false);
        }
        categoryList.get(position).setIs_selected(true);
    }

    public void setSelectedSort(int position){
        for (SortInfo cat:sortOptions){
            cat.setIs_selected(false);
        }
        sortOptions.get(position).setIs_selected(true);
    }

    public void setSelection(int position){
        if(isShowingCategory()){
            setSelectedCategory(position);
        }else{
            setSelectedSort(position);
        }
    }

    public boolean isShowingCategory(){
        return getModels() == categoryList;
    }

    public SortInfo getSelectedCategory(){
        if(categoryList !=  null){
            for(SortInfo cat:categoryList){
                if(cat.isIs_selected()){
                    return cat;
                }
            }
        }
        return null;
    }

    public SortInfo getSelectedSort(){
        if(sortOptions !=  null){
            for(SortInfo cat:sortOptions){
                if(cat.isIs_selected()){
                    return cat;
                }
            }
        }
        return null;
    }

    public void showSortList(){
        clear();
        setModels(sortOptions);
        Log.e("xx", "modes:" + getModels());
    }

    public String getCategoryName(String id){
        for (SortInfo si : categoryList){
            if(si.getCategory_id().equals(id)){
                si.setIs_selected(true);
                return si.getName();
            }
        }
        return "商品分类";
    }

    @Override
    protected ViewHolder<SortInfo> onCreateViewHolder(LayoutInflater inflater) {
        return new SortViewHolder(inflater);
    }

    private class SortViewHolder implements ViewHolder<SortInfo>{

        private TextView  view;
        SortViewHolder(LayoutInflater inflater){
            view  = (TextView) inflater.inflate(R.layout.item_text,null);
            view.setTextColor(Color.BLACK);
        }

        @Override
        public void setModel(SortInfo model) {
            view.setText(model.getName());
            if(model.isIs_selected()){
                view.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.green));
            }else{
                view.setTextColor(Color.BLACK);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
