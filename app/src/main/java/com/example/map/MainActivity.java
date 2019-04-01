package com.example.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "MainActivity";
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private RoutePlanSearch routePlanSearch = null;
    private Button mFindMeButton;
    private EditText mSearch;
    private CardView mSearchLayout;
    private CardView mFindMeCardView;
    private LocationClient mLocationClient;
    private RelativeLayout mMainRelativeLayout;
    private TransitionSet mSet;
    private double mCurrentX;
    private double mCurrentY;
    private boolean isFirstLoad = true;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(this);

        setContentView(R.layout.activity_main);

        //请求权限
        requestPower();

        init();



    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        routePlanSearch.destroy();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }
            mCurrentX = location.getLatitude();
            mCurrentY = location.getLongitude();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation)
                navigateTo(location);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "纬度: " + location.getLatitude());
                    Log.d(TAG, "经线: " + location.getLongitude());
                    Log.d(TAG, "国家: " + location.getCountry());
                    Log.d(TAG, "省: " + location.getProvince());
                    Log.d(TAG, "市: " + location.getCity());
                    Log.d(TAG, "区: " + location.getDistrict());
                    Log.d(TAG, "街道: " + location.getStreet());
                    Log.d(TAG, "定位方式: " + location.getLocType());
                }
            }).start();
        }
    }

    private void init(){
        mFindMeButton = findViewById(R.id.find_me_btn);
        mSearch = findViewById(R.id.edittxt_search);
        mSearchLayout = findViewById(R.id.ll_search);
        mMainRelativeLayout = findViewById(R.id.main_rl);
        mFindMeCardView = findViewById(R.id.find_me_cardview);

        //获取地图控件引用
        mMapView =  findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMapView.setZoomControlsPosition(new Point(mFindMeCardView.getLeft(),mFindMeCardView.getTop() - 350)); //设置缩放控件位置
            }
        });
        //定位初始化
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true); //设置需要地址信息
        option.setScanSpan(1000);
//设置locationClientOption
        mLocationClient.setLocOption(option);


//注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        mFindMeButton.setOnClickListener(this);

        mSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    expand();
                }else {
                    reduce();
                }
            }
        });

        mSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearch.setFocusable(true);
                mSearch.setFocusableInTouchMode(true);
                mSearch.requestFocus();
                return false;
            }
        });


        //创建驾车线路规划检索实例；
        routePlanSearch = RoutePlanSearch.newInstance();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_me_btn:
                findMe();
                break;
        }
    }

    private void navigateTo(BDLocation location){
        if (isFirstLoad){
            findMe();
            isFirstLoad = false;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        mBaiduMap.setMyLocationData(locationData);
    }

    private void findMe(){
        //创建驾车线路规划检索监听者；
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                //获取驾车线路规划结果
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                Toast.makeText(MainActivity.this,"开始",Toast.LENGTH_SHORT).show();
                if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.d(TAG, "onGetWalkingRouteResult: " + walkingRouteResult.error);
                    Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    Toast.makeText(MainActivity.this,"起终点或途经点地址有岐义",Toast.LENGTH_SHORT).show();
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    Log.d(TAG, "onGetWalkingRouteResult: " + walkingRouteResult);
                    return;
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(MainActivity.this,"导航成功",Toast.LENGTH_SHORT).show();
//                                    nodeIndex = -1;
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
//                    route = result.getRouteLines().get(0);
//                    WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
//                    mBaidumap.setOnMarkerClickListener(overlay);
//                    routeOverlay = overlay;
//                    overlay.setData(result.getRouteLines().get(0));
//                    overlay.addToMap();
//                    overlay.zoomToSpan();


                }

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };
        //设置驾车线路规划检索监听者，该方法要先于检索方法drivingSearch(DrivingRoutePlanOption)前调用，否则会在某些场景出现拿不到回调结果的情况


        //准备检索起、终点信息；
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("福州","福州大学学生公寓32");
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("福州", "37栋西2教学楼");
        //发起驾车线路规划检索；
        routePlanSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(stNode)
             //   .to(PlanNode.withLocation(new LatLng(119.201366,26.064479))));
                .to(enNode));

        routePlanSearch.setOnGetRoutePlanResultListener(listener);



        LatLng ll = new LatLng(mCurrentX,mCurrentY);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(19f);
        mBaiduMap.animateMapStatus(update);

    }

    public void requestPower(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,
                       permissions, 1);
        }else {
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        requestLocation();
                    }
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void requestLocation(){
        //开启地图定位图层
        mLocationClient.start();
    }


    private void expand() {
        //设置伸展状态时的布局
     //   mSearch.setText("搜索简书的内容和朋友");
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchLayout.getLayoutParams();
        LayoutParams.width = LayoutParams.MATCH_PARENT;
        LayoutParams.setMargins(dip2px(10), dip2px(10), dip2px(10), dip2px(10));
        mSearchLayout.setLayoutParams(LayoutParams);
        //设置动画
        beginDelayedTransition(mSearchLayout);
    }

    private void reduce() {
        //设置收缩状态时的布局
        //mSearch.setText("搜索");
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchLayout.getLayoutParams();
        LayoutParams.width = dip2px(80);
        LayoutParams.setMargins(dip2px(10), dip2px(10), dip2px(10), dip2px(10));
        mSearchLayout.setLayoutParams(LayoutParams);
        //设置动画
        beginDelayedTransition(mSearchLayout);
    }

    void beginDelayedTransition(ViewGroup view) {
        mSet = new AutoTransition();
        //设置动画持续时间
        mSet.setDuration(300);
        // 开始表演
        TransitionManager.beginDelayedTransition(view, mSet);
    }

    private int dip2px(float dpVale) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v =  getCurrentFocus();
            Log.d(TAG, "v: " + v);
            if (isShouldHideInput(v, ev)) {
                mSearch.setFocusable(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            else if ((v instanceof EditText)){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    v.requestFocus();
                    imm.showSoftInput(v, 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            ((ViewGroup)v.getParent()).getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];

            int bottom = ((ViewGroup)v.getParent()).getHeight() + top;
            int right = ((ViewGroup)v.getParent()).getWidth() + left;
            Log.d("touchEventP", "left: " +left );
            Log.d("touchEventP", "top: " + top);
            Log.d("touchEventP", "bottom: " + bottom);
            Log.d("touchEventP", "right: " + right);
            Log.d("touchEventP", "event.getX(): " + event.getX());
            Log.d("touchEventP", "event.getY(): " + event.getY());

            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


}
