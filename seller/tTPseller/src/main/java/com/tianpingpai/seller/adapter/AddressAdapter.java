package com.tianpingpai.seller.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.ui.AddressListViewController;
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
//        if (selectionMode) {
//            return new SelectionViewHolder(inflater);
//        } else {
            return new AddressViewHolder(inflater);
//        }
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public boolean selectionMode = true;

    private class AddressViewHolder implements ViewHolder<Model> {
        private View view;
        @Binding(id = R.id.function_desc_text_view)
        private TextView functionDescTextView;
        @Binding(id = R.id.name_text_view,format = "{{consignee}}")
        private TextView nameTextView;
        @Binding(id = R.id.address_edit_text,format = "{{area}},{{address}},{{detail}}")
        private TextView addressTextView;
        @Binding(id = R.id.default_image_view)
        private ImageView defaultImageView;

        private Model model;

        private Binder binder = new Binder();

        private AddressViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_address, null);
            binder.bindView(this,view);
            functionDescTextView.setText("设为默认地址");
        }

        @Override
        public void setModel(Model model) {
            this.model = model;
            binder.bindData(model);
            if (isDefault(model)) {
                defaultImageView.setImageResource(R.drawable.checkbox_checked);
            } else {
                defaultImageView.setImageResource(R.drawable.checkbox_unchecked);
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
