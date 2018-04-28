package com.example.shizhuan.chelaile_ui;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by ShiZhuan on 2018/4/25.
 */

public class MyApplication extends Application {
    private List<Station> list;
    private List<Station> currentline;
    private String bus_line;

    public static final String TAG = MyApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    public void setlist(List<Station> list){
        this.list = list;
    }

    public List<Station> getlist(){
        return list;
    }

    public void setline(String line){
        this.bus_line = line;
    }

    public String getline(){
        return bus_line;
    }

    public void setcurrentline(List<Station> currentline){
        this.currentline = currentline;
    }

    public List<Station> getcurrentline(){
        return currentline;
    }
}
