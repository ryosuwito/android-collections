package com.riko.smartkoi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class SetelanActivity extends AppCompatActivity {
    private URL mUrl;
    private HttpURLConnection conn;
    private OutputStream os;
    private String mUrls;
    private EditText pakanInterval, servoInterval, suhuMin, suhuMaks, phMaks, phMin;
    private Button btnSimpan;
    private String suhuMaksValue, suhuMinValue, phMaksValue, phMinValue;
    private String pakanIntervalValue, servoIntervalValue, connMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setelan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pakanInterval = (EditText) findViewById(R.id.pakanInterval);
        servoInterval = (EditText) findViewById(R.id.servoInterval);
        suhuMaks = (EditText) findViewById(R.id.suhuMaks);
        suhuMin = (EditText) findViewById(R.id.suhuMin);
        phMaks = (EditText) findViewById(R.id.phMaks);
        phMin = (EditText) findViewById(R.id.phMin);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PI = pakanInterval.getText().toString();
                String SI = servoInterval.getText().toString();
                String SX = suhuMaks.getText().toString();
                String SM = suhuMin.getText().toString();
                String PX = phMaks.getText().toString();
                String PM = phMin.getText().toString();
                if(PI==null || PI.isEmpty()) PI = "0";
                if(SI==null || SI.isEmpty()) SI = "0";
                if(SX==null || SX.isEmpty()) SX = "0";
                if(SM==null || SM.isEmpty()) SM = "0";
                if(PX==null || PX.isEmpty()) PX = "0";
                if(PM==null || PM.isEmpty()) PM = "0";
                Log.v("PI", PI);
                Log.v("SI", SI);
                Log.v("SX", SX);
                Log.v("SM", SM);
                Log.v("PX", PX);
                Log.v("PM", PM);

                suhuMaksValue = SX;
                suhuMinValue = SM;
                phMaksValue = PX;
                phMinValue = PM;
                pakanIntervalValue = PI;
                servoIntervalValue = SI;

                new SaveSettingTask().execute(mUrl);
            }
        });

    }

    private class SaveSettingTask extends AsyncTask<URL, Integer, Long>{

        @Override
        protected Long doInBackground(URL... urls) {
            try {
                String baseUrl = "http://smartkoi.topters.us/setsettings.php";
                mUrls = baseUrl + "?pi="+pakanIntervalValue;
                mUrls += "&si="+servoIntervalValue;
                mUrls += "&sx="+suhuMaksValue;
                mUrls += "&sm="+suhuMinValue;
                mUrls += "&px="+phMaksValue;
                mUrls += "&pm="+phMinValue;
                Log.v("murls", mUrls);
                mUrl = new URL(mUrls);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection) mUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                os = conn.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                os.close();
                Log.v("responseCode",conn.getResponseMessage());
                connMessage = conn.getResponseMessage();
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Handler handler =  new Handler(SetelanActivity.this.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(SetelanActivity.this, connMessage,Toast.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }
}
