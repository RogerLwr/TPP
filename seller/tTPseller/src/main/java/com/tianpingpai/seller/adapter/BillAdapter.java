package com.tianpingpai.seller.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.utils.pinnedheaderlistview.PinnedHeaderListView;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by LiuWenRong on 16/4/21.
 */
public class BillAdapter extends ModelAdapter<Model> implements AbsListView.OnScrollListener, PinnedHeaderListView.PinnedHeaderAdapter{

    private TextView headerTextView;

    private class BillViewHolder implements ViewHolder<Model>{

            private View view;

            @Binding(id = R.id.header_title_text_view)
            private TextView headerTitleTextView;
            @Binding(id = R.id.title_text_view, format = "{{title}}")
            private TextView titleTextView;
            @Binding(id = R.id.status_text_view, format = "{{status}}")
            private TextView statusTextView;

            @Binding(id = R.id.desc_text_view, format = "{{description}}")
            private TextView descTextView;
            @Binding(id = R.id.time_text_view, format = "{{time}}")
            private TextView timeTextView;

            @Binding(id = R.id.money_text_view, format = "{{mny}}")
            private TextView moneyTextView;



            private Binder binder = new Binder();
            private BillViewHolder(View v){
                this.view = v;
                binder.bindView(this, view);
            }

            @Override
            public void setModel(Model model) {

                if(model.getInt("type") == 1){ //收入
                    moneyTextView.setTextColor(moneyTextView.getResources().getColor(R.color.green_0c));
                    model.set("mny", "+"+model.getDouble("mny"));
                }else {
                    moneyTextView.setTextColor(moneyTextView.getResources().getColor(R.color.red_ff6));
                    model.set("mny", "-"+model.getDouble("mny"));
                }

                binder.bindData(model);

                if (needTitle(getCurrentPos())) {
                    Log.e("xx", "74--------pos="+getCurrentPos());
                    // 显示标题并设置内容
                    headerTitleTextView.setText(model.getString("yearMonth"));
                    headerTitleTextView.setVisibility(View.VISIBLE);
                } else {
                    // 内容项隐藏标题
                    headerTitleTextView.setVisibility(View.GONE);
                }

            }
            @Override
            public View getView() {
                return this.view;
            }
        }

    @SuppressLint("InflateParams")
    @Override
    protected ViewHolder<Model> onCreateViewHolder(
            LayoutInflater inflater) {
        return new BillViewHolder(inflater.inflate(R.layout.item_bill, null));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        Log.e("xx", "98--------firstVisibleItem=" + firstVisibleItem);
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).controlPinnedHeader(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.e("xx", "98--------scrollState=" + scrollState);
    }

    @Override
    public int getPinnedHeaderState(int position) {
        if (getCount() == 0 || position < 0) {
            return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_GONE;
        }

        if (isMove(position) == true) {
            return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP;
        }

        return PinnedHeaderListView.PinnedHeaderAdapter.PINNED_HEADER_VISIBLE;
    }

    @Override
    public void configurePinnedHeader(View headerView, int position, int alpaha) {
        // 设置标题的内容
        Model itemEntity = getItem(position);
        String headerValue = itemEntity.getString("yearMonth");

        Log.e("xx", "120----Pos="+"position"+ ", header = " + headerValue);

        if (!TextUtils.isEmpty(headerValue)) {
            headerTextView = (TextView) headerView
                    .findViewById(R.id.header);
            headerTextView.setText(headerValue);
        }

    }

    /**
     * 判断是否需要显示标题
     *
     * @param position
     * @return
     */
    private boolean needTitle(int position) {
        // 第一个肯定是分类
        if (position == 0) {
            return true;
        }

        // 异常处理
        if (position < 0) {
            return false;
        }

        // 当前 // 上一个
        Model currentEntity =  getItem(position);
        Model previousEntity =  getItem(position - 1);
        if (null == currentEntity || null == previousEntity) {
            return false;
        }

        String currentTitle = currentEntity.getString("yearMonth");
        String previousTitle = previousEntity.getString("yearMonth");
        if (null == previousTitle || null == currentTitle) {
            return false;
        }

        // 当前item分类名和上一个item分类名不同，则表示两item属于不同分类
        if (currentTitle.equals(previousTitle)) {
            return false;
        }

        return true;
    }

    private boolean isMove(int position) {
        // 获取当前与下一项
        Model currentEntity = getItem(position);
        Model nextEntity = getItem(position + 1);
        if (null == currentEntity || null == nextEntity) {
            return false;
        }

        // 获取两项header内容
        String currentTitle = currentEntity.getString("yearMonth");
        Log.e("xx", "184------curTitle="+currentTitle);
        String nextTitle = nextEntity.getString("yearMonth");
        if (null == currentTitle || null == nextTitle) {
            return false;
        }

        // 当前不等于下一项header，当前项需要移动了
        if (!currentTitle.equals(nextTitle)) {
            return true;
        }

        return false;
    }

}
