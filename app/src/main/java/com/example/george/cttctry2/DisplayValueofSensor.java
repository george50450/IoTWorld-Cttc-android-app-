package com.example.george.cttctry2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by George Kalampokis on 7/1/2017.
 */
public class DisplayValueofSensor extends AppCompatActivity {
    String name_of_sensor, unit, name_of_type;
    int address_of_sensor,type_id;
    List<NameValuePair> params;
    TextView sensorname, sensorvalue;
    NetworkInfo networkInfo;
    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_valueof_sensor);

        //initialization
        sensorname = (TextView) findViewById(R.id.sensrname);
        sensorvalue = (TextView) findViewById(R.id.sensrvalue);

        //get data from previous activity
        name_of_sensor = getIntent().getStringExtra("name_of_sensor");
        unit = getIntent().getStringExtra("unit");
        address_of_sensor = getIntent().getIntExtra("address_of_sensor", 0);
        name_of_type = getIntent().getStringExtra("name_of_type");
        type_id=getIntent().getIntExtra("type_id",0);

        //set name of sensor to sensor textview and title to the activity
        sensorname.setText(name_of_sensor);
        setTitle(name_of_type + ": " + name_of_sensor);

        //get value of sensor if internet connection is available
        try {
            ConnectivityManager connMgr = (ConnectivityManager) DisplayValueofSensor.this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                test_connection("getvalueofsensorspecifictype", address_of_sensor);
            } else {
                sensorvalue.setText("- " + unit);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        h = new Handler();
        final int delay = 2000; //milliseconds

        //get value of sensor every 2 seconds
        h.postDelayed(new Runnable() {
            public void run() {
                try {

                    ConnectivityManager connMgr = (ConnectivityManager) DisplayValueofSensor.this
                            .getSystemService(Context.CONNECTIVITY_SERVICE);

                    networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        test_connection("getvalueofsensorspecifictype", address_of_sensor);
                    } else {
                        sensorvalue.setText("- " + unit);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                h.postDelayed(this, delay);
            }
        }, delay);

    }

    public void test_connection(String urlpart, int address_of_sensor) throws JSONException {
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("address_of_sensor", String.valueOf(address_of_sensor)));
        ServerParameterRequest sp = new ServerParameterRequest();

        //store value from json response
        JSONArray json = sp.getJSON("http://monitoring.iotworld.cttc.es/" + urlpart, params);
        if (json != null) {
            final int values_length = json.length();
            String value = "-";
            for (int i = 0; i < values_length; i++) {
                JSONObject json2 = json.getJSONObject(i);

                if (json2.get("value").equals(null)) {
                    value = "-";
                } else {
                    value = (String) json2.get("value");
                }
            }
            if(unit.equals("ÂºC"))  //bug encoding with ºC
            {
                unit="ºC";
            }

            //set textview to value and unit
            sensorvalue.setText(value + " " + unit);

            //create animation and set it to textview
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(500);


            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setStartOffset(500);
            fadeOut.setDuration(500);

            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            //animation.addAnimation(fadeOut);
            sensorvalue.startAnimation(animation);  //start animation of textview after it has been set

        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        h.removeCallbacksAndMessages(null);
        deleteCache(getApplicationContext());
        Intent intent = new Intent(this, TypeSensorsActivity.class);
        intent.putExtra("type_id",type_id);
        intent.putExtra("name_of_type", name_of_type);
        intent.putExtra("unit",unit);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graphs_plot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if user selects the graphbutton from the options menu
            case R.id.graphsbutton:
                ConnectivityManager connMgr = (ConnectivityManager) DisplayValueofSensor.this
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected())
                {
                    h.removeCallbacksAndMessages(null);   //remove handler callbacks
                    deleteCache(getApplicationContext()); //delete cache
                    //start graph class if network is available
                    Intent intent = new Intent(this, Graph.class);
                    intent.putExtra("address_of_sensor", address_of_sensor);
                    intent.putExtra("unit", unit);
                    intent.putExtra("name_of_sensor", name_of_sensor);
                    intent.putExtra("name_of_type", name_of_type);
                    intent.putExtra("type_id", type_id);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}

