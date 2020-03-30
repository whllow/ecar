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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public class Blue extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private ArrayAdapter<String> deviceAdapter;
    private ArrayList<String> deviceList = new ArrayList<>();
    private Button but,but2;
    private ListView listView;
    private FileOutputStream os;
    private BluetoothSocket socket;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cell);
        begin();
    }
    private void begin(){
        but = findViewById(R.id.button);
        but2  = findViewById(R.id.button2);
        but.setOnClickListener(this);
        but2.setOnClickListener(this);
        listView  = findViewById(R.id.list);
        deviceAdapter = new ArrayAdapter<>(Blue.this,android.R.layout.simple_list_item_1
        ,deviceList);
        listView.setAdapter(deviceAdapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button:
                search();
                break;
            case R.id.button2:
                close();
                break;
        }
    }
    class Listener extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                StringBuilder sb = new StringBuilder();
                sb.append(device.getName()).append(" ").append(device.getAddress());
                if(!deviceList.contains(sb.toString())){
                    deviceList.add(sb.toString());
                    deviceAdapter.notifyDataSetChanged();
                }
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(Blue.this, "停止搜索", Toast.LENGTH_SHORT).show();
            }
            }
            }
    private void close(){
        bluetoothAdapter.cancelDiscovery();
    }

    private Listener listener;

    private void search(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
        IntentFilter filter  = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        listener = new Listener();
        this.registerReceiver(listener,filter);
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String tmp = deviceList.get(i);
            String[] a = tmp.split(" ");
            String address = a[1];
            String name = a[0];
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            try{
                if(bluetoothDevice.getBondState()==BluetoothDevice.BOND_NONE){
                    Method createBondMethod = BluetoothDevice.class.getMethod(name);
                    Toast.makeText(this, "正在配对", Toast.LENGTH_SHORT).show();
                    Boolean result =(Boolean)createBondMethod.invoke(bluetoothDevice);
                }
                else if(bluetoothDevice.getBondState()==BluetoothDevice.BOND_BONDED)
                {
                   send();
                }
            }catch(NoSuchMethodException e){
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
    }


    public void send(){
        try {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str ="hello world";
        try {
            os.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
