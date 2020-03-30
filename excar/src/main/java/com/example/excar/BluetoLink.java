package com.example.excar;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoLink extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ListView listView;
    private Button back,openLine,closeLine;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> deviceList =new ArrayList<>();
    private ArrayAdapter<String> deviceAdapter;
    private BluetoothDevice device;
    private BluetoothSocket clientSocket;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private OutputStream os;


    private Myadapter<String> madapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        back = findViewById(R.id.fanhui);
        back.setOnClickListener(this);
        openLine = findViewById(R.id.openline);
        closeLine=findViewById(R.id.closeline);
        openLine.setOnClickListener(this);
        closeLine.setOnClickListener(this);
        listView =findViewById(R.id.listview);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new ArrayAdapter<String>(BluetoLink.this,
                android.R.layout.simple_list_item_1,deviceList);
        listView.setAdapter(deviceAdapter);
        listView.setOnItemClickListener(BluetoLink.this);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.openline:
                search();
                break;
            case R.id.closeline:

                break;
            case R.id.fanhui:
                finish();
                break;
        }
    }

    private BluetoothReceive Receiver;

    public void search(){

        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
        Receiver = new BluetoothReceive();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        bluetoothAdapter.startDiscovery();
        this.registerReceiver(Receiver,filter);

    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        unregisterReceiver(Receiver);
        String s = deviceAdapter.getItem(i);
        String address = s.substring(s.indexOf(":")+1).trim();
        Toast.makeText(this, address, Toast.LENGTH_LONG).show();

        try{
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            if(device==null)
            {
                device = bluetoothAdapter.getRemoteDevice(address);
            }
            if(clientSocket==null){
                clientSocket =device.createInsecureRfcommSocketToServiceRecord(uuid);
                clientSocket.connect();
                //获得输出流（客户端指向服务端输出文本）
                os = clientSocket.getOutputStream();

            }


        }catch (Exception e){
            e.printStackTrace();
        }
        if(os!=null){
            try {
                os.write("蓝牙信息来了".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private class BluetoothReceive extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    deviceList.add(device.getName() + ":" + device.getAddress());
                    deviceAdapter.notifyDataSetChanged();
                }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    Log.d("blue", "jkl");
                }
                else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                    Log.d("blue","uuu");
                }
            }



        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
