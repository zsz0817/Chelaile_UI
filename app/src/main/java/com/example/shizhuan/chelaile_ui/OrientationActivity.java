package com.example.shizhuan.chelaile_ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.example.shizhuan.chelaile_ui.Utils.Constants;
import com.example.shizhuan.chelaile_ui.http.OkHttpClientManager;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ShiZhuan on 2018/4/24.
 */

public class OrientationActivity extends AppCompatActivity implements View.OnClickListener {
    Map<String,Object> map1,map2;
    Map<String,Map<String,Object>> param = new HashMap<>();

    private final int MSG_HELLO = 0;
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
    List<Station> stationslist = new ArrayList<>();

    private RecyclerView recyclerView;
    private TextView time,distance;
    private MyRecyclerAdapter adapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        //取得从上一个Activity当中传递过来的Intent对象
//        Intent intent = getIntent();
//        //从Intent当中根据key取得value
//        if (intent != null) {
//            stationslist = (List<Station>) intent.getSerializableExtra("stations");
//        }
        MyApplication application = (MyApplication)this.getApplication();
        stationslist = application.getcurrentline();
        init();
    }

    private void init(){
        time = (TextView)findViewById(R.id.time);
        distance = (TextView)findViewById(R.id.distance);

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

        if (!stationslist.get(0).getContent().equals("公司")){
            Collections.reverse(stationslist);
        }

        mLayoutManager = new LinearLayoutManager(this );
        recyclerView = (RecyclerView) findViewById(R.id.line_station);

        //设置布局管理器
        recyclerView.setLayoutManager(mLayoutManager);
        //设置为垂直布局，这也是默认的
        mLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new MyRecyclerAdapter(OrientationActivity.this,stationslist);
        //设置Adapter
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int Position) {
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
                map2.put("line","1");
                param.put("head",map1);
                param.put("body",map2);
                new CustomThread().start();//新建并启动CustomThread实例
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
        });
        //设置分隔线
        recyclerView.addItemDecoration(new DividerGridItemDecoration());
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                                String laststation = body.getString("bus_laststa");//上一站
                                String nextstation = body.getString("bus_nextsta");//下一站
                                final String time_value = body.getString("bus_nexttm");//到下一站的时间
                                final String distance_value = body.getString("bus_nextdis");//到下一站的距离
                                LatLng newLatLng = new LatLng(latitude, longitude);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        time.setText(Integer.parseInt(time_value)/60+"");
                                        distance.setText(distance_value);
                                    }
                                });
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            };
            Looper.loop();//4、启动消息循环
        }
    }
}
