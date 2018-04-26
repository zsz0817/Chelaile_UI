package com.example.shizhuan.chelaile_ui;

import android.app.Application;

import java.util.List;

/**
 * Created by ShiZhuan on 2018/4/25.
 */

public class MyApplication extends Application {
    private List<Station> list;
    private List<Station> currentline;
    private String bus_line;

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
