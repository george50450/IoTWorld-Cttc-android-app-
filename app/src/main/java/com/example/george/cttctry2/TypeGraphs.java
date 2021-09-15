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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Created by George Kalampokis on 7/16/2017.
 */
public class TypeGraphs extends AppCompatActivity {
    String type;
    WebView wv;
    WebSettings ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_graphs);



        //get type of cost graph which user selected in previous activity
        type = getIntent().getStringExtra("type");

        //set Title
        setTitle("Cost " + type + " Graph");

        final ProgressDialog pd = ProgressDialog.show(TypeGraphs.this, "", "Please wait, graph is loading...", true,true,
                new DialogInterface.OnCancelListener(){
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onBackPressed();

                    }
                }
        );


        wv = (WebView) findViewById(R.id.web);
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

        //load url of graphana plot depending on type
        if(type.equals("perminute"))
        {
            wv.loadUrl("http://84.88.61.206:8000/dashboard-solo/db/iotworld-average-minute-dashboard?refresh=1m&orgId=1&panelId=5&theme=light");
        }
        else if(type.equals("perhour"))
        {
            wv.loadUrl("http://84.88.61.206:8000/dashboard-solo/db/iotworld-average-hour-dashboard?refresh=5s&orgId=1&panelId=4&theme=light");
        }
        else if (type.equals("perday"))
        {
            wv.loadUrl("http://84.88.61.206:8000/dashboard-solo/db/iotworld-average-day-dashboard?refresh=1s&orgId=1&panelId=4&theme=light");
        }
        else if(type.equals("permonth"))
        {
            wv.loadUrl("http://84.88.61.206:8000/dashboard-solo/db/iotworld-average-month-dashboard?refresh=5s&orgId=1&panelId=4&theme=light");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        wv.reload();
        /*
        Intent intent = new Intent(getApplicationContext(), TypeGraphs.class);
        intent.putExtra("type", "perday");
        startActivity(intent);*/
    }

    @Override
    public void onBackPressed() {
        Intent main =  new Intent(getApplicationContext(), MainActivity.class);
        //in order to load fragment one which is cost fragment
        main.putExtra("frag", 1);
        startActivity(main);
        finish();
    }

}
