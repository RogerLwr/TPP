package com.tianpingpai.crm.ui;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.MarketAdapter;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;

import java.util.ArrayList;

@Layout(id = R.layout.vc_market_list)
public class MarketListViewController extends BaseViewController {


    public interface MarketSelectionListener{
        void onMarketSelected(MarketModel marketModel);
    }

    MarketAdapter adapter = new MarketAdapter();

    public interface OnBackListener{
        /**
         * 返回上个界面 并把上个页面的选中 的背景颜色去掉
         */
        void onBackClick();
    }
    OnBackListener onBackListener;
    public void setOnBackListener(OnBackListener onBackListener){
        this.onBackListener = onBackListener;
    }

    public void setListener(MarketSelectionListener listener) {
        this.listener = listener;
    }

    private MarketSelectionListener listener;

    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackListener.onBackClick();
            getViewTransitionManager().popViewController(MarketListViewController.this);
        }
    };

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        {
//            setActionBarLayout(R.layout.ab_title_white);
        }
        getView().findViewById(R.id.edge_container).setOnClickListener(backButtonListener);
        getView().findViewById(R.id.back_button).setOnClickListener(backButtonListener);
        View doneButton = getView().findViewById(R.id.done_button);
        hideActionBar();
        doneButton.setVisibility(View.GONE);
        ArrayList<MarketModel> markets = new ArrayList<>(MarketManager.getInstance().getMarkets());
//        Log.e("qweqwqwq",markets.get(2).toString());
        MarketModel marketModel = new MarketModel();
        marketModel.setId(-1);
        marketModel.setName("全部");
        markets.add(0, marketModel);
        adapter.clear();
        adapter.setModels(markets);
        ListView marketListView = (ListView) getView().findViewById(R.id.market_list_view);
        marketListView.setAdapter(adapter);
        marketListView.setOnItemClickListener(onItemClickListener);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(listener != null){
                listener.onMarketSelected(adapter.getItem(i));
            }
            onBackListener.onBackClick();
            getViewTransitionManager().popViewController(MarketListViewController.this);
        }
    };
}
