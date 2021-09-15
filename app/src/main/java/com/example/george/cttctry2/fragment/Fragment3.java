package com.example.george.cttctry2.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.george.cttctry2.R;
/**
 * Created by George Kalampokis on 6/18/2017.
 */
public class Fragment3 extends Fragment {
    public WebView mWebView;
    public Fragment3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment3, container, false);
        //initialize webview
        mWebView = (WebView) rootView.findViewById(R.id.webview);
        //open the local application_info.html file
        try {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl("file:///android_asset/application_info.html");

        } catch (Exception e) {
            // TODO: handle exception
        }

        return rootView;
    }




}
