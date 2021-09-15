package com.example.george.cttctry2;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by George Kalampokis on 6/22/2017.
 */
public class TypeSensorsActivity extends AppCompatActivity {
    ListView sensors;
    List<NameValuePair> params;
    String name_of_type,unit;
    int type_id;
    ArrayAdapter<String> listAdapter ;

    @Override
    public void onBackPressed() {
        deleteCache(getApplicationContext());
        sensors.setAdapter(null);  //set adapter of listview null to empty the listview on back pressed and call main activity
        Intent main =  new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
        finish();

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_sensors);

        //initialize listview
        sensors=(ListView)findViewById(R.id.sensorlist);
        sensors.setAdapter(null);

        //get data from previous activity
        name_of_type = getIntent().getStringExtra("name_of_type");
        unit=getIntent().getStringExtra("unit");
        type_id=getIntent().getIntExtra("type_id",0);


        //set title of activity
        setTitle(name_of_type + " Sensors");


        //request server to get the sensors of the specific type user selected in previous activity
        try {
            test_connection("getsensorsofspecifictype",type_id);
        } catch (JSONException e) {
            e.printStackTrace();
            sensors.setAdapter(null);
        }



    }



    public void test_connection(String urlpart, final int type_id) throws JSONException {
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type_id", String.valueOf(type_id)));
        Log.d("params",String.valueOf(params));
        ServerParameterRequest sp=new ServerParameterRequest();

        //get names and addresses of sensors from json response
        //! addresses will be used later in order to get the value of the sensors
        JSONArray json = sp.getJSON("http://monitoring.iotworld.cttc.es/" + urlpart,params);
        if (json != null) {
            final int values_length = json.length();
            final String[] names = new String[values_length];
            final int[] addresses = new int[values_length];
            for (int i = 0; i < values_length; i++) {

                JSONObject json2 = json.getJSONObject(i);

                if (json2.get("name").equals(null)) {
                    names[i] = "-";
                } else {
                    names[i] = (String) json2.get("name");
                }

                if (json2.get("address").equals(null)) {
                    addresses[i] = 0;
                } else {
                    addresses[i] = (int) json2.get("address");
                }

            }

            //add names table to our listview
            ArrayList<String> sensors_names = new ArrayList<String>();
            sensors_names.addAll( Arrays.asList(names) );

            listAdapter = new ArrayAdapter<String>(this, R.layout.sensors_row,  sensors_names);

            sensors.setAdapter(listAdapter);

            //on listview item click listener
            sensors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    for (int i = 0; i < values_length; i++) {
                        if (position == i) {
                            //call the displayvalue of sensor activity passing some usefull extras
                            Intent intent = new Intent(getApplicationContext(), DisplayValueofSensor.class);
                            intent.putExtra("name_of_sensor", names[i]);
                            intent.putExtra("name_of_type",name_of_type);
                            intent.putExtra("address_of_sensor", addresses[i]);
                            intent.putExtra("unit",unit);
                            intent.putExtra("type_id", type_id);
                            startActivity(intent);
                        }
                    }
                }
            });


        }
        else
        {
            Toast.makeText(getApplicationContext(),"Mysql database unreachable..!",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frag1, menu); //same menu as frag1
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if user pushes the refresh button the reload the activity by resending request to the server
        switch (item.getItemId()) {
            case R.id.refreshbutton:
                //request server to get the sensors of the specific type user selected in previous activity
                ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null || !(networkInfo.isConnected()))
                {
                    Toast.makeText(getApplicationContext(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
                }
                else {
                    final ProgressDialog prog = new ProgressDialog(TypeSensorsActivity.this);
                    prog.setTitle("Refreshing Sensors");
                    prog.setMessage("Please wait...");
                    prog.setCancelable(false);
                    prog.setIndeterminate(true);
                    prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    prog.show();
                    try {
                        test_connection("getsensorsofspecifictype", type_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        sensors.setAdapter(null);
                    }

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            prog.dismiss();
                        }
                    }, 400);
                }
        }
        return super.onOptionsItemSelected(item);
    }
}

