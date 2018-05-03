package com.example.shizhuan.chelaile_ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.example.shizhuan.chelaile_ui.Utils.Constants;

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

public class MapActivity extends BaseActivity implements View.OnClickListener,AMapLocationListener,CloudSearch.OnCloudSearchListener,
        AMapNaviListener, AMapNaviViewListener {

    //记录用户首次点击返回键的时间
    private long firstTime = 0;


    private ImageButton home,map,direction,notice,work,tohome;
    private Toolbar toolbar;
    private Intent intent;



    private AMap aMap;
    private MarkerOptions markerOption;
    CameraUpdate cameraUpdate;

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    MyLocationStyle myLocationStyle;

    /**
     * 地图对象
     */
    private Marker mStartMarker;
    private Marker mEndMarker;
    private NaviLatLng sLatLng;//起点
    private NaviLatLng eLatLng; //终点
    private List<Station> currentline = new ArrayList<>();
    NaviLatLng naviLatLng;
    MyApplication application;

    private CloudSearch mCloudSearch;

    private String mLocalCityName = "深圳市";
    private ArrayList<CloudItem> mCloudItems;
    private CloudSearch.Query mQuery;

    protected Station station;
    protected MapView mapView;
    protected AMapNavi mAMapNavi;
    //    protected NaviLatLng mEndLatlng = new NaviLatLng(40.084894,116.603039);
//    protected NaviLatLng mStartLatlng = new NaviLatLng(39.825934,116.342972);
    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();
    protected List<NaviLatLng> mWayPointList = new ArrayList<>();

    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();

    /**
     * 路线计算成功标志位
     */
    private boolean calculateSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        application = (MyApplication)this.getApplication();
        init();

        queryStations(Constants.lineKeys[application.getLine_number()*2]);

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        MapActivity.this.setTitle("查看地图");
        mapView.onCreate(savedInstanceState);
//        startLocation();

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
        work = (ImageButton)findViewById(R.id.ToWork);
        work.setVisibility(View.GONE);
        work.setOnClickListener(this);
        tohome = (ImageButton)findViewById(R.id.ToHome);
        tohome.setVisibility(View.VISIBLE);
        tohome.setOnClickListener(this);

        mapView = (com.amap.api.maps.MapView) findViewById(R.id.mapdetail);
        aMap = mapView.getMap();
        mStartMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.start))));
        mEndMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.end))));

    }

    /**
     * 查询当前选择路线站点信息
     */
    private void queryStations(String keys){
        mCloudSearch = new CloudSearch(this);// 初始化查询类
        mCloudSearch.setOnCloudSearchListener(this);// 设置回调函数
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(mLocalCityName);
        try {
            mQuery = new CloudSearch.Query(keys, "深圳", bound);
            mCloudSearch.searchCloudAsyn(mQuery);
        } catch (AMapException e) {
            Toast.makeText(MapActivity.this, e.getErrorMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:
                finish();
                intent = new Intent(MapActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.map:

                break;
            case R.id.direction:
                finish();
                intent = new Intent(MapActivity.this, OrientationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.notice:
                finish();
                intent = new Intent(MapActivity.this, NoticeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.ToWork:
                tohome.setVisibility(View.VISIBLE);
                work.setVisibility(View.GONE);
                queryStations(Constants.lineKeys[application.getLine_number()*2]);
                break;
            case R.id.ToHome:
                tohome.setVisibility(View.GONE);
                work.setVisibility(View.VISIBLE);
                queryStations(Constants.lineKeys[application.getLine_number()*2+1]);
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

                    sList.clear();
                    eList.clear();
                    mWayPointList.clear();
                    currentline.clear();
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
                        int len = currentline.size();
                        sLatLng = new NaviLatLng(currentline.get(0).getLatLonPoint().getLatitude(),currentline.get(0).getLatLonPoint().getLongitude());
                        eLatLng = new NaviLatLng(currentline.get(len-1).getLatLonPoint().getLatitude(),currentline.get(len-1).getLatLonPoint().getLongitude());
                        sList.add(sLatLng);
                        eList.add(eLatLng);
                        mWayPointList = new ArrayList<>();
                        for(int i=1;i<len-1;i++){
                            naviLatLng = new NaviLatLng(currentline.get(i).getLatLonPoint().getLatitude(),currentline.get(i).getLatLonPoint().getLongitude());
                            mWayPointList.add(naviLatLng);
                        }
                        int strategy = 0;
                        try {
                            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
                            strategy = mAMapNavi.strategyConvert(true, true, true, false, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
                    } else {
                        Toast.makeText(this, R.string.no_result,Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(MapActivity.this, R.string.no_result,Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MapActivity.this, rCode,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息
                amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                amapLocation.getFloor();//获取当前室内定位的楼层
                amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Toast.makeText(MapActivity.this,"AmapError"+amapLocation.getErrorCode()+amapLocation.getErrorInfo(),Toast.LENGTH_LONG).show();
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }



    @Override
    public void onInitNaviSuccess() {
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, true, true, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);

    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        //清空上次计算的路径列表。
        routeOverlays.clear();
        aMap.clear();
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < ids.length; i++) {
            AMapNaviPath path = paths.get(ids[i]);
            if (path != null) {
                drawRoutes(ids[i], path);
            }
        }
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        calculateSuccess = false;
        Toast.makeText(getApplicationContext(), "计算路线失败，errorcode＝" + arg0, Toast.LENGTH_SHORT).show();
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        aMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(true);
        routeOverLay.addToMap();
        routeOverLay.zoomToSpan();
        routeOverlays.put(routeId, routeOverLay);
    }


    /**
     *
     * 返回键监听
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MapActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                removeALLActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
    }

    @Override
    public void onInitNaviFailure() {
        Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartNavi(int type) {
        //开始导航回调
    }

    @Override
    public void onTrafficStatusUpdate() {
        //
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        //当前位置回调
    }

    @Override
    public void onGetNavigationText(int type, String text) {
        //播报类型和播报文字回调
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {
        //结束模拟导航
    }

    @Override
    public void onArriveDestination() {
        //到达目的地
    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //拥堵后重新计算路线回调
    }

    @Override
    public void onArrivedWayPoint(int wayID) {
        //到达途径点
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
        //GPS开关状态回调
    }

    @Override
    public void onNaviSetting() {
        //底部导航设置点击回调
    }

    @Override
    public void onNaviMapMode(int isLock) {
        //地图的模式，锁屏或锁车
    }

    @Override
    public void onNaviCancel() {
        finish();
    }


    @Override
    public void onNaviTurnClick() {
        //转弯view的点击回调
    }

    @Override
    public void onNextRoadClick() {
        //下一个道路View点击回调
    }


    @Override
    public void onScanViewButtonClick() {
        //全览按钮点击回调
    }

    @Deprecated
    @Override
    public void onNaviInfoUpdated(AMapNaviInfo naviInfo) {
        //过时
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        //导航过程中的信息更新，请看NaviInfo的具体说明
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
        //已过时
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        //已过时
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        //显示转弯回调
    }

    @Override
    public void hideCross() {
        //隐藏转弯回调
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
        //显示车道信息

    }

    @Override
    public void hideLaneInfo() {
        //隐藏车道信息
    }


    @Override
    public void notifyParallelRoad(int i) {
        if (i == 0) {
            Toast.makeText(this, "当前在主辅路过渡", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在主辅路过渡");
            return;
        }
        if (i == 1) {
            Toast.makeText(this, "当前在主路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在主路");
            return;
        }
        if (i == 2) {
            Toast.makeText(this, "当前在辅路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在辅路");
        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        //更新交通设施信息
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        //更新巡航模式的统计信息
    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        //更新巡航模式的拥堵信息
    }

    @Override
    public void onPlayRing(int i) {

    }


    @Override
    public void onLockMap(boolean isLock) {
        //锁地图状态发生变化时回调
    }

    @Override
    public void onNaviViewLoaded() {
        Log.d("wlx", "导航页面加载成功");
        Log.d("wlx", "请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }



}
