package com.riko.smartkoi;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private URL murl;
    private HttpURLConnection conn;
    private InputStream is;
    private String mUrls,connMessage;
    private String pakanInterval = null;
    private String servoInterval = null;
    private String suhuMaks = null;
    private String suhuMin = null;
    private String phMaks = null;
    private String phMin = null;
    private String tanggal = null;
    private TextView suhuMinText, suhuMaksText, phMinText, phMaksText;
    private TextView servoIntervalText, pakanIntervalText, tanggalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btn = (Button) findViewById(R.id.grafik);

        suhuMaksText = (TextView) findViewById(R.id.suhuMaksText);
        suhuMinText = (TextView) findViewById(R.id.suhuMinText);
        phMaksText = (TextView) findViewById(R.id.phMaksText);
        phMinText = (TextView) findViewById(R.id.phMinText);
        servoIntervalText = (TextView) findViewById(R.id.servoIntervalText);
        pakanIntervalText = (TextView) findViewById(R.id.pakanIntervalText);
        tanggalText = (TextView) findViewById(R.id.tanggalText);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GrafikActivity.class);
                i.setData(Uri.parse("http://smartkoi.topters.us"));
                startActivity(i);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        new GetSettingTask().execute();
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
            Intent i = new Intent(getApplicationContext(), SetelanActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetSettingTask extends AsyncTask<URL, Integer, Long>{

        @Override
        protected Long doInBackground(URL... urls) {
            JSONArray array = null;
            JSONObject object = null;
            final StringBuilder result = new StringBuilder();
            mUrls = "http://smartkoi.topters.us/getsettings.php";
            try {
                murl = new URL(mUrls);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                conn = (HttpURLConnection)murl.openConnection();
                conn.setReadTimeout(10000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null){
                    result.append(line);
                }
                try {
                    array = new JSONArray(result.toString());
                    object = array.getJSONObject(0);
                    pakanInterval = ": "+object.get("pakanInterval").toString()+" menit";
                    servoInterval = ": "+object.get("servoInterval").toString()+" detik";
                    suhuMaks = ": "+object.get("suhuMaks").toString()+" C";
                    suhuMin = ": "+object.get("suhuMin").toString()+" C";
                    phMaks = ": "+object.get("phMaks").toString();
                    phMin = ": "+object.get("phMin").toString();
                    tanggal = ": "+object.get("tanggal").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                is.close();
                connMessage = conn.getResponseMessage();
                conn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Handler handler =  new Handler(MainActivity.this.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    pakanIntervalText.setText(pakanInterval);
                    servoIntervalText.setText(servoInterval);
                    suhuMaksText.setText(suhuMaks);
                    suhuMinText.setText(suhuMin);
                    phMaksText.setText(phMaks);
                    phMinText.setText(phMin);
                    tanggalText.setText(tanggal);
                    Toast.makeText(MainActivity.this, "Update Success",Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }
    }
}
