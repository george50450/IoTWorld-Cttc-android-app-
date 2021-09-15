package com.example.george.cttctry2.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.cttctry2.R;
import com.example.george.cttctry2.ServerRequest;
import com.example.george.cttctry2.TypeSensorsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static android.R.attr.port;

/**
 * Created by George Kalampokis on 6/18/2017.
 */

public class Fragment1 extends Fragment {
    ListView sensortypeslist;
    public Fragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment1, container, false);
        ///initialize sensortypeslist
        sensortypeslist=(ListView)rootView.findViewById(R.id.typesofsensors);

        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !(networkInfo.isConnected()))
        {
            Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
        }
        //if internet is available request from server sensor types and icons


        try {
            test_connection("getsensortypesandicons");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    public void test_connection(String urlpart) throws JSONException {
        //request to the server
        ServerRequest sr = new ServerRequest();


        JSONArray json = sr.getJSON("http://monitoring.iotworld.cttc.es/" + urlpart);
        if (json != null) {

            //storing json to tables
            final int values_length = json.length();
            final String[] names_of_types = new String[values_length];
            final String[] units = new String[values_length];
            final String[] urls = new String[values_length];
            final int[] types = new int[values_length];
            //store data to tables from the json that server returned
            for (int i = 0; i < values_length; i++) {

                JSONObject json2 = json.getJSONObject(i);

                if (json2.get("name").equals(null) || json2.get("name")==null || json2.get("name").equals("")) {
                    names_of_types[i] = "-";
                } else {
                    names_of_types[i] = (String) json2.get("name");
                }

                if (json2.get("unit").equals(null) || json2.get("unit")==null || json2.get("unit").equals("")) {
                    units[i] = "-";
                } else {
                    units[i] = (String) json2.get("unit");

                }

                if (json2.get("type").equals(null) || json2.get("type")==null || json2.get("type").equals("")) {
                    types[i] = 0;
                } else {
                    types[i] = (int) json2.get("type");

                }

                if (json2.get("IconUrl").equals(null) || json2.get("IconUrl")==null || json2.get("IconUrl").equals("")) {
                    urls[i] = "-";
                } else {
                    urls[i] = (String) json2.get("IconUrl");

                }


            }
            //initialize customlist adapter in order to have the image of sensor and the type of sensor
            CustomList adapter = new CustomList(getActivity(), names_of_types, urls);
            sensortypeslist.setAdapter(adapter);
            sensortypeslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    for (int i = 0; i < values_length; i++) {
                        if (position == i) {
                            ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);

                            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                //if connection exists start the activity which lists the sensors of the type user selected
                                Intent intent = new Intent(getActivity(), TypeSensorsActivity.class);
                                intent.putExtra("name_of_type", names_of_types[i]);
                                intent.putExtra("type_id", types[i]);
                                intent.putExtra("unit", units[i]);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });


        }
    }

    //Custom list class
    public class CustomList extends ArrayAdapter<String>{

        private final Activity context;
        private final String[] web;
        private final String[] imageId;
        public CustomList(Activity context,
                          String[] web, String[] imageId) {
            super(context, R.layout.custom_row, web);
            this.context = context;
            this.web = web;
            this.imageId = imageId;

        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.custom_row, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.sensorname);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.sensorimage);
            txtTitle.setText(web[position]);
            //if image exists load that image else open the error image instead
            if(imageId[position]!="-")
            {
                Picasso.with(getContext()).load(imageId[position]).resize(100, 100)
                        .error(R.drawable.error).into(imageView);
            }
            else
            {
                imageView.setImageResource(R.drawable.error);
            }
            return rowView;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate the fragment1 menu
        inflater.inflate(R.menu.frag1, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if user pushes the refresh button the reload the activity by resending request to the server
        switch (item.getItemId()) {
            case R.id.refreshbutton:
                ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null || !(networkInfo.isConnected()))
                {
                    Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    final ProgressDialog prog = new ProgressDialog(getActivity());
                    prog.setTitle("Refreshing Sensor Types");
                    prog.setMessage("Please wait...");
                    prog.setCancelable(false);
                    prog.setIndeterminate(true);
                    prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    prog.show();
                    try {
                        test_connection("getsensortypesandicons");
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
