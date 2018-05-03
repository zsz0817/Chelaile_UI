package com.example.shizhuan.chelaile_ui;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShiZhuan on 2018/4/25.
 */

public class MyApplication extends Application {

    private List<Station> list;
    private List<Station> currentline;
    private String bus_line;
    private int line_number = 0;

    public static final String TAG = MyApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;

    private List<Activity> oList;//用于存放所有启动的Activity的集合

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        oList = new ArrayList<Activity>();
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
        // 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
        //判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
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

    public void setLine_number(int line_number){
        this.line_number = line_number;
    }

    public int getLine_number(){
        return line_number;
    }
}
