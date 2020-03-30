package com.example.excar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BlueLink extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button openLine,CloseLine,back,send;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> deviceList = new ArrayList<>();

    private BluetoothSocket socket;
    private OutputStream os;
    private BluetoothDevice devices;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        start();
    }

    private  void start(){
        openLine =findViewById(R.id.openline1);
        CloseLine = findViewById(R.id.closeline1);
        back  =findViewById(R.id.fanhui1);
        openLine.setOnClickListener(this);
        send = findViewById(R.id.send);
        CloseLine.setOnClickListener(this);
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        listView = findViewById(R.id.listview1);
        listAdapter = new ArrayAdapter<String>(BlueLink.this,
                android.R.layout.simple_list_item_1,deviceList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
    }
    private BlueReceiver blueReceiver;
    private void search(){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }
            Set<BluetoothDevice> set = bluetoothAdapter.getBondedDevices();
            //获取已配对过的蓝牙键值对
            for(BluetoothDevice tmp:set){
                StringBuilder sb = new StringBuilder();
                sb.append(tmp.getName()).append(" ").append(tmp.getAddress());
                deviceList.add(sb.toString());
            }
            listAdapter.notifyDataSetChanged();
            blueReceiver  = new BlueReceiver();//设置广播的监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        this.registerReceiver(blueReceiver,intentFilter);
        bluetoothAdapter.startDiscovery();

    };
    private class BlueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();//获取广播的动作
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//获取蓝牙设备的相关参数
                    String gg = device.getName()+" "+device.getAddress();//Name：设备名  Address：蓝牙的Mac地址
                    if(!device.getName().equals(null)){
                    if(!deviceList.contains(gg)){
                        deviceList.add(gg);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                }
                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    Toast.makeText(BlueLink.this, "停止搜索了",
                            Toast.LENGTH_SHORT).show();
                }
        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.openline1:
                search();
                break;
            case R.id.closeline1:

                break;
            case R.id.fanhui1:
                finish();
                break;
            case R.id.send:
                send("0x111");
                break;
        }
    }

    private void connect(){
        try {
            socket = devices.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void send(String ab) {

        try {
            socket = devices.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            //  os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*byte[] msg =ab.getBytes();
        try {
            os.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (os != null) {
            try {
                os.write("蓝牙信息来了".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String tmp = deviceList.get(i);
            String[]  a = tmp.split(" ");                                           //从列表中去出相关的数据，这个和储存数据的形式有关// 最终能解析出address（蓝牙的mac地址）即可
            String address = a[1];
            String name = a[0];
           devices = bluetoothAdapter.getRemoteDevice(address);//配对蓝牙设备
            try{
                if(devices.getBondState()==BluetoothDevice.BOND_NONE){
                    Method createBondMethod = BluetoothDevice.class.getMethod(name);
                    //createBond
                    Log.d("hello","开始配对");
                    Boolean result =(Boolean)createBondMethod.invoke(devices);

                }else if(devices.getBondState()==BluetoothDevice.BOND_BONDED){
                    connect();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

    }
}

