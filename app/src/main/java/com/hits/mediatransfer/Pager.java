package com.hits.mediatransfer;


import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.util.Log;

import android.view.MenuItem;

import org.json.JSONArray;

import java.io.File;


/**
 * Created by charleston on 30/11/15.
 */
public class Pager extends AppCompatActivity {
    ViewPager pager;
    TabLayout tabLayout;
    String serverAddress = "";
    static String TAG = "ExelLog";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("pager", "Pager ok");
        //Log.d("number", Long.parseLong("20150627161946432")+"");
        String extStore = System.getenv("EXTERNAL_STORAGE");
        String secStore = System.getenv("SECONDARY_STORAGE");
        Log.d("storages", extStore+" - "+secStore);
        Log.d("DCIM", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        Log.d("external ", getExternalFilesDirs("1")[1].toString());

        setContentView(R.layout.pager);
        checkPermission();
        pager= (ViewPager) findViewById(R.id.view_pager);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);

        // Fragment manager to add fragment in viewpager we will pass object of Fragment manager to adpater class.
        FragmentManager manager=getSupportFragmentManager();

        //object of PagerAdapter passing fragment manager object as a parameter of constructor of PagerAdapter class.
        PagerAdapter adapter=new PagerAdapter(manager);

        //set Adapter to view pager
        pager.setAdapter(adapter);

        //set tablayout with viewpager
        //tabLayout.setupWithViewPager(pager);

        // adding functionality to tab and viewpager to manage each other when a page is changed or when a tab is selected
        //pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Setting tabs from adpater
        //tabLayout.setTabsFromPagerAdapter(adapter);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //myToolbar.setTitle("");
        //setSupportActionBar(myToolbar);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_wifi) {
            connectWifi(ssid, senha_wifi);
            //removeWifi("xt3");
            return true;
        }*/
        /*else if (id == R.id.action_facebook) {
            Uri uri = Uri.parse("fb://messaging/100003874832086");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }



    private class TarefaPesada extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray dados = null;

            return dados;
        }

        @Override
        protected void onPostExecute(JSONArray dados) {


        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    /*public void getServerAddress() {
        if(serverAddress.equals("")){
            String ip = wifiIpAddress(getBaseContext());
            if(ip!=null){
                final String ip_part = ip.substring(0, ip.lastIndexOf("."))+".";

                for (int i = 1; i < 255; i++) {
                    URI uri;
                    try {
                        uri = new URI("http://"+ip_part+i+":8080/who");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return;
                    }
                    final int ip_final = i;
                    WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                        @Override
                        public void onOpen(ServerHandshake serverHandshake) {
                            Log.i("Websocket", "Opened");
                            //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                            Log.d("server", ip_part+ip_final+"");
                            serverAddress = ip_part+ip_final+"";
                        }

                        @Override
                        public void onMessage(String s) {
                            final String message = s;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("ws message", message);
                                    Toast.makeText(Pager.super.getApplicationContext(), "Server is: "+message, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onClose(int i, String s, boolean b) {
                            Log.i("Websocket", "Closed " + s);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.i("Websocket", "Error " + e.getMessage());
                        }
                    };
                    mWebSocketClient.connect();
                }
            }
            else{
                Toast.makeText(getBaseContext(), "Wifi não conectada", Toast.LENGTH_LONG).show();
            }
        }
        buscarMedias();
    }
    protected String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }*/
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)     {
                    //Peform your task here if any
                } else {

                    checkPermission();
                }
                return;
            }
        }
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
