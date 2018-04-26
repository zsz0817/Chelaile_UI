package com.example.shizhuan.chelaile_ui;


import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import java.io.Serializable;

/**
 * Created by ShiZhuan on 2018/4/24.
 */

public class Station implements Serializable {

    private static final long serialVersionUID = 1L;
    private String content;  //站点名称
    private LatLonPoint latLonPoint; //站点坐标
    private int number; //站点顺序

    public Station(int number,String content,LatLonPoint latLonPoint){
        this.content = content;
        this.number = number;
        this.latLonPoint = latLonPoint;
    }

    public Station(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LatLonPoint getLatLonPoint(){
        return latLonPoint;
    }

    public void setLatLng(LatLonPoint latLonPoint){
        this.latLonPoint = latLonPoint;
    }

    public int getNumber(){
        return number;
    }

    public void setNumber(int number){
        this.number = number;
    }

}
