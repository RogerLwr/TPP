package com.tianpingpai.seller.ui;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.adapter.BluetoothDeviceAdapter;
import com.tianpingpai.seller.manager.BluetoothController;
import com.tianpingpai.seller.manager.BluetoothEvent;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

import java.util.ArrayList;

@SuppressWarnings("unused")
@ActionBar(title = "设备管理")
@Layout(id = R.layout.ui_manage_blue_tooth_devices)
public class ManageBlueToothDevicesViewController extends BaseViewController{

    @Binding(id = R.id.scan_button)
    private TextView scanButton;

    private BluetoothDeviceAdapter deviceAdapter = new BluetoothDeviceAdapter();

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();

        ListView deviceListView = (ListView) rootView.findViewById(R.id.device_list_view);
        deviceListView.setAdapter(deviceAdapter);
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        devices.addAll(BluetoothController.getInstance().getBoundedDevices());
        deviceAdapter.setModels(devices);

        deviceListView.setOnItemClickListener(itemClickListener);
        BluetoothController.getInstance().registerListener(deviceListener);
        if(!BluetoothController.getInstance().isOpen()){
            scanButton.setText("打开蓝牙");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BluetoothController.getInstance().unregisterListener(deviceListener);
    }

    @OnClick(R.id.scan_button)
    private View.OnClickListener scanButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!BluetoothController.getInstance().isOpen()){
                BluetoothController.getInstance().open();
                return;
            }
            BluetoothController.getInstance().startDiscovery();
        }
    };

    private ModelStatusListener<BluetoothEvent, Model> deviceListener = new ModelStatusListener<BluetoothEvent, Model>() {
        @Override
        public void onModelEvent(BluetoothEvent event, Model model) {
            switch (event){
                case OnSearchEnd:
                    updateDeviceList();
                    scanButton.setEnabled(true);
                    scanButton.setText("扫描");
                    break;
                case OnDeviceFound:
                    updateDeviceList();
                    break;
                case OnSearchStarted:
                    scanButton.setEnabled(false);
                    scanButton.setText("正在扫描..");
                    break;
                case OnStatusChanged:
                    if(BluetoothController.getInstance().isOpen()){
                        scanButton.setText("扫描");
                    }else{
                        scanButton.setText("打开蓝牙");
                    }
                    break;
            }
        }
    };

    private void updateDeviceList(){
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        devices.addAll(BluetoothController.getInstance().getBoundedDevices());
        devices.addAll(BluetoothController.getInstance().getFoundDevices());
        deviceAdapter.setModels(devices);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothController.getInstance().connect(deviceAdapter.getItem(i));
        }
    };
}
