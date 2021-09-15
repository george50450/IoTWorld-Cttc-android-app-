package com.example.george.cttctry2.fragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;
import com.example.george.cttctry2.R;


/**
 * Created by George Kalampokis on 6/18/2017.
 */
public class Fragment2 extends Fragment {
    WebView wv;

    public Fragment2() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.maps_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //refresh map if user selects the refresh button from menu options
        switch (item.getItemId()) {
            case R.id.refreshbutton:
                final ProgressDialog prog= new ProgressDialog(getActivity());
                prog.setTitle("Refreshing Sensor Types");
                prog.setMessage("Please wait...");
                prog.setCancelable(false);
                prog.setIndeterminate(true);
                prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                prog.show();
                try {
                    wv.getSettings().setJavaScriptEnabled(true);
                    wv.loadUrl("file:///android_asset/map.html");

                } catch (Exception e) {
                    // TODO: handle exception
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment2, container, false);
        //initialize webview
        wv=(WebView)rootView.findViewById(R.id.webview);
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !(networkInfo.isConnected()))
        {
            Toast.makeText(getActivity(),"Internet Connection NOT available!",Toast.LENGTH_LONG).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);  //needed in order to allow run external javascripts from server
        }



        try {
            //load the map file
            wv.getSettings().setJavaScriptEnabled(true);
            wv.loadUrl("file:///android_asset/map.html");

        } catch (Exception e) {
            // TODO: handle exception
        }

        return rootView;
    }
}