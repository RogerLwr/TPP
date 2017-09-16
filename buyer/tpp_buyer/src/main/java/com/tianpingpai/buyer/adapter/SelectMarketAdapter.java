package com.tianpingpai.buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.model.AddressModel;
import com.tianpingpai.buyer.ui.NotValidatedViewController;
import com.tianpingpai.buyer.ui.SelectMarketViewController;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

public class SelectMarketAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_MARKET = 1;

    private AddressModel address;
    private boolean isLoading = false;
    private boolean showCurrentLocation = true;
    private double lat;
    private double lng;
    private View.OnClickListener selectAddressButtonListener;
    private View.OnClickListener locateButtonListener;

    private TextView currentCityTextView;
    private Model city;
    private String currentAddress;

    SelectMarketViewController selectMarketViewController;

    public void setSelectMarketViewController(SelectMarketViewController vc){
        this.selectMarketViewController = vc;
    }

    public void setCity(Model city){
        this.city = city;
        currentCityTextView.setText(ContextProvider.getContext().getString(R.string.current_selected_city, city.getString("name")));
    }


    public void setShowCurrentLocation(boolean currentLocation){
        this.showCurrentLocation = currentLocation;
        notifyDataSetChanged();
    }

    public void setLoading(boolean loading){
        this.isLoading = loading;
    }

    public void setPageControl(ModelAdapter.PageControl<Model> pageControl) {
        this.pageControl = pageControl;
    }

    private ModelAdapter.PageControl<Model> pageControl;

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
        notifyDataSetChanged();
    }

    public void setAddress(AddressModel address) {
        this.address = address;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        int count = 1;
        if(listResult != null){
            if(listResult.hasMorePage()){
                count += markets.size() + 1;
            }else{
                count += markets.size();
            }
        }
        return count;

    }

    @Override
    public Model getItem(int i) {
        return markets.get(i - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return ITEM_TYPE_HEADER;
        }
        if(position <= markets.size()){
            return ITEM_TYPE_MARKET;
        }
        return 2;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        switch (getItemViewType(i)){
            case ITEM_TYPE_HEADER:
                if(view == null){
                    view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_header_select_market,null);
                }
                configureHeaderView(view);
                break;
            case ITEM_TYPE_MARKET:
                MarketViewHolder viewHolder;
                if(view == null){
                    viewHolder = new MarketViewHolder();
                    view = viewHolder.getView();
                    view.setTag(viewHolder);
                }else{
                    viewHolder = (MarketViewHolder) view.getTag();
                }
                viewHolder.setModel(markets.get(i - 1));
                view = viewHolder.getView();
                break;
            case 2:
                if(view == null) {
                    view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_more_market, null);
                    view.setOnClickListener(loadMoreItemListener);
                }
                TextView tv = (TextView) view.findViewById(R.id.load_more_text_view);
                if(isLoading){
                    tv.setText(R.string.loading);
                }else{
                    tv.setText(R.string.click_to_load_more);
                }
                break;
        }
        return view;
    }

    private View.OnClickListener loadMoreItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isLoading){
                return;
            }
            if(pageControl != null){
                pageControl.onLoadPage(listResult.getNextPage());
            }
        }
    };

    private View.OnClickListener helpButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet actionSheet = new ActionSheet();
            NotValidatedViewController vc = new NotValidatedViewController();
            vc.setActivity(selectMarketViewController.getActivity());
            vc.setFinishActivityOnExit(false);
            actionSheet.setViewController(vc);
            vc.setActionSheet(actionSheet);
            vc.setMessage("您可以拨打客服电话\n" +
                    "让我们帮您选择商圈\n" +
                    "拨打：4006-406-010");
            vc.setCancelButtonText("取消");
            actionSheet.setActivity(selectMarketViewController.getActivity());
            actionSheet.show();
        }
    };

    private void configureHeaderView(View view) {
        view.findViewById(R.id.locate_button).setOnClickListener(locateButtonListener);
        view.findViewById(R.id.address_container).setOnClickListener(selectAddressButtonListener);
        TextView addressTextView = (TextView) view.findViewById(R.id.address_edit_text);

        View locateButton = view.findViewById(R.id.locate_button);
        View cityContainer = view.findViewById(R.id.city_container);
        TextView currentAddressTextView = (TextView) view.findViewById(R.id.current_address_text_view);
        this.currentCityTextView = (TextView) view.findViewById(R.id.current_city_text_view);
        view.findViewById(R.id.help_button).setOnClickListener(helpButtonListener);
        view.findViewById(R.id.select_city_button).setOnClickListener(selectCityButtonListener);
        if(showCurrentLocation){
            view.findViewById(R.id.address_container).setVisibility(View.GONE);
            locateButton.setVisibility(View.VISIBLE);
            cityContainer.setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.address_container).setVisibility(View.VISIBLE);
            locateButton.setVisibility(View.GONE);
            cityContainer.setVisibility(View.GONE);
            String address = this.address == null ? "" : this.address.getAddress();
            addressTextView.setText(ContextProvider.getContext().getString(R.string.receive_address,address));
        }

        String cityName = city == null ? "" :city.getString("name");
        currentCityTextView.setText(ContextProvider.getContext().getString(R.string.current_selected_city, cityName));

        if(currentAddress != null){
            currentAddressTextView.setText(currentAddress);
            currentAddressTextView.setVisibility(View.VISIBLE);
        }else{
            currentAddressTextView.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener selectCityButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectMarketViewController.showSelectCityActionSheet();
        }
    };

    public void setSelectAddressButtonListener(View.OnClickListener selectAddressButtonListener) {
        this.selectAddressButtonListener = selectAddressButtonListener;
    }

    public void setLocateButtonListener(View.OnClickListener locateButtonListener) {
        this.locateButtonListener = locateButtonListener;
    }

    private ListResult<Model> listResult;
    ArrayList<Model> markets = new ArrayList<>();
    public void addMarkets(ListResult<Model> result) {
        listResult = result;
        markets.addAll(result.getModels());
        isLoading = false;
        notifyDataSetChanged();
    }

    public void clear(){
        markets.clear();
        listResult = null;
        notifyDataSetChanged();
    }

    public void setCurrentAddress(String address) {
        this.currentAddress = address;
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    private class MarketViewHolder implements ModelAdapter.ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.name_text_view,format = "{{name}}")
        private TextView nameTextView;
        @Binding(id = R.id.distance_text_view,format = "距您{{_distance| money}}公里")
        private TextView distanceTextView;

        private Binder binder = new Binder();

        {
            view = View.inflate(ContextProvider.getContext(),R.layout.item_market_detail,null);
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model model) {
            Number distance = model.getNumber("distance");
//            Toast.makeText(ContextProvider.getContext(),"distance:" + distance,Toast.LENGTH_LONG).show();
            if(distance != null && distance.intValue() != 0){
                distance = distance.doubleValue() / 1000;
                model.set("_distance",distance);
                distanceTextView.setVisibility(View.VISIBLE);
            }else{
                distanceTextView.setVisibility(View.GONE);
                model.set("_distance", 0);
            }
            binder.bindData(model);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
