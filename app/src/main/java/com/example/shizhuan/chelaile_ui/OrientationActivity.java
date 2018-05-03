package com.example.shizhuan.chelaile_ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.example.shizhuan.chelaile_ui.Utils.Constants;
import com.example.shizhuan.chelaile_ui.Utils.KyLoadingBuilder;
import com.example.shizhuan.chelaile_ui.http.OkHttpClientManager;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ShiZhuan on 2018/4/24.
 */

public class OrientationActivity extends BaseActivity implements View.OnClickListener,AMapLocationListener,
        CloudSearch.OnCloudSearchListener{
    Map<String,Object> map1,map2;
    Map<String,Map<String,Object>> param = new HashMap<>();

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    private final int MSG_HELLO = 0;
    private final int MSG_WORLD = 1;
    private static Handler mHandler;

    private static int vHeight = -1;
    private static int reHeight = -1;
    private static int position = 0;
    private static final int target = 10;
    private static boolean isMove = false;
    private boolean isClick = false;

    private ImageButton home,map,direction,notice;
    private Toolbar toolbar;
    private Intent intent;
    private List<Station> currentline = new ArrayList<>();

    private AppCompatSpinner spinner;

    private RecyclerView recyclerView;
    private TextView time,distance;
    private MyRecyclerAdapter adapter;
    private LinearLayoutManager mLayoutManager;

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    MyApplication application;

    private Station station;

    private CloudSearch mCloudSearch;

    private String mLocalCityName = "深圳市";

    private ArrayList<CloudItem> mCloudItems;
    private CloudSearch.Query mQuery;

    private KyLoadingBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        new CustomThread().start();

        mlocationClient = new AMapLocationClient(OrientationActivity.this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //检测系统是否打开开启了地理定位权限
        if (ContextCompat.checkSelfPermission(OrientationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(OrientationActivity.this, new String []{android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms

        mLocationOption.setLocationCacheEnable(false);
        mLocationOption.setOnceLocation(true);

        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        application = (MyApplication)this.getApplication();
//        stationslist = application.getcurrentline();
        init();
    }

    private void init(){

        builder = new KyLoadingBuilder(this);
        builder.setIcon(R.mipmap.loading);
        builder.setText("正在加载中...");
//builder.setOutsideTouchable(false);//点击空白区域是否关闭
//builder.setBackTouchable(true);//按返回键是否关闭
//builder.dismiss();//关闭
//        builder.show();

        time = (TextView)findViewById(R.id.time);
        distance = (TextView)findViewById(R.id.distance);

        spinner = (AppCompatSpinner)findViewById(R.id.spinner);
        ArrayAdapter<String> spinneradapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, Constants.ToHomeLines);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner .setAdapter(spinneradapter);
        if (application.getLine_number()!=0){
            spinner.setSelection(application.getLine_number());
        }else {
            spinner.setSelection(0);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                application.setLine_number(position);
                currentline.clear();
                queryStations();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.line_station);
        home = (ImageButton)findViewById(R.id.home);
        map = (ImageButton)findViewById(R.id.map);
        direction = (ImageButton)findViewById(R.id.direction);
        notice = (ImageButton)findViewById(R.id.notice);
        home.setOnClickListener(this);
        map.setOnClickListener(this);
        direction.setOnClickListener(this);
        notice.setOnClickListener(this);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mLayoutManager = new LinearLayoutManager(this );
        recyclerView = (RecyclerView) findViewById(R.id.line_station);

        //设置布局管理器
        recyclerView.setLayoutManager(mLayoutManager);
        //设置为垂直布局，这也是默认的
        mLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new MyRecyclerAdapter(OrientationActivity.this,currentline);
        //设置Adapter
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int Position) {
                if ((adapter.getbusItem()<0&&Position<=Math.abs(adapter.getbusItem()))
                        || (adapter.getbusItem()>=0&&Position<adapter.getbusItem())){
                    Toast.makeText(OrientationActivity.this,"班车已路过该站",Toast.LENGTH_SHORT).show();
                }else {
                    adapter.setSelectItem(Position);
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    df1.format(date);//定位时间
                    map1 = new HashMap<>();
                    map2 = new HashMap<>();
                    map1.put("TRACDE","BC00001");
                    map1.put("TRADAT",df1.format(date));
                    map1.put("TRATIM",df2.format(date));
                    map1.put("USRNAM","zhou");
                    map2.put("line",application.getLine_number()*2+2);
                    map2.put("stanum",Position+1);
                    param.put("head",map1);
                    param.put("body",map2);
                    net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(param);
                    String tmp = jsonArray.toString().substring(1,jsonArray.toString().length()-1);
                    Log.d("Test", "MainThread is ready to send msg:" + tmp);
                    mHandler.obtainMessage(MSG_HELLO, tmp).sendToTarget();//发送消息到CustomThread实例

                    position = Position;
                    vHeight = view.getWidth();

                    Rect rect = new Rect();
                    recyclerView.getGlobalVisibleRect(rect);
                    reHeight = rect.right - rect.left - vHeight;

                    // handler.removeCallbacksAndMessages(null);
                    isClick = true;
                    if (vHeight < 0) {
                        isMove = false;
                        return;
                    }
                    scrollToMiddle();
                }

            }
        });
        //设置分隔线
        recyclerView.addItemDecoration(new DividerGridItemDecoration());
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    /**
     * 查询当前选择路线站点信息
     */
    private void queryStations(){
        mCloudSearch = new CloudSearch(this);// 初始化查询类
        mCloudSearch.setOnCloudSearchListener(this);// 设置回调函数
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(mLocalCityName);
        try {
            mQuery = new CloudSearch.Query(Constants.lineKeys[application.getLine_number()*2+1], "深圳", bound);
            mCloudSearch.searchCloudAsyn(mQuery);
        } catch (AMapException e) {
            Toast.makeText(OrientationActivity.this, e.getErrorMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:
                intent = new Intent(OrientationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.map:
                intent = new Intent(OrientationActivity.this, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.direction:

                break;
            case R.id.notice:
                intent = new Intent(OrientationActivity.this, NoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int rCode) {
    }

    @Override
    public void onCloudSearched(CloudResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(mQuery)) {
                    mCloudItems = result.getClouds();

                    if (mCloudItems != null && mCloudItems.size() > 0) {
                        for (CloudItem item : mCloudItems) {
                            station = new Station(Integer.parseInt(item.getID()),item.getTitle(),item.getLatLonPoint());
                            currentline.add(station);
                        }

                        Collections.sort(currentline, new Comparator<Station>(){
                            /*
                             * int compare(Person p1, Person p2) 返回一个基本类型的整型，
                             * 返回负数表示：p1 小于p2，
                             * 返回0 表示：p1和p2相等，
                             * 返回正数表示：p1大于p2
                             */
                            public int compare(Station p1, Station p2) {
                                //按照Person的年龄进行升序排列
                                if(p1.getNumber() > p2.getNumber()){
                                    return 1;
                                }
                                if(p1.getNumber() == p2.getNumber()){
                                    return 0;
                                }
                                return -1;
                            }
                        });
                        adapter.notifyDataSetChanged();
                        builder.dismiss();
                    } else {
                        Toast.makeText(this, R.string.no_result,Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(OrientationActivity.this, R.string.no_result,Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(OrientationActivity.this, rCode,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            StringBuffer sb = new StringBuffer();
            if (aMapLocation.getErrorCode() == 0) {
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                try {
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    map1 = new HashMap<>();
                    map2 = new HashMap<>();
                    map1.put("TRACDE","BC00006");
                    map1.put("TRADAT",df1.format(date));
                    map1.put("TRATIM",df2.format(date));
                    map1.put("USRNAM","zhou");
                    map2.put("line",application.getLine_number()*2+2);
                    map2.put("toc","1");
                    map2.put("longitude",aMapLocation.getLongitude());
                    map2.put("latitude",aMapLocation.getLatitude());
                    param.put("head",map1);
                    param.put("body",map2);
                    net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(param);
                    String tmp = jsonArray.toString().substring(1,jsonArray.toString().length()-1);
                    mHandler.obtainMessage(MSG_WORLD, tmp).sendToTarget();//发送消息到CustomThread实例
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(OrientationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                //定位失败
            }
        }
    }

    public void scrollToMiddle() {
        final int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        int top = recyclerView.getChildAt(position - firstPosition).getRight();
        int half = reHeight / 2;
        recyclerView.scrollBy(top - half, 0);
        isMove = false;

    }

    class CustomThread extends Thread {
        @Override
        public void run() {
            //建立消息循环的步骤
            Looper.prepare();//1、初始化Looper
            mHandler = new Handler(){//2、绑定handler到CustomThread实例的Looper对象
                public void handleMessage (Message msg) {//3、定义处理消息的方法
                    switch(msg.what) {
                        case MSG_HELLO:
                            try {
                                Response response = OkHttpClientManager.getAsyn(Constants.url_getLocation + msg.obj);
                                JSONObject allObject = new JSONObject(response.body().string());
                                JSONObject head = allObject.getJSONObject("head");
                                JSONObject body = allObject.getJSONObject("body");
                                String retcode = head.getString("RTNSTS");
                                double longitude = body.getDouble("bus_longitude3");
                                double latitude = body.getDouble("bus_latitude3");
                                final String laststation = body.getString("bus_laststa");//上一站
                                final String nextstation = body.getString("bus_nextsta");//下一站
                                final String time_value = body.getString("bus_nexttm");//到下一站的时间
                                final String distance_value = body.getString("bus_nextdis");//到下一站的距离
                                final String stadis = body.getString("stadis");//到所选站点的距离
                                final String statime = body.getString("statime");//到所选站点的时间
                                if (Integer.parseInt(laststation)==Integer.parseInt(nextstation)){
                                    adapter.setbusItem(Integer.parseInt(laststation)-1);
                                }else {
                                    adapter.setbusItem(1-Integer.parseInt(laststation));
                                }

                                LatLng newLatLng = new LatLng(latitude, longitude);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        time.setText(Integer.parseInt(statime)/60+"");
                                        distance.setText(stadis);
                                        adapter.notifyDataSetChanged();
                                        builder.dismiss();
                                    }
                                });
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case MSG_WORLD:
                            try {
                                Response response = OkHttpClientManager.getAsyn(Constants.url_querystation + msg.obj);
                                JSONObject allObject = new JSONObject(response.body().string());
                                JSONObject head = allObject.getJSONObject("head");
                                JSONObject line = allObject.getJSONObject("line");
                                String line_staname = line.getString("line_staname");//最近站点名称
                                final String line_stanum = line.getString("line_stanum");//最近站点编号

                                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
                                Date date = new Date();
                                df1.format(date);//定位时间
                                map1 = new HashMap<>();
                                map2 = new HashMap<>();
                                map1.put("TRACDE","BC00001");
                                map1.put("TRADAT",df1.format(date));
                                map1.put("TRATIM",df2.format(date));
                                map1.put("USRNAM","zhou");
                                map2.put("line",application.getLine_number()*2+2);
                                map2.put("stanum",line_stanum);
                                param.put("head",map1);
                                param.put("body",map2);
                                net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(param);
                                String tmp = jsonArray.toString().substring(1,jsonArray.toString().length()-1);
                                Response response1 = OkHttpClientManager.getAsyn(Constants.url_getLocation + tmp);
                                JSONObject allObject1 = new JSONObject(response1.body().string());
                                JSONObject head1 = allObject1.getJSONObject("head");
                                JSONObject body = allObject1.getJSONObject("body");
                                String retcode = head1.getString("RTNSTS");
                                double longitude = body.getDouble("bus_longitude3");
                                double latitude = body.getDouble("bus_latitude3");
                                final String laststation = body.getString("bus_laststa");//上一站
                                final String nextstation = body.getString("bus_nextsta");//下一站
                                final String time_value = body.getString("bus_nexttm");//到下一站的时间
                                final String distance_value = body.getString("bus_nextdis");//到下一站的距离
                                final String stadis = body.getString("stadis");//到所选站点的距离
                                final String statime = body.getString("statime");//到所选站点的时间
                                if (Integer.parseInt(laststation)==Integer.parseInt(nextstation)){
                                    adapter.setbusItem(Integer.parseInt(laststation)-1);
                                }else {
                                    adapter.setbusItem(1-Integer.parseInt(laststation));
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        time.setText(Integer.parseInt(statime)/60+"");
                                        distance.setText(stadis);
                                        adapter.setSelectItem(Integer.parseInt(line_stanum)-1);
                                        adapter.notifyDataSetChanged();
                                        builder.dismiss();
                                    }
                                });
//
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            };
            Looper.loop();//4、启动消息循环
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(OrientationActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                removeALLActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
