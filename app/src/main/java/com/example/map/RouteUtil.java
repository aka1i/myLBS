package com.example.map;

import android.util.Log;
import android.widget.Toast;

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
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class RouteUtil {
    private static String TAG = "RouteUtil";
    int nodeIndex = -1;
    public static void drivingRoute() {
        //创建驾车线路规划检索实例；
        RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
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
                if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                   // Toast.makeText(MainActivity,"抱歉，未找到结果",Toast.LENGTH_SHORT);
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
    //                nodeIndex = -1;
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

        routePlanSearch.setOnGetRoutePlanResultListener(listener);
        //准备检索起、终点信息；
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("福州", "福大生活区-三区");
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("福大生活区-三区", "福大生活1区");
        //发起驾车线路规划检索；
        routePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
        //释放检索实例；
        routePlanSearch.destroy();
    }
}
