package com.example.map.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.adapter.IWTTSPlayer;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;

public class WNaviGuideActivity extends Activity {
    WalkNavigateHelper mNaviHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取诱导页面地图展示View
//创建诱导View，并接收回调事件。在activity生命周期内调用诱导BikeNavigateHelper对应的生命周期函数。
        mNaviHelper = WalkNavigateHelper.getInstance();
        try {
            View view = mNaviHelper.onCreate(WNaviGuideActivity.this);
            if (view != null) {
                setContentView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mNaviHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            @Override
            public void onWalkNaviModeChange(int mode, WalkNaviModeSwitchListener listener) {
                mNaviHelper.switchWalkNaviMode(WNaviGuideActivity.this, mode, listener);
            }

            @Override
            public void onNaviExit() {

            }
        });

        mNaviHelper.setTTsPlayer(new IWTTSPlayer() {
            @Override
            public int playTTSText(final String s, boolean b) {
                return 0;
            }
        });

// 开始导航

        mNaviHelper.startWalkNavi(this);

// 设置诱导监听, 主要包括导航开始、结束，导航过程中偏航、偏航结束、诱导信息（包含诱导默认图标、诱导类型、诱导信息、剩余距离、时间、振动回调等。
        mNaviHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable icon) {
                //诱导图标更新
            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {
                //诱导枚举信息
            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {
                //诱导信息
            }

            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {
                // 总的剩余距离
            }

            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {
                //总的剩余时间
            }

            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {
                //GPS状态发生变化，来自诱导引擎的消息
            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {
                //偏航信息
            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {
                //偏航规划中的信息
            }

            @Override
            public void onReRouteComplete() {
                //重新算路成功
            }

            @Override
            public void onArriveDest() {
                //到达目的地
            }

            @Override
            public void onVibrate() {
                //震动
            }

            @Override
            public void onIndoorEnd(Message message) {

            }

            @Override
            public void onFinalEnd(Message message) {

            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }
}
