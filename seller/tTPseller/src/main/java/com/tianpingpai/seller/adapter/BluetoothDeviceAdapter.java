package com.tianpingpai.seller.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ModelAdapter;

public class BluetoothDeviceAdapter extends ModelAdapter<BluetoothDevice>{
    @Override
    protected ViewHolder<BluetoothDevice> onCreateViewHolder(LayoutInflater inflater) {
        return new DeviceViewHolder(inflater);
    }

    class DeviceViewHolder implements ViewHolder<BluetoothDevice>{

        private View view;
        private TextView nameTextView;

        public DeviceViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_blue_tooth_device,null);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        }

        @Override
        public void setModel(BluetoothDevice model) {
            String status ;
            if(model.getBondState() == BluetoothDevice.BOND_BONDED){
                status = "已绑定";
            }else if(model.getBondState() == BluetoothDevice.BOND_BONDING){
                status =  "正在绑定";
            }else{
                status = "未绑定";
            }
            String text = model.getName() + "(" + status + ")";
            nameTextView.setText(text);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
