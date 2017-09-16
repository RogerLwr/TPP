package com.tianpingpai.seller.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.utils.SingletonFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class BluetoothController extends ModelManager<BluetoothEvent, Model> {

    public static final String ACTION_CONNECT_STATUS = "action.connect.status";

    public static final int STATE_NOT_CONNECTED = 1;
    public static final int STATE_CONNECTING = 10;
    public static final int STATE_CONNECTED = 20;

    private int connectionState = STATE_NOT_CONNECTED;

    private BluetoothDevice mTargetDevice;
    private BluetoothDevice mCurrentDevice;

    public static BluetoothController getInstance() {
        return SingletonFactory.getInstance(BluetoothController.class);
    }

    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private GpService mService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = GpService.Stub.asInterface(service);
            if(mTargetDevice != null){
                connect(mTargetDevice);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    public BluetoothController() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECT_STATUS);
        ContextProvider.getContext().registerReceiver(printerStatusBroadcastReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        ContextProvider.getContext().registerReceiver(onOffBroadcastReceiver,filter);

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        ContextProvider.getContext().registerReceiver(discoveryBroadcastReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ContextProvider.getContext().registerReceiver(discoveryBroadcastReceiver, filter);

        Intent intent = new Intent(ContextProvider.getContext(), GpPrintService.class);
        ContextProvider.getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE); // bindService
    }

    public int getConnectionState(){
        return connectionState;
    }

    public boolean isOpen(){
        return adapter.isEnabled();
    }

    void sendReceipt(List<Line> lines) {
        EscCommand esc = new EscCommand();

        int size = lines.size();
        for (int i = 0; i < size; i++) {
            Line line = lines.get(i);
            if (line.getAlignment() == Line.ALIGNMENT_LEFT) {
                esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            } else if (line.getAlignment() == Line.ALIGNMENT_MIDDLE) {
                esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
            } else {
                esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            }
            if(line.getBitmap() != null) {
                Bitmap b = line.getBitmap();
                esc.addRastBitImage(b, 360, 0);   //打印图片
            }else if(line.getText() != null){
                if(line.getText().equals("\n")){
                    esc.addPrintAndLineFeed();
                }else{
                    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);//取消
                    esc.addText(line.getText());
                }
            }
        }

        Vector<Byte> commandBytes = esc.getCommand(); //发送数据
        Byte[] Bytes = commandBytes.toArray(new Byte[commandBytes.size()]);
        byte[] bytes = new byte[Bytes.length];
        for (int i = 0; i < Bytes.length; i++) {
            bytes[i] = Bytes[i];
        }
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mService.sendEscCommand(1, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(ContextProvider.getContext(), GpCom.getErrorText(r),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean print(List<Line> lines) {
        if(mService == null){
            return false;
            //TODO
        }
        try {
            int type = mService.getPrinterCommandType(1);
            if (type == GpCom.ESC_COMMAND) {
                int status = mService.queryPrinterStatus(1, 500);
                if (status == GpCom.STATE_NO_ERR) {
                    sendReceipt(lines);
                } else {
                    Toast.makeText(ContextProvider.getContext(),
                            "打印机错误！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return true;
    }

    public void autoConnect() {
        if(isConnection()){
            return;
        }
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        String address = "98:D3:31:80:31:37";
        BluetoothDevice device = null;
        for (BluetoothDevice bd : devices) {
            Log.e("xx", "bd:" + bd.getName() + "address:" + bd.getAddress());
            if(address.equals(bd.getAddress())){
                device = bd;
                break;
            }
        }
        if(device != null) {
            connect(device);
        }
    }

    private boolean isConnection() {
        return true;
    }

    public void connect(BluetoothDevice device) {
        mTargetDevice = device;
        if (mService == null) {
            Intent intent = new Intent(ContextProvider.getContext(), GpPrintService.class);
            ContextProvider.getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE); // bindService
        } else {
            if(mCurrentDevice != null){
                try {
                    mService.closePort(1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            try {
                int rel = mService.openPort(1, PortParameters.BLUETOOTH, device.getAddress(), 0);
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                if(r == GpCom.ERROR_CODE.SUCCESS){
                    mCurrentDevice = device;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public void disconnect(BluetoothDevice d) {
//        device.closePort();
    }

    public void startDiscovery() {
        foundDevices.clear();
        adapter.startDiscovery();
        notifyEvent(BluetoothEvent.OnSearchStarted, null);
    }

    private ArrayList<BluetoothDevice> foundDevices = new ArrayList<>();

    public ArrayList<BluetoothDevice> getFoundDevices(){
        return foundDevices;
    }

    public Set<BluetoothDevice> getBoundedDevices(){
        return adapter.getBondedDevices();
    }

    private BroadcastReceiver discoveryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("xx","dis:" + action);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    foundDevices.add(device);
                    notifyEvent(BluetoothEvent.OnDeviceFound,null);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                notifyEvent(BluetoothEvent.OnSearchEnd,null);
            }
        }
    };

    private BroadcastReceiver printerStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("xx", "action:" + intent.getAction());
            if (ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                Log.e("xx", "type:" + type);
                Log.e("xx", "id:" + id);
                if (type == GpDevice.STATE_CONNECTING) {
                    Log.e("xx", "正在连接");
                    connectionState = STATE_CONNECTING;
                    notifyEvent(BluetoothEvent.OnConnecting,null);
                } else if (type == GpDevice.STATE_NONE) {
                    Log.e("xx", "None");
                } else if (type == GpDevice.STATE_VALID_PRINTER) {
                    Log.e("xx", "valid");
                    connectionState = STATE_CONNECTED;
                    notifyEvent(BluetoothEvent.OnConnected, null);
                } else if (type == GpDevice.STATE_INVALID_PRINTER) {
                    Log.e("xx", "invalid");
                    connectionState = STATE_NOT_CONNECTED;
                    notifyEvent(BluetoothEvent.OnConnectFailed, null);
                } else if (type == GpDevice.STATE_CONNECTED) {
                    Log.e("xx", "connected");

                }
            }
        }
    };

    private BroadcastReceiver onOffBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyEvent(BluetoothEvent.OnStatusChanged,null);
        }
    };

    public void open() {
        adapter.enable();
    }
}
