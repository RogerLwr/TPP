package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.SignInViewController;
import com.tianpingpai.location.LocationModel;
import com.tianpingpai.model.CommentModel;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.SaleNumberModel;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
@SuppressWarnings("unused")
public class CommentAdapter extends BaseAdapter{

    private Fragment fragment;
    private Activity activity;

    private CustomerModel customerModel;

    public boolean hide;

    public void setActivity(Activity a){
        this.activity=a;
    }

    public void setSaleNumberModel(SaleNumberModel saleNumberModel) {
        this.saleNumberModel = saleNumberModel;
        notifyDataSetChanged();
    }

    private SaleNumberModel saleNumberModel;
    private ListResult<CommentModel> comments;

    public void setCustomerModel(CustomerModel cm){
        if(cm == null){
            return;
        }
        this.customerModel = cm;
        notifyDataSetChanged();
    }

    public void setComments(ListResult<CommentModel> listResult){
        this.comments = listResult;//TODO
        notifyDataSetChanged();
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        int gap = hide ? 0 : 1;
        return comments == null ? gap : comments.getModels().size() + gap;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            if(hide){
                return 1;
            }
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(ContextProvider.getContext());
        if(getItemViewType(i) == 0){
            CustomerInfoViewHolder holder;
            if(view == null){
                holder = new CustomerInfoViewHolder(inflater);
                holder.getView().setTag(holder);
            }else{
                holder = (CustomerInfoViewHolder) view.getTag();
            }
            view = holder.getView();
            holder.setModel(customerModel);
        }else{
            CommentViewHolder holder;
            if(view == null){
                holder = new CommentViewHolder(inflater);
                holder.getView().setTag(holder);
            }else{
                holder = (CommentViewHolder) view.getTag();
            }
            view = holder.getView();
            if(hide){
                holder.setModel(comments.getModels().get(i));
            }else{
                holder.setModel(comments.getModels().get(i - 1));
            }

        }
        return view;
    }

    class CustomerInfoViewHolder implements ModelAdapter.ViewHolder<CustomerModel> {

        private View view;

		@Binding(id = R.id.sale_name_text_view,format = "{{sale_name}}")
		private TextView saleNameTextView;
		@Binding(id = R.id.contact_text_view,format = "{{phone}}")
		private TextView contactTextView;
        @Binding(id = R.id.store_address_text_view,format = "店铺地址:{{sale_address}}")
        private TextView storeAddressTextView;
        @Binding(id = R.id.store_name_text_view,format = "店铺名称:{{sale_name}}")
        private TextView storeNameTextView;
        @Binding(id = R.id.user_type_text_view)
        private TextView userTypeTextView;

        private TextView todayOrdersTextView;
        private TextView thisWeekOrdersTextView;
        private TextView moreTextView;
        private TextView allTextView;

        private TextView todayFeeTextView;
        private TextView weekFeeTextView;
        private TextView monthFeeTextView;
        private TextView allFeeTextView;

        private Binder binder = new Binder();

        public CustomerInfoViewHolder(LayoutInflater inflater) {
            view = activity.getLayoutInflater().inflate(R.layout.item_customer_detail,null);
            binder.bindView(this,view);

            todayOrdersTextView = (TextView) view.findViewById(R.id.today_orders_text_view);
            thisWeekOrdersTextView = (TextView) view.findViewById(R.id.this_week_text_view);
            moreTextView = (TextView) view.findViewById(R.id.more_text_view);
            allTextView = (TextView) view.findViewById(R.id.all_text_view);

            todayFeeTextView = (TextView) view.findViewById(R.id.today_fee_text_view);
            weekFeeTextView = (TextView) view.findViewById(R.id.week_fee_text_view);
            monthFeeTextView = (TextView) view.findViewById(R.id.month_fee_text_view);
            allFeeTextView = (TextView) view.findViewById(R.id.more_fee_text_view);

        }

        @Override
		public void setModel(CustomerModel model) {
            if(model == null){
                return;
            }
            binder.bindData(model);
            String ss = model.getUserType() == CustomerModel.USER_TYPE_BUYER ? "买家" : "卖家";
            userTypeTextView.setText("商户类型:" + ss);

            if(saleNumberModel != null){
                Log.e( "ee",saleNumberModel.toString());
                todayOrdersTextView.setText(saleNumberModel.getToday());
                thisWeekOrdersTextView.setText(saleNumberModel.getWeek());
                moreTextView.setText(saleNumberModel.getMore());
                allTextView.setText(saleNumberModel.getAll());

                todayFeeTextView.setText(saleNumberModel.getFeeToday());
                weekFeeTextView.setText(saleNumberModel.getFeeWeek());
                monthFeeTextView.setText(saleNumberModel.getFeeMonth());
                allFeeTextView.setText(saleNumberModel.getFeeAll());
            }
		}

		@Override
		public View getView() {
            return view;
		}
	}

    class CommentViewHolder implements ModelAdapter.ViewHolder<CommentModel> {
		private View view;
		private TextView nameTextView;
		private TextView dateTextView;
        @Binding(id = R.id.content_text_view,format = "拜访内容:{{content}}")
		private TextView contentTextView;
        @Binding(id = R.id.address_text_view,format = "拜访地址:{{position}}")
		private TextView addressTextView;
        private CommentModel comment;

        Binder binder = new Binder();

        @SuppressLint("InflateParams")
		CommentViewHolder(LayoutInflater inflater){
			view = activity.getLayoutInflater().inflate(R.layout.item_comment1509, null);
            binder.bindView(this,view);
			nameTextView = (TextView) view.findViewById(R.id.name_text_view);
			dateTextView = (TextView) view.findViewById(R.id.date_text_view);
			addressTextView.setOnClickListener(addressButtonListener);
		}

		@Override
		public void setModel(CommentModel model) {
            comment = model;
            binder.bindData(model);
			nameTextView.setText(model.getMarketerName());
			dateTextView.setText(model.getCreatedTime());
		}

		@Override
		public View getView() {
			return view;
		}

		private View.OnClickListener addressButtonListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                double lat = comment.getLatitude();
                double longitude = comment.getLongitude();

                Log.e("xx", "onClick:" + lat + "," + longitude);
                if(lat > 1 && longitude > 1){
                    LocationModel locationModel = new LocationModel();
                    locationModel.setLatitude(lat);
                    locationModel.setLongitude(longitude);
                    locationModel.setAddress(comment.getAddress());

                    Intent intent = new Intent(fragment.getActivity(), ContainerActivity.class);
                    intent.putExtra(ContainerActivity.KEY_CONTENT, SignInViewController.class);
                    intent.putExtra(SignInViewController.KEY_LOCATION, locationModel);
                    fragment.startActivity(intent);
                }
			}
		};

	}
}
