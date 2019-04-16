package com.example.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.map.adapter.DaohangAdapter;
import com.example.map.bean.PositionData;
import com.example.map.overlayutil.OverlayManager;
import com.example.map.overlayutil.WalkingRouteOverlay;
import com.example.map.utils.MyMapUtil;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "MainActivity";
    private long exitTime=0;
    MyLocationListener myLocationListener;
    private PopupWindow pop;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    private RoutePlanSearch routePlanSearch = null;
    private float mZoomScale = 19f;
    private LatLng mDestinationPoint;
    public static View mCurrentFcous;
    private Button mFindMeButton;
    private EditText mSearch;
    private CardView mSearchLayout;
    private CardView mFindMeCardView;
    private LocationClient mLocationClient;
    private RelativeLayout mMainRelativeLayout;
    private TransitionSet mSet;
    private RouteLine route = null;
    OverlayManager routeOverlay = null;
    MyLocationData locData;
    private BoomMenuButton bmb;
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
        mLocationClient.unRegisterLocationListener(myLocationListener);
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
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, null));  //显示方向
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation)
                navigateTo(location);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "纬度: " + location.getLatitude());
                    Log.d(TAG, "经线: " + location.getLongitude());
                    Log.d(TAG, "方向: " + location.getDirection());
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
        bmb = findViewById(R.id.bmb);
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
        myLocationListener = new MyLocationListener();
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

//        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
//            TextOutsideCircleButton.Builder builder = new TextOutsideCircleButton.Builder()
//                    .normalImageRes(R.drawable.ic_launcher_background)
//                    .normalText("Butter Doesn't fly!");
//            bmb.addBuilder(builder);
//        }


        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.fad_daohang)
                .normalText("路线规划")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        showDaohangPop();
                    }
                });
        bmb.addBuilder(builder1);
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.fad_study)
                .normalText("图书馆打卡") .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        new Thread(run1).start();
                    }
                });
        bmb.addBuilder(builder2);
        TextOutsideCircleButton.Builder builder3 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.fad_eat)
                .normalText("食堂打卡") .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        new Thread(run2).start();
                    }
                });
        bmb.addBuilder(builder3);
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
        builder.direction(location.getDirection());
        MyLocationData locationData = builder.build();
        mBaiduMap.setMyLocationData(locationData);
    }

    private void findMe(){
        LatLng ll = new LatLng(mCurrentX,mCurrentY);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(mZoomScale);
        mBaiduMap.animateMapStatus(update);

    }


    private void luxianguihua(LatLng start,LatLng end){
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
                mBaiduMap.clear();
                Toast.makeText(MainActivity.this,"麋鹿正在为您寻找路线哦~~",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this,"有麋鹿，不迷路~~~",Toast.LENGTH_SHORT).show();
//                                    nodeIndex = -1;
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
                    route = walkingRouteResult.getRouteLines().get(0);
                    WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    routeOverlay = overlay;
                    overlay.setData(walkingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
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
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("福州","福州大学学生公寓32");
       // PlanNode enNode = PlanNode.withCityNameAndPlaceName("福州", "37栋西2教学楼");
        //发起驾车线路规划检索；
        routePlanSearch.setOnGetRoutePlanResultListener(listener);
        routePlanSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(PlanNode.withLocation(start))
                .to(PlanNode.withLocation(end)));

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


    private void showDaohangPop(){
        View popView = View.inflate(MainActivity.this,R.layout.layout_daohang_pop,null);
        final EditText startEdit = popView.findViewById(R.id.edit_text_start);
        final EditText endEdit = popView.findViewById(R.id.edit_text_end);
        ImageButton daohangButton = popView.findViewById(R.id.daohang_button);
        mCurrentFcous = startEdit;
        startEdit.setText("我的位置");
        daohangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startEdit.getText().toString().equals("") || endEdit.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"还未填入起点或重点哦~~",Toast.LENGTH_SHORT).show();
                    return;
                }else if (startEdit.getText().toString().equals(endEdit.getText().toString())){
                    Toast.makeText(MainActivity.this,"起点和终点不能一样哦~~",Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLng start = MyMapUtil.changeTextToLatng(startEdit.getText().toString(),mCurrentX,mCurrentY);
                LatLng end = MyMapUtil.changeTextToLatng(endEdit.getText().toString(),mCurrentX,mCurrentY);
                luxianguihua(start,end);
                closePopupWindow();
            }
        });
        startEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentFcous = v;
                startEdit.setBackground(getDrawable(R.drawable.seleted_bg));
                endEdit.setBackground(getDrawable(R.drawable.unseleted_bg));
            }
        });
        endEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentFcous = v;
                startEdit.setBackground(getDrawable(R.drawable.unseleted_bg));
                endEdit.setBackground(getDrawable(R.drawable.seleted_bg));
            }
        });
        RecyclerView recyclerView1 = popView.findViewById(R.id.pop_rl_1);
        RecyclerView recyclerView2 = popView.findViewById(R.id.pop_rl_2);
        RecyclerView recyclerView3 = popView.findViewById(R.id.pop_rl_3);
        RecyclerView recyclerView4 = popView.findViewById(R.id.pop_rl_4);
        DaohangAdapter daohangAdapter1 = new DaohangAdapter(this, new ArrayList<String>(PositionData.teachingBuilding.keySet()));
        DaohangAdapter daohangAdapter2 = new DaohangAdapter(this, new ArrayList<String>(PositionData.canteen.keySet()));
        DaohangAdapter daohangAdapter3 = new DaohangAdapter(this, new ArrayList<String>(PositionData.scenicSpot.keySet()));
        DaohangAdapter daohangAdapter4 = new DaohangAdapter(this, new ArrayList<String>(PositionData.others.keySet()));
        recyclerView1.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView1.setAdapter(daohangAdapter1);
        recyclerView2.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView2.setAdapter(daohangAdapter2);
        recyclerView3.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView3.setAdapter(daohangAdapter3);
        recyclerView4.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView4.setAdapter(daohangAdapter4);

        pop = new PopupWindow(popView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
   //     pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
//                    case R.id.tv_album:
//
//                        break;
//                    case R.id.tv_camera:
//
//                    case R.id.tv_cancel:
//                        //取消
//                        break;
                }
                closePopupWindow();
            }
        };
    }
    private void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
            mCurrentFcous = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //两次点击返回按钮退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis() - exitTime)>2000){
                Toast.makeText(getApplicationContext(),"再按一次退出应用",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else {
                finish();
            //    ActivityCollector.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setTextOption(LatLng point, String str, String color) {
        //使用MakerInfoWindow
        if (point == null) return;
        TextView view = new TextView(getApplicationContext());
        view.setBackgroundResource(R.mipmap.map_textbg);
        view.setPadding(0, 23, 0, 0);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextSize(14);
        view.setGravity(Gravity.CENTER);
        view.setText(str);
        view.setTextColor(Color.parseColor(color));
        mInfoWindow = new InfoWindow(view, point, 170);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    private void setMarkerOptions(LatLng ll, int icon) {
        if (ll == null) return;
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(icon);
        MarkerOptions ooD = new MarkerOptions().position(ll).icon(bitmap);
        mBaiduMap.addOverlay(ooD);
    }


    private void setMapZoomScale(LatLng ll) {
        if (mDestinationPoint == null) {//打卡坐标不为空
            //mZoomScale = getZoomScale(ll);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(ll, mZoomScale));//缩放
        } else {
         //   mZoomScale = getZoomScale(ll);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(mCurrentX,mCurrentY), mZoomScale));//缩放
        }
    }

    private Runnable run1 = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
       //     Date date = new Date(System.currentTimeMillis());//获取当前时间
       //     mTime_tv.setText(simpleDateFormat.format(date)); //更新时间
            mHandler.sendEmptyMessage(0);
        }
    };

    private Runnable run2 = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
            //     Date date = new Date(System.currentTimeMillis());//获取当前时间
            //     mTime_tv.setText(simpleDateFormat.format(date)); //更新时间
            mHandler2.sendEmptyMessage(0);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        //   BDLocation location = (BDLocation) msg.obj;
            mBaiduMap.clear();
            OverlayOptions ooCircle = new CircleOptions().fillColor(0x4057FFF8)
                    .center(PositionData.teachingBuilding.get("图书馆")).stroke(new Stroke(1, 0xB6FFFFFF)).radius(100);
            mBaiduMap.addOverlay(ooCircle);
            LatLng LocationPoint = new LatLng(mCurrentX, mCurrentY);
            //打卡范围
            mDestinationPoint = PositionData.teachingBuilding.get("图书馆");//假设公司坐标
     //       setCircleOptions();
            //计算两点距离,单位：米
            double mDistance = DistanceUtil.getDistance(PositionData.teachingBuilding.get("图书馆"), LocationPoint);
            if (mDistance <= 100) {
                //显示文字
                setTextOption(mDestinationPoint, "开始学习叭~~", "#7ED321");
                //目的地图标
                setMarkerOptions(mDestinationPoint, R.mipmap.arrive_icon);
                //按钮颜色
                //commit_bt.setBackgroundDrawable(getResources().getDrawable(R.mipmap.restaurant_btbg_yellow));
             //   mBaiduMap.setMyLocationEnabled(false);
            } else {
                setTextOption(LocationPoint, "您不在图书馆范围之内", "#FF6C6C");
                setMarkerOptions(mDestinationPoint, R.mipmap.library_icon);
                //commit_bt.setBackgroundDrawable(getResources().getDrawable(R.mipmap.restaurant_btbg_gray));
            //    mBaiduMap.setMyLocationEnabled(true);
            }
            // mDistance_tv.setText("距离目的地：" + mDistance + "米");
            //缩放地图
            setMapZoomScale(LocationPoint);

            mHandler3.removeMessages(0);
            mHandler3.sendEmptyMessageDelayed(0,3000);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            OverlayOptions ooCircle1 = new CircleOptions().fillColor(0x4057FFF8)
                    .center(PositionData.canteen.get("紫荆园")).stroke(new Stroke(1, 0xB6FFFFFF)).radius(50);
            mBaiduMap.addOverlay(ooCircle1);
            OverlayOptions ooCircle2 = new CircleOptions().fillColor(0x4057FFF8)
                    .center(PositionData.canteen.get("玫瑰园")).stroke(new Stroke(1, 0xB6FFFFFF)).radius(50);
            mBaiduMap.addOverlay(ooCircle2);
            OverlayOptions ooCircle3 = new CircleOptions().fillColor(0x4057FFF8)
                    .center(PositionData.canteen.get("京元")).stroke(new Stroke(1, 0xB6FFFFFF)).radius(50);
            mBaiduMap.addOverlay(ooCircle3);
            OverlayOptions ooCircle4 = new CircleOptions().fillColor(0x4057FFF8)
                    .center(PositionData.canteen.get("丁香园")).stroke(new Stroke(1, 0xB6FFFFFF)).radius(50);
            mBaiduMap.addOverlay(ooCircle4);

            LatLng LocationPoint = new LatLng(mCurrentX, mCurrentY);
            //打卡范围
            LatLng[] mDestinationPoints = {PositionData.canteen.get("紫荆园"),
                                            PositionData.canteen.get("玫瑰园"),
                                            PositionData.canteen.get("京元"),
                                            PositionData.canteen.get("丁香园")};
            double mMinDistance =  DistanceUtil.getDistance(PositionData.canteen.get("紫荆园"), LocationPoint);
            mDestinationPoint = PositionData.canteen.get("紫荆园");
            for (int i = 1 ; i < mDestinationPoints.length;i++){
                double mDistance = DistanceUtil.getDistance(mDestinationPoints[i], LocationPoint);
                if (mMinDistance > mDistance){
                    mDestinationPoint = mDestinationPoints[i];
                }
            }
            //       setCircleOptions();
            //计算两点距离,单位：米


            if (mMinDistance <= 50) {
                Log.d(TAG, "handleMessage: " + mDestinationPoint.latitude + "    " + mDestinationPoint.longitude);
                //显示文字
                setTextOption(mDestinationPoint, "要恰饭的嘛~~！！", "#7ED321");
                //目的地图标
                setMarkerOptions(mDestinationPoint, R.mipmap.arrive_icon);
                //按钮颜色
                //commit_bt.setBackgroundDrawable(getResources().getDrawable(R.mipmap.restaurant_btbg_yellow));
            } else {
                setTextOption(LocationPoint, "您不在食堂范围之内", "#FF6C6C");
                setMarkerOptions(mDestinationPoint, R.mipmap.library_icon);
            }

            // mDistance_tv.setText("距离目的地：" + mDistance + "米");

            setMapZoomScale(LocationPoint);

            mHandler3.removeMessages(0);
            mHandler3.sendEmptyMessageDelayed(0,3000);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler3 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mBaiduMap.clear();
        }
    };


}
