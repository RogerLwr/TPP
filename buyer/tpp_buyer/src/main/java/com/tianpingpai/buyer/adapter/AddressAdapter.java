package com.tianpingpai.buyer.adapter;

import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.ui.AddressListViewController;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
public class AddressAdapter extends ModelAdapter<Model> implements AdapterView.OnItemClickListener {

    public interface OnSetDefaultAddressListener {
        void onSetDefaultAddress(Model model);
    }

    public void setOnSetDefaultAddressListener(OnSetDefaultAddressListener onSetDefaultAddressListener) {
        this.onSetDefaultAddressListener = onSetDefaultAddressListener;
    }

    private OnSetDefaultAddressListener onSetDefaultAddressListener;

    public void setFragment(AddressListViewController fragment) {
        this.fragment = fragment;
    }

    private AddressListViewController fragment;

    public Model getSelectedAddress() {
        return selectedAddress;
    }

    private Model selectedAddress;

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        if (selectionMode) {
            return new SelectionViewHolder(inflater);
        } else {
            return new AddressViewHolder(inflater);
        }
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public boolean selectionMode = true;

    private class SelectionViewHolder implements ViewHolder<Model> {
        private View view;
        @Binding(id = R.id.name_text_view,format = "{{consignee}}")
        private TextView nameTextView;
        @Binding(id = R.id.phone_text_view,format = "{{phone}}")
        private TextView phoneTextView;
        @Binding(id = R.id.address_edit_text,format = "{{area}},{{address}},{{detail}}")
        private TextView addressTextView;
        @Binding(id = R.id.selection_indicator)
        private ImageView selectionIndicator;

        private Binder binder = new Binder();

        SelectionViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_address_selection_mode, null);
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
//            String name = model.getString("consignee");
//            String phone = model.getString("phone");
//            SpannableStringBuilder ssb = new SpannableStringBuilder(name + "    " + phone);
//
//            nameTextView.setText(ssb);
//
            SpannableStringBuilder ssbAddr;
            if (isDefault(model)) {
                ssbAddr = new SpannableStringBuilder("[默认地址]" + " ");
            }else {
                ssbAddr = new SpannableStringBuilder("");
            }
            int length = ssbAddr.length();
            ssbAddr.setSpan(new ForegroundColorSpan(ContextProvider.getContext().getResources().getColor(R.color.red_ff6)), 0, length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            ssbAddr.append(model.getString("area")+ ","+ model.getString("address") + "," + model.getString("detail"));
            ssbAddr.setSpan(new ForegroundColorSpan(Color.BLACK), length, ssbAddr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            addressTextView.setText(ssbAddr);

            if (selectedAddress == model) {
                selectionIndicator.setImageResource(R.drawable.checkbox_checked);
            } else {
                selectionIndicator.setImageResource(R.drawable.checkbox_off);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }

    private class AddressViewHolder implements ViewHolder<Model> {
        private View view;
        @Binding(id = R.id.function_desc_text_view)
        private TextView functionDescTextView;
        @Binding(id = R.id.name_text_view,format = "{{consignee}}")
        private TextView nameTextView;
        @Binding(id = R.id.phone_text_view,format = "{{phone}}")
        private TextView phoneTextView;
        @Binding(id = R.id.address_edit_text,format = "{{area}},{{address}},{{detail}}")
        private TextView addressTextView;
        @Binding(id = R.id.default_image_view)
        private ImageView defaultImageView;

        private Model model;

        private Binder binder = new Binder();

        private AddressViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_address, null);
            binder.bindView(this, view);
            functionDescTextView.setText("默认地址");
        }

        @Override
        public void setModel(Model model) {
            this.model = model;
            binder.bindData(model);
            if (isDefault(model)) {
                defaultImageView.setImageResource(R.drawable.ic_checkbox_on_red);
                functionDescTextView.setTextColor(functionDescTextView.getResources().getColor(R.color.red_ff6));
            } else {
                defaultImageView.setImageResource(R.drawable.checkbox_off);
                functionDescTextView.setTextColor(functionDescTextView.getResources().getColor(R.color.gray_66));
            }
        }

        @Override
        public View getView() {
            return view;
        }

        @OnClick(R.id.default_image_view)
        private final View.OnClickListener defaultButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDefault(model)) {
                    return;
                }
                if (onSetDefaultAddressListener != null) {
                    onSetDefaultAddressListener.onSetDefaultAddress(model);
                }
            }
        };

        @OnClick(R.id.edit_button)
        private View.OnClickListener editButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.editAddress(model);//TODO
            }
        };

        @OnClick(R.id.delete_button)
        private View.OnClickListener deleteButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.deleteAddress(model);
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedAddress = getItem(i);
        notifyDataSetChanged();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment.selectModel(selectedAddress);
            }
        }, 500);

    }

    private Handler mHandler = new Handler();

    private boolean isDefault(Model m) {
        return getModels().indexOf(m) == 0;//TODO
    }
}
