package com.example.shizhuan.chelaile_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by ShiZhuan on 2018/4/24.
 */

public class NoticeActivity extends BaseActivity implements View.OnClickListener{
    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    private ImageButton home,map,direction,notice;
    private Toolbar toolbar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        init();
    }

    private void init(){
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:
                intent = new Intent(NoticeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.map:
                intent = new Intent(NoticeActivity.this, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.direction:
                intent = new Intent(NoticeActivity.this, OrientationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.notice:

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(NoticeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                removeALLActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
