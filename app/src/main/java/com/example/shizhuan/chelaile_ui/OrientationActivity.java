package com.example.shizhuan.chelaile_ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ShiZhuan on 2018/4/24.
 */

public class OrientationActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    private ImageButton home,map,direction,notice;
    private Toolbar toolbar;
    private Intent intent;
    List<Station> stationslist = new ArrayList<>();

    private HorizontialListView listView;
    private LineAdapter adapter;

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
        stationslist = application.getlist();
        init();
    }

    private void init(){
        listView = (HorizontialListView)findViewById(R.id.line_station);
        home = (ImageButton)findViewById(R.id.home);
        map = (ImageButton)findViewById(R.id.map);
        direction = (ImageButton)findViewById(R.id.direction);
        notice = (ImageButton)findViewById(R.id.notice);
        listView.setOnItemClickListener(this);
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

        adapter = new LineAdapter(OrientationActivity.this,stationslist);
        listView.setAdapter(adapter);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setSelectItem(position);
        adapter.notifyDataSetInvalidated();
        listView.scrollTo(position);
    }
}
