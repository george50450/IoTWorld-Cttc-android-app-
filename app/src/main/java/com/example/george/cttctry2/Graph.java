package com.example.george.cttctry2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by George Kalampokis on 7/6/2017.
 */
public class Graph extends AppCompatActivity {
    String name_of_sensor,type_of_sensor_name,unit;
    int address_of_sensor,type_id;
    WebView wv;
    WebSettings ws;
    String url="";
    List<NameValuePair> params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);



        //get data from previous activity
        name_of_sensor = getIntent().getStringExtra("name_of_sensor");
        address_of_sensor = getIntent().getIntExtra("address_of_sensor", 0);
        unit = getIntent().getStringExtra("unit");
        type_of_sensor_name = getIntent().getStringExtra("name_of_type");
        type_id=getIntent().getIntExtra("type_id",0);
        wv=(WebView)findViewById(R.id.webview);

        //set title of activity
        setTitle(name_of_sensor + " " + "Graph");

        //request from server to get the graph url of specific sensor
        try {
            getGraphUrl("getgraphurlspecificsensor",address_of_sensor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //initialize dialog which start when webview starts loading and dismiss when webview is finished loading
        final ProgressDialog pd = ProgressDialog.show(Graph.this, "", "Please wait, graph is loading...", true,true,
                new DialogInterface.OnCancelListener(){
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onBackPressed();
                    }
                }  //if user cancells the progressdialog then onbackpressed is being called
        );

        ws = wv.getSettings();

        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ECLAIR) {
            try {
                //Log.d(TAG, "Enabling HTML5-Features");
                Method m1 = WebSettings.class.getMethod("setDomStorageEnabled", new Class[]{Boolean.TYPE});
                m1.invoke(ws, Boolean.TRUE);

                Method m2 = WebSettings.class.getMethod("setDatabaseEnabled", new Class[]{Boolean.TYPE});
                m2.invoke(ws, Boolean.TRUE);

                Method m3 = WebSettings.class.getMethod("setDatabasePath", new Class[]{String.class});
                m3.invoke(ws, "/data/data/" + getPackageName() + "/databases/");

                Method m4 = WebSettings.class.getMethod("setAppCacheMaxSize", new Class[]{Long.TYPE});
                m4.invoke(ws, 1024*1024*8);

                Method m5 = WebSettings.class.getMethod("setAppCachePath", new Class[]{String.class});
                m5.invoke(ws, "/data/data/" + getPackageName() + "/cache/");

                Method m6 = WebSettings.class.getMethod("setAppCacheEnabled", new Class[]{Boolean.TYPE});
                m6.invoke(ws, Boolean.TRUE);

                //Log.d(TAG, "Enabled HTML5-Features");
            }
            catch (NoSuchMethodException e) {
                // Log.e(TAG, "Reflection fail", e);
            }
            catch (InvocationTargetException e) {
                // Log.e(TAG, "Reflection fail", e);
            }
            catch (IllegalAccessException e) {
                // Log.e(TAG, "Reflection fail", e);
            }
        }
        wv.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                pd.show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();
            }

        });


    }

    public void getGraphUrl(String urlpart,int address_of_sensor) throws JSONException {
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("address_of_sensor", String.valueOf(address_of_sensor)));
        ServerParameterRequest sp = new ServerParameterRequest();


        JSONArray json = sp.getJSON("http://monitoring.iotworld.cttc.es/" + urlpart, params);
        if (json != null) {

            JSONObject json2 = json.getJSONObject(0);
            url=(String) json2.get("GraphUrl");

            //if url from json response is not null or empty start loading the url to the webview
            if(!(url.equals("")))
            {
                wv.loadUrl(url);

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Url invalid",Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        wv.reload();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), DisplayValueofSensor.class);
        intent.putExtra("name_of_sensor", name_of_sensor);
        intent.putExtra("name_of_type",type_of_sensor_name);
        intent.putExtra("address_of_sensor", address_of_sensor);
        intent.putExtra("unit",unit);
        intent.putExtra("type_id", type_id);
        startActivity(intent);
    }
}
