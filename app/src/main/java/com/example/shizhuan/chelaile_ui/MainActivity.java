package com.example.shizhuan.chelaile_ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.model.Marker;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.example.shizhuan.chelaile_ui.entity.CloudOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,
        CloudSearch.OnCloudSearchListener{

    private CloudSearch mCloudSearch;
    private ImageView tothere;

    private Marker mCloudIDMarer;
    private String TAG = "AMapYunTuDemo";
    private String mLocalCityName = "深圳市";
    private ArrayList<CloudItem> items = new ArrayList<CloudItem>();

    private ArrayList<CloudItem> mCloudItems;
    private List<CloudOverlay> overlays;
    private String mTableID = "5a49c02f2376c17f01cddda9";
    private String mId = "2"; // 用户table 行编号
    private String mKeyWord; // 搜索关键字
    private CloudSearch.Query mQuery;
    private String bus_line;

    private List<Station> currentline = new ArrayList<>();
    private CloudOverlay mPoiCloudOverlay;

    private ImageButton home,map,direction,notice;
    private Toolbar toolbar;
    private Intent intent;
    private HorizontialListView listView;
    private LineAdapter adapter;
    private Station station;
    List<Station> stations = new ArrayList<>();
    String[] stationlist = {"坪洲地铁站","西乡步行街","同仁医院","群贤花园","宝安体育馆","金城名苑","中南花园","甲岸路口","西城上筑","南头","公司"};

    private Boolean isReverse = false;
    MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (MyApplication)this.getApplication();
        initStations();
        init();
        bus_line = application.getline();
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(mLocalCityName);
        try {
            mQuery = new CloudSearch.Query(mTableID, bus_line, bound);
            mCloudSearch.searchCloudAsyn(mQuery);
        } catch (AMapException e) {
            Toast.makeText(MainActivity.this, e.getErrorMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

        listView = (HorizontialListView)findViewById(R.id.line_station);
        listView.setOnItemClickListener(this);
        adapter = new LineAdapter(MainActivity.this,stations);
        adapter.setSelectItem(1);
        listView.setAdapter(adapter);

        mCloudSearch = new CloudSearch(this);// 初始化查询类
        mCloudSearch.setOnCloudSearchListener(this);// 设置回调函数
    }

    private void initStations(){
        for(int i=0;i<stationlist.length;i++){
            Station station = new Station(stationlist[i]);
            stations.add(station);
        }
        application.setlist(stations);
        application.setline("宝安");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:

                break;
            case R.id.map:
                intent = new Intent(MainActivity.this, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.direction:
//                Collections.reverse(stations);
//                adapter.notifyDataSetChanged();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("stations",(Serializable) stations);
                intent = new Intent(MainActivity.this, OrientationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.notice:
                intent = new Intent(MainActivity.this, NoticeActivity.class);
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
//                            items.add(item);
//                            Log.i(TAG, "_id " + item.getID());
//                            Log.i(TAG, "_location "
//                                    + item.getLatLonPoint().toString());
//                            Log.i(TAG, "_name " + item.getTitle());
//                            Log.i(TAG, "_address " + item.getSnippet());
//                            Toast.makeText(MapActivity.this,"_address1" + item.getSnippet(),Toast.LENGTH_SHORT).show();
//                            Log.i(TAG, "_caretetime " + item.getCreatetime());
//                            Log.i(TAG, "_updatetime " + item.getUpdatetime());
//                            Log.i(TAG, "_distance " + item.getDistance());
//                            Iterator iter = item.getCustomfield().entrySet()
//                                    .iterator();
//                            while (iter.hasNext()) {
//                                Map.Entry entry = (Map.Entry) iter.next();
//                                Object key = entry.getKey();
//                                Object val = entry.getValue();
//                                Log.i(TAG, key + "   " + val);
//                            }
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
                        application.setcurrentline(currentline);



//
//                        //获取AMapNavi实例
//                        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
//                        //添加监听回调，用于处理算路成功
//                        mAMapNavi.addAMapNaviListener(this);
//                        mAMapNaviView.setAMapNaviViewListener(this);
                    } else {
                        Toast.makeText(this, R.string.no_result,Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.no_result,Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, rCode,Toast.LENGTH_SHORT).show();
        }
    }
}
