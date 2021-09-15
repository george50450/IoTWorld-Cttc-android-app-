package com.example.george.cttctry2.fragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.cttctry2.R;
import com.example.george.cttctry2.ServerRequest;
import com.example.george.cttctry2.TypeGraphs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by George on 7/11/2017.
 */
public class CostFragment extends Fragment {
    Button minutebtn,hourbtn,daybtn,monthbtn;;
    TextView minutetxt,hourtxt,daytxt,monthtxt;
    NetworkInfo networkInfo;
    Handler h;
public CostFragment() {
        }
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cost_fragment, container, false);

    //Initialize buttons
    minutebtn=(Button)rootView.findViewById(R.id.minutebutton);
    hourbtn=(Button)rootView.findViewById(R.id.hourbtn);
    daybtn=(Button)rootView.findViewById(R.id.daybtn);
    monthbtn=(Button)rootView.findViewById(R.id.monthbtn);

    minutetxt=(TextView)rootView.findViewById(R.id.minutetxt);
    hourtxt=(TextView)rootView.findViewById(R.id.hourtext);
    daytxt=(TextView)rootView.findViewById(R.id.daytxt);
    monthtxt=(TextView)rootView.findViewById(R.id.monthtxt);

    //set title of fragment
    getActivity().setTitle("Cost");


    //minutebtn listener
    minutebtn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //change background color
            minutebtn.setBackgroundColor(Color.rgb(55,49,86));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    minutebtn.setBackgroundColor(Color.TRANSPARENT);
                }
            }, 100);

            //call graph activity if internet connection exists
            ConnectivityManager connMgr = (ConnectivityManager)
            getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                Intent intent = new Intent(getActivity(), TypeGraphs.class);
                intent.putExtra("type", "perminute");
                startActivity(intent);
            }
            else
            {
                minutebtn.setBackgroundColor(Color.TRANSPARENT);
                Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
            }
        }
    });

    //hourbtn listener
    hourbtn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //change background color
            hourbtn.setBackgroundColor(Color.rgb(55,49,86));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    hourbtn.setBackgroundColor(Color.TRANSPARENT);
                }
            }, 100);
            //call graph activity if internet connection exists
            ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                Intent intent = new Intent(getActivity(), TypeGraphs.class);
                intent.putExtra("type", "perhour");
                startActivity(intent);
            }
            else
            {
                hourbtn.setBackgroundColor(Color.TRANSPARENT);
                Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
            }
        }
    });

    //daybtn listener
    daybtn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //change background color
            daybtn.setBackgroundColor(Color.rgb(55,49,86));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    daybtn.setBackgroundColor(Color.TRANSPARENT);
                }
            }, 100);
            //call graph activity if internet connection exists
            ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                Intent intent = new Intent(getActivity(), TypeGraphs.class);
                intent.putExtra("type", "perday");
                startActivity(intent);
            }
            else
            {
                daybtn.setBackgroundColor(Color.TRANSPARENT);
                Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
            }
        }
    });

    //monthbtn listener
    monthbtn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //change background color
            monthbtn.setBackgroundColor(Color.rgb(55,49,86));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    monthbtn.setBackgroundColor(Color.TRANSPARENT);
                }
            }, 100);
            //call graph activity if internet connection exists
            ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                Intent intent = new Intent(getActivity(), TypeGraphs.class);
                intent.putExtra("type", "permonth");
                startActivity(intent);
            }
            else
            {
                daybtn.setBackgroundColor(Color.TRANSPARENT);
                Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
            }
        }
    });

    //request server for the first cost values if internet is available
    try {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            test_connection("energycost1smartgrids");
            test_connection("energycost2smartgrids");
            test_connection("energycost3smartgrids");
            test_connection("energycost4smartgrids");
        }
        else
        {
            Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
            minutetxt.setText("-");
            hourtxt.setText("-");
            daytxt.setText("-");
            monthtxt.setText("-");
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }

    //every ten second send request to server to get new values from costs
    h = new Handler();
    final int delay = 10000; //milliseconds

    h.postDelayed(new Runnable(){
        public void run(){
            //do something
            try {
                ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected())
                {
                    test_connection("energycost1smartgrids");
                    test_connection("energycost2smartgrids");
                    test_connection("energycost3smartgrids");
                    test_connection("energycost4smartgrids");
                }
                else
                {
                    minutetxt.setText("-");
                    hourtxt.setText("-");
                    daytxt.setText("-");
                    monthtxt.setText("-");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            h.postDelayed(this, delay);
        }
    }, delay);
    return rootView;
}

    public void test_connection(String urlpart) throws JSONException {

        ServerRequest sr = new ServerRequest();

        JSONArray json = sr.getJSON("http://monitoring.iotworld.cttc.es/" + urlpart);
        if (json != null && (!json.equals("null"))) {

            int values_length = json.length();

            String value="-";
            if(values_length>0)
            {
                for (int i = 0; i < values_length; i++) {
                    JSONObject json2 = json.getJSONObject(i);
                    if(json2.get("value").equals(null))
                    {
                        value="-";
                    }
                    else
                    {
                        value = (String) json2.get("value");
                    }

                }
            }
            else
            {
                value="-";
            }

            //temp is only one value so values[0] is our temp value

            if (urlpart.equals("energycost1smartgrids"))
            {
                minutetxt.setText(value + " €");
            }
            else if (urlpart.equals("energycost2smartgrids"))
            {
                hourtxt.setText(value + " €");
            }
            else if (urlpart.equals("energycost3smartgrids"))
            {
                daytxt.setText(value + " €");
            }
            else if (urlpart.equals("energycost4smartgrids"))
            {
                monthtxt.setText(value + " €");
            }
            else
            {
                minutetxt.setText("- €");
                hourtxt.setText("- €");
                daytxt.setText("- €");
                monthtxt.setText("- €");
            }

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
            minutetxt.startAnimation(animation);
            hourtxt.startAnimation(animation);
            daytxt.startAnimation(animation);
            monthtxt.startAnimation(animation);
        }
        else
        {
            minutetxt.setText("-");
            hourtxt.setText("-");
            daytxt.setText("-");
            monthtxt.setText("-");
        }

}

    @Override
    public void onDestroy () {
        if(!(h.equals(null)))
        {
            h.removeCallbacksAndMessages(null);
            deleteCache(this.getActivity());
        }
        super.onDestroy ();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    @Override
    public void onStop() {
        if(!(h.equals(null)))
        {
            h.removeCallbacksAndMessages(null);
            deleteCache(getActivity());
        }
        super.onStop();
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
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
