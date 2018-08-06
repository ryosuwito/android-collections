package com.riko.trafiko;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //widgets
    Button btnConnect, btnLamp1, btnLamp2, btnLamp3, btnLamp4;
    ListView listDevice;
    TextView textStatus, simpang1, simpang2, simpang3, simpang4, listTitle;
    //Bluetooth
    private ArrayAdapter emptyAdapter = null;
    private BluetoothAdapter mBluetooth = null;
    private BluetoothAdapter mBtAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    private String remoteAddress = "";
    private Handler handler = null;
    BluetoothSocket mSocket = null;
    private boolean isConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList list = new ArrayList();
        emptyAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setBackgroundColor(0xFF44FF66);
        btnLamp1 = (Button)findViewById(R.id.btnLamp1);
        btnLamp1.setText("Urgent");
        btnLamp2 = (Button)findViewById(R.id.btnLamp2);
        btnLamp2.setText("Urgent");
        btnLamp3 = (Button)findViewById(R.id.btnLamp3);
        btnLamp3.setText("Urgent");
        btnLamp4 = (Button)findViewById(R.id.btnLamp4);
        btnLamp4.setText("Urgent");

        listDevice = (ListView)findViewById(R.id.listDevice);

        mBluetooth = BluetoothAdapter.getDefaultAdapter();

        textStatus = (TextView)findViewById(R.id.textStatus);
        textStatus.setText("Device is not connected");
        simpang1 = (TextView)findViewById(R.id.simpang1);
        simpang1.setText("Jalur No#1");
        simpang2 = (TextView)findViewById(R.id.simpang2);
        simpang2.setText("Jalur No#2");
        simpang3 = (TextView)findViewById(R.id.simpang3);
        simpang3.setText("Jalur No#3");
        simpang4 = (TextView)findViewById(R.id.simpang4);
        simpang4.setText("Jalur No#4");
        listTitle = (TextView)findViewById(R.id.listTitle);
        listTitle.setText(" ");

        if(mBluetooth == null){
            //Show a message. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Perangkat tidak ditemukan", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!mBluetooth.isEnabled()){
            Intent turnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBluetooth, 1);
        }

        btnConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pairedDevicesList();
            }
        });

        btnLamp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnLed("1");
            }
        });
        btnLamp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnLed("2");
            }
        });
        btnLamp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnLed("3");
            }
        });
        btnLamp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnLed("4");
            }
        });
    };

    private void pairedDevicesList(){
        pairedDevices = mBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size()>0){
            for(BluetoothDevice bt : pairedDevices){
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
            listTitle.setText("Daftar perangkat Bluetooth");
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Perangkat Bluetooth tidak ditemukan",
                    Toast.LENGTH_LONG).show();
            listTitle.setText("Perangkat Bluetooth tidak ditemukan");
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listDevice.setAdapter(adapter);
        listDevice.setOnItemClickListener(mListClickListener);
    };

    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            remoteAddress = address;
            new ConnectBT().execute();

        }
    };

    private class ConnectBT extends AsyncTask<Void, Void, Void>{
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Void doInBackground(Void... devices){
            try{
                if(mSocket == null || !isConnected){
                    mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = mBtAdapter.getRemoteDevice(remoteAddress);
                    mSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mSocket.connect();
                }
            }
            catch (IOException e){
                connectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!connectSuccess) {
                msg("Koneksi Gagal. Coba Lagi.");
                finish();
            } else {

                listDevice.setAdapter(emptyAdapter);

                msg("Koneksi Berhasil.");
                textStatus.setText("Device is connected");
                btnConnect.setText("Disconnect");;
                btnConnect.setBackgroundColor(0xFFFF6644);
                listTitle.setText(" ");
                btnConnect.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        disconnect();
                    }
                });
                isConnected = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void msg(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void disconnect(){
        if(mSocket != null){
            try {
                mSocket.close();
                isConnected = false;
                textStatus.setText("Device is not connected");
                btnConnect.setText("Connect");
                btnConnect.setBackgroundColor(0xFF44FF66);
                listDevice.setAdapter(emptyAdapter);

                btnConnect.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        pairedDevicesList();
                    }
                });
            }
            catch(IOException e){
                msg("Pemutusan Koneksi Error");
                finish();
            }
        }
    }

    private void turnOnLed(String index){
        if(mSocket!=null){
            try {
                mSocket.getOutputStream().write(index.toString().getBytes());
                msg("Data Terkirim");
                btnLamp1.setEnabled(false);
                btnLamp2.setEnabled(false);
                btnLamp3.setEnabled(false);
                btnLamp4.setEnabled(false);
                buttonDelay();
            }
            catch (IOException e){
                msg("Gagal mengirim data");
            }
        }
    }
    private void buttonDelay(){
        handler = new Handler();
        handler.postDelayed(runnable, 15000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            btnLamp1.setEnabled(true);
            btnLamp2.setEnabled(true);
            btnLamp3.setEnabled(true);
            btnLamp4.setEnabled(true);
            handler.postDelayed(this,100);
        }
    };
}
